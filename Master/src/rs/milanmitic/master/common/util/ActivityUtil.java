package rs.milanmitic.master.common.util;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EmbeddedId;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import rs.milanmitic.master.common.EventLogIgnoreFields;
import rs.milanmitic.master.common.exception.UnpredictableException;

/**
 * Activity util class, used to transform bean to string
 * 
 * @author milan
 */
public class ActivityUtil implements Serializable {

	private static final Logger log = LogManager.getLogger(ActivityUtil.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<ActivitySingleData> activityList;
	public static final String OPEN_ERR_TAG = "<err_root>";
	public static final String CLOSE_ERR_TAG = "</err_root>";
	public static final String OPEN_TAG = "<root>";
	public static final String CLOSE_TAG = "</root>";

	public static final String LOG_DATE_FORMAT = "dd.MM.yyyy";
	public static final String LOG_DATE_TIME_FORMAT = "dd.MM.yyyy hh:mm:ss";

	private static final Set<String> simpleType = new HashSet<String>();
	private static final Set<String> dateType = new HashSet<String>();
	static {
		simpleType.add(String.class.getName());
		simpleType.add(Integer.class.getName());
		simpleType.add(Double.class.getName());
		simpleType.add(Float.class.getName());
		simpleType.add(Short.class.getName());
		simpleType.add(Long.class.getName());
		simpleType.add(BigDecimal.class.getName());
		simpleType.add(Boolean.class.getName());

		dateType.add(java.util.Date.class.getName());
		dateType.add(java.sql.Date.class.getName());
		dateType.add(java.sql.Timestamp.class.getName());
		dateType.add(java.util.Calendar.class.getName());

	}
	private static boolean alwaysIgnoreNull = true;

	private boolean ignoreNull = false;

	private List<String> excludeFieldNames;

	/**
	 * Include or exclude inherited fields
	 */
	private boolean includeParent = false;

	private static List<String> excludeFieldNamesStatic;
	static {
		excludeFieldNamesStatic = new ArrayList<String>();
		excludeFieldNamesStatic.add("empty");
		excludeFieldNamesStatic.add("itemsPerPage");
		excludeFieldNamesStatic.add("orderAsc");
		excludeFieldNamesStatic.add("orderColumn");
		excludeFieldNamesStatic.add("currentPage");
		excludeFieldNamesStatic.add("columnMapping");
		excludeFieldNamesStatic.add("pswdhashtx");
		excludeFieldNamesStatic.add("pswdtx");
		excludeFieldNamesStatic.add("pswdtx_");
		excludeFieldNamesStatic.add("password");
	}

	/**
	 * Constructor
	 */
	public ActivityUtil() {
		this.activityList = new ArrayList<ActivitySingleData>();
		this.excludeFieldNames = new ArrayList<String>();
	}

	/**
	 * Do not add supplied field name in activity bean
	 * 
	 * @param fieldName
	 */
	public void excludeField(String fieldName) {
		excludeFieldNames.add(fieldName);

	}

	/**
	 * Prepare value for logging
	 * 
	 * @param value
	 * @param ignoreNull
	 * @return
	 */
	public static String prepareValue(Object value, boolean ignoreNull) {
		String g = null;

		if (value != null) {
			if (value instanceof java.sql.Date) {
				SimpleDateFormat sdf = new SimpleDateFormat(LOG_DATE_FORMAT);
				g = sdf.format((java.sql.Date) value);

			} else if (value instanceof java.util.Date) {
				SimpleDateFormat sdf = new SimpleDateFormat(LOG_DATE_TIME_FORMAT);
				g = sdf.format((java.util.Date) value);

			} else if (value instanceof java.util.Calendar) {
				SimpleDateFormat sdf = new SimpleDateFormat(LOG_DATE_TIME_FORMAT);
				g = sdf.format((java.util.Calendar) value);

			} else
				g = value.toString();
			g = StringEscapeUtils.escapeXml11(g);
		} else {
			if (!ignoreNull && !alwaysIgnoreNull)
				g = "null";
		}
		return g;
	}

	public ActivityUtil addActivity(String propName, Object value) {
		return internalAddActivity(propName, value);
	}

	public ActivityUtil replaceActivity(String propName, Object value) {
		if (activityList != null)
			activityList.clear();
		return internalAddActivity(propName, value);
	}

	/**
	 * Add value to activity list
	 * 
	 * @param propName
	 * @param value
	 */
	private ActivityUtil internalAddActivity(String propName, Object value) {
		String g = prepareValue(value, ignoreNull);
		if (StringUtils.isNotBlank(g))
			activityList.add(new ActivitySingleData(propName, g));
		return this;

	}

	public ActivityUtil addActivityXML(String xml) {
		if (StringUtils.isNotBlank(xml))
			activityList.add(new ActivitySingleData(xml));
		return this;

	}

	public ActivityUtil addEmptyNode(String nodeName) {
		if (StringUtils.isNotBlank(nodeName))
			activityList.add(new ActivitySingleData("<" + nodeName + "/>"));
		return this;

	}

	/**
	 * Add/parse array of values to xml
	 * 
	 * @param propName
	 * @param value
	 * @return
	 */
	public ActivityUtil addActivity(String propName, Object[] value) {
		String g = "";
		if (value != null) {
			int n = value.length;
			StringBuilder sb = new StringBuilder(n);
			for (int i = 0; i < n; i++) {
				if (i > 0)
					sb.append(",");
				sb.append(value[i]);
			}
			g = StringEscapeUtils.escapeXml11(sb.toString());

		} else {
			if (!ignoreNull && !alwaysIgnoreNull) {
				g = "null";
			}
		}
		if (StringUtils.isNotBlank(g)) {
			activityList.add(new ActivitySingleData(propName, g));
		}
		return this;
	}

	/**
	 * Add all properties of bean to activity list
	 * 
	 * @param bean
	 */
	public ActivityUtil addActivity(Object bean) {
		return internalAddActivity(bean);
	}

	public ActivityUtil replaceActivity(Object bean) {
		if (activityList != null)
			activityList.clear();
		return internalAddActivity(bean);
	}

	private ActivityUtil internalAddActivity(Object bean) {
		if (bean == null)
			return this;
		try {
			if (bean.getClass().isPrimitive()) {
				addActivity("value_" + bean.getClass().getSimpleName().toLowerCase(), bean);
				return this;
			}
			if (bean instanceof EventLogIgnoreFields)
				excludeFieldNames.addAll(((EventLogIgnoreFields) bean).getEventIgnoreFields());

			Field[] fieldList = bean.getClass().getDeclaredFields();
			addFields(bean, fieldList);

			if (isIncludeParent() && bean.getClass().getSuperclass() != null) {
				addFields(bean, bean.getClass().getSuperclass().getDeclaredFields());
				if (bean.getClass().getSuperclass().getSuperclass() != null) {
					addFields(bean, bean.getClass().getSuperclass().getSuperclass().getDeclaredFields());
				}
			}

		} catch (Exception t) {
			throw UnpredictableException.create("Error retreiving Activity data from bean:" + bean, t);
		}
		return this;
	}

	private void addFields(Object bean, Field[] fieldList) throws IllegalAccessException {
		int ll = fieldList.length;
		for (int i = 0; i < ll; i++) {
			String n = fieldList[i].getName();

			if (shouldSkipField(fieldList, i) || isExcluded(n) || isExcluded(n.toLowerCase()))
				continue;

			if (n.length() > 1)
				n = n.substring(0, 1).toLowerCase() + n.substring(1);

			adSingleField(bean, fieldList, i, n);
		}
	}

	private boolean isExcluded(String nl) {
		return excludeFieldNames.contains(nl) || excludeFieldNamesStatic.contains(nl) || Utils.isProtecteFieldContent(nl);
	}

	private boolean shouldSkipField(Field[] fieldList, int i) {
		return fieldList[i].getType() == null || java.lang.reflect.Modifier.isStatic(fieldList[i].getModifiers()) || fieldList[i].isAnnotationPresent(Transient.class);
	}

	private void adSingleField(Object bean, Field[] fieldList, int i, String n) throws IllegalAccessException {
		String t = fieldList[i].getType().getName();
		try {
			if (!fieldList[i].isAccessible())
				fieldList[i].setAccessible(true);
			if (fieldList[i].getType().isPrimitive()) {
				addActivity(fieldList[i].getName(), fieldList[i].get(bean));

			} else if (simpleType.contains(t) || dateType.contains(t)) {
				addActivity(n, fieldList[i].get(bean));

			} else if (fieldList[i].isAnnotationPresent(EmbeddedId.class)) {
				addActivity(fieldList[i].get(bean));
			}
		} catch (IllegalAccessException t1) {
			log.error("Error retreiving value from field:" + fieldList[i].getName() + " for bean:" + bean, t1);
			throw t1;
		}
	}

	/**
	 * Generate XML and return generated xml
	 * 
	 * @return xml as string
	 */
	public String getXml() {
		if (activityList == null || activityList.isEmpty())
			return "";
		StringBuilder sb = new StringBuilder(500);
		sb.append(OPEN_TAG);
		int n = activityList.size();
		for (int i = 0; i < n; i++) {
			ActivitySingleData a = activityList.get(i);
			if (a.isXml()) {
				sb.append(a.getValue());
				continue;
			}
			if (!Utils.isPartProtecteFieldContent(a.getPropName())) {
				sb.append("<").append(a.getPropName()).append(">");
				sb.append(a.getValue() != null ? a.getValue() : "");
				sb.append("</").append(a.getPropName()).append(">");
			}
		}
		sb.append(CLOSE_TAG);
		return sb.toString();
	}

	public String getCustomXml() {
		StringBuilder sb = null;
		if (activityList != null && !activityList.isEmpty()) {
			sb = new StringBuilder(500);
			int n = activityList.size();
			for (int i = 0; i < n; i++) {
				ActivitySingleData a = activityList.get(i);
				if (a.isXml()) {
					sb.append(a.getValue());
				} else {
					sb.append("<").append(a.getPropName()).append(">");
					sb.append(a.getValue() != null ? a.getValue() : "");
					sb.append("</").append(a.getPropName()).append(">");
				}
			}
		}
		return sb != null ? sb.toString() : "";
	}

	/**
	 * @return Returns the includeParent.
	 */
	public boolean isIncludeParent() {
		return includeParent;
	}

	/**
	 * @param includeParent
	 *            The includeParent to set.
	 */
	public void setIncludeParent(boolean includeParent) {
		this.includeParent = includeParent;
	}

	public static ActivityUtil create() {
		return new ActivityUtil();
	}

	public boolean isIgnoreNull() {
		return ignoreNull;
	}

	public void setIgnoreNull(boolean ignoreNull) {
		this.ignoreNull = ignoreNull;
	}

}

/**
 * Single activity data
 * 
 * @author milan
 */
class ActivitySingleData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean xml = false;

	private String propName;

	private String value;

	/**
	 * Constructor
	 * 
	 * @param propName
	 * @param value
	 */
	public ActivitySingleData(String propName, String value) {
		this.propName = propName;
		this.value = value;
	}

	public ActivitySingleData(String xml) {
		this.xml = true;
		this.value = xml;
	}

	/**
	 * @return Returns the propName.
	 */
	public String getPropName() {
		return propName;
	}

	/**
	 * @param propName
	 *            The propName to set.
	 */
	public void setPropName(String propName) {
		this.propName = propName;
	}

	/**
	 * @return Returns the value.
	 */
	public String getValue() {
		return value;
	}

	public boolean isXml() {
		return xml;
	}

	/**
	 * @param value
	 *            The value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
