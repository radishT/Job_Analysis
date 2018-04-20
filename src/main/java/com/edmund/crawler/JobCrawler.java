package com.edmund.crawler;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.edmund.utils.DBUtils;
import com.edmund.vo.Job;

/**
 * 用于职位信息爬取的爬虫类
 * 
 * @author Edmund
 *
 */
public class JobCrawler {
	// private static String[] keys = { "java", "c#", "c++", "Android", "php" };
	private static String[] keys = { "php" };
	private static Map<String, List<String>> infos = null;

	private static List<String> cities = null;
	private static List<String> roots = null;
	private static String localdriver = null;

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

	public static void main(String[] args) {
		for (String strkey : keys) {
			initLists(strkey);
		}

		for (int i = 0; i < THREAD_NUMBER; i++) {
			new JobCrawler().new crawThread().start();
		}
	}

	/**
	 * 爬取数据的线程类
	 * @author Edmund
	 *
	 */
	class crawThread extends Thread {
		ChromeDriver driver = initBrowser();

		@Override
		public void run() {
			while (true) {
				String[] urls = getURL();
				if (urls == null) {
					break;
				}
				String key = whichKey(urls[1]);

				List<Job> jobs = null;
				try {
					jobs = crawJobs(urls, key, driver);
				} catch (Exception e) {
					pushIntoLists(urls);
				}
				DBUtils.writeToFile(jobs,
						"./result-sources/EdmundDXu/jobs/" + key + "/"
								+ this.getName() + "/" + urls[0] + "-" + key
								+ "-info.txt");
			}
		}
	}

	/**
	 * 线程同步取url和city信息
	 * @return urls[0]保存city,urls[1]保存url
	 */
	private synchronized static String[] getURL() {
		if (cities == null || cities.isEmpty()) {
			return null;
		}
		if (roots == null || roots.isEmpty()) {
			return null;
		}
		String[] urls = { cities.get(0), roots.get(0) };
		cities.remove(0);
		roots.remove(0);

		return urls;
	}

	/**
	 * 静态初始化职位信息，将所有信息加载到内存中
	 * @param strkey 关键字
	 */
	private static void initLists(String strkey) {
		try {
			infos = DBUtils
					.readFromFile("./result-sources/EdmundDXu/files/emp.txt");
		} catch (IOException e) {
		}
		List<String> newroot = new ArrayList<String>();
		cities = infos.get("cities");

		for (String root : infos.get("roots")) {
			newroot.add(root.replace("#", strkey));
		}
		roots = newroot;
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
	 * 如果出现异常情况导致没有被其他catch语句捕获，就将该url重新加入列表中处理
	 * @param url
	 */
	private synchronized static void pushIntoLists(String[] urls) {
		cities.add(urls[0]);
		roots.add(urls[1]);
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
	 * 从指定根站点，以指定关键字开始爬取职位信息,多线程方式将职位信息逐条写入文件中 58同城
	 * 该方法暂时废弃
	 */

	/**
	 * 从指定根站点，以指定关键字开始爬取职位信息 58同城
	 * @param urls 保存url和city信息的数组,urls[0]保存city,urls[1]保存url
	 * @param key 需要爬取的关键字
	 * @param driver 浏览器驱动对象
	 * @return 包含职位信息的列表
	 */
	public static List<Job> crawJobs(String[] urls, String key,
			ChromeDriver driver) {

		if (pretreatment(urls[1], driver) == -1) {
			return null;
		}

		List<Job> jobs = new ArrayList<Job>();
		while (true) {
			WebElement list = driver.findElementById("list_con");
			List<WebElement> positions = list.findElements(By.tagName("li"));
			for (WebElement webElement : positions) {
				// 出现此条语句表示下面的结果与搜索关键字无关，故直接抛弃下面的职位
				if (webElement.getAttribute("class").contains("noData")) {
					break;
				}
				jobs.add(createJobVo(webElement, urls[0], key));
			}
			if (nextPage(driver) == -1) {
				break;
			}
		}
		return jobs;

	}

	/**
	 * 在爬取数据之前做的预处理工作
	 * @param url 需要爬取的url
	 * @param driver 浏览器驱动对象
	 * @return 0表示预处理正常,-1表示预处理失败
	 */
	private static int pretreatment(String url, ChromeDriver driver) {
		driver.get(url);
		// 最大化窗口
		// driver.manage().window().maximize();

		WebDriverWait wait = new WebDriverWait(driver, 10);

		// 等待职位列表和分页列表加载完毕
		try {
			wait.until(ExpectedConditions
					.presenceOfElementLocated(By.id("list_con")));
		} catch (Exception e) {
			// 如果出现页面中没有list_con元素的情况，视为没有职位信息，直接退出本页面
			return -1;
		}
		// wait.until(ExpectedConditions.presenceOfElementLocated(By.className("next")));

		return 0;
	}

	/**
	 * 爬取完数据后的翻页操作
	 * @param driver 浏览器驱动对象
	 * @return 0表示翻页操作可以正常执行,-1表示翻页操作不能继续进行
	 */
	public static int nextPage(ChromeDriver driver) {
		// 使用findElements可以避免出现‘页面中没有next元素’而导致的异常
		List<WebElement> nextlist = driver.findElementsByClassName("next");
		// 如果页面中没有next元素，则不点击next，直接退出本次循环
		if (nextlist == null || nextlist.isEmpty()) {
			return -1;
		}

		WebElement next = nextlist.get(0);

		// 一旦翻页按钮无法使用，表示到了最后一页，则退出循环
		if (next.getAttribute("class").contains("disabled")) {
			return -1;
		}
		next.click();
		return 0;
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
