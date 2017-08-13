<%@page import="rs.milanmitic.master.common.menu.MasterMenuPermissions"%>
<%@page import="net.sf.navigator.menu.MenuRepository"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://struts-menu.sf.net/tag" prefix="menu" %>
<%@ taglib uri="http://struts-menu.sf.net/tag" prefix="menu-el" %>

<%-- SmartMenus jQuery init --%>
<%-- #main-menu config - instance specific stuff not covered in the theme --%>
<style type="text/css">
	#main-menu {
		position:relative;
		z-index:9999;
		width:80%;
		display: inline-block;
	}
	#main-menu ul {
		width:10em; /* fixed width only please - you can use the "subMenusMinWidth"/"subMenusMaxWidth" script options to override this if you like */
	}
</style>
<script type="text/javascript">
	$(function() {
		$('#main-menu').smartmenus({
			subMenusSubOffsetX: 1,
			subMenusSubOffsetY: -8
		});
		$('#main-menu').smartmenus('keyboardSetHotkey', 123, 'shiftKey');
	});
</script>

<%request.setAttribute("menuPermissions",new MasterMenuPermissions(request)); %>
<menu:useMenuDisplayer name="OfficialMagicMenu" repository="menuRepository" permissions="menuPermissions">
    <c:forEach var="menu" items="${menuRepository.topMenus}">
        <menu-el:displayMenu name="${menu.name}"/>
    </c:forEach>
</menu:useMenuDisplayer>