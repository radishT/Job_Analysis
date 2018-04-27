package com.random.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

import jeasy.analysis.MMAnalyzer;

public class TaskManager {
	private Connection conn;
	private ChromeDriver driver;

	public TaskManager() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/crawler_db?characterEncoding=utf-8", "root",
					"root");

		} catch (Exception e) {
			System.out.println("数据库连接失败");
		}
		System.setProperty("webdriver.chrome.driver", "D:/chrome_driver/chromedriver.exe");
		driver = new ChromeDriver();
		System.out.println("构造方法执行完毕");
	}

	/**
	 * 
	 * 将job_message.txt文件中的key url写入job_message中,并将status的值置为0
	 * 
	 * @param fileList
	 */
	public void initData(List<File> fileList) {
		for (File file : fileList) {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				String line = null;
				String sql = null;
				while ((line = reader.readLine()) != null) {
					String key = line.split("\t")[0];
					String url = line.split("\t")[1];
					sql = "INSERT INTO job_message(key_word,url,status) VALUES(?,?,0)";
					PreparedStatement stmt = conn.prepareStatement(sql);
					stmt.setString(1, key);
					stmt.setString(2, url);
					stmt.executeUpdate();
					stmt.close();
				}
				reader.close();
			} catch (Exception e) {
				System.out.println("文件读取失败");
			}
		}
	}

	/**
	 * 爬虫获取数据表中的url 爬取 message 取出url,status置为1
	 * 
	 */
	public void startCrawler() {
		try {// 设置事务不自动提交
			conn.setAutoCommit(false);
			System.out.println("设置自动提交为false");
		} catch (SQLException e1) {
			System.out.println("设置自动提交为false,失败!!!!!!!-----------------");
		}
		s: while (true) {
			String sql = "SELECT url_id,url FROM job_message WHERE status=0 ORDER BY url_id LIMIT 1";
			try {
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery();
				// 如果rs有没有
				if (rs.wasNull()) {
					conn.commit();
					return;
				} else {// 如果有,指针下移
					// System.out.println("查到一条记录");
					rs.next();
					int urlId = rs.getInt(1);
					String url = rs.getString(2);
					// System.out.println("url_id:" + urlId + "url:" + url);
					sql = "UPDATE job_message SET status = 1 WHERE url_id = ?";
					stmt = conn.prepareStatement(sql);
					stmt.setInt(1, urlId);
					stmt.executeUpdate();
					conn.commit();
					// url ---> message 此处是getBossMessage() 拉钩的方法为
					String message = null;
					// int reTry = 0;
					// 如果得message失败,就重试,直到重试2次
					// while ((message = getBossMessage(url)) == null && reTry <2) {
					// reTry++;
					// }
					// 如果重试2次还=null,放弃这个url
					if ((message = getBossMessage(url)) == null) {
						System.out.println("死活得不到这个message,不要了 urlID:" + urlId);
						continue s;
					}
					try {
						sql = "UPDATE job_message SET message=? WHERE url_id=?";
						stmt = conn.prepareStatement(sql);
						stmt.setString(1, message);
						stmt.setInt(2, urlId);
						stmt.executeUpdate();
						conn.commit();
					} catch (Exception e) {
						System.out.println("一个状态值被置为1的记录,并没有写入message!!!! urlID:" + urlId);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				continue s;
			}
		}
	}

	/*
	 * 通过url,动态爬取Boss直聘 message
	 */
	private String getBossMessage(String url) {
		String message = null;
		try {
			driver.get(url);
			Thread.sleep(2000);
			message = driver.findElement(By.cssSelector("div.detail-content .job-sec")).getText();
		} catch (Exception e) {
			try {
				Thread.sleep(20 * 1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			System.out.println("根据url获取message失败,可能是需要输入验证码");
		}
		return message;
	}

	/**
	 * 方法的出口,条件是没有status=1 而且有message了
	 */
	public void pickMapFromMessage() {
		s: while (true) {
			try {
				conn.setAutoCommit(false);
			} catch (SQLException e1) {
				System.out.println("设置自动提交false失败");
			}
			Map<String, Integer> message_map = new HashMap<String, Integer>();
			String sql = "SELECT url_id,message FROM job_message WHERE status=1 ORDER BY url_id LIMIT 1";
			try {
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery();
				conn.commit();
				if (rs.wasNull()) {
					// 方法的出口,条件是没有status=1的记录
					return;
				} else {
					rs.next();
					int urlId = rs.getInt(1);
					// 如果查到了记录,就把相对应的记录的status设置为2
					try {
						sql = "update job_message set status=2 where url_id=" + urlId;
						stmt = conn.prepareStatement(sql);
						stmt.executeUpdate();
						conn.commit();
					} catch (Exception e1) {
						System.out.println("设置message处理完毕的status=2失败,url_id:" + urlId);
						try {
							conn.rollback();
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
					String message = rs.getString(2);
					MMAnalyzer mm = new MMAnalyzer();
					String[] keys = mm.segment(message, "|").split("\\|");
					for (String key : keys) {
						if (key.matches("[a-zA-Z/#\\\\]+")) {
							// 如果符合英文,但是已有,则value+1
							if (message_map.containsKey(key)) {
								message_map.put(key, message_map.get(key) + 1);
							} else {// 如果不包含
								message_map.put(key, 1);
							}
						}
					}

					try {
						sql = "UPDATE job_message SET message_map = ? WHERE url_id=?";
						stmt = conn.prepareStatement(sql);
						stmt.setObject(1, message_map);
						stmt.setInt(2, urlId);
						stmt.executeUpdate();
						conn.commit();
					} catch (Exception e) {
						conn.rollback();
						System.out.println("message_map写入失败,url_id:" + urlId);
					}
				}
			} catch (Exception e) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
				continue s;
			}
		}
	}

	public Map<String, Integer> readMap() {

		String sql = "SELECT message_map FROM job_message WHERE message IS NOT NULL ORDER BY url_id LIMIT 1";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			// System.out.println("rs.size" + rs.wasNull());
			rs.next();
			Blob message_map = rs.getBlob(1);
			ObjectInputStream objIs = new ObjectInputStream(message_map.getBinaryStream());
			Map<String, Integer> map = (Map<String, Integer>) objIs.readObject();
			// Set<String> keySet = map.keySet();
			// for (String key : keySet) {
			// System.out.println("key:" + key + " value:" + map.get(key));
			// }

			// 一定要考虑map为空的情况

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Map<String, Integer>> combineMaps() {
		List<Map<String, Integer>> mapList = null;
		while (true) {
			String sql = "SELECT key_words FROM key_map WHERE key_word='java' ORDER BY id LIMIT 1";
			try {
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery();
				if (rs.wasNull()) {
					System.out.println("mapList完成");
					return mapList;
				}
				rs.next();
				Blob keyWords = rs.getBlob(1);
				ObjectInputStream objIs = new ObjectInputStream(keyWords.getBinaryStream());
				Map<String, Integer> map = (Map<String, Integer>) objIs.readObject();
				mapList = new ArrayList<Map<String, Integer>>();
				mapList.add(map);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Map<String, Integer> combine(List<Map<String, Integer>> mapList) {
		Map<String, Integer> keymap = new HashMap<String, Integer>();
		for (Map<String, Integer> map : mapList) {
			if (map == null) {
				continue;
			}
			Set<String> keySet = map.keySet();
			for (String key : keySet) {
				if (keymap.containsKey(key)) {
					keymap.put(key, keymap.get(key) + 1);
				} else {
					keymap.put(key, 1);
				}
			}
		}
		return keymap;
	}
}
