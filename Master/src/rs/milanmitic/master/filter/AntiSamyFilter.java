package rs.milanmitic.master.filter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.springframework.web.util.HtmlUtils;

import rs.milanmitic.master.common.Constants;

/**
 * Servlet filter that checks all request parameters for potential XSS attacks. See http://bazageous.com/2011/04/14/preventing-xss-attacks-with-antisamy/
 * 
 * @author milan
 */
public class AntiSamyFilter implements Filter {

	private static final Logger log = LogManager.getLogger(AntiSamyFilter.class);
	private static final String POLICY_FILE_PARAM = "antisamy-policy-file";
	/**
	 * AntiSamy is unfortunately not immutable, but is thread safe if we only call {@link AntiSamy#scan(String taintedHTML, int scanType)}
	 */
	private AntiSamy antiSamy;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			CleanServletRequest cleanRequest = new CleanServletRequest((HttpServletRequest) request, antiSamy);
			chain.doFilter(cleanRequest, response);
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		try {
			String policyFile = filterConfig.getInitParameter(POLICY_FILE_PARAM);
			String path = Constants.getApplicationPath() + "WEB-INF" + File.separator + "conf" + File.separator + policyFile;
			log.debug("Loading policy file:{}", path);
			Policy policy = Policy.getInstance(new File(path));
			antiSamy = new AntiSamy(policy);
		} catch (Exception e) {
			throw new IllegalStateException("Error initializing antisamy filter", e);
		}
	}

	@Override
	public void destroy() {
		// future use
	}

	/**
	 * Wrapper for a {@link HttpServletRequest} that returns 'safe' parameter values by passing the raw request parameters through the anti-samy filter. Should be private
	 */
	private static class CleanServletRequest extends HttpServletRequestWrapper {

		private static final String[] STRING_ARR_EMPTY = null;
		private final AntiSamy antiSamy;

		private CleanServletRequest(HttpServletRequest request, AntiSamy antiSamy) {
			super(request);
			this.antiSamy = antiSamy;
		}

		/**
		 * overriding getParameter functions in {@link ServletRequestWrapper}
		 */
		@Override
		public String[] getParameterValues(String name) {
			String[] originalValues = super.getParameterValues(name);
			if (originalValues == null) {
				return STRING_ARR_EMPTY;
			}
			List<String> newValues = new ArrayList<String>(originalValues.length);
			int n = originalValues.length;
			for (int i = 0; i < n; i++) {
				String value = originalValues[i];
				newValues.add(filterString(value));
			}
			return newValues.toArray(new String[newValues.size()]);
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Map getParameterMap() {
			Map originalMap = super.getParameterMap();
			Map filteredMap = new HashMap(originalMap.size());
			Iterator it = originalMap.keySet().iterator();
			while (it.hasNext()) {
				String name = (String) it.next();
				filteredMap.put(name, getParameterValues(name));
			}
			return Collections.unmodifiableMap(filteredMap);
		}

		@Override
		public String getParameter(String name) {
			String potentiallyDirtyParameter = super.getParameter(name);
			return filterString(potentiallyDirtyParameter);
		}

		/**
		 * @param potentiallyDirtyParameter
		 *            string to be cleaned
		 * @return a clean version of the same string
		 */
		private String filterString(String potentiallyDirtyParameter1) {
			if (StringUtils.isBlank(potentiallyDirtyParameter1))
				return potentiallyDirtyParameter1;
			try {
				String potentiallyDirtyParameter = HtmlUtils.htmlUnescape(potentiallyDirtyParameter1);

				CleanResults cr = antiSamy.scan(potentiallyDirtyParameter, antiSamy.getPolicy());
				if (cr.getNumberOfErrors() > 0) {
					log.info("antisamy encountered problem with input: {}, err:{}", potentiallyDirtyParameter, cr.getErrorMessages());
				}
				potentiallyDirtyParameter = cr.getCleanHTML();
				potentiallyDirtyParameter = StringEscapeUtils.unescapeXml(potentiallyDirtyParameter);
				return potentiallyDirtyParameter;
			} catch (Exception e) {
				throw new IllegalStateException("Error antisamy clean p:" + potentiallyDirtyParameter1, e);
			}
		}
	}
}
