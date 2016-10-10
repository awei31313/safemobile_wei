package com.example.mobilesafe1.utils;

/**
 * 从下载文件的url中获取下载文件名字的工具类
 * @author Administrator
 *
 */
public class downLoadFileInfoUtils {

	/**
	 * 获取下载文件的名字和后缀
	 * @param url
	 * @return
	 */
	public static String getDownLoadFile(String url) {
		String file = url.substring(url.lastIndexOf("/") + 1);
//		System.out.println(file);
		return file;

	}

	/**
	 * 只获取下载文件的名字
	 * @param url
	 * @return
	 */
	public static String getDownLoadFileName(String url) {
		String fileName = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
//		System.out.println(fileName);
		return fileName;

	}

}
