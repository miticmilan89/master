
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="masterInput" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>


<form id="loginForm2" name="f" action="<c:url value="/app/login-process"/>" method="post" autocomplete="off" >
	<div style="padding-top:20px; text-align: center">
				<c:choose>
            		<c:when test="${sessionScope._errLogin!='1' }">
            			<span style="color:red; font-size: 12px"><spring:message code="${sessionScope._errLogin}"/><br/>&nbsp;</span>
            			<c:remove var="_errLogin" scope="session"/>
            		</c:when>
            		<c:when test="${param.error!=null || sessionScope._errLogin=='1' }">
            			<span style="color:red; font-size: 12px"><spring:message code="error.wrongCredentials"/><br/>&nbsp;</span>
            			<c:remove var="_errLogin" scope="session"/>
            		</c:when>
           		</c:choose>
               	<div class="row" style="font-weight: bold; text-align: center; padding-bottom:5px;">
	                <spring:message code="label.pleaseLogin"/><br/><br/>

	                <label class="column-label" for="username"><spring:message code="label.username"/></label>
	                <input class="column-data" type="text" name="username" placeholder="<spring:message code="label.username"/>" value="<c:out   escapeXml="false" value="${param.username}"/>"/><br/>
	                
	                <label class="column-label" for="password"><spring:message code="label.password"/></label>
	                <input class="column-data" type="password" name="password" placeholder="<spring:message code="label.password"/>" value="<c:out   escapeXml="false" value="${param.password}"/>"/><br/>			                
				</div>

				<div style="clear: both;"></div>
                <div style="padding-top : 15px;text-align:center" >
                    <button type="submit" class="btn toolbarButton"><spring:message code="button.login"/></button>
                </div>
           <masterInput:mastertokens/>
           <input type="hidden" name="loginType" value="User"/>
	</div>
</form>   