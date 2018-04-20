package com.edmund.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.edmund.vo.Job;

/**
 * 用于支持职位信息爬虫的文件操作类
 * 
 * @author Edmund
 *
 */
public class DBUtils {
	private static PrintWriter pw = null;
	private static int count = 1;

	/**
	 * 从文件中读取网站根路径和城市
	 * 
	 * @param filepath
	 *            文件路径
	 * @return 包含网站根路径列表和城市列表的map集合,可以通过get("cities")获得城市列表,get("roots")获得网站根路径列表，两个列表的索引一一对应
	 * @throws IOException
	 */
	public static Map<String, List<String>> readFromFile(String filepath)
			throws IOException {
		Map<String, List<String>> infos = new HashMap<String, List<String>>();
		List<String> cities = new ArrayList<String>();
		List<String> roots = new ArrayList<String>();

		File file = new File(filepath);
		FileInputStream in = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(in, "UTF-8"));
		String line = null;

		while ((line = reader.readLine()) != null) {
			cities.add(line.split("\\t")[1]);
			roots.add(line.split("\\t")[2]);
		}
		infos.put("cities", cities);
		infos.put("roots", roots);
		reader.close();
		return infos;
	}

	/**
	 * 将职位信息写入到文件中
	 * 
	 * @param job
	 *            职位信息
	 * @param filepath
	 *            保存的文件路径
	 * @throws FileNotFoundException
	 */
	public static void writeToFile(Job job, String filepath)
			throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(
				new FileOutputStream(new File(filepath), true));
		pw.print(job.getCity() + "\t");
		pw.print(job.getKey() + "\t");
		pw.print(job.getTitle() + "\t");
		pw.print(job.getSalary() + "\t");
		pw.print(job.getCompany() + "\t");
		pw.print(job.getJob() + "\t");
		pw.print(job.getEducation() + "\t");
		pw.println(job.getExperience());
		pw.flush();
		pw.close();
	}

	/**
	 * 将职位信息列表中的职位信息写入到文件中
	 * 
	 * @param jobs
	 *            职位信息列表
	 * @param filepath
	 *            文件路径
	 */
	public static void writeToFile(List<Job> jobs, String filepath) {
		if (jobs == null || jobs.isEmpty()) {
			return;
		}
		try {
			initWriter(filepath);
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
	 * @param filepath 文件路径
	 * @throws FileNotFoundException
	 */
	public static void initWriter(String filepath)
			throws FileNotFoundException {
		if (pw == null) {
			File file = new File(filepath);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			pw = new PrintWriter(new FileOutputStream(file, true));
		}
	}
}
