package rs.milanmitic.master.filter;

import java.io.File;
import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.support.ServletContextResource;

public class MasterResourceServlet extends org.springframework.web.servlet.ResourceServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger log = LogManager.getLogger(MasterResourceServlet.class);

	protected static String defaultResourceNotFoundRedirectUrl = "/app/resourcenotfound";

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			ServletContext servletContext = getServletContext();

			ServletContextResource resource = new ServletContextResource(servletContext, req.getRequestURI());
			String path = resource.getPath();
			path = StringUtils.remove(path, req.getContextPath());
			path = StringUtils.remove(path, "/resources");

			String defaultPath = path;
			if (path.indexOf("/theme") >= 0) {
				String themeResourcePath = path.substring("/theme".length() + 1);
				defaultPath = "/theme/default" + themeResourcePath.substring(themeResourcePath.indexOf('/'));
			}

			File realResource = new File(servletContext.getRealPath(path));
			File defaultRealResource = new File(servletContext.getRealPath(defaultPath));

			if (!realResource.exists() && !defaultRealResource.exists()) {
				super.service(req, resp);
			} else if (realResource.exists() && realResource.isFile()) {
				RequestDispatcher rd = req.getRequestDispatcher(path);
				rd.include(req, resp);
				return;
			} else if (defaultRealResource.exists() && defaultRealResource.isFile()) {
				RequestDispatcher rd = req.getRequestDispatcher(defaultPath);
				rd.include(req, resp);
				return;
			} else {
				throw new ServletException("Resource: [" + path + "] not found.");
			}

		} catch (ServletException e) {
			String requestedURL = req.getRequestURL().toString();
			log.error("requestedURL: " + requestedURL, e);
			resp.sendRedirect(req.getContextPath() + defaultResourceNotFoundRedirectUrl);
		}
	}

	public void setDefaultResourceNotFoundRedirectUrl(String x) {
		setResourceNotFoundRedirectUrl(x);
	}

	public static void setResourceNotFoundRedirectUrl(String x) {
		defaultResourceNotFoundRedirectUrl = x;
	}

}
