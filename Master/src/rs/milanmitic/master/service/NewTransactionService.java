package rs.milanmitic.master.service;

import java.io.Serializable;

import rs.milanmitic.master.model.ActivityLog;
import rs.milanmitic.master.model.AuditLog;

/**
 * All methods here open new transaction (existing is not used)
 * 
 * @author milan
 * 
 */
public interface NewTransactionService {
	/**
	 * Create new transaction and add EventLog to database
	 * 
	 * @param ev
	 */

	void saveActivityLog(ActivityLog ev);

	public <T extends Object> T getByPk(Serializable pk, Class<T> k);

	void saveAuditLog(AuditLog usrLog);
	
	void update(Object o);

}
