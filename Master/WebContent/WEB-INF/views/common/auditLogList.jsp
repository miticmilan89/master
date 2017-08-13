<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="masterInput" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="util" uri="/WEB-INF/tlds/util-functions.tld"%>


<script type="text/javascript">
var defaultFormName = "filterForm";
var defaultSubmitBtnName = "submitSearchBtn";
$(document).ready(function() {
	$(".accordionFilter").accordion({
		heightStyle: "content",
		collapsible: true,
		active: <c:choose><c:when test="${ util:isFilterOpen(sessionScope.filters, 'auditLogSF') }" >0</c:when><c:otherwise>false</c:otherwise></c:choose>
	});
	$(".auditLogDetails").click(function() {
		var index = $(this).attr("_index");
		$(this).toggleClass("ui-icon-circle-triangle-s");
		$(this).toggleClass("ui-icon-circle-triangle-n");
	
		if ($(this).hasClass("ui-icon-circle-triangle-n"))
			$("#"+index+"_TR_DETAIL").show();
		else
			$("#"+index+"_TR_DETAIL").hide();
		return false;
	});
	$("._divWs").width($(".list").width()-35);
	
});
</script>
<div class="accordionFilter">
	<h3 onclick="showHideFilter('auditLogSF')"><span id="auditLogSF" class="searchFilter" style="visibility:hidden; text-align:left;">
		<c:choose><c:when test="${ util:isFilterOpen(sessionScope.filters, 'auditLogSF') }" >+</c:when><c:otherwise>-</c:otherwise></c:choose>	
		</span><spring:message code="label.filter" />
	</h3>

	<div>
		<c:url value="/app/auditLog/list" var="_action"/>
		<form:form action="${_action }" modelAttribute="auditLog" method="POST" id="filterForm" name="filterForm">

			<masterInput:searchDateTimeField dataClass="field30perc" required="false" label="label.dateFrom" name="dateFrom" timename="timeFrom"/>
			<masterInput:searchDateTimeField dataClass="field30perc" required="false" label="label.dateTo" name="dateTo" timename="timeTo"/>
			<masterInput:searchInputField dataClass="field30perc" required="false" label="label.auditLog.tableName" name="tableName"  value="${auditLog.tableName}" /> 
			<masterInput:searchInputField dataClass="field30perc" required="false" label="label.auditLog.auditType" name="auditType"  value="${auditLog.auditType}" /> 
			<masterInput:searchSelectField dataClass="field30perc" required="false" label="label.auditLog.auditStatus" name="auditStatus" items="${statusList }" size="1" />
 			
			<input type="hidden" name="currentPage" id="currentPage" value="<c:out   escapeXml="false" value="${auditLog.currentPage}"/>" />
			<input type="hidden" name="orderColumn" id="orderColumn" value="<c:out   escapeXml="false" value="${auditLog.orderColumn}"/>" />
			<input type="hidden" name="orderAsc" id="orderAsc" value="<c:out   escapeXml="false" value="${auditLog.orderAsc}"/>" />
			<input type="hidden" name="restartPagging" id="restartPagging" value="true"/>
			<masterInput:mastertokens/>
			
			<input type="submit" value="Submit" id="submitSearchBtn" style="display:none" />
		</form:form>
	</div>		
</div>
<span id="PAGE_TOOLBAR">
	<button type="button" class="toolbarButton" onclick="return submitDefaultForm()"><spring:message code="button.search"/></button>
</span>
<span id="PAGE_TITLE"><spring:message code="title.auditLog.list"/></span>

<div>
<c:if test="${searchResults!=null }">
	<c:set value="${searchResults}" var="pagingData" scope="request"/>
	<c:import url="/WEB-INF/views/pagingHeader.jsp"/>
	<c:remove var="pagingData" scope="request"/>
	
	<table class="list _masterRespTable">
	<thead>
		<tr>
			<th _col="1" onclick="return applySearchOrder(this)"><spring:message code="label.auditLog.id" /></th>
			<th _col="2" onclick="return applySearchOrder(this)"><spring:message code="label.auditLog.auditTimestamp" /></th>
			<th _col="3" onclick="return applySearchOrder(this)"><spring:message code="label.auditLog.tableName" /></th>
			<th _col="4" onclick="return applySearchOrder(this)"><spring:message code="label.auditLog.auditType" /></th>
			<th _col="5" onclick="return applySearchOrder(this)"><spring:message code="label.auditLog.auditStatus" /></th>
			<th _col="6" onclick="return applySearchOrder(this)"><spring:message code="label.auditLog.appUserFk" /></th>
			<th _col="7" onclick="return applySearchOrder(this)"><spring:message code="label.auditLog.participantFk" /></th>
			<th> </th>
			
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${searchResults.results}" var="item" varStatus="loopStatus"> 
			<tr class="${loopStatus.index % 2 == 0 ? 'even' : 'odd'}">
				<%--<td><a class="activeLink" onclick="return showLoadingDiv()" href="<c:url value="/app/auditLog/view/${item.id}"/>"><c:out   escapeXml="false" value="${item.id}" /></a></td> --%>
				<td><c:out   escapeXml="false" value="${item.id}" /></td>
				<td><util:formatDate type="both" value="${item.auditTimestamp}" /></td>
				<td><c:out   escapeXml="false" value="${item.tableName}" /></td>
				<td><c:out   escapeXml="false" value="${item.auditType}" /></td>
				<td>
					<c:choose>
						<c:when test="${item.success}"><spring:message  code="label.auditLog.auditStatus.${item.auditStatus}" /></c:when>
						<c:when test="${item.error}"><span style="color:red"><spring:message  code="label.auditLog.auditStatus.${item.auditStatus}" /></span></c:when>
					</c:choose>
				</td>
				<td><c:out   escapeXml="false" value="${item.appUser.username}" /></td>
				<td><c:out   escapeXml="false" value="${item.participant.name}" /></td>
				<td>
					<span style="margin-left: auto; margin-right: auto;" class="ui-icon ui-icon-circle-triangle-s auditLogDetails" _index="${loopStatus.index}" title="<spring:message code="label.detail" />"></span>
				</td>
			</tr>
			<tr style="display: none;" id="${loopStatus.index}_TR_DETAIL" class="${item.error ? 'error' : loopStatus.index % 2 == 0 ? 'even' : 'odd'}">
				<td colspan="9" style="padding-left: 20px; padding-bottom: 20px;">
					<c:set var="_hx" value="150" scope="page" />
					<div style="overflow: auto; height:<c:out value="${_hx }"/>px; width: 900px" class="_divWs">				
					<pre>
<c:out   escapeXml="true" value="${item.updateStringForDisplay}" />
					</pre>
					</div>
				
				
				</td>
			</tr>
			
		</c:forEach>
		</tbody>
	</table>
</c:if>
</div>