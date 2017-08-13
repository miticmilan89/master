<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="masterInput" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="util" uri="/WEB-INF/tlds/util-functions.tld"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<script type="text/javascript">
var defaultFormName = "saveAppRoleForm";
var defaultSubmitBtnName = "btnSubmit";

$(document).ready(function(){
	$(".includeBtn").click(function(){
		 $("#unassignedFunctions option:selected").each(function() {
			 $(this).detach().prependTo("#assignedFunctions").prop('value', $(this).prop('value'));
		     $(this).removeAttr('selected');
		 });
	});
	
	$(".removeBtn").click(function(){
		$("#assignedFunctions option:selected").each(function() {
		     $(this).detach().prependTo("#unassignedFunctions").prop('value', $(this).prop('value'));
		     $(this).removeAttr('selected');
		 });
	});
	
	$(".includeAllBtn").click(function(){
		 $("#unassignedFunctions option").each(function() {
		      $(this).detach().appendTo("#assignedFunctions").prop('value', $(this).prop('value'));
		      $(this).removeAttr('selected');
		 });
	});
	
	$(".removeAllBtn").click(function(){
		$("#assignedFunctions option").each(function() {
		      $(this).detach().appendTo("#unassignedFunctions").prop('value', $(this).prop('value'));
		      $(this).removeAttr('selected');
		 });
	});

	<c:if test="${!util:isUrlVisible('/app/appRole/save') }">
		$('#assignedFunctions').prop('disabled', true);
	</c:if>

});

function checkForm() {
	$('#assignedFunctions option').prop('selected', true);
	return submitDefaultForm();
}

</script>

<c:choose>
	<c:when test="${!util:isUrlVisible('/app/appRole/save') }">
		<spring:message code="title.appRole.view" var="_pageTitle"/>
		<c:set var="_pageReadOnly" value="true" scope="request"/>
	</c:when>
	<c:when test="${requestScope[Constants.ACTION]==Constants.ACTION_ADD}">
		<spring:message code="button.insert" var="_btnLabel"/>
		<spring:message code="title.appRole.add" var="_pageTitle"/>
	</c:when>
	<c:otherwise>
		<spring:message code="button.update" var="_btnLabel"/>
		<spring:message code="title.appRole.update" var="_pageTitle"/>
	</c:otherwise> 
</c:choose>

<c:url value="/app/appRole/save" var="_action"/>
<form:form action="${_action }" modelAttribute="appRole" method="POST" id="saveAppRoleForm" name="saveAppRoleForm">
<c:set value="${appRole}" var="currentFormBean" scope="request"/>

	

		<masterInput:inputField label="label.appRole.roleName" name="roleName" fieldClass="field40perc" required="true" />
		<sec:authorize access="hasRole('ROLE_ADMIN')">
			<masterInput:selectField label="label.appRole.participantFk" name="participantFk" items="${ participantList }"   size="1"  fieldClass="field30perc" required="true" />
		</sec:authorize>
		<div class="row" >
			<div class="column-data field100perc">
				<c:if test="${util:isUrlVisible('/app/appRole/save') }">
				
					<div style="float:left">
						<spring:message code="label.appRole.notAssignedFunctions" />:<br/>
						<select id="unassignedFunctions" size = "20" style="min-width:250px;" multiple="multiple">
							<c:forEach var="function" items="${notAssignedFunctions}">
								<option value="${function.id}"><spring:message code="label.appFunction.${function.functionName}" /></option>
							</c:forEach>	
						</select>	
					</div>
					
					<div class="sbMoveDiv">	
							<input type="button" value="&#187;" class="includeAllBtn sbMoveButton" style="width:40px;float:left">
							<input type="button" value="&#155;" class="includeBtn sbMoveButton" style="width:40px;float:left">				
							<input type="button" value="&#139;"  class="removeBtn sbMoveButton" style="width:40px;float:left">
							<input type="button" value="&#171;" class="removeAllBtn sbMoveButton" style="width:40px;float:left">
					</div>
				</c:if>
				
				<div style="float:left">
					<spring:message code="label.appRole.assignedFunctions" />:<br/>
					<select id="assignedFunctions" name="assignedFunctions" size="20" style="min-width:250px;" multiple="multiple" class="selectAll" oldval=".">
					<c:if test="${assignedFunctions != null}">
						<c:forEach var="function" items="${assignedFunctions}">
							<option  value="${function.id}"><spring:message code="label.appFunction.${function.functionName}" /></option>
							<script>$(document).ready(function() { $("#assignedFunctions").attr("oldval", $("#assignedFunctions").attr("oldval") + "${function.id}" + "."  ); });</script>
						</c:forEach>
					</c:if>
					</select>
				</div>
			</div>
		</div>

	<masterInput:mastertokens/>
	<form:hidden path="id"/>
	<form:hidden path="version"/>
	<input type="submit" value="Submit" id="btnSubmit" style="display:none" />
</form:form>


<span id="PAGE_TOOLBAR">
	<c:if test="${requestScope._pageReadOnly!='true' && util:isUrlVisible('/app/appRole/save') }">
		<button type="button" id="_saveBtn" class="toolbarButton" onclick="return checkForm()"><span class="${_btnIcon}" aria-hidden="true"></span><c:out   escapeXml="false" value="${_btnLabel }"/></button>
	</c:if>
	<c:if test="${requestScope._pageReadOnly!='true' && util:isUrlVisible('/app/appRole/delete') }">
		<c:if test="${requestScope[Constants.ACTION]!=Constants.ACTION_ADD}">
			<button type="button" id="_deleteBtn" class="toolbarButton" onclick="return deleteItem('/app/appRole/delete');"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span><spring:message code="button.delete"/></button>
		</c:if>
	</c:if>
	<button style="float:right" id="_listBtn" type="button" class="toolbarButton" onclick="return gotoRelativeUrl('/app/appRole/list?_mnu=2');"><span class="glyphicon glyphicon glyphicon-list-alt" aria-hidden="true"></span><spring:message code="button.list"/></button>
	<c:if test="${requestScope._pageReadOnly!='true' && util:isUrlVisible('/app/appRole/add') }">
		<c:if test="${requestScope[Constants.ACTION]!=Constants.ACTION_ADD}">
			<button style="float:right" id="_addBtn" type="button" class="toolbarButton" onclick="return gotoRelativeUrl('/app/appRole/add')"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span><spring:message code="button.add"/></button>
		</c:if>
	</c:if>
</span>
<span id="PAGE_TITLE">
	<spring:message code="${_pageTitle}"/>
</span>