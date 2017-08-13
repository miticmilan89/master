package rs.milanmitic.master.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Basic Methods
 * 
 * @author milan
 */

public interface BasicDao {
	/**
	 * Create new record
	 * 
	 * @param o
	 */
	public void save(Object o);

	/**
	 * Update record
	 * 
	 * @param o
	 */
	public void update(Object o);

	/**
	 * Delete record by setting delete flag
	 * 
	 * @param o
	 */
	public void delete(Object o);

	/**
	 * Get record by PK
	 * 
	 * @param
	 * @param k
	 * @return
	 */
	public <T extends Object> T getByPk(Serializable pk, Class<T> k);

	/**
	 * Remove object from hibernate session
	 * 
	 * @param o
	 */
	void evict(Serializable o);

	public <T extends Object> List<T> listNamedQuery(String namedQuery, Map<String, Object> params, Class<T> k);

	public <T extends Object> T uniqueNamedQuery(String namedQuery, Map<String, Object> params, Class<T> k);

	public <T extends Object> T uniqueNamedQueryNoException(String namedQuery, Map<String, Object> params, Class<T> k);

	public int executeUpdateNamedQuery(String namedQuery, Map<String, Object> params);

}
