package com.edmund.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 数据库连接管理类
 * 
 * @author Edmund
 *
 */
public class DataBaseConnection {
	private static String DBDRIVER = "org.gjt.mm.mysql.Driver";
	private static String DBURL = "jdbc:mysql://localhost:3306/test";
	private static String DBUSER = "root";
	private static String DBPASSWORD = "redhat";

	private Connection conn = null;

	public DataBaseConnection() {
		super();
	}

	/**
	 * 返回一个数据库连接
	 * 
	 * @return
	 */
	public Connection getConn() {
		try {
			if (conn == null || conn.isClosed()) {
				Class.forName(DBDRIVER);
				conn = DriverManager.getConnection(DBURL, DBUSER, DBPASSWORD);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * 关闭数据库连接
	 */
	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
