package com.hegongshan.notepad.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public final class FileUtils {
	public static String readFile(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String str;
		StringBuilder sb = new StringBuilder();
		while((str = br.readLine()) != null) {
			sb.append(str+"\n");
		}
		br.close();
		return sb.toString();
	}
	
	public static void writeFile(File file,String content) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		bw.write(content);
		bw.flush();
		bw.close();
	}
}
