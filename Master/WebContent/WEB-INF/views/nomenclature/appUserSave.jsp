<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="masterInput" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="util" uri="/WEB-INF/tlds/util-functions.tld"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<script type="text/javascript">
var defaultFormName = "saveAppUserForm";
var defaultSubmitBtnName = "btnSubmit";
$(document).ready(function(){
	
	$("#userType").change(function() {
		var val = $(this).val();
		if (val == 1) {
			$("#tab2").show();
			$("#participantFk_ROW_DIV").show();
			$("#participantFk").change();
		} else {
			$("#tab2").hide();
			$("#participantFk_ROW_DIV").hide();
		}
	});
	
	$("#participantFk").change(function() {
		var participantFk = $(this).val();
		var action = appPath + "/app/appUser/roles/" + participantFk;
		
		$.ajax({
	   		type: 'GET',
	   		url: action,
	   		async: false, 
	   		success: function(data) {
	   			$("#tabs-2").html(data);
	   		}
	   	});
		
		var action2 = appPath + "/app/appUser/view/passPolicy";
		if (participantFk != null && participantFk != '')
			action2 += "/" + participantFk;
		
		setTimeout(function() {
			$.ajax({
		   		type: 'GET',
		   		url: action2,
		   		success: function(data) {
		   			$("#passPolicyFk").html("");
		   			for (var i = 0; i < data.length; i++) {
		   				$("#passPolicyFk").append("<option value='" + data[i].value + "'>" + data[i].label + "</option>");	
		   			}
		   		}
		   	});
		}, 100);
	});
	
	<c:if test="${hideRoles}">
		$("#tabs-2").html("");
		$("#tab2").hide();
		$("#participantFk_ROW_DIV").hide();
	</c:if>
	
});
function checkForm() {
	$('#assignedRoles option').prop('selected', true);
	return submitDefaultForm();
}
</script>

<c:choose>
	<c:when test="${!util:isUrlVisible('/app/appUser/save') }">
		<spring:message code="title.appUser.view" var="_pageTitle"/>
		<c:set var="_pageReadOnly" value="true" scope="request"/>
	</c:when>
	<c:when test="${requestScope[Constants.ACTION]==Constants.ACTION_ADD}">
		<spring:message code="button.insert" var="_btnLabel"/>
		<spring:message code="title.appUser.add" var="_pageTitle"/>
	</c:when>
	<c:otherwise>
		<spring:message code="button.update" var="_btnLabel"/>
		<spring:message code="title.appUser.update" var="_pageTitle"/>
	</c:otherwise> 
</c:choose>

<c:url value="/app/appUser/save" var="_action"/>
<form:form action="${_action }" modelAttribute="appUser" method="POST" id="saveAppUserForm" name="saveAppUserForm">
<c:set value="${appUser}" var="currentFormBean" scope="request"/>

	<div id="tabs">
		<ul>
			<li><a href="#tabs-1"><spring:message code="label.appUser.userDetails" /></a></li>
			<c:if test="${!(requestScope[Constants.ACTION]==Constants.ACTION_ADD && !util:isUrlVisible('/app/appRole/add')) }">
				<li id="tab2"><a href="#tabs-2"><spring:message code="label.appUser.userRoles" /></a></li>
			</c:if>
		</ul>
		
		<div id="tabs-1">
		
			<masterInput:selectField fieldClass="field30perc" label="label.appUser.userType" name="userType" required="true" items="${ userTypeList }" value="${ appUser.userType }" size="1" convertToReadOnly="${ requestScope[Constants.ACTION]!=Constants.ACTION_ADD }" hiddenRow="${ sessionScope[Constants.LOGGED_USER].user }" />
			<sec:authorize access="hasRole('ROLE_ADMIN')">
				<masterInput:selectField fieldClass="field30perc" label="label.appUser.participantFk" name="participantFk" value="${ appUser.participantFk }" items="${ participantList }" size="1" required="true" convertToReadOnly="${ requestScope[Constants.ACTION]!=Constants.ACTION_ADD }"  />
			</sec:authorize>
			<masterInput:inputField fieldClass="field30perc" label="label.appUser.firstName" name="firstName"  required="true"  />
			<masterInput:inputField fieldClass="field30perc" label="label.appUser.lastName" name="lastName"  required="true"  />
			<masterInput:inputField fieldClass="field30perc" label="label.appUser.username" name="username" required="true" convertToReadOnly="${ requestScope[Constants.ACTION]!=Constants.ACTION_ADD }" />
			<masterInput:inputField inputType="password" fieldClass="field30perc" label="label.appUser.password" name="password" required="true"  />
			<masterInput:inputField inputType="password" fieldClass="field30perc" label="label.appUser.repeatPassword" name="repeatPassword" required="true"  />
		
			<masterInput:selectField fieldClass="field30perc" label="label.appUser.status" name="status" required="true" items="${ userStatusList }" value="${ appUser.status }"  size="1" />
			<masterInput:selectField fieldClass="field30perc" label="label.appUser.passPolicyFk" name="passPolicyFk" value="${ appUser.passPolicyFk }" items="${ passPolicyList }" size="1" required="false"  />	
	
		</div>
		<sec:authorize access="${!(requestScope[Constants.ACTION]==Constants.ACTION_ADD && !util:isUrlVisible('/app/appRole/add')) }">
			<div id="tabs-2">
				<jsp:include page="appUserSaveRole.jsp" />
			</div>
		</sec:authorize>
	</div>
	<masterInput:mastertokens/>
	<form:hidden path="id"/>
	<form:hidden path="version"/>
	<input type="submit" value="Submit" id="btnSubmit" style="display:none" />
</form:form>


<span id="PAGE_TOOLBAR">
	<c:if test="${util:isUrlVisible('/app/appUser/save') }">
		<button type="button" class="toolbarButton" onclick="return checkForm()"><c:out   escapeXml="false" value="${_btnLabel }"/></button>
	</c:if>
	<c:if test="${util:isUrlVisible('/app/appUser/delete') }">
		<c:if test="${requestScope[Constants.ACTION]!=Constants.ACTION_ADD}">
			<button type="button" class="toolbarButton" onclick="return deleteItem('/app/appUser/delete');"><spring:message code="button.delete"/></button>
		</c:if>
	</c:if>
	<button style="float:right" type="button" class="toolbarButton" onclick="return gotoRelativeUrl('/app/appUser/list');"><spring:message code="button.list"/></button>
	
	<c:if test="${util:isUrlVisible('/app/appUser/add') }">
		<c:if test="${requestScope[Constants.ACTION]!=Constants.ACTION_ADD}">
			<button style="float:right" type="button" class="toolbarButton" onclick="return gotoRelativeUrl('/app/appUser/add')"><spring:message code="button.add"/></button>
		</c:if>
	</c:if>
</span>
<span id="PAGE_TITLE">
	<spring:message code="${_pageTitle}"/>
</span>