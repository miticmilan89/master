package rs.milanmitic.master.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import rs.milanmitic.master.common.pagging.SearchInput;
import rs.milanmitic.master.common.util.Utils;

/**
 *
 * <p>
 * Title: AppUserAppRole
 * </p>
 *
 * <p>
 * Description: Domain Object describing a AppUserAppRole entity
 * </p>
 *
 */
@NamedQueries({ @NamedQuery(name = "getAssignedRolesForUser", query = "from AppRole a where a.id IN (SELECT f.appRoleFk FROM AppUserAppRole f WHERE f.appUserFk=:appUserFk) order by a.roleName"),
		@NamedQuery(name = "getNotAssignedRolesForUser", query = "from AppRole a where a.id NOT IN (SELECT f.appRoleFk FROM AppUserAppRole f WHERE f.appUserFk=:appUserFk) order by a.roleName") })
@Entity(name = "AppUserAppRole")
@Table(name = "APP_USER_APP_ROLE")
public class AppUserAppRole extends SearchInput implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "APP_ROLE_FK", referencedColumnName = "ID", nullable = false, unique = false, insertable = false, updatable = false)
	private AppRole appRole;

	@Column(name = "APP_ROLE_FK", nullable = false, unique = false)
	private Long appRoleFk;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "APP_USER_FK", referencedColumnName = "ID", nullable = false, unique = false, insertable = false, updatable = false)
	private AppUser appUser;

	@Column(name = "APP_USER_FK", nullable = false, unique = false)
	private Long appUserFk;

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
	 * @return the appRole
	 */
	public AppRole getAppRole() {
		return appRole;
	}

	/**
	 * @param appRole
	 *            the appRole to set
	 */
	public void setAppRole(AppRole appRole) {
		this.appRole = appRole;
	}

	/**
	 * @return the appRoleFk
	 */
	public Long getAppRoleFk() {
		return appRoleFk;
	}

	/**
	 * @param appRoleFk
	 *            the appRoleFk to set
	 */
	public void setAppRoleFk(Long appRoleFk) {
		this.appRoleFk = appRoleFk;
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
	 * @return the appUserFk
	 */
	public Long getAppUserFk() {
		return appUserFk;
	}

	/**
	 * @param appUserFk
	 *            the appUserFk to set
	 */
	public void setAppUserFk(Long appUserFk) {
		this.appUserFk = appUserFk;
	}

	@Override
	public String toString() {
		return Utils.toStringGlobal(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appRoleFk == null) ? 0 : appRoleFk.hashCode());
		result = prime * result + ((appUserFk == null) ? 0 : appUserFk.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AppUserAppRole other = (AppUserAppRole) obj;
		if (appRoleFk == null) {
			if (other.appRoleFk != null) {
				return false;
			}
		} else if (!appRoleFk.equals(other.appRoleFk)) {
			return false;
		}
		if (appUserFk == null) {
			if (other.appUserFk != null) {
				return false;
			}
		} else if (!appUserFk.equals(other.appUserFk)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}
