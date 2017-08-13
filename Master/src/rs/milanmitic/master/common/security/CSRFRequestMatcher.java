package rs.milanmitic.master.common.security;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

@Component
public class CSRFRequestMatcher implements RequestMatcher {

	private Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");

	// private RegexRequestMatcher apiMatcher = new RegexRequestMatcher("/v[0-9]*/.*", null);

	@Override
	public boolean matches(HttpServletRequest request) {
		// CSRF disabled on allowedMethod
		if (allowedMethods.matcher(request.getMethod()).matches())
			return false;
		String path = request.getRequestURI();
		path = StringUtils.remove(path, request.getContextPath());

		boolean isPost = "POST".equalsIgnoreCase(request.getMethod());

		if (isPost && path.startsWith("/sessionCheck"))
			return false;

		// CRLF not enabled ON GET
		if ("GET".equalsIgnoreCase(request.getMethod()))
			return false;

		// CSRF disabled on api calls
		// if (apiMatcher.matches(request))
		// return false;

		// CSRF enables for other requests
		return true;
	}

}
