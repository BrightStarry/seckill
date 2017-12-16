--数据库初始化脚本

--创建数据库
CREATE DATABASE seckill;
--使用数据库
USE seckill;
--创建秒杀库存表
CREATE TABLE seckill(	
	seckill_id bigint NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
	name varchar(120) NOT NULL COMMENT '商品名称',
	number int NOT NULL COMMENT '库存数量',
	start_time timestamp NOT NULL COMMENT '秒杀开始时间',
	end_time timestamp NOT NULL COMMENT '秒杀结束时间',
	create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	PRIMARY KEY (seckill_id),
	key idx_start_time(start_time),
	key idx_end_time(end_time),
	key idx_create_time(create_time)
)ENGINE = InnoDB  AUTO_INCREMENT=1000  COMMENT="秒杀库存表";

--timestamp类型的数据只能有一个default或者update列，把有default值的create_time字段放在前面问题解决。


--初始化数据
insert into 
	seckill(name,number,start_time,end_time)
values
	('1000元秒杀iphone6','100','2016-11-20 00:00:00','2016-11-30 00:00:00'),
	('666元秒杀小米3','200','2016-11-20 00:00:00','2016-11-30 00:00:00'),
	('200元秒杀红米note','300','2016-11-20 00:00:00','2016-11-30 00:00:00'),
	('500元秒杀ipad2','400','2016-11-20 00:00:00','2016-11-30 00:00:00');

--秒杀成功明细表
--用户登陆认证相关的信息
CREATE TABLE success_killed(
	seckill_id bigint NOT NULL COMMENT '秒杀商品id',
	user_phone bigint NOT NULL COMMENT '用户手机号',
	state tinyint NOT NULL DEFAULT -1 COMMENT '状态标识：-1:无效    1:成功  2:已付款 3:已发货',
	create_time timestamp NOT NULL  COMMENT  '创建时间',
	PRIMARY KEY(seckill_id,user_phone),/*联合主键*/
	key idx_create_time(create_time)
)ENGINE = InnoDB   COMMENT="秒杀成功明细表";	

--连接数据库认证
mysql -uroot -p  /*在cmd中输入*/