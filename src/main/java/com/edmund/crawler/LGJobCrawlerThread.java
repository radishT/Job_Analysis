package com.edmund.crawler;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.edmund.utils.DataBaseConnection;
import com.edmund.utils.LGDBUtils;
import com.edmund.vo.LGJob;

import jeasy.analysis.MMAnalyzer;

/**
 * 多线程静态爬取职位信息的线程类
 * 现在用于从ready_url表中读取出需要处理的url,然后将处理结果存入lagou表中
 * 爬虫处理阶段3
 * @author Edmund
 *
 */
class LGJobCrawlerThread extends Thread {

	private DataBaseConnection dbc = new DataBaseConnection();
	private LGDBUtils utils = new LGDBUtils(dbc);
	private static String[] keys = { "web", "java", "python", "c++", "c#",
			"android", "linux" };
	private static String localdriver = null; // 本地浏览器驱动位置
	private static String localexport = null; // 本地输出路径

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36";

	/**
	 * 读取配置文件
	 */
	static {
		Properties property = new Properties();
		try {
			property.load(new FileInputStream(
					"./src/main/java/com/edmund/properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		localdriver = property.getProperty("LocalChromedriver");
		localexport = property.getProperty("LocalExportPath");

	}

	public static void main(String[] args) throws InterruptedException {
		// for (int i = 0; i < THREAD_NUMBER; i++) {
		// new LGJobCrawlerThread().start();
		// Thread.sleep(5000);
		// }
		LGJobCrawlerThread t = new LGJobCrawlerThread();
		t.merge();

	}

	/**
	 * 合并数据库中所有条目的Map集合,整合为一个Map集合,并输出到本地文件系统中
	 */
	private void merge() {
		for (String keyword : keys) {
			Map<String, Integer> kwMerge = new HashMap<String, Integer>();
			List<LGJob> jobs = utils.getLGJob(keyword);
			for (LGJob job : jobs) {
				Map<String, Integer> kwMap = job.getKeywords();
				Set<String> keyset = kwMap.keySet();
				for (String key : keyset) {
					if (kwMerge.containsKey(key)) {
						kwMerge.put(key, kwMerge.get(key) + kwMap.get(key));
					} else {
						if (key.contains("/")) {
							String[] keys = key.split("/");
							for (String inner_key : keys) {
								if (kwMerge.containsKey(inner_key)) {
									kwMerge.put(inner_key,
											kwMerge.get(inner_key)
													+ kwMap.get(key));
								} else {
									kwMerge.put(inner_key, kwMap.get(key));
								}
							}
						} else {
							kwMerge.put(key, kwMap.get(key));
						}

					}
				}
			}
			LGDBUtils.writeToFile(kwMerge,
					localexport + "/" + keyword + ".txt");
		}
	}

	@Override
	public void run() {
		ChromeDriver driver = initBrowser();
		while (true) {
			try {
				String[] infos = null;
				if ((infos = utils.readFromReadyURL()) == null) {
					try {
						Thread.sleep(6000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					LGJob job = getJobDetails_Dynamic(infos, driver);
					utils.insertLGJob(job);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 初始化浏览器驱动
	 * @return 浏览器驱动对象
	 */
	private static ChromeDriver initBrowser() {
		System.setProperty("webdriver.chrome.driver", localdriver);
		ChromeDriver driver = new ChromeDriver();
		return driver;
	}

	/**
	 * 在爬取数据之前做的预处理工作
	 * @param url 需要爬取的url
	 * @param driver 浏览器驱动对象
	 * @return 0表示预处理正常,-1表示预处理失败
	 */
	private int pretreatment(String url, ChromeDriver driver) {
		driver.get(url);
		// driver.manage().window().maximize();

		WebDriverWait wait = new WebDriverWait(driver, 5);

		try {
			wait.until(ExpectedConditions
					.presenceOfElementLocated(By.className("position-head")));
			wait.until(ExpectedConditions
					.presenceOfElementLocated(By.id("job_detail")));

		} catch (Exception e) {
			e.printStackTrace();
			restart(url);
			return -1;
		}

		return 0;
	}

	private LGJob getJobDetails_Dynamic(String[] infos, ChromeDriver driver) {
		LGJob job = null;
		String url = infos[0];
		// 过滤条件，只允许包含数据的url通过
		if (url.matches(".*lagou\\.com/jobs/[0-9]+\\..?html")) {
			if (pretreatment(url, driver) == -1) {
				return null;
			}
			String key = infos[1];
			String[] job_request = driver.findElementByClassName("job_request")
					.getText().split("/");
			String salary = job_request[0].trim();
			String city = job_request[1].trim();
			String experience = job_request[2].trim();
			String education = job_request[3].trim();

			String company = driver.findElementByClassName("company").getText();
			String keywords = driver.findElementByClassName("job_bt")
					.findElement(By.tagName("div")).getText();

			job = new LGJob(null, key, null, salary, city, experience,
					education, company.substring(0, company.length() - 2),
					getKeywordsMap(keywords));

		} else {
			return null;
		}

		return job;
	}

	/**
	 * 根据infos数组获取工作的详细信息,infos[0]保存url,infos[1]保存keyword
	 * @param infos 保存了url和keyword的数组
	 * @return 职位信息的封装类
	 */
	private LGJob getJobDetails(String[] infos) {
		Document doc = null;
		LGJob job = null;
		String url = infos[0];
		try {
			// 过滤条件，只允许包含数据的url通过
			if (url.matches(".*lagou\\.com/jobs/[0-9]+\\..?html")) {
				doc = Jsoup.connect(url).userAgent(USER_AGENT).get();
			} else {
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		String key = infos[1];
		String[] job_request = null;
		try {
			job_request = doc.getElementsByClass("job_request").first().text()
					.split("/");
		} catch (Exception e) {
			restart(url);
			e.printStackTrace();
		}
		String salary = job_request[0].trim();
		String city = job_request[1].trim();
		String experience = job_request[2].trim();
		String education = job_request[3].trim();

		String company = doc.getElementsByClass("company").first().text();
		String keywords = doc.getElementsByClass("job_bt").first()
				.getElementsByTag("div").text();

		job = new LGJob(null, key, null, salary, city, experience, education,
				company, getKeywordsMap(keywords));

		return job;
	}

	/**
	 * 处理url出现异常时，恢复该url在数据库中的状态，并且休息10秒钟
	 * @param url
	 */
	private void restart(String url) {
		utils.restoreReadyURL(url);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据传入的文本进行分词，取出其中的英文单词,并且将其出现的次数按照map的格式保存
	 * @param keywords 需要分词的文本
	 * @return 分词后的单词和其出现的次数
	 */
	private static Map<String, Integer> getKeywordsMap(String keywords) {
		Map<String, Integer> kwMap = new HashMap<String, Integer>();
		MMAnalyzer mm = new MMAnalyzer();
		MMAnalyzer.addWord("C#");
		MMAnalyzer.addWord("c#");
		try {
			String[] kwStrs = mm.segment(keywords, "|").split("\\|");
			for (String kwStr : kwStrs) {
				if (!kwStr.matches("[a-zA-Z/#\\\\]+")) {
					continue;
				}
				if (kwMap.containsKey(kwStr)) {
					kwMap.put(kwStr, kwMap.get(kwStr) + 1);
				} else {
					kwMap.put(kwStr, 1);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return kwMap;
	}
}