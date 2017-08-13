package rs.milanmitic.master.common.exception;

import java.util.Arrays;

import rs.milanmitic.master.common.Constants;

/**
 * Handle validation/user friendly/ exceptions
 * 
 * @author milan
 * 
 */
public class ValidateException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String additionalField = null;

	private final String message;
	private final String field;
	private final transient Object[] params;

	public ValidateException(String message, Object... params) {
		this.message = message;
		this.params = params;
		this.field = null;
	}

	public ValidateException(String field, String message, Object... params) {
		this.message = message;
		this.params = params;
		this.field = field;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public Object[] getParams() {
		return params;
	}

	public String[] getParamsAsStrings() {
		if (params == null)
			return Constants.getEmptyStringArray();
		int n = params.length;
		String[] p = new String[n];
		for (int i = 0; i < n; i++) {
			p[i] = params[i] != null ? params[i].toString() : null;
		}
		return p;
	}

	public String getField() {
		return field;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ValidateException [message=").append(message).append(", params=").append(", field=").append(field).append(Arrays.toString(params)).append("]");
		return builder.toString();
	}

	/**
	 * @return the additionalField
	 */
	public String getAdditionalField() {
		return additionalField;
	}

	/**
	 * @param additionalField
	 *            the additionalField to set
	 */
	public void setAdditionalField(String additionalField) {
		this.additionalField = additionalField;
	}

}
