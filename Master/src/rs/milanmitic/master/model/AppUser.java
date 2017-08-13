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
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import rs.milanmitic.master.common.Constants;
import rs.milanmitic.master.common.pagging.SearchInput;
import rs.milanmitic.master.common.protector.HiddenFieldBuilder;
import rs.milanmitic.master.common.protector.HiddenFieldsSecureInterface;
import rs.milanmitic.master.common.util.Utils;

/**
 * <p>
 * Title: AppUser
 * </p>
 *
 * <p>
 * Description: Domain Object describing a AppUser entity
 * </p>
 *
 */
@Entity(name = "AppUser")
@Table(name = "APP_USER")
public class AppUser extends SearchInput implements Serializable, HiddenFieldsSecureInterface {

	private static final long serialVersionUID = 1L;

	public static final Integer STATUS_ACTIVE = Constants.STATUS_ACTIVE;
	public static final Integer STATUS_NOT_ACTIVE = Constants.STATUS_NOT_ACTIVE;
	public static final Integer STATUS_BLOCKED = Constants.STATUS_BLOCKED;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "FIRST_NAME", nullable = true, unique = false)
	@Length(max = 30)
	@NotEmpty
	private String firstName;

	@Column(name = "LAST_NAME", nullable = true, unique = false)
	@Length(max = 30)
	@NotEmpty
	private String lastName;

	@Column(name = "USERNAME", nullable = true, unique = false)
	@Length(max = 20)
	@NotEmpty
	private String username;

	@Column(name = "PASSWORD_HASH", nullable = true, unique = false)
	@Length(max = 72)
	private String passwordHash;

	@Column(name = "USER_TYPE", nullable = true, unique = false)
	@NotNull
	private Long userType;

	@Column(name = "STATUS", nullable = true, unique = false)
	private Integer status;

	@Column(name = "PARTICIPANT_FK", nullable = true, unique = false)
	private Long participantFk;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "PARTICIPANT_FK", referencedColumnName = "ID", nullable = false, unique = false, insertable = false, updatable = false)
	private Participant participant;

	@Column(name = "CHANGED_DT", nullable = false, unique = false)
	private Timestamp changedDt;

	@Column(name = "CHANGED_BY_APP_USER_FK", nullable = true, unique = false)
	private Long changedByAppUserFk;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "CHANGED_BY_APP_USER_FK", referencedColumnName = "ID", nullable = false, unique = false, insertable = false, updatable = false)
	private AppUser changedByAppUser;

	@Column(name = "FORCE_PASS_CHANGE", nullable = true, unique = false)
	private String forcePassChange;

	@Column(name = "INVALID_LOGIN_ATTEMPT", nullable = true, unique = false)
	private Integer invalidLoginAttempt;

	@Column(name = "LAST_VALID_LOGIN", nullable = true, unique = false)
	private Timestamp lastValidLogin;

	@Column(name = "LAST_INVALID_LOGIN", nullable = true, unique = false)
	private Timestamp lastInValidLogin;

	@Column(name = "LAST_PASS_CHANGED_DT", nullable = true, unique = false)
	private Timestamp lastPassChangedDt;

	@Column(name = "USER_EMAIL", length = 100, nullable = true, unique = false)
	@Length(max = 100)
	private String userEmail;

	@Column(name = "PASS_POLICY_FK", length = 10, nullable = true, unique = false)
	private Long passPolicyFk;

	@Version
	private Long version;

	@Transient
	private String password;

	@Transient
	private String repeatPassword;

	@Transient
	private String currentPassword;

	public AppUser() {
		super();
		addColumnMapping("1", "a.id");
		addColumnMapping("2", "lower(a.firstName)");
		addColumnMapping("3", "lower(a.lastName)");
		addColumnMapping("4", "lower(a.username)");
		addColumnMapping("5", "a.status");
		addColumnMapping("6", "lower(a.participant.name)");
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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public Long getUserType() {
		return userType;
	}

	public void setUserType(Long userType) {
		this.userType = userType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRepeatPassword() {
		return repeatPassword;
	}

	public void setRepeatPassword(String repeatPassword) {
		this.repeatPassword = repeatPassword;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
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

	public AppUser getChangedByAppUser() {
		return changedByAppUser;
	}

	public void setChangedByAppUser(AppUser changedByAppUser) {
		this.changedByAppUser = changedByAppUser;
	}

	public String getForcePassChange() {
		return forcePassChange;
	}

	public void setForcePassChange(String forcePassChange) {
		this.forcePassChange = forcePassChange;
	}

	public Integer getInvalidLoginAttempt() {
		return invalidLoginAttempt;
	}

	public void setInvalidLoginAttempt(Integer invalidLoginAttempt) {
		this.invalidLoginAttempt = invalidLoginAttempt;
	}

	public Timestamp getLastValidLogin() {
		return lastValidLogin;
	}

	public void setLastValidLogin(Timestamp lastValidLogin) {
		this.lastValidLogin = lastValidLogin;
	}

	public Timestamp getLastInValidLogin() {
		return lastInValidLogin;
	}

	public void setLastInValidLogin(Timestamp lastInValidLogin) {
		this.lastInValidLogin = lastInValidLogin;
	}

	public Timestamp getLastPassChangedDt() {
		return lastPassChangedDt;
	}

	public void setLastPassChangedDt(Timestamp lastPassChangedDt) {
		this.lastPassChangedDt = lastPassChangedDt;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public Long getPassPolicyFk() {
		return passPolicyFk;
	}

	public void setPassPolicyFk(Long passPolicyFk) {
		this.passPolicyFk = passPolicyFk;
	}

	/** * {@inheritDoc} */
	@Override
	public boolean equals(final Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof AppUser))
			return false;
		AppUser castOther = (AppUser) other;
		return new EqualsBuilder().append(getId(), castOther.getId()).append(getFirstName(), castOther.getFirstName()).append(getLastName(), castOther.getLastName()).append(getUsername(), castOther.getUsername())
				.append(getPasswordHash(), castOther.getPasswordHash()).append(getUserType(), castOther.getUserType()).append(getStatus(), castOther.getStatus()).append(getParticipantFk(), castOther.getParticipantFk())
				.append(getVersion(), castOther.getVersion()).isEquals();
	}

	/** * {@inheritDoc} */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getId()).append(getFirstName()).append(getLastName()).append(getUsername()).append(getPasswordHash()).append(getUserType()).append(getStatus()).append(getParticipantFk()).append(getVersion()).toHashCode();
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
		if (username != null) {
			h.add(username);
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

	public boolean isActive() {
		return STATUS_ACTIVE.equals(status);
	}
}
