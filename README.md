2016年11月26日 15:31:48
	周末，跟着慕课网上的视频做个小项目，不过这个项目不错，用到了redis，restfull
之类的很多东西。
	下面说两个小东西，
	1.
	<!-- 添加秒杀记录 -->
  	<insert id="insertSuccessKilled">
  		<!-- 主键冲突，报错
  			使用 ignore ，重复主键后不报错，只是返回0
  		 -->
  		insert ignore into 
  			success_killed(seckill_id,user_phone,state)
  		values
  			(#{seckillId},#{userPhone},#{state})
  	</insert>
  	
  	2.
  	在ssm整合的时候也可以加上mybatis的配置文件
  	<!-- sqlSessionFactory -->
	<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
		<!-- dataSource -->
		<property name="dataSource" ref="dataSource"></property>
		<!-- mapper文件 -->
		<property name="mapperLocations" value="classpath:com/zx/mapper/*.xml"></property>
		<!-- 实体类包名 -->
		<property name="typeAliasesPackage" value="com.zx.domain"></property>
		<!-- MyBatis自身配置文件 -->
		<property name="configLocation" value="classpath:MyBatis.xml"></property>
	</bean>
	然后，在配置文件里面写
	<settings>
		<!-- 使用jdbc的getGeneratedKeys获取数据库自增主键 -->
		<setting name="useGeneratedKeys" value="true"/>
		<!-- 使用列别名替换列名
			select a as b from c
		 -->
		 <setting name="useColumnLabel" value="true"/>
		 <!-- 开启驼峰命名转换 :table(create_time) -- Entity(createTime) -->
		 <setting name="mapUnderscoreToCamelCase" value="true"/>
	</settings>
	很好的东西.
	
	3.
	还有一个小技巧，就是假设一个对象叫successKilled,这个对象里面包含了一个seckill对象，
想要在Mybatis的mapper文件中查询出一个完整的包含了seckill的successKilled对象,
使用驼峰命名方法可以这么写
	<!-- 查询秒杀记录,根据id和userPhone -->
  	<select id="queryByIdWithSecklii"  resultType="successKilled">
  		select 
  			su.seckill_id,
  			su.user_phone,
  			su.state,
  			su.create_time,
  			se.seckill_id "seckill.seckill_id",
  			se.name "seckill.name",
  			se.number "seckill.number",
  			se.start_time "seckill.start_time",
  			se.end_time "seckill.end_time",
  			se.create_time "seckill.create_time"
  		from 
  			success_killed su
   		inner join 
  			seckill se
  		on 
  			su.seckill_id = se.seckill_id
  		where
  			su.seckill_id = #{seckillId} and
  			su.user_phone = #{userPhone}
  	</select>
 ---------------------------------------------------------------------------------
 与数据表相关的对象是实体类，放在domain包中，还可以有一种ENTITY，可以放在dto包中，也就是传输数据对象，
 方便后台向前台传输数据
 ------------------------------------------------------------------------------
 可以写一个Exception包，用来自定义各种异常，
 一般是自己写一个父类Exception继承RumTimeException，然后定义的其他异常对象继承于它
 --------------------------------------------------------------------------------
  //TODO  这个注释表示待办事项，表示之后完成
 -------------------------------------------------------------------------------
 返回数据的时候，不需要的定义好一个变量，可以直接return 的时候new一个，感觉比较省内存
 ---------------------------------------------------------------------
 spring有封装好的MD5加密方法，使用MD5加密一个字符串的时候，可以先定义一个复杂的 盐值 ，然后将待加密的
 字符串 +　“/” +盐值，再进行加密，可以有效的防止解密。
 ---------------------------------------------------------------------------------------
spring3中的controller默认是单例的，若是某个controller中有一个私有的变量a,所有请求到同一个controller时，
使用的a变量是共用的，即若是某个请求中修改了这个变量a，则，在别的请求中能够读到这个修改的内容。
　　若是在@controller之前增加@Scope("prototype")，就可以改变单例模式为多例模式	
-------------------------------------------------------------------------------
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring-dao.xml","classpath:spring-service.xml"})
这么写可以在Junit的时候导入多个spring配置文件。
！！！！还有很重要的一点，那就是，再写spring配置文件的时候，可以分成三个，spring-dao，spring-service，spring-controller
这样比较清晰。
-----------------------------------------------------------------------------------
springMVC访问静态资源的三(四？)种方式
1.在web.xml中配置tomcat的default+servlet来处理，要配置多个，
<servlet-mapping>
    <servlet-name>default</servlet-name>
    <url-pattern>*.jpg</url-pattern>
</servlet-mapping>
<servlet-mapping>
    <servlet-name>default</servlet-name>
    <url-pattern>*.js</url-pattern>
</servlet-mapping>

2.
<!-- 让WEB-INF/resources文件夹可以被访问 -->
<mvc:resources location="/resources/" mapping="/reources/**"></mvc:resources>
	
3.
	<!-- servlet-mapping 映射路径："/" -->
	<!-- 静态资源默认servlet配置
		1.加入对静态资源的处理：js,gif,png
		2.允许使用"/"做整体映射
		PS:(自己加的)
		如果不做这样的映射，用了"/"，默认来说，所有的请求都会被当作对controller的请求，
		因此会产生找不到controller的错误。
		使用这个注解后，一旦spring发现请求是对静态资源的访问，就会把这个请求交给web容器
		（tomcat）进行处理。
		
		而上面的<mvc:resources>这个则是更进一步，直接自己对这些访问静态资源的请求处理。
		也因此，使用<mvc:resources>可以把静态资源放在WEB-INF目录下访问。
		还可以把静态资源直接放在classpath目录中，甚至可以用jar的形式放着。
	 -->
	<mvc:default-servlet-handler/>

4.在配置DispatcherServlet的时候<url-pattern>写成*.do之类的。不过这个不符合Restful规范
-------------------------------------------------------------------------------
昨晚还好好看了restful这种设计风格，感觉不错
---------------------------------------------------------------------------------
<servlet>
  	<servlet-name>spring-controller</servlet-name>
  	<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
  	<init-param>
  		<param-name>contextConfigLocation</param-name>
  		<param-value>classpath:spring/spring-*.xml</param-value>
  	</init-param>
  	<load-on-startup>1</load-on-startup>
  </servlet>
  <servlet-mapping>
  	<servlet-name>spring-controller</servlet-name>
  	<url-pattern>/</url-pattern>
  </servlet-mapping>
web.xml中可以这么写，就不用写监听器就可以自动加载spring的配置文件了
--------------------------------------------------------------------------------------
@RequestMapping(value="/{seckillId}/exposer",
			method=RequestMethod.POST,
			produces={"application/json;charset=UTF-8"})
	@ResponseBody
这样配置，应该就可以直接返回json数据，而不需要把返回的对象再转换成json对象。
---------------------------------------------------------------------------------------
JDK1.8 new Date()需要传一个System.currentTimeMillis()进去
--------------------------------------------------------------------------------------
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:formatDate value="${seckill.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
引入上面这个标签后，可以jsp中这么写，格式化时间格式，注意，HH表示24小时的时间格式 ，
--------------------------------------------------------------------------------							 
springmvc 有一个validation接口可以用来进行数据验证
--------------------------------------------------------------------------------------
bootstrap可以直接在http://www.runoob.com/bootstrap/bootstrap-environment-setup.html这个网站把
它的代码考下来，就可以直接进行bootstrap的开发了
-------------------------------------------------------------------------------------
可以进入这个网站  www.bootcdn.cn 获取可靠的CDN，在引入一些外部js。css文件的时候就不需要自己下载了。
----------------------------------------------------------------------------------------
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
这个视频看的太值了。
js模块化：
var seckill = {
		//封装秒杀相关的ajax的url
		URL:{
			
		},
		//验证手机号
		validatePhone : function(userPhone){
			if()
		},
		//详情页秒杀逻辑 
		detail:{
			//详情页初始化
			init : function(params){
				//手机验证和登录，计时交互
				//在cookie中查找手机号
				var userPhone = $.cookie('userPhone');//手机号
				var seckillId = params['seckillId'];//秒杀商品id
				var startTime = params['startTime'];//开始时间:毫秒
				var endTIme = params['endTime'];//结束时间
			}
		}
}
假设需要用详情页初始化的方法，就在详情页里面调用
seckill.detail.init(params);
-------------------------------------------------------------------------------
<!-- 让WEB-INF/resources文件夹可以被访问 -->
	<mvc:resources location="/WEB-INF/resources/" mapping="/resources/**"></mvc:resources>
这么写才可以访问到WEB-INF中的资源文件。
---------------------------------------------------------------------------------
NaN表示非数值属性。
----------------------------------------------------------------------------
一个Date的fastTime属性，也就是毫秒数，居然会多000。比如页面用EL表达式获取到一个Date对象的time
${date.time}也就是fastTime属性的时候，需要除以1000，才能正常的转换成一个年月日时分秒的日期。
我是因为不管活动是否在进行时间内，都显示活动倒计时，然后看了date的源码才发现的。
事实证明，问题并不在这里，多000居然没什么关系，主要是我给计时方法传参的时候少写了一个参数，导致顺序不对，
可这样这个方法居然能执行！！！
--------------------------------------------------------------------------------
在执行一个事务的时候可以考虑各个语句执行的顺序，例如一个很快的操作可能出错的，就可以尽快执行，这样可以更快的
执行完一个事务
--------------------------------------------------------------------------------
存储过程：
	1.优化的是事务行级锁持有的时间
	2.不要过度依赖存储过程
	3.简单的逻辑可以应用存储过程
	4.QPS:一个商品6000/qps
-----------------------------------------------------------------------------------
对于spring来说，如果不考虑完善的处理异常，异常只是告诉它是否需要回滚而已
------------------------------------------------------------------------------------
这个项目里我还写了调用短信接口的service，用httpclient.jar可以很方便的发送请求，调用接口。
我之前居然还想着在js里写ajax来调用接口直接返回。
-----------------------------------------------------------------------------------
从网上弄了一个bootstrap的黑色的主题，好看多了。
！！！此处注意，想要在springMVC中引入WEB-INF下的静态资源，需要在配置文件中配置<mvc:resources>。
最好用${pageContext.servletContext.contextPath}这种方式引入。