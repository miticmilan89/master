<!DOCTYPE html>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html>
<head>
	<meta charset="UTF-8">
	<meta http-equiv="Expires" content="0" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate" />
	<meta name="viewport" content="width=device-width,initial-scale=1.0">
	
	<title><spring:message code="app.title" /></title>
	
	<script type="text/javascript">
	var appPath = "<c:out value="${pageContext.request.contextPath}"/>";
	var appTheme = "<c:out value="${sessionScope[Constants.CURRENT_THEME]}"/>";
	var currentURL = ""+window.location;
	var pageLoaded = false;
	var pageTitle = "Master title"; 	
	var formSubmitted=false;
	</script>	
	
	<link rel="stylesheet" href="<c:url value="/themes/common/reset.css"/>" type="text/css"/>
	<link rel="stylesheet" href="<c:url value="/themes/${sessionScope[Constants.CURRENT_THEME]}"/>/common.css" type="text/css"/>
	<script type="text/javascript"  src="<c:url value="/js/common.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/ready.js"/>"></script>
<!--[if IE]>
	<style type="text/css">
	</style>
<![endif]-->	
</head>
<body>
	<tiles:insertAttribute name="body" />
</body>
</html>