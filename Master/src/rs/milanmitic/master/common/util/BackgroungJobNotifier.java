package rs.milanmitic.master.common.util;

import java.util.concurrent.ConcurrentHashMap;

import rs.milanmitic.master.common.ContextHolder;

/**
 * Background Job Notifier class
 *
 */
public class BackgroungJobNotifier {

	private static volatile ConcurrentHashMap<String, BackgroundJob> hash = new ConcurrentHashMap<String, BackgroundJob>();

	private BackgroungJobNotifier() {
		super();
	}

	public static void addJob(String key, String title, String sessionId) {
		BackgroundJob b = new BackgroundJob(sessionId);
		b.setJobTitle(title);
		b.setLoggedUser(ContextHolder.getLoggedUser());
		b.setStatusMessage("label.exportStarted");
		ContextHolder.setJobkey(key);
		hash.put(key, b);
	}

	public static BackgroundJob getJob(String key) {
		return hash.get(key);
	}

	public static BackgroundJob removeJob(String key) {
		return hash.remove(key);
	}

	public static void setStatusMsg(String key, String msg, Object... params) {
		BackgroundJob b = getJob(key);
		if (b != null)
			b.setStatusMessage(msg, params);
	}

	public static boolean isJobCanceled(String key) {
		BackgroundJob b = getJob(key);
		if (b != null)
			return b.isCancel();
		return false;
	}

}
