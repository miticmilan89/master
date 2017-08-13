package rs.milanmitic.master.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.StaleObjectStateException;
import org.hibernate.exception.GenericJDBCException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.HibernateJdbcException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import rs.milanmitic.master.common.Constants;
import rs.milanmitic.master.common.ContextHolder;
import rs.milanmitic.master.common.CustomDateEditor;
import rs.milanmitic.master.common.SelectFieldIntf;
import rs.milanmitic.master.common.data.FormatPatterns;
import rs.milanmitic.master.common.data.LabelValue;
import rs.milanmitic.master.common.data.LoggedUserData;
import rs.milanmitic.master.common.exception.ValidateException;
import rs.milanmitic.master.common.pagging.SearchInput;
import rs.milanmitic.master.common.protector.HiddenFieldsSecureInterface;
import rs.milanmitic.master.common.util.Utils;

/**
 * Basic controller - all others should extend it
 * 
 * @author milan
 * 
 */
@Component
public class BasicController implements MessageSourceAware {

	private static final String LABEL_ERROR_LOGGED = "_errorLogged";
	private static final String LABEL_ERROR_UNPREDICTABLE_ERROR = "error.unpredictableError";
	protected final Logger log = LogManager.getLogger(this.getClass());
	public static final String APP_MESSAGES = "appMessages";

	public static final String APP_ERROR = "appError";

	protected MessageSource messageSource;

	@Autowired
	protected SpringValidatorAdapter validator;

	@InitBinder
	public void initBinder(WebDataBinder binder, HttpServletRequest request) {
		FormatPatterns mfp = Utils.getFormatPatterns(request.getSession());
		if (mfp != null && mfp.getDateInputPattern() != null) {
			binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(mfp.getDateInputPattern(), mfp.getTimeInputPattern(), true, java.util.Date.class));
			binder.registerCustomEditor(java.sql.Date.class, new CustomDateEditor(mfp.getDateInputPattern(), mfp.getTimeInputPattern(), true, java.sql.Date.class));
			binder.registerCustomEditor(java.sql.Timestamp.class, new CustomDateEditor(mfp.getDateInputPattern(), mfp.getTimeInputPattern(), true, java.sql.Timestamp.class));
		}

		DecimalFormat nf = Utils.getDecimalFormat(request);
		DecimalFormat integerFormat = Utils.getDecimalFormat(request);
		integerFormat.setMaximumFractionDigits(0);

		binder.registerCustomEditor(BigDecimal.class, null, new CustomNumberEditor(BigDecimal.class, nf, true));
		binder.registerCustomEditor(Double.class, null, new CustomNumberEditor(Double.class, nf, true));
		binder.registerCustomEditor(Long.class, null, new CustomNumberEditor(Long.class, integerFormat, true));
		binder.registerCustomEditor(Integer.class, null, new CustomNumberEditor(Integer.class, integerFormat, true));

	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;

	}

	protected void handleError(HttpServletRequest request, Throwable t) {
		handleError(request, t, null);

	}

	private void logError(HttpSession session, Throwable t, BindingResult result, ObjectError e, String errorMsg) {
		if (session.getAttribute(LABEL_ERROR_LOGGED) != null)
			return;
		long errorNo = ContextHolder.getErrorCount();
		session.setAttribute("_errorCounter", errorNo);

		StringBuilder sb = new StringBuilder();
		sb.append("(errorNo:").append(ContextHolder.getErrorCount()).append(") - (URL:" + ContextHolder.getURL() + ") ");
		if (t != null) {
			sb.append(t.getClass().getSimpleName()).append(":");
			if (t instanceof ValidateException) {
				internalLogValidateException(t, result, sb);
			} else {
				sb.append("Error:").append(t.getMessage());
				log.error(sb, t);
			}
		} else if (e != null) {
			sb.append(e.getClass().getSimpleName()).append(":").append(e);
			log.error(sb);
		} else if (errorMsg != null) {
			sb.append("Error:").append(errorMsg);
			log.error(sb);
		}

	}

	private void internalLogValidateException(Throwable t, BindingResult result, StringBuilder sb) {
		ValidateException ve = (ValidateException) t;
		if (result != null && StringUtils.isNotBlank(ve.getField())) {
			result.rejectValue(ve.getField(), ve.getMessage(), ve.getParams(), ve.getMessage());
			sb.append(" ObjectName:").append(result.getObjectName()).append(", Field:").append(ve.getField()).append(", Message:").append(ve.getMessage());
		} else {
			sb.append(" Message:").append(ve.getMessage());
		}
		if (ve.getParams() != null) {
			sb.append(", Params:");
			for (Object x : ve.getParams())
				sb.append(x).append(",");
		}
		sb.append(":minimalStackTrace:").append(Utils.getMinimalStackTrace(t));
		log.error(sb);
	}

	protected void handleError(HttpServletRequest request, Throwable t, BindingResult result) {
		try {
			logError(request.getSession(), t, result, null, null);
			request.getSession().setAttribute(LABEL_ERROR_LOGGED, 1);
			if (t == null) {
				addError(request, "error.unpredictableError1");
			} else if (t instanceof ValidateException) {
				internalHandleValidateException(request, t, result);
			} else if (t instanceof org.springframework.security.access.AccessDeniedException) {
				addError(request, "global.error.accessDeniedException.code");
			} else if (t instanceof DataIntegrityViolationException) {
				addError(request, "global.error.dataIntegrityViolation.code");
			} else if (t instanceof org.hibernate.exception.LockAcquisitionException) {
				addError(request, "global.error.LockAcquisitionException.code");

			} else if (t instanceof org.hibernate.exception.ConstraintViolationException) {
				addError(request, "global.error.constraintViolation.code");
			} else if (isOptimisticLockException(t)) {
				addError(request, "error.fieldUpdatedOrDeleted");
			} else if (t instanceof org.hibernate.QueryException) {
				addError(request, "global.error.sqlError");
			} else if (isHibernateException(t)) {

				if (!Constants.isDebugMode())
					addError(request, "global.error.sqlError");
				else
					addError(request, BasicController.LABEL_ERROR_UNPREDICTABLE_ERROR, t.getMessage());
			} else {
				if (!Constants.isDebugMode())
					addError(request, "error.unpredictableError1");
				else
					addError(request, BasicController.LABEL_ERROR_UNPREDICTABLE_ERROR, t.getMessage());
			}
		} finally {
			request.getSession().removeAttribute(LABEL_ERROR_LOGGED);
		}
	}

	private void internalHandleValidateException(HttpServletRequest request, Throwable t, BindingResult result) {
		ValidateException ve = (ValidateException) t;
		if (result != null && StringUtils.isNotBlank(ve.getField())) {
			result.rejectValue(ve.getField(), ve.getMessage(), ve.getParams(), ve.getMessage());
			addError(request, new FieldError(result.getObjectName(), ve.getField(), getMessage(ve.getMessage(), ve.getParams())));
		} else {
			addError(request, ve.getMessage(), ve.getParams());
		}
	}

	private boolean isHibernateException(Throwable t) {
		return t instanceof HibernateJdbcException || t instanceof GenericJDBCException || t instanceof org.hibernate.exception.SQLGrammarException || t instanceof org.hibernate.exception.DataException;
	}

	private boolean isOptimisticLockException(Throwable t) {
		return t instanceof StaleObjectStateException || t instanceof org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException || t instanceof org.springframework.orm.hibernate4.HibernateOptimisticLockingFailureException;
	}

	protected void handleError(HttpSession session, Throwable t) {
		if (t instanceof ValidateException) {
			ValidateException ve = (ValidateException) t;
			addError(session, ve.getMessage(), ve.getParams());
		} else if (t instanceof StaleObjectStateException) {
			addError(session, "error.fieldUpdatedOrDeleted");
		} else if (t instanceof org.hibernate.exception.ConstraintViolationException) {
			addError(session, "global.error.constraintViolation.code");

		} else {
			addError(session, BasicController.LABEL_ERROR_UNPREDICTABLE_ERROR, t.getMessage());
		}
		logError(session, t, null, null, null);
	}

	protected List<String> convertToAjaxErrors(List<Object> list) {
		if (list == null)
			return Constants.getEmptyStringList();
		Iterator<Object> it = list.iterator();
		List<String> result = new ArrayList<String>();
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof FieldError) {
				FieldError f = (FieldError) o;
				result.add(f.getField() + "|" + getMessage("label." + f.getObjectName() + "." + f.getField()) + " : " + getMessage(f.getCode(), f.getArguments()));
			} else
				result.add((String) o);
		}
		return result.isEmpty() ? Constants.getEmptyStringList() : result;

	}

	protected void checkErrors(HttpServletRequest request, BindingResult result, String type) {
		if (result.hasErrors()) {
			for (FieldError e : result.getFieldErrors()) {
				if ("NotNull".equals(e.getCodes()[3]) || "NotEmpty".equals(e.getCodes()[3]))
					addError(request, Constants.LABEL_ERROR_REQUIRED, getMessage(type.concat(".").concat(e.getField())));
				else if ("Length".equals(e.getCodes()[3]))
					addError(request, "error.maxLength", getMessage(type.concat(".").concat(e.getField())), e.getArguments()[1]);
				else if ("typeMismatch".equals(e.getCodes()[3]))
					addError(request, "error.typeMismatch", getMessage(type.concat(".").concat(e.getField())));
				else
					addError(request, e.getCodes()[0]);
			}
		}
	}

	protected void checkErrors(HttpSession session, BindingResult result, String type) {
		if (result.hasErrors()) {
			for (FieldError e : result.getFieldErrors()) {
				if ("NotNull".equals(e.getCodes()[3]) || "NotEmpty".equals(e.getCodes()[3]))
					addError(session, Constants.LABEL_ERROR_REQUIRED, getMessage(type.concat(".").concat(e.getField())));
				else
					addError(session, e.getCodes()[0]);
			}
		}
	}

	protected LoggedUserData getLoggedUser(HttpServletRequest request) {
		return (LoggedUserData) request.getSession().getAttribute(Constants.LOGGED_USER);
	}

	@SuppressWarnings("unchecked")
	protected void addError(HttpServletRequest request, List<ObjectError> list) {
		List<Object> l = (List<Object>) request.getAttribute(APP_ERROR);
		if (l == null) {
			l = new ArrayList<Object>();
		}
		for (ObjectError t : list) {
			if (!l.contains(t)) {
				l.add(t);
				logError(request.getSession(), null, null, t, null);
			}
		}
		request.setAttribute(APP_ERROR, l);
	}

	@SuppressWarnings("unchecked")
	protected void addError(HttpSession session, List<ObjectError> list) {
		List<Serializable> l = (List<Serializable>) session.getAttribute(APP_ERROR);
		if (l == null) {
			l = new ArrayList<Serializable>();
		}
		for (ObjectError t : list) {
			if (!l.contains(t)) {
				l.add(t);
				logError(session, null, null, t, null);
			}
		}
		session.setAttribute(APP_ERROR, l);
	}

	@SuppressWarnings("unchecked")
	protected void addError(HttpServletRequest request, ObjectError err) {
		List<Object> l = (List<Object>) request.getAttribute(APP_ERROR);
		if (l == null) {
			l = new ArrayList<Object>();
		}
		if (!l.contains(err)) {
			l.add(err);
			logError(request.getSession(), null, null, err, null);

		}
		request.setAttribute(APP_ERROR, l);
	}

	@SuppressWarnings("unchecked")
	protected List<Object> getErrors(HttpServletRequest request) {
		List<Object> list = new ArrayList<Object>();
		List<Object> listRequest = (List<Object>) request.getAttribute(APP_ERROR);
		List<Object> listSession = (List<Object>) request.getSession().getAttribute(APP_ERROR);
		if (listRequest != null)
			list.addAll(listRequest);
		if (listSession != null)
			list.addAll(listSession);
		return list.isEmpty() ? null : list;

	}

	protected MessageSourceAccessor getMessageSourceAccessor() {
		return new MessageSourceAccessor(messageSource);
	}

	public String getMessage(String key, Object... params) {
		return Utils.getMessage(key, getMessageSourceAccessor(), params);
	}

	@SuppressWarnings("unchecked")
	protected void addMessage(HttpServletRequest request, String message, Object... params) {
		List<String> list = (List<String>) request.getAttribute(APP_MESSAGES);
		if (list == null) {
			list = new ArrayList<String>();
			request.setAttribute(APP_MESSAGES, list);
		}
		list.add(getMessage(message, params));
	}

	@SuppressWarnings("unchecked")
	protected void addMessage(HttpSession session, String message, Object... params) {
		List<String> list = (List<String>) session.getAttribute(APP_MESSAGES);
		if (list == null) {
			list = new ArrayList<String>();
			session.setAttribute(APP_MESSAGES, list);
		}
		list.add(getMessage(message, params));
	}

	@SuppressWarnings("unchecked")
	protected void addError(HttpServletRequest request, String message, Object... params) {
		List<String> list = (List<String>) request.getAttribute(APP_ERROR);
		if (list == null) {
			list = new ArrayList<String>();
			request.setAttribute(APP_ERROR, list);
		}
		String m = getMessage(message, params);
		list.add(m);
		logError(request.getSession(), null, null, null, m);

	}

	@SuppressWarnings("unchecked")
	protected void addError(HttpSession session, String message, Object... params) {
		List<String> list = (List<String>) session.getAttribute(APP_ERROR);
		if (list == null) {
			list = new ArrayList<String>();
			session.setAttribute(APP_ERROR, list);
		}
		String m = getMessage(message, params);
		list.add(m);
		logError(session, null, null, null, m);
	}

	@SuppressWarnings("unchecked")
	protected boolean hasErrors(HttpServletRequest request) {
		List<String> list = (List<String>) request.getSession().getAttribute(APP_ERROR);
		if (list != null && !list.isEmpty())
			return true;
		list = (List<String>) request.getAttribute(APP_ERROR);
		return (list != null && !list.isEmpty());

	}

	public List<SelectFieldIntf> getNoYesSelect() {
		List<SelectFieldIntf> yesno = new ArrayList<SelectFieldIntf>();
		yesno.add(new LabelValue(getMessage("label.no"), "" + Constants.getStatusPassive()));
		yesno.add(new LabelValue(getMessage("label.yes"), "" + Constants.getStatusActive()));
		return yesno;
	}

	public List<SelectFieldIntf> getYesNoSelect() {
		List<SelectFieldIntf> yesno = new ArrayList<SelectFieldIntf>();
		yesno.add(new LabelValue(getMessage("label.yes"), "" + Constants.getStatusActive()));
		yesno.add(new LabelValue(getMessage("label.no"), "" + Constants.getStatusPassive()));
		return yesno;
	}

	public List<SelectFieldIntf> getYesNoStringSelect() {
		List<SelectFieldIntf> yesno = new ArrayList<SelectFieldIntf>();
		yesno.add(new LabelValue(getMessage("label.yes"), "" + Constants.YES));
		yesno.add(new LabelValue(getMessage("label.no"), "" + Constants.NO));
		return yesno;
	}

	public List<SelectFieldIntf> getStatusSelect(boolean firstEmpty) {
		List<SelectFieldIntf> yesno = new ArrayList<SelectFieldIntf>();
		if (firstEmpty)
			yesno.add(new LabelValue("", ""));
		yesno.add(new LabelValue(getMessage("label.active"), "" + Constants.getStatusActive()));
		yesno.add(new LabelValue(getMessage("label.inactive"), "" + Constants.getStatusPassive()));
		return yesno;
	}

	protected void checkSecureHiddenFields(HiddenFieldsSecureInterface bean, HttpServletRequest request) {
		String stvReq = request.getParameter(Constants.SECURE_FIELD);
		String stvbean = bean.generateSecureFieldsHash();
		if (stvReq == null || !stvReq.equals(stvbean)) {
			if (log.isDebugEnabled())
				log.debug("stvReq:" + stvReq + ",stvbean:" + stvbean);
			throw new ValidateException("error.invalidSecureHiddenFields");
		}
	}

	protected boolean isRequestFromMenu(HttpServletRequest request) {
		String mnu = request.getParameter("_mnu");
		boolean pagging = "true".equals(request.getParameter("_pagging"));
		return "2".equals(mnu) || pagging;
	}

	@SuppressWarnings("unchecked")
	protected <T extends Serializable> T isRequestFromMenu(HttpServletRequest request, T bean) {
		if (bean == null)
			return bean;
		String mnu = request.getParameter("_mnu");
		boolean pagging = "true".equals(request.getParameter("_pagging"));
		String key = request.getRequestURI() + "_" + bean.getClass().getSimpleName();

		if ("2".equals(mnu) || pagging) {
			Object b = request.getSession().getAttribute(key);
			if (b != null) {
				Integer newPage = null;
				if (pagging && b instanceof SearchInput)
					newPage = ((SearchInput) bean).getCurrentPage();
				bean = (T) b;
				if (newPage != null) {
					SearchInput xx = (SearchInput) bean;
					xx.setRestartPagging(false);
					xx.setCurrentPage(newPage);
				}

			}
		} else {
			request.getSession().setAttribute(key, bean);
		}
		return bean;
	}

}
