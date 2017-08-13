package rs.milanmitic.master.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import rs.milanmitic.master.common.Constants;
import rs.milanmitic.master.common.ContextHolder;
import rs.milanmitic.master.common.SelectFieldIntf;
import rs.milanmitic.master.common.aop.MasterLogAnnotation;
import rs.milanmitic.master.common.data.LabelValue;
import rs.milanmitic.master.common.exception.ValidateException;
import rs.milanmitic.master.common.pagging.SearchResults;
import rs.milanmitic.master.common.security.UserType;
import rs.milanmitic.master.common.util.Utils;
import rs.milanmitic.master.model.AppRole;
import rs.milanmitic.master.model.AppUser;
import rs.milanmitic.master.model.Participant;
import rs.milanmitic.master.model.PassPolicy;
import rs.milanmitic.master.service.NomenclatureService;

/**
 * Handling BANK CRUD
 * 
 * @author milan
 */
@Controller
@RequestMapping(value = "/appUser")
public class AppUserController extends BasicController {

	private static final String BEAN_KEY = "appUser";
	private static final String SAVE_PAGE = "appUserSave";

	@Autowired
	private NomenclatureService nomenclatureService;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void prepareAppUserData(Model model, AppUser appUser) {

		model.addAttribute("userStatusList", getUserStatusList());

		List<SelectFieldIntf> list = new ArrayList<SelectFieldIntf>();
		list.add(new LabelValue(getMessage("label.appUser.userType." + UserType.ADMIN.getId()), UserType.ADMIN.getDbId()));
		list.add(new LabelValue(getMessage("label.appUser.userType." + UserType.USER.getId()), UserType.USER.getDbId()));
		model.addAttribute("userTypeList", list);

		if (ContextHolder.getLoggedUser().isAdmin())
			model.addAttribute("participantList", nomenclatureService.getParticipantList(null).getResults(Participant.class));

		PassPolicy bean = new PassPolicy();
		bean.setItemsPerPage(Integer.MAX_VALUE);
		if (appUser != null && appUser.getParticipantFk() != null) {
			bean.setParticipantFk(appUser.getParticipantFk());
		} else {
			if (ContextHolder.getLoggedUser().isAdmin())
				bean.setForAdmin(true);
		}
		List pplist = nomenclatureService.getPassPolicyList(bean).getResults();
		pplist.add(0, new LabelValue("", ""));
		model.addAttribute("passPolicyList", pplist);

	}

	public List<SelectFieldIntf> getUserStatusList() {
		List<SelectFieldIntf> yesno = new ArrayList<SelectFieldIntf>();
		yesno.add(new LabelValue(getMessage("label.appUser.status." + AppUser.STATUS_ACTIVE), "" + AppUser.STATUS_ACTIVE));
		yesno.add(new LabelValue(getMessage("label.appUser.status." + AppUser.STATUS_NOT_ACTIVE), "" + AppUser.STATUS_NOT_ACTIVE));
		return yesno;
	}

	private void prepareUserRoles(Model model, Long appUserFk, Long participantId) {
		// In case when we add new user we need to show just not assigned roles
		if (appUserFk != null) {
			// read assigned roles and not assigned roles
			List<AppRole> assignedRoles = nomenclatureService.getAssignedRolesForUser(appUserFk);
			List<AppRole> notAssignedRoles = nomenclatureService.getNotAssignedRolesForUser(appUserFk);

			model.addAttribute("assignedRoles", assignedRoles);
			model.addAttribute("notAssignedRoles", notAssignedRoles);
		} else {
			if (participantId == null) {
				List<AppRole> notAssignedRoles = nomenclatureService.getAllAppRoles();
				model.addAttribute("notAssignedRoles", notAssignedRoles);
			} else {
				AppRole bean = new AppRole();
				bean.setParticipantFk(participantId);
				SearchResults notAssignedRoles = nomenclatureService.getAppRoleList(bean);
				model.addAttribute("notAssignedRoles", notAssignedRoles.getResults());
			}
		}
	}

	/**
	 * Add new record to DB
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@MasterLogAnnotation
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String addAppUser(HttpServletRequest request, Model model) {
		try {
			AppUser d = new AppUser();
			model.addAttribute(BEAN_KEY, d);
			model.addAttribute(Constants.ACTION, Constants.ACTION_ADD);
			prepareAppUserData(model, d);
			prepareUserRoles(model, null, null);
			if (ContextHolder.getLoggedUser().isAdmin())
				model.addAttribute("hideRoles", true);
		} catch (Exception t) {
			handleError(request, t);
		}
		return SAVE_PAGE;
	}

	@RequestMapping(value = "/roles/{participantFk}", method = RequestMethod.GET)
	public String getAppUserRoles(HttpServletRequest request, Model model, @PathVariable Long participantFk) {
		try {
			AppRole bean = new AppRole();
			bean.setParticipantFk(participantFk);
			SearchResults notAssignedRoles = nomenclatureService.getAppRoleList(bean);
			model.addAttribute("notAssignedRoles", notAssignedRoles.getResults());
		} catch (Exception t) {
			handleError(request, t);
		}
		return "appUserSaveRole";
	}

	/**
	 * View existing record
	 * 
	 * @param request
	 * @param model
	 * @param instid
	 * @return
	 */
	@MasterLogAnnotation
	@RequestMapping(value = "/view/{appUserId}", method = RequestMethod.GET)
	public String viewAppUser(HttpServletRequest request, Model model, @PathVariable Long appUserId) {
		try {
			AppUser appUser = nomenclatureService.getAppUserById(appUserId);
			if (appUser == null)
				return "redirect:/app/appUser/list";

			model.addAttribute("appUser", appUser);
			prepareAppUserData(model, appUser);
			prepareUserRoles(model, appUserId, appUser.getParticipantFk());
			
			if (appUser.getParticipantFk() == null)
				model.addAttribute("hideRoles", true);
			
		} catch (Exception t) {
			handleError(request, t);
		}
		return SAVE_PAGE;
	}

	/**
	 * Add or update record in DB
	 * 
	 * @param request
	 * @param model
	 * @param appUser
	 * @param result
	 * @return
	 */
	@MasterLogAnnotation
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String saveAppUser(HttpServletRequest request, Model model, @ModelAttribute(BEAN_KEY) @Valid AppUser appUser, BindingResult result) {
		try {
			String action = request.getParameter(Constants.ACTION);
			model.addAttribute(BEAN_KEY, appUser);
			if (StringUtils.isNotBlank(action))
				model.addAttribute(Constants.ACTION, action);
			if (result.hasErrors())
				addError(request, result.getAllErrors());
			boolean isAdd = Constants.ACTION_ADD.equals(action);
			prepareAppUserData(model, appUser);
			prepareUserRoles(model, appUser.getId(), appUser.getParticipantFk());

			String[] assignedRoles = prepareAssignedRoles(request);
			
			if (ContextHolder.getLoggedUser().isUser())
				appUser.setParticipantFk(ContextHolder.getLoggedParticipantData().getParticipantId());
			
			if (appUser.getParticipantFk() == null)
				model.addAttribute("hideRoles", true);

			Utils.checkExistsValueInList(appUser.getUserType(), model.asMap().get("userTypeList"), request, result, "userType", messageSource);
			Utils.checkExistsValueInList(appUser.getStatus(), model.asMap().get("userStatusList"), request, result, "status", messageSource);
			if (appUser.getPassPolicyFk() != null)
				Utils.checkExistsValueInList(appUser.getPassPolicyFk(), model.asMap().get("passPolicyList"), request, result, "passPolicyFk", messageSource);
			
			if (ContextHolder.getLoggedUser().isAdmin() && UserType.USER.getDbId().equals(appUser.getUserType()))
				Utils.checkExistsValueInList(appUser.getParticipantFk(), model.asMap().get("participantList"), request, result, "participantFk", messageSource);
			
			if (!hasErrors(request)) {
				if (!isAdd) {
					checkSecureHiddenFields(appUser, request);
					ContextHolder.createUserTransactionLog(request);
					nomenclatureService.updateAppUser(appUser, assignedRoles, true);
					addMessage(request.getSession(), "message.recordUpdate");
				} else {
					ContextHolder.createUserTransactionLog(request);
					nomenclatureService.addAppUser(appUser, assignedRoles);
					addMessage(request.getSession(), "message.recordInsert");
				}
				return "redirect:/app/appUser/view/" + appUser.getId();
			}
		} catch (Exception t) {
			handleError(request, t, result);
		}
		return SAVE_PAGE;
	}

	/**
	 * Search database with search criteria
	 * 
	 * @param request
	 * @param appUser
	 * @param result
	 * @param model
	 * @return
	 */
	@MasterLogAnnotation
	@RequestMapping(value = "/list")
	public String appUserList(HttpServletRequest request, Model model, AppUser appUser, BindingResult result) {
		try {
			// check only fields with Length annotation
			Utils.checkAnnotationLength(appUser, result);

			if (result.hasErrors())
				addError(request, result.getAllErrors());
			else {
				appUser = isRequestFromMenu(request, appUser);
				model.addAttribute(BEAN_KEY, appUser);

				if (StringUtils.isBlank(appUser.getOrderColumn()))
					appUser.setOrderColumn("1");
				if (appUser.getOrderAsc() == null)
					appUser.setOrderAsc(Boolean.TRUE);
				SearchResults sd = nomenclatureService.getAppUserList(appUser);
				if (sd.isEmpty())
					addMessage(request, "error.noRecordsFound");
				else
					model.addAttribute("searchResults", sd);
			}
		} catch (Exception t) {
			handleError(request, t);
		}
		return "appUserList";
	}

	/**
	 * Delete existing record
	 * 
	 * @param request
	 * @param model
	 * @param instid
	 * @return
	 */
	@MasterLogAnnotation
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String deleteAppUser(HttpServletRequest request, Model model, AppUser appUser, BindingResult result) {
		try {
			ContextHolder.createUserTransactionLog(request);
			nomenclatureService.deleteAppUser(appUser);
			addMessage(request.getSession(), "message.recordDelete");
			return "redirect:/app/appUser/list";
		} catch (Exception t) {
			handleError(request.getSession(), t);
		}
		return "redirect:/app/appUser/view/" + appUser.getId();
	}

	private String[] prepareAssignedRoles(HttpServletRequest request) {
		List<AppRole> list = nomenclatureService.getAllAppRoles();

		String[] assignedRoles = request.getParameterValues("assignedRoles");

		if (assignedRoles == null)
			return assignedRoles;

		for (String roleId : assignedRoles) {
			boolean exists = false;
			for (AppRole appRole : list) {
				if (appRole.getId().toString().equals(roleId)) {
					exists = true;
					break;
				}
			}
			if (!exists) {
				throw new ValidateException("error.invalidSecureHiddenFields");
			}
		}
		return assignedRoles;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@MasterLogAnnotation
	@RequestMapping(value = "/view/passPolicy", method = RequestMethod.GET)
	@ResponseBody
	public Object getAppUserPassPolicy(HttpServletRequest request, Model model) {
		try {
			PassPolicy bean = new PassPolicy();
			if (ContextHolder.getLoggedUser().isAdmin())
				bean.setForAdmin(true);
			List pplist = nomenclatureService.getPassPolicyList(bean).getResults();
			pplist.add(0, new LabelValue("", ""));
			return pplist;
		} catch (Exception t) {
			handleError(request, t);
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@MasterLogAnnotation
	@RequestMapping(value = "/view/passPolicy/{participantId}", method = RequestMethod.GET)
	@ResponseBody
	public Object getAppUserPassPolicy(HttpServletRequest request, Model model, @PathVariable Long participantId) {
		try {
			PassPolicy bean = new PassPolicy();
			bean.setParticipantFk(participantId);
			List pplist = nomenclatureService.getPassPolicyList(bean).getResults();
			pplist.add(0, new LabelValue("", ""));
			return pplist;
		} catch (Exception t) {
			handleError(request, t);
		}
		return null;
	}
}
