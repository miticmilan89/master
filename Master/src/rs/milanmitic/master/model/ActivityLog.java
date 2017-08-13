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
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.web.util.HtmlUtils;

import rs.milanmitic.master.common.Constants;
import rs.milanmitic.master.common.pagging.SearchInput;
import rs.milanmitic.master.common.util.ActivityUtil;
import rs.milanmitic.master.common.util.Utils;

/**
 *
 * <p>
 * Title: ActivityLog
 * </p>
 *
 * <p>
 * Description: Domain Object describing a ActivityLog entity
 * </p>
 *
 */
@Entity(name = "ActivityLog")
@Table(name = "ACTIVITY_LOG")
public class ActivityLog extends SearchInput implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final Integer TRANSSTATUS_NOT_SUCCESS = Constants.STATUS_NOT_ACTIVE;
	public static final Integer TRANSSTATUS_SUCCESS = Constants.STATUS_ACTIVE;

	public static final int MSSGTX_MAX_LEN = 3500;
	public static final int THREAD_NAME_MAX_LEN = 50;

	public static final Integer LIST_ERROR_FLAG_SHOW_ERRORS = Integer.valueOf(1);
	public static final String DEFAULT_THREAD_NAME = "X";

	public static final Long DEFAULT_ERROR_NO = Long.valueOf(-1);

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "DURATION", nullable = true, unique = false)
	private Long duration;

	@Column(name = "URL", length = 200, nullable = true, unique = false)
	private String url;

	@Column(name = "DATETIME_START", nullable = true, unique = false)
	private Timestamp datetimeStart;

	@Column(name = "ACTIVITY_DATA", length = 3500, nullable = true, unique = false)
	private String activityData;

	@Column(name = "USER_IP", length = 40, nullable = true, unique = false)
	private String userIp;

	@Column(name = "THREAD_NAME", length = 30, nullable = true, unique = false)
	private String threadName;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "APP_USER_FK", referencedColumnName = "ID", nullable = true, unique = false, insertable = false, updatable = false)
	private AppUser appUser;

	@Column(name = "APP_USER_FK", nullable = true)
	private Long appUserFk;

	@Column(name = "error_no", nullable = true, unique = false)
	private Long errorNo;

	@Column(name = "PARTICIPANT_FK", nullable = true, unique = false)
	private Long participantFk;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "PARTICIPANT_FK", referencedColumnName = "ID", nullable = true, unique = false, insertable = false, updatable = false)
	private Participant participant;

	@Transient
	private ActivityUtil activityUtil = ActivityUtil.create();

	@Transient
	private boolean doNotSave = false;

	@Transient
	private Integer listErrorFlag = null;

	@Transient
	private Date dateFrom;

	@Transient
	private Date dateTo;

	@Transient
	private String timeFrom;

	@Transient
	private String timeTo;

	public ActivityLog() {// create default column mapping
		addColumnMapping("1", "a.id");
		addColumnMapping("2", "a.duration");
		addColumnMapping("3", "a.url");
		addColumnMapping("4", "a.datetimeStart");
		addColumnMapping("5", "a.userIp");
		addColumnMapping("6", "a.threadName");
		addColumnMapping("7", "a.appUserFk");
		addColumnMapping("8", "a.participantFk");
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Timestamp getDatetimeStart() {
		return datetimeStart;
	}

	public void setDatetimeStart(Timestamp datetimeStart) {
		this.datetimeStart = datetimeStart;
	}

	public String getActivityData() {
		return activityData;
	}

	public String getActivityDataForDisplay() {
		if (activityData == null)
			return activityData;
		if (StringUtils.isNotBlank(activityData)) {
			return Utils.prettyPrintXML(activityData);
		}
		return HtmlUtils.htmlEscape(activityData);
	}

	public void setActivityData(String activityData) {
		this.activityData = activityData;
	}

	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	public AppUser getAppUser() {
		return appUser;
	}

	public void setAppUser(AppUser appUser) {
		this.appUser = appUser;
	}

	public Long getAppUserFk() {
		return appUserFk;
	}

	public void setAppUserFk(Long appUserFk) {
		this.appUserFk = appUserFk;
	}

	@Override
	public String toString() {
		return Utils.toStringGlobal(this);
	}

	/**
	 * @return the errorNo
	 */
	public Long getErrorNo() {
		return errorNo;
	}

	/**
	 * @param errorNo
	 *            the errorNo to set
	 */
	public void setErrorNo(Long errorNo) {
		this.errorNo = errorNo;
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
	public Participant getParticipant() {
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
	 * @return the doNotSave
	 */
	public boolean isDoNotSave() {
		return doNotSave;
	}

	/**
	 * @param doNotSave
	 *            the doNotSave to set
	 */
	public void setDoNotSave(boolean doNotSave) {
		this.doNotSave = doNotSave;
	}

	/**
	 * @return the threadName
	 */
	public String getThreadName() {
		return threadName;
	}

	/**
	 * @param threadName
	 *            the threadName to set
	 */
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public boolean isThreadLog() {
		return threadName != null && !DEFAULT_THREAD_NAME.equals(threadName);
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
		return errorNo == null;
	}

	public boolean isError() {
		return errorNo != null;
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

	public Integer getListErrorFlag() {
		return listErrorFlag;
	}

	public void setListErrorFlag(Integer listErrorFlag) {
		this.listErrorFlag = listErrorFlag;
	}

	public boolean isShowOnlyErrors() {
		return LIST_ERROR_FLAG_SHOW_ERRORS.equals(listErrorFlag);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ActivityLog)) {
			return false;
		}
		ActivityLog castOther = (ActivityLog) other;
		return new EqualsBuilder().append(getId(), castOther.getId()).append(getDuration(), castOther.getDuration()).append(getUrl(), castOther.getUrl()).append(getDatetimeStart(), castOther.getDatetimeStart())
				.append(getActivityData(), castOther.getActivityData()).append(getUserIp(), castOther.getUserIp()).append(getThreadName(), castOther.getThreadName()).append(getAppUserFk(), castOther.getAppUserFk())
				.append(getErrorNo(), castOther.getErrorNo()).append(getParticipantFk(), castOther.getParticipantFk()).append(isDoNotSave(), castOther.isDoNotSave()).append(getDateFrom(), castOther.getDateFrom())
				.append(getDateTo(), castOther.getDateTo()).append(getTimeFrom(), castOther.getTimeFrom()).append(getTimeTo(), castOther.getTimeTo()).isEquals();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getId()).append(getDuration()).append(getUrl()).append(getDatetimeStart()).append(getActivityData()).append(getUserIp()).append(getThreadName()).append(getAppUserFk()).append(getErrorNo())
				.append(getParticipantFk()).append(isDoNotSave()).append(getDateFrom()).append(getDateTo()).append(getTimeFrom()).append(getTimeTo()).toHashCode();
	}

}
