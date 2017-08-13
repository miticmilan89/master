package rs.milanmitic.master.common.security;

import rs.milanmitic.master.common.exception.UnpredictableException;

/**
 * User roles
 * 
 * @author milan
 * 
 */
public enum UserRole {
	ROLE_ADMIN("ROLE_ADMIN"), ROLE_USER("ROLE_USER");

	private final String id;

	private UserRole(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public static UserRole getById(String id) {
		for (UserRole u : values()) {
			if (u.getId().equals(id))
				return u;

		}
		throw new UnpredictableException("Unknown role type:" + id);
	}
}