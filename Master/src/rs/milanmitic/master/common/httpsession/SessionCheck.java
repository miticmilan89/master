package rs.milanmitic.master.common.httpsession;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;

import rs.milanmitic.master.common.Constants;
import rs.milanmitic.master.common.data.LoggedUserData;

/**
 * Servlet implementation class SessionCheck
 */
public class SessionCheck extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected static Logger log = LogManager.getLogger(SessionCheck.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SessionCheck() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// NOT SUPPORTED
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			HttpSession session = request.getSession(false);
			LoggedUserData loggedUser = session != null ? (LoggedUserData) session.getAttribute(Constants.LOGGED_USER) : null;
			if (loggedUser != null)
				ThreadContext.put(Constants.LOGGED_USER, loggedUser.getLog4jKey());
			String uq = request.getParameter("UQ");
			String loginType = request.getParameter("lt");
			String appUsr = request.getParameter("usr");
			String result = "-1";
			if (session != null && request.getSession().getAttribute(Constants.SESSION_LAST_ACCESS) != null) {
				// GET LAST TIME WE ACCESSED WEB APPL -- this is set in ContextFilter
				long lastAccessTimeMsec = (Long) request.getSession().getAttribute(Constants.SESSION_LAST_ACCESS);
				// Convert it to SEC
				long lastAccessTimeSec = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastAccessTimeMsec);

				// CHECK HOW MANY SECONDS WE STILL HAVE BEFORE SESSION EXPIRED
				int maxInactiveTimeSec = request.getSession().getMaxInactiveInterval();
				long millis = maxInactiveTimeSec - lastAccessTimeSec;
				// IN CASE SESSION EXPIRED
				result = "" + (millis < 0 ? -1 : millis);

				// if (log.isDebugEnabled())
				// log.debug(uq + ", lastAccessTimeSec:" + lastAccessTimeSec + ", maxInactiveTimeSec:" + maxInactiveTimeSec + ", result :" + result);
			}

			if ("-1".equals(result) && session != null) {
				try {
					// invalidate session
					Authentication auth = SecurityContextHolder.getContext().getAuthentication();
					if (auth != null) {
						new SecurityContextLogoutHandler().logout(request, response, auth);
					}
					SecurityContextHolder.getContext().setAuthentication(null);

					CookieClearingLogoutHandler cookieClearingLogoutHandler = new CookieClearingLogoutHandler(AbstractRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY);
					cookieClearingLogoutHandler.logout(request, response, null);

				} catch (Exception  t) {
					log.error(uq + ", Error(ignored), doPost", t);
				}
			} else if (loggedUser != null) {
				// check is logged user same as on browser page
				if (!(loggedUser.getUserType().getId().equals(loginType) && loggedUser.getUsernameHash().equals(appUsr))) {
					result = "-2";
				}
			}
			// if (log.isDebugEnabled())
			// log.debug(uq+", SEND:" + result);
			response.getOutputStream().write(result.getBytes());
			response.getOutputStream().flush();

		} finally {
			try {
				// MDC.remove("user-id");
				ThreadContext.remove(Constants.LOGGED_USER);
			} catch (Exception  t) {
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	@Override
	public void destroy() {
		Constants.setShutDownInProgress(Boolean.TRUE);
		super.destroy();
	}

}
