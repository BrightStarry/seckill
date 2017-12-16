package com.zx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/util")
public class UtilController {
	/**
	 * 给指定手机号发送验证码
	 */
//	@RequestMapping("/{userPhone}/sender")
//	public String sender(@PathVariable("userPhone")Long userPhone){
//		if(userPhone == null || userPhone.SIZE != 11){
//			return "手机号码不符合规范！";
//		}
//		
//	}
}
