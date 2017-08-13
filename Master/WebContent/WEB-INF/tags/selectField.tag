<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="util" uri="/WEB-INF/tlds/util-functions.tld"%>

<%@ attribute name="name" required="true" rtexprvalue="true" description="Name of corresponding property in bean object" %>
<%@ attribute name="label" required="true" rtexprvalue="true" description="Label appears in red color if input is considered as invalid after submission" %>
<%@ attribute name="norow" required="false" rtexprvalue="false" description="Shold we create row div or not" %>
<%@ attribute name="required" required="false" rtexprvalue="false" description="Required or not" %>
<%@ attribute name="size" required="true" rtexprvalue="true" description="Size of Select" %>
<%@ attribute name="items" required="true" rtexprvalue="true" type="java.util.List" description="Items with pair of value and label" %>
<%@ attribute name="value" required="false" rtexprvalue="true" description="Value of Select"  %>
<%@ attribute name="onchange" required="false" rtexprvalue="true" description="On change callback"  %>
<%@ attribute name="hiddenRow" required="false" rtexprvalue="true" description="Is row is hidden or not"  %>
<%@ attribute name="tooltip" required="false" rtexprvalue="true" description="Message for tooltip"  %>

<%@ attribute name="style" required="false" rtexprvalue="true" description="Style of input"  %>
<%@ attribute name="rowstyle" required="false" rtexprvalue="true" description="Style of input"  %>

<%@ attribute name="convertToReadOnly" required="false" rtexprvalue="true" description="Show value as read only field"  %>
<%@ attribute name="disabled" required="false" rtexprvalue="true" description="Is field disabled or not"  %>
<%@ attribute name="checked" required="false" rtexprvalue="true" description="Indicator is checkbox checked"  %>

<%@ attribute name="dataClass" required="false" rtexprvalue="true" description="Css class"  %>
<%@ attribute name="rowClass" required="false" rtexprvalue="true" description="Css class"  %>
<%@ attribute name="fieldClass" required="false" rtexprvalue="true" description="Css class"  %>
<%@ attribute name="labelClass" required="false" rtexprvalue="true" description="Css class"  %>

<c:if test="${norow==null }"><c:set var="norow" value="false"/></c:if>
<%-- fix for mobile users --%>
<c:if test="${!norow}">
	<c:set var="_tstRW" value="60%" scope="page"/>
</c:if>

<spring:bind path="${name}">
    <c:if test="${!norow}">
		<c:choose>
       		<c:when test="${hiddenRow}">
				<div class="row ${rowClass}" id="${name}_ROW_DIV" hidden="${hiddenRow}" <c:if test="${rowstyle!=null }"> style='${rowstyle}' </c:if> >
			</c:when>
       		<c:otherwise>
	       		<div class="row ${rowClass}" id="${name}_ROW_DIV" <c:if test="${rowstyle!=null }"> style='${rowstyle}' </c:if> >
       		</c:otherwise>
        </c:choose>
    </c:if>
        <div class="column-label ${labelClass}" >
	        <label class="control-label" for="${name}" id="${name}_LABEL"><spring:message code="${label}"/><c:if test="${required}"><span style="color:red">*</span></c:if></label>
		</div>

        <div class="column-data ${dataClass}" <c:if test="${!norow}">style="width:${pageScope._tstRW }"</c:if>>
            <c:choose>
        		<c:when test="${convertToReadOnly or _pageReadOnly}">
        			<input type="hidden" name="${name}" id="${name}" value="${value}" />
		            	<c:forEach items="${items}" var="item">
		            		<c:set var="_label" value="${item.label}"/>
		            		<c:if test="${util:startsWith(item.label,'#')}">
		            			<spring:message code="${util:substring1(item.label,1)}" var="_label" />
		            		</c:if>
		            		<c:if test = "${item.value==value}">
		            			<c:out value="${_label}"/>
		            		</c:if>
		            	</c:forEach>
            	</c:when>
        		<c:otherwise>
		            <form:select path="${name}" id="${name}" size="${size}" cssStyle="selectW" onchange="${onchange}" cssClass="${fieldClass}">
		            	<c:forEach items="${items}" var="item">
		            		<c:set var="_label" value="${item.label}"/>
		            		<c:if test="${util:startsWith(item.label,'#')}">
		            			<spring:message code="${util:substring1(item.label,1)}" var="_label" />
		            		</c:if>
		            		<form:option value="${item.value}" label="${_label}"/>
		            	</c:forEach>
		            		<c:remove var="_label"/>
		            </form:select>
          		</c:otherwise>
        	</c:choose>
        	<c:if test="${tooltip != null }"><div class="jqHelptooltip jqhelpbckg" title="<spring:message code="${tooltip}"/>"></div></c:if>
            <div class="jqErrtooltip jqerrbckg" id="${name}_ERR" <c:if test="${!status.error}">style="display:none"</c:if> title="${status.errorMessage}" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>
        </div>
    <c:if test="${!norow}">
	    </div>
	</c:if>
</spring:bind>
<c:if test="${!norow}">	<c:remove var="_tstRW" scope="page"/></c:if>