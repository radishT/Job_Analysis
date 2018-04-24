package com.radish.crawler.distributed;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.openqa.selenium.chrome.ChromeDriver;

/**
 * 实现分布式爬虫
 * 
 * 任务:
 * 1.从数据库表:url_list获取一条url,
 * 
 * 2.将爬到的数据存入到表lagou
 * 爬虫类,每次领取一个任务,并将任务的状态值置为1
 * 任务完成后,将结果存入lagou表,并将url_list中相对应任务的状态值置为2
 * @author admin
 *
 */
public class DistributedCrawler {
	private Connection conn;
	private ChromeDriver driver;

	// 空构造,初始化conn
	public DistributedCrawler() {

		System.setProperty("webdriver.chrome.driver", "D:/chrome_driver/chromedriver.exe");
		driver = new ChromeDriver();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			// 注意,IP可以填写分布式数据库所在的主机
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/crawler_db?characterEncoding=utf-8", "root",
					"admin");
		} catch (Exception e) {
			System.out.println("数据库连接初始化失败");
		}
	}

	// 关闭内置的数据库连接
	public void closeConnection() throws Exception {
		conn.close();
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public ChromeDriver getDriver() {
		return driver;
	}

	public void setDriver(ChromeDriver driver) {
		this.driver = driver;
	}
}
