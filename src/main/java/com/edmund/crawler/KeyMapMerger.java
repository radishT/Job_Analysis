package com.edmund.crawler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.edmund.utils.DataBaseConnection;
import com.edmund.utils.LGDBUtils;
import com.edmund.vo.KeyMap;

/**
 * 关键字map的合并类
 * @author Edmund
 *
 */
public class KeyMapMerger {

	private DataBaseConnection dbc = new DataBaseConnection();
	private LGDBUtils utils = new LGDBUtils(dbc);
	private static String[] keys = { "web", "java", "python", "c++", "c#",
			"android", "linux" };

	public static void main(String[] args) {
		new KeyMapMerger().merge();
	}

	/**
	 * 合并数据库中所有条目的Map集合,整合为一个Map集合,并输出到本地文件系统中
	 */
	private void merge() {
		for (String keyword : keys) {
			Map<String, Integer> kwMerge = new HashMap<String, Integer>();
			// List<LGJob> jobs = utils.getLGJob(keyword);
			// for (LGJob job : jobs) {
			List<KeyMap> kms = utils.getKeyMap(keyword);
			for (KeyMap km : kms) {
				// Map<String, Integer> kwMap = job.getKeywords();
				Map<String, Integer> kwMap = km.getKeywords();
				Set<String> keyset = kwMap.keySet();
				for (String key : keyset) {
					if (kwMerge.containsKey(key)) {
						kwMerge.put(key, kwMerge.get(key) + kwMap.get(key));
					} else {
						if (key.contains("/")) {
							String[] keys = key.split("/");
							for (String inner_key : keys) {
								if (kwMerge.containsKey(inner_key)) {
									kwMerge.put(inner_key,
											kwMerge.get(inner_key)
													+ kwMap.get(key));
								} else {
									kwMerge.put(inner_key, kwMap.get(key));
								}
							}
						} else {
							kwMerge.put(key, kwMap.get(key));
						}

					}
				}
			}
			utils.writeKeyMapToMysql(kwMerge, keyword);
		}
		dbc.close();
	}
}
