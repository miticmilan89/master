package rs.milanmitic.master.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import rs.milanmitic.master.common.ContextHolder;
import rs.milanmitic.master.common.aop.MasterLogAnnotation;
import rs.milanmitic.master.common.data.LoggedUserData;
import rs.milanmitic.master.common.pagging.SearchInput;
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
@Repository
public class CommonHibernateDao extends BasicDaoHibernate implements CommonDao {

	@Override
	@MasterLogAnnotation
	public void logoutUser(LoggedUserData ld) {
		// for future use
	}

	@MasterLogAnnotation
	@Override
	public AppUser getAppUserByUserName(String username) {
		String hql = "SELECT a FROM AppUser a WHERE a.username = :username ";
		Query q = getCurrentSession().createQuery(hql);
		q.setMaxResults(1);
		q.setParameter("username", username.toLowerCase().trim());

		AppUser d = (AppUser) q.uniqueResult();
		if (d != null && d.getParticipantFk() != null)
			Hibernate.initialize(d.getParticipant());
		return d;
	}

	@Override
	@MasterLogAnnotation
	public SearchResults getAuditLogList(AuditLog bean) {
		String sql = SQL_DEFAULT_WHERE_1_1;
		Map<String, Object> map = new HashMap<String, Object>();
		if (ContextHolder.isLoggedUserParticipant()) {
			sql += " AND a.participantFk=:participantFk ";
			map.put("participantFk", ContextHolder.getLoggedParticipantData().getParticipantId());
		}
		if (bean != null) {
			if (bean.getId() != null) {
				sql += " AND a.id = :id ";
				map.put("id", bean.getId());
			}
			if (bean.getAuditStatus() != null) {
				sql += " AND a.auditStatus=:auditStatus ";
				map.put("auditStatus", bean.getAuditStatus());
			}
			if (StringUtils.isNotBlank(bean.getTableName())) {
				sql += " AND a.tableName LIKE :tableName ";
				map.put("tableName", "%" + bean.getTableName().trim().toUpperCase() + "%");
			}
			if (StringUtils.isNotBlank(bean.getAuditType())) {
				sql += " AND a.auditType LIKE :auditType ";
				map.put("auditType", "%" + bean.getAuditType().trim().toUpperCase() + "%");
			}
			if (bean.getDateFrom() != null && bean.getDateTo() != null) {
				sql += " AND a.auditTimestamp BETWEEN :from and :to ";
				map.put("from", bean.getDateFrom());
				map.put("to", bean.getDateTo());

			} else if (bean.getDateFrom() != null) {
				sql += " AND a.auditTimestamp >= :from ";
				map.put("from", bean.getDateFrom());

			} else if (bean.getDateTo() != null) {
				sql += " AND a.auditTimestamp BETWEEN <= :to ";
				map.put("to", bean.getDateTo());
			}

		}

		SearchResults sd = new SearchResults(bean);
		List<SearchInput> l = executePagging("SELECT a from AuditLog a left outer join fetch a.appUser left outer join fetch a.participant " + sql, bean, map);
		if (sd.isExecuteCount())
			sd.setResultsCount(executeCount("SELECT count(*) from AuditLog a " + sql, bean, map));
		sd.setResults(l);
		return sd;
	}

	@Override
	@MasterLogAnnotation
	public SearchResults getActivityLogList(ActivityLog bean) {
		String sql = SQL_DEFAULT_WHERE_1_1;
		Map<String, Object> map = new HashMap<String, Object>();
		if (ContextHolder.isLoggedUserParticipant()) {
			sql += " AND a.participantFk=:participantFk ";
			map.put("participantFk", ContextHolder.getLoggedParticipantData().getParticipantId());
		}
		if (bean != null) {
			if (bean.getId() != null) {
				sql += " AND a.id=:id ";
				map.put("id", bean.getId());
			}
			if (StringUtils.isNotBlank(bean.getUrl())) {
				sql += " AND lower(a.url) LIKE :url ";
				map.put("url", "%" + bean.getUrl().trim().toLowerCase() + "%");
			}
			if (StringUtils.isNotBlank(bean.getUserIp())) {
				sql += " AND a.userIp=:userIp ";
				map.put("userIp", bean.getUserIp());
			}
			if (StringUtils.isNotBlank(bean.getThreadName())) {
				sql += " AND a.threadName=:threadName ";
				map.put("threadName", bean.getThreadName());
			}

			if (bean.getDateFrom() != null && bean.getDateTo() != null) {
				sql += " AND a.datetimeStart BETWEEN :from and :to ";
				map.put("from", bean.getDateFrom());
				map.put("to", bean.getDateTo());

			} else if (bean.getDateFrom() != null) {
				sql += " AND a.datetimeStart >= :from ";
				map.put("from", bean.getDateFrom());

			} else if (bean.getDateTo() != null) {
				sql += " AND a.datetimeStart BETWEEN <= :to ";
				map.put("to", bean.getDateTo());
			}
		}

		SearchResults sd = new SearchResults(bean);
		List<SearchInput> l = executePagging("SELECT a.id from ActivityLog a " + sql, bean, map);
		if (sd.isExecuteCount())
			sd.setResultsCount(executeCount("SELECT count(*) from ActivityLog a " + sql, bean, map));
		Query fastQ = createQuery("SELECT a from ActivityLog a left outer join fetch a.appUser left outer join fetch a.participant WHERE a.id=:id");
		List<SearchInput> newList = new ArrayList<SearchInput>();
		for (Object o : l) {
			fastQ.setLong("id", (Long) o);
			ActivityLog a = (ActivityLog) fastQ.uniqueResult();
			evict(a);
			newList.add(a);

		}
		sd.setResults(newList);
		return sd;
	}

	@Override
	@Transactional
	public SearchResults getAppConfigList(AppConfig bean) {
		if (bean == null) {
			bean = new AppConfig();
			bean.setItemsPerPage(Integer.MAX_VALUE);
		}
		String sql = SQL_DEFAULT_WHERE_1_1;
		Map<String, Object> map = new HashMap<String, Object>();
		sql += addSQLString("a.paramName", bean.getParamName(), bean, map);
		sql += addSQLString("a.paramValue", bean.getParamValue(), bean, map);
		sql += addSQLLong("a.participant", bean.getParticipantFk(), bean, map);
		SearchResults sd = new SearchResults(bean);
		List<SearchInput> l = executePagging("SELECT a from AppConfig a " + sql, bean, map);
		sd.setResults(l);
		if (sd.isExecuteCount())
			sd.setResultsCount(executeCount("SELECT count(*) from AppConfig a " + sql, bean, map));
		return sd;
	}

}
