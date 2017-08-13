package rs.milanmitic.master.common;

import java.io.File;
import java.util.List;

import org.apache.commons.collections.map.LinkedMap;

/**
 * Application constants
 * 
 * @author milan
 * 
 */
public class Constants {

	public static final Short BANK_DEFAULT_CURRENCY_NUM = 978;

	public static final Integer STATUS_ACTIVE = Integer.valueOf(1);
	public static final Integer STATUS_NOT_ACTIVE = Integer.valueOf(2);
	public static final Integer STATUS_BLOCKED = Integer.valueOf(3);

	public static final String THREAD_REQUEST = "_threadReq";

	public static final String SESSION_LAST_ACCESS = "SESS_LAST_ACCESS_TIME";
	public static final String REDIRECT_URL_PREFIX = "_redirectUrlPref";
	public static final String RUNTIME_REQUEST = "_runtimeReq";
	public static final String JOB_KEY = "_jobKey";
	public static final Integer AJAX_ITEM_NUMBER = 15;

	/**
	 * Should by default search filter should show in OPEN or CLOSED state
	 */
	public static final boolean SEARCH_FILTER_OPEN_DEFAULT = true;
	/**
	 * List of characters that can not be used in password
	 */
	public static final String PSWD_SPEC_CHAR_NOTVALID = "~`!@#$%^-_=+|\\:'\",./;";

	public static final String DEFAULT_THEME = "white";

	public static final String ERROR_COUNTER = "_errCount";
	public static final String CURRENT_URL = "_currUrl";
	public static final String LOGGED_USER = "loggedUser";
	public static final String AUDIT_LOG = "_auditLog";
	public static final String EVENT_LOG = "_eventLog";
	public static final String CURRENT_THEME = "mastertheme";

	public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	public static final String YES = "Y";
	public static final String NO = "N";

	public static final String ACTION = "_action";
	public static final String ACTION_ADD = "add";

	public static final String SECURE_FIELD = "_master_sec_f";
	public static final Integer ITEMS_PER_PAGE = 20;
	public static final String THEME_NAME = "white";
	/**
	 * Is application in DEBUG MODE or not
	 */
	private static boolean debugMode = false;

	private static String applicationPath;

	private static volatile LinkedMap xmlMenuDisplayers = null;
	/**
	 * Should we log to database method that name start with get<>
	 */
	public static boolean LOG_TO_EVENTLOG_GET_METHOD = false;
	/**
	 * Should we log to database method that name start with validate<>
	 */
	public static boolean LOG_TO_EVENTLOG_VALIDATE_METHOD = false;

	public static boolean TRACE_METHOD = true;

	public static Integer WEB_SECURITY_SESSION_TIMEOUT_SEC = 1800;// 30 minutes

	public static int TEXT_MAXIMUM_LENGTH = 40;

	public static final boolean DEVELOPMENT_MODE = true;
	public static final String DEFAULT_LANGUAGE_VALUE = "sr_RS";

	private static boolean shutDownInProgress = false;

	private static final Object[] EMPTY_OBJECT_ARRAY = null;
	private static final List<File> EMPTY_FILE_LIST = null;
	private static final File[] EMPTY_FILE_ARRAY = null;
	private static final List<String> EMPTY_STRING_LIST = null;
	private static final String[] EMPTY_STRING_ARRAY = null;
	private static final byte[] EMPTY_BYTE_ARRAY = null;

	public static final String LABEL_MESSAGE_RECORD_UPDATE = "message.recordUpdate";
	public static final String LABEL_MESSAGE_RECORD_INSERT = "message.recordInsert";
	public static final String LABEL_MESSAGE_RECORD_NOT_FOUND = "error.recordNotFound1";

	public static final String LABEL_ERROR_REQUIRED = "error.required";
	private static String serverId;

	private static boolean webSecurityUseHttpOnly = true;
	private static boolean webSecurityUseSecureCookie = false;

	private Constants() {
		super();

	}

	public static String getServerId() {
		return serverId;
	}

	public static void setServerId(String serverId) {
		Constants.serverId = serverId;
	}

	public static Object[] getEmptyObjectArray() {
		return EMPTY_OBJECT_ARRAY;
	}

	public static List<File> getEmptyFileList() {
		return EMPTY_FILE_LIST;
	}

	public static File[] getEmptyFileArray() {
		return EMPTY_FILE_ARRAY;
	}

	public static List<String> getEmptyStringList() {
		return EMPTY_STRING_LIST;
	}

	public static String[] getEmptyStringArray() {
		return EMPTY_STRING_ARRAY;
	}

	public static byte[] getEmptyByteArray() {
		return EMPTY_BYTE_ARRAY;
	}

	public static boolean isShutDownInProgress() {
		return shutDownInProgress;
	}

	public static void setShutDownInProgress(boolean shutDownInProgress) {
		Constants.shutDownInProgress = shutDownInProgress;
	}

	public static boolean isWebSecurityUseHttpOnly() {
		return webSecurityUseHttpOnly;
	}

	public static boolean isWebSecurityUseSecureCookie() {
		return webSecurityUseSecureCookie;
	}

	public static LinkedMap getXmlMenuDisplayers() {
		return xmlMenuDisplayers;
	}

	public static void setXmlMenuDisplayers(LinkedMap xmlMenuDisplayers) {
		Constants.xmlMenuDisplayers = xmlMenuDisplayers;
	}

	public static boolean isDebugMode() {
		return debugMode;
	}

	public static void setDebugMode(boolean debugMode) {
		Constants.debugMode = debugMode;
	}

	public static String getApplicationPath() {
		return applicationPath;
	}

	public static void setApplicationPath(String applicationPath) {
		Constants.applicationPath = applicationPath;
	}

	public static Integer getStatusActive() {
		return STATUS_ACTIVE;
	}

	public static Integer getStatusPassive() {
		return STATUS_NOT_ACTIVE;
	}

}
