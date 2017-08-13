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
		active: <c:choose><c:when test="${ util:isFilterOpen(sessionScope.filters, 'appUserSf') }" >0</c:when><c:otherwise>false</c:otherwise></c:choose>
	});
});

</script>

<div class="accordionFilter">
	<h3 onclick="showHideFilter('appUserSf')"><span id="appUserSf" class="searchFilter" style="visibility:hidden; text-align:left;">
		<c:choose><c:when test="${ util:isFilterOpen(sessionScope.filters, 'appUserSf') }" >+</c:when><c:otherwise>-</c:otherwise></c:choose>	
		</span><spring:message code="label.filter" />
	</h3>
	<div>
		<c:url value="/app/passPolicy/list" var="_action"/>
		<form:form action="${_action }" class="form-horizontal" modelAttribute="passPolicy" method="POST" id="filterForm" name="filterForm">
			<c:set value="${passPolicy}" var="currentFormBean" scope="request"/>
			
			<masterInput:searchInputField fieldClass="field30perc" label="label.passPolicy.name" name="name" value="${passPolicy.name }" />  
			<sec:authorize access="hasRole('ROLE_ADMIN')">
				<masterInput:searchSelectField fieldClass="field30perc" label="label.appUser.participantFk" name="participantFk" required="false" items="${ participantList }"   size="1" />
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
	<c:if test="${util:isUrlVisible('/app/passPolicy/add') }">
	<button type="button" class="toolbarButton" onclick="return gotoRelativeUrl('/app/passPolicy/add')" style="float:right !important;"><spring:message code="button.add"/></button>
	</c:if>
	<button type="button" class="toolbarButton" onclick="return submitDefaultForm()"><spring:message code="button.search"/></button>
</span>
<span id="PAGE_TITLE">
	<spring:message code="title.passPolicy.list"/>
</span>


<c:if test="${searchResults!=null }">
<div class="panel">
	<table class="list _masterRespTable">
		<thead>
		<tr>
			<th _col="1" onclick="return applySearchOrder(this)"><spring:message code="label.passPolicy.id" /></th>
			<th _col="2" onclick="return applySearchOrder(this)"><spring:message code="label.passPolicy.name" /></th>
			<sec:authorize access="hasRole('ROLE_ADMIN')">
				<th _col="15" onclick="return applySearchOrder(this)"><spring:message code="label.passPolicy.participantFk" /></th>
			</sec:authorize>
			<th _col="16" onclick="return applySearchOrder(this)"><spring:message code="label.passPolicy.changedByAppUserFk" /></th>
			<th _col="17" onclick="return applySearchOrder(this)"><spring:message code="label.passPolicy.changedDt" /></th>

		</tr>
		</thead>
		<tbody>
		<c:forEach items="${searchResults.results}" var="item" varStatus="loopStatus">
			<tr class="${loopStatus.index % 2 == 0 ? 'even' : 'odd'}">
				<td class="numberAlign"><a class="activeLink" onclick="return showLoadingDiv()" href="<c:url value="/app/passPolicy/view/${item.id}"/>"><c:out  value="${item.id}" /></a></td>
				<td><c:out value="${item.name}" /></td>
				<sec:authorize access="hasRole('ROLE_ADMIN')">
					<td><c:out value="${item.participant.name}" /></td>
				</sec:authorize>
				<td class="numberAlign"><c:out value="${item.changedByAppUser.username}" /></td>
				<td><util:formatDate type="both"  value="${item.changedDt}" /></td>

			</tr>
		</c:forEach>
		</tbody>
	</table>
</div>
</c:if>