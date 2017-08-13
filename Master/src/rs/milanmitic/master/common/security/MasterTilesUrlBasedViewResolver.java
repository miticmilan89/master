package rs.milanmitic.master.common.security;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.View;

import rs.milanmitic.master.common.ContextHolder;

/**
 * Support HTTP and HTTPS protocols
 * 
 * @author milan
 * 
 */
public class MasterTilesUrlBasedViewResolver extends org.springframework.web.servlet.view.tiles3.TilesViewResolver {

	private static final Logger log = LogManager.getLogger(MasterTilesUrlBasedViewResolver.class);

	public static final String REDIRECTHTTPS_URL_PREFIX = "redirect:ipghttps:";

	@Override
	protected View createView(String viewName, Locale locale) throws Exception {
		boolean isHttps = ContextHolder.isHTTPS();
		boolean isViewHttps = viewName != null && viewName.startsWith(REDIRECTHTTPS_URL_PREFIX);
		String loadBalancerUrl = ContextHolder.getLoadBalancerUrl();
		if (log.isDebugEnabled())
			log.debug("createView viewName:" + viewName + ", locale:" + locale + " isHTTPS:" + isHttps + ", REDIRECT_URL_PREFIX:" + REDIRECT_URL_PREFIX + ", isViewHttps:" + isViewHttps);

		if (viewName == null) {
			log.error("Supplied view name is NULL");
			return super.createView(viewName, locale);
		}
		if (isRedirect(viewName)) {
			return super.createView(viewName, locale);

		} else if (StringUtils.isNotBlank(loadBalancerUrl)) {
			if (isViewHttps) {
				String url = viewName.substring(REDIRECTHTTPS_URL_PREFIX.length());
				String loadBalancedViewName = REDIRECT_URL_PREFIX + loadBalancerUrl + url;
				return super.createView(loadBalancedViewName, locale);

			} else if (isHttps && viewName.startsWith(REDIRECT_URL_PREFIX)) {
				String url = viewName.substring(REDIRECT_URL_PREFIX.length());
				String loadBalancedViewName = REDIRECT_URL_PREFIX + loadBalancerUrl + url;
				return super.createView(loadBalancedViewName, locale);

			}
		}
		return super.createView(viewName, locale);
	}

	private boolean isRedirect(String viewName) {
		return viewName.startsWith("redirect:https:") || viewName.startsWith("redirect:http:");
	}
}
