package com.zx.util;

import java.security.MessageDigest;


/**
 * 加密解密工具类
 */
public class SignUtil {
	/**获取sha1加密后的值*/
	public static String getSHA1(String appSecret,String nonce,String curTime ){
		return encode("sha1", appSecret + nonce + curTime);
		
	}
	
	/**获取md5加密后的值*/
	public static String getMD5(String requestBody){
		return encode("md5", requestBody);
	}
	
	/**编码方法 ：使用java自带的MessageDigest*/
	private static String encode(String algorithm,String value){
		if(value == null){
			return null;
		}
		try {
			//根据算法创建对应的消息摘要实例
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			messageDigest.update(value.getBytes());
			//获取格式化后的字符串（16进制，小写）
			return getFormattedText(messageDigest.digest());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**把字符格式化成16进制，小写*/
	private static final char[] HEX ={'0','1','2','3','4','5','6','7','8','9',
			'a','b','c','d','e','f'};
	public static String getFormattedText(byte...bytes){
		int leng = bytes.length;
		StringBuilder buf = new StringBuilder(leng * 2);
		for (int i = 0; i < leng; i++) {
			buf.append(HEX[bytes[i] >> 4 & 0x0f]);
			buf.append(HEX[bytes[i] & 0x0f]);
		}
		return buf.toString();
	}
}
