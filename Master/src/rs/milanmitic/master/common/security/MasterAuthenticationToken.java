package rs.milanmitic.master.common.security;

import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import rs.milanmitic.master.common.data.FormatPatterns;
import rs.milanmitic.master.model.AppUser;

/**
 * Master autentification token
 * 
 * @author milan
 * 
 */
public class MasterAuthenticationToken extends UsernamePasswordAuthenticationToken {

	private static final long serialVersionUID = 1L;

	private UserType userType;
	private boolean forcePasswordChange = false;
	private boolean passwordExipred = false;

	private AppUser appUser;
	private HashSet<String> urlList;

	private FormatPatterns formatPatterns;

	public MasterAuthenticationToken(Object principal, Object credentials) {
		super(principal, credentials);
	}

	public MasterAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
		super(principal, credentials, authorities);
	}

	public MasterAuthenticationToken(String username, String password, UserType userType) {
		super(username, password);
		this.userType = userType;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public boolean isForcePasswordChange() {
		return forcePasswordChange;
	}

	public void setForcePasswordChange(boolean forcePasswordChange) {
		this.forcePasswordChange = forcePasswordChange;
	}

	public boolean isPasswordExipred() {
		return passwordExipred;
	}

	public void setPasswordExipred(boolean passwordExipred) {
		this.passwordExipred = passwordExipred;
	}

	public FormatPatterns getFormatPatterns() {
		return formatPatterns;
	}

	public void setFormatPatterns(FormatPatterns formatPatterns) {
		this.formatPatterns = formatPatterns;
	}

	public HashSet<String> getUrlList() {
		return urlList;
	}

	public void setUrlList(HashSet<String> urlList) {
		this.urlList = urlList;
	}

	/**
	 * @return the appUser
	 */
	public AppUser getAppUser() {
		return appUser;
	}

	/**
	 * @param appUser
	 *            the appUser to set
	 */
	public void setAppUser(AppUser appUser) {
		this.appUser = appUser;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof MasterAuthenticationToken)) {
			return false;
		}
		MasterAuthenticationToken castOther = (MasterAuthenticationToken) other;
		return new EqualsBuilder().append(getUserType(), castOther.getUserType()).append(isForcePasswordChange(), castOther.isForcePasswordChange()).append(isPasswordExipred(), castOther.isPasswordExipred())
				.append(getAppUser(), castOther.getAppUser()).append(getUrlList(), castOther.getUrlList()).append(getFormatPatterns(), castOther.getFormatPatterns()).isEquals();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getUserType()).append(isForcePasswordChange()).append(isPasswordExipred()).append(getAppUser()).append(getUrlList()).append(getFormatPatterns()).toHashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("userType", getUserType()).append("forcePasswordChange", isForcePasswordChange()).append("passwordExipred", isPasswordExipred()).append("appUser", getAppUser()).append("urlList", getUrlList())
				.append("formatPatterns", getFormatPatterns()).toString();
	}

}