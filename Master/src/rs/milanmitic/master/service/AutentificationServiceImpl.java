package rs.milanmitic.master.service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import rs.milanmitic.master.common.aop.MasterLogAnnotation;
import rs.milanmitic.master.common.data.FormatPatterns;
import rs.milanmitic.master.common.exception.ValidateException;
import rs.milanmitic.master.common.security.MasterAuthenticationToken;
import rs.milanmitic.master.common.security.MasterUserPasswordEncoder;
import rs.milanmitic.master.common.security.UserStatus;
import rs.milanmitic.master.model.AppFunction;
import rs.milanmitic.master.model.AppUser;
import rs.milanmitic.master.model.PassPolicy;

/**
 * @see AutentificationService
 * @author milan
 * 
 */
@Service
@Transactional
public class AutentificationServiceImpl implements AutentificationService {

	final protected Logger log = LogManager.getLogger(this.getClass());

	@Autowired
	CommonService commonService;

	@Autowired
	NomenclatureService nomenclatureService;

	@Autowired
	NewTransactionService newTransactionService;

	@Autowired
	private MasterUserPasswordEncoder masterUserPasswordEncoder;

	/**
	 * @see AutentificationService#adminLogin(Authentication)
	 */
	@Override
	@MasterLogAnnotation(eventLogEnabled = false)
	public MasterAuthenticationToken adminLogin(Authentication authentication) {
		// bellow code is based on com.aciworldwide.application.admin.AdminUser.authenticateUser() method

		MasterAuthenticationToken loginBean = (MasterAuthenticationToken) authentication;

		if (loginBean.getPrincipal() == null || StringUtils.isBlank(loginBean.getPrincipal().toString()))
			throw new ValidateException("error.required", "#label.userid");

		if (loginBean.getCredentials() == null || StringUtils.isBlank(loginBean.getCredentials().toString()))
			throw new ValidateException("error.required", "#label.password");

		AppUser appUser = commonService.getAppUserByUserName(loginBean.getPrincipal().toString());
		if (appUser == null) {
			if (log.isDebugEnabled())
				log.debug("User not found: " + loginBean.getPrincipal());
			throw new ValidateException("error.user.notFound", loginBean.getPrincipal());
		}
		if (appUser.getParticipantFk() != null) {
			if (log.isDebugEnabled())
				log.debug("User is BANK user, cannot login as ADMIN, so user not found: " + loginBean.getPrincipal());
			throw new ValidateException("error.user.notFound", loginBean.getPrincipal());
		}
		PassPolicy passPolicy = null;
		if (appUser.getPassPolicyFk() != null) {
			passPolicy = nomenclatureService.getPassPolicyById(appUser.getPassPolicyFk());
		}

		loginBean.setAppUser(appUser);

		// check is user inactive
		if (UserStatus.INACTIVE.getId().equals(appUser.getStatus())) {
			if (log.isDebugEnabled())
				log.debug("Login attempt on inactive user: " + loginBean.getPrincipal());
			throw new ValidateException("error.loginOnInactiveUser", loginBean.getPrincipal());
		}

		checkPasswordPolicy(passPolicy, loginBean, appUser);

		Timestamp currentTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
		appUser.setInvalidLoginAttempt(0);
		appUser.setLastValidLogin(currentTime);
		appUser.setChangedDt(currentTime);
		appUser.setLastInValidLogin(null);

		FormatPatterns formatPatterns = new FormatPatterns();
		loginBean.setFormatPatterns(formatPatterns);

		return loginBean;
	}

	/**
	 * @see AutentificationService#userLogin(Authentication)
	 */
	@Override
	@MasterLogAnnotation(eventLogEnabled = false)
	public MasterAuthenticationToken userLogin(Authentication authentication) {
		// bellow code is based on com.aciworldwide.application.admin.AdminUser.authenticateUser() method

		MasterAuthenticationToken loginBean = (MasterAuthenticationToken) authentication;

		if (loginBean.getPrincipal() == null || StringUtils.isBlank(loginBean.getPrincipal().toString()))
			throw new ValidateException("error.required", "#label.userid");

		if (loginBean.getCredentials() == null || StringUtils.isBlank(loginBean.getCredentials().toString()))
			throw new ValidateException("error.required", "#label.password");

		AppUser appUser = commonService.getAppUserByUserName(loginBean.getPrincipal().toString());
		if (appUser == null) {
			if (log.isDebugEnabled())
				log.debug("User not found: " + loginBean.getPrincipal());
			throw new ValidateException("error.user.notFound", loginBean.getPrincipal());
		}
		if (appUser.getParticipantFk() == null) {
			if (log.isDebugEnabled())
				log.debug("User is ADMIN user, cannot login as BANK, so user not found: " + loginBean.getPrincipal());
			throw new ValidateException("error.user.notFound", loginBean.getPrincipal());
		}
		PassPolicy passPolicy = null;
		if (appUser.getPassPolicyFk() != null) {
			passPolicy = nomenclatureService.getPassPolicyById(appUser.getPassPolicyFk());
		}

		loginBean.setAppUser(appUser);

		// check is user inactive
		if (UserStatus.INACTIVE.getId().equals(appUser.getStatus())) {
			if (log.isDebugEnabled())
				log.debug("Login attempt on inactive user: " + loginBean.getPrincipal());
			throw new ValidateException("error.loginOnInactiveUser", loginBean.getPrincipal());
		}

		checkPasswordPolicy(passPolicy, loginBean, appUser);

		Timestamp currentTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
		appUser.setInvalidLoginAttempt(0);
		appUser.setLastValidLogin(currentTime);
		appUser.setChangedDt(currentTime);
		appUser.setLastInValidLogin(null);

		FormatPatterns formatPatterns = new FormatPatterns();
		loginBean.setFormatPatterns(formatPatterns);

		// now read list of URLS that user can access
		List<AppFunction> list = nomenclatureService.getAppFunctionAccessByUser(appUser.getId());
		generateUrlList(loginBean, list);

		return loginBean;
	}

	private void checkPasswordPolicy(PassPolicy passPolicy, MasterAuthenticationToken loginBean, AppUser appUserBean) {
		Timestamp todayTS = new Timestamp(Calendar.getInstance().getTimeInMillis());
		if (!masterUserPasswordEncoder.matches(loginBean.getCredentials().toString(), appUserBean.getPasswordHash())) {
			// check password and login attempts
			if (passPolicy != null && passPolicy.getPassLoginAttempt() != null) {
				Integer attempts = appUserBean.getInvalidLoginAttempt();
				if (attempts == null || attempts < 0)
					attempts = 1;
				else
					attempts++;
				Integer lockoutLoginAttempts = passPolicy.getPassLoginAttempt();
				appUserBean.setInvalidLoginAttempt(attempts);
				if (log.isDebugEnabled())
					log.debug("Invalid login attempt (#" + attempts + ") on User: " + loginBean.getPrincipal());
				if (attempts >= lockoutLoginAttempts) {
					// lock user
					appUserBean.setLastInValidLogin(new Timestamp(Calendar.getInstance().getTimeInMillis()));
					appUserBean.setStatus(AppUser.STATUS_BLOCKED);
					if (log.isDebugEnabled())
						log.debug("Lock user: " + loginBean.getPrincipal() + ", attempts:" + attempts);
				}
				newTransactionService.update(appUserBean);
			}

			log.debug("Invalid pass: {}, db:{}, app:{}", loginBean.getCredentials(), appUserBean.getPasswordHash(), masterUserPasswordEncoder.encode(java.nio.CharBuffer.wrap(loginBean.getCredentials().toString().toCharArray())));
			throw new ValidateException("error.wrongCredentials");
		}

		// check is user locked
		if (AppUser.STATUS_BLOCKED.equals(appUserBean.getStatus()) && passPolicy != null) {
			if ("N".equals(passPolicy.getPassUnblockAutomatically())) {
				if (log.isDebugEnabled())
					log.debug("Login attempt on locked out User (reinstate = no): " + loginBean.getPrincipal());
				throw new ValidateException("error.loginOnBlockedUser", loginBean.getPrincipal());

			}
			if (passPolicy.getPassBlockWaitTime() != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(appUserBean.getLastInValidLogin());
				cal.add(Calendar.MINUTE, passPolicy.getPassBlockWaitTime());
				Timestamp lockoutTS = new Timestamp(cal.getTime().getTime());
				if (lockoutTS.before(todayTS)) {
					if (log.isDebugEnabled())
						log.debug("User Reinstate performed on User: " + loginBean.getPrincipal());
					appUserBean.setStatus(AppUser.STATUS_ACTIVE);
				} else {
					if (log.isDebugEnabled())
						log.debug("Login attempt on locked out User: " + loginBean.getPrincipal());
					throw new ValidateException("error.loginOnBlockedUser", loginBean.getPrincipal());
				}
			} else {
				if (log.isDebugEnabled())
					log.debug("Login attempt on locked out User: " + loginBean.getPrincipal());
				throw new ValidateException("error.loginOnBlockedUser", loginBean.getPrincipal());
			}
		}

		// check is password expired
		if (passPolicy != null && passPolicy.getPassMaxPeriodInDays() != null && appUserBean.getLastPassChangedDt() != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(appUserBean.getLastPassChangedDt());
			cal.add(Calendar.DATE, passPolicy.getPassMaxPeriodInDays());
			Timestamp expireTS = new Timestamp(cal.getTime().getTime());
			if (expireTS.before(todayTS)) {
				if (log.isDebugEnabled())
					log.debug("Password expired for Participant User:" + loginBean.getPrincipal());
				loginBean.setForcePasswordChange(true);
				loginBean.setPasswordExipred(true);
			}
		}

		// should we force user update password
		if (UserStatus.FORCE_PASSWORD_CHANGE.getId().equals(appUserBean.getStatus())) {
			if (log.isDebugEnabled())
				log.debug("Force password change for User: " + loginBean.getPrincipal());
			loginBean.setForcePasswordChange(true);
		}

	}

	private void generateUrlList(MasterAuthenticationToken loginBean, List<AppFunction> list) {
		loginBean.setUrlList(new HashSet<String>());

		for (AppFunction m : list) {
			if (m.getUrls() != null) {
				StringTokenizer st = new StringTokenizer(m.getUrls(), "|");
				while (st.hasMoreTokens()) {
					loginBean.getUrlList().add(st.nextToken().trim());
				}
			}
		}
	}
}