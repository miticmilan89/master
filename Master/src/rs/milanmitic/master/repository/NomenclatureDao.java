package rs.milanmitic.master.repository;

import java.util.List;

import rs.milanmitic.master.common.pagging.SearchResults;
import rs.milanmitic.master.model.AppFunction;
import rs.milanmitic.master.model.AppRole;
import rs.milanmitic.master.model.AppRoleAppFunction;
import rs.milanmitic.master.model.AppUser;
import rs.milanmitic.master.model.AppUserAppRole;
import rs.milanmitic.master.model.Participant;
import rs.milanmitic.master.model.PassHistory;
import rs.milanmitic.master.model.PassPolicy;

/**
 * Nomenclature repository
 * 
 * @author milan
 * 
 */
public interface NomenclatureDao extends BasicDao {

	SearchResults getAppUserList(AppUser bean);

	SearchResults getAppRoleList(AppRole bean);

	List<AppFunction> getAllAppFunctionAccess();

	List<AppFunction> getAssignedFunctionsForRole(Long appRoleFk);

	List<AppFunction> getNotAssignedFunctionsForRole(Long appRoleFk);

	List<AppRole> getAssignedRolesForUser(Long appUserFk);

	List<AppRole> getNotAssignedRolesForUser(Long appUserFk);

	List<AppRole> getAllAppRoles();

	void deleteRolesForUser(Long appUserFk);

	SearchResults getAppUserAppRoleList(AppUserAppRole b);

	SearchResults getAppRoleAppFunctionList(AppRoleAppFunction b);

	List<AppFunction> getAppFunctionAccessByUser(Long id);

	public void preDeleteParticipantCheck(Participant b);

	SearchResults getParticipantList(Participant bean);

	/**
	 * Check is record can be deleted from DB
	 * 
	 * @param b
	 */
	public void preDeletePassPolicyCheck(PassPolicy b);

	/**
	 * Search database for PassPolicys
	 * 
	 * @param bean
	 * @return list of results
	 */
	SearchResults getPassPolicyList(PassPolicy bean);

	void validateAppUser(AppUser bean);

	List<PassHistory> getPasswordHistoryByUserId(Long id);

	void deleteAppRoleAppFunctionForRole(Long id);

}
