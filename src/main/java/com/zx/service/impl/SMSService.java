package com.zx.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.zx.util.SignUtil;

/**
 *短信验证码service
 */
@Service("sMSService")
public class SMSService {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String APP_KEY = "4a94608909bce324e2f5000ce18debee";
	
	private static final String APP_SECRET = "9ad1c7fa7226";
	
	private static final String URL = "https://api.netease.im/sms/sendcode.action";//请求路径
	
	private static final String NONCE = "zhengxing";//临时随机数（128字符以内）
	
	
	public static void main(String[] args) {
		SMSService service = new SMSService();
		String result = service.sendSMS("17826824998");
		System.out.println(result);
	}
	
	
	/**
	 * 调用 网易云信 的短信接口
	 */
	public String sendSMS(String mobile){
		String result = null;
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {
			//创建httpclient对象
			httpClient = HttpClients.createDefault();
			//创建post请求方式的对象
			HttpPost httpPost = new HttpPost(SMSService.URL);
			
			//设置参数
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("mobile", mobile));//目标手机号
			//设置参数到请求对象中
			httpPost.setEntity(new UrlEncodedFormEntity(nvps,"UTF-8"));
			
			//设置header信息
			String curTime = String.valueOf(System.currentTimeMillis());
			httpPost.setHeader("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
			httpPost.setHeader("AppKey",SMSService.APP_KEY);
			httpPost.setHeader("Nonce",SMSService.NONCE);
			httpPost.setHeader("CurTime",curTime);
			httpPost.setHeader("CheckSum",SignUtil.getSHA1(SMSService.APP_SECRET,SMSService.NONCE,curTime));
			
			//执行请求操作，并拿到结果（同步阻塞）
			response = httpClient.execute(httpPost);
			
			//取出结果实体
			HttpEntity entity = response.getEntity();
			if(entity != null) {
				//按指定编码转换结果实体为String类型
				result = EntityUtils.toString(entity,"UTF-8");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			//关闭连接
			try {
				response.close();
				httpClient.close();
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
		return result;
	}
}
