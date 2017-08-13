<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="masterInput" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<html>
<head>
	<meta charset="UTF-8">
	<meta http-equiv="Expires" content="0" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate" />
	<meta name="viewport" content="width=device-width,initial-scale=1.0">
	<title><spring:message code="app.title" /></title>
<style>
.container {
	display: inline-block;
	width:100%;
	margin: 0px;
	padding: 0px;
	border: 0px;
}
.headerLogin{ 
	border: 0px;
	text-align: center;
    height: 46px;
    background-color:#466FA9;
    color: white;
	font-size: 31px;
	line-height: 44px;
	letter-spacing: -2px;
	font-weight: bold;
	padding-left: 30px;
	padding-right: 30px; 
	
}
.mainSection {
	padding-left:30px; padding-right:30px; text-align:center;
}
.footerSection {
	text-align:center;
	background-color:#466FA9;
	color: white;
}
.column-label{
	display : inline-block;
	width:160px;
	text-align:right;
	padding: 4px 6px 4px 0px;
	vertical-align: middle;
	line-height: 13px;
	min-height:13px;
	border-collapse: collapse;
}
.column-data{
	width:160px;
	text-align: left;
	line-height: 13px;
	min-height:13px;
	border-collapse: collapse;  
	margin-left: -1px;
	vertical-align: middle;
}
</style>
</head>

<body>

<div class="container">
<article>
	<header>
		<div style="float:left"><img src="<c:url value="/Master.gif"/>" width="130px"/></div>
		<div style="clear: both;"></div>
  		<h1 class="headerLogin"><spring:message code="app.sessionTimeout"/></h1>
	</header>

	<section class="mainSection">
			<img src="<c:url value="/icon_session_timeout-256x225.png"/>" width="130px"/>
			<br/>
			<h2>Click <a href="<c:url value="/index.jsp"/>" >here</a> to login</h2> 		
	</section>
	<br/><br/>
</article>	
	<footer class="footerSection">
  		<br>
  	</footer>
</div>
  
</body>
</html>
 