package rs.milanmitic.master.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

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

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.HtmlUtils;

import rs.milanmitic.master.common.Constants;
import rs.milanmitic.master.common.pagging.SearchInput;
import rs.milanmitic.master.common.util.ActivityUtil;
import rs.milanmitic.master.common.util.Utils;

/**
 *
 * <p>
 * Title: AuditLog
 * </p>
 *
 * <p>
 * Description: Domain Object describing a AuditLog entity
 * </p>
 *
 */
@Entity(name = "AuditLog")
@Table(name = "AUDIT_LOG")
public class AuditLog extends SearchInput implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final Integer TRANSSTATUS_NOT_SUCCESS = Constants.STATUS_NOT_ACTIVE;
	public static final Integer TRANSSTATUS_SUCCESS = Constants.STATUS_ACTIVE;

	public static final Integer MSSGTX_MAX_LEN = Integer.valueOf(3500);

	public static final String DEFAULT_THREAD_NAME = "X";

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "AUDIT_TIMESTAMP", nullable = true, unique = false)
	private Timestamp auditTimestamp;

	@Column(name = "TABLE_NAME", length = 50, nullable = true, unique = false)
	private String tableName;

	@Column(name = "UPDATE_STRING", length = 3500, nullable = true, unique = false)
	private String updateString;

	@Column(name = "AUDIT_TYPE", length = 25, nullable = true, unique = false)
	private String auditType;

	@Column(name = "AUDIT_STATUS", nullable = true, unique = false)
	private Integer auditStatus;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "APP_USER_FK", referencedColumnName = "ID", nullable = true, unique = false, insertable = false, updatable = false)
	private AppUser appUser;

	@Column(name = "APP_USER_FK", nullable = true, unique = false)
	private Long appUserFk;

	@Column(name = "PARTICIPANT_FK", nullable = true, unique = false)
	private Long participantFk;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "PARTICIPANT_FK", referencedColumnName = "ID", nullable = true, unique = false, insertable = false, updatable = false)
	private Participant participant;

	@Transient
	private boolean doNotSave = false;

	@Transient
	private ActivityUtil activityUtil = ActivityUtil.create();

	@Transient
	private Date dateFrom;

	@Transient
	private Date dateTo;

	@Transient
	private String timeFrom;

	@Transient
	private String timeTo;

	public AuditLog() {// create default column mapping
		addColumnMapping("1", "a.id");
		addColumnMapping("2", "a.auditTimestamp");
		addColumnMapping("3", "lower(a.tableName)");
		addColumnMapping("4", "a.auditType");
		addColumnMapping("5", "a.auditStatus");
		addColumnMapping("6", "a.appUserFk");
		addColumnMapping("7", "a.participantFk");
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
	 * @return the auditTimestamp
	 */
	public Timestamp getAuditTimestamp() {
		return auditTimestamp;
	}

	/**
	 * @param auditTimestamp
	 *            the auditTimestamp to set
	 */
	public void setAuditTimestamp(Timestamp auditTimestamp) {
		this.auditTimestamp = auditTimestamp;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName
	 *            the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * @return the updateString
	 */
	public String getUpdateString() {
		return updateString;
	}

	public String getUpdateStringForDisplay() {
		if (updateString == null)
			return updateString;
		if (StringUtils.isNotBlank(updateString)) {
			return Utils.prettyPrintXML(updateString);
		}
		return HtmlUtils.htmlEscape(updateString);
	}

	/**
	 * @param updateString
	 *            the updateString to set
	 */
	public void setUpdateString(String updateString) {
		this.updateString = updateString;
	}

	/**
	 * @return the auditType
	 */
	public String getAuditType() {
		return auditType;
	}

	/**
	 * @param auditType
	 *            the auditType to set
	 */
	public void setAuditType(String auditType) {
		this.auditType = auditType;
	}

	/**
	 * @return the auditStatus
	 */
	public Integer getAuditStatus() {
		return auditStatus;
	}

	/**
	 * @param auditStatus
	 *            the auditStatus to set
	 */
	public void setAuditStatus(Integer auditStatus) {
		this.auditStatus = auditStatus;
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
		result = prime * result + ((auditStatus == null) ? 0 : auditStatus.hashCode());
		result = prime * result + ((auditTimestamp == null) ? 0 : auditTimestamp.hashCode());
		result = prime * result + ((auditType == null) ? 0 : auditType.hashCode());
		result = prime * result + ((participantFk == null) ? 0 : participantFk.hashCode());
		result = prime * result + ((appUserFk == null) ? 0 : appUserFk.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((tableName == null) ? 0 : tableName.hashCode());
		result = prime * result + ((updateString == null) ? 0 : updateString.hashCode());
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
		AuditLog other = (AuditLog) obj;
		if (auditStatus == null) {
			if (other.auditStatus != null) {
				return false;
			}
		} else if (!auditStatus.equals(other.auditStatus)) {
			return false;
		}
		if (auditTimestamp == null) {
			if (other.auditTimestamp != null) {
				return false;
			}
		} else if (!auditTimestamp.equals(other.auditTimestamp)) {
			return false;
		}
		if (auditType == null) {
			if (other.auditType != null) {
				return false;
			}
		} else if (!auditType.equals(other.auditType)) {
			return false;
		}
		if (participantFk == null) {
			if (other.participantFk != null) {
				return false;
			}
		} else if (!participantFk.equals(other.participantFk)) {
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
		if (tableName == null) {
			if (other.tableName != null) {
				return false;
			}
		} else if (!tableName.equals(other.tableName)) {
			return false;
		}
		if (updateString == null) {
			if (other.updateString != null) {
				return false;
			}
		} else if (!updateString.equals(other.updateString)) {
			return false;
		}
		return true;
	}

	public boolean isDoNotSave() {
		return doNotSave;
	}

	public void setDoNotSave(boolean doNotSave) {
		this.doNotSave = doNotSave;
	}

	/**
	 * @return the activityUtil
	 */
	public ActivityUtil getActivityUtil() {
		return activityUtil;
	}

	/**
	 * @param activityUtil
	 *            the activityUtil to set
	 */
	public void setActivityUtil(ActivityUtil activityUtil) {
		this.activityUtil = activityUtil;
	}

	public ActivityUtil addActivity(Object o) {
		return activityUtil.addActivity(o);
	}

	public ActivityUtil replaceActivity(Object o) {
		return activityUtil.replaceActivity(o);
	}

	public ActivityUtil replaceActivity(String property, Object value) {
		return activityUtil.replaceActivity(property, value);
	}

	public ActivityUtil addActivity(String property, Object value) {
		return activityUtil.addActivity(property, value);
	}

	public ActivityUtil addActivity(String property, Object[] value) {
		return activityUtil.addActivity(property, value);
	}

	public ActivityUtil addActivityXML(String xml) {
		return activityUtil.addActivityXML(xml);
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

	/**
	 * @return the participant
	 */
	public Participant getParticipantk() {
		return participant;
	}

	/**
	 * @param participant
	 *            the participant to set
	 */
	public void setParticipant(Participant participant) {
		this.participant = participant;
	}

	/**
	 * @return the dateFrom
	 */
	public Date getDateFrom() {
		return dateFrom;
	}

	/**
	 * @param dateFrom
	 *            the dateFrom to set
	 */
	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	/**
	 * @return the dateTo
	 */
	public Date getDateTo() {
		return dateTo;
	}

	/**
	 * @param dateTo
	 *            the dateTo to set
	 */
	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public boolean isSuccess() {
		return TRANSSTATUS_SUCCESS.equals(auditStatus);
	}

	public boolean isError() {
		return TRANSSTATUS_NOT_SUCCESS.equals(auditStatus);
	}

	/**
	 * @return the timeFrom
	 */
	public String getTimeFrom() {
		return timeFrom;
	}

	/**
	 * @param timeFrom
	 *            the timeFrom to set
	 */
	public void setTimeFrom(String timeFrom) {
		this.timeFrom = timeFrom;
	}

	/**
	 * @return the timeTo
	 */
	public String getTimeTo() {
		return timeTo;
	}

	/**
	 * @param timeTo
	 *            the timeTo to set
	 */
	public void setTimeTo(String timeTo) {
		this.timeTo = timeTo;
	}

	public Participant getParticipant() {
		return participant;
	}

}
