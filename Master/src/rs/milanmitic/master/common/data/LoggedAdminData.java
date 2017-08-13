package rs.milanmitic.master.common.data;

import java.io.Serializable;

import rs.milanmitic.master.common.security.UserType;
import rs.milanmitic.master.common.util.Utils;

/**
 * ADMIN data used later in application
 * 
 * @author milan
 * 
 */
public class LoggedAdminData extends LoggedUserData implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public UserType getUserType() {
		return UserType.ADMIN;
	}

	@Override
	public String toString() {
		return Utils.toStringGlobal(this);
	}

}
