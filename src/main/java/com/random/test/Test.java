package com.random.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class Test {

	public static void main(String[] args) throws Exception {
		FileOutputStream fOut = new FileOutputStream(new File(""), true);
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(fOut));
		writer.println("");
		writer.flush();
		writer.println("");
		writer.flush();
		writer.println("");
		writer.flush();
		writer.println("");
		writer.flush();
		writer.println("city\t");
		StringBuilder builder = new StringBuilder();
		builder.append("aaaa\tsadasd\r\n");
		writer.flush();
		writer.println("");
		writer.flush();
		writer.println("");
		writer.flush();
		
		
		
		writer.close();
	}

}
