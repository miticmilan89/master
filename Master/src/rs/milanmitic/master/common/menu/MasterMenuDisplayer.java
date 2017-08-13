package rs.milanmitic.master.common.menu;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import net.sf.navigator.displayer.MenuDisplayerMapping;
import net.sf.navigator.displayer.MessageResourcesMenuDisplayer;
import net.sf.navigator.menu.MenuComponent;
import rs.milanmitic.master.common.ContextHolder;
import rs.milanmitic.master.common.util.Utils;

/**
 * 
 * @author milan
 * @version 1.0
 */
public class MasterMenuDisplayer extends MessageResourcesMenuDisplayer {

	private static final String LABEL_SEPARATOR = "label.separator";
	private static final String SP = "<li class=\"divider\">&nbsp;</li>";

	private boolean selectMode = false;
	private int counter = 0;
	private List<String> list = null;
	private WebApplicationContext webAppContext;

	/**
	 * Constructor
	 * 
	 */
	public MasterMenuDisplayer() {
		super();
	}

	@Override
	public void init(PageContext pageContext, MenuDisplayerMapping mapping) {
		super.init(pageContext, mapping);
		try {
			webAppContext = ContextLoader.getCurrentWebApplicationContext();
			locale = LocaleContextHolder.getLocale();

			counter = 0;

			out.println("<ul id=\"main-menu\" class=\"sm sm-blue\" >");
		} catch (Exception e) {
			log.error("Error", e);
		}
	}

	@Override
	public void display(MenuComponent menu) throws JspException, IOException {
		if (isAllowed(menu)) {
			displayComponents(menu, 0);
		}
	}

	private boolean checkUrlRights(String url) {
		if (StringUtils.isNotBlank(url)) {
			if (url.endsWith(".jsp") || url.startsWith("cgwaction"))
				return true;
			if (Utils.isUrlVisible(url)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check is at least one of children nodes has access rights
	 * 
	 * @param menu
	 * @return
	 */
	private boolean checkChildRights(MenuComponent menu) {
		MenuComponent[] components = menu.getMenuComponents();
		if (components != null) {
			for (MenuComponent m : components) {
				MenuComponent[] components1 = m.getMenuComponents();
				if (components1 != null && components1.length > 0 && checkChildRights(m))
					return true;
				if (checkUrlRights(m.getPage()))
					return true;
			}
			return false;
		}
		return checkUrlRights(menu.getPage());
	}

	protected void displayComponents(MenuComponent menu, int level) throws JspException, IOException {
		counter++;
		MenuComponent[] components = menu.getMenuComponents();
		// SECURITY_COMMENT
		if (ContextHolder.isSecurityEnabled() && !checkChildRights(menu))
			return;

		String title = getMessage(menu.getTitle());
		String onclick = getOnClick(menu);
		String star = isImplemented(menu);
		StringBuilder sbLevel = new StringBuilder("");
		for (int x = 0; x < level; x++) {
			sbLevel.append("\t");
		}

		if (components.length > 0) {

			if (StringUtils.isEmpty(onclick)) {
				out.println(sbLevel + "<li class= \"levelTitle_" + counter + "\"><a href=\"\" onclick=\"return false\">&nbsp;&nbsp;" + star + title + "&nbsp;&nbsp;</a>");
			} else {
				out.println(sbLevel + "<li class=\"levelTitle_" + counter + "\"><a href= \"\" " + onclick + " >&nbsp;&nbsp;" + star + title + "&nbsp;&nbsp;</a>");
			}
			int n = components.length;
			boolean first = true;
			for (int i = 0; i < n; i++) {
				if (isAllowed(components[i])) {
					first = makeChildHTML(level, components, sbLevel, first, i);
				}
			}
			if (!first) {
				out.println(sbLevel + "</ul>");
			}

			out.println(sbLevel + "</li>");

		} else if (menu.getParent() == null) {
			if (LABEL_SEPARATOR.equals(menu.getTitle()))
				out.println(sbLevel + SP);
			else
				out.println(sbLevel + "<li class=\"levelTitle_" + counter + "\"><a href=\"\" " + onclick + ">&nbsp;&nbsp;" + star + title + "&nbsp;&nbsp;</a></li>");
		} else {
			if (LABEL_SEPARATOR.equals(menu.getTitle()))
				out.println(sbLevel + SP);
			else
				out.println(sbLevel + "<li class=\"levelTitle_" + counter + "\"><a href=\"\" " + onclick + " >&nbsp;&nbsp;" + star + title + "&nbsp;&nbsp;</a></li>");
		}
	}

	private boolean makeChildHTML(int level, MenuComponent[] components, StringBuilder sbLevel, boolean first, int i) throws IOException, JspException {
		if (first) {
			out.println(sbLevel + "<ul class=\"dropdown-menu\">");
			sbLevel.append("\t");
			first = false;
		}
		if (components[i].getMenuComponents().length > 0) {
			displayComponents(components[i], level + 1);
		} else {
			if (LABEL_SEPARATOR.equals(components[i].getTitle()) || " ".equals(components[i].getTitle())) {
				out.print(sbLevel + SP);
			} else {
				StringBuilder sb = new StringBuilder("");
				sb.append("<li><a  style=\"display:inline-block\" href=\"\" ").append(getOnClick(components[i])).append(" >&nbsp;&nbsp;").append(isImplemented(components[i]) + getMessage(components[i].getTitle())).append("&nbsp;&nbsp;</a>");
				if (selectMode) {
					boolean checked = list != null && list.indexOf(components[i].getName()) != -1;
					sb.append("<input " + (checked ? " checked='checked' " : "") + " menuName=\"" + components[i].getName() + "\" style=\"display:inline-block\" type=\"checkbox\" class=\"_fastMenu\" parentTitleLevel=\"" + counter + "\" />");
				}
				sb.append("</li>");
				out.println(sbLevel + "" + sb);
			}
		}
		return first;
	}

	private String getOnClick(MenuComponent m) {
		String url = m.getPage();
		url = "onclick=\"goToMenuItem('" + url + "','" + this.getMessage(m.getTitle()) + "');return false\"";
		return url;
	}

	@Override
	public void end(PageContext context) {
		try {
			out.println("</ul>");
		} catch (Exception e) {
			log.error("Error", e);
		}
	}

	@Override
	public String getMessage(String msg) {
		String s = webAppContext != null ? webAppContext.getMessage(msg, null, locale) : super.getMessage(msg);
		if (s != null)
			s = StringUtils.replace(s, "_TR", "");
		return s;
	}

	private String isImplemented(MenuComponent m) {
		int componentsLen = m.getMenuComponents().length;
		String star = "";
		if (componentsLen == 0 && m.getPage() != null && m.getPage().indexOf("/app") == -1)
			star = "** ";
		return star;
	}

}