package rs.milanmitic.master.common;

import java.util.List;

/**
 * If we do not want to put some values in EventLog, we should implement this interface
 * 
 * @author milan
 * 
 */
public interface EventLogIgnoreFields {
	/**
	 * Define here list of fields that we do not want to store in EventLog
	 * 
	 * @return
	 */
	public List<String> getEventIgnoreFields();
}
