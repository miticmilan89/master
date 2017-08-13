<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="masterInput" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="util" uri="/WEB-INF/tlds/util-functions.tld"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<script type="text/javascript">
var defaultFormName = "savePassPolicyForm";
var defaultSubmitBtnName = "btnSubmit";
</script>

<c:choose>
	<c:when test="${!util:isUrlVisible('/app/passPolicy/save') }">
		<spring:message code="title.passPolicy.view" var="_pageTitle"/>
		<c:set var="_pageReadOnly" value="true" scope="request"/>
	</c:when>
	<c:when test="${requestScope[Constants.ACTION]==Constants.ACTION_ADD}">
		<spring:message code="button.insert" var="_btnLabel"/>
		<spring:message code="title.passPolicy.add" var="_pageTitle"/>
	</c:when>
	<c:otherwise>
		<spring:message code="button.update" var="_btnLabel"/>
		<spring:message code="title.passPolicy.update" var="_pageTitle"/>
	</c:otherwise> 
</c:choose>

<div id="tab-1" class="tab-pane active" >
	
	<div class="panel col-md-6 col-lg-4">
		
		<div class="panel-content">
			<c:url value="/app/passPolicy/save" var="_action"/>
			<form:form action="${_action }" class="form-horizontal" modelAttribute="passPolicy" method="POST" id="savePassPolicyForm" name="savePassPolicyForm">
			<c:set value="${passPolicy}" var="currentFormBean" scope="request"/>
			
					<masterInput:inputField fieldClass="field30perc"  label="label.passPolicy.name" name="name" required="true"  />
					<sec:authorize access="hasRole('ROLE_ADMIN')">
						<masterInput:selectField fieldClass="field30perc" label="label.appUser.participantFk" name="participantFk" items="${ participantList }"   size="1" />
					</sec:authorize>	
					<masterInput:inputField fieldClass="field30perc"  label="label.passPolicy.passLoginAttempt" name="passLoginAttempt"  />
					<masterInput:inputField fieldClass="field30perc"  label="label.passPolicy.passMinPeriodInDays" name="passMinPeriodInDays"  />
					<masterInput:inputField fieldClass="field30perc"  label="label.passPolicy.passMaxPeriodInDays" name="passMaxPeriodInDays"  />
					<masterInput:inputField fieldClass="field30perc"  label="label.passPolicy.passMinLength" name="passMinLength"  />
					<masterInput:inputField fieldClass="field30perc"  label="label.passPolicy.passMinHistoryRepeat" name="passMinHistoryRepeat"  />
					<masterInput:selectField fieldClass="field30perc"  label="label.passPolicy.passMustHaveLowercase" name="passMustHaveLowercase" items="${ yesNoSelect }" size="1" />
					<masterInput:selectField fieldClass="field30perc"  label="label.passPolicy.passMustHaveNumber" name="passMustHaveNumber" items="${ yesNoSelect }" size="1" />
					<masterInput:selectField fieldClass="field30perc"  label="label.passPolicy.passMustHaveUppercase" name="passMustHaveUppercase" items="${ yesNoSelect }" size="1" />
					<masterInput:selectField fieldClass="field30perc"  label="label.passPolicy.passMustHaveSpecialChars" name="passMustHaveSpecialChars" items="${ yesNoSelect }" size="1" />
					<masterInput:selectField fieldClass="field30perc"  label="label.passPolicy.passUnblockAutomatically" name="passUnblockAutomatically" items="${ yesNoSelect }" size="1" />
					<masterInput:inputField fieldClass="field30perc"  label="label.passPolicy.passSpecialChars" name="passSpecialChars"  />
					<masterInput:inputField fieldClass="field30perc"  label="label.passPolicy.passBlockWaitTime" name="passBlockWaitTime"  />
				
				<masterInput:mastertokens/>
				<form:hidden path="id"/>
				<form:hidden path="version"/>
				<input type="submit" value="Submit" id="btnSubmit" style="display:none" />
			</form:form>
		</div>
	</div>
</div>

<span id="PAGE_TOOLBAR">
	<c:if test="${util:isUrlVisible('/app/passPolicy/save') }">
		<button type="button" class="toolbarButton" onclick="return submitDefaultForm()"><c:out   escapeXml="false" value="${_btnLabel }"/></button>
	</c:if>
	<c:if test="${util:isUrlVisible('/app/passPolicy/delete') }">
		<c:if test="${requestScope[Constants.ACTION]!=Constants.ACTION_ADD}">
			<button type="button" class="toolbarButton" onclick="return deleteItem('/app/passPolicy/delete');"><spring:message code="button.delete"/></button>
		</c:if>
	</c:if>
	<button style="float:right" type="button" class="toolbarButton" onclick="return gotoRelativeUrl('/app/passPolicy/list');"><spring:message code="button.list"/></button>
	
	<c:if test="${util:isUrlVisible('/app/passPolicy/add') }">
		<c:if test="${requestScope[Constants.ACTION]!=Constants.ACTION_ADD}">
			<button style="float:right" type="button" class="toolbarButton" onclick="return gotoRelativeUrl('/app/passPolicy/add')"><spring:message code="button.add"/></button>
		</c:if>
	</c:if>
</span>
<span id="PAGE_TITLE">
	<spring:message code="${_pageTitle}"/>
</span>