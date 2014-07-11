<%@page import="org.qiyu.ClientUtil"%>
<%@page import="java.io.FileInputStream"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<script type='text/javascript' src='./jquery.min.js'></script>
<script type="text/javascript">
function refresh()
{
	var element = document.getElementById("log");
	element.innerHTML = "/Log?type=1";
	window.open("http://localhost:8000/QiYuWeb/Log?type=1");
}
function loadData()
{
    //加载a.html到层 
    $("#log").load("./Log?type=1");
}
function clearLog()
{
	$("#log").load("./Log?type=4");
}
</script>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>QiYuLog</title>
</head>
<body onload="loadData()">
<div id="operater">
<input type="button" value="refresh" onclick="loadData();"/>
<input type="button" value="delLog" onclick="clearLog();"/>
</div>
<div id="log">
</div>
</body>
</html>