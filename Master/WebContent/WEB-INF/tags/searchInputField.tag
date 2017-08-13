<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="util" uri="/WEB-INF/tlds/util-functions.tld"%>

<%@ attribute name="name" required="true" rtexprvalue="true" description="Name of corresponding property in bean object" %>
<%@ attribute name="label" required="true" rtexprvalue="true" description="Label appears in red color if input is considered as invalid after submission" %>
<%@ attribute name="norow" required="false" rtexprvalue="false" description="Shold we create row div or not" %>
<%@ attribute name="required" required="false" rtexprvalue="false" description="Required or not" %>
<%@ attribute name="autofocus" required="false" rtexprvalue="false" description="Autofocus or not" %>
<%@ attribute name="inputType" required="false" rtexprvalue="true" description="Type of input (Check HTML5 input types)"  %>
<%@ attribute name="value" required="false" rtexprvalue="true" description="Value of input"  %>
<%@ attribute name="msgTxt" required="false" rtexprvalue="true" description="Info Message"  %>
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

<%@ attribute name="maxLength" required="false" rtexprvalue="true" description="Maximum number of characters enabled for input"  %>

<c:if test="${type==null }"><c:set var="type" value="text"/></c:if>
<c:if test="${norow==null }"><c:set var="norow" value="false"/></c:if>

<%-- fix for mobile users --%>
<c:if test="${!norow}">
	<c:set var="_tstRW" value="60%" scope="page"/>
</c:if>

<spring:bind path="${name}" >
	<c:if test="${!norow}">
		<c:choose>
       		<c:when test="${hiddenRow}">
				<div class="searchRow ${rowClass}" id="${name}_ROW_DIV" hidden="${hiddenRow}" <c:if test="${rowstyle!=null }"> style='${rowstyle}' </c:if> >
			</c:when>
       		<c:otherwise>
	       		<div class="searchRow ${rowClass}" id="${name}_ROW_DIV" <c:if test="${rowstyle!=null }"> style='${rowstyle}' </c:if> >
       		</c:otherwise>
        </c:choose>
    </c:if>
	    <div class="column-label ${labelClass}" id="${name}_ROW_LAB_DIV">
	        <label class="control-label" for="${name }"><spring:message code="${label}"/><c:if test="${required }"><span style="color:red">*</span></c:if></label>
		</div>
        <div class="column-data ${dataClass}" <c:if test="${!norow}">style="width:${pageScope._tstRW }"</c:if> id="${name}_ROW_DATA_DIV">
        	<c:choose>
        		<c:when test="${convertToReadOnly}">
            		<input type="hidden" name="${name}" id="${name}" value="${value}" />
            		<c:out value="${value}"/>
        		
        		</c:when>
        		<c:otherwise>
		            <input class="${fieldClass}" type="${inputType!=null && inputType!='number' ? inputType : 'text' }" name="${name}" id="${name}" value="${value}" <c:if test="${required }">required</c:if> <c:if test="${disabled }">disabled</c:if> <c:if test="${autofocus }">autofocus</c:if> <c:if test="${ style != null }">style="${ style }"</c:if> <c:if test="${checked == true}"> checked="checked" </c:if> <c:if test="${maxLength != null}">maxLength=${maxLength}</c:if> />
        		
        		</c:otherwise>
        	</c:choose>
        	<c:if test="${tooltip != null }"><div class="jqHelptooltip jqhelpbckg" title="<spring:message code="${tooltip}"/>"></div></c:if>
            <div class="jqErrtooltip jqerrbckg" id="${name}_ERR" style="<c:if test="${!status.error}">display:none</c:if>" title="${util:isErrorMessageTypeMismatch(status) ? util:getFormattedTypeMismatchErrorMessage(status) : status.errorMessage}" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>
            <c:if test="${msgTxt!=null }">
            	<div class="" style="display:inline" id="${name}_TXT" ><spring:message code="${msgTxt }"/></div>
            </c:if>
        </div>
	<c:if test="${!norow}">
	    </div>
	</c:if>
</spring:bind>	
<c:if test="${!norow}">	<c:remove var="_tstRW" scope="page"/></c:if>