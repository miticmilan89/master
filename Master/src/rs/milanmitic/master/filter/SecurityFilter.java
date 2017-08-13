package rs.milanmitic.master.filter;

import java.io.IOException;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.support.ServletContextResource;

import rs.milanmitic.master.common.Constants;
import rs.milanmitic.master.common.ContextHolder;
import rs.milanmitic.master.common.data.LoggedUserData;
import rs.milanmitic.master.common.util.Utils;

public class SecurityFilter implements Filter {
	private static final Logger log = LogManager.getLogger(SecurityFilter.class);
	/**
	 * List of URLs that we do not check
	 */
	private static Set<String> ignoreUrls = null;

	/**
	 * Default constructor.
	 */
	public SecurityFilter() {
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

		ServletContextResource resource = new ServletContextResource(req.getSession().getServletContext(), req.getRequestURI());
		String path = resource.getPath();
		path = StringUtils.remove(path, req.getContextPath());

		if (path.indexOf(';') != -1) {
			path = path.substring(0, path.indexOf(';'));
		}
		log.debug("---> SecurityFilter permission URI:{}", path);

		// SECURITY_COMMENT
		if (!ContextHolder.isSecurityEnabled()) {
			chain.doFilter(request, response);
			return;
		}

		LoggedUserData loggedUser = ContextHolder.getLoggedUser();
		boolean ignoredUrl = Utils.containsUrl(path, ignoreUrls);
		if (ignoredUrl) {
			log.debug("---> SecurityFilter globalIgnore:{}, path:{}", ignoredUrl, path);
			chain.doFilter(request, response);
		} else {

			if (loggedUser == null) {
				// should never happen
				log.debug("---> SecurityFilter Here user if not logged should not have access");
				throw new IllegalArgumentException("Here user if not logged should not have access");
			} else {

				// now check can user access this URL
				if (!Utils.checkUrlRights(path, loggedUser)) {
					log.debug("---> SecurityFilter ACCESS DENIED path:{}", path);
					String failureUrl = Utils.getHttpsOrHttpUrl(req, "/error403.jsp" + (Constants.isDebugMode() ? "?url=" + path : ""));
					log.debug("failureUrl:{}", failureUrl);

					((HttpServletResponse) response).sendRedirect(failureUrl);
					return;
				}
				chain.doFilter(request, response);
			}
		}

	}

	public static Set<String> getIgnoreUrls() {
		return ignoreUrls;
	}

	public static void setIgnoreUrls(Set<String> ignoreUrls) {
		SecurityFilter.ignoreUrls = ignoreUrls;
	}

}
