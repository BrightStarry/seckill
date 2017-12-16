package com.zx.controller;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zx.domain.Seckill;
import com.zx.dto.Exposer;
import com.zx.dto.SeckillExecution;
import com.zx.dto.SeckillResult;
import com.zx.enumerare.SeckillStatEnum;
import com.zx.exception.RepeatKillException;
import com.zx.exception.SeckillCloseException;
import com.zx.service.SeckillService;

@Controller
@RequestMapping(value="/")
public class SeckillController {
	//slf4j 日志
	Logger logger = LoggerFactory.getLogger(this.getClass());
	//秒杀服务类
	@Autowired
	private SeckillService seckillService;
	
	//进入秒杀商品列表页面
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public String list(Model model){
		List<Seckill> list = seckillService.getSeckillList();
		model.addAttribute("list", list);
		return "list";
	}
	
	//进入某秒杀商品详情页面
	@RequestMapping(value="/{seckillId}/detail",method=RequestMethod.GET)
	public String detail(@PathVariable("seckillId")Long seckillId,Model model){
		//如果id为空，重定向到秒杀列表页面
		if(seckillId == null){
			return "redirect:/seckill/list";
		}
		//根据ID获取seckill
		Seckill seckill = seckillService.getById(seckillId);
		//如果为空同样返回列表页面
		if(seckill == null){
			return "forward:/seckill/list";
		}
		model.addAttribute("seckill",seckill);
		return "detail";
	}
	
	//暴露秒杀地址  AJAX JSON
	@RequestMapping(value="/{seckillId}/exposer",
			method=RequestMethod.POST,
			produces={"application/json;charset=UTF-8"})
	@ResponseBody
	public SeckillResult<Exposer> exposer(@PathVariable("seckillId")Long seckillId,Model model){
		SeckillResult<Exposer> result;
		try {
			//如果未发生异常，返回true以及data
			Exposer exposer = seckillService.exportSeckillUrl(seckillId);
			result = new SeckillResult<Exposer>(true, exposer);
		} catch (Exception e) {
			//发生异常，记录日志，返回false以及错误信息
			logger.error(e.getMessage(),e);
			result = new SeckillResult<Exposer>(false, e.getMessage());
		}
		return result;
	}
	
	//执行秒杀
	@RequestMapping(value="/{seckillId}/{md5}/execution",
			method=RequestMethod.POST,
			produces={"application/json;charset=UTF-8"})
	@ResponseBody
	public SeckillResult<SeckillExecution> execute(
			@PathVariable("seckillId")Long seckillId,
			@PathVariable("md5")String md5,
			@CookieValue(value="userPhone",required=false) Long userPhone){
		//如果userPhone为空
		if(userPhone == null){
			return new SeckillResult<SeckillExecution>(false, "未登录");
		}
		try {
			//如果成功，
//			SeckillExecution execution = seckillService.executeSeckill(seckillId, userPhone, md5);
			SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, userPhone, md5);
			
			return new SeckillResult<SeckillExecution>(true,execution);
		} catch (SeckillCloseException e) {
			//秒杀结束错误
			SeckillExecution execution = new SeckillExecution(seckillId,SeckillStatEnum.END);
			return new SeckillResult<SeckillExecution>(false, execution);
		} catch (RepeatKillException e) {
			//重复秒杀错误
			SeckillExecution execution = new SeckillExecution(seckillId,SeckillStatEnum.REPEAT_KILL);
			return new SeckillResult<SeckillExecution>(false, execution);
		} catch (Exception e) {
			//内部错误
			SeckillExecution execution = new SeckillExecution(seckillId,SeckillStatEnum.INNER_ERROR);
			return new SeckillResult<SeckillExecution>(false, execution);
		}
	}
	
	
	//获取系统时间
	@RequestMapping(value="/time/now",
			method=RequestMethod.GET,
			produces={"application/json;charset=UTF-8"})
	@ResponseBody
	public SeckillResult<Long> time(){
		return new SeckillResult<Long>(true, System.currentTimeMillis());
	}
	
	
	
}
