package com.edmund.crawler;

import java.io.IOException;
import java.util.List;

import com.edmund.utils.DataBaseConnection;
import com.edmund.utils.LGDBUtils;
import com.edmund.vo.LGJob;

/**
 * 用于根据关键字和城市来生成所有需要处理的url,并存入city_url表中
 * @author Edmund
 *
 */
public class LGJobUrlGenerator {

	private DataBaseConnection dbc = new DataBaseConnection();
	private LGDBUtils utils = new LGDBUtils(dbc);
	private static String[] keys = { "web", "java", "python", "c++", "c#",
			"android", "linux" };
	private static String root = "https://www.lagou.com/jobs/list_%KW%?px=default&city=%CT%#filterBox";

	public static void main(String[] args) throws IOException {
		new LGJobUrlGenerator().initURLList();
		// Test test = new Test();
		// String line = null;
		// List<LGJob> jobs = test.read();
		// for (LGJob lgJob : jobs) {
		// System.out.println(lgJob.getKeywords());
		// }
	}

	private void initURLList() throws IOException {
		List<String> cities = utils
				.readFromFile("C:/Users/admin/Desktop/files/lagou.txt");
		for (String key : keys) {
			for (String city : cities) {
				String url = root.replace("%KW%", key).replace("%CT%", city);
				utils.writeIntoCityURL(url);
			}
		}
	}

	private List<LGJob> read() {
		return utils.getLGJob("web");
	}
}
