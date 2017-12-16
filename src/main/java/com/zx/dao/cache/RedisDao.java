package com.zx.dao.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.zx.domain.Seckill;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
/**redis缓存数据库操作类*/
public class RedisDao {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	//连接池
	private JedisPool jedisPool;
	private RedisDao(String ip,int port){
		jedisPool = new JedisPool(ip,port);
	}
	
	//protostuff 序列化 对象
	private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);
	//获取Seckil的方法
	public Seckill getSeckill(long seckillId){
		try {
			//从连接池获取连接
			Jedis jedis = jedisPool.getResource();
			try {
				String key = "seckill:" + seckillId;
				//并没有实现内部序列化操作
				//get->byte[]->反序列化 -> Object(Seckill)
				//采用自定义序列化 protostuff ：pojo
				byte[] bytes = jedis.get(key.getBytes());//通过字节型的key获取value
				if(bytes != null){
					//空对象
					Seckill seckill =schema.newMessage();
					//如果取到了key对应的value
					//用protostuff的工具类反序列化取出Object 需要三个参数 ： 字节数组，空对象，运行模式（RunTimeSchema）
					ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
					return seckill;
				}
			} finally {
				jedis.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}
	
	//存入Seckill方法
	public String putSeckill(Seckill seckill){
		//set Object(Seckill) --> 序列化--> byte[] 
		try {
			Jedis jedis = jedisPool.getResource();
			String result;
			try {
				String key = "seckill:" + seckill.getSeckillId();
				//将对象序列化,需要三个参数: 需要序列化的对象， 运行模式，缓存器
				byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
				//缓存超时时间
				int timeOut = 60*60;//1小时
				result = jedis.setex(key.getBytes(), timeOut, bytes);
			}finally{
				jedis.close();
			}
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}
}
