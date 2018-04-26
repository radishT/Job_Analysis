package com.edmund.crawler;

import com.edmund.utils.DataBaseConnection;
import com.edmund.utils.LGCleanUtils;

/**
 * 拉钩数据表的清洗类
 * @author Edmund
 *
 */
public class LGJobCleaner {
	private DataBaseConnection dbc = new DataBaseConnection();
	private LGCleanUtils utils = new LGCleanUtils(dbc);

	private static final int LAGOU = 0;
	private static final int BOSS = 1;

	public static void main(String[] args) {
		new LGJobCleaner().clean();
	}

	private void clean() {
		utils.JobClean(LAGOU);
		dbc.close();
	}
}
