package rs.milanmitic.master.repository;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gdata.util.common.base.Objects;

import rs.milanmitic.master.common.ContextHolder;
import rs.milanmitic.master.common.aop.MasterLogAnnotation;
import rs.milanmitic.master.common.exception.UnpredictableException;
import rs.milanmitic.master.common.pagging.SearchInput;
import rs.milanmitic.master.common.util.Utils;

/**
 * @see BasicDao
 * @author milan
 * 
 */
@Component
public abstract class BasicDaoHibernate implements BasicDao {

	protected final Logger log = LogManager.getLogger(this.getClass());

	private static final String LABEL_AND = " AND ";
	protected static final String SQL_DEFAULT_WHERE_1_1 = " WHERE 1=1 ";

	@Autowired
	protected SessionFactory sessionFactory;

	@Override
	public void evict(Serializable o) {
		sessionFactory.getCurrentSession().evict(o);
	}

	/**
	 * Save object to DB
	 */
	@Override
	@MasterLogAnnotation
	public void save(Object o) {
		Utils.trimAllStrings(o);
		sessionFactory.getCurrentSession().save(o);
		sessionFactory.getCurrentSession().flush();
	}

	/**
	 * Update object in DB
	 */
	@Override
	@MasterLogAnnotation
	public void update(Object o) {
		Utils.trimAllStrings(o);
		sessionFactory.getCurrentSession().update(o);
		sessionFactory.getCurrentSession().flush();
	}

	/**
	 * Delete object from DB (not for real, just set DELETE flag)
	 */
	@Override
	@MasterLogAnnotation
	public void delete(Object o) {
		sessionFactory.getCurrentSession().delete(o);
		sessionFactory.getCurrentSession().flush();
	}

	/**
	 * Get object from DB by ID
	 */
	@SuppressWarnings("unchecked")
	@Override
	@MasterLogAnnotation
	public <T extends Object> T getByPk(Serializable pk, Class<T> k) {
		Object o = sessionFactory.getCurrentSession().get(k, pk);
		return (T) o;
	}

	/**
	 * Return hibernate session
	 * 
	 * @return
	 */
	public Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	/**
	 * Prepare query for PAGING
	 * 
	 * @param sql
	 * @param si
	 * @return
	 */
	protected Query makePagging(String sql, SearchInput si) {
		int cp = si != null && si.getCurrentPage() != null ? si.getCurrentPage() : 1;
		cp--;
		int ipp = si != null && si.getItemsPerPage() != null ? si.getItemsPerPage() : ContextHolder.getFormatPatterns().getItemsPerPage();
		int start = cp * ipp;
		long end = (cp + 1l) * ipp;
		if (log.isDebugEnabled())
			log.debug("---> Current page:" + cp + ", itemsPerPage:" + ipp + ", start=" + start + ",end:" + end);

		Query q = getCurrentSession().createQuery(sql);
		q.setFirstResult(start);
		q.setMaxResults(ipp);
		return q;
	}

	/**
	 * Prepare query with supplied data
	 * 
	 * @param q
	 * @param params
	 * @return
	 */
	protected Query prepareQuery(Query q, Map<String, Object> params) {
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		return q;
	}

	protected Query createQuery(String sql) {
		return getCurrentSession().createQuery(sql);
	}
	/**
	 * Execute pagging query
	 * 
	 * @param sql
	 * @param si
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<SearchInput> executePagging(String sql, SearchInput si, Map<String, Object> params) {
		String orderBy = si != null ? si.getOrderSQL() : "";

		Query q = makePagging(sql + orderBy, si);
		return prepareQuery(q, params).list();
	}

	/**
	 * Execute count query
	 * 
	 * @param sql
	 * @param si
	 * @param params
	 * @return
	 */
	@MasterLogAnnotation
	protected Long executeCount(String sql, SearchInput si, Map<String, Object> params) {
		Query q = getCurrentSession().createQuery(sql);
		Object s = prepareQuery(q, params).uniqueResult();
		return new Long(s != null ? s.toString() : "0");
	}

	@SuppressWarnings("unchecked")
	@MasterLogAnnotation
	protected List<SearchInput> executeQuery(String sql, Map<String, Object> params) {
		Query q = getCurrentSession().createQuery(sql);
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		return q.list();
	}

	protected static String addSQLString(String column, String value, SearchInput bean, Map<String, Object> map) {
		if (StringUtils.isBlank(value))
			return "";
		return addSQLString(column, StringUtils.replace(column, ".", ""), value, bean, map);
	}

	/**
	 * Make string as this: <br/>
	 * " AND lower(a.firstName) LIKE :firstName " <br/>
	 * and add parameter to MAP depending on LIKE, CASE SENSITIVE
	 * 
	 * @param column
	 * @param param
	 * @param value
	 * @param bean
	 * @param map
	 * @return
	 */
	protected static String addSQLString(String column, String param, String value, SearchInput bean, Map<String, Object> map) {
		if (StringUtils.isBlank(value))
			return "";
		StringBuilder sb = new StringBuilder(BasicDaoHibernate.LABEL_AND);
		// 1. trim string
		value = value.trim();

		// 2.check is case sensitive
		if (bean.isCaseSensitiveOn()) {
			value = value.toLowerCase();
			sb.append("lower(").append(column).append(") ");
		} else {
			sb.append(column).append(" ");
		}
		// 3. check is value contain LIKE character % or not
		boolean valueLike = value.indexOf('%') != -1;
		if (bean.isStringLikeOn() || valueLike) {
			sb.append(" LIKE ");
		} else {
			sb.append(" = ");
		}
		sb.append(":").append(param).append(" ");
		// 4. add parameter to MAP
		if (valueLike)
			map.put(param, value);
		else if (bean.isStringLikeOn())
			map.put(param, "%" + value + "%");
		else
			map.put(param, value);

		return sb.toString();
	}

	protected static String addSQLInt(String column, String param, Integer value, Map<String, Object> map) {
		return addSQLInt(column, param, value, value, map);
	}

	protected static String addSQLInt(String column, Integer value, Map<String, Object> map) {
		return addSQLInt(column, StringUtils.replace(column, ".", ""), value, value, map);
	}

	protected static String addSQLInt(String column, String param, Integer valueFrom, Integer valueTo, Map<String, Object> map) {
		if (valueFrom == null && valueTo == null)
			return "";
		StringBuilder sb = new StringBuilder(BasicDaoHibernate.LABEL_AND);

		// 2.check is case sensitive
		sb.append(column).append(" ");

		// 3. check is value contain LIKE character % or not
		boolean bothValuesExist = valueFrom != null && valueTo != null;
		boolean equals = bothValuesExist && valueFrom.intValue() == valueTo.intValue();
		if (equals) {
			sb.append(" = ");
			sb.append(":").append(param).append(" ");
			map.put(param, valueFrom);
		} else if (bothValuesExist) {
			sb.append(" BETWEEN :").append(param).append("From AND  :").append(param).append("To");
			map.put(param + "From", valueFrom);
			map.put(param + "To", valueTo);
		} else if (valueFrom != null) {
			sb.append(" >= ");
			sb.append(":").append(param).append(" ");
			map.put(param, valueFrom);
		} else {
			sb.append(" <= ");
			sb.append(":").append(param).append(" ");
			map.put(param, valueTo);
		}
		return sb.toString();
	}

	protected static String addSQLLong(String column, String param, Long value, SearchInput bean, Map<String, Object> map) {
		return addSQLLong(column, param, value, value, bean, map);
	}

	protected static String addSQLLong(String column, Long value, SearchInput bean, Map<String, Object> map) {
		return addSQLLong(column, StringUtils.replace(column, ".", ""), value, value, bean, map);
	}
	
	protected static String addSQLInt(String column, Integer value, SearchInput bean, Map<String, Object> map) {
		return addSQLInt(column, StringUtils.replace(column, ".", ""), value, value, bean, map);
	}

	protected static String addSQLLong(String column, String param, Long valueFrom, Long valueTo, SearchInput bean, Map<String, Object> map) {
		if ((valueFrom == null && valueTo == null) || bean == null)
			return "";
		StringBuilder sbb = new StringBuilder(BasicDaoHibernate.LABEL_AND);

		// 2.check is case sensitive
		sbb.append(column).append(" ");

		// 3. check is value contain LIKE character % or not
		boolean bothValuesExist = valueFrom != null && valueTo != null;
		boolean equals = bothValuesExist && valueFrom.longValue() == valueTo.longValue();
		if (equals) {
			sbb.append(" = ");
			sbb.append(":").append(param).append(" ");
			map.put(param, valueFrom);
		} else if (bothValuesExist) {
			sbb.append(" BETWEEN  :").append(param).append("From  AND  :").append(param).append("To");
			map.put(param + "From", valueFrom);
			map.put(param + "To", valueTo);
		} else if (valueFrom != null) {
			sbb.append(" >= ");
			sbb.append(":").append(param).append(" ");
			map.put(param, valueFrom);
		} else {
			sbb.append(" <= ");
			sbb.append(":").append(param).append(" ");
			map.put(param, valueTo);
		}
		return sbb.toString();
	}
	
	protected static String addSQLInt(String column, String param, Integer valueFrom, Integer valueTo, SearchInput bean, Map<String, Object> map) {
		if ((valueFrom == null && valueTo == null) || bean == null)
			return "";
		StringBuilder sbb = new StringBuilder(BasicDaoHibernate.LABEL_AND);

		// 2.check is case sensitive
		sbb.append(column).append(" ");

		// 3. check is value contain LIKE character % or not
		boolean bothValuesExist = valueFrom != null && valueTo != null;
		boolean equals = bothValuesExist && valueFrom.longValue() == valueTo.longValue();
		if (equals) {
			sbb.append(" = ");
			sbb.append(":").append(param).append(" ");
			map.put(param, valueFrom);
		} else if (bothValuesExist) {
			sbb.append(" BETWEEN  :").append(param).append("From  AND  :").append(param).append("To");
			map.put(param + "From", valueFrom);
			map.put(param + "To", valueTo);
		} else if (valueFrom != null) {
			sbb.append(" >= ");
			sbb.append(":").append(param).append(" ");
			map.put(param, valueFrom);
		} else {
			sbb.append(" <= ");
			sbb.append(":").append(param).append(" ");
			map.put(param, valueTo);
		}
		return sbb.toString();
	}

	protected static String addSQLDate(String column, Date valueFrom, Date valueTo, SearchInput bean, Map<String, Object> map) {
		return addSQLDate(column, StringUtils.replace(column, ".", ""), valueFrom, valueTo, bean, map);
	}

	protected static String addSQLDate(String column, Date valueFrom, String timeFrom, Date valueTo, String timeTo, SearchInput bean, Map<String, Object> map) {
		if (valueFrom == null && valueTo == null)
			return "";
		if (StringUtils.isNotBlank(timeFrom))
			valueFrom = Utils.setTime(valueFrom, timeFrom);
		else
			valueFrom = Utils.setMinTime(valueFrom);
		if (StringUtils.isNotBlank(timeTo))
			valueTo = Utils.setTime(valueTo, timeTo);
		else
			valueTo = Utils.setMaxTime(valueTo);

		return addSQLDate(column, StringUtils.replace(column, ".", ""), valueFrom, valueTo, bean, map);
	}

	protected static String addSQLDate(String column, String param, Date valueFrom, Date valueTo, SearchInput bean, Map<String, Object> map) {
		if ((valueFrom == null && valueTo == null) || bean == null)
			return "";
		StringBuilder sb = new StringBuilder(BasicDaoHibernate.LABEL_AND);

		// 2.check is case sensitive
		sb.append(column).append(" ");

		// 3. check is value contain LIKE character % or not
		boolean bothValuesExist = valueFrom != null && valueTo != null;
		if (bothValuesExist) {
			sb.append(" BETWEEN :").append(param).append("From  AND :").append(param).append("To");
			map.put(param + "From", valueFrom);
			map.put(param + "To", valueTo);
		} else if (valueFrom != null) {
			sb.append(" >= ");
			sb.append(":").append(param).append(" ");
			map.put(param, valueFrom);
		} else {
			sb.append(" <= ");
			sb.append(":").append(param).append(" ");
			map.put(param, valueTo);
		}
		return sb.toString();
	}

	protected static String addSQLDate(String column, Timestamp valueFrom, Timestamp valueTo, Map<String, Object> map) {
		return addSQLDate(column, StringUtils.replace(column, ".", ""), valueFrom, valueTo, map);
	}

	protected static String addSQLDate(String column, Timestamp valueFrom, String timeFrom, Timestamp valueTo, String timeTo, Map<String, Object> map) {
		if (valueFrom == null && valueTo == null)
			return "";
		if (StringUtils.isNotBlank(timeFrom))
			valueFrom = Utils.setTime(valueFrom, timeFrom);
		else
			valueFrom = Utils.setMinTime(valueFrom);
		if (StringUtils.isNotBlank(timeTo))
			valueTo = Utils.setTime(valueTo, timeTo);
		else
			valueTo = Utils.setMaxTime(valueTo);

		return addSQLDate(column, StringUtils.replace(column, ".", ""), valueFrom, valueTo, map);
	}

	protected static String addSQLDate(String column, String param, Timestamp valueFrom, Timestamp valueTo, Map<String, Object> map) {
		if (valueFrom == null && valueTo == null)
			return "";
		StringBuilder sb = new StringBuilder(BasicDaoHibernate.LABEL_AND);

		// 2.check is case sensitive
		sb.append(column).append(" ");

		// 3. check is value contain LIKE character % or not
		boolean bothValuesExist = valueFrom != null && valueTo != null;
		if (bothValuesExist) {
			sb.append(" BETWEEN  :").append(param).append("From  AND :").append(param).append("To");
			map.put(param + "From", valueFrom);
			map.put(param + "To", valueTo);
		} else if (valueFrom != null) {
			sb.append(" >= ");
			sb.append(":").append(param).append(" ");
			map.put(param, valueFrom);
		} else {
			sb.append(" <= ");
			sb.append(":").append(param).append(" ");
			map.put(param, valueTo);
		}
		return sb.toString();
	}

	protected static String addSQLDouble(String column, String param, Double valueFrom, Double valueTo, Map<String, Object> map) {
		if (valueFrom == null && valueTo == null)
			return "";
		StringBuilder sb = new StringBuilder(BasicDaoHibernate.LABEL_AND);

		// 2.check is case sensitive
		sb.append(column).append(" ");

		// 3. check is value contain LIKE character % or not
		boolean bothValuesExist = valueFrom != null && valueTo != null;
		boolean equals = bothValuesExist && Objects.equal(valueFrom, valueTo);
		if (equals) {
			sb.append(" = ");
			sb.append(":").append(param).append(" ");
			map.put(param, valueFrom);
		} else if (bothValuesExist) {
			sb.append(" BETWEEN ");
			sb.append(":").append(param).append("From AND :").append(param).append("To");
			map.put(param + "From", valueFrom);
			map.put(param + "To", valueTo);
		} else if (valueFrom != null) {
			sb.append(" >= ");
			sb.append(":").append(param).append(" ");
			map.put(param, valueFrom);
		} else {
			sb.append(" <= ");
			sb.append(":").append(param).append(" ");
			map.put(param, valueTo);
		}
		return sb.toString();
	}

	protected static String addSQLBigDecimal(String column, String param, BigDecimal valueFrom, BigDecimal valueTo, Map<String, Object> map) {
		if (valueFrom == null && valueTo == null)
			return "";
		StringBuilder sb = new StringBuilder(BasicDaoHibernate.LABEL_AND);

		// 2.check is case sensitive
		sb.append(column).append(" ");

		// 3. check is value contain LIKE character % or not
		boolean bothValuesExist = valueFrom != null && valueTo != null;
		boolean equals = bothValuesExist && valueFrom.compareTo(valueTo) == 0;
		if (equals) {
			sb.append(" = ");
			sb.append(":").append(param).append(" ");
			map.put(param, valueFrom);
		} else if (bothValuesExist) {
			sb.append(" BETWEEN ");
			sb.append(":").append(param).append("From AND :").append(param).append("To");
			map.put(param + "From", valueFrom);
			map.put(param + "To", valueTo);
		} else if (valueFrom != null) {
			sb.append(" >= ");
			sb.append(":").append(param).append(" ");
			map.put(param, valueFrom);
		} else {
			sb.append(" <= ");
			sb.append(":").append(param).append(" ");
			map.put(param, valueTo);
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> listNamedQuery(String namedQuery, Map<String, Object> params, Class<T> k) {
		Query q = getCurrentSession().getNamedQuery(namedQuery);
		if (params != null)
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				q.setParameter(entry.getKey(), entry.getValue());
			}
		List<T> list = (List<T>) q.list();
		return list == null || list.isEmpty() ? null : list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T uniqueNamedQuery(String namedQuery, Map<String, Object> params, Class<T> k) {
		Query q = getCurrentSession().getNamedQuery(namedQuery);
		if (params != null)
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				q.setParameter(entry.getKey(), entry.getValue());
			}
		return (T) q.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T uniqueNamedQueryNoException(String namedQuery, Map<String, Object> params, Class<T> k) {
		Query q = getCurrentSession().getNamedQuery(namedQuery);
		if (params != null)
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				q.setParameter(entry.getKey(), entry.getValue());
			}
		List<T> list = q.list();
		if (list != null && list.size() > 1)
			throw new UnpredictableException("More than one result returned for namedQuery:" + namedQuery + ", params:" + params);

		return (T) list != null && !list.isEmpty() ? list.get(0) : null;
	}

	@Override
	public int executeUpdateNamedQuery(String namedQuery, Map<String, Object> params) {
		Query q = getCurrentSession().getNamedQuery(namedQuery);
		if (params != null)
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				q.setParameter(entry.getKey(), entry.getValue());
			}
		return q.executeUpdate();
	}

	/**
	 * Close Statement and ignore error if occured
	 * 
	 * @param is
	 */
	public void close(Statement is) {
		try {
			if (is != null) {
				is.close();
			}
		} catch (Exception t) {
			if (log.isDebugEnabled())
				log.error("Error, ignore, close Statement", t);

		}
	}

	/**
	 * Close Statement and ignore error if occured
	 * 
	 * @param is
	 */
	public void close(ResultSet is) {
		try {
			if (is != null) {
				is.close();
			}
		} catch (Exception t) {
			if (log.isDebugEnabled())
				log.error("Error, ignore, close ResultSet", t);

		}
	}

}
