package com.edmund.crawler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	private static ChromeDriver driver = null;
	private static String[] keys = { "java" };
	private static Map<String, List<String>> infos = null;
	private static String root = null;
	private static String key = null;
	private static String city = null;

	public static void main(String[] args) throws IOException {
		initBrowser();
		infos = DBUtils.readFromFile("emp.txt");
		List<String> cities = infos.get("cities");
		List<String> roots = infos.get("roots");
		for (int i = 0; i < cities.size(); i++) {
			root = roots.get(i);
			city = cities.get(i);
			for (String strkey : keys) {
				key = strkey;
				List<Job> jobs = crawJobs();
				DBUtils.writeToFile(jobs, city + "-" + key + "-info.txt");
				// crawJobs_MultiThread();
			}
		}
	}

	/**
	 * 初始化浏览器驱动
	 */
	public static void initBrowser() {
		System.setProperty("webdriver.chrome.driver", "D:/utils/chromedriver.exe");
		driver = new ChromeDriver();
	}

	/**
	 * 从指定根站点，以指定关键字开始爬取职位信息,多线程方式将职位信息逐条写入文件中 58同城
	 * 
	 * @throws FileNotFoundException
	 */
	public static void crawJobs_MultiThread() throws FileNotFoundException {

		if (pretreatment() == -1) {
			return;
		}

		while (true) {
			WebElement list = driver.findElementById("list_con");
			List<WebElement> positions = list.findElements(By.tagName("li"));
			for (WebElement webElement : positions) {
				// 出现此条语句表示下面的结果与搜索关键字无关，故直接抛弃下面的职位
				if (webElement.getText().contains("为您推荐以下职位")) {
					break;
				}
				new Thread() {
					@Override
					public void run() {
						try {
							DBUtils.writeToFile(createJobVo(webElement), city + "/" + city + "-" + key + "-info.txt");
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
				}.start();
			}

			if (nextPage() == -1) {
				break;
			}
		}

	}

	/**
	 * 从指定根站点，以指定关键字开始爬取职位信息 58同城
	 * 
	 * @param root
	 *            根站点
	 * @param key
	 *            搜索关键字
	 * @return 包含职位信息的列表
	 */
	public static List<Job> crawJobs() {

		if (pretreatment() == -1) {
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
				jobs.add(createJobVo(webElement));
			}
			if (nextPage() == -1) {
				break;
			}
		}
		return jobs;

	}

	/**
	 * 在爬取数据之前做的预处理工作
	 * 
	 * @return 0表示预处理正常,-1表示预处理失败
	 */
	private static int pretreatment() {
		String baseUrl = root;// 预处理的URL
		driver.get(baseUrl.replace("#", key));
		// 最大化窗口
		// driver.manage().window().maximize();

		WebDriverWait wait = new WebDriverWait(driver, 10);

		// 等待职位列表和分页列表加载完毕
		try {
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("list_con")));
		} catch (Exception e) {
			// 如果出现页面中没有list_con元素的情况，视为没有职位信息，直接退出本页面
			return -1;
		}
		// wait.until(ExpectedConditions.presenceOfElementLocated(By.className("next")));

		return 0;
	}

	/**
	 * 爬取完数据后的翻页操作
	 * 
	 * @return 0表示翻页操作可以正常执行,-1表示翻页操作不能继续进行
	 */
	public static int nextPage() {
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
	 * 打印职位信息
	 * 
	 * @param webElement
	 *            包含职位信息的页面元素包装类
	 */
	private static void printPositionInfo(WebElement webElement) {
		System.out.print(webElement.findElement(By.className("job_name")).getText() + "\t");
		System.out.print(webElement.findElement(By.className("job_salary")).getText() + "\t");
		System.out.print(webElement.findElement(By.className("comp_name")).getText() + "\t");
		System.out.print(webElement.findElement(By.className("cate")).getText() + "\t");
		System.out.print(webElement.findElement(By.className("xueli")).getText() + "\t");
		System.out.println(webElement.findElement(By.className("jingyan")).getText());
	}

	/**
	 * 用于创建职位信息的封装类
	 * 
	 * @param webElement
	 * @return 封装职位信息的Job对象
	 */
	private static Job createJobVo(WebElement webElement) {
		String title = webElement.findElement(By.className("job_name")).getText();
		String job_name = webElement.findElement(By.className("cate")).getText();
		String salary = webElement.findElement(By.className("job_salary")).getText();
		String company = webElement.findElement(By.className("comp_name")).getText();
		String education = webElement.findElement(By.className("xueli")).getText();
		String experience = webElement.findElement(By.className("jingyan")).getText();

		Job job = new Job(null, city, key, title, salary.split("元/月")[0], company.split(" ")[0], job_name, education,
				experience);
		return job;
	}
}
