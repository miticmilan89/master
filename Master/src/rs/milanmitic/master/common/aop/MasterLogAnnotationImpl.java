package rs.milanmitic.master.common.aop;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import rs.milanmitic.master.common.Constants;
import rs.milanmitic.master.common.ContextHolder;
import rs.milanmitic.master.common.util.ActivityUtil;
import rs.milanmitic.master.common.util.Utils;
import rs.milanmitic.master.model.ActivityLog;
import rs.milanmitic.master.model.AuditLog;

/**
 * Intercepts every method that change something in database.<br/>
 * 
 * 
 * @author milan
 */
@Aspect
@Component
public class MasterLogAnnotationImpl {
	protected static final Logger log = LogManager.getLogger(MasterLogAnnotationImpl.class);

	/**
	 * Around aspect
	 * 
	 * @param joinPoint
	 * @return
	 * @throws Throwable
	 */
	@Around(value = "@annotation(rs.milanmitic.master.common.aop.MasterLogAnnotation)")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable { 
		boolean success = false;
		try {
			if (Constants.TRACE_METHOD)
				writeMethodInfo(joinPoint);

			if (!ContextHolder.isRuntimeRequest())
				addToAuditLog(joinPoint);

			// continue on the intercepted method
			Object returnValue = joinPoint.proceed();
			success = true;
			return returnValue;
		} catch (Exception t) {
			if (!ContextHolder.isRuntimeRequest())
				writeMethodErrorInfo(joinPoint, t);
			throw t;
		} finally {
			log.debug("logAround end, success:{}", success);
		}
	}

	private void addToAuditLog(ProceedingJoinPoint joinPoint) {
		AuditLog audtilLog = ContextHolder.getAuditLog();
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		MasterLogAnnotation annotation = method.getAnnotation(MasterLogAnnotation.class);
		String fullMethodName = joinPoint.getTarget().getClass().getSimpleName() + "." + method.getName();
		if (audtilLog != null) {
			if (annotation.eventLogEnabled()) {
				String methodName = method.getName();
				boolean makeEventLog = true;
				if (!Constants.LOG_TO_EVENTLOG_GET_METHOD && methodName.startsWith("get"))
					makeEventLog = false;
				if (!Constants.LOG_TO_EVENTLOG_VALIDATE_METHOD && methodName.startsWith("validate"))
					makeEventLog = false;

				if (makeEventLog) {
					audtilLog.addActivity("methodName", fullMethodName);
				}

			} else {
				log.debug("Anotation event log will not be created for input parameters({}.{})", joinPoint.getTarget().getClass().getName(), method.getName());
			}
		}
		ActivityLog acLog = ContextHolder.getActivityLog();
		if (acLog != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("<action><methodName>").append(fullMethodName).append("</methodName>");
			sb.append("<params>");

			Object[] o = joinPoint.getArgs();
			if (o != null) {
				int n = o.length;
				for (int i = 0; i < n; i++) {

					String className = null;
					if (o[i] instanceof HttpServletResponse)
						className = "<response/>";
					else if (o[i] instanceof HttpServletRequest)
						className = "<request/>";
					else if (o[i] instanceof Model)
						className = "<model/>";
					else if (o[i] instanceof BindingResult)
						className = "<bindingResult/>";
					if (className == null) {
						className = o[i] == null ? "param_" + i : "" + o[i].getClass().getSimpleName();
						sb.append("<").append(className).append(">");
						sb.append(o[i]);
						sb.append("</").append(className).append(">");
					} else {
						sb.append(className);

					}

				}
			} else {
				sb.append("<noparams/>");
			}

			sb.append("</params>");
			sb.append("</action>");

			acLog.addActivityXML(sb.toString());
		}

	}

	private void writeMethodErrorInfo(ProceedingJoinPoint joinPoint, Throwable t) {
		StringBuilder sb = null;
		try {
			// logging can be disabled by target class
			Logger targetClasslog = LogManager.getLogger(joinPoint.getTarget().getClass());
			sb = new StringBuilder();
			sb.append("(errorNo:").append(ContextHolder.getErrorCount()).append(") - (URL:" + ContextHolder.getURL() + ") ");

			ActivityLog ev = ContextHolder.createActivityLog();
			StringBuilder sbEventLog = new StringBuilder();
			boolean first = ev.getErrorNo() == null;
			if (first) {
				ev.setErrorNo(ContextHolder.getErrorCount());
				sbEventLog.append("<master_error method=\"").append(((MethodSignature) joinPoint.getSignature()).getName()).append("\" >");
			} else {
				sbEventLog.append("<master_error class=\"").append(joinPoint.getTarget().getClass().getName()).append("\" method=\"").append(((MethodSignature) joinPoint.getSignature()).getName()).append("\" >");

			}
			createParametersLog(joinPoint, sb, sbEventLog);

			if (!joinPoint.getTarget().getClass().getSimpleName().endsWith("HibernateDao") || first) {
				StringBuilder sbs = t == null ? new StringBuilder() : generateStackTraceLog(t);
				if (first) {
					ActivityUtil au = new ActivityUtil();
					sbEventLog.append("\n").append(au.addActivity("stack_trace", sbs).getCustomXml());
				}

				// do not log stack trace in Hibernate, but in service method
				if (!joinPoint.getTarget().getClass().getSimpleName().endsWith("HibernateDao")) {
					sbs.insert(0, "\nstack trace:").append("\n");
					log.debug(sb + (t != null ? "Error:" + t.getMessage() : "Error:null"));
					sb.append(sbs);
				}
			} else {
				log.debug(sb);
			}
			if (targetClasslog.isErrorEnabled()) {
				targetClasslog.error(sb);
			}
			sbEventLog.append("</master_error>");
			if (first) {
				ev.setActivityData("<err_root>\n" + sbEventLog.toString() + "\n</err_root>\n");
			}

		} catch (Exception t1) {
			// should never happen
			log.error("Error(ignored) generating method info:" + sb, t1);
		}
	}

	private StringBuilder generateStackTraceLog(Throwable t) {
		String s = Utils.getStackTrace(t);
		StringBuilder sbs = new StringBuilder();
		String[] so = s.split("\n");
		for (String h : so) {
			String g = h.trim();
			final boolean b = g.startsWith("at org.springframework.") || g.startsWith("at sun.reflect.") || g.startsWith("at java.lang.reflect");
			final boolean c = g.startsWith("at javax.servlet.") || g.startsWith("at org.apache.") || g.startsWith("at org.hibernate.");
			final boolean d = g.startsWith("at java.util.") || g.startsWith("at rs.milanmitic.master.common.aop.MasterLogAnnotationImpl.logAround");
			if (b || c || d || g.indexOf("BySpringCGLIB$$") != -1)
				continue;
			sbs.append(h);
		}
		return sbs;
	}

	/**
	 * Log method info
	 * 
	 * @param joinPoint
	 */
	protected void writeMethodInfo(ProceedingJoinPoint joinPoint) {
		// logging can be disabled by target class
		Logger targetClasslog = LogManager.getLogger(joinPoint.getTarget().getClass());
		if (targetClasslog.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("executedMethod:");

			createParametersLog(joinPoint, sb, null);

			targetClasslog.debug(sb);
		}
	}

	// note
	// (http://stackoverflow.com/questions/5714411/getting-the-java-lang-reflect-method-from-a-proceedingjoinpoint)
	// ************************
	// You should be careful because Method method = signature.getMethod()
	// will return the method of the interface, you should add this to be sure
	// to get the method of the implementation class:

	private void createParametersLog(ProceedingJoinPoint joinPoint, StringBuilder sb, StringBuilder sbEventLog) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		ActivityUtil au = null;
		if (sbEventLog != null) {
			au = new ActivityUtil();
		}
		sb.append("class:").append(joinPoint.getTarget().getClass().getName()).append(".").append(method.getName()).append(", ");
		sb.append("parameters:");
		Object[] o = joinPoint.getArgs();
		if (o != null) {
			int n = o.length;
			for (int i = 0; i < n; i++) {
				String className = null;
				if (o[i] instanceof HttpServletResponse)
					className = "<response>";
				else if (o[i] instanceof HttpServletRequest)
					className = "<request>";
				else if (o[i] instanceof Model)
					className = "<model>";
				else if (o[i] instanceof BindingResult)
					className = "<bindingResult>";
				if (className == null) {
					if (o[i] == null)
						sb.append("[").append(i).append("-").append(i).append("]=").append(o[i]).append(", ");
					else
						sb.append("[").append(i).append("-").append(o[i].getClass().getName()).append("]=").append(o[i]).append(", ");
					if (sbEventLog != null && o[i] != null)
						au.addActivity(o[i].getClass().getName(), o[i]);

				} else {
					sb.append("[").append(i).append("-").append(className).append("], ");
					if (sbEventLog != null)
						au.addActivity(className);
				}
			}
		} else {
			sb.append("<no parameters>");
		}
		if (sbEventLog != null) {
			sbEventLog.append(au.getCustomXml());
		}

	}

}
