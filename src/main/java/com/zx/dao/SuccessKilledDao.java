package com.zx.dao;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.zx.domain.SuccessKilled;

/**成功秒杀接口*/
public interface SuccessKilledDao {
	/**添加秒杀记录
	 * 插入的行数
	 * */
	int insertSuccessKilled(@Param("seckillId")long seckillId,@Param("userPhone")long userPhone,@Param("state")short state,@Param("createTime")Date createTime);
	
	/**查询秒杀记录，根据id和userPhone，并且携带秒杀对象实体*/
	SuccessKilled queryByIdWithSeckill(@Param("seckillId")long seckillId,@Param("userPhone")long userPhone); 
	
	
}
