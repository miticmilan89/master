package rs.milanmitic.master.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import rs.milanmitic.master.common.Constants;
import rs.milanmitic.master.common.ContextHolder;
import rs.milanmitic.master.common.aop.MasterLogAnnotation;
import rs.milanmitic.master.common.data.LoggedUserData;
import rs.milanmitic.master.common.exception.ValidateException;
import rs.milanmitic.master.common.security.MasterTilesUrlBasedViewResolver;
import rs.milanmitic.master.common.security.MasterUserPasswordEncoder;
import rs.milanmitic.master.model.AppUser;
import rs.milanmitic.master.service.NomenclatureService;

/**
 * Handling of common URLs that all three users can execute
 * 
 * @author milan
 * 
 */
@Controller
public class CommonController extends BasicController {
	
	@Autowired
	private NomenclatureService nomenclatureService;

	@Autowired
	protected MasterUserPasswordEncoder masterUserPasswordEncoder;

	private static final String LABEL_LOGIN_TYPE = "loginType";
	
	/**
	 * Prepare page for password change
	 * 
	 * @param request
	 * @return
	 */
	@MasterLogAnnotation
	@RequestMapping(value = "/changePass", method = RequestMethod.GET)
	public String changePass(HttpServletRequest request) {
		LoggedUserData lud = ContextHolder.getLoggedUser();
		boolean isFromLoginPage = lud.isForcePasswordChange();

		return isFromLoginPage ? "changePassAfterLogin" : "changePass";
	}

	/**
	 * Execute password change
	 * 
	 * @param request
	 * @return
	 */
	@MasterLogAnnotation
	@RequestMapping(value = "/changePass", method = RequestMethod.POST)
	public String changePassPOST(HttpServletRequest request) {
		LoggedUserData lud = ContextHolder.getLoggedUser();
		boolean isFromLoginPage = lud.isForcePasswordChange();
		try {
			AppUser appUser = nomenclatureService.getAppUserById(lud.getId());
			String currentPassword = request.getParameter("currentPassword");
			String newPassword = request.getParameter("newPassword");
			String newPasswordRepeat = request.getParameter("newPasswordRepeat");

			if (StringUtils.isBlank(currentPassword))
				throw new ValidateException(getMessage("error.required", "#label.currentPassword"));

			if (!masterUserPasswordEncoder.matches(currentPassword, appUser.getPasswordHash()))
				throw new ValidateException("error.currentPasswordsNotEq");

			if (StringUtils.isBlank(newPassword))
				throw new ValidateException(getMessage("error.required", "#label.newPassword"));

			appUser.setPassword(newPassword);
			appUser.setRepeatPassword(newPasswordRepeat);

			ContextHolder.createUserTransactionLog(request);
			nomenclatureService.updateAppUser(appUser, null, false);
			lud.setForcePasswordChange(false);
			lud.setPasswordExipred(false);
			lud.setPassword(appUser.getPasswordHash());
			getLoggedUser(request).setPassword(appUser.getPasswordHash());

			addMessage(request.getSession(), "message.passwordChangedSuccessfuly");
			return "redirect:/app/home";

		} catch (ValidateException t) {
			if (isFromLoginPage)
				request.getSession().setAttribute("changePassAfterLoginErr", getMessage(t.getMessage(), t.getParams()));
			else
				handleError(request, t);
		} catch (Exception t) {
			log.error("Error, changePassPOST", t);
			if (isFromLoginPage)
				request.getSession().setAttribute("changePassAfterLoginErr", getMessage("error.unpredictableError1"));
			else
				handleError(request, t);
		}

		return isFromLoginPage ? "changePassAfterLogin" : "changePass";
	}

	@MasterLogAnnotation
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String defaultLogin(HttpServletRequest request, HttpServletResponse response, Model model) {
		request.getSession().setAttribute(Constants.CURRENT_THEME, Constants.DEFAULT_THEME);
		return "redirect:/app/login/Admin";
	}
	
	@MasterLogAnnotation
	@RequestMapping(value = "/login/{loginType}", method = RequestMethod.GET)
	public String login(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String loginType) {
		request.getSession().setAttribute(Constants.CURRENT_THEME, Constants.DEFAULT_THEME);
		if (StringUtils.isBlank(loginType))
			loginType = "Admin";
		request.setAttribute(LABEL_LOGIN_TYPE, loginType);

		return "login";
	}

	/**
	 * Logout user
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@MasterLogAnnotation
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request, HttpServletResponse response, Model model) {
		String loginType = (String) request.getSession().getAttribute(LABEL_LOGIN_TYPE);
		boolean ishttps = ContextHolder.isHTTPS();
		try {

			if (StringUtils.isBlank(loginType))
				loginType = request.getParameter(LABEL_LOGIN_TYPE);
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null) {
				new SecurityContextLogoutHandler().logout(request, response, auth);
			}
			SecurityContextHolder.getContext().setAuthentication(null);

			CookieClearingLogoutHandler cookieClearingLogoutHandler = new CookieClearingLogoutHandler(AbstractRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY);
			cookieClearingLogoutHandler.logout(request, response, null);

			request.getSession().removeAttribute("menuRepository");
			request.getSession().removeAttribute(Constants.LOGGED_USER);
		} catch (Exception t) {
			log.error("ERROR(ignored), login/logout", t);
		}
		String x;
		if (StringUtils.isBlank(loginType)) {
			x = !ishttps ? "redirect:/" : MasterTilesUrlBasedViewResolver.REDIRECTHTTPS_URL_PREFIX + "/";

		} else {
			x = !ishttps ? "redirect:/app/login/" + loginType : MasterTilesUrlBasedViewResolver.REDIRECTHTTPS_URL_PREFIX + "/app/login/" + loginType;
		}
		if (log.isDebugEnabled())
			log.debug("goto:" + x + ", ishttps:" + ishttps + ", loginType:" + loginType);
		return x;
	}

}