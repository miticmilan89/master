package rs.milanmitic.master.service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import rs.milanmitic.master.common.ContextHolder;
import rs.milanmitic.master.common.aop.MasterLogAnnotation;
import rs.milanmitic.master.common.exception.UnpredictableException;
import rs.milanmitic.master.common.exception.ValidateException;
import rs.milanmitic.master.common.pagging.SearchResults;
import rs.milanmitic.master.common.security.MasterUserPasswordEncoder;
import rs.milanmitic.master.common.security.UserType;
import rs.milanmitic.master.common.util.Utils;
import rs.milanmitic.master.model.AppFunction;
import rs.milanmitic.master.model.AppRole;
import rs.milanmitic.master.model.AppRoleAppFunction;
import rs.milanmitic.master.model.AppUser;
import rs.milanmitic.master.model.AppUserAppRole;
import rs.milanmitic.master.model.Participant;
import rs.milanmitic.master.model.PassHistory;
import rs.milanmitic.master.model.PassPolicy;
import rs.milanmitic.master.repository.CommonDao;
import rs.milanmitic.master.repository.NomenclatureDao;


@Transactional
@Service
public class NomenclatureServiceImpl extends MasterServiceImpl implements NomenclatureService {

	@Autowired
	private NomenclatureDao nomenclatureDao;
	
	@Autowired
	private CommonDao commonDao;

	@Autowired
	protected MessageSource messageSource;

	@Autowired
	protected MasterUserPasswordEncoder masterUserPasswordEncoder;
	
	private static final String FIELD_PSWD = "password";

	@Override
	@MasterLogAnnotation
	public AppUser getAppUserById(Long id) {
		AppUser b = nomenclatureDao.getByPk(id, AppUser.class);
		if (b != null && ContextHolder.isLoggedUserParticipant() && !ContextHolder.getLoggedParticipantData().getParticipantId().equals(b.getParticipantFk()))
			b = null;
		return b;
	}

	@Override
	@MasterLogAnnotation
	public void updateAppUser(AppUser bean, String[] assignedRoles, boolean updateRoles) {
		checkPassword(bean, false);
		bean.setChangedByAppUserFk(ContextHolder.getLoggedUser().getId());
		validateAppUser(bean);

		if (UserType.ADMIN.getDbId().equals(bean.getUserType()))
			bean.setParticipantFk(null);
		
		if (ContextHolder.isLoggedUserParticipant() && !ContextHolder.getLoggedParticipantData().getParticipantId().equals(bean.getParticipantFk())) {
			log.error("Wrong user participant id supplied:" + bean.getParticipantFk() + " but logged user participant id is:" + ContextHolder.getLoggedParticipantData().getParticipantId());
			throw new UnpredictableException("Wrong user participant");
		}

		PassPolicy passPolicy = null;

		if (bean.getPassPolicyFk() != null) {
			passPolicy = commonDao.getByPk(bean.getPassPolicyFk(), PassPolicy.class);
			checkPasswordSecurityData(bean, passPolicy, false);
		}

		AppUser oldAppUser = commonDao.getByPk(bean.getId(), AppUser.class);
		commonDao.evict(oldAppUser);

		boolean passwordChanged = StringUtils.isNotBlank(bean.getPasswordHash()) && !oldAppUser.getPasswordHash().equals(bean.getPasswordHash());

		if (passwordChanged) {
			checkPasswordHistory(bean.getId(), bean.getPassword());
			logAndCleanPasswordHistory(passPolicy, bean, bean.getPasswordHash());
			bean.setLastPassChangedDt(new Timestamp(System.currentTimeMillis()));
		}

		commonDao.update(bean);

		// We should update user roles just in case we come from User CRUD, otherwise we skip update of user roles.
		if (updateRoles) {
			updateAppUserAppRoles(bean, assignedRoles);
		}
	}
	
	/**
	 * Manage password history
	 * 
	 * @param passPolicy
	 * @param instid
	 * @param encPass
	 * @param userid
	 * @param userType
	 */
	public void logAndCleanPasswordHistory(PassPolicy passPolicy, AppUser user, String encPass) {
		log.debug("--> logAndCleanPasswordHistory passPolicy:{}, user:{}, encPass:{}" + passPolicy, user, encPass);

		if (passPolicy != null && passPolicy.getPassMinHistoryRepeat() != null && passPolicy.getPassMinHistoryRepeat().intValue() > 0) {
			PassHistory passHistory = new PassHistory();
			passHistory.setAppUserFk(user.getId());
			passHistory.setPasswordHash(encPass);
			passHistory.setChangedDt(new Timestamp(System.currentTimeMillis()));

			validatePasswordHistoryData(passHistory);

			try {
				commonDao.save(passHistory);
			} catch (ConstraintViolationException e) {
				log.debug("Error(converted to ValidateException", e);
				throw new ValidateException("error.passwordPreviouslyUsed");
			}

			// fetch all user pass history records
			List<PassHistory> list = nomenclatureDao.getPasswordHistoryByUserId(user.getId());
			if (list != null && !list.isEmpty()) {
				log.debug("--> logAndCleanPasswordHistory remove oldest user password changes size:{}", list.size());

				while (list.size() > passPolicy.getPassMinHistoryRepeat()) {
					PassHistory p = list.remove(0);
					commonDao.delete(p);
					log.debug("--> logAndCleanPasswordHistory delete:{}", p);

				}
			}
		}
	}

	/**
	 * Check required fields for AppUserPasswordSecurity
	 */
	protected void validatePasswordHistoryData(PassHistory passHistory) {
		if (passHistory.getAppUserFk() == null)
			throw new ValidateException("error.userIDRequired");

		if (StringUtils.isBlank(passHistory.getPasswordHash()))
			throw new ValidateException("error.userPasswordRequired");

		if (passHistory.getChangedDt() == null)
			throw new ValidateException("error.passwordChangeDateRequired");
	}

	protected void checkPasswordHistory(Long appUserFk, String hasPassword) {
		List<PassHistory> pwdhistList = nomenclatureDao.getPasswordHistoryByUserId(appUserFk);
		for (PassHistory passHistory : pwdhistList) {
			commonDao.evict(passHistory);
			if (masterUserPasswordEncoder.matches(hasPassword, passHistory.getPasswordHash()))
				throw new ValidateException("error.passwordPreviouslyUsed");
		}
	}
	
	@Override
	@MasterLogAnnotation
	public void addAppUser(AppUser bean, String[] assignedRoles) {
		
		checkPassword(bean, true);

		if (UserType.ADMIN.getDbId().equals(bean.getUserType()))
			bean.setParticipantFk(null);
		
		if (ContextHolder.isLoggedUserParticipant())
			bean.setParticipantFk(ContextHolder.getLoggedParticipantData().getParticipantId());

		bean.setChangedByAppUserFk(ContextHolder.getLoggedUser().getId());

		validateAppUser(bean);

		PassPolicy passPolicy = null;
		if (bean.getPassPolicyFk() != null) {
			passPolicy = commonDao.getByPk(bean.getPassPolicyFk(), PassPolicy.class);
			checkPasswordSecurityData(bean, passPolicy, true);
		}

		bean.setLastPassChangedDt(new Timestamp(System.currentTimeMillis()));
		
		nomenclatureDao.validateAppUser(bean);

		nomenclatureDao.save(bean);

		updateAppUserAppRoles(bean, assignedRoles);
		
		logAndCleanPasswordHistory(passPolicy, bean, bean.getPasswordHash());
		
	}
	
	protected void checkPasswordSecurityData(AppUser user, PassPolicy passPolicy, boolean isAdd) {
		String password = user.getPassword();
		String passwordConfirm = user.getRepeatPassword();
		String userId = user.getUsername();

		if (isAdd && StringUtils.isBlank(password)) {
			throw new ValidateException(FIELD_PSWD, "error.userPasswordRequired");
		}
		if (isAdd && StringUtils.isBlank(passwordConfirm)) {
			throw new ValidateException("error.userConfirmPasswordRequired");
		}

		if (!isAdd && StringUtils.isNotBlank(password) && passPolicy.getPassMinPeriodInDays() != null) {
			checkPassHistory(user, passPolicy);
		}

		if (StringUtils.isNotBlank(password) && StringUtils.isNotBlank(passwordConfirm) && !password.equals(passwordConfirm)) {
			throw new ValidateException("error.userPasswordsDoNoMatch");
		}
		if (StringUtils.isNotBlank(password) && passPolicy.getPassMinLength() != null && (password.length() < passPolicy.getPassMinLength())) {
			throw new ValidateException(FIELD_PSWD, "error.passLength", password.length(), passPolicy.getPassMinLength());
		}
		if (StringUtils.isNotBlank(password) && password.equalsIgnoreCase(userId)) {
			throw new ValidateException(FIELD_PSWD, "error.passwordMatchesUserID");
		}

		if (StringUtils.isNotBlank(password))
			internalCheckPass(passPolicy, password);
	}
	
	private void internalCheckPass(PassPolicy passPolicy, String password) {
		char[] charNum = password.toCharArray();
		int numLgth = charNum.length;
		for (int j = 0; j < numLgth; j++)
			if (Character.isWhitespace(charNum[j]))
				throw new ValidateException(FIELD_PSWD, "error.userPasswordWhiteSpace");

		for (int i = 0; i < password.length() - 2; i++) {
			if ((password.charAt(i) == password.charAt(i + 1)) && (password.charAt(i) == password.charAt(i + 2)))
				throw new ValidateException(FIELD_PSWD, "error.pass.consecutiveChars");
		}

		if (("Y".equals(passPolicy.getPassMustHaveNumber())) && !Utils.passwordContainsNumeric(password))
			throw new ValidateException(FIELD_PSWD, "error.passwordRequiresNumericCharacters");

		if (("Y".equals(passPolicy.getPassMustHaveSpecialChars())) && !Utils.passwordContainsSpecial(password, passPolicy.getPassSpecialChars()))
			throw new ValidateException(FIELD_PSWD, "error.passwordRequiresSpecialCharacters", passPolicy.getPassSpecialChars());

		if (("Y".equals(passPolicy.getPassMustHaveLowercase())) && !Utils.passwordContainsLowercase(password))
			throw new ValidateException(FIELD_PSWD, "error.passwordRequireLowercaseChars");

		if (("Y".equals(passPolicy.getPassMustHaveUppercase())) && !Utils.passwordContainsUppercase(password))
			throw new ValidateException(FIELD_PSWD, "error.passwordRequireUppercaseChars");
	}
	
	private void checkPassHistory(AppUser user, PassPolicy passPolicy) {
		List<PassHistory> histList = nomenclatureDao.getPasswordHistoryByUserId(user.getId());
		if (histList != null && !histList.isEmpty()) {
			PassHistory hist = histList.get(histList.size() - 1);
			if (hist.getChangedDt() != null) {
				java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());

				Calendar cal = Calendar.getInstance();
				cal.setTime(hist.getChangedDt());
				cal.add(Calendar.DATE, passPolicy.getPassMinPeriodInDays().intValue());

				java.sql.Date nextPassChangeAllowed = new java.sql.Date(cal.getTimeInMillis());

				if (nextPassChangeAllowed.after(currentDate)) {
					throw new ValidateException("error.passCanBeChangedToOften", nextPassChangeAllowed);
				}
			}
		}
	}
	
	private void validateAppUser(AppUser bean) {
		if (StringUtils.isNotBlank(bean.getUserEmail()) && !Utils.checkIsValidEmail(bean.getUserEmail())) {
			if (log.isDebugEnabled())
				log.debug("--> validateAppUser, Add Function Denied due to invalid email: " + bean.getUserEmail());
			throw new ValidateException("userEmail", "error.emailInvalid");
		}
	}

	private void updateAppUserAppRoles(AppUser bean, String[] assignedRoles) {
		AppUserAppRole b = new AppUserAppRole();
		b.setAppUserFk(bean.getId());
		SearchResults sd = nomenclatureDao.getAppUserAppRoleList(b);
		Map<Long, AppUserAppRole> map = new HashMap<Long, AppUserAppRole>();
		if (sd.isNotEmpty()) {
			for (Object o : sd.getResults()) {
				b = (AppUserAppRole) o;
				map.put(b.getAppRoleFk(), b);
			}
		}
		log.debug("map:" + map);

		if (assignedRoles != null && assignedRoles.length > 0) {
			for (String roleId : assignedRoles) {
				AppUserAppRole a = new AppUserAppRole();
				a.setAppRoleFk(Long.valueOf(roleId));
				a.setAppUserFk(bean.getId());

				if (map.containsKey(a.getAppRoleFk())) {
					// do nothing
					map.remove(a.getAppRoleFk());
				} else {

					commonDao.save(a);
				}
			}
		}
		for (AppUserAppRole a : map.values()) {
			commonDao.delete(a);
		}
	}

	private void checkPassword(AppUser bean, boolean isAdd) {
		AppUser dbUser = null;
		if (!isAdd) {
			dbUser = getAppUserById(bean.getId());
			commonDao.evict(dbUser);
		}

		boolean makeACheck = false;
		if (isAdd) {
			makeACheck = true;
		} else {
			if (StringUtils.isNotBlank(bean.getPassword()) || StringUtils.isNotBlank(bean.getRepeatPassword())) {
				makeACheck = true;
			} else {
				bean.setPasswordHash(dbUser.getPasswordHash());
			}
		}
		if (makeACheck) {

			if (StringUtils.isBlank(bean.getPassword()))
				throw new ValidateException(getMessage(messageSource, "error.required", "#label.newPassword"));

			if (StringUtils.isBlank(bean.getRepeatPassword()))
				throw new ValidateException(getMessage(messageSource, "error.required", "#label.newPasswordRepeat"));

			if (!bean.getPassword().equals(bean.getRepeatPassword()))
				throw new ValidateException(getMessage(messageSource, "error.passwordsNotSame"));

			bean.setPassword(bean.getPassword().trim());
			bean.setRepeatPassword(bean.getRepeatPassword().trim());

			if (dbUser != null) {
				// user name not changeable
				bean.setUsername(dbUser.getUsername());
				if (masterUserPasswordEncoder.matches(bean.getPassword(), dbUser.getPasswordHash()))
					throw new ValidateException(getMessage(messageSource, "error.newPasswordSameAsOld"));
			}

			if (bean.getPassword().toLowerCase().equals(bean.getUsername().toLowerCase().trim()))
				throw new ValidateException(getMessage(messageSource, "error.newPasswordSameAsUsername"));

			bean.setPasswordHash(masterUserPasswordEncoder.encode(bean.getPassword()));

		}

	}

	@Override
	@MasterLogAnnotation
	public SearchResults getAppUserList(AppUser bean) {
		return nomenclatureDao.getAppUserList(bean);
	}

	@Override
	@MasterLogAnnotation
	public void deleteAppUser(AppUser bean) {
		AppUser b = getAppUserById(bean.getId());
		if (b == null)
			throw new ValidateException("error.appUser.notFound", "" + bean.getId());
		if (!b.getVersion().equals(bean.getVersion()))
			throw new ValidateException("error.fieldUpdatedOrDeleted");
		nomenclatureDao.delete(b);
	}

	/**
	 * @see NomenclatureService#getAppRoleById(Long)
	 */
	@Override
	@MasterLogAnnotation
	public AppRole getAppRoleById(Long id) {
		return nomenclatureDao.getByPk(id, AppRole.class);
		// if (b != null && ContextHolder.isLoggedUserParticipant() && !ContextHolder.getLoggedParticipantData().getParticipantId().equals(b.getParticipantFk()))
		// b = null;
		// return b;
	}

	/**
	 * @see NomenclatureService#updateAppRole(Object)
	 */
	@Override
	@MasterLogAnnotation
	public void updateAppRole(AppRole bean, String[] assignedFunctions) {
		nomenclatureDao.update(bean);
		updateAppRoleAppFunction(bean, assignedFunctions);
	}

	/**
	 * @see NomenclatureService#saveAppRole(Object)
	 */
	@Override
	@MasterLogAnnotation
	public void addAppRole(AppRole bean, String[] assignedFunctions) {
		nomenclatureDao.save(bean);
		updateAppRoleAppFunction(bean, assignedFunctions);
	}

	private void updateAppRoleAppFunction(AppRole bean, String[] assignedFunctions) {
		AppRoleAppFunction b = new AppRoleAppFunction();
		b.setAppRoleFk(bean.getId());
		b.setItemsPerPage(Integer.MAX_VALUE);
		SearchResults sd = nomenclatureDao.getAppRoleAppFunctionList(b);
		Map<Long, AppRoleAppFunction> map = new HashMap<Long, AppRoleAppFunction>();
		if (sd.isNotEmpty()) {
			for (Object o : sd.getResults()) {
				b = (AppRoleAppFunction) o;
				map.put(b.getAppFunctionFk(), b);
			}
		}

		if (assignedFunctions != null && assignedFunctions.length > 0) {
			for (String functionId : assignedFunctions) {
				AppRoleAppFunction a = new AppRoleAppFunction();
				a.setAppFunctionFk(Long.valueOf(functionId));
				a.setAppRoleFk(bean.getId());

				if (map.containsKey(a.getAppFunctionFk())) {
					map.remove(a.getAppFunctionFk());
				} else {
					commonDao.save(a);
				}
			}
		}

		for (AppRoleAppFunction a : map.values()) {
			commonDao.delete(a);
		}
	}

	/**
	 * @see NomenclatureService#getAppRoleList(AppRole) {
	 */
	@Override
	@MasterLogAnnotation
	public SearchResults getAppRoleList(AppRole bean) {
		return nomenclatureDao.getAppRoleList(bean);
	}

	/**
	 * @see NomenclatureService#deleteAppRole(Object)
	 */
	@Override
	@MasterLogAnnotation
	public void deleteAppRole(AppRole bean) {
		AppRole b = getAppRoleById(bean.getId());
		if (b == null)
			throw new ValidateException("error.appRole.notFound", "" + bean.getId());
		if (b.getVersion() != null && !b.getVersion().equals(bean.getVersion()))
			throw new ValidateException("error.fieldUpdatedOrDeleted");
		
		nomenclatureDao.deleteAppRoleAppFunctionForRole(bean.getId());
		nomenclatureDao.delete(b);
	}

	@Override
	@MasterLogAnnotation
	public List<AppFunction> getAllAppFunctionAccess() {
		return nomenclatureDao.getAllAppFunctionAccess();
	}

	@Override
	@MasterLogAnnotation
	public List<AppFunction> getAssignedFunctionsForRole(Long appRoleFk) {
		return nomenclatureDao.getAssignedFunctionsForRole(appRoleFk);
	}

	@Override
	@MasterLogAnnotation
	public List<AppFunction> getNotAssignedFunctionsForRole(Long appRoleFk) {
		return nomenclatureDao.getNotAssignedFunctionsForRole(appRoleFk);
	}

	@Override
	@MasterLogAnnotation
	public List<AppRole> getAssignedRolesForUser(Long appUserFk) {
		return nomenclatureDao.getAssignedRolesForUser(appUserFk);
	}

	@Override
	@MasterLogAnnotation
	public List<AppRole> getNotAssignedRolesForUser(Long appUserFk) {
		return nomenclatureDao.getNotAssignedRolesForUser(appUserFk);
	}

	@Override
	@MasterLogAnnotation
	public List<AppRole> getAllAppRoles() {
		return nomenclatureDao.getAllAppRoles();
	}

	@Override
	public List<AppFunction> getAppFunctionAccessByUser(Long id) {
		return nomenclatureDao.getAppFunctionAccessByUser(id);
	}

	/**
	 * @see NomenclatureService#getParticipantById(Long)
	 */
	@Override
	@MasterLogAnnotation
	public Participant getParticipantById(Long id) {
		return nomenclatureDao.getByPk(id, Participant.class);
		// if (b != null && ContextHolder.isLoggedUserParticipant() && !ContextHolder.getLoggedParticipantData().getParticipantId().equals(b.getParticipantFk()))
		// b = null;
		// return b;
	}

	/**
	 * @see NomenclatureService#updateParticipant(Object)
	 */
	@Override
	@MasterLogAnnotation
	public void updateParticipant(Participant bean) {
		nomenclatureDao.update(bean);
	}

	/**
	 * @see NomenclatureService#saveParticipant(Object)
	 */
	@Override
	@MasterLogAnnotation
	public void addParticipant(Participant bean) {
		nomenclatureDao.save(bean);
	}

	/**
	 * @see NomenclatureService#getParticipantList(Participant) {
	 */
	@Override
	@MasterLogAnnotation
	public SearchResults getParticipantList(Participant bean) {
		return nomenclatureDao.getParticipantList(bean);
	}

	/**
	 * @see NomenclatureService#deleteParticipant(Object)
	 */
	@Override
	@MasterLogAnnotation
	public void deleteParticipant(Participant bean) {
		Participant b = getParticipantById(bean.getId());
		if (b == null)
			throw new ValidateException("error.participant.notFound", "" + bean.getId());
		if (!b.getVersion().equals(bean.getVersion()))
			throw new ValidateException("error.fieldUpdatedOrDeleted");
		// nomenclatureDao.preDeleteParticipantCheck(b);
		nomenclatureDao.delete(b);
	}

	/**
	 * @see NomenclatureService#getPassPolicyById(Long)
	 */
	@Override
	@MasterLogAnnotation
	public PassPolicy getPassPolicyById(Long id) {
		PassPolicy b = nomenclatureDao.getByPk(id, PassPolicy.class);
		if (b != null && ContextHolder.isLoggedUserParticipant() && !ContextHolder.getLoggedParticipantData().getParticipantId().equals(b.getParticipantFk()))
			b = null;
		return b;
	}

	/**
	 * @see NomenclatureService#updatePassPolicy(Object)
	 */
	@Override
	@MasterLogAnnotation
	public void updatePassPolicy(PassPolicy bean) {
		if (ContextHolder.isLoggedUserParticipant())
			bean.setParticipantFk(ContextHolder.getLoggedParticipantData().getParticipantId());
		validatePassPolicy(bean);
		bean.setChangedByAppUserFk(ContextHolder.getLoggedUser().getId());
		bean.setChangedDt(new Timestamp(Calendar.getInstance().getTimeInMillis()));
		nomenclatureDao.update(bean);
	}

	/**
	 * @see NomenclatureService#savePassPolicy(Object)
	 */
	@Override
	@MasterLogAnnotation
	public void addPassPolicy(PassPolicy bean) {
		if (ContextHolder.isLoggedUserParticipant())
			bean.setParticipantFk(ContextHolder.getLoggedParticipantData().getParticipantId());
		validatePassPolicy(bean);
		bean.setChangedByAppUserFk(ContextHolder.getLoggedUser().getId());
		bean.setChangedDt(new Timestamp(Calendar.getInstance().getTimeInMillis()));
		nomenclatureDao.save(bean);
	}

	private void validatePassPolicy(PassPolicy bean) {
		if (bean.getPassMinPeriodInDays() != null && bean.getPassMaxPeriodInDays() != null && bean.getPassMinPeriodInDays() >= bean.getPassMaxPeriodInDays()) {
			throw new ValidateException("error.passPolicyMaxMinPeriodDays");
		}
	}

	/**
	 * @see NomenclatureService#getPassPolicyList(PassPolicy) {
	 */
	@Override
	@MasterLogAnnotation
	public SearchResults getPassPolicyList(PassPolicy bean) {
		return nomenclatureDao.getPassPolicyList(bean);
	}

	/**
	 * @see NomenclatureService#deletePassPolicy(Object)
	 */
	@Override
	@MasterLogAnnotation
	public void deletePassPolicy(PassPolicy bean) {
		PassPolicy b = getPassPolicyById(bean.getId());
		if (b == null)
			throw new ValidateException("error.passPolicy.notFound", "" + bean.getId());
		if (!b.getVersion().equals(bean.getVersion()))
			throw new ValidateException("error.fieldUpdatedOrDeleted");
		nomenclatureDao.preDeletePassPolicyCheck(b);
		nomenclatureDao.delete(b);
	}
}