package com.edmund.test;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Test {
	private static final String ROOT = "http://hz.58.com/"; // 根站点
	private static final String BASEURL = ROOT + "job/?key=#&final=1&jump=1"; // 预处理的URL
	private static final String key = "java"; // 搜索的关键字，用于填充预处理的URL

	public static void main(String[] args) {
		System.setProperty("webdriver.chrome.driver", "D:/utils/chromedriver.exe");
		ChromeDriver driver = new ChromeDriver();
		driver.get(BASEURL.replace("#", key));
		// 最大化窗口
		driver.manage().window().maximize();

		WebDriverWait wait = new WebDriverWait(driver, 10);

		// 等待职位列表和分页列表加载完毕
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("list_con")));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.className("next")));

		// 如果当前页存在翻页按钮，就继续执行循环

		while (true) {
			WebElement list = driver.findElementById("list_con");
			List<WebElement> positions = list.findElements(By.tagName("li"));
			for (WebElement webElement : positions) {
				// 出现此条语句表示下面的结果与搜索关键字无关，故直接抛弃下面的职位
				if (webElement.getText().contains("为您推荐以下职位")) {
					break;
				}
				System.out.print(webElement.findElement(By.className("job_name")).getText() + "\t");
				System.out.print(webElement.findElement(By.className("job_salary")).getText() + "\t");
				System.out.print(webElement.findElement(By.className("comp_name")).getText() + "\t");
				System.out.print(webElement.findElement(By.className("cate")).getText() + "\t");
				System.out.print(webElement.findElement(By.className("xueli")).getText() + "\t");
				System.out.println(webElement.findElement(By.className("jingyan")).getText());
			}

			WebElement next = driver.findElement(By.className("next"));

			// 一旦翻页按钮无法使用，表示到了最后一页，则退出循环
			if (next.getAttribute("class").contains("disabled")) {
				break;
			}
			next.click();
		}

	}
}
