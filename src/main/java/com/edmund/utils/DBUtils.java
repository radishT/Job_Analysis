package com.edmund.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;

import com.edmund.vo.Job;

/**
 * 用于支持职位信息爬虫的文件操作类
 * 
 * @author Edmund
 *
 */
public class DBUtils {
	private static final String BASEDIR = "C:/Users/admin/Desktop/jobs/";
	private static PrintWriter pw = null;
	private static int count = 1;

	/**
	 * 从文件中读取网站根路径和城市
	 * 
	 * @return String[0][N]表示根网站，String[1][N]表示城市
	 */
	public static String[][] readFromFile() {
		String[][] infos = new String[2][];
		return infos;
	}

	/**
	 * 将职位信息列表中的职位信息写入到文件中
	 * 
	 * @param jobs
	 * @param filename
	 */
	public static void writeToFile(List<Job> jobs, String filename) {
		if (jobs == null && jobs.isEmpty()) {
			return;
		}
		try {
			initWriter(filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (Job job : jobs) {
			System.out.println("正在处理: " + job + ",已处理: " + count++);
			pw.print(job.getCity() + "\t");
			pw.print(job.getKey() + "\t");
			pw.print(job.getTitle() + "\t");
			pw.print(job.getSalary() + "\t");
			pw.print(job.getCompany() + "\t");
			pw.print(job.getJob() + "\t");
			pw.print(job.getEducation() + "\t");
			pw.println(job.getExperience());
		}
		pw.flush();
		closeAll();

	}

	/**
	 * 关闭writer
	 */
	public static void closeAll() {
		if (pw != null) {
			pw.close();
			pw = null;
		}
	}

	/**
	 * 开启writer
	 * 
	 * @throws FileNotFoundException
	 */
	public static void initWriter(String filename) throws FileNotFoundException {
		if (pw == null) {
			pw = new PrintWriter(new FileOutputStream(new File(BASEDIR + filename), true));
		}
	}
}
