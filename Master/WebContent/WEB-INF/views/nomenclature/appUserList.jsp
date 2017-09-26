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
		active: <c:choose><c:when test="${ util:isFilterOpen(sessionScope.filters, 'appUserSF') }" >0</c:when><c:otherwise>false</c:otherwise></c:choose>
	});
});


</script>
<div class="accordionFilter">
	<h3 onclick="showHideFilter('appUserSF')"><span id="appUserSF" class="searchFilter" style="visibility:hidden; text-align:left;">
		<c:choose><c:when test="${ util:isFilterOpen(sessionScope.filters, 'appUserSF') }" >+</c:when><c:otherwise>-</c:otherwise></c:choose>	
		</span><spring:message code="label.filter" />
	</h3>

	<div>
		<c:url value="/app/appUser/list" var="_action"/>
		<form:form action="${_action }" modelAttribute="appUser" method="POST" id="filterForm" name="filterForm">
			<c:set value="${appUser}" var="currentFormBean" scope="request"/>
			<masterInput:searchInputField fieldClass="field30perc" label="label.appUser.firstName" name="firstName" value="${appUser.firstName }" /> 
			<masterInput:searchInputField fieldClass="field30perc" label="label.appUser.lastName" name="lastName" value="${appUser.lastName }" /> 
			<masterInput:searchInputField fieldClass="field30perc" label="label.appUser.username" name="username" value="${appUser.username }" /> 
			
			<input type="hidden" name="currentPage" id="currentPage" value="<c:out   escapeXml="false" value="${appUser.currentPage}"/>" />
			<input type="hidden" name="orderColumn" id="orderColumn" value="<c:out   escapeXml="false" value="${appUser.orderColumn}"/>" />
			<input type="hidden" name="orderAsc" id="orderAsc" value="<c:out   escapeXml="false" value="${appUser.orderAsc}"/>" />
			<input type="hidden" name="restartPagging" id="restartPagging" value="true"/>
			
			<masterInput:mastertokens/>
			
			<input type="submit" value="Submit" id="submitSearchBtn" style="display:none" />
		</form:form>
	</div>
</div>
<span id="PAGE_TOOLBAR">
	<c:if test="${util:isUrlVisible('/app/appUser/add') }">
	<button type="button" class="toolbarButton" onclick="return gotoRelativeUrl('/app/appUser/add')" style="float:right !important;"><spring:message code="button.add"/></button>
	</c:if>
	<button type="button" class="toolbarButton" onclick="return submitDefaultForm()"><spring:message code="button.search"/></button>
</span>
<span id="PAGE_TITLE">
	<spring:message code="title.appUser.list"/>
</span>


<c:if test="${searchResults!=null }">
	<c:set value="${searchResults}" var="pagingData" scope="request"/>
	<c:import url="/WEB-INF/views/pagingHeader.jsp"/>
	<c:remove var="pagingData" scope="request"/>
	
	<table class="list _masterRespTable">
		<thead>
		<tr>
			<th _col="1" onclick="return applySearchOrder(this)"><spring:message code="label.appUser.id" /></th>
			<th _col="2" onclick="return applySearchOrder(this)"><spring:message code="label.appUser.firstName" /></th>
			<th _col="3" onclick="return applySearchOrder(this)"><spring:message code="label.appUser.lastName" /></th>
			<th _col="4" onclick="return applySearchOrder(this)"><spring:message code="label.appUser.username" /></th>
			<th _col="5" onclick="return applySearchOrder(this)"><spring:message code="label.appUser.status" /></th>
			<sec:authorize access="hasRole('ROLE_ADMIN')">
				<th _col="6" onclick="return applySearchOrder(this)"><spring:message code="label.participant.name" /></th>
			</sec:authorize>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${searchResults.results}" var="item" varStatus="loopStatus">
			<tr class="${loopStatus.index % 2 == 0 ? 'even' : 'odd'}">
				<td><a class="activeLink" onclick="return showLoadingDiv()" href="<c:url value="/app/appUser/view/${item.id}"/>"><c:out   escapeXml="false" value="${item.id}" /></a></td>
				<td><c:out   escapeXml="false" value="${item.firstName}" /></td>
				<td><c:out   escapeXml="false" value="${item.lastName}" /></td>
				<td><c:out   escapeXml="false" value="${item.username}" /></td>
				<td>
					<c:choose>
						<c:when test="${item.active}"><spring:message  code="label.appUser.status.${item.status}" /></c:when>
						<c:when test="${!item.active}"><span style="color:red"><spring:message  code="label.appUser.status.${item.status}" /></span></c:when>
					</c:choose>
				</td>
				<sec:authorize access="hasRole('ROLE_ADMIN')">
					<td><c:out   escapeXml="false" value="${item.participant.name}" /></td>
				</sec:authorize>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</c:if>
