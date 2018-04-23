package com.radish.HDFSUtil;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HDFSTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Configuration configuration = new Configuration();
		Path path = new Path("hdfs://192.168.199.233:9000/input/H2.txt");
		try {
			FileSystem fs = path.getFileSystem(configuration);
			FSDataOutputStream os = fs.create(path);
			os.writeUTF("Ni  Hao ~");
			os.close();
		} catch (Exception e) {
			System.out.println("catch a exception");
		}
	}
}
