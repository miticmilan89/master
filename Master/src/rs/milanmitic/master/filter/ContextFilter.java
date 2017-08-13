package rs.milanmitic.master.filter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import net.sf.navigator.menu.MenuRepository;
import rs.milanmitic.master.common.Constants;
import rs.milanmitic.master.common.ContextHolder;
import rs.milanmitic.master.common.data.LoggedUserData;
import rs.milanmitic.master.model.ActivityLog;
import rs.milanmitic.master.service.NewTransactionService;

/**
 * Servlet Filter implementation class ContextFilter
 */
public class ContextFilter implements Filter {

	private Logger log = LogManager.getLogger(ContextFilter.class);
	private static final AtomicLong COUNTER = new AtomicLong();

	/**
	 * Default constructor.
	 */
	public ContextFilter() {
		super();
	}

	/**
	 * @see Filter#destroy()
	 */
	@Override
	public void destroy() {
		// for future use
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	@Override
	public void init(FilterConfig fConfig) throws ServletException {
		// for future use
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		try {
			String path = req.getRequestURI();
			path = StringUtils.remove(path, req.getContextPath());

			resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			resp.setHeader("Pragma", "no-cache");
			resp.setDateHeader("Expires", 0);

			log.debug("--->  ContextFilter create context URI:{}, path:{}", req.getRequestURI(), path);

			if (req.getSession().getAttribute(Constants.CURRENT_THEME) == null)
				req.getSession().setAttribute(Constants.CURRENT_THEME, Constants.DEFAULT_THEME);

			String mastertheme = request.getParameter("mastertheme");
			if (StringUtils.isNotBlank(mastertheme))
				req.getSession().setAttribute(Constants.CURRENT_THEME, mastertheme);

			ContextHolder.createContext(req);

			LoggedUserData loggedUser = ContextHolder.getLoggedUser();
			if (loggedUser != null) {
				createMenu(req, path, loggedUser);
				if (loggedUser.isForcePasswordChange() && !"/app/changePass".equals(path) && !"/app/logout".equals(path)) {
					req.getSession().getServletContext().getRequestDispatcher("/app/changePass").forward(request, response);
					return;
				}
			} else {
				ThreadContext.put(Constants.LOGGED_USER, Long.toString(COUNTER.getAndIncrement()));

			}
			handleCookie(response, req);

			// pass the request along the filter chain
			chain.doFilter(request, response);
		} catch (Exception t) {
			log.error("Error", t);
			throw new IllegalArgumentException("Error", t);
		} finally {
			handleFinally(req);
		}
	}

	private void handleFinally(HttpServletRequest req) {
		ActivityLog ev = null;
		try {
			ev = ContextHolder.removeActivityLog();
			if (ev != null) {
				ev.setActivityData(ev.getActivityUtil().getXml());
				ev.setDuration(System.currentTimeMillis() - ev.getDatetimeStart().getTime());
				ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(req.getSession().getServletContext());
				NewTransactionService newTransactionService = ctx.getBean(NewTransactionService.class);
				if (ev.getActivityData() != null && ev.getActivityData().length() > ActivityLog.MSSGTX_MAX_LEN) {
					ev.setActivityData(ev.getActivityData().substring(0, ActivityLog.MSSGTX_MAX_LEN - 4) + "...");
					log.error("Event log not fully saved because length of Mssgtx field >" + ActivityLog.MSSGTX_MAX_LEN + " so here is fully record :" + ev);
				}
				 newTransactionService.saveActivityLog(ev);
			}
		} catch (Exception t) {
			log.error("Error save eventlog :" + ev, t);
		}
		ThreadContext.remove(Constants.LOGGED_USER);

		try {
			ContextHolder.clearContext();
		} catch (Exception t) {
			log.error("Error, ignored, clear conext", t);
		}
		log.debug("--->  ContextFilter destroy context");
	}

	private void handleCookie(ServletResponse response, HttpServletRequest req) {
		// OWASP recommendation: https://www.owasp.org/index.php/HttpOnly
		if (req.getCookies() != null && (Constants.isWebSecurityUseHttpOnly() || Constants.isWebSecurityUseSecureCookie())) {
			Cookie[] c = req.getCookies();
			for (Cookie oneCookie : c) {
				// HttpOnly supported only on Servlet 3.0 SPEC - so depends on server library set for the project
				if (Constants.isWebSecurityUseHttpOnly() && oneCookie.getPath() != null && oneCookie.getPath().indexOf("HttpOnly") == -1) {
					oneCookie.setPath("; HttpOnly;");
					// (!oneCookie.isHttpOnly() || !oneCookie.getSecure())
					// (!oneCookie.isHttpOnly())
					log.info(">>SET COOKIE HTTPONLY TO TRUE");
					// oneCookie.setHttpOnly(true)
					//
				}
				if (Constants.isWebSecurityUseSecureCookie() && !oneCookie.getSecure()) {
					log.info(">>SET COOKIE  SECURE TO TRUE");
					oneCookie.setSecure(true);
				}

				((HttpServletResponse) response).addCookie(oneCookie);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void createMenu(HttpServletRequest req, String path, LoggedUserData loggedUser) {
		if (!path.startsWith("/sessionCheck"))
			req.getSession().setAttribute(Constants.SESSION_LAST_ACCESS, Long.valueOf(System.currentTimeMillis()));
		ThreadContext.put(Constants.LOGGED_USER, loggedUser.getLog4jKey());
		try {
			MenuRepository mr = (MenuRepository) req.getSession().getAttribute("menuRepository");
			// solve Tomcat restart problem, when no menu is shown, but mr exists in session
			boolean menuNotExist = mr == null || mr.getTopMenus() == null || mr.getTopMenus().isEmpty();
			if (!menuNotExist) {
				Set<String> s = mr.getMenuNames();
				if (!s.isEmpty() && mr.getMenu(s.iterator().next()).getName() == null)
					menuNotExist = true;
			}
			if (menuNotExist) {
				MenuRepository xmlDefaultRepository;
				if (Constants.getXmlMenuDisplayers() == null || mr == null) {
					xmlDefaultRepository = (MenuRepository) req.getSession().getServletContext().getAttribute(MenuRepository.MENU_REPOSITORY_KEY);
					Constants.setXmlMenuDisplayers(xmlDefaultRepository.getDisplayers());
					req.getSession().setAttribute("menuRepository", xmlDefaultRepository);
				}

			}
		} catch (Exception t) {
			log.error("Error creating menu", t);
		}
	}
}
