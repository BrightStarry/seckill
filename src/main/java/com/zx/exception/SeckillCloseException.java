package com.zx.exception;
/**秒杀关闭异常*/
@SuppressWarnings("serial")
public class SeckillCloseException extends SeckillException{

	public SeckillCloseException(String message, Throwable cause) {
		super(message, cause);
	}

	public SeckillCloseException(String message) {
		super(message);
	}
	
	
}
