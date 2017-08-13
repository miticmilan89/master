<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ page isErrorPage="true"%>

<html>
<body style="text-align: center;">
	<br /> <br />
	<h1>Access denied</h1>
	<br />

	<h1>
		<a href="" onclick="window.history.back();return false;">Go back</a> or <a href="<c:url value="/index.jsp"/>" >Login</a> 
	</h1>
</body>
</html>