package com.zx.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.zx.dao.SeckillDao;
import com.zx.dao.SuccessKilledDao;
import com.zx.dao.cache.RedisDao;
import com.zx.domain.Seckill;
import com.zx.domain.SuccessKilled;
import com.zx.dto.Exposer;
import com.zx.dto.SeckillExecution;
import com.zx.enumerare.SeckillStatEnum;
import com.zx.exception.RepeatKillException;
import com.zx.exception.SeckillCloseException;
import com.zx.exception.SeckillException;
import com.zx.service.SeckillService;
@Service("SeckillService")
public class SeckillServiceImpl implements SeckillService{
	//日志对象
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SeckillDao seckillDao;
	@Autowired
	private SuccessKilledDao successKilledDao;
	@Autowired
	private RedisDao redisDao;
	
	//MD5盐值字符，用于混淆MD5
	private final String salt = "fjuifg&*T*UOh34r9@!@I(#*RHO545U*U(%%*)%#&ghdn";
	
	/**获取秒杀商品列表*/
	@Override
	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 4);
	}
	
	/**根据id获取秒杀商品详情*/
	@Override
	public Seckill getById(long seckillId) {
		return seckillDao.queryById(seckillId);
	}

	/**暴露秒杀地址*/
	@Override
	public Exposer exportSeckillUrl(long seckillId) {
		//使用Redis进行缓存优化
		//先试图从redis中取出
		Seckill seckill= redisDao.getSeckill(seckillId);
		if(seckill == null){
			//如果为空，从数据库中取
			seckill = seckillDao.queryById(seckillId);
			if(seckill == null){
				//如果还为空，抛出异常
				return  new Exposer(false,seckillId);
			}else{
				//否则，将数据存入缓存
				redisDao.putSeckill(seckill);
			}
		}
		
		
//		//先查询是否存在这个秒杀商品
//		Seckill seckill = seckillDao.queryById(seckillId);
//		//如果为空，抛出异常
//		if(seckill == null ){
//			return  new Exposer(false,seckillId);
//		}
		//获取秒杀开始结束时间、当前时间的毫秒数
		long startTime = seckill.getStartTime().getTime();
		long endTime = seckill.getEndTime().getTime();
		long nowTime = new Date().getTime();
		//是否在秒杀活动时间内
		if (startTime > nowTime || endTime < nowTime) {
			return new Exposer(false,seckillId,nowTime,startTime,endTime);
		}
		//MD5加密
		String md5 = getMD5(seckillId);
		return new Exposer(true,md5,seckillId);
	}
	
	/**获取MD5加密字符*/
	private String getMD5(long seckillId){
		String base = seckillId + "/" + salt;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}
	
	
	
	/**执行秒杀操作*/
	@Override
	@Transactional
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, SeckillCloseException, RepeatKillException {
		try {
			//判断秒杀信息MD5是否为空或者被串改
			if(md5 == null || !md5.equals(getMD5(seckillId))){
				throw new SeckillException("秒杀信息为空或被篡改!");
			}
			//执行秒杀逻辑 ： 减库存 +记录秒杀行为
			
			//记录购买行为
			int insertNumber = successKilledDao.insertSuccessKilled(seckillId, userPhone, (short)1,new Date());
			//如果小于等于0，表示重复秒杀
			if(insertNumber <= 0){
				throw new RepeatKillException("重复秒杀!");
			}
			
			//修改库存
			Date nowTime = new Date();
			int reduceNumber = seckillDao.reduceNumber(seckillId, nowTime);
			//如果没有减少库存,秒杀结束
			if(reduceNumber <= 0){
				throw new SeckillCloseException("秒杀结束！");
			}else{
				//秒杀成功
				SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
				return new SeckillExecution(seckillId,SeckillStatEnum.SUCCESS, successKilled);
			}
		}catch(SeckillCloseException e1){
			throw e1;
		}catch(RepeatKillException e2){
			throw e2;
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
			//把编译时异常转换成运行时异常
			throw new SeckillException("seckill inner error" + e.getMessage());
		}
	}

	
	
	/**
	 * 执行秒杀操作 by 存储过程
	 */
	@Override
	public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5){
		if(md5 == null || !md5.equals(getMD5(seckillId))){
			return new SeckillExecution(seckillId, SeckillStatEnum.DATA_REWRITE);
		}
		Date nowTime = new Date();
		Map<String, Object> paramMap = new HashMap<String,Object>();
		paramMap.put("seckillId", seckillId);
		paramMap.put("userPhone", userPhone);
		paramMap.put("nowTime", nowTime);
		paramMap.put("result", null);
		//执行存储过程，返回result
		try {
			seckillDao.killByProcedure(paramMap);
			//获取result commons-collections jar中的map工具类
			Integer result = MapUtils.getInteger(paramMap, "result",-2);
			if(result == 1){
				SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
				return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS,successKilled);
			}else{
				return new SeckillExecution(seckillId, SeckillStatEnum.stateOf(result));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
		}
	} 
}
