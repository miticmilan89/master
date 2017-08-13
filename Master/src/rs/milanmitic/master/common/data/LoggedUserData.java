package rs.milanmitic.master.common.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import rs.milanmitic.master.common.EventLogIgnoreFields;
import rs.milanmitic.master.common.security.UserType;
import rs.milanmitic.master.common.util.Utils;

/**
 * User data stored in session
 * 
 * @author milan
 * 
 */
public abstract class LoggedUserData implements Serializable, EventLogIgnoreFields {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String username;
	private String password;
	private boolean forcePasswordChange = false;
	private boolean passwordExipred = false;
	private FormatPatterns formatPatterns = new FormatPatterns();
	private String log4jKey = null;
	private HashSet<String> urlList = null;
	private boolean customizedTheme = false;

	public abstract UserType getUserType();

	public String getUsername() {
		return username;
	}

	public String getUsernameHash() {
		return username != null ? Integer.toString(31 * username.hashCode()) : "";
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public boolean isAdmin() {
		return UserType.ADMIN.equals(getUserType());
	}

	public boolean isUser() {
		return UserType.USER.equals(getUserType());
	}

	public String getLog4jKey() {
		if (log4jKey == null) {
			if (isAdmin())
				log4jKey = getUserType().getId() + "-" + getId() + "-" + getUsername();
			else
				log4jKey = getUserType().getId() + "-" + getId() + "-" + getUsername();
		}
		return log4jKey;
	}

	@Override
	public List<String> getEventIgnoreFields() {
		List<String> l = new ArrayList<String>();
		l.add("password");
		return l;
	}

	public HashSet<String> getUrlList() {
		return urlList;
	}

	public void setUrlList(HashSet<String> urlList) {
		this.urlList = urlList;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the customizedTheme
	 */
	public boolean isCustomizedTheme() {
		return customizedTheme;
	}

	/**
	 * @param customizedTheme
	 *            the customizedTheme to set
	 */
	public void setCustomizedTheme(boolean customizedTheme) {
		this.customizedTheme = customizedTheme;
	}

	@Override
	public String toString() {
		return Utils.toStringGlobal(this);
	}

}
