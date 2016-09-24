package com.example.mobilesafe1.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {

	public static String streamToString(InputStream is) {

		String result = null;
		ByteArrayOutputStream baos = null;
				
		try {
			baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = -1;
			while((len=is.read(buffer))!=-1){
				baos.write(buffer, 0, len);
				baos.flush();
			}
			result = baos.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

}
