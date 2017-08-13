package rs.milanmitic.master.service;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import rs.milanmitic.master.common.aop.MasterLogAnnotation;
import rs.milanmitic.master.model.ActivityLog;
import rs.milanmitic.master.model.AuditLog;
import rs.milanmitic.master.repository.CommonDao;

/**
 * @see NewTransactionService
 * @author milan
 */
@Service
@org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRES_NEW)
public class NewTransactionServiceImpl implements NewTransactionService {

	protected final Logger log = LogManager.getLogger(this.getClass());

	@Autowired
	private CommonDao commonDao;

	/**
	 * @see NewTransactionService#saveUsraulog(Usraulog)
	 */
	@Override
	public void saveActivityLog(ActivityLog ev) {
		this.commonDao.save(ev);
	}

	/**
	 * @see NewTransactionService#saveUsraulog(Usraulog)
	 */
	@Override
	public void saveAuditLog(AuditLog ev) {
		this.commonDao.save(ev);
	}

	@Override
	@MasterLogAnnotation
	public <T extends Object> T getByPk(Serializable pk, Class<T> k) {
		return commonDao.getByPk(pk, k);
	}

	@Override
	public void update(Object o) {
		commonDao.update(o);
	}

}
