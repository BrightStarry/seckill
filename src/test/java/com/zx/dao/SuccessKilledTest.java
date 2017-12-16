package com.zx.dao;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zx.domain.Seckill;
import com.zx.domain.SuccessKilled;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class SuccessKilledTest {
	@Autowired
	private SuccessKilledDao successKilledDao;
	
	@Test
	public void insertSuccessKilled(){
		int i = successKilledDao.insertSuccessKilled(1000L, 17826824998L,(short)1,new Date());
		System.out.println(i);
	}
	
	
	@Test
	public void queryByIdWithSeckill(){
		SuccessKilled s = successKilledDao.queryByIdWithSeckill(1000L, 17826824998L);
		Seckill seckill = s.getSeckill();
		System.out.println(s);
		System.out.println(seckill);
		
	}
}
