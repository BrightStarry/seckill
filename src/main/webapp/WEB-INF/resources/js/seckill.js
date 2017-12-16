//存放主要交互逻辑的js代码
//javascript 模块化
//seckill.detail.init(params);
var seckill = {
		// 封装秒杀相关的ajax的url
		URL:{
			//返回获取当前时间的URL
			now : function(){
				return '/seckill/time/now';
			},
			//返回暴露地址的URL
			exposer : function(seckillId){
				return '/seckill/' + seckillId + '/exposer';
			},
			//返回秒杀地址的URL
			execution : function(seckillId,md5){
				return '/seckill/' + seckillId + '/' + md5 + '/execution';
			}
			
		},
		
		//获取秒杀地址，控制显示逻辑，执行秒杀
		handleSeckillKill :function(seckillId,node){
			node.hide()//先隐藏一下比较美观
				.html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>')//添加按钮
			$.post(seckill.URL.exposer(seckillId),{},function(result){
				//在回调函数中，执行交互流程
				if(result && result['success']){
					var exposer = result['data'];
					if(exposer['exposed']){
						//如果开启了秒杀
						var md5 = exposer['md5'];
						var killUrl = seckill.URL.execution(seckillId,md5);
						//绑定  一次  点击 的事件
						$('#killBtn').one('click',function(){
							//执行秒杀请求的操作
							//1.先禁用按钮，防止重复秒杀
							$(this).addClass('sidabled');
							//2.发送AJAX请求
							$.post(killUrl,{},function(result){
//								if(result && result['success']){
									var killResult = result['data'];//返回的秒杀信息对象
									var state = killResult['state'];//秒杀状态
									var stateInfo = killResult['stateInfo'];//秒杀状态描述
									//3.显示秒杀结果
									node.html('<span class="label label-success">' + stateInfo + '</span>');
//								}
							});
						});
						node.show(500);
					}else{
						//没有开启秒杀,也就是执行时出现偏差，在执行一次计时方法
						var now = exposer['now'];
						var start = exposer['start'];
						var end = exposer['end'];
						seckill.countdown(seckillId,now,start,end);
					}
				}else{
					console.log('exposer result :' + result);
				}
			});
		},
		
		// 验证手机号
		validatePhone : function(userPhone){
			// 如果userPhone不为空，且长度=11，且是否是数字(isNaN方法如果是非数字返回true，所以取反)
			if(userPhone && userPhone.length == 11 && !isNaN(userPhone)){
				return true;
			}else{
				return false;
			}
		},
		
		
		//计时
		countdown:function(seckillId,nowTime,startTime,endTime){
			var seckillBox = $('#seckill-box');
			//时间判断
			if(nowTime > endTime){
				//秒杀结束
				seckillBox.text('秒杀结束!');
			}else if(nowTime < startTime){
				//秒杀未开始,计时时间绑定
				var killTime = new Date(startTime + 1000)
				seckillBox.countdown(killTime,function(event){
					//时间格式
					var format = event.strftime('秒杀倒计时: %D天 %H时 %M分 %S秒');
					seckillBox.html(format);
				}).on('finish.countdown',function(){//倒计时结束的回调函数
					
					seckill.handleSeckillKill(seckillId,seckillBox);
				});
				
			}else{
				//秒杀开始
				seckill.handleSeckillKill(seckillId,seckillBox);
			}
		},
		
		// 详情页秒杀逻辑
		detail:{
			
			// 详情页初始化
			init : function(params){
				// 手机验证和登录，计时交互
				// 在cookie中查找手机号
				var userPhone = $.cookie('userPhone');// 手机号
				var seckillId = params['seckillId'];// 秒杀商品id
				var startTime = params['startTime'];// 开始时间:毫秒
				var endTime = params['endTime'];// 结束时间
				// 验证手机号
				if(!seckill.validatePhone(userPhone)){
					// 如果验证失败
					// 弹出
					var　killPhoneModal = $('#killPhoneModal');
					//稍微居中显示
					//killPhoneModal.css({'margin': '200px auto'});
					killPhoneModal.modal({
						// 弹出窗口后，如果验证没有通过，不允许关闭该窗口
						show:true,// 显示弹出层
						backdrop:'static',// 禁止位置关闭
						keyboard:false// 关闭键盘事件
					});
					//点击弹出层的submit按钮后的事件
					$('#killPhoneBtn').click(function(){
						var inputPhone = $('#killPhoneKey').val();
						//再次验证
						if(seckill.validatePhone(inputPhone)){
							//将Phone写入cookie,有效时间为7天，在/seckill路径下
							$.cookie('userPhone',inputPhone,{expires:7,path:'/seckill'});
							//刷新页面
							window.location.reload();
						}else{
							$('#killPhoneMessage').hide().html('<label class="label label-danger">手机号码错误!</label>').show(500);
						}
					});
				}
				//手机号验证通过，已经登录
				//计时交互
				//获取系统当前时间
				$.get(seckill.URL.now(),{},function(result){
					if(result && result['success']){
						var nowTime = result['data'];
						//时间判断
						seckill.countdown(seckillId,nowTime,startTime,endTime);
					}else{
						//如果获取失败,在浏览其 的控制台输出
						console.log('now time result:' + result);
					}
					
				});
			}
		}
}