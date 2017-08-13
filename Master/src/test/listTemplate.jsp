<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %><%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@ taglib prefix="masterInput" tagdir="/WEB-INF/tags" %><%@ taglib prefix="util" uri="/WEB-INF/tlds/util-functions.tld"%>

<script type="text/javascript">
var defaultFormName = "filterForm";
var defaultSubmitBtnName = "submitSearchBtn";
$(document).ready(function() {
	$(".accordionFilter").accordion({
		heightStyle: "content",
		collapsible: true,
		active: <c:choose><c:when test="${ util:isFilterOpen(sessionScope.filters, '|entitySmallFirstLetter|Filter') }" >0</c:when><c:otherwise>false</c:otherwise></c:choose>
	});
});


</script>
<div class="accordionFilter">
	<h3 onclick="showHideFilter('|entitySmallFirstLetter|Filter')"><span id="|entitySmallFirstLetter|Filter" class="searchFilter" style="visibility:hidden; text-align:left;">
		<c:choose><c:when test="${ util:isFilterOpen(sessionScope.filters, '|entitySmallFirstLetter|Filter') }" >+</c:when><c:otherwise>-</c:otherwise></c:choose>	
		</span><spring:message code="label.filter" />
	</h3>

	<div>
		<c:url value="/app/|entitySmallFirstLetter|/list" var="_action"/>
		<form:form action="${_action }" modelAttribute="|entitySmallFirstLetter|" method="POST" id="filterForm" name="filterForm">
			<c:set value="${|entitySmallFirstLetter|}" var="currentFormBean" scope="request"/>

|searchFields|
			
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
	<c:if test="${util:isUrlVisible('/app/|entitySmallFirstLetter|/add') }">
	<button type="button" id="_addBtn" class="toolbarButton" onclick="return gotoRelativeUrl('/app/|entitySmallFirstLetter|/add')" style="float:right !important;"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span><spring:message code="button.add"/></button>
	</c:if>
	<button type="button" id="_searchBtn" class="toolbarButton" onclick="return submitDefaultForm()"><span class="glyphicon glyphicon-search" aria-hidden="true"></span><spring:message code="button.search"/></button>
</span>
<span id="PAGE_TITLE"><spring:message code="title.|entitySmallFirstLetter|.list"/></span>


<c:if test="${searchResults!=null }">
	<c:set value="${searchResults}" var="pagingData" scope="request"/>
	<c:import url="/WEB-INF/views/pagingHeader.jsp"/>
	<c:remove var="pagingData" scope="request"/>
	
	<table class="list _masterRespTable">
		<thead>
		<tr>
|tableHeader|
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${searchResults.results}" var="item" varStatus="loopStatus">
			<tr class="${loopStatus.index % 2 == 0 ? 'even' : 'odd'}">
|tableColumn|
			</tr>
		</c:forEach>
		</tbody>
	</table>
</c:if>