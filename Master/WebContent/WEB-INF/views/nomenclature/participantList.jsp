<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %><%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@ taglib prefix="masterInput" tagdir="/WEB-INF/tags" %><%@ taglib prefix="util" uri="/WEB-INF/tlds/util-functions.tld"%>

<script type="text/javascript">
var defaultFormName = "filterForm";
var defaultSubmitBtnName = "submitSearchBtn";
$(document).ready(function() {
	$(".accordionFilter").accordion({
		heightStyle: "content",
		collapsible: true,
		active: <c:choose><c:when test="${ util:isFilterOpen(sessionScope.filters, 'participantFilter') }" >0</c:when><c:otherwise>false</c:otherwise></c:choose>
	});
});


</script>
<div class="accordionFilter">
	<h3 onclick="showHideFilter('participantFilter')"><span id="participantFilter" class="searchFilter" style="visibility:hidden; text-align:left;">
		<c:choose><c:when test="${ util:isFilterOpen(sessionScope.filters, 'participantFilter') }" >+</c:when><c:otherwise>-</c:otherwise></c:choose>	
		</span><spring:message code="label.filter" />
	</h3>

	<div>
		<c:url value="/app/admin/participant/list" var="_action"/>
		<form:form action="${_action }" modelAttribute="participant" method="POST" id="filterForm" name="filterForm">
			<c:set value="${participant}" var="currentFormBean" scope="request"/>

					<masterInput:searchInputField label="label.participant.name" name="name" value="${participant.name }" />  
					<masterInput:searchInputField label="label.participant.adrress" name="adrress" value="${participant.adrress }" />  
					<masterInput:searchInputField label="label.participant.city" name="city" value="${participant.city }" />  
					<masterInput:searchInputField label="label.participant.phone" name="phone" value="${participant.phone }" />  

			
			<input type="hidden" name="currentPage" id="currentPage" value="<c:out   escapeXml="false" value="${currentFormBean.currentPage}"/>" />
			<input type="hidden" name="orderColumn" id="orderColumn" value="<c:out   escapeXml="false" value="${currentFormBean.orderColumn}"/>" />
			<input type="hidden" name="orderAsc" id="orderAsc" value="<c:out   escapeXml="false" value="${currentFormBean.orderAsc}"/>" />
			<input type="hidden" name="restartPagging" id="restartPagging" value="true"/>
			<masterInput:mastertokens/>
			
			<input type="submit" value="Submit" id="submitSearchBtn" style="display:none" />
		</form:form>
	</div>
</div>
<span id="PAGE_TOOLBAR">
	<c:if test="${util:isUrlVisible('/app/admin/participant/add') }">
	<button type="button" id="_addBtn" class="toolbarButton" onclick="return gotoRelativeUrl('/app/admin/participant/add')" style="float:right !important;"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span><spring:message code="button.add"/></button>
	</c:if>
	<button type="button" id="_searchBtn" class="toolbarButton" onclick="return submitDefaultForm()"><span class="glyphicon glyphicon-search" aria-hidden="true"></span><spring:message code="button.search"/></button>
</span>
<span id="PAGE_TITLE"><spring:message code="title.participant.list"/></span>


<c:if test="${searchResults!=null }">
	<c:set value="${searchResults}" var="pagingData" scope="request"/>
	<c:import url="/WEB-INF/views/pagingHeader.jsp"/>
	<c:remove var="pagingData" scope="request"/>
	
	<table class="list _masterRespTable">
		<thead>
		<tr>
			<th _col="1" onclick="return applySearchOrder(this)"><spring:message code="label.participant.id" /></th>
			<th _col="2" onclick="return applySearchOrder(this)"><spring:message code="label.participant.name" /></th>
			<th _col="3" onclick="return applySearchOrder(this)"><spring:message code="label.participant.adrress" /></th>
			<th _col="4" onclick="return applySearchOrder(this)"><spring:message code="label.participant.city" /></th>
			<th _col="5" onclick="return applySearchOrder(this)"><spring:message code="label.participant.phone" /></th>

		</tr>
		</thead>
		<tbody>
		<c:forEach items="${searchResults.results}" var="item" varStatus="loopStatus">
			<tr class="${loopStatus.index % 2 == 0 ? 'even' : 'odd'}">
				<td class="numberAlign"><a class="activeLink" onclick="return showLoadingDiv()" href="<c:url value="/app/admin/participant/view/${item.id}"/>"><c:out  value="${item.id}" /></a></td>
				<td><c:out value="${item.name}" /></td>
				<td><c:out value="${item.adrress}" /></td>
				<td><c:out value="${item.city}" /></td>
				<td><c:out value="${item.phone}" /></td>

			</tr>
		</c:forEach>
		</tbody>
	</table>
</c:if>