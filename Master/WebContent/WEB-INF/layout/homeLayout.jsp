<!DOCTYPE html>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="util" uri="/WEB-INF/tlds/util-functions.tld"%>
<%@ page language="java" contentType="text/html;  charset=UTF-8"%>
<html>
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=9" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="Expires" content="0" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate" />
	<meta name="viewport" content="width=device-width,initial-scale=1.0">
	
	<title><spring:message code="app.title" /></title>
	
	<script type="text/javascript">
		var appUnique = (new Date()).getMilliseconds();
		var appUsr = "<c:out value="${sessionScope[Constants.LOGGED_USER].usernameHash}"/>";
		var appPath = "<c:out value="${pageContext.request.contextPath}"/>";
		var appTheme = "<c:out value="${sessionScope[Constants.CURRENT_THEME]}"/>";
		var appjobkey = "";
		var appjobid = null;
		var appjobintervalmsec = 5000;
		var applastident = null;
		var currentURL = ""+window.location;
		var pageLoaded = false;
		var pageTitle = "Master title"; 	
		var formSubmitted=false;
		var loginType = "<c:out value="${sessionScope[Constants.LOGGED_USER].userType.id}"/>";
		
		var label_ok = "<spring:message code="button.OK"/>";
		var label_close ="<spring:message code="button.close"/>";
		var label_cancel ="<spring:message code="button.cancel"/>";
		var label_clear ="<spring:message code="button.clear"/>";
		var label_proceed="<spring:message code="button.proceed"/>";
		var label_delete_messagetext = "<spring:message code='app.delete.messagetext' />";
		var label_confirmation_messagetitle = "<spring:message code="app.confirmation.messagetitle"/>";
		var label_warning_messagetitle = "<spring:message code="app.warning.messagetitle"/>";
		var label_disable_messagetext = "<spring:message code="app.disable.messagetitle"/>";
		
		var sessionExpired = "<spring:message code="message.sessionExpired"/>";
		var resetBtn = "<spring:message code="button.reset" />";
		var formNotChanged = "<spring:message code="label.formNotChanged" />";
		
		var notValidUrl = "<spring:message code="label.urlNotValid" />";
		var validUrl = "<spring:message code="label.urlValid" />";

		var notValidEmail = "<spring:message code="label.emailNotValid" />";
		var validEmail = "<spring:message code="label.emailValid" />";
		
		var maxInactiveSeconds = "<c:out value="${pageContext.request.session.maxInactiveInterval}"/>";
		var sessionRemainSecCount = "<c:out value="${pageContext.request.session.maxInactiveInterval}"/>";
		
		var appDatePattern = "<c:out value="${sessionScope[Constants.LOGGED_USER].formatPatterns.dateInputPattern}"/>".toLowerCase();
		var appTimePattern = "<c:out value="${sessionScope[Constants.LOGGED_USER].formatPatterns.timeInputPattern}"/>".toLowerCase();
		var appDecimalSeparator = "<c:out value="${sessionScope[Constants.LOGGED_USER].formatPatterns.numberDecimalSeparatorChar}"/>".toLowerCase();
		var appTousandSeparator = "<c:out value="${sessionScope[Constants.LOGGED_USER].formatPatterns.numberTousandSeparatorChar}"/>".toLowerCase();
	</script>	
	<link rel="stylesheet" href="<c:url value="/themes/common/reset.css"/>" type="text/css"/>
	
	<link rel="stylesheet" href="<c:url value="/themes/${sessionScope[Constants.CURRENT_THEME]}"/>/jquery-ui.css" type="text/css"/>
	<link rel="stylesheet" href="<c:url value="/themes/${sessionScope[Constants.CURRENT_THEME]}"/>/common.css" type="text/css"/>
	<link rel="stylesheet" href="<c:url value="/themes/${sessionScope[Constants.CURRENT_THEME]}"/>/jquery.ui.timepicker.css" type="text/css"/>	
	
	<%-- Bootstrap core CSS 
    <link href="<c:url value="/themes/${sessionScope[Constants.CURRENT_THEME]}"/>/smartmenu/bootstrap/bootstrap.min.css" rel="stylesheet">
    SmartMenus jQuery Bootstrap Addon CSS 
    <link href="<c:url value="/themes/${sessionScope[Constants.CURRENT_THEME]}"/>/smartmenu/jquery.smartmenus.bootstrap.css" rel="stylesheet">
    --%>	

	<%-- SmartMenus core CSS (required) --%>
    <link href="<c:url value="/themes/${sessionScope[Constants.CURRENT_THEME]}"/>/smartmenu/sm-core-css.css" rel="stylesheet">	

	<%-- "sm-blue" menu theme (optional, you can use your own CSS, too) --%>
    <link href="<c:url value="/themes/${sessionScope[Constants.CURRENT_THEME]}"/>/smartmenu/sm-blue.css" rel="stylesheet">	
	
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery-1.11.2.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery-ui.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.ui.datepicker-formats.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.ui.timepicker.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.ui.timepicker-formats.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.ui.touch-punch.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tinysort.min.js"/>"></script>
	<script type="text/javascript"  src="<c:url value="/js/common.js"/>"></script>
	<script type="text/javascript"  src="<c:url value="/js/commonFunctions.js"/>"></script>
	
   <%-- Bootstrap core JavaScript
    ================================================== 
     Placed at the end of the document so the pages load faster 
    <script src="<c:url value="/js/smartmenu/bootstrap/bootstrap.min.js"/>"></script>
    --%>

    <%-- SmartMenus jQuery plugin --%>
    <script src="<c:url value="/js/smartmenu/jquery.smartmenus.min.js"/>"></script>
    <script src="<c:url value="/js/smartmenu/keyboard/jquery.smartmenus.keyboard.min.js"/>"></script>
    

    <%-- SmartMenus jQuery Bootstrap Addon 
    <script src="<c:url value="/js/smartmenu/bootstrap/jquery.smartmenus.bootstrap.min.js"/>"></script>
    --%>
	
	<!--[if lt IE 9]>
      <script src="<c:url value="/js/html5.js"/>"></script>
    <![endif]-->
	
<!--[if IE]>
	<style type="text/css">
	</style>
<![endif]-->	

</head>
<body>
<div >
	<div class="container"> 
		<article>
			<div class="header"><tiles:insertAttribute name="header" /></div>
			<div class="menu"><tiles:insertAttribute name="menu" /></div>
		</article>
		<article style="text-align: center">
			<div class="content">
				<div class="contentTitle" id="CONTENT_TITLE"><tiles:insertAttribute name="title" /></div>
				<div class="contentToolbar" id="CONTENT_TOOLBAR"></div>
				<jsp:include page="/pageErrors.jsp" flush="true"></jsp:include>
				<tiles:insertAttribute name="body" />
			</div>
		</article>
		<footer class="footer">
			<tiles:insertAttribute name="footer" />
		</footer>
	</div>
</div>
<div id="_dialogDiv" style="display:none"> 
	<div id="_dialogDivImgContainer" style="float:left"><img id="_dialogDivImg" height="50px" src=""/></div> 
	<span id="_dialogDivContent" style="float:left; padding-top:20px; padding-left:15px"></span> 
</div> 
<div id="_modalDialogDiv" style="display:none;"></div> 
<div id="_exportDialogDiv" style="display:none;text-align:left"><br/><div id="JOB_STATUS"></div></div> 
<div id="_loadingDiv" style="display:none"><img src="<c:url value="/themes/${sessionScope[Constants.CURRENT_THEME]}/img/loading.gif"/>" /><br/>Please Wait</div>
<c:if test="${sessionScope.EMAIL_ERROR!=null}">
<div id="_emailError" style="display:none" title="">
<spring:message code="label.emailErrorDt" />:<c:out value="${sessionScope.EMAIL_ERROR_DATE}"/><br/>
<c:out value="${sessionScope.EMAIL_ERROR}"/> 
</div>
</c:if>
</html>