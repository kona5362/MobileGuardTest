package com.test.mobileguardtest.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {

	/**
	 * 流转字符串
	 * @param is 流对象
	 * @return 转后的字符串
	 */
	public static String stream2String(InputStream is) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int length = -1;
		try {
			while((length = is.read(buf))!=-1){
				bos.write(buf,0,length);
			}
			return bos.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		
	}
	
}
