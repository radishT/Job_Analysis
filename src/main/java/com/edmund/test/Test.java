package com.edmund.test;

import java.io.IOException;

import com.edmund.utils.DBUtils;

public class Test {
	public static void main(String[] args) throws IOException {
		DBUtils.readFromFile("emp.txt");

	}
}
