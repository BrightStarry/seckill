<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!-- 引入jstl -->
<%@include file="common/tag.jsp" %>
<!DOCTYPE html>
<html>
   <head>
      <title>秒杀商品列表页</title>
      <%@include file="common/head.jsp" %>
   </head>
   <body>
   		<!-- 页面显示部分 -->
   		<div class="container">
   			<div class="panel panel-default">
   				<div class="panel-heading text-center">
   					<h2 id="test" >秒杀列表</h2>
   				</div>
   				<div class="panel-body">
   					<table class="table table-hover">
   						<thead>
   							<tr>
   								<th>名称</th>
   								<th>库存</th>
   								<th>开始时间</th>
   								<th>结束时间</th>
   								<th>创建时间</th>
   								<th>详情页</th>
   							</tr>
   						</thead>
   						<tbody>
   							<c:forEach items="${list}" var="seckill">
   								<tr>
   									<td>${seckill.name}</td>
   									<td>${seckill.number}</td>
   									<td>
   										<fmt:formatDate value="${seckill.startTime}"
   											 pattern="yyyy-MM-dd HH:mm:ss"/>
   									</td>
   									<td>
   										<fmt:formatDate value="${seckill.endTime}"
   											 pattern="yyyy-MM-dd HH:mm:ss"/>
   									</td>
   									<td>
   										<fmt:formatDate value="${seckill.createTime}"
   											 pattern="yyyy-MM-dd HH:mm:ss"/>
   									</td>
   									<td><a class="btn btn-info" href="/seckill/${seckill.seckillId}/detail" target="_blank">link</a></td>
   								</tr>
   							</c:forEach>
   						</tbody>
   					</table>
   				</div>
   			</div>
   		</div>
   </body>
<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="http://cdn.static.runoob.com/libs/jquery/2.1.1/jquery.min.js"></script>
<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="http://cdn.static.runoob.com/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script type="text/javascript">
	/*$(function(){
				
		$('#test').click(function(){
			var params={
				'key':'',
				'info':'我很难受',
				'dtype':'json',
				'userid':'11'
			};
			$.post('http://op.juhe.cn/robot/index',params,function(result){
				console.log('返回码：' + result.error_code);
				console.log('返回说明：' + result.reason)
			},'json');
		});
	});*/
</script>
</html>