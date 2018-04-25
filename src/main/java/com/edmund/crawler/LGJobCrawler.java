package com.edmund.crawler;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.edmund.utils.DataBaseConnection;
import com.edmund.utils.LGDBUtils;
import com.edmund.vo.LGJob;

import jeasy.analysis.MMAnalyzer;

/**
 * 拉钩网爬虫类
 * 现在用于从city_url表中读取需要处理的所有url,然后将抓取到的所有href保存到ready_url表中
 * @author Edmund
 *
 */
public class LGJobCrawler {
	private static String[] keys = { "web", "java", "python", "c++", "c#",
			"android", "linux" };
	private static String root = "https://www.lagou.com/jobs/list_%KW%?px=default&city=%CT%#filterBox";

	private static String localdriver = null; // 本地浏览器驱动位置
	private DataBaseConnection dbc = new DataBaseConnection();
	private LGDBUtils utils = new LGDBUtils(dbc);

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36";
	private static final int THREAD_NUMBER = 1;

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

	}

	public static void main(String[] args) throws Exception {
		LGJobCrawler lgCrawler = new LGJobCrawler();
		ChromeDriver driver = initBrowser();

		// for (int i = 0; i < THREAD_NUMBER; i++) {
		// new LGJobCrawler().new LGJobCrawlerThread().start();
		// }
		String url = null;

		while ((url = lgCrawler.read()) != null) {
			lgCrawler.crawJobs(url, driver);
		}
	}

	private String read() {
		return utils.readFromCityURL();
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
	private static int pretreatment(String url, ChromeDriver driver) {
		driver.get(url);
		// driver.manage().window().maximize();

		WebDriverWait wait = new WebDriverWait(driver, 5);

		try {
			wait.until(ExpectedConditions
					.presenceOfElementLocated(By.id("s_position_list")));
		} catch (Exception e) {
			return -1;
		}

		return 0;
	}

	/**
	 * 从给定url爬取职位信息
	 * @param url 网页路径
	 * @param driver 浏览器驱动
	 * @return 职位信息列表
	 * @throws Exception 
	 */
	public void crawJobs(String url, ChromeDriver driver) throws Exception {

		try {
			if (pretreatment(url, driver) == -1) {
				return;
			}

			while (true) {
				WebElement list = driver.findElementById("s_position_list");
				WebElement list_ul = list.findElement(By.tagName("ul"));
				List<WebElement> positions = list_ul
						.findElements(By.tagName("li"));
				for (WebElement webElement : positions) {
					String href = webElement.findElement(By.tagName("a"))
							.getAttribute("href");
					utils.writeIntoReadyURL(href, whichKey(url));
				}

				if (nextPage(driver) == -1) {
					break;
				}
			}
		} catch (Exception e) {
			restart(url);
			e.printStackTrace();
		}
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
	 * 根据infos数组获取工作的详细信息,infos[0]保存url,infos[1]保存keyword
	 * @param infos 保存了url和keyword的数组
	 * @return 职位信息的封装类
	 */
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
					education, company, getKeywordsMap(keywords));

		} else {
			return null;
		}

		return job;
	}

	/**
	 * 处理url出现异常时，恢复该url在数据库中的状态，并且休息10秒钟
	 * @param url
	 */
	private void restart(String url) {
		utils.restoreReadyURL(url);
		System.out.println("正在回滚数据");
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

	/**
	 * 爬取完数据后的翻页操作
	 * @param driver 浏览器驱动对象
	 * @return 0表示翻页操作可以正常执行,-1表示翻页操作不能继续进行
	 * @throws InterruptedException 
	 */
	private static int nextPage(ChromeDriver driver)
			throws InterruptedException {
		// 使用findElements可以避免出现‘页面中没有next元素’而导致的异常
		List<WebElement> nextlist = driver.findElements(
				By.cssSelector("#s_position_list span.pager_next"));

		// 如果页面中没有next元素，则不点击next，直接退出本次循环
		if (nextlist == null || nextlist.isEmpty()) {
			return -1;
		}

		WebElement next = nextlist.get(0);
		driver.getKeyboard().sendKeys(Keys.END);
		Thread.sleep(2000);
		// 一旦翻页按钮无法使用，表示到了最后一页，则退出循环
		if (next.getAttribute("class").contains("pager_next_disabled")) {
			return -1;
		}
		next.click();
		Thread.sleep(2000);
		return 0;
	}

	/**
	 * 根据url判断该url属于哪个关键字
	 * @param url
	 * @return 关键字
	 */
	private static String whichKey(String url) {
		for (String key : keys) {
			if (url.contains(key)) {
				return key;
			}
		}
		return null;
	}

}
