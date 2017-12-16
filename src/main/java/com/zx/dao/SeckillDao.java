package com.zx.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.zx.domain.Seckill;
@Repository("seckillDao")
public interface SeckillDao {
	/**减少库存
	 * 如果影响行数>1，表示更新的记录行数
	 * */
	int reduceNumber(@Param("seckillId")long seckillId,@Param("killTime")Date killTime);
	
	/**根据id查询秒杀对象*/
	Seckill queryById(long seckillId);
	
	/**根据偏移量查询秒杀商品列表*/
	List<Seckill> queryAll(@Param("offet")int offet,@Param("limit")int limit);
	
	/**使用存储过程执行秒杀*/
	void killByProcedure(Map<String, Object> paramMap);
}
