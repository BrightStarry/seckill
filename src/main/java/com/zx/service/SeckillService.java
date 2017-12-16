package com.zx.service;

import java.util.List;

import com.zx.domain.Seckill;
import com.zx.dto.Exposer;
import com.zx.dto.SeckillExecution;
import com.zx.exception.RepeatKillException;
import com.zx.exception.SeckillCloseException;
import com.zx.exception.SeckillException;

/**
 *业务接口
 *
 */
public interface SeckillService {
	/**
	 * 查询所有秒杀记录
	 */
	List<Seckill> getSeckillList();
	
	/**
	 * 查询单个秒杀记录
	 */
	Seckill getById(long seckillId);
	
	/**
	 * 秒杀开启时输出秒杀接口的地址，
	 * 否则输出系统时间和秒杀时间
	 */
	Exposer exportSeckillUrl(long seckillId);
	
	/**
	 * 执行秒杀操作
	 * 既然后面两个异常继承第一个异常，为什么要写三个：
	 * 后面两个异常如果被抛出，是明确的错误：秒杀关闭和重复秒杀，
	 * 如果是第一个异常，还可能是其他的秒杀失败异常
	 */
	SeckillExecution executeSeckill(long seckillId,long userPhone,String md5)
		throws SeckillException,SeckillCloseException,RepeatKillException;
	
	/**
	 * 执行秒杀操作  by 存储过程
	 */
	SeckillExecution executeSeckillProcedure(long seckillId,long userPhone,String md5)
		throws SeckillException,SeckillCloseException,RepeatKillException;
}
