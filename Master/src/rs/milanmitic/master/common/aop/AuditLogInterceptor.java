package rs.milanmitic.master.common.aop;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import rs.milanmitic.master.common.ContextHolder;
import rs.milanmitic.master.common.util.ActivityUtil;
import rs.milanmitic.master.model.AuditLog;

public class AuditLogInterceptor extends EmptyInterceptor {

	private static final Logger log = LogManager.getLogger(AuditLogInterceptor.class);

	private static final long serialVersionUID = 1L;

	@Override
	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		// log delete events
		log.debug("Delete:{}", entity);
		addToEventLog(entity, "DELETE");
	}

	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
		log.debug("onFlushDirty, Update:{}", entity);
		addToEventLog(entity, "UPDATE");
		return false;
	}

	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		log.debug("onSave, Create:{}", entity);
		addToEventLog(entity, "INSERT");
		return false;
	}

	private void addToEventLog(Object object, String action) {
		AuditLog ev = ContextHolder.getAuditLog();
		if (ev != null && !(object instanceof AuditLog)) {
			String table = object.getClass().getSimpleName().toUpperCase();
			if (ev.getTableName() == null)
				ev.setTableName(table);
			else {
				if (ev.getTableName().indexOf(table) == -1) {
					ev.setTableName(ev.getTableName() + " " + table);
				}
			}

			if (ev.getAuditType() == null)
				ev.setAuditType(action);
			else {
				if (ev.getAuditType().indexOf(action) == -1) {
					ev.setAuditType(ev.getAuditType() + " " + action);
				}
			}

			ActivityUtil au = new ActivityUtil();
			StringBuilder sb = new StringBuilder();
			sb.append("<db_obj ent=\"").append(table).append("\" action=\"").append(action).append("\" >");
			sb.append(au.addActivity(object).getCustomXml());
			sb.append("</db_obj>");
			ev.addActivityXML(sb.toString());
		}
	}
}