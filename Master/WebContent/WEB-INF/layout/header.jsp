<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<div class="content">
<article>
	<header>
	
		<div style="text-align:left; float:left;cursor:pointer" title="Home page" onclick="document.location='<c:url value="/app/home"/>'">
			<img src="<c:url value="/Master.gif"/>" height="80px"/>
		</div>				
						
		<div style="width:33%; text-align:right; padding-top:10px; float:right;padding-right:5px;">
			<div style="width:250px; float:right;">
				<div style="width:100%; clear:both;">
					<a title="Serbian" style="float:left;margin-left:5px;padding-top:3px;" href="<c:url value='/app/changeLocale/sr_RS'/>"><img src="<c:url value="/themes/${sessionScope[Constants.CURRENT_THEME]}/img/srb.png"/>" width="25px" /></a>
					<a title="English" style="float:left;margin-left:5px;padding-top:3px;" href="<c:url value='/app/changeLocale/en_EN'/>"><img src="<c:url value="/themes/${sessionScope[Constants.CURRENT_THEME]}/img/gbr.png"/>" width="25px" /></a>
					<a style="float:right;margin-right:5px;" href="<c:url value='/app/logout'/>"><img src="<c:url value="/themes/${sessionScope[Constants.CURRENT_THEME]}/img/logout.png"/>" width="20px" /></a>
				</div><br>
				<div style="width:100%; clear:both;">
					<hr>
				</div>
				<div style="width:100%; text-align:left;">
					<table style="width:100%;">
						<tr><td><spring:message code="label.loggedUser" />:</td><td align="right"><c:out   escapeXml="false" value="${sessionScope[Constants.LOGGED_USER].username}"/></td></tr>
						<tr><td><spring:message code="label.userType" />:</td><td align="right"><spring:message code="label.appUser.userType.${sessionScope[Constants.LOGGED_USER].userType.id}" /></td></tr>
						<c:if test="${sessionScope[Constants.LOGGED_USER].user}">
							<tr><td><spring:message code="label.participant.name" />:</td><td align="right"><c:out   escapeXml="false" value="${sessionScope[Constants.LOGGED_USER].participantName}"/></td></tr>
						</c:if>
						<tr><td><spring:message code="label.sessionExpire" />:</td><td align="right"><div id="sessionTimer"></div></td></tr>
					</table>
				</div>
			</div>
		</div>
		<div style="clear: both;"></div>	
	</header>
</article> 

</div>