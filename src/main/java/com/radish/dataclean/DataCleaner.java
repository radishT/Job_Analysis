package com.radish.dataclean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.edmund.test.Test;
/**
 * 数据清洗的相关类
 * @author radish
 *
 */
public class DataCleaner {
	private Connection conn;

	/**
	 * 构造器初始化数据库的连接
	 */
	public DataCleaner() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://localhost:3306/crawler_db?characterEncoding=utf-8";
		String username = "root";
		String password = "admin";
		conn = DriverManager.getConnection(url, username, password);
	}

	/**
	 * 
	 * 根据url_list,为lagou表每一条记录根据city填上province
	 * @throws Exception 
	 */
	public void insertProvince() throws Exception {
		String sql = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		while (true) {
			try {
				sql = "SELECT id,city FROM lagou WHERE province IS NULL LIMIT 1";
				stmt = conn.prepareStatement(sql);
				rs = stmt.executeQuery();
				// 如果查询到数据
				if (rs.next()) {
					int id = rs.getInt(1);
					String city = rs.getString(2);
					// 用city去job_data表查询province
					sql = "SELECT province FROM url_list WHERE city=? LIMIT 1";
					stmt = conn.prepareStatement(sql);
					stmt.setString(1, city);
					rs = stmt.executeQuery();
					// 如果有查到province
					if (rs.next()) {
						String province = rs.getString(1);
						sql = "UPDATE lagou SET province=? WHERE city = ?";
						stmt = conn.prepareStatement(sql);
						stmt.setString(1, province);
						stmt.setString(2, city);
						stmt.executeUpdate();
					} else {
						String province = "null";
						sql = "UPDATE lagou SET province=? WHERE id = ?";
						stmt = conn.prepareStatement(sql);
						stmt.setString(1, province);
						stmt.setInt(2, id);
						stmt.executeUpdate();
					}
				} else {
					System.out.println("处理完毕");
					if (stmt != null) {
						stmt.close();
					}
					if (rs != null) {
						rs.close();
					}
					System.exit(0);
				}
			} catch (Exception e) {
				if (stmt != null) {
					stmt.close();
				}
				if (rs != null) {
					rs.close();
				}
				e.printStackTrace();
				System.exit(0);
			}
		}
	}

	/**
	 * 根据url_list,为job_data表每一条记录根据city填上province
	 * 
	 * @throws Exception
	 */
	public void insertProvinceToBoss() throws Exception {
		String sql = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		while (true) {
			try {
				sql = "SELECT id,city FROM job_data WHERE province IS NULL LIMIT 1";
				stmt = conn.prepareStatement(sql);
				rs = stmt.executeQuery();
				// 如果查询到数据
				if (rs.next()) {
					int id = rs.getInt(1);
					String city = rs.getString(2);
					// 用city去job_data表查询province
					sql = "SELECT province FROM url_list WHERE city=? LIMIT 1";
					stmt = conn.prepareStatement(sql);
					stmt.setString(1, city);
					rs = stmt.executeQuery();
					// 如果有查到province
					if (rs.next()) {
						String province = rs.getString(1);
						sql = "UPDATE job_data SET province=? WHERE city = ?";
						stmt = conn.prepareStatement(sql);
						stmt.setString(1, province);
						stmt.setString(2, city);
						stmt.executeUpdate();
					} else {
						String province = "null";
						sql = "UPDATE job_data SET province=? WHERE id = ?";
						stmt = conn.prepareStatement(sql);
						stmt.setString(1, province);
						stmt.setInt(2, id);
						stmt.executeUpdate();
					}
				} else {
					System.out.println("处理完毕");
					if (stmt != null) {
						stmt.close();
					}
					if (rs != null) {
						rs.close();
					}
					System.exit(0);
				}
			} catch (Exception e) {
				if (stmt != null) {
					stmt.close();
				}
				if (rs != null) {
					rs.close();
				}
				e.printStackTrace();
				System.exit(0);
			}
		}
	}

	/**
	 * 将拉勾网的所有数据清洗后,填写到表job_data_result
	 * 
	 */
	public void moveLagouDataToResult() throws Exception {

	}

	/**
	 * 把BOSS直聘的数据清洗后插入到表job_data_result
	 * 
	 */
	public void moveBossDataToResult() throws Exception {
		int count=0;
		String sql = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		sql = "SELECT province,"// 1
				+ "city,"// 2
				+ "key_word,"// 3
				+ "company,"// 4
				+ "salary,"// 5
				+ "experience,"// 6
				+ "education" // 7
				+ " FROM job_data";
		stmt = conn.prepareStatement(sql);
		rs = stmt.executeQuery();
		/*
		 * 测试rs的长度,输出结果为46353,全部可以提取 rs.last(); System.out.println(rs.getRow());
		 */
		// 查到46353条记录后
		// 插入到结果集的data_from 1代表BOSS直聘的数据
		int dataFrom = 1;
		String province = null;
		String city = null;
		String keyWord = null;
		String companyOrTeam = null;
		double minSalary = 0.0;
		double maxSalary = 0.0;
		double avgSalary = 0.0;
		int minExperience=0;
		int minEducation=0;
		// key_words_map留空
		while (rs.next()) {
			try {
				// 提取province----->
				province = rs.getString(1);
				// 提取city
				city = rs.getString(2);
				// 提取关键词
				keyWord = rs.getString(3);
				// 提取公司_组织名
				companyOrTeam = rs.getString(4);
				/*
				 * 提取salary,并做处理
				 */
				String salaryStr = rs.getString(5);
				try {
					String[] salaryArray = salaryStr.trim().split("-");
					String minSalaryStr = salaryArray[0];
					minSalary = Double.parseDouble(minSalaryStr.substring(0, minSalaryStr.indexOf("k")));
					String maxSalaryStr = salaryArray[1];
					maxSalary = Double.parseDouble(maxSalaryStr.substring(0, maxSalaryStr.indexOf("k")));
					minSalary=minSalary*1000;
					maxSalary=maxSalary*1000;
					avgSalary = (minSalary + maxSalary) / 2;
				} catch (Exception e) {
					// 如果salary处理失败或者报错,任何薪水项=0
					maxSalary = 0;
					minSalary = 0;
					avgSalary = 0;
				}
				/*
				 * 提取工作经验:experience 处理后--->min_experience 处理失败的都置为0
				 */
				String experienceStr = rs.getString(6);
				try {
					minExperience = Integer.parseInt(String.valueOf(experienceStr.trim().charAt(0)));
				} catch (Exception e) {
					// 如果最小工作经验转化失败,则置为-1
					minExperience = 0;
				}
				/*
				 * 提取学历,处理出min_education
				 * 如果处理失败,则置为-1
				 * 最低学历:
				 */
				String educationStr=rs.getString(7);
				try {
					if(educationStr.contains("专")){
						minEducation=1;
					}else if(educationStr.contains("本")){
						minEducation=2;
					}else if(educationStr.contains("硕")){
						minEducation=3;
					}else if(educationStr.contains("博")){
						minEducation=4;
					}else {
						minEducation=0;
					}
				} catch (Exception e) {
					// 最小学历转化失败,则置为-1
					minEducation = -1;
				}
			} catch (Exception e) {
				System.out.println("getString失败");
				e.printStackTrace();
			}
			sql="INSERT INTO job_data_result(data_from,province,city,key_word"
					+ ",company_or_team,min_salary,max_salary,avg_salary"
					+ ",min_experience,min_education) "
					+ "VALUES(?,?,?,?"
					+ ",?,?,?,?"
					+ ",?,?"
					+ ")";
			stmt=conn.prepareStatement(sql);
			stmt.setInt(1, dataFrom);
			stmt.setString(2, province);
			stmt.setString(3, city);
			stmt.setString(4, keyWord);
			stmt.setString(5, companyOrTeam);
			stmt.setDouble(6, minSalary);
			stmt.setDouble(7, maxSalary);
			stmt.setDouble(8, avgSalary);
			stmt.setInt(9, minExperience);
			stmt.setInt(10, minEducation);
			stmt.executeUpdate();
			count++;
		}
		System.out.println("插入条数:"+count);
	}

	public static void main(String[] args) {
		try {
			DataCleaner cleaner = new DataCleaner();
			cleaner.moveBossDataToResult();
			// cleaner.moveBossDataToResult();
			// cleaner.moveLagouDataToResult();
			// String string = " 3-5年本科";
			// string = string.trim();
			// System.out.println(string.charAt(0));
			//test();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void test() {
		String string = "10k-20k";
		string = string.trim();
		for (String string2 : string.split("-")) {
			System.out.println(string2);
		}
		String experienceStr = "";
		
	}
}
