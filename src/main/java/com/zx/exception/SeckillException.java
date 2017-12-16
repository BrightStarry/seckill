package com.zx.exception;
/**
 *秒杀业务相关异常 
 *
 */
@SuppressWarnings("serial")
public class SeckillException extends RuntimeException{

	public SeckillException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public SeckillException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	
}
