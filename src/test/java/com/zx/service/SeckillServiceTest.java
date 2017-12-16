package com.zx.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zx.domain.Seckill;
import com.zx.dto.Exposer;
import com.zx.dto.SeckillExecution;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml","classpath:spring/spring-service.xml"})
public class SeckillServiceTest {
	//日志输出
	private final Logger logger = LoggerFactory.getLogger(this.getClass()); 
	
	@Autowired
	private SeckillService seckillService;
	
	//获取秒杀列表方法
	@Test
	public void testGetSeckillList(){
		List<Seckill> list = seckillService.getSeckillList();
//		for (Seckill seckill : list) {
//			System.out.println(seckill);
//		}
		logger.info("list={}",list);
	}
	
	//根据id查询秒杀商品详情
	@Test
	public void testGetById(){
		Seckill seckill = seckillService.getById(1000);
		System.out.println(seckill);
		logger.info("seckill={}",seckill);
	}
	
	//暴露秒杀地址
	@Test
	public void testExportSeckillUrl(){
		Exposer exposer = seckillService.exportSeckillUrl(1000);
		System.out.println(exposer);
	}
	
	//执行秒杀方法
	@Test
	public void testExecuteSeckill(){
		SeckillExecution seckillExecution = seckillService.executeSeckill(1000L, 17764572704L, "2ae272526febcf09ddc358359d4a0429");
		System.out.println(seckillExecution);
	}
}
