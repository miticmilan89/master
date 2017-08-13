package rs.milanmitic.master.common.security;

import rs.milanmitic.master.common.exception.UnpredictableException;

public enum UserType {
	ADMIN("Admin", 0L), USER("User", 1L);

	private final String id;
	private final Long dbId;

	private UserType(String id, Long dbId) {
		this.id = id;
		this.dbId = dbId;
	}

	public String getId() {
		return id;
	}

	public Long getDbId() {
		return dbId;
	}

	public static UserType getById(String id) {
		for (UserType u : values()) {
			if (u.getId().equals(id))
				return u;

		}
		throw new UnpredictableException("Unknown user type:" + id);

	}

}
