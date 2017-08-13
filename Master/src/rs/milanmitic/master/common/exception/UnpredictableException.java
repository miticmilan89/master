package rs.milanmitic.master.common.exception;

import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import rs.milanmitic.master.common.util.Utils;

/**
 * Unpredictable exception
 * 
 * @author milan
 */
public class UnpredictableException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String SEP = "---------------------------------------------";
	private static final String LS = "\n";

	private Throwable exception = null;

	private String mess = null;

	private final Properties params;

	private int level = 0;

	private UnpredictableException() {
		params = new Properties();
	}

	/**
	 * Constructor
	 * 
	 * @param t
	 */
	public UnpredictableException(Throwable t) {
		this();
		this.exception = t;
		if (t != null && t instanceof UnpredictableException)
			this.level = ((UnpredictableException) t).getLevel() + 1;
	}

	/**
	 * Constructor
	 * 
	 * @param mess
	 */
	public UnpredictableException(String mess) {
		this();
		this.mess = mess;
	}

	/**
	 * Constructor
	 * 
	 * @param mess
	 * @param t
	 */
	public UnpredictableException(String mess, Throwable t) {
		this();
		this.exception = t;
		this.mess = mess;
		if (t != null && t instanceof UnpredictableException)
			this.level = ((UnpredictableException) t).getLevel() + 1;
	}

	private static String getLevelSpace(int level) {
		if (level == 0)
			return "";
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < level; i++)
			s.append("\t");
		return s.toString();
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append(LS).append("************* ERROR START ").append(level).append(" *************");
		sb.append(LS).append("Message:").append(mess != null ? mess : "none");

		if (params != null && !params.isEmpty()) {
			sb.append(LS).append(SEP);
			sb.append(LS).append("Parameters:");
			Enumeration<?> en = params.propertyNames();
			while (en.hasMoreElements()) {
				String s = (String) en.nextElement();
				sb.append(LS).append(" ").append(s).append(":").append(params.get(s));
			}
		}
		if (exception != null) {
			sb.append(LS).append(SEP);
			sb.append(LS).append("Error message: " + exception.getMessage());
			sb.append(LS).append(SEP);
			if (! (exception instanceof  UnpredictableException))
				sb.append(LS).append("Stack trace: " + Utils.getStackTrace(exception));
			else {
				String x = exception.toString();
				x = StringUtils.replace(x, "\n", "\n\t");
				sb.append(LS).append(x);
			}
		} else {
			sb.append(LS).append("Exception: none");
		}
		sb.append(LS).append("************* ERROR END ").append(level).append(" *************");
		return StringUtils.replace(sb.toString(), "\n", "\n" + getLevelSpace(level));
	}

	/**
	 * Get real exception
	 * 
	 * @return real exception
	 */
	public Throwable getException() {
		return exception;
	}

	/**
	 * Add some parameter name and parameter value to exception body
	 * 
	 * @param key
	 * @param value
	 * @return exception instance
	 */
	public UnpredictableException addParameter(String key, Object value) {
		if (value == null)
			params.put(key, "<null>");
		else
			params.put(key, value);

		return this;
	}

	/**
	 * Add some parameter name and parameter value to exception body
	 * 
	 * @param key
	 * @param value
	 * @return exception instance
	 */
	public UnpredictableException addParameter(String key, boolean value) {
		params.put(key, Boolean.toString(value));
		return this;
	}

	/**
	 * Add some parameter name and parameter value to exception body
	 * 
	 * @param key
	 * @param value
	 * @return exception instance
	 */
	public UnpredictableException addParameter(String key, int value) {
		params.put(key, value);
		return this;
	}

	/**
	 * Add some parameter name and parameter value to exception body
	 * 
	 * @param key
	 * @param value
	 * @return exception instance
	 */
	public UnpredictableException addParameter(String key, double value) {
		params.put(key, value);
		return this;
	}

	/**
	 * Create new exception
	 * 
	 * @param t
	 * @return exception instance
	 */
	public static UnpredictableException create(Throwable t) {
		return new UnpredictableException(t);
	}

	/**
	 * Create new exception
	 * 
	 * @param s
	 * @return exception instance
	 */
	public static UnpredictableException create(String s) {
		return new UnpredictableException(s);
	}

	/**
	 * Create new exception
	 * 
	 * @param t
	 * @param s
	 * @return exception instance
	 */
	public static UnpredictableException create(String s, Throwable t) {
		return new UnpredictableException(s, t);
	}

	public int getLevel() {
		return level;
	}
}
