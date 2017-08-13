package rs.milanmitic.master.repository;

import rs.milanmitic.master.common.data.LoggedUserData;
import rs.milanmitic.master.common.pagging.SearchResults;
import rs.milanmitic.master.model.ActivityLog;
import rs.milanmitic.master.model.AppConfig;
import rs.milanmitic.master.model.AppUser;
import rs.milanmitic.master.model.AuditLog;

/**
 * Common repository
 * 
 * @author milan
 * 
 */
public interface CommonDao extends BasicDao {

	// NO SECURITY NEEDED
	void logoutUser(LoggedUserData ld);

	AppUser getAppUserByUserName(String username);

	SearchResults getAuditLogList(AuditLog bean);

	SearchResults getActivityLogList(ActivityLog bean);

	SearchResults getAppConfigList(AppConfig bean);

}
