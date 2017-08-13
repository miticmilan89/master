package rs.milanmitic.master.common.util;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * RequestUtil utility class Good ol' copy-n-paste from <a href="http://www.javaworld.com/javaworld/jw-02-2002/ssl/utilityclass.txt"> http://www.javaworld.com/javaworld/jw-02-2002/ssl/utilityclass.txt</a> which is referenced in the following article:
 * <a href="http://www.javaworld.com/javaworld/jw-02-2002/jw-0215-ssl.html"> http://www.javaworld.com/javaworld/jw-02-2002/jw-0215-ssl.html</a>
 */
public class RequestUtil {
	private static final Logger log = LogManager.getLogger(RequestUtil.class);
	private static final String USERAGENT = "user-agent";
	private static final String XFF = "x-forwarded-for";
	public static final int BROWSE_LOG_LENGTH = 50;
	private static final String IP6 = "0:0:0:0:0:0:0:1";

	private RequestUtil() {
		super();
	}

	/**
	 * Convenience method to set a cookie
	 * 
	 * @param response
	 * @param name
	 * @param value
	 * @param path
	 */
	public static void setCookie(HttpServletResponse response, String name, String value, String path) {
		if (log.isDebugEnabled()) {
			log.debug("Setting cookie '" + name + "' on path '" + path + "'");
		}

		Cookie cookie = new Cookie(name, value);
		cookie.setSecure(false);
		cookie.setPath(path);
		cookie.setMaxAge(3600 * 24 * 30); // 30 days

		response.addCookie(cookie);
	}

	/**
	 * Convenience method to get a cookie by name
	 * 
	 * @param request
	 *            the current request
	 * @param name
	 *            the name of the cookie to find
	 * 
	 * @return the cookie (if found), null if not found
	 */
	public static Cookie getCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		Cookie returnCookie = null;

		if (cookies == null) {
			return returnCookie;
		}

		for (int i = 0; i < cookies.length; i++) {
			Cookie thisCookie = cookies[i];

			if (thisCookie.getName().equals(name) && !"".equals(thisCookie.getValue())) {
				returnCookie = thisCookie;

				break;
			}
		}

		return returnCookie;
	}

	/**
	 * Convenience method for deleting a cookie by name
	 * 
	 * @param response
	 *            the current web response
	 * @param cookie
	 *            the cookie to delete
	 * @param path
	 *            the path on which the cookie was set (i.e. /appfuse)
	 */
	public static void deleteCookie(HttpServletResponse response, Cookie cookie, String path) {
		if (cookie != null) {
			// Delete the cookie by setting its maximum age to zero
			cookie.setMaxAge(0);
			cookie.setPath(path);
			response.addCookie(cookie);
		}
	}

	/**
	 * Convenience method to get the application's URL based on request variables.
	 * 
	 * @param request
	 * @return string
	 */
	public static String getAppURL(HttpServletRequest request) {
		StringBuilder url = new StringBuilder();
		int port = request.getServerPort();
		if (port < 0) {
			port = 80; // Work around java.net.URL bug
		}
		String scheme = request.getScheme();
		url.append(scheme);
		url.append("://");
		url.append(request.getServerName());
		if (("http".equals(scheme) && (port != 80)) || ("https".equals(scheme) && (port != 443))) {
			url.append(':');
			url.append(port);
		}
		url.append(request.getContextPath());
		return url.toString();
	}

	public static String getBrowserIp(ServletRequest request) {
		String s = null;
		if (request instanceof HttpServletRequest)
			s = ((HttpServletRequest) request).getHeader(XFF);
		if (StringUtils.isBlank(s))
			s = request.getRemoteAddr();
		else if (IP6.equals(s)) {
			s = request.getRemoteAddr();
		}
		return s;
	}

	public static String getBrowserUserAgentIp(HttpServletRequest request) {
		return request.getHeader(USERAGENT);
	}

	public static String getBrowserInfoForLog(HttpServletRequest request) {
		String s = getBrowserIp(request) + "/" + getBrowserUserAgentIp(request);
		if (s.length() > BROWSE_LOG_LENGTH)
			s = s.substring(0, BROWSE_LOG_LENGTH - 1);
		return s;
	}
}
