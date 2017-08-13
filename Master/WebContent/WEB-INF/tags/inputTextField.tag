<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<%@ attribute name="name" required="true" rtexprvalue="true" description="Name of corresponding property in bean object" %>
<%@ attribute name="label" required="true" rtexprvalue="true" description="Label appears in red color if input is considered as invalid after submission" %>
<%@ attribute name="norow" required="false" rtexprvalue="true" description="Shold we create row div or not" %>
<%@ attribute name="required" required="false" rtexprvalue="true" description="Required or not" %>
<%@ attribute name="inputType" required="false" rtexprvalue="true" description="Required or not"  %>
<%@ attribute name="autofocus" required="false" rtexprvalue="false" description="Autofocus or not" %>
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

	<c:if test="${!norow}">
		<c:choose>
       		<c:when test="${hiddenRow}">
				<div class="row ${rowClass}" hidden="${hiddenRow}" <c:if test="${rowstyle!=null }"> style='${rowstyle}' </c:if> >
			</c:when>
       		<c:otherwise>
	       		<div class="row ${rowClass}" <c:if test="${rowstyle!=null }"> style='${rowstyle}' </c:if> >
       		</c:otherwise>
        </c:choose>
    </c:if>
	    <div class="column-label ${labelClass}">
	        <label class="control-label" for="${name }"><spring:message code="${label}"/><c:if test="${required }"><span style="color:red">*</span></c:if></label>
		</div>
        <div class="column-data ${dataClass}" <c:if test="${!norow}">style="width:${pageScope._tstRW }"</c:if>>
            <input class="${fieldClass}" type="${inputType!=null && inputType!='number' ? inputType : 'text' }" name="${name}" id="${name}" value="${requestScope[name]!=null ? requestScope[name] : param[name] }"  <c:if test="${autofocus }">autofocus</c:if>/>
			<c:if test="${tooltip != null }"><div class="jqHelptooltip jqhelpbckg" title="<spring:message code="${tooltip}"/>"></div></c:if>
        </div>
	<c:if test="${!norow}">
	    </div>
	</c:if>
<c:remove var="_ifw"/>
<c:if test="${!norow}">	<c:remove var="_tstRW" scope="page"/></c:if>