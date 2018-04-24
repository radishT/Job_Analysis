package com.random.test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Demo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.setProperty("webdriver.chrome.driver", "D:/chrome_driver/chromedriver.exe");
		ChromeDriver driver = new ChromeDriver();
		driver.get(
				"https://www.zhipin.com/job_detail/691ad23916cba6891n1409y4FFo~.html?ka=search_list_11_blank&lid=7-PfVqs0wE8c.search");
		WebElement element = driver.findElement(By.cssSelector("div.detail-content .job-sec"));
		System.out.println(element.getText());
	}

}
