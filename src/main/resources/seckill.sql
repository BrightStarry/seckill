--秒杀执行存储过程
DELIMITER $$ -- ; 转换成 $$ 
--定义存储过程
--参数：in输入参数 ，out输出参数
--row_count()返回上一条修改类型sql影响的行数 
--存储过程中间不能写注释，如果在navicat中执行，不需要替换 ;成$$
CREATE PROCEDURE 'seckill'.'execute_seckill'
	(in v_seckill_id bigint,in v_phone bigint,
		in v_kill_time timestamp,out r_result int)
	BEGIN		
		DECLARE insert_count int DEFAULT 0;
		START TRANSACTION;
		insert ignore into success_killed
			(seckill_id,user_phone,state,create_time)
			value(v_seckill_id,v_phone,1,v_kill_time);
		select row_count() into insert_count;
		if(insert_count =0) THEN
			ROLLBACK;
			SET r_result = -1;
		ELSEIF(insert_count <0)THEN
			ROLLBACK;
			SET r_result = -2;
		ELSE
			update seckill
			set number = number -1
			where seckill_id = v_seckill_id
				and end_time > v_kill_time
				and start_time < v_kill_time
				and number > 0;
			select	row_count() into insert_count;
			if(insert_count =0)THEN
				ROLLBACK;
				set r_result = 0;
			ELSEIF(insert_count < 0)THEN
				ROLLBACK;
				SET r_result = -2;
			ELSE
				COMMIT;
				SET r_result = 1;
			END IF;
		END IF;
	END 
$$
--存储过程结束

DELIMITER ; --把结束符号换回来
set @r_result = -3;
call execute_seckill(1000,17826824993,now(),@r_result);

--获取结果
select @r_result;