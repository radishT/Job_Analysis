package com.edmund.utils;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 拉钩数据表清洗工具类
 * @author Edmund
 *
 */
public class LGCleanUtils {
	public DataBaseConnection dbc = null;

	public LGCleanUtils(DataBaseConnection dbc) {
		this.dbc = dbc;
	}

	/**
	 * experience字段的清洗方法
	 * @param experience
	 * @return -1表示清洗失败, 否则返回大于0的值
	 */
	private int experienceClean(String experience) {
		String strClean = experience.substring(2);
		int expClean = -1;
		if (strClean.matches("[0-9]+-[0-9]+年")) {
			expClean = Integer.parseInt(strClean.split("-")[0]);
		} else if (strClean.contains("不限") || strClean.contains("应届毕业生")
				|| strClean.matches("[0-9]+年以下")) {
			expClean = 0;
		} else if (strClean.matches("[0-9]+年以上")) {
			expClean = Integer.parseInt(strClean.split("年")[0]);
		}
		return expClean;
	}

	/**
	 * education字段的清洗方法
	 * @param education 
	 * @return -1 表示清洗失败,否则返回大于0的值
	 */
	private int educationClean(String education) {
		int edu = -1;
		if (education.matches("学历不限")) {
			edu = 0;
		} else if (education.matches("大专及以上")) {
			edu = 1;
		} else if (education.matches("本科及以上")) {
			edu = 2;
		} else if (education.matches("硕士及以上")) {
			edu = 3;
		}
		return edu;
	}

	/**
	 * salary字段的清洗方法
	 * @param salary
	 * @return 三个均为0表示清洗失败，否则返回大于0的三个值数组
	 */
	private int[] salaryClean(String salary) {
		int[] cleanSal = new int[3];
		int min_salary = 0;
		int max_salary = 0;
		int avg_salary = 0;
		if (salary.matches("[0-9]+[kK]-[0-9]+[kK]")) {
			String[] sals = salary.split("-");
			min_salary = Integer
					.parseInt(sals[0].replace("k", "000").replace("K", "000"));
			max_salary = Integer
					.parseInt(sals[1].replace("k", "000").replace("K", "000"));
		} else if (salary.matches("[0-9]+[kK]以上")) {
			String[] sals = salary.split("以上");
			min_salary = Integer
					.parseInt(sals[0].replace("k", "000").replace("K", "000"));
			max_salary = Integer.parseInt(
					sals[0].replace("k", "000").replace("K", "000")) + 5000;
		}
		avg_salary = (min_salary + max_salary) / 2;
		cleanSal[0] = min_salary;
		cleanSal[1] = max_salary;
		cleanSal[2] = avg_salary;
		return cleanSal;
	}

	/**
	 * 拉钩数据表条目的清洗方法,用于清洗整个数据条目
	 * @param data_from 0表示清洗拉勾网数据,1表示清洗BOSS网数据
	 */
	public void JobClean(int data_from) {
		String query_sql = "SELECT id,key_word,job,salary,province,city,experience,education,company,key_words FROM lagou";
		String insert_sql = "INSERT INTO job_data_result(data_from,province,city,key_word,company_or_team,min_salary,max_salary,avg_salary,min_experience,min_education,key_words_map) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
		try {
			PreparedStatement pst = dbc.getConn().prepareStatement(query_sql);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(1);
				String key_word = rs.getString(2);
				String job = rs.getString(3);
				String salary = rs.getString(4);
				String province = rs.getString(5);
				String city = rs.getString(6);
				String experience = rs.getString(7);
				String education = rs.getString(8);
				String company_or_team = rs.getString(9);
				Blob key_words = rs.getBlob(10);

				int min_education = educationClean(education);
				int min_experience = experienceClean(experience);
				int[] cleanSal = salaryClean(salary);
				int min_salary = cleanSal[0];
				int max_salary = cleanSal[1];
				int avg_salary = cleanSal[2];

				pst = dbc.getConn().prepareStatement(insert_sql);
				pst.setInt(1, data_from);
				pst.setString(2, province);
				pst.setString(3, city);
				pst.setString(4, key_word);
				pst.setString(5, company_or_team);
				pst.setInt(6, min_salary);
				pst.setInt(7, max_salary);
				pst.setInt(8, avg_salary);
				pst.setInt(9, min_experience);
				pst.setInt(10, min_education);
				pst.setBlob(11, key_words);

				pst.executeUpdate();
			}

			rs.close();
			pst.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
