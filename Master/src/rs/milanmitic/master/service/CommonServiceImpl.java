package rs.milanmitic.master.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import rs.milanmitic.master.common.aop.MasterLogAnnotation;
import rs.milanmitic.master.common.data.LoggedUserData;
import rs.milanmitic.master.common.pagging.SearchResults;
import rs.milanmitic.master.model.ActivityLog;
import rs.milanmitic.master.model.AppConfig;
import rs.milanmitic.master.model.AppUser;
import rs.milanmitic.master.model.AuditLog;
import rs.milanmitic.master.repository.CommonDao;

@Service
@Transactional
public class CommonServiceImpl extends MasterServiceImpl implements CommonService, InitializingBean, DisposableBean {

	@Autowired
	private CommonDao commonDao;

	@Autowired
	protected MessageSource messageSource;

	@Override
	@MasterLogAnnotation
	public SecurityManager getSecurityManager() {
		return new SecurityManager();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// getSecurityManager().initialize()

	}

	@Override
	public void destroy() throws Exception {
		// getSecurityManager().destroy()
	}

	@MasterLogAnnotation
	@Override
	public void logoutUser(LoggedUserData ld) {
		commonDao.logoutUser(ld);
	}

	@MasterLogAnnotation
	@Override
	public AppUser getAppUserByUserName(String username) {
		return commonDao.getAppUserByUserName(username);
	}

	@Override
	@MasterLogAnnotation
	public SearchResults getAuditLogList(AuditLog bean) {
		return commonDao.getAuditLogList(bean);
	}

	@Override
	@MasterLogAnnotation
	public SearchResults getActivityLogList(ActivityLog bean) {
		return commonDao.getActivityLogList(bean);
	}

	@MasterLogAnnotation
	@Override
	public SearchResults getAppConfigList(AppConfig bean) {
		return commonDao.getAppConfigList(bean);
	}

}
