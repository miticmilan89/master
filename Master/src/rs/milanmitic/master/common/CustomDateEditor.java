package rs.milanmitic.master.common;

import java.beans.PropertyEditorSupport;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import org.springframework.util.StringUtils;

/**
 * PropertyEditor for java.util.Date, supporting a custom DateFormat.
 * 
 * <p>
 * This is not meant to be used as system PropertyEditor but rather as locale-specific date editor within custom controller code, to parse user-entered date strings into Date properties of beans, and render them in the UI form.
 * 
 * <p>
 * In web MVC code, this editor will typically be registered with <code>binder.registerCustomEditor</code> calls in an implementation of BaseCommandController's <code>initBinder</code> method.
 * 
 * @author milan
 */
public class CustomDateEditor extends PropertyEditorSupport {

	private final String dateFormat;

	private final String timeFormat;

	private final boolean allowEmpty;

	private final Class<?> classToConvert;

	/**
	 * Create a new CustomDateEditor instance, using the given DateFormat for parsing and rendering.
	 * <p>
	 * The "allowEmpty" parameter states if an empty String should be allowed for parsing, i.e. get interpreted as null value. Otherwise, an IllegalArgumentException gets thrown in that case.
	 * 
	 * @param dateFormat
	 *            DateFormat to use for parsing and rendering
	 * @param allowEmpty
	 *            if empty strings should be allowed
	 */
	public CustomDateEditor(String dateFormat, String timeFormat, boolean allowEmpty, Class<?> classToConvert) {
		this.dateFormat = dateFormat;
		this.timeFormat = timeFormat;
		this.allowEmpty = allowEmpty;
		this.classToConvert = classToConvert;
	}

	/**
	 * Parse the Date from the given text, using the specified DateFormat.
	 */
	@Override
	public void setAsText(String text) {
		if (this.allowEmpty && !StringUtils.hasText(text)) {
			// treat empty String as null value
			setValue(null);
		} else {
			try {
				if (text != null && text.trim().indexOf(' ') != -1) {
					tryToSetDateAndTime(text);

				} else {
					tryToSetDate(text);
				}
			} catch (ParseException ex) {
				throw new IllegalArgumentException("Could not parse date: " + ex.getMessage());
			}
		}
	}

	private void tryToSetDate(String text) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		if (text == null) {
			setValue("");
		} else {
			java.util.Date d = sdf.parse(text);
			if (classToConvert.equals(Timestamp.class)) {
				setValue(new Timestamp(d.getTime()));
			} else {
				setValue(d);
			}
		}
	}

	private void tryToSetDateAndTime(String txt) throws ParseException {
		String text = txt.trim();
		String s = text.substring(text.indexOf(' ')).trim();
		StringTokenizer st = new StringTokenizer(s, ":");
		String dtf = dateFormat + " " + timeFormat;
		if (st.countTokens() == 3)
			dtf = dtf + ":ss";
		SimpleDateFormat sdf = new SimpleDateFormat(dtf);
		java.util.Date d = sdf.parse(text);
		if (classToConvert.equals(Timestamp.class)) {
			setValue(new Timestamp(d.getTime()));
		} else {
			setValue(d);
		}
	}

	/**
	 * Format the Date as String, using the specified DateFormat.
	 */
	@Override
	public String getAsText() {
		if (getValue() == null)
			return "";
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format((Date) getValue());
	}

}
