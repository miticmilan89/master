package rs.milanmitic.master.common.pagging;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import rs.milanmitic.master.common.ContextHolder;
import rs.milanmitic.master.common.SelectFieldIntf;

/**
 * Transfer search results from repository to controller
 * 
 * @author zd
 * 
 */
public class SearchResults extends SearchInput {
	/**
	 * Total number of results in DB
	 */
	private Long resultsCount;

	private Map<String, Object> additionalData = null;
	/**
	 * Results from DB
	 */
	private List<SearchInput> results;

	public SearchResults() {
		super();
	}

	public SearchResults(SearchInput si) {
		if (si != null) {
			this.setItemsPerPage(si.getItemsPerPageDef());
			this.setCurrentPage(si.getCurrentPage() != null ? si.getCurrentPage() : 1);
		}
	}

	public Long getResultsCount() {
		return resultsCount;
	}

	public void setResultsCount(Long resultsCount) {
		this.resultsCount = resultsCount;
	}

	public List<SearchInput> getResults() {
		return results;
	}
	@SuppressWarnings("unchecked")
	public <T extends SearchInput> List<T> getResults(Class<T> k) {
		if (k==null)
			throw new IllegalArgumentException("Class must be supplied");
		return (List<T>)results;
	}
	@SuppressWarnings("unchecked")
	public <T extends SelectFieldIntf> List<T> getResultsAsSelectField() {
		return (List<T>)results;
	}

	public void setResults(List<SearchInput> results) {
		this.results = results;
	}

	public Long getTotalPageNo() {
		Long totalPageNo;
		long resCount = getResultsCount() != null ? getResultsCount() : 1;
		long itPerPage = getItemsPerPageDef();
		if (resCount <= itPerPage)
			totalPageNo = 1l;
		else {
			if (resCount % itPerPage == 0)
				totalPageNo = resCount / itPerPage;
			else
				totalPageNo = resCount / itPerPage + 1;
		}
		return totalPageNo;
	}

	public void addAdditionalData(String key, Object value) {
		if (additionalData == null)
			additionalData = new HashMap<String, Object>();
		additionalData.put(key, value);
	}

	public Object getAdditionalData(String key) {
		return additionalData != null ? additionalData.get(key) : null;
	}

	public boolean isHasNextPage() {
		long curPage = getCurrentPage() != null ? getCurrentPage() : 1;
		return getTotalPageNo() > curPage;
	}

	public boolean isHasPrevPage() {
		long curPage = getCurrentPage() != null ? getCurrentPage() : 1;
		return curPage > 1;
	}

	public Long getDisplayPageFrom() {
		long curPage = getCurrentPage() != null ? getCurrentPage() : 1;
		long temp = curPage - getDefaultPageNumbersToSHowBefore();
		long check = getDefaultPageNumbersToSHowAfter() + getDefaultPageNumbersToSHowBefore() + 1;
		long x = getDisplayPageTo() - temp;
		if (x < check)
			temp -= (check - x);
		return temp > 0 ? temp : 1l;
	}

	public Long getDisplayPageTo() {
		long temp = getCurrentPage() != null && getCurrentPage() > getDefaultPageNumbersToSHowBefore() ? getCurrentPage() : getDefaultPageNumbersToSHowBefore();
		long displayPageTo = temp + 1 + getDefaultPageNumbersToSHowAfter();
		return displayPageTo <= getTotalPageNo() ? displayPageTo : getTotalPageNo();
	}

	public Long getStartNo() {
		long curPage = getCurrentPage() != null ? getCurrentPage() : 1;
		long itPerPage = getItemsPerPage() != null ? getItemsPerPage() : ContextHolder.getFormatPatterns().getItemsPerPage();
		return curPage == 1 ? 1 : (curPage - 1) * itPerPage + 1;
	}

	public Long getEndNo() {
		long curPage = getCurrentPage() != null ? getCurrentPage() : 1;
		long itPerPage = getItemsPerPage() != null ? getItemsPerPage() : ContextHolder.getFormatPatterns().getItemsPerPage();
		long temp = curPage * itPerPage;
		long rc = getResultsCount() != null ? getResultsCount() : 0;
		return temp > rc ? rc : temp;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("displayPageFrom", this.getDisplayPageFrom()).append("itemsPerPageDef", this.getItemsPerPageDef()).append("hasNextPage", this.isHasNextPage()).append("hasPrevPage", this.isHasPrevPage())
				.append("resultsCount", this.resultsCount).append("endNo", this.getEndNo()).append("results", this.results).append("startNo", this.getStartNo()).append("currentPage", this.getCurrentPage())
				.append("itemsPerPage", this.getItemsPerPage()).append("displayPageTo", this.getDisplayPageTo()).append("totalPageNo", this.getTotalPageNo()).append("firstPage", this.isFirstPage()).toString();
	}

	@Override
	public boolean isExecuteCount() {
		if (getResults() != null && isFirstPage() && (getResults().size() < getItemsPerPageDef())) {
			// no need for count
			this.setResultsCount(Long.valueOf(getResults().size()));
			return false;
		} else if (getItemsPerPage() != null && Integer.MAX_VALUE == getItemsPerPage().intValue()) {
			// no need for count
			this.setResultsCount(Long.valueOf(getResults() == null ? 0 : getResults().size()));
			return false;

		}
		return true;
	}

	public boolean isNotEmpty() {
		return !isEmpty();
	}

	public boolean isEmpty() {
		return getResults() == null || getResults().isEmpty();
	}
}
