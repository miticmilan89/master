package rs.milanmitic.master.common;

import java.io.Serializable;

import rs.milanmitic.master.model.ActivityLog;

/**
 * Generate custom event log
 * 
 * @author milan
 * 
 */
public interface CustomEventLog extends Serializable {
	/**
	 * Generate custom event log
	 * 
	 * @return
	 */
	public ActivityLog toEventLog();
}
