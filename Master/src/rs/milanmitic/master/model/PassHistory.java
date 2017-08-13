package rs.milanmitic.master.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import rs.milanmitic.master.common.pagging.SearchInput;
import rs.milanmitic.master.common.protector.HiddenFieldBuilder;
import rs.milanmitic.master.common.protector.HiddenFieldsSecureInterface;
import rs.milanmitic.master.common.util.Utils;

/**
 * <p>
 * Title: PassHistory
 * </p>
 *
 * <p>
 * Description: Domain Object describing a PassHistory entity
 * </p>
 *
 */
@Entity(name = "PassHistory")
@Table(name = "PASS_HISTORY")
public class PassHistory extends SearchInput implements Serializable, HiddenFieldsSecureInterface {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "CHANGED_DT", nullable = false, unique = false)
	@NotNull
	private Timestamp changedDt;

	@Column(name = "APP_USER_FK", nullable = false, unique = false)
	@NotNull
	private Long appUserFk;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "APP_USER_FK", referencedColumnName = "ID", nullable = false, unique = false, insertable = false, updatable = false)
	private AppUser appUser;

	@Column(name = "PASSWORD_HASH", nullable = false, unique = false)
	@Length(max = 72)
	@NotEmpty
	private String passwordHash;

	public PassHistory() {
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

	public Timestamp getChangedDt() {
		return changedDt;
	}

	public void setChangedDt(Timestamp changedDt) {
		this.changedDt = changedDt;
	}

	public Long getAppUserFk() {
		return appUserFk;
	}

	public void setAppUserFk(Long appUserFk) {
		this.appUserFk = appUserFk;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	/** * {@inheritDoc} */
	@Override
	public boolean equals(final Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof PassHistory))
			return false;
		PassHistory castOther = (PassHistory) other;
		return new EqualsBuilder().append(getId(), castOther.getId()).append(getChangedDt(), castOther.getChangedDt()).append(getAppUserFk(), castOther.getAppUserFk()).append(getPasswordHash(), castOther.getPasswordHash()).isEquals();
	}

	/** * {@inheritDoc} */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getId()).append(getChangedDt()).append(getAppUserFk()).append(getPasswordHash()).toHashCode();
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
