<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="util" uri="/WEB-INF/tlds/util-functions.tld"%>

<%@ attribute name="name" required="true" rtexprvalue="true" description="Name of corresponding property in bean object" %>
<%@ attribute name="timename" required="false" rtexprvalue="true" description="Name of corresponding property in bean object" %>
<%@ attribute name="label" required="true" rtexprvalue="true" description="Label appears in red color if input is considered as invalid after submission" %>
<%@ attribute name="norow" required="false" rtexprvalue="false" description="Shold we create row div or not" %>
<%@ attribute name="required" required="false" rtexprvalue="false" description="Required or not" %>
<%@ attribute name="autofocus" required="false" rtexprvalue="false" description="Autofocus or not" %>
<%@ attribute name="inputType" required="false" rtexprvalue="true" description="Type of input (Check HTML5 input types)"  %>
<%@ attribute name="value" required="false" rtexprvalue="true" description="Value of input"   %>
<%@ attribute name="timevalue" required="false" rtexprvalue="true" description="Value of input"   %>
<%@ attribute name="type" required="false" rtexprvalue="true" description="Should we create new datepicker or timepicker"  %>
<%@ attribute name="hiddenRow" required="false" rtexprvalue="true" description="Is row is hidden or not"  %>

<%@ attribute name="style" required="false" rtexprvalue="true" description="Style of input"  %>
<%@ attribute name="rowstyle" required="false" rtexprvalue="true" description="Style of input"  %>

<%@ attribute name="convertToReadOnly" required="false" rtexprvalue="true" description="Show value as read only field"  %>
<%@ attribute name="disabled" required="false" rtexprvalue="true" description="Is field disabled or not"  %>
<%@ attribute name="checked" required="false" rtexprvalue="true" description="Indicator is checkbox checked"  %>

<%@ attribute name="dataClass" required="false" rtexprvalue="true" description="Css class"  %>
<%@ attribute name="rowClass" required="false" rtexprvalue="true" description="Css class"  %>
<%@ attribute name="fieldClass" required="false" rtexprvalue="true" description="Css class"  %>
<%@ attribute name="labelClass" required="false" rtexprvalue="true" description="Css class"  %>

<c:if test="${type==null }"><c:set var="type" value="text"/></c:if>
<c:if test="${norow==null }"><c:set var="norow" value="false"/></c:if>

<%-- fix for mobile users --%>
<c:if test="${!norow}">
	<c:set var="_tstRW" value="60%" scope="page"/>
</c:if>

<c:if test = "${timename!=null }">
	<spring:bind path="${timename}" >
		<c:set var="timestatusvalue" value="${status.value }" scope="page"/>
		<c:if test="${status.error }">
			<c:set var="timestatuserrorMessage" value="${status.errorMessage }" scope="page"/>
		</c:if>
	</spring:bind>
</c:if>

<spring:bind path="${name}" >
	<c:if test="${!norow}">
		<c:choose>
       		<c:when test="${hiddenRow}">
				<div class="searchRow ${rowClass}" hidden="${hiddenRow}" <c:if test="${rowstyle!=null }"> style='${rowstyle}' </c:if> >
			</c:when>
       		<c:otherwise>
	       		<div class="searchRow ${rowClass}" <c:if test="${rowstyle!=null }"> style='${rowstyle}' </c:if> >
       		</c:otherwise>
        </c:choose>
	</c:if>
		<div class="column-label ${labelClass}">
			<label class="control-label" for="${name }"><spring:message code="${label}"/><c:if test="${required }"><span style="color:red">*</span></c:if></label>
		</div>
		<div class="column-data ${dataClass}" <c:if test="${!norow}">style="width:${pageScope._tstRW }"</c:if>>
			<c:choose>
				<c:when test="${type == 'date'}">
					<input type="text" name="${name}" id="${name}" value="<c:out value="${value!=null ? value : status.value}" />" class="datepicker ${fieldClass}" <c:if test="${required }">required</c:if> <c:if test="${autofocus}">autofocus</c:if> <c:if test="${ style != null }">style="${ style }"</c:if>  />
					<div class="jqErrtooltip jqerrbckg" id="${name}_ERR" <c:if test="${!status.error}">style="display:none"</c:if> title="${util:isErrorMessageTypeMismatch(status) ? util:getFormattedTypeMismatchErrorMessage(status) : status.errorMessage}" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>
				</c:when>
				<c:when test="${type == 'time'}">
					<input type="text" name="${name}" id="${name}" value="<c:out value="${value!=null ? value : status.value}" />" class="timepicker ${fieldClass}" <c:if test="${required }">required</c:if> <c:if test="${autofocus}">autofocus</c:if> <c:if test="${ style != null }">style="${ style }"</c:if>  />
					<div class="jqErrtooltip jqerrbckg" id="${name}_ERR" <c:if test="${!status.error}">style="display:none"</c:if> title="${util:isErrorMessageTypeMismatch(status) ? util:getFormattedTypeMismatchErrorMessage(status) : status.errorMessage}" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>
				</c:when>
				<c:when test="${timename!=null}">
					<input type="text" name="${name}" id="${name}" value="<c:out value="${value!=null ? value : status.value}" />" class="datepicker ${fieldClass}" <c:if test="${required }">required</c:if> <c:if test="${autofocus}">autofocus</c:if> <c:if test="${ style != null }">style="${ style }"</c:if>  />
					<div class="jqErrtooltip jqerrbckg" id="${name}_ERR" <c:if test="${!status.error}">style="display:none"</c:if> title="${util:isErrorMessageTypeMismatch(status) ? util:getFormattedTypeMismatchErrorMessage(status) : status.errorMessage}" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>
					
					<input type="text" name="${timename}" id="${timename}" value="<c:out value="${timevalue!=null ? timevalue : pageScope.timestatusvalue}" />" class="timepicker ${fieldClass}" <c:if test="${required }">required</c:if> <c:if test="${autofocus}">autofocus</c:if> <c:if test="${ style != null }">style="${ style }"</c:if>  />
					<div class="jqErrtooltip jqerrbckg" id="${timename}_ERR" <c:if test="${timestatuserrorMessage==null}">style="display:none"</c:if> title="${timestatuserrorMessage}" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>
				</c:when>
				<c:otherwise>
					WRONG TYPE : {type} !
				</c:otherwise>
			</c:choose> 
			<c:if test="${tooltip != null }"><div class="jqHelptooltip jqhelpbckg" title="<spring:message code="${tooltip}"/>"></div></c:if>
	        <c:if test="${msgTxt!=null }">
	        	<div class="" style="display:inline" id="${name}_TXT" ><spring:message code="${msgTxt }"/></div>
	        </c:if>
		</div>
	<c:if test="${!norow}">
		</div>
	</c:if>
</spring:bind>
<c:remove var="timestatusvalue" scope="page"/>
<c:remove var="timestatuserrorMessage" scope="page"/>
<c:if test="${!norow}">	<c:remove var="_tstRW" scope="page"/></c:if>