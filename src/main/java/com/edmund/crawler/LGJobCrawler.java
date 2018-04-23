package com.edmund.crawler;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
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
import com.edmund.vo.Job;
import com.edmund.vo.LGJob;

import jeasy.analysis.MMAnalyzer;

public class LGJobCrawler {
	private static String[] keys = { "web", "java", "python", "c++", "c#",
			"android" };
	private static String root = "https://www.lagou.com/jobs/list_%KW%?px=default&city=%CT%#filterBox";
	private static List<String> URLList = new ArrayList<String>();
	private static String localdriver = null; // 本地浏览器驱动位置
	private static String localexport = null; // 本地输出路径
	private DataBaseConnection dbc = new DataBaseConnection();
	private LGDBUtils utils = new LGDBUtils(dbc);

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36";
	private static final int THREAD_NUMBER = 5;

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

	public static void main(String[] args)
			throws IOException, InterruptedException {
		LGJobCrawler lgCrawler = new LGJobCrawler();
		// lgCrawler.initURLList();
		// ChromeDriver driver = initBrowser();
		//
		// for (String url : URLList) {
		// lgCrawler.crawJobs(url, driver);
		// }
		getJobDetails("https://www.lagou.com/jobs/3544094.html");
	}

	/**
	 * 初始化所有需要爬取的url,将其添加到url列表中
	 * @throws IOException 
	 */
	private void initURLList() throws IOException {
		List<String> cities = utils
				.readFromFile("./result-sources/EdmundDXu/files/lagou.txt");
		// 初始化所有关键字
		for (String key : keys) {
			// 初始化所有城市
			for (String city : cities) {
				String url = root.replace("%KW%", key).replace("%CT%", city);
				URLList.add(url.replace("c#", "c%23"));
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

	public List<Job> crawJobs(String url, ChromeDriver driver)
			throws InterruptedException {

		if (pretreatment(url, driver) == -1) {
			return null;
		}

		List<Job> jobs = new ArrayList<Job>();
		while (true) {
			WebElement list = driver.findElementById("s_position_list");
			WebElement list_ul = list.findElement(By.tagName("ul"));
			List<WebElement> positions = list_ul.findElements(By.tagName("li"));
			for (WebElement webElement : positions) {
				System.out.println(webElement.getText());
				// jobs.add(createJobVo(webElement, urls[0], key));
			}

			if (nextPage(driver) == -1) {
				break;
			}
		}
		return jobs;

	}

	/**
	 * 根据url获取工作的详细信息
	 * @param url
	 * @return
	 */
	private static LGJob getJobDetails(String url) {
		Document doc = null;
		LGJob job = null;
		try {
			if (url.matches(".*lagou\\.com/jobs/[0-9]+\\..?html")) {
				doc = Jsoup.connect(url).userAgent(USER_AGENT).get();
			} else {
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		String key = whichKey(url);
		String[] infos = doc.getElementsByClass("job_request").first().text()
				.split("/");
		String salary = infos[0].trim();
		String city = infos[1].trim();
		String experience = infos[2].trim();
		String education = infos[3].trim();

		String company = doc.getElementsByClass("company").first().text();
		String keywords = doc.getElementsByClass("job_bt").first()
				.getElementsByTag("div").text();

		job = new LGJob(null, key, null, salary, city, experience, education,
				company, getKeywordsMap(keywords));
		System.out.println(job);
		return job;
	}

	private static Map<String, Integer> getKeywordsMap(String keywords) {
		Map<String, Integer> kwMap = new HashMap<String, Integer>();
		MMAnalyzer mm = new MMAnalyzer();
		try {
			String[] kwStrs = mm.segment(keywords, "|").split("\\|");
			for (String kwStr : kwStrs) {
				// if (!kwStr.matches("[a-zA-Z/#\\\\]+")) {
				// continue;
				// }
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

	/**
	 * 创建职位信息的封装类
	 * @param webElement
	 * @param city 城市信息
	 * @param key 关键字
	 * @return 封装职位信息的Job对象
	 */
	private static Job createJobVo(WebElement webElement, String city,
			String key) {
		String title = webElement.findElement(By.className("job_name"))
				.getText();
		String job_name = webElement.findElement(By.className("cate"))
				.getText();
		String salary = webElement.findElement(By.className("job_salary"))
				.getText();
		String company = webElement.findElement(By.className("comp_name"))
				.getText();
		String education = webElement.findElement(By.className("xueli"))
				.getText();
		String experience = webElement.findElement(By.className("jingyan"))
				.getText();

		Job job = new Job(null, city, key, title, salary.split("元/月")[0],
				company.split(" ")[0], job_name, education, experience);
		return job;
	}

}
