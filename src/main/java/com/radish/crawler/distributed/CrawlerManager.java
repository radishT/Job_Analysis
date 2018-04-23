package com.radish.crawler.distributed;

import java.sql.Connection;

/**
 * 实现分布式爬虫
 * 
 * 任务:
 * 1.从数据库表:url_list获取一条url,
 * 
 * 2.将爬到的数据存入到表logou
 * 
 * @author admin
 *
 */
public class CrawlerManager {
	private Connection conn;
	
	
	
	// 空构造
	public CrawlerManager() {
		
	}




	public void initConn(){
		
	} 
}
