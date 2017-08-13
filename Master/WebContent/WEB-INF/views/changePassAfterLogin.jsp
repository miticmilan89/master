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
	<title><spring:message code="label.changePass" /></title>
	
	<link rel="stylesheet" href="<c:url value="/themes/${sessionScope[Constants.CURRENT_THEME]}"/>/jquery-ui.css" type="text/css"/> 
	<link rel="stylesheet" href="<c:url value="/themes/${sessionScope[Constants.CURRENT_THEME]}"/>/common.css" type="text/css"/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery-1.11.2.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery-ui.min.js"/>"></script>
	
<style>
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
.footerSection {
	text-align:center;
	background-color:#466FA9;
	color: white;
}

</style>
<script>
function goToLogout() {
	document.location="<c:url value="/app/logout"/>";
}
$(document).ready(function() {
	$.widget("ui.tooltip", $.ui.tooltip, {
		 options: {
			 content: function () {
				 return $(this).prop('title');
			 }
		 }
	});
	
	$('.jqHelptooltip').tooltip({
	    position: {
	        my: "center bottom-5",
	        at: "center top",
	        using: function (position, feedback) {
	            $(this).css(position);
	            $(this).addClass("jqhelptooltip");
	            $("<div>")
	                .addClass(feedback.vertical)
	                .addClass(feedback.horizontal)
	                .appendTo(this);
	        }
	    },
	    content: function() {
	        return $(this).attr('title');
	    }
	});	
});
</script>
</head>

<body>

<div class="container">
<article>
	<header>
		<div style="float:left">
			<c:set var="_hght" value="80px"/>
			<c:choose>
				<c:when test="${!sessionScope[Constants.LOGGED_USER].customizedTheme }">
					<img src="<c:url value="/Master.gif"/>" height="<c:out value="${_hght }"/>"/>				
				</c:when>
				<c:otherwise>
					<img src="<c:url value="/themes/${sessionScope[Constants.CURRENT_THEME]}"/>/logo.png" height="<c:out value="${_hght }"/>"/>
				</c:otherwise>
			</c:choose>
		</div>
		<div style="clear: both;"></div>
  		<h1 class="headerLogin">
  			<spring:message code="label.changePass"/>
  			<c:if test="${passwordExpired}"> - <spring:message code="label.expiredPassword"/></c:if>
  		</h1>
	</header>

	<section class="mainSection">
		<form id="loginForm3" name="f" action="<c:url value="/app/changePass"/>" method="post" autocomplete="off" >
			<div style="padding-top:20px;">
	            		<c:if test="${sessionScope.changePassAfterLoginErr!=null }">
	            			<span style="color:red; font-size: 12px"><c:out   escapeXml="false" value="${sessionScope.changePassAfterLoginErr}"/><br/>&nbsp;</span>
	            			<c:remove var="changePassAfterLoginErr" scope="request"/>
	            		</c:if>
	                	<div class="row" style="font-weight: bold; text-align: center; padding-bottom:5px;">

			                <label class="column-label" style="width:150px" for="username"><spring:message code="label.currentPassword"/></label>
			                <input type="password" required name="currentPassword" placeholder="<spring:message code="label.currentPassword"/>" value="<c:out   escapeXml="false" value="${param.currentPassword}"/>" autofocus="autofocus"/>
			                <div class="jqHelptooltip jqhelpbckg" title="" style="visibility: hidden;"></div><br/>
			                
			                <label class="column-label" style="width:150px" for="password"><spring:message code="label.newPassword"/></label>
			                <input type="password" required name="newPassword" placeholder="<spring:message code="label.newPassword"/>" value="<c:out   escapeXml="false" value="${param.newPassword}"/>"/>
			                <c:if test="${passwordRequirements != null }"><div class="jqHelptooltip jqhelpbckg" title="<spring:message code="${passwordRequirements}"/>"></div></c:if><br/>

			                <label class="column-label" style="width:150px" for="password"><spring:message code="label.newPasswordRepeat"/></label>
			                <input type="password" required name="newPasswordRepeat" placeholder="<spring:message code="label.newPasswordRepeat"/>" value="<c:out   escapeXml="false" value="${param.newPasswordRepeat}"/>"/>
			                <div class="jqHelptooltip jqhelpbckg" title="" style="visibility: hidden;"></div><br/>
						</div>

						<div style="clear: both;"></div>
		                <div style="padding-top : 15px" >
		                    <button type="submit" class="btn"><spring:message code="button.OK"/></button>
		                    <button type="button" class="btn" onclick="goToLogout()"><spring:message code="button.cancel"/></button>
		                </div>
	            <masterInput:mastertokens/>
			</div>
		</form>   		
	</section>
	<br/><br/>
</article>	
	<footer class="footerSection">
  		<br>
  	</footer>
</div>
  
</body>
</html>
 