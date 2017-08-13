<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="util" uri="/WEB-INF/tlds/util-functions.tld"%>


<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
<c:if test="${requestScope[Constants.ACTION]!=null }">
	<input type="hidden" name="${Constants.ACTION}" value="${requestScope[Constants.ACTION]}"/>
</c:if>
<c:if test="${requestScope[Constants.SECURE_FIELD]!=null }">
	<input type="hidden" name="${Constants.SECURE_FIELD}" value="${requestScope[Constants.SECURE_FIELD]}"/>
</c:if>

<c:if test="${requestScope[Constants.ACTION]!=Constants.ACTION_ADD}">
	<c:out value="${util:generateSecureHiddenTag(currentFormBean)}" escapeXml="false" />
</c:if>

<%-- requestScope[Constants.ACTION]:<c:out value="${requestScope[Constants.ACTION] }"/> <br/>
[Constants.ACTION_ADD]:<c:out value="${Constants.ACTION_ADD }"/>
--%>
