<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="masterInput" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="util" uri="/WEB-INF/tlds/util-functions.tld"%>

<script>
$(document).ready(function() {

	$(".includeBtn").click(function(){
		 $("#unassignedRoles option:selected").each(function() {
			 $(this).detach().prependTo("#assignedRoles").prop('value', $(this).prop('value'));
		     $(this).removeAttr('selected');
		 });
	});
	
	$(".removeBtn").click(function(){
		$("#assignedRoles option:selected").each(function() {
		     $(this).detach().prependTo("#unassignedRoles").prop('value', $(this).prop('value'));
		     $(this).removeAttr('selected');
		 });
	});
	
	$(".includeAllBtn").click(function(){
		 $("#unassignedRoles option").each(function() {
		      $(this).detach().appendTo("#assignedRoles").prop('value', $(this).prop('value'));
		      $(this).removeAttr('selected');
		 });
	});
	
	$(".removeAllBtn").click(function(){
		$("#assignedRoles option").each(function() {
		      $(this).detach().appendTo("#unassignedRoles").prop('value', $(this).prop('value'));
		      $(this).removeAttr('selected');
		 });
	});

	<c:if test="${!util:isUrlVisible('/app/appUser/save') }">
		$('#assignedRoles').prop('disabled', true);
	</c:if>
	
});

</script>

<div class="row" >
	<div class="column-data field100perc">
		<c:if test="${util:isUrlVisible('/app/appRole/save') }">
		
			<div style="float:left">
				<spring:message code="label.appUser.notAssignedRoles" />:<br/>
				<select id="unassignedRoles" size = "20" style="min-width:250px;" multiple="multiple">
					<c:forEach var="role" items="${notAssignedRoles}">
						<option value="${role.id}">${role.roleName}</option>
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
			<spring:message code="label.appUser.assignedRoles" />:<br/>
			<select id="assignedRoles" name="assignedRoles" size="20" style="min-width:250px;" multiple="multiple" class="selectAll" oldval=".">
			<c:if test="${assignedRoles != null}">
				<c:forEach var="role" items="${assignedRoles}">
					<option  value="${role.id}">${role.roleName}</option>
					<script>$(document).ready(function() { $("#assignedRoles").attr("oldval", $("#assignedRoles").attr("oldval") + "${role.id}" + "."  ); });</script>
				</c:forEach>
			</c:if>
			</select>
		</div>
	</div>
</div>
