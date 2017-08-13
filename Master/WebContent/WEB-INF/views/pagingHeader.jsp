<%@ taglib prefix="c"  		uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%--  
	resultsCount:${pagingData.resultsCount}<br/>
	prevPage:${pagingData.hasPrevPage}<br/>
	nextPage:${pagingData.hasNextPage}<br/>
	currentPage:${pagingData.currentPage}<br/>
	totalPageNo:${pagingData.totalPageNo}<br/>
	displayPageFrom:${pagingData.displayPageFrom}<br/>
	displayPageTo:${pagingData.displayPageTo}<br/>
--%> 	
 <c:if test="${empty requestScope.pageClickExtend }">
 	<c:set var="pageClickExtend" value="" scope="request"/>
 </c:if>
 <c:if test="${empty requestScope.gotopagejs }">
 	<c:set var="gotopagejs" scope="request" value="goToPage"/>
 </c:if>
 
 <c:if test="${empty requestScope.gotopagecustomjs }">
 	<c:set var="gotopagecustomjs" scope="request" value="goToPageCustom"/>
 </c:if>

 <c:if test="${empty requestScope.gotopagerootclass }">
 	<c:set var="gotopagerootclass" scope="request" value=""/>
 </c:if>
 
<c:if test="${pagingData.displayPageTo!=null && pagingData.displayPageTo!=1}">
<div class="listheader ${gotopagerootclass}"  >&nbsp;
	<div style="float:left;">
	<span title="<spring:message code='paging.firstRecord' />">${pagingData.startNo}</span> - <span title="<spring:message code='paging.lastRecord' />">${pagingData.endNo}</span>&nbsp;of&nbsp; <span title="<spring:message code='paging.totalRecords' />">${pagingData.resultsCount}</span>
	(<span title="<spring:message code='paging.currentPage' />">${pagingData.currentPage}</span>/<span title="<spring:message code='paging.totalPages' />">${pagingData.totalPageNo}</span>)
	
	</div> 
	<div style="float:right;">
		<c:if test="${pagingData.hasPrevPage}">
			<a href="" onclick="return ${gotopagejs}(1, this)" title="<spring:message code='paging.first' />" <c:out   escapeXml="false" value="${requestScope.pageClickExtend }" />><span class="ui-icon ui-icon-seek-first"></span></a>
			<a href="" onclick="return ${gotopagejs}(${pagingData.currentPage-1}, this)" title="<spring:message code='paging.previous' />" <c:out   escapeXml="false" value="${requestScope.pageClickExtend }" />><span class="ui-icon ui-icon-seek-prev"></span></a>
		</c:if>
		<c:forEach var="pageNo" begin="${pagingData.displayPageFrom}" end="${pagingData.displayPageTo}" varStatus="pageNoCounter">
			<c:choose>
				<c:when test="${pageNo==pagingData.currentPage}">
					<%--current page --%>
					<span title="<spring:message code='paging.currentPage' />" class="currentPage">${pageNo}</span>
				</c:when>
				<c:otherwise>
					<%--other pages --%>
					<a href="" onclick="return ${gotopagejs}(${pageNo}, this)" title="<spring:message code='paging.displayPage' /> ${pageNo}" class="page"  <c:out   escapeXml="false" value="${requestScope.pageClickExtend }" />  >${pageNo}</a>
				</c:otherwise>
			</c:choose>
<%-- 			<c:if test = "${!pageNoCounter.last}"> --%>
<%-- 			</c:if>   --%>
		</c:forEach>
		<c:if test="${pagingData.hasNextPage}">
			&nbsp;<a href="" onclick="return ${gotopagejs}(${pagingData.currentPage+1}, this)" title="<spring:message code='paging.next' />" <c:out   escapeXml="false" value="${requestScope.pageClickExtend }" />><span class="ui-icon ui-icon-seek-next"></span></a>
			<a href="" onclick="return ${gotopagejs}(${pagingData.totalPageNo}, this)" title="<spring:message code='paging.last' />" <c:out   escapeXml="false" value="${requestScope.pageClickExtend }" /> ><span class="ui-icon ui-icon-seek-end"></span></a>
		</c:if>
	</div>  
	
</div>	
</c:if>

<script>
function ${gotopagejs}(currentPage, elem) {
	if (document.getElementById("_pagging")==null)
		$(document.getElementById("currentPage").form).append('<input type="hidden" name="_pagging" id="_pagging" value="true" />');
	if(typeof ${requestScope.gotopagecustomjs} === "function"){ 
		${requestScope.gotopagecustomjs}(currentPage, elem);
	} else {
		document.getElementById("currentPage").value=currentPage;
		document.getElementById("restartPagging").value=false;
		document.getElementById("submitSearchBtn").click();
	}
	return false;
}	
</script>