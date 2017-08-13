<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="masterInput" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="util" uri="/WEB-INF/tlds/util-functions.tld"%>

<script type="text/javascript">
var defaultFormName = "save|entity|Form";
var defaultSubmitBtnName = "btnSubmit";
</script>

<c:choose>
	<c:when test="${!util:isUrlVisible('/app/|entitySmallFirstLetter|/save') }">
		<spring:message code="title.|entitySmallFirstLetter|.view" var="_pageTitle"/>
		<c:set var="_pageReadOnly" value="true" scope="request"/>
	</c:when>
	<c:when test="${requestScope[Constants.ACTION]==Constants.ACTION_ADD}">
		<spring:message code="button.insert" var="_btnLabel"/>
		<spring:message code="title.|entitySmallFirstLetter|.add" var="_pageTitle"/>
		<c:set var="_btnIcon" value="glyphicon glyphicon-floppy-disk" scope="request"/>
	</c:when>
	<c:otherwise>
		<spring:message code="button.update" var="_btnLabel"/>
		<spring:message code="title.|entitySmallFirstLetter|.update" var="_pageTitle"/>
		<c:set var="_btnIcon" value="glyphicon glyphicon-floppy-save" scope="request"/>
	</c:otherwise> 
</c:choose>

<c:url value="/app/|entitySmallFirstLetter|/save" var="_action"/>
<form:form action="${_action }" modelAttribute="|entitySmallFirstLetter|" method="POST" id="save|entity|Form" name="save|entity|Form">
<c:set value="${|entitySmallFirstLetter|}" var="currentFormBean" scope="request"/>

|inputFields|
	
	<masterInput:mastertokens/>
	<form:hidden path="id"/>
	<form:hidden path="version"/>
	<input type="submit" value="Submit" id="btnSubmit" style="display:none" />
</form:form>


<span id="PAGE_TOOLBAR">
	<c:if test="${requestScope._pageReadOnly!='true' && util:isUrlVisible('/app/|entitySmallFirstLetter|/save') }">
		<button type="button" id="_saveBtn" class="toolbarButton" onclick="return submitDefaultForm()"><span class="${_btnIcon}" aria-hidden="true"></span><c:out   escapeXml="false" value="${_btnLabel }"/></button>
	</c:if>
	<c:if test="${requestScope._pageReadOnly!='true' && util:isUrlVisible('/app/|entitySmallFirstLetter|/delete') }">
		<c:if test="${requestScope[Constants.ACTION]!=Constants.ACTION_ADD}">
			<button type="button" id="_deleteBtn" class="toolbarButton" onclick="return deleteItem('/app/|entitySmallFirstLetter|/delete');"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span><spring:message code="button.delete"/></button>
		</c:if>
	</c:if>
	<button style="float:right" id="_listBtn" type="button" class="toolbarButton" onclick="return gotoRelativeUrl('/app/|entitySmallFirstLetter|/list?_mnu=2');"><span class="glyphicon glyphicon glyphicon-list-alt" aria-hidden="true"></span><spring:message code="button.list"/></button>
	<c:if test="${requestScope._pageReadOnly!='true' && util:isUrlVisible('/app/|entitySmallFirstLetter|/add') }">
		<c:if test="${requestScope[Constants.ACTION]!=Constants.ACTION_ADD}">
			<button style="float:right" id="_addBtn" type="button" class="toolbarButton" onclick="return gotoRelativeUrl('/app/|entitySmallFirstLetter|/add')"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span><spring:message code="button.add"/></button>
		</c:if>
	</c:if>
</span>
<span id="PAGE_TITLE">
	<spring:message code="${_pageTitle}"/>
</span>