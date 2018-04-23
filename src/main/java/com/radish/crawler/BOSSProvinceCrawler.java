package com.radish.crawler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 爬取https://www.zhipin.com/job_detail/?query=java&scity=101090100&industry=&position=100101
 * 中BOOS直聘的province编号.
 * java置空为#
 * 输出
 * 省	市	url
 * @author admin
 *
 */
public class BOSSProvinceCrawler {

	public static void main(String[] args) throws Exception {
		//
		// work();
		//
		jsoupWork();
	}

	/**
	 * 使用selenium打开目标网页,爬取主要信息,并保存到/result-sources/BoosUrl.txt
	 */
	public static void work() throws Exception {
		String url = "https://www.zhipin.com/job_detail/?query=java&scity=101090100&industry=&position=100101";
		// 设置Chrome浏览器驱动所在位置
		System.setProperty("webdriver.chrome.driver", "D:/chrome_driver/chromedriver.exe");
		ChromeDriver driver = new ChromeDriver();

		WebDriverWait wait = new WebDriverWait(driver, 5);
		// 打开网页
		driver.get(url);

		// 等待网页加载完毕
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("wrap")));
		// System.out.println(bodyText);
		// 点击下拉框
		driver.findElement(By.cssSelector("span.label-text")).click();
		// 找出city-box 显示省份条数
		List<WebElement> provinceList = driver.findElements(By.cssSelector("div.city-box ul.dorpdown-province li"));
		// 去掉第一行的热门
		provinceList.remove(0);
		// 找出每个省份对应的ul列
		List<WebElement> cityList = driver.findElements(By.cssSelector("div.dorpdown-city ul"));
		// 去掉第一个热门
		cityList.remove(0);
		// 遍历省
		for (WebElement provinceEL : provinceList) {
			int i = 0;
			// 省名称
			String provinceName = provinceEL.getText();
			// 遍历省对应的城市ul
			WebElement ulEL = cityList.get(i);
			List<WebElement> liList = ulEL.findElements(By.tagName("li"));
			for (WebElement li : liList) {
				System.out.println(provinceName + "\t" + li.getText() + "\t" + li.getAttribute("data-val"));
			}
			i++;
		}

	}

	public static void jsoupWork() {
		try {
			// String url =
			// "https://www.zhipin.com/job_detail/?query=java&scity=101090100&industry=&position=100101";
			// String
			// userAgent="Opera11.11–WindowsUser-Agent:Opera/9.80(WindowsNT6.1;U;en)Presto/2.8.131Version/11.11";
			String url = "https://www.zhipin.com/job_detail/?query=java&scity=101090100&industry=&position=100101";
			// 设置Chrome浏览器驱动所在位置
			System.setProperty("webdriver.chrome.driver", "D:/chrome_driver/chromedriver.exe");
			ChromeDriver driver = new ChromeDriver();
			WebDriverWait wait = new WebDriverWait(driver, 5);
			// 打开网页
			driver.get(url);
			// 等待网页加载完毕
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("footer")));
			
			Document document = Jsoup.parse(driver.getPageSource());
			// 找到省
			Elements provinceList = document.select("div.city-box ul.dorpdown-province li");
			// 去掉热门
			provinceList.remove(0);
			// 市ul列表
			Elements cityULList = document.select("div.dorpdown-city ul");
			// 去掉热门
			cityULList.remove(0);
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < provinceList.size(); i++) {
				// 得到省名称
				String provinceName = provinceList.get(i).text();
				// 找到城市ul并得到其中的li
				Elements cityList = cityULList.get(i).select("li");
				// 遍历li
				for (Element cityLi : cityList) {
					// 写入到/result-sources/BoosUrl.txt
					//provinceName + "\t" + cityLi.text() + "\t" + cityLi.attr("data-val")
					// url=https://www.zhipin.com/job_detail/?query=java&scity=101281900
					String line = provinceName + "\t" + cityLi.text() + "\t" 
							+"https://www.zhipin.com/job_detail/?query=#&scity="
					+ cityLi.attr("data-val")+"\r\n";
					System.out.println(">>"+line);
					builder.append(line);
				}
			}
			// 结果写入到文件
			File result = new File("D:/eclipse_2/git/Job_Analysis/result-sources/radish/BossUrl.txt");
			result.createNewFile();
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(result),"UTF-8"));
			writer.print(builder.toString());
			writer.flush();
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
