package com.radish.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.radish.vo.BOSSUrlVO;
/**
 * 初始化表url_list
 * 表结构:
 * `id` int(11) NOT NULL AUTO_INCREMENT,  任务标号
  `province` varchar(50) NOT NULL,
  `city` varchar(100) NOT NULL,
  `url` varchar(500) NOT NULL,
  `key_word` varchar(50) NOT NULL,
   status  int not null   0代表爬虫可领取,1代表爬虫已领取,2代表爬虫领取任务后成功提交
 * @author admin
 *
 */
public class UrlListIniter {
	// 初始化爬取队列,每个VO中的url都是可直接访问的
	private static List<BOSSUrlVO> urlList = new ArrayList<BOSSUrlVO>();
	// 要爬取的关键词 java python web linux C#
	private static String[] keys = new String[] { "java", "python", "web", "linux","C%23" };
	// 数据库连接
	private static Connection conn;
	private static String sql="";
	static{
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String username="root";
			String password="admin";
			String url="jdbc:mysql://localhost:3306/crawler_db?characterEncoding=utf8";
			conn=DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			System.out.println("静态初始化块出错");
		}
	}
	public static void main(String[] args) throws Exception {
		String filePath = "C:/Users/admin/Desktop/BossUrl.txt";
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(new File(filePath)), "UTF-8"));
		// 逐行读
		String line = null;
		while ((line = reader.readLine()) != null) {
			StringTokenizer tokens = new StringTokenizer(line);
			String province = null;
			String city = null;
			String url = null;
			if (tokens.hasMoreTokens()) {
				province = tokens.nextToken();
			}
			if (tokens.hasMoreTokens()) {
				city = tokens.nextToken();
			}
			if (tokens.hasMoreTokens()) {
				url = tokens.nextToken();
			}
			// 根据关键词数组进行初始化
			for (int i = 0; i < keys.length; i++) {
				urlList.add(new BOSSUrlVO(province, city, url, keys[i]));
			}
		}
		reader.close();
		// 如果list初始化成功
		if (urlList.size() != 0) {
			for (BOSSUrlVO vo : urlList) {
				insertVO(vo);
			}
		} else {
			System.out.println("list 初始化失败 程序退出");
			System.exit(0);
		}
		conn.close();
	}// main
	/**
	 * 向数据库插入一条数据
	 * @param vo
	 */
	public static void insertVO(BOSSUrlVO vo){
		try {
			//conn.setTransactionIsolation(conn.TRANSACTION_SERIALIZABLE);
			//conn.setAutoCommit(false);
			sql="INSERT INTO url_list(province,city,url,key_word,status) VALUES(?,?,?,?,0)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, vo.getProvince());
			stmt.setString(2, vo.getCity());
			stmt.setString(3, vo.getUrl());
			stmt.setString(4, vo.getKey());
			stmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("insertVO error!");
			e.printStackTrace();
		}
		
	}
}
