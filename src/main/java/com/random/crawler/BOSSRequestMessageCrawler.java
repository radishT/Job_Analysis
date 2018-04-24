package com.random.crawler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.chrome.ChromeDriver;

/**
 * 将job_message.txt文件 文件格式key_word job_request_url
 * 中的url取出,根据url爬取数据存入到数据库表job_message中
 * 
 * @author admin
 *
 */
public class BOSSRequestMessageCrawler {
	private List<File> fileList;
	private Connection conn;
	private static String localdriver = null; // 本地浏览器驱动位置
	private ChromeDriver driver;

	/**
	 * 读取配置文件
	 */
	static {
		Properties property = new Properties();
		try {
			property.load(new FileInputStream("./src/main/java/com/random/properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		localdriver = property.getProperty("LocalChromedriver");
	}

	/*
	 * 有参构造方法,得到一个fileList
	 */
	public BOSSRequestMessageCrawler(List<File> fileList) {

		try {
			Class.forName("org.gjt.mm.mysql.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/crawler_db?characterEncoding=utf-8", "root",
					"root");
		} catch (Exception e) {
			System.out.println("数据库连接失败");
		}
		this.fileList = fileList;
	}

	/*
	 * 将job_message.txt文件中的key url写入job_message中,并将status的值置为0
	 */
	public void addAllLineToMySQL() {

	}

	public void crawlerMessage() {
		String sql = "SELECT url FROM job_message WHERE status=0 ORDER BY id LIMIT 1";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			// 如果rs有没有
			if (rs.wasNull()) {
				return;
			} else {// 如果有,指针下移
				rs.next();
				String url = rs.getString(1);

			}
		} catch (Exception e) {

		}

	}
}
