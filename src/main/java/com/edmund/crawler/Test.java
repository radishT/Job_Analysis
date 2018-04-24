package com.edmund.crawler;

import java.io.IOException;
import java.util.List;

import com.edmund.utils.DataBaseConnection;
import com.edmund.utils.LGDBUtils;
import com.edmund.vo.LGJob;

public class Test {

	private DataBaseConnection dbc = new DataBaseConnection();
	private LGDBUtils utils = new LGDBUtils(dbc);
	private static String[] keys = { "web", "java", "python", "c++", "c#",
			"android" };
	private static String root = "https://www.lagou.com/jobs/list_%KW%?px=default&city=%CT%#filterBox";

	public static void main(String[] args) throws IOException {
		// new Test().initURLList();
		Test test = new Test();
		String line = null;
		List<LGJob> jobs = test.read();
		for (LGJob lgJob : jobs) {
			System.out.println(lgJob.getKeywords());
		}
	}

	private List<LGJob> read() {
		return utils.getLGJob();
	}
}
