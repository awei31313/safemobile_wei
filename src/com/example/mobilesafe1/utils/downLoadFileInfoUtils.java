package com.example.mobilesafe1.utils;

public class downLoadFileInfoUtils {

	public static String getDownLoadFile(String url) {

		String file = url.substring(url.lastIndexOf("/") + 1);
//		System.out.println(file);
		return file;

	}

	public static String getDownLoadFileName(String url) {

		String fileName = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
//		System.out.println(fileName);
		return fileName;

	}

}
