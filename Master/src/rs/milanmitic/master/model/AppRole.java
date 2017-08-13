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
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import rs.milanmitic.master.common.pagging.SearchInput;
import rs.milanmitic.master.common.protector.HiddenFieldBuilder;
import rs.milanmitic.master.common.protector.HiddenFieldsSecureInterface;
import rs.milanmitic.master.common.util.Utils;

/**
 *
 * <p>
 * Title: AppRole
 * </p>
 *
 * <p>
 * Description: Domain Object describing a AppRole entity
 * </p>
 *
 */
@Entity(name = "AppRole")
@Table(name = "APP_ROLE")
public class AppRole extends SearchInput implements Serializable, HiddenFieldsSecureInterface {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "ROLE_NAME", length = 50, nullable = true, unique = false)
	@NotEmpty
	private String roleName;

	@Column(name = "PARTICIPANT_FK", nullable = true, unique = false)
	private Long participantFk;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "PARTICIPANT_FK", referencedColumnName = "ID", nullable = true, unique = false, insertable = false, updatable = false)
	private Participant participant;

	@Version
	@Column(name = "VERSION", nullable = true, unique = false)
	private Integer version;

	@Override
	public String toString() {
		return Utils.toStringGlobal(this);
	}

	public AppRole() {
		addColumnMapping("1", "a.id");
		addColumnMapping("2", "lower(a.roleName)");
		addColumnMapping("3", "a.participantFk");
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
	 * @return the roleName
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * @param roleName
	 *            the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * @return the participantFk
	 */

	public Long getParticipantFk() {
		return participantFk;
	}

	/**
	 * @param participantFk
	 *            the participantFk to set
	 */
	public void setParticipantFk(Long participantFk) {
		this.participantFk = participantFk;
	}

	public Participant getParticipant() {
		return participant;
	}

	public void setParticipant(Participant participant) {
		this.participant = participant;
	}

	/**
	 * @return the version
	 */
	public Integer getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AppRole)) {
			return false;
		}
		AppRole castOther = (AppRole) other;
		return new EqualsBuilder().append(getId(), castOther.getId()).append(getRoleName(), castOther.getRoleName()).append(getVersion(), castOther.getVersion()).isEquals();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getId()).append(getRoleName()).append(getVersion()).toHashCode();
	}
	
	@Override
	public String getSecureFields() {
		HiddenFieldBuilder h = HiddenFieldBuilder.create(this.getClass().getName()).add(this.version);
		if (id != null) {
			h.add(id);
		}
		return h.toString();
	}

	@Override
	public String generateSecureFieldsHash() {
		return Utils.generateSecureFieldsHash(this);
	}

}
