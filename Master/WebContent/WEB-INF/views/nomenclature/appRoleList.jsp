<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %><%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@ taglib prefix="masterInput" tagdir="/WEB-INF/tags" %><%@ taglib prefix="util" uri="/WEB-INF/tlds/util-functions.tld"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<script type="text/javascript">
var defaultFormName = "filterForm";
var defaultSubmitBtnName = "submitSearchBtn";
$(document).ready(function() {
	$(".accordionFilter").accordion({
		heightStyle: "content",
		collapsible: true,
		active: <c:choose><c:when test="${ util:isFilterOpen(sessionScope.filters, 'appRoleFilter') }" >0</c:when><c:otherwise>false</c:otherwise></c:choose>
	});
});


</script>
<div class="accordionFilter">
	<h3 onclick="showHideFilter('appRoleFilter')"><span id="appRoleFilter" class="searchFilter" style="visibility:hidden; text-align:left;">
		<c:choose><c:when test="${ util:isFilterOpen(sessionScope.filters, 'appRoleFilter') }" >+</c:when><c:otherwise>-</c:otherwise></c:choose>	
		</span><spring:message code="label.filter" />
	</h3>

	<div>
		<c:url value="/app/appRole/list" var="_action"/>
		<form:form action="${_action }" modelAttribute="appRole" method="POST" id="filterForm" name="filterForm">
			<c:set value="${appRole}" var="currentFormBean" scope="request"/>
			
			<masterInput:searchInputField label="label.appRole.roleName" name="roleName" value="${appRole.roleName }" fieldClass="field30perc" />  
			<sec:authorize access="hasRole('ROLE_ADMIN')">
				<masterInput:searchSelectField label="label.appRole.participantFk" name="participantFk" required="false" items="${ participantList }"   size="1" fieldClass="field30perc" />
			</sec:authorize>		
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
	<c:if test="${util:isUrlVisible('/app/appRole/add') }">
	<button type="button" id="_addBtn" class="toolbarButton" onclick="return gotoRelativeUrl('/app/appRole/add')" style="float:right !important;"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span><spring:message code="button.add"/></button>
	</c:if>
	<button type="button" id="_searchBtn" class="toolbarButton" onclick="return submitDefaultForm()"><span class="glyphicon glyphicon-search" aria-hidden="true"></span><spring:message code="button.search"/></button>
</span>
<span id="PAGE_TITLE"><spring:message code="title.appRole.list"/></span>


<c:if test="${searchResults!=null }">
	<c:set value="${searchResults}" var="pagingData" scope="request"/>
	<c:import url="/WEB-INF/views/pagingHeader.jsp"/>
	<c:remove var="pagingData" scope="request"/>
	
	<table class="list _masterRespTable">
		<thead>
		<tr>
			<th _col="1" onclick="return applySearchOrder(this)"><spring:message code="label.appRole.id" /></th>
			<th _col="2" onclick="return applySearchOrder(this)"><spring:message code="label.appRole.roleName" /></th>
			<sec:authorize access="hasRole('ROLE_ADMIN')">
				<th _col="3" onclick="return applySearchOrder(this)"><spring:message code="label.appRole.participantFk" /></th>
			</sec:authorize>		
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${searchResults.results}" var="item" varStatus="loopStatus">
			<tr class="${loopStatus.index % 2 == 0 ? 'even' : 'odd'}">
				<td class="numberAlign"><a class="activeLink" onclick="return showLoadingDiv()" href="<c:url value="/app/appRole/view/${item.id}"/>"><c:out  value="${item.id}" /></a></td>
				<td><c:out value="${item.roleName}" /></td>
				<sec:authorize access="hasRole('ROLE_ADMIN')">
					<td class="numberAlign"><c:out value="${item.participant.name}" /></td>
				</sec:authorize>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</c:if>