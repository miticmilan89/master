package rs.milanmitic.master.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import rs.milanmitic.master.common.ContextHolder;
import rs.milanmitic.master.common.aop.MasterLogAnnotation;
import rs.milanmitic.master.common.exception.ValidateException;
import rs.milanmitic.master.common.pagging.SearchInput;
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
@Repository
public class NomenclatureHibernateDao extends BasicDaoHibernate implements NomenclatureDao {

	private static final String COLUMN_PARTICIPANT_FK = "participantFk";

	@Override
	@MasterLogAnnotation
	public SearchResults getAppUserList(AppUser bean) {
		String sql = SQL_DEFAULT_WHERE_1_1;
		Map<String, Object> map = new HashMap<String, Object>();
		if (ContextHolder.isLoggedUserParticipant()) {
			sql += " AND a.participantFk=:participantFk ";
			map.put(COLUMN_PARTICIPANT_FK, ContextHolder.getLoggedParticipantData().getParticipantId());
		}

		SearchResults sd = new SearchResults(bean);
		List<SearchInput> l = executePagging("SELECT a from AppUser a left outer join fetch a.participant" + sql, bean, map);
		if (sd.isExecuteCount())
			sd.setResultsCount(executeCount("SELECT count(*) from AppUser a " + sql, bean, map));
		sd.setResults(l);
		return sd;
	}

	/**
	 * @see NomenclatureDao#getAppRoleList(AppRole) {
	 */
	@Override
	@MasterLogAnnotation
	public SearchResults getAppRoleList(AppRole bean) {
		if (bean == null) {
			bean = new AppRole();
			bean.setItemsPerPage(Integer.MAX_VALUE);
		}
		String sql = SQL_DEFAULT_WHERE_1_1;
		Map<String, Object> map = new HashMap<String, Object>();
		sql += addSQLLong("a.id", bean.getId(), bean, map);
		sql += addSQLString("a.roleName", bean.getRoleName(), bean, map);
		sql += addSQLLong("a.participantFk", bean.getParticipantFk(), bean, map);
		if (ContextHolder.isLoggedUserParticipant()) {
			sql += addSQLLong("a.participantFk", ContextHolder.getLoggedParticipantData().getParticipantId(), bean, map);
		}
		SearchResults sd = new SearchResults(bean);
		List<SearchInput> l = executePagging("SELECT a from AppRole a join fetch a.participant " + sql, bean, map);
		sd.setResults(l);
		if (sd.isExecuteCount())
			sd.setResultsCount(executeCount("SELECT count(*) from AppRole a " + sql, bean, map));
		return sd;
	}

	@SuppressWarnings("unchecked")
	@Override
	@MasterLogAnnotation
	public List<AppFunction> getAllAppFunctionAccess() {
		String hql = "SELECT f FROM AppFunction f";
		Query q = getCurrentSession().createQuery(hql);
		return (List<AppFunction>) q.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	@MasterLogAnnotation
	public List<AppFunction> getAssignedFunctionsForRole(Long appRoleFk) {
		return getCurrentSession().getNamedQuery("getAssignedFunctionsForRole").setLong("appRoleFk", appRoleFk).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	@MasterLogAnnotation
	public List<AppFunction> getNotAssignedFunctionsForRole(Long appRoleFk) {
		return getCurrentSession().getNamedQuery("getNotAssignedFunctionsForRole").setLong("appRoleFk", appRoleFk).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	@MasterLogAnnotation
	public List<AppRole> getAssignedRolesForUser(Long appUserFk) {
		Map<String, Object> map = new HashMap<String, Object>();
		String sql = "select a from AppRole a where a.id IN (SELECT f.appRoleFk FROM AppUserAppRole f WHERE f.appUserFk=:appUserFk)";
		map.put("appUserFk", appUserFk);
		if (ContextHolder.getLoggedUser().isUser()) {
			sql += " and a.participantFk = :participantFk";
			map.put("participantFk", ContextHolder.getLoggedParticipantData().getParticipantId());
		}
		sql += " order by a.roleName";
		Query q = getCurrentSession().createQuery(sql);
		prepareQuery(q, map);

		return q.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	@MasterLogAnnotation
	public List<AppRole> getNotAssignedRolesForUser(Long appUserFk, Long participantFk) {

		Map<String, Object> map = new HashMap<String, Object>();
		String sql = "select a from AppRole a where a.id NOT IN (SELECT f.appRoleFk FROM AppUserAppRole f WHERE f.appUserFk=:appUserFk)";
		map.put("appUserFk", appUserFk);
		if (ContextHolder.getLoggedUser().isUser()) {
			sql += " and a.participantFk = :participantFk";
			map.put("participantFk", ContextHolder.getLoggedParticipantData().getParticipantId());
		}
		if (participantFk != null) {
			sql += " and a.participantFk = :participantFk";
			map.put("participantFk", participantFk);
		}
		sql += " order by a.roleName";
		Query q = getCurrentSession().createQuery(sql);
		prepareQuery(q, map);

		return q.list();
	}

	@Override
	@MasterLogAnnotation
	public void deleteRolesForUser(Long appUserFk) {
		String sql = "DELETE FROM AppUserAppRole a WHERE a.appUserFk=:appUserFk";
		Query q = getCurrentSession().createQuery(sql);
		q.setLong("appUserFk", appUserFk);
		int x = q.executeUpdate();
		if (log.isDebugEnabled())
			log.debug("deleteRolesForUser delete current roles, count:" + x);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AppRole> getAllAppRoles() {
		String hql = "SELECT f FROM AppRole f where 1 =1";
		if (ContextHolder.getLoggedUser().isUser()) {
			hql += " AND f.participantFk = :participantFk";
		}
		Query q = getCurrentSession().createQuery(hql);
		if (ContextHolder.getLoggedUser().isUser()) {
			q.setLong("participantFk", ContextHolder.getLoggedParticipantData().getParticipantId());
		}
		return (List<AppRole>) q.list();
	}

	@Override
	@MasterLogAnnotation
	public SearchResults getAppUserAppRoleList(AppUserAppRole bean) {
		if (bean == null) {
			bean = new AppUserAppRole();
			bean.setItemsPerPage(Integer.MAX_VALUE);
		}
		String sql = SQL_DEFAULT_WHERE_1_1;
		Map<String, Object> map = new HashMap<String, Object>();
		sql += addSQLLong("a.id", bean.getId(), bean, map);
		sql += addSQLLong("a.appRoleFk", bean.getAppRoleFk(), bean, map);
		sql += addSQLLong("a.appUserFk", bean.getAppUserFk(), bean, map);
		SearchResults sd = new SearchResults(bean);
		List<SearchInput> l = executePagging("SELECT a from AppUserAppRole a " + sql, bean, map);
		sd.setResults(l);
		if (sd.isExecuteCount())
			sd.setResultsCount(executeCount("SELECT count(*) from AppUserAppRole a " + sql, bean, map));
		return sd;
	}

	@Override
	@MasterLogAnnotation
	public SearchResults getAppRoleAppFunctionList(AppRoleAppFunction bean) {
		if (bean == null) {
			bean = new AppRoleAppFunction();
			bean.setItemsPerPage(Integer.MAX_VALUE);
		}
		String sql = SQL_DEFAULT_WHERE_1_1;
		Map<String, Object> map = new HashMap<String, Object>();
		sql += addSQLLong("a.id", bean.getId(), bean, map);
		sql += addSQLLong("a.appRoleFk", bean.getAppRoleFk(), bean, map);
		sql += addSQLLong("a.appFunctionFk", bean.getAppFunctionFk(), bean, map);
		SearchResults sd = new SearchResults(bean);
		List<SearchInput> l = executePagging("SELECT a from AppRoleAppFunction a " + sql, bean, map);
		sd.setResults(l);
		if (sd.isExecuteCount())
			sd.setResultsCount(executeCount("SELECT count(*) from AppRoleAppFunction a " + sql, bean, map));
		return sd;
	}

	@SuppressWarnings("unchecked")
	@Override
	@MasterLogAnnotation
	public List<AppFunction> getAppFunctionAccessByUser(Long id) {
		String hql = "SELECT f FROM AppUserAppRole a, AppRoleAppFunction b, AppFunction f  ";
		hql += " WHERE a.appUserFk=:appUserFk AND a.appRoleFk=b.appRoleFk AND b.appFunctionFk=f.id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("appUserFk", id);
		Query q = getCurrentSession().createQuery(hql);
		prepareQuery(q, params);
		return q.list();
	}

	/**
	 * @see NomenclatureDao#preDeleteParticipantCheck(Participant) {
	 */
	@Override
	@MasterLogAnnotation
	public void preDeleteParticipantCheck(Participant b) {
		// for future use
	}

	/**
	 * @see NomenclatureDao#getParticipantList(Participant) {
	 */
	@Override
	@MasterLogAnnotation
	public SearchResults getParticipantList(Participant bean) {
		if (bean == null) {
			bean = new Participant();
			bean.setItemsPerPage(Integer.MAX_VALUE);
		}
		String sql = SQL_DEFAULT_WHERE_1_1;
		Map<String, Object> map = new HashMap<String, Object>();
		sql += addSQLLong("a.id", bean.getId(), bean, map);
		sql += addSQLString("a.name", bean.getName(), bean, map);
		sql += addSQLString("a.adrress", bean.getAdrress(), bean, map);
		sql += addSQLString("a.city", bean.getCity(), bean, map);
		sql += addSQLString("a.phone", bean.getPhone(), bean, map);
		if (ContextHolder.isLoggedUserParticipant()) {
			// sql += addSQLLong("a.participantFk", ContextHolder.getLoggedParticipantData().getParticipantId(), bean, map);
		}
		SearchResults sd = new SearchResults(bean);
		List<SearchInput> l = executePagging("SELECT a from Participant a " + sql, bean, map);
		sd.setResults(l);
		if (sd.isExecuteCount())
			sd.setResultsCount(executeCount("SELECT count(*) from Participant a " + sql, bean, map));
		return sd;
	}

	/**
	 * @see NomenclatureDao#preDeletePassPolicyCheck(PassPolicy) {
	 */
	@Override
	@MasterLogAnnotation
	public void preDeletePassPolicyCheck(PassPolicy b) {
		// for future use
	}

	/**
	 * @see NomenclatureDao#getPassPolicyList(PassPolicy) {
	 */
	@Override
	@MasterLogAnnotation
	public SearchResults getPassPolicyList(PassPolicy bean) {
		if (bean == null) {
			bean = new PassPolicy();
			bean.setItemsPerPage(Integer.MAX_VALUE);
		}
		String sql = SQL_DEFAULT_WHERE_1_1;
		Map<String, Object> map = new HashMap<String, Object>();
		sql += addSQLLong("a.id", bean.getId(), bean, map);
		sql += addSQLLong("a.changedByAppUserFk", bean.getChangedByAppUserFk(), bean, map);
		sql += addSQLString("a.name", bean.getName(), bean, map);
		sql += addSQLLong("a.participantFk", bean.getParticipantFk(), bean, map);
		sql += addSQLInt("a.passMinPeriodInDays", bean.getPassMinPeriodInDays(), bean, map);
		sql += addSQLInt("a.passMaxPeriodInDays", bean.getPassMaxPeriodInDays(), bean, map);
		sql += addSQLInt("a.passMinLength", bean.getPassMinLength(), bean, map);
		sql += addSQLInt("a.passMinHistoryRepeat", bean.getPassMinHistoryRepeat(), bean, map);
		sql += addSQLInt("a.passLoginAttempt", bean.getPassLoginAttempt(), bean, map);
		sql += addSQLInt("a.passBlockWaitTime", bean.getPassBlockWaitTime(), bean, map);
		sql += addSQLString("a.passMustHaveLowercase", bean.getPassMustHaveLowercase(), bean, map);
		sql += addSQLString("a.passMustHaveNumber", bean.getPassMustHaveNumber(), bean, map);
		sql += addSQLString("a.passMustHaveUppercase", bean.getPassMustHaveUppercase(), bean, map);
		sql += addSQLString("a.passMustHaveSpecialChars", bean.getPassMustHaveSpecialChars(), bean, map);
		sql += addSQLString("a.passUnblockAutomatically", bean.getPassUnblockAutomatically(), bean, map);
		sql += addSQLString("a.passSpecialChars", bean.getPassSpecialChars(), bean, map);
		if (ContextHolder.isLoggedUserParticipant()) {
			 sql += addSQLLong("a.participantFk", ContextHolder.getLoggedParticipantData().getParticipantId(), bean, map);
		}
		if (bean.isForAdmin())
			sql += " and a.participantFk IS NULL";
		SearchResults sd = new SearchResults(bean);
		List<SearchInput> l = executePagging("SELECT a from PassPolicy a left join fetch a.participant join fetch a.changedByAppUser" + sql, bean, map);
		sd.setResults(l);
		if (sd.isExecuteCount())
			sd.setResultsCount(executeCount("SELECT count(*) from PassPolicy a " + sql, bean, map));
		return sd;
	}

	@Override
	@MasterLogAnnotation
	public void validateAppUser(AppUser bean) {
		String hql = "select 1 from AppUser e WHERE e.username = :username";
		Query q = getCurrentSession().createQuery(hql);
		q.setString("username", bean.getUsername());

		Object o = q.uniqueResult();
		if (o != null)
			throw new ValidateException("username", "error.appUser.unique", bean.getUsername());
	}

	@SuppressWarnings("unchecked")
	@Override
	@MasterLogAnnotation
	public List<PassHistory> getPasswordHistoryByUserId(Long appUserFk) {
		Query q = getCurrentSession().createQuery("from PassHistory c WHERE c.appUserFk=:appUserFk ORDER BY c.changedDt asc");
		q.setLong("appUserFk", appUserFk);

		return (List<PassHistory>) q.list();
	}

	@Override
	public void deleteAppRoleAppFunctionForRole(Long id) {
		String sql = "DELETE FROM AppRoleAppFunction a WHERE a.appRoleFk=:appRoleFk";
		Query q = getCurrentSession().createQuery(sql);
		q.setLong("appRoleFk", id);
		int x = q.executeUpdate();
		if (log.isDebugEnabled())
			log.debug("deleteAppRoleAppFunctionForRole delete current functions, count:" + x);
	}

}