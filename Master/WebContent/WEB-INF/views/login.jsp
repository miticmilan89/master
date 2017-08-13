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
	<script>
	var loginPage=true;
	function sbChanged() {
		for (var i=1;i<=3;i++)
			document.getElementById("loginForm"+i).style.display="none";
		
		var x = document.getElementById("loginType").value;
		document.getElementById("loginForm"+x).style.display="";

	}
	</script>
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
	text-align:center;
	width:100%;
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
<noscript><c:import url="/nojs.jsp"/></noscript>
    <div id="nohtml5" style="display:none; width:100%; text-align: center; vertical-align: middle; font-size: 15px; background-color: #ffffff; color:red; font-weight: bold;">
    	<img src="<c:url value="/nohtml5.png"/>" height="70px"/>
    	<br/><spring:message code="label.noHtml5" />
    </div>

<div class="container pagecontainer">
<article>
	<header>
		<div style="float:left"><img src="<c:url value="/Master.gif"/>" height="109px"/></div>
		<div style="float:right;padding-top:50px; width: 80px">
			<a style="float:right;margin-left:15px;" href="<c:url value='/app/changeLanguage/en_EN/${requestScope.loginType}'/>"><img src="<c:url value="/gbr.png"/>" width="25px" style="border:0px" /></a>
			<a style="float:right;margin-left:15px;" href="<c:url value='/app/changeLanguage/sr_RS/${requestScope.loginType}'/>"><img src="<c:url value="/srb.png"/>" width="25px" style="border:0px" /></a>
		</div>
		<div style="clear: both;"></div>
  		<h1 class="headerLogin"><spring:message code="app.title"/></h1>
	</header>

	<section class="mainSection">
		<c:import url="/WEB-INF/views/login${requestScope.loginType}.jsp"></c:import>		
	</section>
	<br/><br/>
</article>	
<div class="footerSection">
	<footer>
  		<br>
  	</footer>
</div>  	
</div>
<script>
	if( window.FormData === undefined ) {
		document.getElementById("nohtml5").style.display="block";
	}
</script>
  
</body>
</html>
 