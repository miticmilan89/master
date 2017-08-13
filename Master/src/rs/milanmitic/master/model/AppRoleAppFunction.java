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
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import rs.milanmitic.master.common.pagging.SearchInput;
import rs.milanmitic.master.common.protector.HiddenFieldBuilder;
import rs.milanmitic.master.common.protector.HiddenFieldsSecureInterface;
import rs.milanmitic.master.common.util.Utils;

/**
 * <p>
 * Title: AppRoleAppFunction
 * </p>
 *
 * <p>
 * Description: Domain Object describing a AppRoleAppFunction entity
 * </p>
 *
 */
@NamedQueries({
	@NamedQuery(
		name="getAssignedFunctionsForRole",
		query="from AppFunction a where a.id IN (SELECT f.appFunctionFk FROM AppRoleAppFunction f WHERE f.appRoleFk=:appRoleFk) order by a.functionName"
	),
	@NamedQuery(
		name="getNotAssignedFunctionsForRole",
		query="from AppFunction a where a.id NOT IN (SELECT f.appFunctionFk FROM AppRoleAppFunction f WHERE f.appRoleFk=:appRoleFk) order by a.functionName"
	)
})
@Entity(name = "AppRoleAppFunction")
@Table(name = "APP_ROLE_APP_FUNCTION")
public class AppRoleAppFunction extends SearchInput implements Serializable, HiddenFieldsSecureInterface {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "APP_ROLE_FK", nullable = false, unique = false)
	@NotNull
	private Long appRoleFk;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "APP_ROLE_FK", referencedColumnName = "ID", nullable = false, unique = false, insertable = false, updatable = false)
	private AppRole appRole;

	@Column(name = "APP_FUNCTION_FK", nullable = false, unique = false)
	@NotNull
	private Long appFunctionFk;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "APP_FUNCTION_FK", referencedColumnName = "ID", nullable = false, unique = false, insertable = false, updatable = false)
	private AppFunction appFunction;

	public AppRoleAppFunction() {
		super();
	}

	@Override
	public String toString() {
		return Utils.toStringGlobal(this);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAppRoleFk() {
		return appRoleFk;
	}

	public void setAppRoleFk(Long appRoleFk) {
		this.appRoleFk = appRoleFk;
	}

	public Long getAppFunctionFk() {
		return appFunctionFk;
	}

	public void setAppFunctionFk(Long appFunctionFk) {
		this.appFunctionFk = appFunctionFk;
	}

	/** * {@inheritDoc} */
	@Override
	public boolean equals(final Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof AppRoleAppFunction))
			return false;
		AppRoleAppFunction castOther = (AppRoleAppFunction) other;
		return new EqualsBuilder().append(getId(), castOther.getId()).append(getAppRoleFk(), castOther.getAppRoleFk()).append(getAppFunctionFk(), castOther.getAppFunctionFk()).isEquals();
	}

	/** * {@inheritDoc} */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getId()).append(getAppRoleFk()).append(getAppFunctionFk()).toHashCode();
	}

	/**
	 * @see HiddenFieldsSecureInterface#generateSecureFieldsHash()
	 */
	@Override
	public String getSecureFields() {
		HiddenFieldBuilder h = HiddenFieldBuilder.create(this.getClass().getName());
		if (id != null) {
			h.add(id);
		}
		return h.toString();
	}

	/**
	 * @see HiddenFieldsSecureInterface#generateSecureFieldsHash()
	 */
	@Override
	public String generateSecureFieldsHash() {
		return Utils.generateSecureFieldsHash(this);
	}

}
