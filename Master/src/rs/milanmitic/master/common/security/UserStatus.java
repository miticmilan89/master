package rs.milanmitic.master.common.security;

import rs.milanmitic.master.common.exception.UnpredictableException;

/**
 * User status mapping
 * 
 * @author milan
 * 
 */
public enum UserStatus {
	INACTIVE(0), LOCKED(2), ACTIVE(1), FORCE_PASSWORD_CHANGE(3);

	private final Integer id;

	private UserStatus(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public static UserStatus getById(Integer id) {
		for (UserStatus u : values()) {
			if (u.getId().equals(id))
				return u;

		}
		throw new UnpredictableException("Unknown user status:" + id);

	}

	public static boolean isStatusValid(Integer id) {
		for (UserStatus u : values()) {
			if (u.getId().equals(id))
				return true;

		}
		return false;

	}

	public static boolean isActive(Integer code) {
		return ACTIVE.id.equals(code);
	}

	public static boolean isInactive(Integer code) {
		return INACTIVE.id.equals(code);
	}

	public static boolean isLocked(Integer code) {
		return LOCKED.id.equals(code);
	}

	public static boolean isPasswordChange(Integer code) {
		return FORCE_PASSWORD_CHANGE.id.equals(code);
	}

}
