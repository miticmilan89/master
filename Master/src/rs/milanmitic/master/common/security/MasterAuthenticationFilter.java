package rs.milanmitic.master.common.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import rs.milanmitic.master.common.Constants;
import rs.milanmitic.master.common.ContextHolder;
import rs.milanmitic.master.common.data.LoggedAdminData;
import rs.milanmitic.master.common.data.LoggedParticipantData;
import rs.milanmitic.master.common.data.LoggedUserData;
import rs.milanmitic.master.common.exception.UnpredictableException;
import rs.milanmitic.master.common.exception.ValidateException;
import rs.milanmitic.master.common.util.RequestUtil;
import rs.milanmitic.master.common.util.Utils;
import rs.milanmitic.master.model.ActivityLog;
import rs.milanmitic.master.service.CommonService;

@Component(value = "masterAuthenticationFilter")
public class MasterAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	protected static final Logger log = LogManager.getLogger(MasterAuthenticationFilter.class);

	private String usernameParameter = "username";
	private String pswd = "password";

	@Autowired
	private CommonService commonService;

	@Autowired
	public MasterAuthenticationFilter(@Qualifier("masterAuthenticationProvider") AuthenticationProvider authenticationProvider) {
		super("/app/login-process");
		List<AuthenticationProvider> authenticationProviderList = new ArrayList<AuthenticationProvider>();
		authenticationProviderList.add(authenticationProvider);
		AuthenticationManager authenticationManager = new ProviderManager(authenticationProviderList);
		super.setAuthenticationManager(authenticationManager);

		AuthenticationSuccessHandler a = new SimpleUrlAuthenticationSuccessHandler("/app/home");
		super.setAuthenticationSuccessHandler(a);

	}


	protected String obtainPassword(HttpServletRequest request) {
		return request.getParameter(pswd);
	}

	protected String obtainUsername(HttpServletRequest request) {
		return request.getParameter(usernameParameter);
	}

	public void setUsernameParameter(String usernameParameter) {
		Assert.hasText(usernameParameter, "Username parameter must not be empty or null");
		this.usernameParameter = usernameParameter;
	}

	public void setPswd(String passwordParameter) {
		Assert.hasText(passwordParameter, "Password parameter must not be empty or null");
		this.pswd = passwordParameter;
	}

	protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) req;
		final HttpServletResponse response = (HttpServletResponse) res;
		if ("POST".equalsIgnoreCase(request.getMethod())) {
			// If the incoming request is a POST, then we send it up
			// to the AbstractAuthenticationProcessingFilter.
			super.doFilter(request, response, chain);
		} else {
			// If it's a GET, we ignore this request and send it
			// to the next filter in the chain. In this case, that
			// pretty much means the request will hit the /login
			// controller which will process the request to show the
			// login page.
			chain.doFilter(request, response);

		}
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response){
		if (!"POST".equalsIgnoreCase(request.getMethod())) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}
		MasterAuthenticationToken authRequest = null;
		try {

			String username = obtainUsername(request);
			String pswdVal = obtainPassword(request);
			String loginType = request.getParameter("loginType");

			if (StringUtils.isNotBlank(loginType))
				request.getSession().setAttribute("loginType", loginType);

			username = username == null ? "" : username.trim().toUpperCase();
			pswdVal = pswdVal == null ? "" : pswdVal.trim();

			String loadBalancerUrl = Utils.getHttpsUrl(request);
			log.debug("loadBalancerUrl:{}", loadBalancerUrl);
			String failureUrl = "/app/login/" + loginType + "?error";
			
			log.debug("failureUrl:{}", failureUrl);
			AuthenticationFailureHandler b = new SimpleUrlAuthenticationFailureHandler(failureUrl);
			super.setAuthenticationFailureHandler(b);

			authRequest = new MasterAuthenticationToken(username, pswdVal, UserType.getById(loginType));

			// Allow subclasses to set the "details" property
			setDetails(request, authRequest);
			Authentication authentication = tryToLogin(request, authRequest, username);

			if (!(authentication instanceof MasterAuthenticationToken))
				throw new UnpredictableException("Undesirable toke type");

			MasterAuthenticationToken master = (MasterAuthenticationToken) authentication;
			LoggedUserData ld = createUserData(username, pswdVal, master);

			// change language to value from DB
			request.getSession().setAttribute("_CHG_LOC", StringUtils.isNotBlank(ld.getFormatPatterns().getLanguage()) ? ld.getFormatPatterns().getLanguage() : Constants.DEFAULT_LANGUAGE_VALUE);

			request.getSession().setAttribute(Constants.LOGGED_USER, ld);

			if (StringUtils.isNotBlank(ld.getFormatPatterns().getThemeName())) {
				request.getSession().setAttribute(Constants.CURRENT_THEME, ld.getFormatPatterns().getThemeName());
			}
			request.getSession().removeAttribute("menuRepository");

			if (Constants.WEB_SECURITY_SESSION_TIMEOUT_SEC != null) {
				log.info(">>SET SESSION MAX INACTIVITY TIME:{}", Constants.WEB_SECURITY_SESSION_TIMEOUT_SEC);
				request.getSession().setMaxInactiveInterval(Constants.WEB_SECURITY_SESSION_TIMEOUT_SEC);
			}
			return authentication;
		} catch (ValidateException ve) {
			// in case of error, just make s short sleep, in order to prevent brute force attack
			Utils.threadSleep(1500);

			log.debug("Error", ve);
			if (ve.getMessage() != null && ve.getMessage().startsWith("error.AD00000.login")) {
				request.getSession().setAttribute("_errLogin", ve.getMessage());

			} else
				request.getSession().setAttribute("_errLogin", "1");
			log.debug("Validation Error, login, authRequest:{}, err:{}", authRequest, ve.getMessage());
			throw new AuthenticationServiceException(ve.getMessage());
		} catch (AuthenticationException e) {
			log.debug("Error", e);
			// in case of error, just make s short sleep, in order to prevent brute force attack
			Utils.threadSleep(1500);

			throw e;
		}
	}

	private LoggedUserData createUserData(String username, String pswdVal, MasterAuthenticationToken mat) {
		LoggedUserData ld;
		if (UserType.ADMIN.equals(mat.getUserType())) {
			ld = new LoggedAdminData();
			ld.setFormatPatterns(mat.getFormatPatterns());

		} else if (UserType.USER.equals(mat.getUserType())) {
			ld = new LoggedParticipantData();
			ld.setFormatPatterns(mat.getFormatPatterns());
			((LoggedParticipantData) ld).setParticipantId(mat.getAppUser().getParticipantFk());
			((LoggedParticipantData) ld).setParticipantName(mat.getAppUser().getParticipant().getName());
		} else
			throw new IllegalArgumentException("Unknown user type:" + mat.getUserType());

		ld.setId(mat.getAppUser().getId());
		ld.setForcePasswordChange(mat.isForcePasswordChange());
		ld.setPasswordExipred(mat.isPasswordExipred());
		String encPasswd = commonService.getSecurityManager().hashEncodeString(pswdVal);
		ld.setPassword(encPasswd);
		ld.setUsername(username);

		ld.setUrlList(mat.getUrlList());
		return ld;
	}


	private Authentication tryToLogin(HttpServletRequest request, MasterAuthenticationToken authRequest, String username) {
		boolean isContextCreated = ContextHolder.isContextCreated();
		Authentication authentication = null;
		try {
			if (!isContextCreated)
				ContextHolder.createContext(request);

			ContextHolder.createUserTransactionLog(request);

			ActivityLog ev = ContextHolder.getActivityLog();
			ev.addActivity("browserInfo", RequestUtil.getBrowserUserAgentIp(request));
			ev.addActivity("username", username);
			ev.addActivity("usertype", authRequest.getUserType().getId());

			authentication = this.getAuthenticationManager().authenticate(authRequest);
		} finally {
			if (!isContextCreated)
				ContextHolder.clearContext();
		}
		return authentication;
	}

}