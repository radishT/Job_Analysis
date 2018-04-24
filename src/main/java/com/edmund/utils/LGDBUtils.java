package com.edmund.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.edmund.vo.LGJob;

/**
 * 拉勾网操作工具类
 * @author Edmund
 *
 */
public class LGDBUtils {
	private PrintWriter pw = null;

	public DataBaseConnection dbc = null;

	public LGDBUtils(DataBaseConnection dbc) {
		this.dbc = dbc;
	}

	/**
	 * 向数据库中写入需要处理的url
	 * @param url
	 */
	public void writeIntoReadyURL(String url, String keyword) {
		String sql = "INSERT INTO ready_url (url,state,keyword) VALUES (?,0,?)";
		try {
			dbc.getConn().setAutoCommit(false);
			PreparedStatement pst = dbc.getConn().prepareStatement(sql);
			pst.setString(1, url);
			pst.setString(2, keyword);
			pst.executeUpdate();
			dbc.getConn().commit();
			pst.close();

		} catch (SQLException e) {
			try {
				dbc.getConn().rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	/**
	 * 从数据库中读取未被处理过得url，并将其的状态值改为1,由于需要记录职位对应的关键字，故将keyword也一并取出
	 * @return
	 */
	public String[] readFromReadyURL() {
		String sql = "SELECT id,url,keyword FROM ready_url WHERE state=0 LIMIT 1";
		String updateSql = "UPDATE ready_url SET state=1 WHERE id=?";
		String[] infos = null;
		try {
			dbc.getConn().setAutoCommit(false);
			PreparedStatement pst = dbc.getConn().prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				String id = rs.getString(1);
				String url = rs.getString(2);
				String keyword = rs.getString(3);
				infos = new String[2];
				infos[0] = url;
				infos[1] = keyword;
				pst = dbc.getConn().prepareStatement(updateSql);
				pst.setInt(1, Integer.parseInt(id));
				pst.executeUpdate();
			}

			dbc.getConn().commit();
			rs.close();
			pst.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return infos;

	}

	/**
	 * 向数据库中插入一条职位信息记录
	 * @param job 职位信息对象LGJob
	 */
	public void insertLGJob(LGJob job) {
		String sql = "INSERT INTO lagou (key_word,job,salary,city,experience,education,company,key_words) VALUES (?,?,?,?,?,?,?,?)";
		try {
			dbc.getConn().setAutoCommit(false);
			PreparedStatement pst = dbc.getConn().prepareStatement(sql);
			pst.setString(1, job.getKeyword());
			pst.setString(2, null);
			pst.setString(3, job.getSalary());
			pst.setString(4, job.getCity());
			pst.setString(5, job.getExperience());
			pst.setString(6, job.getEducation());
			pst.setString(7, job.getCompany());
			pst.setObject(8, job.getKeywords());

			pst.executeUpdate();
			dbc.getConn().commit();
			pst.close();

		} catch (SQLException e) {
			try {
				dbc.getConn().rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

	}

	/**
	 * 读取数据库中的所有职位信息记录,并封装为对象列表
	 * @return 职位信息对象列表
	 */
	public List<LGJob> getLGJob() {
		String sql = "SELECT key_word,job,salary,city,experience,education,company,key_words FROM lagou";
		List<LGJob> jobs = new ArrayList<LGJob>();
		try {
			PreparedStatement pst = dbc.getConn().prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				Blob kwBlob = rs.getBlob(8);
				ObjectInputStream objIn = new ObjectInputStream(
						kwBlob.getBinaryStream());
				Map<String, Integer> keywords = (Map<String, Integer>) objIn
						.readObject();
				LGJob job = new LGJob(null, rs.getString(1), null,
						rs.getString(3), rs.getString(4), rs.getString(5),
						rs.getString(6), rs.getString(7), keywords);
				jobs.add(job);
				objIn.close();
			}
			rs.close();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return jobs;
	}

	/**
	 * 从指定文件路径中读取文件
	 * @param filepath 文件路径
	 * @return 以行为单位保存的列表
	 * @throws IOException
	 */
	public List<String> readFromFile(String filepath) throws IOException {
		List<String> cities = new ArrayList<String>();

		File file = new File(filepath);
		FileInputStream in = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(in, "UTF-8"));
		String line = null;

		while ((line = reader.readLine()) != null) {
			cities.add(line);
		}
		reader.close();
		return cities;
	}

	/**
	 * 关闭writer
	 */
	public void closeAll() {
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
	public void initWriter(String filepath) throws FileNotFoundException {
		if (pw == null) {
			File file = new File(filepath);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			pw = new PrintWriter(new FileOutputStream(file, true));
		}
	}

}
