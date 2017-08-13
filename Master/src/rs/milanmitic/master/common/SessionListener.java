package rs.milanmitic.master.common;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.web.context.support.WebApplicationContextUtils;

import rs.milanmitic.master.common.data.LoggedUserData;
import rs.milanmitic.master.service.CommonService;

/**
 * For later use
 * 
 * @author milan
 * 
 */
public class SessionListener implements HttpSessionListener, HttpSessionAttributeListener {

	private static final Logger log = LogManager.getLogger(SessionListener.class);

	/*
	 * (non-Java-doc)
	 * 
	 * @see java.lang.Object#Object()
	 */
	public SessionListener() {
		super();

	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpSessionListener#sessionCreated(HttpSessionEvent arg0)
	 */
	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		if (log.isDebugEnabled())
			log.debug("+++++++++++++sessionCreated:" + arg0.getSession().getId() + ", arg0.getSession().MaxInactiveInterval:" + arg0.getSession().getMaxInactiveInterval());

	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(HttpSessionEvent sessionEvent)
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent sessionEvent) {
		if (log.isDebugEnabled())
			log.debug("+++++++++++++sessionDestroyed:" + sessionEvent.getSession().getId());
		// set end session time
		LoggedUserData ld = (LoggedUserData) sessionEvent.getSession().getAttribute(Constants.LOGGED_USER);
		if (ld != null) {
			ApplicationContext ctx1 = WebApplicationContextUtils.getRequiredWebApplicationContext(sessionEvent.getSession().getServletContext());
			CommonService commonService = ctx1.getBean(CommonService.class);
			commonService.logoutUser(ld);
		}

	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpSessionAttributeListener#attributeAdded(HttpSessionBindingEvent arg0)
	 */
	@Override
	public void attributeAdded(HttpSessionBindingEvent arg0) {
		writeInfo(arg0, "attributeAdded");
	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpSessionAttributeListener#attributeRemoved(HttpSessionBindingEvent arg0)
	 */
	@Override
	public void attributeRemoved(HttpSessionBindingEvent arg0) {
		writeInfo(arg0, "attributeRemoved");
	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpSessionAttributeListener#attributeReplaced(HttpSessionBindingEvent arg0)
	 */
	@Override
	public void attributeReplaced(HttpSessionBindingEvent arg0) {
		writeInfo(arg0, "attributeReplaced");
	}

	private void writeInfo(HttpSessionBindingEvent arg0, String methodName) {
		if (arg0.getValue() != null && arg0.getValue() instanceof DefaultCsrfToken) {
			DefaultCsrfToken s = (DefaultCsrfToken) arg0.getValue();
			if (log.isDebugEnabled())
				log.debug("+++++++++++++" + methodName + ":" + arg0.getName() + "=" + arg0.getValue() + ", DefaultCsrfToken:" + s.getParameterName() + "=" + s.getToken());
		} else {
			if (log.isDebugEnabled())
				log.debug("+++++++++++++" + methodName + ":" + arg0.getName() + "=" + arg0.getValue());
		}
	}

}