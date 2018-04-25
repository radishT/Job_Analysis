package com.random.crawler;

public class Test {

	public static void main(String[] args) {

		// 创建核心类
		TaskManager manager = new TaskManager();
		// File file = new File("C:/Users/admin/Desktop/job_data.txt");
		// 初始化文件队列
		// List<File> fileList = new ArrayList<File>();
		// fileList.add(file);
		// manager初始化数据库
		// manager.initData(fileList);
		// manager启动爬虫去根据url 爬取 message
		manager.startCrawler();
		/*
		 * 爬虫根据url填写完message以后 根据massage填写一个message对应的关键字map 注意:
		 * key是message中出现过的英文单词 value是key在message中出现过的次数
		 * 将map存入到数据库的message_map(Blob)域中
		 */
		// manager.pickMapFromMessage();
		// manager.readMap();
		/*
		 * 根据所有key---map 数据表job_message中的每一条记录代表一个就业要求对应的map能力集 整合各个key的所有map 1
		 * key:n map ----->1key:1map 得出5条: key map
		 */
		// manager.combineMaps();

	}

}
