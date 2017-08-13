package rs.milanmitic.master.common.security;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import rs.milanmitic.master.service.NewTransactionService;

@Service("masterAuthenticationProvider")
@Transactional
public class MasterAuthenticationProvider extends DaoAuthenticationProvider {

	protected final Logger log = LogManager.getLogger(this.getClass());

	@Autowired
	@Lazy
	private rs.milanmitic.master.service.AutentificationService autentificationService;

	@Autowired
	@Lazy
	private NewTransactionService newTransactionService;

	/*
	 * We must have this property here, because we cannot get it from super class
	 */
	private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

	@Autowired
	@Qualifier("userDetailsService")
	@Override
	public void setUserDetailsService(UserDetailsService userDetailsService) {
		super.setUserDetailsService(userDetailsService);
	}

	@Override
	public Authentication authenticate(Authentication authentication) {
		Assert.isInstanceOf(MasterAuthenticationToken.class, authentication, messages.getMessage("error.MasterAuthenticationToken.onlySupports", "Only MasterAuthenticationToken is supported"));
		if (log.isDebugEnabled())
			log.debug("-->authenticate started authentication:" + authentication);

		MasterAuthenticationToken loginBean = (MasterAuthenticationToken) authentication;
		if (log.isDebugEnabled())
			log.debug("authenticate started for user:" + loginBean.getPrincipal() + ", user type:" + loginBean.getUserType().getId());

		Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>();

		if (UserType.ADMIN.equals(loginBean.getUserType())) {
			loginBean = autentificationService.adminLogin(authentication);
			setAuths.add(new SimpleGrantedAuthority(UserRole.ROLE_ADMIN.getId()));

		} else if (UserType.USER.equals(loginBean.getUserType())) {
			loginBean = autentificationService.userLogin(authentication);
			setAuths.add(new SimpleGrantedAuthority(UserRole.ROLE_USER.getId()));
		}

		User user = new User(loginBean.getPrincipal().toString(), loginBean.getCredentials().toString(), true, true, true, true, setAuths);

		Authentication a = createSuccessAuthentication(loginBean.getPrincipal(), authentication, user);
		if (log.isDebugEnabled())
			log.debug("authenticate successfuly for user:" + loginBean.getPrincipal() + ", user type:" + loginBean.getUserType().getId());
		return a;

	}

	@Override
	protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
		// Ensure we return the original credentials the user supplied,
		// so subsequent attempts are successful even with encoded passwords.
		// Also ensure we return the original getDetails(), so that future
		// authentication events after cache expiry contain the details
		MasterAuthenticationToken result = new MasterAuthenticationToken(principal, authentication.getCredentials(), authoritiesMapper.mapAuthorities(user.getAuthorities()));
		MasterAuthenticationToken old = (MasterAuthenticationToken) authentication;
		result.setDetails(authentication.getDetails());
		result.setAppUser(old.getAppUser());
		result.setUserType(old.getUserType());
		result.setPasswordExipred(old.isPasswordExipred());
		result.setForcePasswordChange(old.isForcePasswordChange());

		result.setFormatPatterns(old.getFormatPatterns());
		result.setUrlList(old.getUrlList());

		return result;
	}

	@Override
	public void setAuthoritiesMapper(GrantedAuthoritiesMapper authoritiesMapper) {
		this.authoritiesMapper = authoritiesMapper;
		super.setAuthoritiesMapper(authoritiesMapper);
	}

}
