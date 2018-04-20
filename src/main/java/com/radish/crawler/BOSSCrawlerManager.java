package com.radish.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.radish.vo.BOSSUrlVO;

/**
 * 单例模式实现管理爬取队列
 * @author admin
 *
 */
public class BOSSCrawlerManager {
	// 初始化爬取队列,每个VO中的url都是可直接访问的
	private List<BOSSUrlVO> urlList = new ArrayList<BOSSUrlVO>();
	// 要爬取的关键词 java python C# C++ linux
	private String[] keys = new String[] { "java", "python", "C%23", "C%2B%2B", "linux" };
	// 爬虫间争抢runningCrawler的同步对象
	private Object obj = new Object();
	// 所有爬虫爬完后,通过obj通知main线程
	public Object mainThread = new Object();
	// 当前仍然在工作的爬虫数
	public Integer runningCrawler = 0;

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

		// 构造方法
		public WorkerThread() {

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
							if (runningCrawler == 0)
								System.out.println("最后一只爬虫休眠");
							System.out.println("即将唤醒main线程");
							mainThread.notify();
						}
						// 退出run
						return;
					}
				}
				// 如果得到了分配的任务
				work();
			}
		}

		/**
		 * 线程的工作方法
		 */
		private void work() {
			System.out.println(Thread.currentThread().getName() + "处理了" + vo.toString());
		}
	}

}
