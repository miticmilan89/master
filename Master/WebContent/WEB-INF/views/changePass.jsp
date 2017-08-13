<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="masterInput" tagdir="/WEB-INF/tags" %>
<style>
.ui-tooltip {
	min-width:650px;
	font-size:11px;
}
</style>
<script type="text/javascript">
var defaultFormName = "changePasswordForm";
</script>
<form id="changePasswordForm" name="changePasswordForm" action="<c:url value="/app/changePass"/>" method="post" autocomplete="off" >
	<masterInput:inputTextField fieldClass="field30perc" label="label.currentPassword" name="currentPassword" inputType="password" autofocus="true" />
	<masterInput:inputTextField fieldClass="field30perc" label="label.newPassword" name="newPassword" inputType="password" tooltip="${passwordRequirements }"/>
	<masterInput:inputTextField fieldClass="field30perc" label="label.newPasswordRepeat" name="newPasswordRepeat" inputType="password"/>
	<masterInput:mastertokens/>
</form>
   		
<br/><br/><br/><br/>

<span id="PAGE_TOOLBAR">
	<button type="button" class="toolbarButton" onclick="return submitDefaultForm()"><spring:message code="button.ok"/></button>
	<button type="button" class="toolbarButton" onclick="return gotoRelativeUrl('/app/home');"><spring:message code="button.cancel"/></button>
</span>
<span id="PAGE_TITLE">
	<spring:message code="label.changePass"/>
</span>