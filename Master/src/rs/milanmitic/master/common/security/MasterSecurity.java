package rs.milanmitic.master.common.security;

import org.springframework.stereotype.Component;

import rs.milanmitic.master.common.ContextHolder;
import rs.milanmitic.master.common.data.LoggedUserData;

/**
 * Check user authorizations
 * 
 * @author milan
 * 
 */
@Component("masterSecurity")
public class MasterSecurity {

	/**
	 * Check is user has one of supplied roles
	 * 
	 * @param userType
	 * @return
	 */
	public boolean hasRole(UserType... userType) {
		LoggedUserData lgd = ContextHolder.getLoggedUser();
		if (lgd == null)
			return false;
		for (UserType ut : userType) {
			boolean b = lgd.getUserType().equals(ut);
			if (b)
				return true;
		}

		return false;
	}

	/**
	 * Check is user has PARTICIPANT role
	 * 
	 * @return
	 */
	public boolean hasParticipantRole() {
		LoggedUserData lgd = ContextHolder.getLoggedUser();
		if (lgd == null)
			return false;
		return lgd.getUserType().equals(UserType.USER);
	}

	/**
	 * Check is user has ADMIN role
	 * 
	 * @return
	 */
	public boolean hasAdminRole() {
		LoggedUserData lgd = ContextHolder.getLoggedUser();
		if (lgd == null)
			return false;
		return lgd.getUserType().equals(UserType.ADMIN);
	}

	/**
	 * Check is user has one of user roles
	 * 
	 * @return
	 */
	public boolean hasAnyUserRole() {
		LoggedUserData lgd = ContextHolder.getLoggedUser();
		if (lgd == null)
			return false;
		if (lgd.getUserType().equals(UserType.ADMIN))
			return true;
		if (lgd.getUserType().equals(UserType.USER))
			return true;
		return false;

	}

}
