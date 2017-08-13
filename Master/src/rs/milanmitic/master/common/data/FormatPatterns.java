package rs.milanmitic.master.common.data;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.apache.commons.lang3.StringUtils;

import rs.milanmitic.master.common.Constants;
import rs.milanmitic.master.common.util.Utils;

/**
 * Format patterns used for single user
 * 
 * @author milan
 * 
 */
public class FormatPatterns implements Serializable {

	private static final long serialVersionUID = 1L;

	private String dateOutputPattern = "dd/MM/yyyy";

	private String timeOutputPattern = "HH:mm:ss";

	private String timeOutputPatternWithSec = null;

	private String dateInputPattern = "dd/MM/yyyy";

	private String timeInputPattern = "HH:mm";

	private Character numberDecimalSeparatorChar = '.';

	private Character numberTousandSeparatorChar = ' ';

	private Integer minimumFractionDigits = 2;

	private Integer maximumFractionDigits = 6;

	private Integer minimumCurrencyFractionDigits = 2;

	private Integer maximumCurrencyFractionDigits = 2;

	private Integer itemsPerPage = Constants.ITEMS_PER_PAGE;

	private String themeName = Constants.THEME_NAME;

	private String language = Constants.DEFAULT_LANGUAGE_VALUE;

	public FormatPatterns() {
		super();
	}

	public Character getNumberDecimalSeparatorChar() {
		return numberDecimalSeparatorChar;
	}

	public void setNumberDecimalSeparatorChar(Character numberDecimalSeparatorChar) {
		this.numberDecimalSeparatorChar = numberDecimalSeparatorChar;
	}

	public Character getNumberTousandSeparatorChar() {
		return numberTousandSeparatorChar;
	}

	public void setNumberTousandSeparatorChar(Character numberTousandSeparatorChar) {
		this.numberTousandSeparatorChar = numberTousandSeparatorChar;
	}

	public Integer getMinimumFractionDigits() {
		return minimumFractionDigits;
	}

	public void setMinimumFractionDigits(Integer minimumFractionDigits) {
		this.minimumFractionDigits = minimumFractionDigits;
	}

	public Integer getMinimumCurrencyFractionDigits() {
		return minimumCurrencyFractionDigits;
	}

	public void setMinimumCurrencyFractionDigits(Integer minimumCurrencyFractionDigits) {
		this.minimumCurrencyFractionDigits = minimumCurrencyFractionDigits;
	}

	public Integer getMaximumCurrencyFractionDigits() {
		return maximumCurrencyFractionDigits;
	}

	public void setMaximumCurrencyFractionDigits(Integer maximumCurrencyFractionDigits) {
		this.maximumCurrencyFractionDigits = maximumCurrencyFractionDigits;
	}

	public Integer getMaximumFractionDigits() {
		return maximumFractionDigits;
	}

	public void setMaximumFractionDigits(Integer maximumFractionDigits) {
		this.maximumFractionDigits = maximumFractionDigits;
	}

	public DecimalFormat getDecimalFormat() {
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator(numberDecimalSeparatorChar);
		dfs.setGroupingSeparator(numberTousandSeparatorChar);
		DecimalFormat nf = new DecimalFormat();
		nf.setMinimumFractionDigits(minimumFractionDigits);
		nf.setMaximumFractionDigits(maximumFractionDigits);
		nf.setDecimalFormatSymbols(dfs);
		return nf;
	}

	public String getDateInputPattern() {
		return dateInputPattern;
	}

	public void setDateInputPattern(String dateOutputPattern) {
		this.dateInputPattern = dateOutputPattern;
	}

	public String getTimeInputPattern() {
		return timeInputPattern;
	}

	public void setTimeInputPattern(String timeInputPattern) {
		this.timeInputPattern = timeInputPattern;
	}

	public String getDateOutputPattern() {
		return dateOutputPattern;
	}

	public void setDateOutputPattern(String dateOutputPattern) {
		this.dateOutputPattern = dateOutputPattern;
	}

	public String getTimeOutputPattern() {
		return timeOutputPattern;
	}

	public void setTimeOutputPattern(String timeOutputPattern) {
		this.timeOutputPattern = timeOutputPattern;
	}

	public Integer getItemsPerPage() {
		return itemsPerPage;
	}

	public void setItemsPerPage(Integer itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}

	public String getThemeName() {
		return themeName;
	}

	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}

	public String getTimeOutputPatternWithSec() {
		if (timeOutputPatternWithSec == null) {
			if (timeOutputPattern.indexOf("ss") == -1)
				timeOutputPatternWithSec = StringUtils.replace(timeOutputPattern, "mm", "mm:ss");
			else
				timeOutputPatternWithSec = timeOutputPattern;
		}
		return timeOutputPatternWithSec;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	@Override
	public String toString() {
		return Utils.toStringGlobal(this);
	}

}
