package com.radish.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.radish.vo.BOSSUrlVO;

/**
 * 单例模式实现管理爬取队列
 * @author admin
 *
 */
public class BOSSCrawlerManager {
	// 初始化爬取队列,每个VO中的url都是可直接访问的
	private List<BOSSUrlVO> urlList = new ArrayList<BOSSUrlVO>();
	// 要爬取的关键词 java python web linux
	private String[] keys = new String[] { "java", "python", "web", "linux" };
	// 爬虫间争抢runningCrawler的同步对象
	private Object obj = new Object();
	// 所有爬虫爬完后,通过obj通知main线程
	public Object mainThread = new Object();
	// 当前仍然在工作的爬虫数
	private Integer runningCrawler = 0;

	private static BOSSCrawlerManager instance = new BOSSCrawlerManager();

	// 不可构造,单例模式
	private BOSSCrawlerManager() {

	}

	public static BOSSCrawlerManager getInstance() {
		return instance;
	}

	/**
	 * 根据文件绝对路径初始化待爬取队列
	 * 初始化成功则返回true,否则false
	 * @return
	 */
	public boolean init(String filePath) {
		boolean flag = false;
		try {
			// 如果文件不存在
			if (!new File(filePath).exists()) {
				return false;
			}
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
			flag=true;
		} catch (Exception e) {
			// 如果出异常,返回false
			return false;
		}
		// 如果没返回true,则返回false
		return flag;
	}

	/**
	 * 设置爬虫领取任务的同步方法
	 * 如果任务没了,就返回null
	 * @return
	 */
	public synchronized BOSSUrlVO getVO() {
		// 如果待爬取队列空了
		if (urlList.size() == 0) {
			return null;
		}
		BOSSUrlVO vo = urlList.get(0);
		urlList.remove(0);
		return vo;
	}

	public void buildWorker() {
		new WorkerThread().start();
	}

	/**
	 * 建立爬虫类
	 * @author admin
	 *
	 */
	class WorkerThread extends Thread {
		private BOSSUrlVO vo;
		private ChromeDriver driver;
		
		// 构造方法
		public WorkerThread() {
			// 初始化浏览器驱动
			driver = new ChromeDriver();
		}

		/**
		 * 核心工作方法
		 *    不断获取待爬取队列的vo对象,爬取vo对象的url
		 */
		@Override
		public void run() {
			synchronized (runningCrawler) {
				runningCrawler++;
			}
			// 永真循环
			while (true) {
				synchronized (runningCrawler) {
					vo = getVO();
					// 如果队列空了
					if (vo == null) {
						// 当前工作爬虫-1
						runningCrawler--;
						// 如果这是最后一个死掉的爬虫,唤醒main线程
						synchronized (mainThread) {
							if (runningCrawler == 0) {
								System.out.println("最后一只爬虫休眠");
								System.out.println("即将唤醒main线程");
								mainThread.notify();
							}
						}
						// 退出run
						return;
					}
				}
				// 如果得到了分配的任务
				try {
					work();
				} catch (Exception e) {
					// 如果爬虫一次工作出现异常
					System.out.println(Thread.currentThread().getName()+":-------------爬虫工作异常---------------");
				}
			}
		}

		/**
		 * 线程的工作方法
		 * 	    爬取url对应的
		 */
		private void work(){
			
			try {
				String province = vo.getProvince();
				String city = vo.getCity();
				String url = vo.getUrl();
				System.out.println("url:" + url);
				WebDriverWait wait = new WebDriverWait(driver, 8);
				// 打开网页
				driver.get(url);
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
						// 打印一个单元数据测试
						System.out.printf("title:%s\t%s\t%s\t%s\t%s\t%s", title, city, salary, company, experience,
								education+"\r\n");
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
			} catch(Exception e){
				System.out.println("url:"+vo.getUrl()+"error,没有正常爬取完毕");
				try {
					Thread.sleep(10*1000);
				} catch (InterruptedException e1) {
					System.out.println("sleep失败");
				}
				System.out.println("尽快输入验证码");
			}
		}
	}

}
