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
 * Title: AppConfig
 * </p>
 *
 * <p>
 * Description: Domain Object describing a AppConfig entity
 * </p>
 *
 */
@Entity(name = "AppConfig")
@Table(name = "APP_CONFIG")
public class AppConfig extends SearchInput implements Serializable, HiddenFieldsSecureInterface {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "PARTICIPANT_FK", nullable = true, unique = false)
	private Long participantFk;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "PARTICIPANT_FK", referencedColumnName = "ID", nullable = false, unique = false, insertable = false, updatable = false)
	private Participant participant;

	@Column(name = "PARAM_NAME", nullable = false, unique = false)
	@Length(max = 30)
	@NotEmpty
	private String paramName;

	@Column(name = "PARAM_VALUE", nullable = false, unique = false)
	@Length(max = 100)
	@NotEmpty
	private String paramValue;

	@Column(name = "PARAM_DESC", nullable = true, unique = false)
	@Length(max = 500)
	private String paramDesc;

	public AppConfig() {
		super();
		addColumnMapping("1", "a.id");
		addColumnMapping("5", "a.participantFk");
		addColumnMapping("8", "lower(a.paramName)");
		addColumnMapping("9", "lower(a.paramValue)");
		addColumnMapping("10", "lower(a.paramDesc)");
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

	public Long getParticipantFk() {
		return participantFk;
	}

	public void setParticipantFk(Long participantFk) {
		this.participantFk = participantFk;
	}

	public Participant getParticipant() {
		return participant;
	}

	public void setParticipant(Participant participant) {
		this.participant = participant;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public String getParamDesc() {
		return paramDesc;
	}

	public void setParamDesc(String paramDesc) {
		this.paramDesc = paramDesc;
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
		if (participantFk != null) {
			h.add(participantFk);
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AppConfig)) {
			return false;
		}
		AppConfig castOther = (AppConfig) other;
		return new EqualsBuilder().append(getId(), castOther.getId()).append(getParticipantFk(), castOther.getParticipantFk()).append(getParamName(), castOther.getParamName()).append(getParamValue(), castOther.getParamValue())
				.append(getParamDesc(), castOther.getParamDesc()).isEquals();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getId()).append(getParticipantFk()).append(getParamName()).append(getParamValue()).append(getParamDesc()).toHashCode();
	}

}
