package rs.milanmitic.master.common.util;

import java.io.Serializable;

import rs.milanmitic.master.common.data.LoggedUserData;

public class BackgroundJob implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long timeStarted;
	private LoggedUserData loggedUser;
	
	private String statusMessage;
	private Serializable[] params;

	private String jobTitle;
	private String sessionid;
	private boolean cancel = false;
	
	public BackgroundJob(String sessionId) {
		this.timeStarted = System.currentTimeMillis();
		this.sessionid = sessionId;
	}
	
	public long getTimeStarted() {
		return timeStarted;
	}
	public void setTimeStarted(long timeStarted) {
		this.timeStarted = timeStarted;
	}
	public LoggedUserData getLoggedUser() {
		return loggedUser;
	}
	public void setLoggedUser(LoggedUserData loggedUser) {
		this.loggedUser = loggedUser;
	}
	public String getStatusMessage() {
		return statusMessage;
	}
	public void setStatusMessage(String statusMessage, Serializable... params) {
		this.statusMessage = statusMessage;
		this.params = params;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	@Override
	public String toString() {
		return Utils.toStringGlobal(this);
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getSessionid() {
		return sessionid;
	}

	public Object[] getParams() {
		return params;
	}

	public void setParams(Serializable[] params) {
		this.params = params;
	}

	public boolean isCancel() {
		return cancel;
	}

	public void setCancel(boolean cancel) {
		this.cancel = cancel;
	}
	
}