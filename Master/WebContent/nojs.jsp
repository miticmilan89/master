<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

    <style type="text/css">
        .pagecontainer {display:none;}
    </style>
    <div class="noscriptmsg" style="width:100%; height :100%; text-align: center; vertical-align: middle; font-size: 30px; background-color: #ffffff; color:red; font-weight: bold;">
    	<img src="<c:url value="/errNoJs.png"/>"/>
    	<br/><br/><spring:message code="label.noJavascipt" />
    </div>
