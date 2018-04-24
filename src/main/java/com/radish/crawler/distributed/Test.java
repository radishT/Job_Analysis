package com.radish.crawler.distributed;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.radish.vo.BOSSUrlVO;
import com.radish.vo.JobDataVO;

public class Test {
	private static Connection conn;
	private static ChromeDriver driver;
	static{
		DistributedCrawler crawler = new DistributedCrawler();
		System.out.println("新建 crawler对象");
		System.out.println("设置 system.property");
		driver = crawler.getDriver();
		conn = crawler.getConn();
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			System.out.println("设置不自动提交失败");
		}
		System.out.println("初始化块执行完毕");
	}
	public static void main(String[] args) {
		try {
			// 事务处理串行化
			conn.setAutoCommit(false);
		} catch (SQLException e2) {
			System.out.println("设置自动提交失败");
			e2.printStackTrace();
		}

		while (true) {
			try {
				// 查询一个任务的SELECT语句
				String sql = "SELECT id,province,city,url,key_word,status " + "FROM url_list "
						+ "WHERE status=0 order by id limit 1 ";
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery();
				System.out.println("select 执行");
				// 查到东西
				if (!rs.wasNull()) {
					System.out.println("查到了一个对象");
					// 取出到vo对象
					BOSSUrlVO vo = null;
					while (rs.next()) {
						int id = rs.getInt(1);
						String province = rs.getString(2);
						String city = rs.getString(3);
						String url = rs.getString(4);
						System.out.println("url----"+url);
						String keyWord = rs.getString(5);
						int status = rs.getInt(6);
						vo = new BOSSUrlVO(id, province, city, url, keyWord, status);
					}
					// 就修改status = 1
					sql = "UPDATE url_list set status =1 where id=" + vo.getId();
					stmt = conn.prepareStatement(sql);
					stmt.executeUpdate();
					System.out.println("update 执行" );
					conn.commit();
					System.out.println("查询+update status事务提交完毕");
					rs.close();
					stmt.close();
					
					//此刻已经拿到vo
					driver.get(vo.getUrl());
					work(driver,vo);
				} else {// 如果没查到,程序退出
					rs.close();
					stmt.close();
					System.out.println("数据库中没任务了");
					conn.close();
					System.exit(0);
				}
			} catch (Exception e) {
				System.out.println("事务处理失败  rollback");
				try {
					conn.rollback();
					continue;
				} catch (Exception e1) {
					System.out.println("rollback 失败");
					continue;
				}
			}
		} // while
		
	}// main
	public static void work(ChromeDriver driver,BOSSUrlVO urlVo){
		WebDriverWait wait = new WebDriverWait(driver, 8);
		try {
			while (true) {
				// 等待加载
				//wait.until(ExpectedConditions.presenceOfElementLocated(By.id("footer")));
				wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#wrap")));
				// 爬取内容
				// 先爬取所有的div.job-list div.job-primary
				List<WebElement> divList = driver.findElementsByCssSelector("div.job-list div.job-primary");
				for (WebElement jobDiv : divList) {
					// 得到title salary city experience education company
					// 标题
					String title = jobDiv.findElement(By.cssSelector("div.job-title")).getText();
					// 收入
					String salary = jobDiv.findElement(By.cssSelector("span.red")).getText();
					// 企业
					String company = jobDiv.findElement(By.cssSelector("div.company-text h3")).getText();
					// 工作经验       学历
					String text = jobDiv.findElement(By.cssSelector("div.info-primary p")).getText();
					String experience = text.substring(text.indexOf(" "));
					String education = text.substring(text.length()-2);
					JobDataVO jobData = new JobDataVO(urlVo.getId(), urlVo.getCity(), urlVo.getKey(), title, company, null, salary, experience, education, null, null);
					insertData(jobData);
				}
				WebElement nextElement = null;
				// 如果有下一页,则点击下一页,否则
				if((nextElement=driver.findElement(By.cssSelector("div.page a.next")))!=null){
					if(nextElement.getAttribute("class").contains("disabled")){
						return;
					}else{
						nextElement.click();
					}
				}else{// 如果没找到就结束了.
					return;
				}
			}
		} catch (Exception e) {
			// 如果这个while出错,比如被屏蔽需要输入验证码
			System.out.println("需要输入验证码");
			try {
				Thread.sleep(20*1000);
			} catch (InterruptedException e1) {
				System.out.println("sleep 失败");
			}
		} 
	}// work()
	public static void insertData(JobDataVO dataVO){
		try {
			String sql= "INSERT INTO job_data(id,city,key_word,title,company,salary,experience,education) "
					+ "VALUES(?,?,?,?,?,?,?,?)";
			PreparedStatement stmt=conn.prepareStatement(sql);
			stmt.setInt(1, dataVO.getId());
			stmt.setString(2, dataVO.getCity());
			stmt.setString(3, dataVO.getKeyWord());
			stmt.setString(4, dataVO.getTitle());
			stmt.setString(5, dataVO.getCompany());
			stmt.setString(6, dataVO.getSalary());
			stmt.setString(7, dataVO.getExperience());
			stmt.setString(8, dataVO.getEducation());
			stmt.executeUpdate();
			conn.commit();
			stmt.close();
			System.out.println("插入一条数据:" + dataVO.toString());
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				System.out.println("rollback 失败");
			}
			System.out.println("插入结果数据失败");
		}
	}
}
