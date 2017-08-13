package rs.milanmitic.master.common;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;

import rs.milanmitic.master.model.ActivityLog;
import rs.milanmitic.master.model.AuditLog;
import rs.milanmitic.master.service.NewTransactionService;

/**
 * Master Transaction manager
 * 
 * @author milan
 * 
 */
public class MasterHibernateTransactionManager extends org.springframework.orm.hibernate4.HibernateTransactionManager implements ApplicationContextAware {

	protected static final Logger log = LogManager.getLogger(MasterHibernateTransactionManager.class);

	private static final long serialVersionUID = 1L;

	private transient ApplicationContext applicationContext;

	private static AtomicLong transactionCounter = new AtomicLong();

	private static long transactionCounterWarningLimit = 5;

	private static long transactionDurationLimitMsec = 5000;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	protected void doBegin(Object arg0, TransactionDefinition td) {
		long currentTransactionNo = transactionCounter.incrementAndGet();
		log.debug(">> doBegin isolationLevel:{}, propagationBehavior:{}, timeout:{}, name:{}, currentTransactionNo:{}, threadName:{}", td.getIsolationLevel(), td.getPropagationBehavior(), td.getTimeout(), td.getName(), currentTransactionNo,
				Thread.currentThread().getName());

		if (currentTransactionNo > transactionCounterWarningLimit)
			log.info("WARNING doBegin - POSSIBLE TRANSACTION LEAK, currentTransactionNo:{}, threadName:{}", currentTransactionNo, Thread.currentThread().getName());
		super.doBegin(arg0, td);
	}

	protected void printTransactionInfo(String methodName, long currentTransactionNo) {
		Long timeStarted = null;
		Long timeElapsed = null;
		if (ContextHolder.isContextCreated()) {
			timeStarted = ContextHolder.getTimeStartedMsec();
			if (timeStarted != null) {
				timeElapsed = System.currentTimeMillis() - timeStarted;
			}
		}

		if (currentTransactionNo > transactionCounterWarningLimit)
			log.info("WARNING {} - POSSIBLE TRANSACTION LEAK, currentTransactionNo:{}, threadName:{}, timeStarted:{} msec", methodName, currentTransactionNo, Thread.currentThread().getName(), timeStarted);
		log.debug(">> {}, currentTransactionNo:{}, threadName:{}, timeElapsed:{} msec", methodName, currentTransactionNo, Thread.currentThread().getName(), timeElapsed);

		if (timeElapsed != null && timeElapsed.longValue() > transactionDurationLimitMsec) {
			log.info(">> Transaction took to long to finish, {}, currentTransactionNo:{}, threadName:{}, timeElapsed:{} msec", methodName, currentTransactionNo, Thread.currentThread().getName(), timeElapsed);

		}
	}

	@Override
	protected void doCommit(DefaultTransactionStatus arg0) {
		long currentTransactionNo = transactionCounter.decrementAndGet();
		printTransactionInfo("doCommit", currentTransactionNo);
		boolean success = false;
		try {
			super.doCommit(arg0);
			success = true;
		} finally {
			AuditLog usrauLog = ContextHolder.getAuditLog();
			ActivityLog acLog = ContextHolder.getActivityLog();


			if (usrauLog != null) {
				if (!success)
					usrauLog.setAuditStatus(AuditLog.TRANSSTATUS_NOT_SUCCESS);
				saveUsraulog(usrauLog);
			}

			if (acLog != null && acLog.isThreadLog()) {
				try {
					ContextHolder.removeActivityLog();
					saveAclog(acLog);
				} finally {
					ContextHolder.clearContext();
				}
			}
		}
	}

	@Override
	protected void doRollback(DefaultTransactionStatus arg0) {
		long currentTransactionNo = transactionCounter.decrementAndGet();
		printTransactionInfo("doRollback", currentTransactionNo);
		try {
			super.doRollback(arg0);
		} finally {
			AuditLog usrauLog = ContextHolder.getAuditLog();
			ActivityLog acLog = ContextHolder.getActivityLog();
			if (usrauLog != null) {
				usrauLog.setAuditStatus(AuditLog.TRANSSTATUS_NOT_SUCCESS);
				saveUsraulog(usrauLog);
			}
			if (acLog != null && acLog.isThreadLog()) {
				try {
					ContextHolder.removeActivityLog();
					saveAclog(acLog);
				} finally {
					ContextHolder.clearContext();
				}
			}
		}
	}

	private void saveAclog(ActivityLog acLog) {
		if (acLog != null) {
			acLog.setActivityData(acLog.getActivityUtil().getXml());
			if (StringUtils.isBlank(acLog.getActivityData())) {
				// LOG IS EMPTY, DO NOT WRITE EVENT
					log.debug("Activity log not saved because activitydata is empty");
				return;
			}
			if (!acLog.isDoNotSave()) {
				if (acLog.getActivityData() != null && acLog.getActivityData().length() > ActivityLog.MSSGTX_MAX_LEN) {
					acLog.setActivityData(acLog.getActivityData().substring(0, ActivityLog.MSSGTX_MAX_LEN - 4) + "...");
					log.error("Activity log not fully saved because length of activityData field >{} so here is fully record :{}", ActivityLog.MSSGTX_MAX_LEN, acLog);
				}
				NewTransactionService newTransactionService = applicationContext.getBean(NewTransactionService.class);
				newTransactionService.saveActivityLog(acLog);
			} else {
				log.debug("Activity log not saved:{}", acLog);
			}
		}
	}

	private void saveUsraulog(AuditLog usrLog) {
		try {
			// now commit eventLog
			if (usrLog == null)
				return;
			// to avoid recursion, remove event log before save is occurred
			ContextHolder.removeAuditLog();
			usrLog.setUpdateString(usrLog.getActivityUtil().getXml());
			if (StringUtils.isBlank(usrLog.getUpdateString())) {
				// LOG IS EMPTY, DO NOT WRITE EVENT
				log.debug("Audit log not saved because updateData is empty");
				return;
			}
			if (!usrLog.isDoNotSave()) {
				if (usrLog.getUpdateString() != null && usrLog.getUpdateString().length() > AuditLog.MSSGTX_MAX_LEN) {
					usrLog.setUpdateString(usrLog.getUpdateString().substring(0, AuditLog.MSSGTX_MAX_LEN - 4) + "...");
					log.error("Audit log not fully saved because length of Audtentrydetl field >" + AuditLog.MSSGTX_MAX_LEN + " so here is fully record :" + usrLog);
				}
				NewTransactionService newTransactionService = applicationContext.getBean(NewTransactionService.class);
				newTransactionService.saveAuditLog(usrLog);
			} else {
				log.debug("Audit log not saved:{}", usrLog);
			}
		} catch (Exception t) {
			try {
				log.error("Error(ignored) saving Audit log:" + usrLog, t);
			} catch (Exception t1) {
				log.debug("Error(ignored)", t1);

			}
			log.debug("Error(ignored)", t);
		}
	}


	public static void setTransactionCounterWarningLimit(long transactionCounterWarningLimit) {
		MasterHibernateTransactionManager.transactionCounterWarningLimit = transactionCounterWarningLimit;
	}
}