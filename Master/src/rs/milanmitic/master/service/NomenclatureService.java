package rs.milanmitic.master.service;

import java.util.List;

import org.springframework.security.access.annotation.Secured;

import rs.milanmitic.master.common.pagging.SearchResults;
import rs.milanmitic.master.model.AppFunction;
import rs.milanmitic.master.model.AppRole;
import rs.milanmitic.master.model.AppUser;
import rs.milanmitic.master.model.Participant;
import rs.milanmitic.master.model.PassPolicy;

public interface NomenclatureService {

	AppUser getAppUserById(Long id);

	void updateAppUser(AppUser bean, String[] assignedRoles, boolean updateRoles);

	void addAppUser(AppUser bean, String[] assignedRoles);

	SearchResults getAppUserList(AppUser bean);

	void deleteAppUser(AppUser bean);

	AppRole getAppRoleById(Long id);

	void updateAppRole(AppRole bean, String[] assignedFunctions);

	void addAppRole(AppRole bean, String[] assignedFunctions);

	SearchResults getAppRoleList(AppRole bean);

	void deleteAppRole(AppRole bean);

	List<AppFunction> getAssignedFunctionsForRole(Long appRoleFk);

	List<AppFunction> getNotAssignedFunctionsForRole(Long appRoleFk);

	List<AppFunction> getAllAppFunctionAccess();

	List<AppRole> getAssignedRolesForUser(Long appUserFk);

	List<AppRole> getNotAssignedRolesForUser(Long appUserFk, Long participantFk);

	List<AppRole> getAllAppRoles();

	List<AppFunction> getAppFunctionAccessByUser(Long id);

	Participant getParticipantById(Long id);

	void updateParticipant(Participant bean);

	void addParticipant(Participant bean);

	@Secured("ROLE_ADMIN")
	SearchResults getParticipantList(Participant bean);

	void deleteParticipant(Participant bean);

	/**
	 * Get record by PK
	 * 
	 * @param id
	 * @return record or null
	 */
	PassPolicy getPassPolicyById(Long id);

	/**
	 * Update record in DB
	 * 
	 * @param bean
	 */
	void updatePassPolicy(PassPolicy bean);

	/**
	 * Add new record in DB
	 * 
	 * @param bean
	 */
	void addPassPolicy(PassPolicy bean);

	/**
	 * Search database for PassPolicys
	 * 
	 * @param bean
	 * @return list of results
	 */
	SearchResults getPassPolicyList(PassPolicy bean);

	/**
	 * Delete record from DB
	 * 
	 * @param bean
	 */
	void deletePassPolicy(PassPolicy bean);

}
