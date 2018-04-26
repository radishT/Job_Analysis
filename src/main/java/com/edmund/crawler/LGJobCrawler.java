package com.edmund.crawler;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.edmund.utils.DataBaseConnection;
import com.edmund.utils.LGDBUtils;

/**
 * 拉钩网爬虫类
 * 现在用于从city_url表中读取需要处理的所有url,然后将抓取到的所有href保存到ready_url表中
 * 爬虫处理阶段2
 * @author Edmund
 *
 */
public class LGJobCrawler {
	private static String[] keys = { "web", "java", "python", "c++", "c#",
			"android", "linux" };

	private static String localdriver = null; // 本地浏览器驱动位置
	private DataBaseConnection dbc = new DataBaseConnection();
	private LGDBUtils utils = new LGDBUtils(dbc);

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
