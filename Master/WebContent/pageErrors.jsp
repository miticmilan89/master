<%@page import="java.util.Calendar"%>
<%@ page contentType="text/html"%>
<%@ taglib prefix="util" uri="/WEB-INF/tlds/util-functions.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<script>
	var _masterMSGTd = '<tr class="_masterMsgErrTemp" ><td class="_tableErrTd" style="width:20px;"><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span></td><td class="_tableErrTd errMsgGlobal">|MSG_TXT|</td></tr>';
	var _masterERRTd = '<tr class="_masterMsgErrTemp" ><td class="_tableErrTd" style="width:20px;"><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span></td><td class="_tableErrTd errGlobal">|ERR_TXT|</td></tr>';
</script>

<table id="_appMSG"  class="_tableErr ui-state-highlight ui-corner-all" style="width:100%; margin-bottom:10px; display:none">
	<c:if test="${requestScope.appMessages!=null || sessionScope.appMessages!=null }">
		<script>
		$(document).ready(function() {
			$("#_appMSG").show();
		});
		</script>
		<c:forEach var="message" items="${requestScope.appMessages}">
			<tr class="_masterMsgErr">
				<td class="_tableErrTd" style="width:20px;"><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span></td>
				<td class="_tableErrTd errMsgGlobal">
					<c:choose>
						<c:when test="${util:isSpringErrMsg(message)}">
							<c:set var="_dmymsg" value= "label.${message.objectName}.${message.field}"  />
							<c:set var="_dmymsg1" value= "label.${message.field}"  />
							<c:choose>
								<c:when test="${!util:isMessageExist(_dmymsg) && util:isMessageExist(_dmymsg1)}">
									<%-- get message value and check if we don't have property don't show space and : --%>
									<%-- in case when we have KEY but we don't set VALUE for property, use just message without this dynamic part --%>
									<c:set var="_checkEmpty"><spring:message code = "${_dmymsg1}"  /></c:set>
									<c:if test="${not empty _checkEmpty}">
										<spring:message code = "${_dmymsg1}"  />:&nbsp;
									</c:if>
									<spring:message message = "${message}" />${messagesFactory.messages }
								</c:when>
								<c:otherwise>
									<c:set var="_checkEmpty"><spring:message code = "${_dmymsg}"  /></c:set>
									<c:if test="${not empty _checkEmpty}">
										<spring:message code = "${_dmymsg}"  />:&nbsp;
									</c:if>
									<spring:message message = "${message}" />${messagesFactory.messages }
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise><c:out   escapeXml="false" value="${message }" /></c:otherwise>
					</c:choose>
				</td>
			</tr>
		</c:forEach>

		<c:forEach var="message" items="${sessionScope.appMessages}">
			<tr class="_masterMsgErr">
				<td class="_tableErrTd" style="width:20px;"><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span></td>
				<td class="_tableErrTd errMsgGlobal">
					<c:choose>
						<c:when test="${util:isSpringErrMsg(message)}">
							<c:set var="_dmymsg" value= "label.${message.objectName}.${message.field}"  />
							<c:set var="_dmymsg1" value= "label.${message.field}"  />
							<c:choose>
								<c:when test="${!util:isMessageExist(_dmymsg) && util:isMessageExist(_dmymsg1)}">
									<c:set var="_checkEmpty"><spring:message code = "${_dmymsg1}"  /></c:set>
									<c:if test="${not empty _checkEmpty}">
										<spring:message code = "${_dmymsg1}"  />:&nbsp;
									</c:if>
									<spring:message message = "${message}" />${messagesFactory.messages }
								</c:when>
								<c:otherwise>
									<c:set var="_checkEmpty"><spring:message code = "${_dmymsg}"  /></c:set>
									<c:if test="${not empty _checkEmpty}">
										<spring:message code = "${_dmymsg}"  />:&nbsp;
									</c:if>
									<spring:message message = "${message}" />${messagesFactory.messages }
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise><c:out   escapeXml="false" value="${message }" /></c:otherwise>
					</c:choose>
				</td>
			</tr>
		</c:forEach>
		<%-- we MUST remove from session scope, from request scope we can remove it or not --%>
		<c:remove var="appMessages" scope="session" />
	</c:if>
</table>

<table id="_appERR" class="_tableErr ui-state-error ui-corner-all" style="width:100%; margin-bottom:10px;display: none">
	<c:if test="${requestScope.appError!=null || sessionScope.appError!=null }">
		<script>
		$(document).ready(function() {
			$("#_appERR").show();
		});
		</script>
		<c:if test="${sessionScope._errorCounter!=null }">
			<tr class="_masterMsgErr">
				<td class="_tableErrTd" style="width:20px;"><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span></td>
				<td class="_tableErrTd errGlobal">
					<spring:message code="error.dateTime"/> <util:formatDate value="<%=Calendar.getInstance().getTime() %>"  />
					,&nbsp;<spring:message code="error.counter"/> <c:out   escapeXml="false" value="${sessionScope._errorCounter}" />
				</td>
			</tr>
			<c:remove var="_errorCounter" scope="session"/>
		</c:if>
		<c:forEach var="message" items="${requestScope.appError}">
			<tr class="_masterMsgErr">
				<td class="_tableErrTd" style="width:20px;"><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span></td>
				<td class="_tableErrTd errGlobal">
					<c:choose>
						<c:when test="${util:isSpringErrMsg(message)}">
							<c:set var="_dmymsg" value= "label.${message.objectName}.${message.field}"  />
							<c:set var="_dmymsg1" value= "label.${message.field}"  />
							<c:choose>
								<c:when test="${!util:isMessageExist(_dmymsg) && util:isMessageExist(_dmymsg1)}">
									<%-- get message value and check if we don't have property don't show space and : --%>
									<%-- in case when we have KEY but we don't set VALUE for property, use just message without this dynamic part --%>
									<c:set var="_checkEmpty"><spring:message code = "${_dmymsg1}"  /></c:set>
									<c:if test="${not empty _checkEmpty}">
										<spring:message code = "${_dmymsg1}"  />:&nbsp;
									</c:if>
									<spring:message message = "${message}" />${messagesFactory.messages }
								</c:when>
								<c:otherwise>
									<c:set var="_checkEmpty"><spring:message code = "${_dmymsg}"  /></c:set>
									<c:if test="${not empty _checkEmpty}">
										<spring:message code = "${_dmymsg}"  />:&nbsp;
									</c:if>
									<spring:message message = "${message}" />${messagesFactory.messages }
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise><c:out   escapeXml="false" value="${message }" /></c:otherwise>
					</c:choose>
				  </td>
			</tr>
		</c:forEach>
	
		<c:forEach var="message" items="${sessionScope.appError}">
			<tr class="_masterMsgErr">
				<td class="_tableErrTd" style="width:20px;"><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span></td>
				<td class="_tableErrTd errGlobal">
					<c:choose>
						<c:when test="${util:isSpringErrMsg(message)}">
							<c:set var="_dmymsg" value= "label.${message.objectName}.${message.field}"  />
							<c:set var="_dmymsg1" value= "label.${message.field}"  />
							<c:choose>
								<c:when test="${!util:isMessageExist(_dmymsg) && util:isMessageExist(_dmymsg1)}">
									<c:set var="_checkEmpty"><spring:message code = "${_dmymsg1}"  /></c:set>
									<c:if test="${not empty _checkEmpty}">
										<spring:message code = "${_dmymsg1}"  />:&nbsp;
									</c:if>
									<spring:message message = "${message}" />${messagesFactory.messages }
								</c:when>
								<c:otherwise>
									<c:set var="_checkEmpty"><spring:message code = "${_dmymsg}"  /></c:set>
									<c:if test="${not empty _checkEmpty}">
										<spring:message code = "${_dmymsg}"  />:&nbsp;
									</c:if>
									<spring:message message = "${message}" />${messagesFactory.messages }
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise><c:out   escapeXml="false" value="${message }" /></c:otherwise>
					</c:choose>
				</td>
			</tr>
		</c:forEach>
		<%-- we MUST remove from session scope, from request scope we can remove it or not --%>
		<c:remove var="appError" scope="session" />
	</c:if>
</table>
<table id="pageErrors" style="background-color: #E6C1CE" style="width:100%; display:none"></table>
