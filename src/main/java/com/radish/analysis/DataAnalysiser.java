package com.radish.analysis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于得出最终要展示的数据
 * @author radish
 *
 */
public class DataAnalysiser {
	private static Connection conn;
	private static String[] provinceArray = new String[] { "北京", "天津", "河北", "山西", "内蒙古", "辽宁", "吉林", "黑龙江", "上海", "江苏",
			"浙江", "安徽", "福建", "江西", "山东", "河南", "湖北", "湖南", "广东", "广西", "海南", "重庆", "四川", "贵州", "云南", "西藏", "陕西", "甘肃",
			"青海", "宁夏", "新疆" };
	private static String[] keyWordArray = new String[] { "java", "C#", "linux", "python", "web", "c++", "android" };

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/crawler_db?characterEncoding=utf-8";
			String username = "root";
			String password = "admin";
			conn = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * 生成数据
	 */
	public static void main(String[] args) throws Exception {

		for (int i = 0; i < keyWordArray.length; i++) {
			String key = keyWordArray[i];
			for (int strNum = 1; strNum <= 6; strNum++) {
				System.out.print("dataMap."+keyWordArray[i] + "data" + strNum + "=dataFormatter({");// javadata1--
				if (strNum == 1) {
					System.out.print("2018:[");
					List<Integer> countList = countJobRequestNumber(key);
					for (int k = 0; k < countList.size() - 1; k++) {
						System.out.print(countList.get(k) + ",");
					}
					System.out.print(countList.get(countList.size() - 1));
					System.out.print("]});");
					System.out.println();
				}
				// 第二行数据是平均薪资
				if (strNum == 2) {
					System.out.print("2018:[");
					List<Double> countList = countAvgSalary(key);
					for (int k = 0; k < countList.size() - 1; k++) {
						System.out.print(countList.get(k) + ",");
					}
					System.out.print(countList.get(countList.size() - 1));
					System.out.print("]});");
					System.out.println();
				}
				// 第三行数据是平均最高工资
				if (strNum == 3) {
					System.out.print("2018:[");
					List<Double> countList = countMaxSalary(key);
					for (int k = 0; k < countList.size() - 1; k++) {
						System.out.print(countList.get(k) + ",");
					}
					System.out.print(countList.get(countList.size() - 1));
					System.out.print("]});");
					System.out.println();
				}
				// 第四行数据是平均最低工资
				if (strNum == 4) {
					System.out.print("2018:[");
					List<Double> countList = countMinSalary(key);
					for (int k = 0; k < countList.size() - 1; k++) {
						System.out.print(countList.get(k) + ",");
					}
					System.out.print(countList.get(countList.size() - 1));
					System.out.print("]});");
					System.out.println();
				}
				// 本科以及以上员工占岗位数的百分比
				if (strNum == 5) {
					System.out.print("2018:[");
					List<Double> countList = countEducationOver2Percent(key);
					for (int k = 0; k < countList.size() - 1; k++) {
						System.out.print(countList.get(k) + ",");
					}
					System.out.print(countList.get(countList.size() - 1));
					System.out.print("]});");
					System.out.println();
				}
				// 工作经验不限的比例
				if (strNum == 6) {
					System.out.print("2018:[");
					List<Double> countList = countExperienceIn0(key);
					for (int k = 0; k < countList.size() - 1; k++) {
						System.out.print(countList.get(k) + ",");
					}
					System.out.print(countList.get(countList.size() - 1));
					System.out.print("]});");
					System.out.println();
				}
			}
		}

	}

	/**
	 * 
	 * @param key 搜索关键字
	 * @param city 搜索所在省市
	 * @return 搜索数据库中各大province的指定搜索关键词key的总条数
	 */
	public static List<Integer> countJobRequestNumber(String key) throws Exception {
		List<Integer> countList = new ArrayList<Integer>();
		String sql = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		for (String province : provinceArray) {
			sql = "SELECT COUNT(id) FROM job_data_result where key_word=? and province=?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, key);
			stmt.setString(2, province);
			rs = stmt.executeQuery();
			rs.next();
			int jobRequestNumber = rs.getInt(1);
			countList.add(jobRequestNumber);
		}
		if (rs != null) {
			rs.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		return countList;
	}

	/**
	 * 计算平均薪资
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static List<Double> countAvgSalary(String key) throws Exception {
		List<Double> countList = new ArrayList<Double>();
		String sql = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		for (String province : provinceArray) {
			sql = "SELECT AVG(avg_salary) FROM job_data_result where key_word=? and province=?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, key);
			stmt.setString(2, province);
			rs = stmt.executeQuery();
			rs.next();
			double jobRequestNumber = Double.parseDouble(String.format("%.2f", rs.getDouble(1)));
			if (!Double.isNaN(jobRequestNumber)) {
				countList.add(jobRequestNumber);
			} else {
				countList.add(0.0);
			}
		}
		if (rs != null) {
			rs.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		return countList;
	}

	/**
	 * 计算平均最大工资值
	 * @param key
	 * @return 搜索数据库中各大province的指定搜索关键词key的平均最大工资值
	 * @throws Exception
	 */
	public static List<Double> countMaxSalary(String key) throws Exception {
		List<Double> countList = new ArrayList<Double>();
		String sql = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		for (String province : provinceArray) {
			sql = "SELECT AVG(max_salary) FROM job_data_result where key_word=? and province=?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, key);
			stmt.setString(2, province);
			rs = stmt.executeQuery();
			rs.next();
			double jobRequestNumber = Double.parseDouble(String.format("%.2f", rs.getDouble(1)));
			if (!Double.isNaN(jobRequestNumber)) {
				countList.add(jobRequestNumber);
			} else {
				countList.add(0.0);
			}
		}
		if (rs != null) {
			rs.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		return countList;
	}

	/**
	 * 计算平均最小工资值
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static List<Double> countMinSalary(String key) throws Exception {
		List<Double> countList = new ArrayList<Double>();
		String sql = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		for (String province : provinceArray) {
			sql = "SELECT AVG(min_salary) FROM job_data_result where key_word=? and province=?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, key);
			stmt.setString(2, province);
			rs = stmt.executeQuery();
			rs.next();
			double jobRequestNumber = Double.parseDouble(String.format("%.2f", rs.getDouble(1)));
			if (!Double.isNaN(jobRequestNumber)) {
				countList.add(jobRequestNumber);
			} else {
				countList.add(0.0);
			}
		}
		if (rs != null) {
			rs.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		return countList;
	}

	/**
	 * 计算本科以及以上员工占岗位数的百分比
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static List<Double> countEducationOver2Percent(String key) throws Exception {
		List<Double> countList = new ArrayList<Double>();
		String sql = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		for (String province : provinceArray) {
			// 先查询员工总数总数
			sql = "SELECT COUNT(id) FROM job_data_result WHERE key_word=? and province=?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, key);
			stmt.setString(2, province);
			rs = stmt.executeQuery();
			rs.next();
			int staffCount = rs.getInt(1);
			// 再查询本科以上的个数
			sql = "SELECT COUNT(id) FROM job_data_result WHERE key_word=? AND province=? AND " + "min_education>1 ";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, key);
			stmt.setString(2, province);
			rs = stmt.executeQuery();
			rs.next();
			int staffOver2 = rs.getInt(1);
			double persent = (staffOver2 * 1.0) / staffCount;
			double jobRequestNumber = Double.parseDouble(String.format("%.2f", persent));
			if (!Double.isNaN(jobRequestNumber)) {
				countList.add(jobRequestNumber);
			} else {
				countList.add(0.0);
			}
		}
		if (rs != null) {
			rs.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		return countList;
	}

	/**
	 * 计算工作经验不限的比例
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static List<Double> countExperienceIn0(String key) throws Exception {
		List<Double> countList = new ArrayList<Double>();
		String sql = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		for (String province : provinceArray) {
			// 先查询员工总数总数
			sql = "SELECT COUNT(id) FROM job_data_result WHERE key_word=? AND province=?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, key);
			stmt.setString(2, province);
			rs = stmt.executeQuery();
			rs.next();
			int staffCount = rs.getInt(1);
			// 再查询本科以上的个数
			sql = "SELECT COUNT(id) FROM job_data_result WHERE key_word=? AND province=? AND " + "min_experience=1 ";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, key);
			stmt.setString(2, province);
			rs = stmt.executeQuery();
			rs.next();
			int staffSelected = rs.getInt(1);
			double persent = (staffSelected * 1.0) / staffCount;
			double jobRequestNumber = Double.parseDouble(String.format("%.2f", persent));
			if (!Double.isNaN(jobRequestNumber)) {
				countList.add(jobRequestNumber);
			} else {
				countList.add(0.0);
			}
		}
		if (rs != null) {
			rs.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		return countList;
	}
}
