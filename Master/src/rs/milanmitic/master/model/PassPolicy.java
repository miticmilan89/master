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
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import rs.milanmitic.master.common.SelectFieldIntf;
import rs.milanmitic.master.common.pagging.SearchInput;
import rs.milanmitic.master.common.protector.HiddenFieldBuilder;
import rs.milanmitic.master.common.protector.HiddenFieldsSecureInterface;
import rs.milanmitic.master.common.util.Utils;

/**
 * <p>
 * Title: PassPolicy
 * </p>
 *
 * <p>
 * Description: Domain Object describing a PassPolicy entity
 * </p>
 *
 */
@Entity(name = "PassPolicy")
@Table(name = "PASS_POLICY")
public class PassPolicy extends SearchInput implements Serializable, HiddenFieldsSecureInterface, SelectFieldIntf {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Version
	private Long version;

	@Column(name = "CHANGED_DT", nullable = false, unique = false)
	private Timestamp changedDt;

	@Column(name = "CHANGED_BY_APP_USER_FK", nullable = true, unique = false)
	private Long changedByAppUserFk;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "CHANGED_BY_APP_USER_FK", referencedColumnName = "ID", nullable = false, unique = false, insertable = false, updatable = false)
	private AppUser changedByAppUser;

	@Column(name = "NAME", nullable = false, unique = false)
	@Length(max = 30)
	@NotEmpty
	private String name;

	@Column(name = "PARTICIPANT_FK", nullable = true, unique = false)
	private Long participantFk;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "PARTICIPANT_FK", referencedColumnName = "ID", nullable = false, unique = false, insertable = false, updatable = false)
	private Participant participant;

	@Column(name = "PASS_MIN_PERIOD_IN_DAYS", nullable = true, unique = false)
	private Integer passMinPeriodInDays;

	@Column(name = "PASS_MAX_PERIOD_IN_DAYS", nullable = true, unique = false)
	private Integer passMaxPeriodInDays;

	@Column(name = "PASS_MIN_LENGTH", nullable = true, unique = false)
	private Integer passMinLength;

	@Column(name = "PASS_MIN_HISTORY_REPEAT", nullable = true, unique = false)
	private Integer passMinHistoryRepeat;

	@Column(name = "PASS_LOGIN_ATTEMPT", nullable = true, unique = false)
	private Integer passLoginAttempt;

	@Column(name = "PASS_BLOCK_WAIT_TIME", nullable = true, unique = false)
	private Integer passBlockWaitTime;

	@Column(name = "PASS_MUST_HAVE_LOWERCASE", nullable = true, unique = false)
	@Length(max = 1)
	private String passMustHaveLowercase;

	@Column(name = "PASS_MUST_HAVE_NUMBER", nullable = true, unique = false)
	@Length(max = 1)
	private String passMustHaveNumber;

	@Column(name = "PASS_MUST_HAVE_UPPERCASE", nullable = true, unique = false)
	@Length(max = 1)
	private String passMustHaveUppercase;

	@Column(name = "PASS_MUST_HAVE_SPECIAL_CHARS", nullable = true, unique = false)
	@Length(max = 1)
	private String passMustHaveSpecialChars;

	@Column(name = "PASS_UNBLOCK_AUTOMATICALLY", nullable = true, unique = false)
	@Length(max = 1)
	private String passUnblockAutomatically;

	@Column(name = "PASS_SPECIAL_CHARS", nullable = true, unique = false)
	@Length(max = 20)
	private String passSpecialChars;

	@Transient
	private boolean forAdmin;

	public PassPolicy() {
		super();
		addColumnMapping("1", "a.id");
		addColumnMapping("3", "a.changedDt");
		addColumnMapping("4", "a.changedByAppUserFk");
		addColumnMapping("5", "lower(a.name)");
		addColumnMapping("6", "a.participantFk");
		addColumnMapping("7", "a.passMinPeriodInDays");
		addColumnMapping("8", "a.passMaxPeriodInDays");
		addColumnMapping("9", "a.passMinLength");
		addColumnMapping("10", "a.passMinHistoryRepeat");
		addColumnMapping("11", "a.passLoginAttempt");
		addColumnMapping("12", "a.passBlockWaitTime");
		addColumnMapping("13", "lower(a.passMustHaveLowercase)");
		addColumnMapping("14", "lower(a.passMustHaveNumber)");
		addColumnMapping("15", "lower(a.passMustHaveUppercase)");
		addColumnMapping("16", "lower(a.passMustHaveSpecialChars)");
		addColumnMapping("17", "lower(a.passUnblockAutomatically)");
		addColumnMapping("18", "lower(a.passSpecialChars)");
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

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Timestamp getChangedDt() {
		return changedDt;
	}

	public void setChangedDt(Timestamp changedDt) {
		this.changedDt = changedDt;
	}

	public Long getChangedByAppUserFk() {
		return changedByAppUserFk;
	}

	public void setChangedByAppUserFk(Long changedByAppUserFk) {
		this.changedByAppUserFk = changedByAppUserFk;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getParticipantFk() {
		return participantFk;
	}

	public void setParticipantFk(Long participantFk) {
		this.participantFk = participantFk;
	}

	public AppUser getChangedByAppUser() {
		return changedByAppUser;
	}

	public void setChangedByAppUser(AppUser changedByAppUser) {
		this.changedByAppUser = changedByAppUser;
	}

	public Participant getParticipant() {
		return participant;
	}

	public void setParticipant(Participant participant) {
		this.participant = participant;
	}

	public Integer getPassMinPeriodInDays() {
		return passMinPeriodInDays;
	}

	public void setPassMinPeriodInDays(Integer passMinPeriodInDays) {
		this.passMinPeriodInDays = passMinPeriodInDays;
	}

	public Integer getPassMaxPeriodInDays() {
		return passMaxPeriodInDays;
	}

	public void setPassMaxPeriodInDays(Integer passMaxPeriodInDays) {
		this.passMaxPeriodInDays = passMaxPeriodInDays;
	}

	public Integer getPassMinLength() {
		return passMinLength;
	}

	public void setPassMinLength(Integer passMinLength) {
		this.passMinLength = passMinLength;
	}

	public Integer getPassMinHistoryRepeat() {
		return passMinHistoryRepeat;
	}

	public void setPassMinHistoryRepeat(Integer passMinHistoryRepeat) {
		this.passMinHistoryRepeat = passMinHistoryRepeat;
	}

	public Integer getPassLoginAttempt() {
		return passLoginAttempt;
	}

	public void setPassLoginAttempt(Integer passLoginAttempt) {
		this.passLoginAttempt = passLoginAttempt;
	}

	public Integer getPassBlockWaitTime() {
		return passBlockWaitTime;
	}

	public void setPassBlockWaitTime(Integer passBlockWaitTime) {
		this.passBlockWaitTime = passBlockWaitTime;
	}

	public String getPassMustHaveLowercase() {
		return passMustHaveLowercase;
	}

	public void setPassMustHaveLowercase(String passMustHaveLowercase) {
		this.passMustHaveLowercase = passMustHaveLowercase;
	}

	public String getPassMustHaveNumber() {
		return passMustHaveNumber;
	}

	public void setPassMustHaveNumber(String passMustHaveNumber) {
		this.passMustHaveNumber = passMustHaveNumber;
	}

	public String getPassMustHaveUppercase() {
		return passMustHaveUppercase;
	}

	public void setPassMustHaveUppercase(String passMustHaveUppercase) {
		this.passMustHaveUppercase = passMustHaveUppercase;
	}

	public String getPassMustHaveSpecialChars() {
		return passMustHaveSpecialChars;
	}

	public void setPassMustHaveSpecialChars(String passMustHaveSpecialChars) {
		this.passMustHaveSpecialChars = passMustHaveSpecialChars;
	}

	public String getPassUnblockAutomatically() {
		return passUnblockAutomatically;
	}

	public void setPassUnblockAutomatically(String passUnblockAutomatically) {
		this.passUnblockAutomatically = passUnblockAutomatically;
	}

	public String getPassSpecialChars() {
		return passSpecialChars;
	}

	public void setPassSpecialChars(String passSpecialChars) {
		this.passSpecialChars = passSpecialChars;
	}

	/** * {@inheritDoc} */
	@Override
	public boolean equals(final Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof PassPolicy))
			return false;
		PassPolicy castOther = (PassPolicy) other;
		return new EqualsBuilder().append(getId(), castOther.getId()).append(getVersion(), castOther.getVersion()).append(getChangedDt(), castOther.getChangedDt()).append(getChangedByAppUserFk(), castOther.getChangedByAppUserFk())
				.append(getName(), castOther.getName()).append(getParticipantFk(), castOther.getParticipantFk()).append(getPassMinPeriodInDays(), castOther.getPassMinPeriodInDays()).append(getPassMaxPeriodInDays(), castOther.getPassMaxPeriodInDays())
				.append(getPassMinLength(), castOther.getPassMinLength()).append(getPassMinHistoryRepeat(), castOther.getPassMinHistoryRepeat()).append(getPassLoginAttempt(), castOther.getPassLoginAttempt())
				.append(getPassBlockWaitTime(), castOther.getPassBlockWaitTime()).append(getPassMustHaveLowercase(), castOther.getPassMustHaveLowercase()).append(getPassMustHaveNumber(), castOther.getPassMustHaveNumber())
				.append(getPassMustHaveUppercase(), castOther.getPassMustHaveUppercase()).append(getPassMustHaveSpecialChars(), castOther.getPassMustHaveSpecialChars()).append(getPassUnblockAutomatically(), castOther.getPassUnblockAutomatically())
				.append(getPassSpecialChars(), castOther.getPassSpecialChars()).isEquals();
	}

	/** * {@inheritDoc} */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getId()).append(getVersion()).append(getChangedDt()).append(getChangedByAppUserFk()).append(getName()).append(getParticipantFk()).append(getPassMinPeriodInDays()).append(getPassMaxPeriodInDays())
				.append(getPassMinLength()).append(getPassMinHistoryRepeat()).append(getPassLoginAttempt()).append(getPassBlockWaitTime()).append(getPassMustHaveLowercase()).append(getPassMustHaveNumber()).append(getPassMustHaveUppercase())
				.append(getPassMustHaveSpecialChars()).append(getPassUnblockAutomatically()).append(getPassSpecialChars()).toHashCode();
	}

	/**
	 * @see HiddenFieldsSecureInterface#generateSecureFieldsHash()
	 */
	@Override
	public String getSecureFields() {
		HiddenFieldBuilder h = HiddenFieldBuilder.create(this.getClass().getName()).add(this.version);
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

	@Override
	public Object getValue() {
		return id;
	}

	@Override
	public String getLabel() {
		return name;
	}

	public boolean isForAdmin() {
		return forAdmin;
	}

	public void setForAdmin(boolean forAdmin) {
		this.forAdmin = forAdmin;
	}

}
