package rs.milanmitic.master.common;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.support.ServletContextResource;

import rs.milanmitic.master.common.data.FormatPatterns;
import rs.milanmitic.master.common.data.LoggedParticipantData;
import rs.milanmitic.master.common.data.LoggedUserData;
import rs.milanmitic.master.common.security.UserType;
import rs.milanmitic.master.common.util.BackgroungJobNotifier;
import rs.milanmitic.master.common.util.RequestUtil;
import rs.milanmitic.master.common.util.Utils;
import rs.milanmitic.master.model.ActivityLog;
import rs.milanmitic.master.model.AuditLog;

/**
 * Context holder
 * 
 * @author milan
 */
public class ContextHolder {

	private static final String KEY_SKSF = "_SKSF";
	private static final String LABEL_TIMESTARTED = "_TIMESTARTED";
	private static final String LABEL_DCC_HTTPS = "DCC_HTTPS";

	private static ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<Map<String, Object>>();

	protected static Logger log = LogManager.getLogger(ContextHolder.class);

	private static long errorCount = 0;
	private static final long SKIP_BAD_NUMBER = 665l + 1;
	
	private ContextHolder() {
		super();
	}

	/**
	 * Create context from supplied parameters
	 * 
	 * @param request
	 */
	public static void createContext(HttpServletRequest request) {
		threadLocal.set(new HashMap<String, Object>());
		threadLocal.get().put(LABEL_TIMESTARTED, Long.valueOf(System.currentTimeMillis()));
		if (request != null) {
			LoggedUserData user = (LoggedUserData) request.getSession().getAttribute(Constants.LOGGED_USER);
			if (user != null) {
				threadLocal.get().put(Constants.LOGGED_USER, user);
			}
			ServletContextResource resource = new ServletContextResource(request.getSession().getServletContext(), request.getRequestURI());
			String path = resource.getPath();
			path = StringUtils.remove(path, request.getContextPath());

			int l  =path.indexOf(';');
			if (l != -1) {
				path = path.substring(0, l);
			}
			String url = path + "|" + request.getMethod();
			threadLocal.get().put(Constants.CURRENT_URL, url);

			String isHTTPS = (String) request.getSession().getAttribute(ContextHolder.LABEL_DCC_HTTPS);
			threadLocal.get().put(ContextHolder.LABEL_DCC_HTTPS, Boolean.toString("true".equals(isHTTPS)));

			String httpsUrl = Utils.getHttpsUrl(request);
			boolean resolvedInSess = false;
			if (httpsUrl == null) {
				httpsUrl = (String) request.getSession().getAttribute(Constants.REDIRECT_URL_PREFIX);
				resolvedInSess = true;
			}

			if (httpsUrl != null) {
				threadLocal.get().put(Constants.REDIRECT_URL_PREFIX, httpsUrl);

				if (log.isDebugEnabled())
					log.debug("PUT IS HTTPS in CONTEXTHOLDER:" + isHTTPS + ", httpsUrl" + httpsUrl + ", resolvedInSess:" + resolvedInSess);
			}

		}
		ActivityLog al = createActivityLog();
		if (request != null)
			al.setUserIp(RequestUtil.getBrowserIp(request));

	}

	public static void createThreadContext(String threadName) {
		log.debug("ContextHolder createThreadContext threadName:{}", threadName);
		threadLocal.set(new HashMap<String, Object>());
		threadLocal.get().put(LABEL_TIMESTARTED, Long.valueOf(System.currentTimeMillis()));
		ActivityLog ev = new ActivityLog();
		ev.setDatetimeStart(new Timestamp(Calendar.getInstance().getTimeInMillis()));
		if (threadName.length() > ActivityLog.THREAD_NAME_MAX_LEN)
			threadName = threadName.substring(0, ActivityLog.THREAD_NAME_MAX_LEN - 1);
		ev.setThreadName(threadName);
		ev.setErrorNo(ActivityLog.DEFAULT_ERROR_NO);

		threadLocal.get().put(Constants.EVENT_LOG, ev);
		threadLocal.get().put(Constants.THREAD_REQUEST, "true");

	}

	public static boolean isHTTPS() {
		if (threadLocal == null || threadLocal.get() == null) {
			log.debug("ContextHolder isHTTPS is null");
			return false;
		}
		return "true".equals(threadLocal.get().get("DCC_HTTPS"));
	}

	public static boolean isContextCreated() {
		return threadLocal.get() != null;
	}

	public static AuditLog createUserTransactionLog(HttpServletRequest request) {
		AuditLog ev = new AuditLog();
		LoggedUserData user = (LoggedUserData) request.getSession().getAttribute(Constants.LOGGED_USER);
		if (user != null) {
			ev.setAppUserFk(user.getId());
			if (user.isUser())
				ev.setParticipantFk(((LoggedParticipantData) user).getParticipantId());
		}
		ev.setAuditStatus(AuditLog.TRANSSTATUS_SUCCESS);
		ev.setAuditTimestamp(new Timestamp(Calendar.getInstance().getTimeInMillis()));

		threadLocal.get().put(Constants.AUDIT_LOG, ev);
		return ev;
	}

	public static AuditLog createUserTransactionLog(AuditLog ev) {
		threadLocal.get().put(Constants.AUDIT_LOG, ev);
		return ev;
	}

	public static ActivityLog removeActivityLog() {
		if (threadLocal == null || threadLocal.get() == null) {
			if (log.isDebugEnabled())
				log.debug("ContextHolder removeErrorActivityLog is null");
			return null;
		}
		return (ActivityLog) threadLocal.get().remove(Constants.EVENT_LOG);
	}

	public static ActivityLog getActivityLog() {
		if (threadLocal == null || threadLocal.get() == null) {
			if (log.isDebugEnabled())
				log.debug("ContextHolder removeErrorActivityLog is null");
			return null;
		}
		return (ActivityLog) threadLocal.get().get(Constants.EVENT_LOG);
	}
	public static Long getTimeStartedMsec() {
		if (threadLocal == null || threadLocal.get() == null) {
			if (log.isDebugEnabled())
				log.debug("ContextHolder getTimeStartedMsec is null");
			return null;
		}
		return (Long) threadLocal.get().get(LABEL_TIMESTARTED);
	}

	public static ActivityLog createActivityLog() {
		if (threadLocal == null || threadLocal.get() == null) {
				log.debug("ContextHolder createActivityLog is null");

			ActivityLog ev = new ActivityLog();
			ev.setDatetimeStart(new Timestamp(Calendar.getInstance().getTimeInMillis()));
			ev.setThreadName(ActivityLog.DEFAULT_THREAD_NAME);
			ev.setErrorNo(ActivityLog.DEFAULT_ERROR_NO);

			return ev;
		}

		ActivityLog ev = (ActivityLog) threadLocal.get().get(Constants.EVENT_LOG);
		if (ev == null) {

			LoggedUserData loggedUser = getLoggedUser();

			// In case when user is not logged (for example error during login) we log error without user data
			ev = new ActivityLog();
			ev.setThreadName(ActivityLog.DEFAULT_THREAD_NAME);
			ev.setErrorNo(ActivityLog.DEFAULT_ERROR_NO);
			if (loggedUser != null) {
				ev.setAppUserFk(loggedUser.getId());
				if (loggedUser.isUser())
					ev.setParticipantFk(((LoggedParticipantData) loggedUser).getParticipantId());
			}
			ev.setDatetimeStart(new Timestamp(Calendar.getInstance().getTimeInMillis()));
			ev.setUrl(getURL());

			threadLocal.get().put(Constants.EVENT_LOG, ev);

		}
		return ev;
	}

	/**
	 * Clear context data
	 */
	public static void clearContext() {
		if (threadLocal == null)
			return;
		log.debug("ContextHolder clearContext (threadLocal.get() != null)={}", threadLocal.get() != null);
		if (threadLocal.get() != null)
			threadLocal.get().clear();
		threadLocal.set(null);
	}

	public static LoggedUserData getLoggedUser() {
		if (threadLocal == null || threadLocal.get() == null) {
			log.debug("ContextHolder getLoggedUser is null");
			return null;
		}
		return (LoggedUserData) threadLocal.get().get(Constants.LOGGED_USER);
	}

	public static LoggedParticipantData getLoggedParticipantData() {
		if (threadLocal == null || threadLocal.get() == null) {
			log.debug("ContextHolder getLoggedBankData is null");
			return null;
		}
		return (LoggedParticipantData) threadLocal.get().get(Constants.LOGGED_USER);
	}

	public static boolean isLoggedUserParticipant() {
		if (threadLocal == null || threadLocal.get() == null) {
			log.debug("ContextHolder isLoggedUserBank is null");
			return false;
		}
		LoggedUserData l = (LoggedUserData) threadLocal.get().get(Constants.LOGGED_USER);
		return l != null && l.isUser();
	}

	public static AuditLog getAuditLog() {
		if (threadLocal == null || threadLocal.get() == null) {
			log.debug("ContextHolder getAuditLog is null");
			return null;
		}
		return (AuditLog) threadLocal.get().get(Constants.AUDIT_LOG);
	}

	public static void removeAuditLog() {
		if (threadLocal == null || threadLocal.get() == null) {
			log.debug("ContextHolder removeAuditLog is null");
			return;
		}
		threadLocal.get().remove(Constants.AUDIT_LOG);
	}

	public static boolean isSecurityEnabled() {
		if (getLoggedUser() != null && UserType.USER.equals(getLoggedUser().getUserType())) {
			return true;
		}
		return false;
	}

	public static FormatPatterns getFormatPatterns() {
		if (threadLocal == null || threadLocal.get() == null)
			return new FormatPatterns();
		LoggedUserData ud = getLoggedUser();
		return ud != null ? ud.getFormatPatterns() : new FormatPatterns();
	}

	public static String getURL() {
		if (threadLocal == null || threadLocal.get() == null)
			return null;
		return (String) threadLocal.get().get(Constants.CURRENT_URL);
	}

	public static Long getErrorCount() {
		Long count = null;
		if (threadLocal != null && threadLocal.get() != null) {
			count = (Long) threadLocal.get().get(Constants.ERROR_COUNTER);
		}
		if (count == null) {
			if (errorCount == Long.MAX_VALUE)
				errorCount = 0;
			count = errorCount++;
			if (count == SKIP_BAD_NUMBER)
				count = errorCount++;

			if (threadLocal != null && threadLocal.get() != null)
				threadLocal.get().put(Constants.ERROR_COUNTER, count);
		}
		return count;
	}

	public static String getLoadBalancerUrl() {
		if (threadLocal == null || threadLocal.get() == null)
			return null;
		return (String) threadLocal.get().get(Constants.REDIRECT_URL_PREFIX);
	}

	public static void setRuntimeRequest() {
		if (threadLocal == null || threadLocal.get() == null)
			return;
		threadLocal.get().put(Constants.RUNTIME_REQUEST, "true");

	}

	public static boolean isRuntimeRequest() {
		if (threadLocal == null || threadLocal.get() == null)
			return false;
		return "true".equals(threadLocal.get().get(Constants.RUNTIME_REQUEST));

	}

	public static void setJobkey(String jobkey) {
		if (threadLocal == null || threadLocal.get() == null)
			return;
		threadLocal.get().put(Constants.JOB_KEY, jobkey);

	}

	public static boolean isJobCanceled() {
		if (threadLocal == null || threadLocal.get() == null)
			return false;
		String jobkey = (String) threadLocal.get().get(Constants.JOB_KEY);
		if (StringUtils.isNotBlank(jobkey)) {
			return BackgroungJobNotifier.isJobCanceled(jobkey);
		}
		return false;

	}

	public static void setJobStatusMsg(String msg, Object... params) {
		if (threadLocal == null || threadLocal.get() == null)
			return;
		String jobkey = (String) threadLocal.get().get(Constants.JOB_KEY);
		if (StringUtils.isNotBlank(jobkey)) {
			BackgroungJobNotifier.setStatusMsg(jobkey, msg, params);
		}

	}

	public static boolean isSkipStatementFactory() {
		if (threadLocal == null || threadLocal.get() == null)
			return false;
		return "1".equals(threadLocal.get().get(KEY_SKSF));
	}

	public static void setSkipStatementFactory() {
		if (threadLocal == null || threadLocal.get() == null)
			return;
		threadLocal.get().put(KEY_SKSF, "1");
	}

	public static void removeSkipStatementFactory() {
		if (threadLocal == null || threadLocal.get() == null)
			return;
		threadLocal.get().remove(KEY_SKSF);
	}

}
