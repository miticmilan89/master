package rs.milanmitic.master.common.pagging;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import rs.milanmitic.master.common.ContextHolder;

/**
 * Part of paging framework
 * 
 * @author zd
 * 
 */
public abstract class SearchInput implements Comparable<Object> {

	private static long defaultPageNumbersToSHowBefore = 4;
	private static long defaultPageNumbersToSHowAfter = 4;

	public static final String YES = "Y";

	protected HashMap<String, String> columnMapping = new HashMap<String, String>();
	/**
	 * Current page number
	 */
	private Integer currentPage;
	/**
	 * Items per page to show
	 */
	private Integer itemsPerPage;
	/**
	 * Order ASC or DESC
	 */
	private Boolean orderAsc;
	/**
	 * Column used for ordering data
	 */
	private String orderColumn;
	/**
	 * Restart pagging
	 */
	private Boolean restartPagging;
	/**
	 * Custom table prefix
	 */
	private String customTablePrefix;
	/**
	 * Execute count
	 */
	private boolean executeCount = true;
	/**
	 * Query Timeout seconds
	 */
	private Integer queryTimeoutSec = null;
	/**
	 * Case sensitive query "Y/N"
	 */
	private String caseSensitive;
	/**
	 * Use Like for strings "Y/N"
	 */
	private String stringLike;

	public Integer getCurrentPage() {
		if (restartPagging != null && restartPagging)
			return Integer.valueOf(1);
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getItemsPerPage() {
		return itemsPerPage;
	}

	public Integer getItemsPerPageDef() {
		return itemsPerPage != null ? itemsPerPage : ContextHolder.getFormatPatterns().getItemsPerPage();
	}

	public void setItemsPerPage(Integer itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}

	public void setReadAll() {
		this.itemsPerPage = Integer.MAX_VALUE;
	}

	public boolean isFirstPage() {
		return currentPage == null || currentPage == 1;
	}

	public String getColumnOrder() {
		if (orderAsc == null)
			orderAsc = Boolean.TRUE;
		return orderAsc ? "ASC" : "DESC";
	}

	public Boolean getOrderAsc() {
		return orderAsc;
	}

	public void setOrderAsc(Boolean orderAsc) {
		this.orderAsc = orderAsc;
	}

	public String getOrderColumn() {
		return orderColumn;
	}

	public String getOrderColumnName() {
		return orderColumn != null ? columnMapping.get(orderColumn) : null;
	}

	public String getOrderSQL() {
		String s = getOrderColumnName();
		if (StringUtils.isBlank(s))
			return "";
		if (customTablePrefix != null)
			s = StringUtils.replace(s, "a.", customTablePrefix + ".");
		if (s.trim().endsWith("DESC") || s.trim().endsWith("ASC"))
			return " ORDER BY ".concat(s);
		else
			return " ORDER BY ".concat(s).concat(" ").concat(getColumnOrder());
	}

	public void setOrderColumn(String orderColumn) {
		this.orderColumn = orderColumn;
	}

	protected void addColumnMapping(String key, String value) {
		this.columnMapping.put(key, value);
	}

	public Boolean getRestartPagging() {
		return restartPagging;
	}

	public void setRestartPagging(Boolean restartPagging) {
		this.restartPagging = restartPagging;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("itemsPerPageDef", this.getItemsPerPageDef()).append("orderColumn", this.orderColumn).append("orderAsc", this.orderAsc).append("currentPage", this.currentPage).append("itemsPerPage", this.itemsPerPage)
				.toString();
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	public Object getDummy() {
		return null;

	}

	public void setDummy(Object o) {

	}

	public boolean isExecuteCount() {
		return executeCount;
	}

	public void setExecuteCount(boolean executeCount) {
		this.executeCount = executeCount;
	}

	public String getCustomTablePrefix() {
		return customTablePrefix;
	}

	public void setCustomTablePrefix(String customTablePrefix) {
		this.customTablePrefix = customTablePrefix;
	}

	public Integer getQueryTimeoutSec() {
		return queryTimeoutSec;
	}

	public void setQueryTimeoutSec(Integer queryTimeoutSec) {
		this.queryTimeoutSec = queryTimeoutSec;
	}

	public String getCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(String caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public String getStringLike() {
		return stringLike;
	}

	public void setStringLike(String stringLike) {
		this.stringLike = stringLike;
	}

	public boolean isCaseSensitiveOn() {
		return YES.equals(caseSensitive);
	}

	public boolean isStringLikeOn() {
		return YES.equals(stringLike);
	}

	public boolean isGroup() {
		return false;
	}

	public static void setDefaultPageNumbersToSHowBefore(long defaultPageNumbersToSHowBefore) {
		SearchInput.defaultPageNumbersToSHowBefore = defaultPageNumbersToSHowBefore;
	}

	public static void setDefaultPageNumbersToSHowAfter(long defaultPageNumbersToSHowAfter) {
		SearchInput.defaultPageNumbersToSHowAfter = defaultPageNumbersToSHowAfter;
	}

	public static long getDefaultPageNumbersToSHowBefore() {
		return defaultPageNumbersToSHowBefore;
	}

	public static long getDefaultPageNumbersToSHowAfter() {
		return defaultPageNumbersToSHowAfter;
	}

}
