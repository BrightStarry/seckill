package com.zx.dao;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zx.domain.Seckill;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class SeckillDaoTest {
	@Autowired
	private SeckillDao SeckillDao;
	
	@Test
	public void testReduceNumber(){
		int a = SeckillDao.reduceNumber(1000, new Date());
		System.out.println(a);
		
	}
	
	@Test
	public void testQueryById(){
		Seckill seckill = SeckillDao.queryById(1000);
		System.out.println(seckill);
	}
	
	@Test
	public void testQueryAll(){
		List<Seckill> list = SeckillDao.queryAll(0, 5);
		for (Seckill seckill : list) {
			System.out.println(seckill);
		}
	}
}
