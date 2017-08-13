package rs.milanmitic.master.common.util;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.persistence.EmbeddedId;
import javax.persistence.ManyToOne;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Digits;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import rs.milanmitic.master.common.Constants;
import rs.milanmitic.master.common.ContextHolder;
import rs.milanmitic.master.common.SelectFieldIntf;
import rs.milanmitic.master.common.data.FormatPatterns;
import rs.milanmitic.master.common.data.LabelValue;
import rs.milanmitic.master.common.data.LoggedUserData;
import rs.milanmitic.master.common.exception.UnpredictableException;
import rs.milanmitic.master.common.protector.HiddenFieldsSecureInterface;
import rs.milanmitic.master.filter.SecurityFilter;

/**
 * Common methods used in application
 * 
 * @author milan
 * 
 */
public class Utils {

	private static Logger log = LogManager.getLogger(Utils.class);
	private static final String DD_MM_YYYY_DOT = "dd.MM.yyyy";
	private static final String HH_MM_A = "hh:mm a";
	private static final String HH_MM = "HH:mm";
	private static final String DD_MM_YYYY = "dd/MM/yyyy";
	private static final String YYYY_MM_DD = "yyyy/MM/dd";
	private static final String MM_DD_YYYY = "MM/dd/yyyy";
	private static final Random RAND = new Random();

	private static final Set<String> protectedFieldContentList;

	static {

		// MUST BE IN LOWER CASE
		protectedFieldContentList = new HashSet<String>();
		protectedFieldContentList.add("empty");
		protectedFieldContentList.add("password");
		protectedFieldContentList.add("repeatPassword");
		protectedFieldContentList.add("currentPassword");
		protectedFieldContentList.add("newpasswordrepeat");
		protectedFieldContentList.add("passwordHash");
	}

	/**
	 * Check is filter in open or closed state
	 * 
	 * @param map
	 * @param filterName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean isFilterOpen(Object map, String filterName) {
		Map<String, String> m = (Map<String, String>) map;
		String statusF = m != null ? m.get(filterName) : null;
		if (statusF == null)
			return Constants.SEARCH_FILTER_OPEN_DEFAULT;
		else
			return !"off".equals(statusF);
	}

	public static String substring(String s, Integer start, Integer end) {
		if (StringUtils.isBlank(s))
			return s;
		return s.substring(start, end);
	}

	public static String substring(String s, Integer start) {
		if (StringUtils.isBlank(s))
			return s;
		return s.substring(start);
	}

	public static boolean startsWith(String s, String prefix) {
		if (StringUtils.isBlank(s) || StringUtils.isBlank(prefix))
			return false;
		return s.startsWith(prefix);
	}

	/**
	 * Get decimal format based on user settings
	 * 
	 * @param request
	 * @return
	 */
	public static DecimalFormat getDecimalFormat(HttpServletRequest request) {
		return getDecimalFormat(request.getSession());
	}

	/**
	 * Get currency decimal format based on user settings
	 * 
	 * @param request
	 * @return
	 */
	public static DecimalFormat getCurrencyDecimalFormat(HttpServletRequest request) {
		return getCurrencyDecimalFormat(request.getSession());
	}

	public static LoggedUserData getLoggedUserData(HttpSession session) {
		return (LoggedUserData) session.getAttribute(Constants.LOGGED_USER);
	}

	/**
	 * Get format patterns based on user settings
	 * 
	 * @param session
	 * @return
	 */
	public static FormatPatterns getFormatPatterns(HttpSession session) {
		LoggedUserData ud = getLoggedUserData(session);
		return ud == null ? new FormatPatterns() : ud.getFormatPatterns();
	}

	/**
	 * Get decimal format based on user settings
	 * 
	 * @param session
	 * @return
	 */
	public static DecimalFormat getDecimalFormat(HttpSession session) {
		LoggedUserData ud = getLoggedUserData(session);
		return getDecimalFormat(ud == null ? new FormatPatterns() : ud.getFormatPatterns());
	}

	/**
	 * Get currency decimal format based on user settings
	 * 
	 * @param session
	 * @return
	 */
	public static DecimalFormat getCurrencyDecimalFormat(HttpSession session) {
		LoggedUserData ud = getLoggedUserData(session);
		return getCurrencyDecimalFormat(ud == null ? new FormatPatterns() : ud.getFormatPatterns());
	}

	/**
	 * Get decimal format based on user settings
	 * 
	 * @param mfp
	 * @return
	 */
	public static DecimalFormat getDecimalFormat(FormatPatterns mfp) {
		return getInternalDecimalFormat(mfp, false);
	}

	/**
	 * Get currency decimal format based on user settings
	 * 
	 * @param mfp
	 * @return
	 */
	public static DecimalFormat getCurrencyDecimalFormat(FormatPatterns mfp) {
		return getInternalDecimalFormat(mfp, true);
	}

	/**
	 * Get internal decimal format based on user settings
	 * 
	 * @param mfp
	 * @param isCurrencyFormat
	 * @return
	 */
	public static DecimalFormat getInternalDecimalFormat(FormatPatterns mfp, boolean isCurrencyFormat) {
		Integer minFd;
		Integer maxFd;
		if (isCurrencyFormat) {
			minFd = mfp.getMinimumCurrencyFractionDigits();
			maxFd = mfp.getMaximumCurrencyFractionDigits();
		} else {
			minFd = mfp.getMinimumFractionDigits();// (Integer)
			maxFd = mfp.getMaximumFractionDigits();// (Integer)
		}
		Character numberDecimalSeparatorChar = mfp.getNumberDecimalSeparatorChar();// (Character)
		Character numberTousandSeparatorChar = mfp.getNumberTousandSeparatorChar();// (Character)
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator(numberDecimalSeparatorChar != null ? numberDecimalSeparatorChar : '.');
		dfs.setGroupingSeparator(numberTousandSeparatorChar != null ? numberTousandSeparatorChar : ' ');
		DecimalFormat nf = new DecimalFormat();
		nf.setMinimumFractionDigits(minFd != null ? minFd : 2);
		nf.setMaximumFractionDigits(maxFd != null ? maxFd : 6);
		nf.setDecimalFormatSymbols(dfs);
		return nf;
	}

	/**
	 * Format number based on user settings
	 * 
	 * @param b
	 * @return
	 */
	public static String formatNumber(BigDecimal b) {
		if (b == null)
			return "";
		if (ContextHolder.getFormatPatterns() != null) {
			return ContextHolder.getFormatPatterns().getDecimalFormat().format(b);
		} else {
			FormatPatterns mfp = new FormatPatterns();
			return mfp.getDecimalFormat().format(b);
		}
	}

	/**
	 * Format number based on user settings
	 * 
	 * @param number
	 * @return
	 */
	public static String formatLongNumber(Object number) {
		if (number == null)
			return "";
		if (number instanceof String && StringUtils.isBlank((String) number))
			return "";
		try {
			DecimalFormat df;
			if (ContextHolder.getFormatPatterns() != null) {
				df = ContextHolder.getFormatPatterns().getDecimalFormat();
			} else {
				FormatPatterns mfp = new FormatPatterns();
				df = mfp.getDecimalFormat();
			}
			df.setMinimumFractionDigits(0);
			return df.format(Long.valueOf(number.toString()));
		} catch (Exception t) {
			log.debug("Error(ignored),formatLongNumber", t);
			return number.toString();
		}
	}

	public static String formatBigDecimalNumber(Object b) {
		if (b == null)
			return "";
		if (b instanceof String && StringUtils.isBlank((String) b))
			return "";
		try {
			DecimalFormat df;
			if (ContextHolder.getFormatPatterns() != null) {
				df = ContextHolder.getFormatPatterns().getDecimalFormat();
			} else {
				FormatPatterns mfp = new FormatPatterns();
				df = mfp.getDecimalFormat();
			}
			return df.format(new BigDecimal(b.toString()));
		} catch (Exception t) {

			log.debug("Error(ignored),formatBigDecimalNumber", t);
			return b.toString();
		}
	}

	/**
	 * Format date based on user settings
	 *
	 * @param b
	 * @return
	 */
	public static String formatDate(Date b) {
		if (b == null)
			return "";
		if (ContextHolder.getFormatPatterns() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(ContextHolder.getFormatPatterns().getDateOutputPattern());
			return sdf.format(b);
		} else {
			FormatPatterns mfp = new FormatPatterns();
			return new SimpleDateFormat(mfp.getDateOutputPattern()).format(b);
		}
	}

	public static String formatTime(Date b) {
		if (b == null)
			return "";
		if (ContextHolder.getFormatPatterns() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(ContextHolder.getFormatPatterns().getTimeOutputPattern());
			return sdf.format(b);
		} else {
			FormatPatterns mfp = new FormatPatterns();
			return new SimpleDateFormat(mfp.getTimeOutputPattern()).format(b);
		}
	}

	/**
	 * Format elapsed time from milisec to string format
	 * 
	 * @param diff
	 * @return N HH:MM.ss
	 */
	public static String formatTime(long diff) {
		if (diff < 1000) {
			return StringUtils.rightPad(diff + " msec", 8);
		}
		long time = diff / 1000;
		long diffSeconds = (int) (time % 60);
		long diffMinutes = (int) ((time % 3600) / 60);
		long diffDays = (int) (time / (24 * 3600));
		long diffHours = (int) (time / 3600) - diffDays * 24;

		String s = "";
		if (diffDays >= 1)
			s += diffDays + "d ";
		if (diffHours >= 1)
			s += (diffHours > 9 ? Long.toString(diffHours) : "0" + diffHours) + ":";
		else
			s += "00:";
		if (diffMinutes >= 1)
			s += (diffMinutes > 9 ? Long.toString(diffMinutes) : "0" + diffMinutes) + ".";
		else
			s += "00.";

		s += diffSeconds > 9 ? Long.toString(diffSeconds) : "0" + diffSeconds;

		return s;
	}

	/**
	 * Format date and time based on user settings
	 *
	 * @param b
	 * @return
	 */
	public static String formatDateAndTime(Date b) {
		if (b == null)
			return "";
		String format;
		if (ContextHolder.getFormatPatterns() != null) {
			format = ContextHolder.getFormatPatterns().getDateInputPattern() + " " + ContextHolder.getFormatPatterns().getTimeInputPattern();
		} else {
			FormatPatterns mfp = new FormatPatterns();
			format = mfp.getDateInputPattern() + " " + mfp.getTimeInputPattern();
		}
		return new SimpleDateFormat(format).format(b);
	}

	/**
	 * Check pattern
	 * 
	 * @param patternString
	 * @param inputString
	 * @return
	 */
	public static boolean checkPatternMatch(String patternString, String inputString) {
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(inputString);
		return matcher.matches();
	}

	/**
	 * Set min time
	 * 
	 * @param c
	 * @return Calendar with min time
	 */
	public static Calendar setMinTime(Calendar c) {
		if (c == null)
			return null;
		c.set(Calendar.HOUR_OF_DAY, c.getActualMinimum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getActualMinimum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getActualMinimum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getActualMinimum(Calendar.MILLISECOND));
		return c;
	}

	/**
	 * Set max time
	 * 
	 * @param c
	 * @return Calendar with max time
	 */
	public static Calendar setMaxTime(Calendar c) {
		if (c == null)
			return null;
		c.set(Calendar.HOUR_OF_DAY, c.getActualMaximum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getActualMaximum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getActualMaximum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getActualMaximum(Calendar.MILLISECOND));
		return c;
	}

	/**
	 * Set min time
	 * 
	 * @param c
	 * @return Calendar with min time
	 */
	public static java.util.Date setMinTime(java.util.Date c) {
		if (c == null)
			return null;
		Calendar c1 = Calendar.getInstance();
		c1.setTime(c);
		return setMinTime(c1).getTime();
	}

	/**
	 * Set max time
	 * 
	 * @param c
	 * @return Calendar with max time
	 */
	public static java.util.Date setMaxTime(java.util.Date c) {
		if (c == null)
			return null;
		Calendar c1 = Calendar.getInstance();
		c1.setTime(c);
		return setMaxTime(c1).getTime();
	}

	/**
	 * Set min time
	 * 
	 * @param c
	 * @return Calendar with min time
	 */
	public static java.sql.Timestamp setMinTime(java.sql.Timestamp c) {
		if (c == null)
			return null;
		Calendar c1 = Calendar.getInstance();
		c1.setTime(c);
		return new Timestamp(setMinTime(c1).getTimeInMillis());
	}

	/**
	 * Set max time
	 * 
	 * @param c
	 * @return Calendar with max time
	 */
	public static java.sql.Timestamp setMaxTime(java.sql.Timestamp c) {
		if (c == null)
			return null;
		Calendar c1 = Calendar.getInstance();
		c1.setTime(c);
		return new Timestamp(setMaxTime(c1).getTimeInMillis());
	}

	/**
	 * Find number of days between two dates
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static long daysBetween(Date startDate, Date endDate) {
		Calendar calFrom = Calendar.getInstance();
		calFrom.setTime(startDate);
		Calendar calTo = Calendar.getInstance();
		calTo.setTime(endDate);
		return daysBetween(calFrom, calTo);
	}

	/**
	 * Find number of days between two dates
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static Long daysBetween(Calendar startDate, Calendar endDate) {
		if (startDate == null || endDate == null)
			return null;
		Calendar date = (Calendar) startDate.clone();
		date.set(Calendar.HOUR_OF_DAY, 10);
		Calendar dateTo = (Calendar) endDate.clone();
		dateTo.set(Calendar.HOUR_OF_DAY, 9);
		long daysBetween = 0;
		while (date.before(dateTo)) {
			date.add(Calendar.DATE, 1);
			daysBetween++;
		}
		if (log.isDebugEnabled()) {
			SimpleDateFormat sdf = new SimpleDateFormat(Utils.DD_MM_YYYY_DOT);
			log.debug("Days between:" + sdf.format(startDate.getTime()) + " - " + sdf.format(endDate.getTime()) + "=" + daysBetween);
		}
		return daysBetween;
	}

	public static Long daysBetween1(Calendar day1, Calendar day2) {
		if (day1 == null || day2 == null)
			return null;
		Calendar dayOne = (Calendar) day1.clone();
		Calendar dayTwo = (Calendar) day2.clone();

		if (dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)) {
			return Long.valueOf(Math.abs(dayOne.get(Calendar.DAY_OF_YEAR) - dayTwo.get(Calendar.DAY_OF_YEAR)));
		} else {
			if (dayTwo.get(Calendar.YEAR) > dayOne.get(Calendar.YEAR)) {
				// swap them
				Calendar temp = dayOne;
				dayOne = dayTwo;
				dayTwo = temp;
			}
			int extraDays = 0;

			while (dayOne.get(Calendar.YEAR) > dayTwo.get(Calendar.YEAR)) {
				dayOne.add(Calendar.YEAR, -1);
				// getActualMaximum() important for leap years
				extraDays += dayOne.getActualMaximum(Calendar.DAY_OF_YEAR);
			}

			return Long.valueOf(extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOne.get(Calendar.DAY_OF_YEAR));
		}
	}

	/**
	 * Change language to supplied value
	 * 
	 * @param request
	 * @param response
	 * @param lang
	 */
	public static void changeLanguage(HttpServletRequest request, HttpServletResponse response, String lang) {
		log.debug("--->  changeLanguage :{}", lang);
		try {
			LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
			if (localeResolver != null) {
				LocaleEditor localeEditor = new LocaleEditor();
				localeEditor.setAsText(lang);
				Locale loc = (Locale) localeEditor.getValue();
				localeResolver.setLocale(request, response, loc);
				request.getSession().setAttribute("chooseLanguage", lang.contains("_") ? lang : lang + "_" + lang.toUpperCase());
				log.debug("--->  changeLanguage loc:{}", loc);

			}
		} catch (Exception t) {
			log.error("Error(ignored) during language change, lang:" + lang, t);
		}
	}

	/**
	 * Get stack trace of exception
	 * 
	 * @param aThrowable
	 * @return
	 */
	public static String getStackTrace(Throwable aThrowable) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}

	public static StringBuilder getMinimalStackTrace(Throwable t) {
		String s = Utils.getStackTrace(t);
		StringBuilder sb = new StringBuilder();
		String[] so = s.split("\n");
		for (String h : so) {
			String g = h.trim();
			final boolean ignoreString = g.startsWith("at org.springframework.") || g.startsWith("at sun.reflect.") || g.startsWith("at java.lang.reflect");
			if (ignoreString || g.startsWith("at javax.servlet.") || g.startsWith("at org.apache.") || g.startsWith("at java.util."))
				continue;
			sb.append(h);
		}
		return sb;
	}

	/**
	 * Get value from XML node
	 * 
	 * @param e
	 * @param nameOfTheNode
	 * @return
	 */
	public static String getXMLValue(Element e, String nameOfTheNode) {
		String result = null;

		NodeList list = e.getElementsByTagName(nameOfTheNode);
		if (list != null) {
			Node n = list.item(0);
			if (n != null && n.getFirstChild() != null)
				result = n.getFirstChild().getNodeValue();
		}
		return result != null ? result : "";
	}

	/**
	 * Check if string contains other string which is surrounded with dots.
	 * 
	 * @param s
	 * @param sList
	 * @return
	 */
	public static boolean isStringSurroundedWithDotsInList(String s, String sList) {
		if (StringUtils.isBlank(sList) || StringUtils.isBlank(s)) {
			return false;
		} else
			return sList.contains(".".concat(s).concat("."));
	}

	public static String generateSecureFieldsHash(HiddenFieldsSecureInterface obj) {
		try {
			String x = obj.getSecureFields();
			if (x != null) {

				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update(x.getBytes());
				return toHex(md.digest());
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Error securing hidden fields", e);
		}
	}

	public static String toHex(byte[] buffer) {
		StringBuilder sb = new StringBuilder(buffer.length * 2);
		int n = buffer.length;
		for (int i = 0; i < n; i++) {
			sb.append(Character.forDigit((buffer[i] & 0xf0) >> 4, 16));
			sb.append(Character.forDigit(buffer[i] & 0x0f, 16));
		}
		return sb.toString();
	}

	public static String generateSecureHiddenTag(java.lang.Object o) {
		if (o != null && o instanceof HiddenFieldsSecureInterface) {
			String ss = generateSecureFieldsHash((HiddenFieldsSecureInterface) o);
			StringBuilder s = new StringBuilder();
			s.append("<input type=\"hidden\" name=\"").append(Constants.SECURE_FIELD).append("\" value=\"").append(ss).append("\" /> ");
			return s.toString();
		}
		return "";
	}

	public static List<SelectFieldIntf> dateInputFormatList() {
		List<SelectFieldIntf> l = new ArrayList<SelectFieldIntf>();
		Date now = Calendar.getInstance().getTime();
		//
		l.add(new LabelValue((new SimpleDateFormat(Utils.DD_MM_YYYY)).format(now), Utils.DD_MM_YYYY));
		l.add(new LabelValue((new SimpleDateFormat(Utils.DD_MM_YYYY_DOT)).format(now), Utils.DD_MM_YYYY_DOT));
		l.add(new LabelValue((new SimpleDateFormat(Utils.MM_DD_YYYY)).format(now), Utils.MM_DD_YYYY));
		l.add(new LabelValue((new SimpleDateFormat(Utils.YYYY_MM_DD)).format(now), Utils.YYYY_MM_DD));

		return l;
	}

	public static List<SelectFieldIntf> dateOutputFormatList() {
		List<SelectFieldIntf> l = new ArrayList<SelectFieldIntf>();
		Date now = Calendar.getInstance().getTime();
		//
		l.add(new LabelValue((new SimpleDateFormat(Utils.DD_MM_YYYY)).format(now), Utils.DD_MM_YYYY));
		l.add(new LabelValue((new SimpleDateFormat(Utils.DD_MM_YYYY_DOT)).format(now), Utils.DD_MM_YYYY_DOT));
		l.add(new LabelValue((new SimpleDateFormat(Utils.MM_DD_YYYY)).format(now), Utils.MM_DD_YYYY));
		l.add(new LabelValue((new SimpleDateFormat(Utils.YYYY_MM_DD)).format(now), Utils.YYYY_MM_DD));

		l.add(new LabelValue((new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z")).format(now), "yyyy.MM.dd HH:mm:ss z"));
		l.add(new LabelValue((new SimpleDateFormat("EEE, MMM d, ''yy")).format(now), "EEE, MMM d, ''yy"));
		l.add(new LabelValue((new SimpleDateFormat("dd.MMMM.yyyy")).format(now), "dd.MMMM.yyyy"));

		return l;
	}

	public static List<SelectFieldIntf> timeOutputFormatList() {
		List<SelectFieldIntf> l = new ArrayList<SelectFieldIntf>();
		Date now = Calendar.getInstance().getTime();
		//
		l.add(new LabelValue((new SimpleDateFormat(Utils.HH_MM_A)).format(now), Utils.HH_MM_A));
		l.add(new LabelValue((new SimpleDateFormat(Utils.HH_MM)).format(now), Utils.HH_MM));

		return l;
	}

	public static List<SelectFieldIntf> timeInputFormatList() {
		List<SelectFieldIntf> l = new ArrayList<SelectFieldIntf>();
		Date now = Calendar.getInstance().getTime();
		//
		l.add(new LabelValue((new SimpleDateFormat(Utils.HH_MM_A)).format(now), Utils.HH_MM_A));
		l.add(new LabelValue((new SimpleDateFormat(Utils.HH_MM)).format(now), Utils.HH_MM));

		return l;
	}

	public static List<SelectFieldIntf> decimalSeparatorList() {
		List<SelectFieldIntf> l = new ArrayList<SelectFieldIntf>();
		//
		l.add(new LabelValue(".", "."));
		l.add(new LabelValue(",", ","));

		return l;
	}

	public static List<SelectFieldIntf> tousandSeparatorList() {
		List<SelectFieldIntf> l = new ArrayList<SelectFieldIntf>();
		//
		l.add(new LabelValue(",", ","));
		l.add(new LabelValue(".", "."));
		l.add(new LabelValue("'", "'"));
		l.add(new LabelValue(" ", " "));

		return l;
	}

	public static List<SelectFieldIntf> itemsPerPageList() {
		List<SelectFieldIntf> l = new ArrayList<SelectFieldIntf>();
		//
		l.add(new LabelValue("10", "10"));
		l.add(new LabelValue("20", "20"));
		l.add(new LabelValue("30", "30"));
		l.add(new LabelValue("40", "40"));
		l.add(new LabelValue("50", "50"));
		l.add(new LabelValue("60", "60"));
		l.add(new LabelValue("70", "70"));
		l.add(new LabelValue("80", "80"));
		return l;
	}

	public static List<SelectFieldIntf> themeNameList() {
		List<SelectFieldIntf> l = new ArrayList<SelectFieldIntf>();
		//
		l.add(new LabelValue("#label.theme.white", "white"));
		l.add(new LabelValue("#label.theme.black", "black"));
		return l;
	}

	/**
	 * Trim all fields of some bean, that are String type
	 * 
	 * @param bean
	 */
	public static void trimAllStrings(Object bean) {
		if (bean == null)
			return;
		Field[] ff = bean.getClass().getDeclaredFields();
		if (ff != null && ff.length > 0) {
			for (Field field : ff) {
				// skip STATIC, SKIP properties that are not string
				if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) || !field.getType().equals(String.class)) {
					continue;
				}
				if (field.isAnnotationPresent(EmbeddedId.class))
					trimEmbeddedAnnotation(bean, field);
				else
					trimField(bean, field);
			}
		}

	}

	private static void trimField(Object bean, Field field) {
		try {
			String value;
			boolean accessible = field.isAccessible();
			if (!accessible)
				field.setAccessible(true);
			value = (String) field.get(bean);
			if (value != null) {
				String newValue = value.trim();
				if (value.equals(newValue))
					field.set(bean, newValue);
			}

			if (!accessible)
				field.setAccessible(false);
		} catch (Exception t) {
			log.error("Error(trimAllStrings), ignored, bean:" + bean, t);
		}
	}

	private static void trimEmbeddedAnnotation(Object bean, Field f) {
		try {
			f.setAccessible(true);
			Object value = f.get(bean);
			trimAllStrings(value);
			f.setAccessible(false);
		} catch (Exception t) {
			log.error("Error1(trimAllStrings), ignored", t);
		}
	}

	public static List<String> listCommonTokens(List<String> tokens1, List<String> tokens2) {

		if ((tokens1 == null) || (tokens2 == null))
			return new ArrayList<String>();

		if (tokens1.isEmpty() || tokens2.isEmpty())
			return new ArrayList<String>();

		List<String> commonTokens = new ArrayList<String>();
		for (int i = 0; i < tokens1.size(); i++) {
			String token1 = tokens1.get(i);
			for (int j = 0; j < tokens2.size(); j++) {
				String token2 = tokens2.get(j);
				if (token1.equals(token2)) {
					commonTokens.add(token1);
				}
			}

		}
		return commonTokens;
	}

	public String removeDots(String s) {
		return s == null ? s : s.replace(".", "");
	}

	public static String removeAllSpaces(String s) {
		return s == null ? s : s.replaceAll("\\s+", "");
	}

	public static boolean isSpringErrMsg(Object o) {
		if (o == null)
			return false;

		return o instanceof FieldError;
	}

	public static String getStringFromXml(String xml) {
		if (StringUtils.isBlank(xml))
			return "";

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));

			Document doc = db.parse(is);

			StringBuilder sb = new StringBuilder("<ul>");
			appendChildNodes(doc.getDocumentElement(), sb);
			sb.append("</ul>");

			return sb.toString();
		} catch (Exception e) {
			log.error("Error(ignored), input param xml:" + xml, e);
			return "";
		}
	}

	private static void appendChildNodes(Node currentNode, StringBuilder sb) {
		NodeList nodeList = currentNode.getChildNodes();
		int n = nodeList.getLength();
		for (int i = 0; i < n; i++) {
			Node childNode = nodeList.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				appendChildNodes(childNode, sb);
			} else {
				// calls this method for all the children which is Element
				sb.append("<li style='float:left; width:50%;'>").append(childNode.getParentNode().getNodeName()).append(": ").append(childNode.getNodeValue()).append("</li>");
			}
		}
	}

	public static boolean isTimeNotValid(String time1) {
		if (StringUtils.isBlank(time1) || ":".equals(time1.trim()))
			return true;
		String time = time1.toLowerCase().trim();
		boolean isam = false;
		boolean ispm = false;
		if (time.indexOf("am") != -1) {
			time = StringUtils.replace(time, "am", "").trim();
			isam = true;
		}
		if (time.indexOf("pm") != -1) {
			time = StringUtils.replace(time, "pm", "").trim();
			ispm = true;
		}

		String h;
		String min = null;
		if (time.indexOf(':') != -1) {
			StringTokenizer st = new StringTokenizer(time, ":");
			h = st.nextToken().trim();
			min = st.nextToken().trim();
		} else {
			h = time;
		}
		boolean result = true;
		try {
			if ("00".equals(h))
				h = "0";
			if ("00".equals(min))
				min = "0";

			result = checkHourAmPm(isam, ispm, h, min, result);
		} catch (Exception t) {

			log.debug("Error(ignored),isTimeNotValid", t);
			return false;
		}
		return result;
	}

	private static boolean checkHourAmPm(boolean isAM, boolean isPM, String h, String min, boolean result) {
		if (StringUtils.isNotBlank(h)) {
			int temp = Integer.parseInt(h);
			if (isAM || isPM) {
				if (!(temp > 1 && temp <= 12))
					result = false;

			} else {
				if (temp < 0 || temp > 23)
					result = false;
			}
		}
		if (result && StringUtils.isNotBlank(min)) {
			int temp = Integer.parseInt(min);
			if (temp < 0 || temp > 59)
				result = false;

		}
		return result;
	}

	public static Date setTime(Date d, String time) {
		if (d == null || StringUtils.isBlank(time) || ":".equals(time.trim()))
			return d;
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		return setTime(cal, time).getTime();

	}

	public static Timestamp setTime(Timestamp d, String time) {
		if (d == null || StringUtils.isBlank(time) || ":".equals(time.trim()))
			return d;
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		return new Timestamp(setTime(cal, time).getTimeInMillis());
	}

	public static Calendar setTime(Calendar cal, String time1) {
		if (cal == null || StringUtils.isBlank(time1) || ":".equals(time1.trim()))
			return cal;
		String timeValue = time1.trim().toLowerCase();
		boolean isAM = false;
		boolean isPM = false;
		if (timeValue.indexOf("am") != -1) {
			timeValue = StringUtils.replace(timeValue, "am", "").trim();
			isAM = true;
		}
		if (timeValue.indexOf("pm") != -1) {
			timeValue = StringUtils.replace(timeValue, "pm", "").trim();
			isPM = true;
		}

		String h;
		String min = null;
		if (timeValue.indexOf(':') != -1) {
			StringTokenizer st = new StringTokenizer(timeValue, ":");
			h = st.nextToken().trim();
			min = st.nextToken().trim();
		} else {
			h = timeValue;
		}
		if ("00".equals(h))
			h = "0";
		if ("00".equals(min))
			min = "0";

		checkAndSetHourOfDay(cal, isAM, isPM, h);
		if (StringUtils.isNotBlank(min))
			cal.set(Calendar.MINUTE, Integer.parseInt(min));
		return cal;

	}

	private static void checkAndSetHourOfDay(Calendar cal, boolean isAM, boolean isPM, String h) {
		if (StringUtils.isNotBlank(h)) {
			int hi = Integer.parseInt(h);
			if (isAM && hi == 12)
				hi = 0;
			if (isPM && hi != 12)
				hi = hi + 12;

			cal.set(Calendar.HOUR_OF_DAY, hi);
		}
	}

	/**
	 * Check is message exist
	 * 
	 * @param msg
	 * @return
	 */
	public static boolean isMessageExist(String msg) {
		if (msg == null)
			return false;
		WebApplicationContext webAppContext = ContextLoader.getCurrentWebApplicationContext();
		Locale locale = LocaleContextHolder.getLocale();
		String s = webAppContext.getMessage(msg, null, locale);
		return !msg.equals(s);
	}

	/**
	 * Check does first date is before second one
	 * 
	 * @param dateBefore
	 * @param dateAfter
	 * @returnO
	 */
	public static boolean isDateBefore(Date dateBefore, Date dateAfter) {
		if (dateAfter == null || dateBefore == null)
			return false;

		return dateBefore.before(dateAfter);
	}

	/**
	 * Check is value valid number
	 * 
	 * @param dateBefore
	 * @param dateAfter
	 * @return
	 */
	public static boolean isValidNumber(String value) {
		if (StringUtils.isBlank(value))
			return false;

		try {
			BigDecimal v = new BigDecimal(value);
			return v != null;
		} catch (Exception t) {

			log.debug("Error(ignored),isValidNumber", t);
			return false;
		}
	}

	public static boolean isErrorMessageTypeMismatch(Object error) {
		if (error instanceof BindStatus) {
			BindStatus b = (BindStatus) error;
			return "typeMismatch".equals(b.getErrorCode());
		}
		return false;
	}

	public static Object getFormattedTypeMismatchErrorMessage(Object error) {
		if (!(error instanceof BindStatus))
			return error;
		BindStatus b = (BindStatus) error;
		if ("typeMismatch".equals(b.getErrorCode())) {
			if (isInteger(b)) {
				WebApplicationContext webAppContext = ContextLoader.getCurrentWebApplicationContext();
				Locale locale = LocaleContextHolder.getLocale();
				return webAppContext.getMessage("typeMismatch.integerNumber", new Object[] { b.getValue() != null ? b.getValue() : "" }, locale);

			} else if (isDouble(b)) {
				WebApplicationContext webAppContext = ContextLoader.getCurrentWebApplicationContext();
				Locale locale = LocaleContextHolder.getLocale();
				return webAppContext.getMessage("typeMismatch.decimalNumber", new Object[] { b.getValue() != null ? b.getValue() : "" }, locale);

			} else if (isDate(b)) {
				WebApplicationContext webAppContext = ContextLoader.getCurrentWebApplicationContext();
				Locale locale = LocaleContextHolder.getLocale();
				return webAppContext.getMessage("typeMismatch.date", new Object[] { b.getValue() != null ? b.getValue() : "", ContextHolder.getFormatPatterns().getDateInputPattern() }, locale);

			}
		}
		return b.getErrorMessage();
	}

	private static boolean isDate(BindStatus b) {
		return b.getValueType().equals(java.util.Date.class) || b.getValueType().equals(java.sql.Date.class) || b.getValueType().equals(java.sql.Timestamp.class);
	}

	private static boolean isDouble(BindStatus b) {
		return b.getValueType().equals(java.lang.Double.class) || b.getValueType().equals(java.lang.Float.class) || b.getValueType().equals(java.math.BigDecimal.class);
	}

	private static boolean isInteger(BindStatus b) {
		return b.getValueType().equals(java.lang.Long.class) || b.getValueType().equals(java.lang.Integer.class) || b.getValueType().equals(java.lang.Short.class);
	}

	public static String conditionalTitle(Object o) {
		if (o == null)
			return "";
		String s = o.toString();
		if (s.length() < Constants.TEXT_MAXIMUM_LENGTH)
			return "";
		int x = s.indexOf(' ');
		if (s.length() > 110 && (x == -1 || x > 110)) {
			s = s.substring(0, 110) + "<br/>" + s.substring(110);
		}
		x = s.indexOf(' ', x);
		if (s.length() > 225 && (x == -1 || x > 225)) {
			s = s.substring(0, 225) + "<br/>" + s.substring(225);
		}
		s = StringUtils.replace(s, "\"", "\\\"");
		s = StringEscapeUtils.escapeHtml(s);
		return s;
	}

	public static String conditionalTitleClass(Object o) {
		if (o == null)
			return "";
		String s = o.toString();
		if (s.length() < Constants.TEXT_MAXIMUM_LENGTH)
			return "";
		return " jqHelptooltip ";
	}

	public static String conditionalString(Object o) {
		if (o == null)
			return "";
		String s = o.toString();
		if (s.length() < Constants.TEXT_MAXIMUM_LENGTH)
			return s;
		s = s.substring(0, Constants.TEXT_MAXIMUM_LENGTH - 3) + "...";
		return s;
	}

	/**
	 * Only check validity of fields with Length annotations
	 * 
	 * @param bean
	 * @param result
	 * @throws IllegalAccessException
	 * @throws Throwable
	 */
	public static void checkAnnotationLength(Object bean, BindingResult result) throws IllegalAccessException {
		checkAnnotationLength(bean, result, null);

	}

	public static void checkAnnotationLength(Object bean, BindingResult result, String prefix) throws IllegalAccessException {
		if (bean == null)
			return;
		// check length
		Field[] fieldList = bean.getClass().getDeclaredFields();
		int ll = fieldList.length;
		for (int i = 0; i < ll; i++) {

			if (fieldList[i].isAnnotationPresent(EmbeddedId.class)) {
				if (!fieldList[i].isAccessible())
					fieldList[i].setAccessible(true);

				Object v = fieldList[i].get(bean);
				if (v != null)
					checkAnnotationLength(v, result, StringUtils.uncapitalize(bean.getClass().getSimpleName()));
				continue;
			}
			if (fieldList[i].isAnnotationPresent(Length.class)) {
				if (fieldList[i].isAnnotationPresent(Digits.class)) {
					checkDigitsAnnotaion(bean, result, prefix, fieldList, i);
				} else {
					Length an = fieldList[i].getAnnotation(Length.class);
					checkAnnotationLengthInternal(bean, result, prefix, fieldList, i, an);
				}
			}
		}
	}

	private static void checkAnnotationLengthInternal(Object bean, BindingResult result, String prefix, Field[] fieldList, int i, Length an) {
		try {
			if (!fieldList[i].isAccessible())
				fieldList[i].setAccessible(true);

			Object v = fieldList[i].get(bean);
			if (v == null)
				return;
			String g = v.toString().trim();
			int nn = g.length();
			String beanName = StringUtils.uncapitalize(bean.getClass().getSimpleName());

			if (nn > an.max()) {
				result.addError(new FieldError(prefix != null ? prefix : beanName, (prefix != null ? beanName + "." : "") + fieldList[i].getName(), g, true, new String[] { "Length.max" }, new Object[] { Integer.toString(an.max()) },
						"Length must be less than " + an.max()));

			} else if (an.min() == 1) {
				// ignore
			} else if (nn < an.min()) {
				result.addError(new FieldError(prefix != null ? prefix : beanName, (prefix != null ? beanName + "." : "") + fieldList[i].getName(), g, true, new String[] { "Length.min" }, new Object[] { Integer.toString(an.min()) },
						"Length must be more than " + an.min()));

			}
		} catch (Exception t1) {
			log.error("Error,checkAnnotationLength retreiving value from field:" + fieldList[i].getName() + " for bean :" + bean, t1);
			throw UnpredictableException.create("Error,checkAnnotationLength retreiving value from field:" + fieldList[i].getName() + " for bean:" + bean, t1);
		}
	}

	private static void checkDigitsAnnotaion(Object bean, BindingResult result, String prefix, Field[] fieldList, int i) throws IllegalAccessException {
		Digits an = fieldList[i].getAnnotation(Digits.class);

		if (!fieldList[i].isAccessible())
			fieldList[i].setAccessible(true);

		Object v = fieldList[i].get(bean);
		if (v != null) {
			String g = v.toString().trim();
			int nn = g.length();
			String beanName = StringUtils.uncapitalize(bean.getClass().getSimpleName());
			if (an.integer() < nn) {
				result.addError(new FieldError(prefix != null ? prefix : beanName, (prefix != null ? beanName + "." : "") + fieldList[i].getName(), g, true, new String[] { "Length.max" }, new Object[] { Integer.toString(an.integer()) },
						"Length must be less than " + an.integer()));
			}
		}
	}

	public static String maskPan(String cardnumber) {
		if (StringUtils.isBlank(cardnumber))
			return "";
		StringBuilder sb = new StringBuilder(cardnumber);
		for (int i = 4; i < sb.length() - 4; i++)
			sb.setCharAt(i, '*');
		return sb.toString();
	}

	public static String maskString(String unmaskedString, Integer maskFormat) {
		if (StringUtils.isBlank(unmaskedString) || maskFormat == null)
			return "";

		StringBuilder sb = new StringBuilder(unmaskedString);

		if (Integer.valueOf(1).equals(maskFormat) || Integer.valueOf(3).equals(maskFormat)) {
			return sb.toString();

		} else if (Integer.valueOf(2).equals(maskFormat)) {
			for (int i = 4; i < sb.length() - 4; i++)
				sb.setCharAt(i, '*');

			return sb.toString();

		} else {
			for (int i = 6; i < sb.length() - 4; i++)
				sb.setCharAt(i, '*');

			return sb.toString();

		}
	}

	public static boolean checkUrlRights(String url, LoggedUserData user) {
		if (user.getUrlList() != null && StringUtils.isNotBlank(url)) {
			return containsUrl(url, user.getUrlList());
		}
		return false;
	}

	/**
	 * Is URL in list of ignored URLs
	 * 
	 * @param url
	 * @return
	 */
	public static boolean containsUrl(String url, Set<String> urls) {
		for (String s : urls) {
			int x = s.lastIndexOf('*');
			if (x != -1) {
				String sub = s.substring(0, x - 1);
				if (url.startsWith(sub)) {
					return true;
				}
				// check if URL ends with / because it is same /app/bank/url1/ AND /app/bank/url1
				if (checkIsUrlEndsWithBackslash(sub, url))
					return true;

			} else {
				if (url.equals(s)) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean checkIsUrlEndsWithBackslash(String sub, String url) {
		if (sub.endsWith("/")) {
			sub = sub.substring(0, sub.length() - 1);
			if (url.startsWith(sub)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isUrlVisible(String url) {
		return ContextHolder.isSecurityEnabled() ? isInternalUrlVisible(url) : true;
	}

	public static boolean isUrlVisible(String url, String url1) {
		return ContextHolder.isSecurityEnabled() ? isInternalUrlVisible(new String[] { url, url1 }) : true;
	}

	public static boolean isUrlVisible(String url, String url1, String url2) {
		return ContextHolder.isSecurityEnabled() ? isInternalUrlVisible(new String[] { url, url1, url2 }) : true;
	}

	public static boolean isUrlVisible(String url, String url1, String url2, String url3) {
		return ContextHolder.isSecurityEnabled() ? isInternalUrlVisible(new String[] { url, url1, url2, url3 }) : true;
	}

	private static boolean isInternalUrlVisible(String... urls) {
		LoggedUserData loggedUser = ContextHolder.getLoggedUser();
		for (String path : urls) {
			if (Utils.containsUrl(path, SecurityFilter.getIgnoreUrls())) {
				return true;
			}
			if (Utils.checkUrlRights(path, loggedUser)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check is file IMAGE file
	 * 
	 * @param is
	 * @return
	 */
	public static boolean checkIsFileImage(InputStream is) {
		try {
			Image image = ImageIO.read(is);
			return image != null;
		} catch (IOException e) {

			log.debug("Error(ignored),checkIsFileImage", e);
		}
		return false;
	}

	public static boolean isLatinLetter(char c) {
		return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
	}

	public static boolean isProtecteFieldContent(String n) {
		return protectedFieldContentList.contains(n);
	}

	public static boolean isPartProtecteFieldContent(String n) {
		boolean found = false;
		for (String field : protectedFieldContentList) {
			if (n.contains(field)) {
				found = true;
				break;
			}
		}
		return found;
	}

	public static String toStringGlobal(Object bean) {
		StringBuilder builder = new StringBuilder();
		builder.append(bean.getClass().getSimpleName()).append(" [");

		Field[] fieldList = bean.getClass().getDeclaredFields();
		int ll = fieldList.length;
		for (int i = 0; i < ll; i++) {
			String n = fieldList[i].getName();
			if (isProtecteFieldContent(n.toLowerCase()) || n.toLowerCase().indexOf("password") != -1 || fieldList[i].getType() == null || java.lang.reflect.Modifier.isStatic(fieldList[i].getModifiers()))
				continue;

			toStringGlobalSingle(bean, builder, fieldList, i, n);
		}
		String s = builder.toString();
		if (s.endsWith(", "))
			builder = new StringBuilder(s.substring(0, s.length() - 2));
		builder.append("]");
		return builder.toString();

	}

	private static void toStringGlobalSingle(Object bean, StringBuilder builder, Field[] fieldList, int i, String n) {
		if (!fieldList[i].isAnnotationPresent(ManyToOne.class)) {
			try {
				if (!fieldList[i].isAccessible())
					fieldList[i].setAccessible(true);
				Object o = fieldList[i].get(bean);
				if (o != null && StringUtils.isNotBlank(o.toString())) {
					builder.append(n).append("=").append(o).append(", ");
				}

			} catch (Exception t1) {
				log.error("Error(ignored), Error retreiving value from field:" + fieldList[i].getName() + " for bean:" + bean.getClass().getName(), t1);
			}
		}
	}

	/**
	 * Check does exists class with specified name (full path)
	 * 
	 * @param mssgfrmtclastx
	 * @return
	 */
	public static boolean checkExistsClass(String mssgfrmtclastx) {
		if (StringUtils.isBlank(mssgfrmtclastx))
			return false;

		Object o = null;

		try {
			o = Class.forName(mssgfrmtclastx);
		} catch (Exception t) {

			log.debug("Error(ignored),checkExistsClass", t);
		}
		return o != null;
	}

	/**
	 * Check does specified URL and PORT and in valid format (URL:PORT)
	 * 
	 * @param urlPort
	 * @return
	 */
	public static boolean checkIsValidURLAndPort(String urlPort) {
		if (StringUtils.isBlank(urlPort))
			return false;

		try {
			UrlValidator url = new UrlValidator();
			return url.isValid(urlPort);

		} catch (Exception t) {

			log.debug("Error(ignored),checkIsValidURLAndPort", t);
			return false;
		}
	}

	/**
	 * Check does specified IP address is valid in both ipV4 and ipV6 version
	 * 
	 * @param ipAddress
	 * @return
	 */
	public static boolean checkIsValidIPAddress(String ipAddress) {
		if (StringUtils.isBlank(ipAddress))
			return false;

		String ipPattern = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})";
		String ipV6Pattern = "\\[([a-zA-Z0-9:]+)\\]";
		Pattern p = Pattern.compile(ipPattern);
		Matcher m = p.matcher(ipAddress);

		Pattern p1 = Pattern.compile(ipV6Pattern);
		Matcher m1 = p1.matcher(ipAddress);
		if (m.matches() || m1.matches()) {
			return true;
		}
		return false;
	}

	/**
	 * Check is specified PORT number valid
	 * 
	 * @param portNumber
	 * @return
	 */
	public static boolean checkIsValidPortNumber(String portNumber) {
		if (StringUtils.isBlank(portNumber))
			return false;

		try {
			Integer port = Integer.valueOf(portNumber);

			if (port < 0 || port > 65535)
				return false;

		} catch (NumberFormatException n) {
			return false;
		}
		return true;
	}

	/**
	 * Check is phone number valid or not
	 * 
	 * @param phoneNumber
	 * @return
	 */
	public static boolean isValidPhoneNumber(String phoneNumber) {
		String regex = "^\\+?[0-9. ()-]{6,25}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(phoneNumber);

		if (matcher.matches())
			return true;

		return false;
	}

	/**
	 * Resolve HTTPS url if exists in order to make create correct REDIRECT
	 * 
	 * @param request
	 * @return
	 */
	public static String getHttpsUrl(HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		String result = null;

		if (referer != null && referer.indexOf("https:") != -1) {
			int x = referer.indexOf("/app/");
			if (x == -1) {
				// should never happen
				result = referer;
			} else {
				result = referer.substring(0, x);
			}
		}
		return result;
	}

	/**
	 * Create relative url for HTTP or absolute URL for HTTPS requests
	 * 
	 * @param request
	 * @return
	 */
	public static String getHttpsOrHttpUrl(HttpServletRequest request, String relativeUrl) {
		String loadBalancerUrl = getHttpsUrl(request);

		log.debug("loadBalancerUrl:{}, relativeUrl:", loadBalancerUrl, relativeUrl);
		if (StringUtils.isNotBlank(loadBalancerUrl))
			relativeUrl = loadBalancerUrl + relativeUrl;
		else
			relativeUrl = request.getContextPath() + relativeUrl;

		log.debug("relativeUrl:{}", relativeUrl);
		return relativeUrl;
	}

	/**
	 * Check does some element exists in select box.
	 * 
	 * @param value
	 * @param list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean checkExistsValueInList(Object value, Object list) {

		List<SelectFieldIntf> listField = (List<SelectFieldIntf>) list;
		if (listField == null || listField.isEmpty())
			return false;

		for (SelectFieldIntf obj : listField) {
			if (obj.getValue().equals(value))
				return true;
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public static void checkExistsValueInList(Object value, Object list, HttpServletRequest request, BindingResult result, String field, MessageSource messageSource) {

		boolean found = false;
		List<SelectFieldIntf> listField = (List<SelectFieldIntf>) list;
		if (list == null)
			return;

		for (SelectFieldIntf obj : listField) {
			if ((obj.getValue() instanceof String ? obj.getValue() : obj.getValue().toString()).equals(value instanceof String ? value : (value != null ? value.toString() : null))) {
				found = true;
				break;
			}
		}
		if (found)
			return;
		MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);
		FieldError err = new FieldError(result.getObjectName(), field, messageSourceAccessor.getMessage("global.error.invalidValue"));
		List<Object> l = (List<Object>) request.getAttribute("appError");
		if (l == null) {
			l = new ArrayList<Object>();
		}
		if (!l.contains(err)) {
			l.add(err);
		}
		request.setAttribute("appError", l);
	}

	public static boolean hasRole(String... roles) {
		boolean result = false;
		for (GrantedAuthority authority : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
			String userRole = authority.getAuthority();
			for (String role : roles) {
				if (role.equals(userRole)) {
					result = true;
					break;
				}
			}

			if (result) {
				break;
			}
		}
		return result;
	}

	/**
	 * Check is specified email address valid
	 * 
	 * @param email
	 * @return
	 */
	public static boolean checkIsValidEmail(String email) {
		if (StringUtils.isBlank(email))
			return false;

		String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		Pattern p = Pattern.compile(emailPattern);
		Matcher m = p.matcher(email);

		if (m.matches()) {
			return true;
		}
		return false;
	}

	public static void writeMemoryInfo() {
		Runtime runtime = Runtime.getRuntime();

		NumberFormat format = NumberFormat.getInstance();

		StringBuilder sb = new StringBuilder();
		long maxMemory = runtime.maxMemory();
		long allocatedMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();

		sb.append("free memory: " + format.format(freeMemory / 1024) + "\n");
		sb.append("allocated memory: " + format.format(allocatedMemory / 1024) + "\n");
		sb.append("max memory: " + format.format(maxMemory / 1024) + "\n");
		sb.append("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024) + "\n");
		log.debug(sb);
	}

	public static Date setMinDate(Date d) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
		return calendar.getTime();
	}

	public static Calendar setMinDate(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
		return calendar;
	}

	public static Date setMaxDate(Date d) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
		return calendar.getTime();
	}

	public static Timestamp setMinDate(Timestamp d) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
		return new Timestamp(calendar.getTimeInMillis());
	}

	public static Timestamp setMaxDate(Timestamp d) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
		return new Timestamp(calendar.getTimeInMillis());
	}

	public static Timestamp setMinDateTimestamp(Date d) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
		return new Timestamp(calendar.getTimeInMillis());
	}

	public static Timestamp setMaxDateTimestamp(Date d) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
		return new Timestamp(calendar.getTimeInMillis());
	}

	public static Calendar setMinFirstDayOfMonth(Calendar cal) {
		cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DATE));
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));
		return cal;
	}

	public static Calendar setMaxLastDayOfMonth(Calendar cal) {
		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getActualMaximum(Calendar.MILLISECOND));
		return cal;
	}

	public static RoundingMode getRoundingMode() {
		return RoundingMode.HALF_UP;
	}

	@SuppressWarnings("rawtypes")
	public static Object getElement(java.util.List l, Integer x) {
		if (l == null)
			return null;
		return l.get(x);
	}

	public static int randInt(int min, int max) {
		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		return RAND.nextInt((max - min) + 1) + min;
	}

	public static double randDouble(double min, double max) {
		double range = max - min;
		double scaled = RAND.nextDouble() * range;
		return scaled + min;
	}

	public static boolean compareLong(Object o, Object o1) {
		if (o == null || o1 == null)
			return false;
		if (o instanceof String && StringUtils.isBlank((String) o))
			return false;
		if (o1 instanceof String && StringUtils.isBlank((String) o1))
			return false;
		o = StringUtils.replace(o.toString(), ",", "");
		o = StringUtils.replace(o.toString(), ".", "");
		o = StringUtils.replace(o.toString(), "'", "");

		o1 = StringUtils.replace(o1.toString(), ",", "");
		o1 = StringUtils.replace(o1.toString(), ".", "");
		o1 = StringUtils.replace(o1.toString(), "'", "");
		return Long.parseLong(o.toString()) < Long.parseLong(o1.toString());
	}

	public static boolean compareBigDecimal(Object o, Object o1) {
		if (o == null || o1 == null)
			return false;
		if (o instanceof String && StringUtils.isBlank((String) o))
			return false;
		if (o1 instanceof String && StringUtils.isBlank((String) o1))
			return false;
		BigDecimal b = new BigDecimal("" + o);
		BigDecimal b1 = new BigDecimal("" + o1);
		return b.doubleValue() < b1.doubleValue();
	}

	public static String getRandomHtmlRGBColor() {
		int r = (int) (Math.random() * 256);
		int g = (int) (Math.random() * 256);
		int b = (int) (Math.random() * 256);
		return "rgb(" + r + "," + g + "," + b + ")";

	}

	public static String prettyPrintXML(String xml) {
		try {
			final org.dom4j.Document document = DocumentHelper.parseText(xml);
			final OutputFormat format = OutputFormat.createPrettyPrint();
			StringWriter sw = new StringWriter();
			final XMLWriter writer = new XMLWriter(sw, format);
			writer.write(document);
			return sw.toString();
		} catch (Exception t) {
			log.debug("Error(ignored)", t);
			return xml;
		}

	}

	public static void close(InputStream is) {
		try {
			if (is != null)
				is.close();

		} catch (Exception t) {
			log.debug("Error(ignored) closing InputStream", t);
		}
	}

	public static void close(OutputStream is) {
		try {
			if (is != null)
				is.close();

		} catch (Exception t) {
			log.debug("Error(ignored) closing InputStream", t);
		}
	}

	/**
	 * Close reader and ignore error if occured
	 * 
	 * @param is
	 */
	public static void close(Reader is) {
		try {
			if (is != null) {
				is.close();
			}
		} catch (Exception t) {

			log.debug("Error, ignore, close Reader", t);

		}
	}

	/**
	 * Close writter and ignore error if occured
	 * 
	 * @param is
	 */
	public static void close(Writer is) {
		try {
			if (is != null) {
				is.close();
			}
		} catch (Exception t) {
			log.debug("Error, ignore, close Writer", t);

		}
	}

	public static String getMessage(String key, Object... params) {
		WebApplicationContext webAppContext = ContextLoader.getCurrentWebApplicationContext();
		MessageSource messageSource = webAppContext.getBean(MessageSource.class);
		MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);
		return getMessage(key, messageSourceAccessor, params);

	}

	/**
	 * Get message
	 * 
	 * @param key
	 * @param messageSourceAccessor
	 * @param params
	 * @return
	 */
	public static String getMessage(String key, MessageSourceAccessor messageSourceAccessor, Object... params) {
		String oneParam = null;
		try {
			Object[] newParams = null;
			if (params != null) {
				newParams = new Object[params.length];
				oneParam = getMessagePrepareParams(messageSourceAccessor, oneParam, newParams, params);
			}
			return messageSourceAccessor.getMessage(key, newParams);
		} catch (MissingResourceException e) {
			log.error("MissingResourceException, key=" + key + ", subkey=" + oneParam, e);
			return key;
		}
	}

	private static String getMessagePrepareParams(MessageSourceAccessor messageSourceAccessor, String oneParam, Object[] newParams, Object... params) {
		int i = 0;
		for (Object p : params) {
			if (p instanceof String) {
				oneParam = (String) p;
				if (oneParam.startsWith("#"))
					oneParam = getMessage(oneParam.substring(1), messageSourceAccessor);
				newParams[i] = oneParam;
			} else {
				newParams[i] = p;
			}
			i++;
		}
		return oneParam;
	}

	public static final boolean passwordContainsAlpha(String pwdStr) {
		char[] pwdCharArray = pwdStr.toCharArray();
		for (int i = 0; i < pwdStr.length(); i++) {
			if (Character.isLetter(pwdCharArray[i])) {
				return true;
			}
		}
		return false;
	}

	public static final boolean passwordContainsNumeric(String pwdStr) {
		char[] pwdCharArray = pwdStr.toCharArray();
		for (int i = 0; i < pwdStr.length(); i++) {
			if (Character.isDigit(pwdCharArray[i])) {
				return true;
			}
		}
		return false;
	}

	public static final boolean passwordContainsLowercase(String pwdStr) {
		char[] pwdCharArray = pwdStr.toCharArray();
		for (int i = 0; i < pwdStr.length(); i++) {
			if (Character.isLetter(pwdCharArray[i]) && Character.isLowerCase(pwdCharArray[i])) {
				return true;
			}
		}
		return false;
	}

	public static final boolean passwordContainsUppercase(String pwdStr) {
		char[] pwdCharArray = pwdStr.toCharArray();
		for (int i = 0; i < pwdStr.length(); i++) {
			if (Character.isLetter(pwdCharArray[i]) && Character.isUpperCase(pwdCharArray[i])) {
				return true;
			}
		}
		return false;
	}

	public static final boolean passwordContainsSpecial(String pwdStr, String specialStr) {
		if ("".equals(specialStr)) {
			return false;
		}
		char[] specialCharArray = specialStr.toCharArray();
		for (int i = 0; i < specialStr.length(); i++) {
			if (pwdStr.indexOf(specialCharArray[i]) != -1) {
				return true;
			}
		}
		return false;
	}

	public static void setShutDownInProgress() {
		Constants.setShutDownInProgress(true);
	}

	public static String getLastNChars(Object o, int n) {
		if (o == null)
			return "";
		String ss = o.toString();
		int xxx = ss.length();
		if (xxx > n)
			ss = ss.substring(xxx - (n - 1));
		return ss;
	}

	public static String getFileChecksum(String fileName) {
		return getFileChecksum(new File(fileName));
	}

	public static String getFileChecksum(File f) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);

			log.debug("File:{} check sum:{}", f.getAbsolutePath(), md5);
			return md5;
		} catch (Exception t) {
			log.error("Error(ignored) generating file checksum, file:" + f.getAbsolutePath(), t);
			return null;
		} finally {
			close(fis);
		}
	}

	public static void threadSleep(long x) {
		try {
			Thread.sleep(x);
		} catch (Exception t) {
			log.debug("Error(ignored)", t);
		}
	}

	/**
	 * Get value from XML node
	 * 
	 * @param e
	 * @param nameOfTheNode
	 * @return
	 */
	public static String getXMLValueAsFolder(Element e, String nameOfTheNode) {
		String result = getXMLValue(e, nameOfTheNode);
		if (StringUtils.isBlank(result))
			return null;

		if (!((result.endsWith("/") || result.endsWith("\\")))) {
			result += File.separator;
		}

		return result;
	}
}