package com.radish.crawler;

import org.apache.hadoop.classification.InterfaceAudience.Public;

import com.radish.crawler.BOSSCrawlerManager.WorkerThread;

public class Test {
	public static void main(String[] args) {
		System.setProperty("webdriver.chrome.driver", "D:/chrome_driver/chromedriver.exe");
		// 获得单例句柄
		BOSSCrawlerManager instance = BOSSCrawlerManager.getInstance();
		// 初始化单例对象
		instance.init("C:/Users/admin/Desktop/BossUrl.txt");
		System.out.println("urlList初始化完毕!--------------------");
		instance.buildWorker();
		//instance.buildWorker();
		//instance.buildWorker();
		System.out.println("3只爬虫启动成功");
		
		// 启动3个线程后,自己睡眠等待最后一只休眠的爬虫唤醒自己
		Object mainThread = instance.mainThread;
		synchronized (mainThread) {
			try {
				System.out.println("main线程睡眠");
				mainThread.wait();
				System.out.println("main线程被唤醒");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// 被唤醒后 main方法负责
	}
}
