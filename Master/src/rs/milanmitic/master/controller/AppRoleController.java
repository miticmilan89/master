package rs.milanmitic.master.controller;

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

import rs.milanmitic.master.common.Constants;
import rs.milanmitic.master.common.ContextHolder;
import rs.milanmitic.master.common.aop.MasterLogAnnotation;
import rs.milanmitic.master.common.exception.ValidateException;
import rs.milanmitic.master.common.pagging.SearchResults;
import rs.milanmitic.master.common.util.Utils;
import rs.milanmitic.master.model.AppFunction;
import rs.milanmitic.master.model.AppRole;
import rs.milanmitic.master.model.Participant;
import rs.milanmitic.master.service.NomenclatureService;

/**
 * Handling AppRole CRUD operations
 * 
 * @author milan
 */
@Controller
@RequestMapping(value = "/appRole")
public class AppRoleController extends BasicController {

	private static final String SAVE_PAGE = "appRoleSave";
	private static final String BEAN_KEY = "appRole";
	private static final String URL_LIST = "redirect:/app/appRole/list";
	private static final String URL_VIEW = "redirect:/app/appRole/view/";

	@Autowired
	private NomenclatureService nomenclatureService;

	/**
	 * Prepare additional data to show on page
	 * 
	 * @param model
	 * @param isAdd
	 * @param appRole
	 * @param isList
	 */
	private void preparePageData(Model model, boolean isAdd, AppRole bean, boolean isList) {
//		if (ContextHolder.getLoggedUser().isAdmin())
			model.addAttribute("participantList", nomenclatureService.getParticipantList(null).getResults(Participant.class));
	}

	private void prepareRoleFunctions(Model model, Long appRoleFk) {
		// In case when we add new role we need to show just not assigned
		// functions
		if (appRoleFk != null) {
			// read assigned functions and not assigned functions
			List<AppFunction> assignedFunctions = nomenclatureService.getAssignedFunctionsForRole(appRoleFk);
			List<AppFunction> notAssignedFunctions = nomenclatureService.getNotAssignedFunctionsForRole(appRoleFk);

			model.addAttribute("assignedFunctions", assignedFunctions);
			model.addAttribute("notAssignedFunctions", notAssignedFunctions);
		} else {
			List<AppFunction> notAssignedFunctions = nomenclatureService.getAllAppFunctionAccess();
			model.addAttribute("notAssignedFunctions", notAssignedFunctions);
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
	public String addAppRole(HttpServletRequest request, Model model) {
		try {
			AppRole b = new AppRole();
			model.addAttribute(BEAN_KEY, b);
			model.addAttribute(Constants.ACTION, Constants.ACTION_ADD);
			preparePageData(model, true, null, false);
			prepareRoleFunctions(model, null);
		} catch (Exception t) {
			handleError(request, t);
		}
		return SAVE_PAGE;
	}

	/**
	 * View existing record
	 * 
	 * @param request
	 * @param model
	 * @param id
	 * @return
	 */
	@MasterLogAnnotation
	@RequestMapping(value = "/view/{appRoleId}", method = RequestMethod.GET)
	public String viewAppRole(HttpServletRequest request, Model model, @PathVariable("appRoleId") Long id) {
		try {
			AppRole bean = nomenclatureService.getAppRoleById(id);
			if (bean == null)
				return "redirect:/app/appRole/list";

			model.addAttribute(BEAN_KEY, bean);
			preparePageData(model, false, bean, false);
			prepareRoleFunctions(model, id);

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
	 * @param bean
	 * @param result
	 * @return
	 */
	@MasterLogAnnotation
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String saveAppRole(HttpServletRequest request, Model model, @ModelAttribute(BEAN_KEY) @Valid AppRole bean, BindingResult result) {
		try {
			String action = request.getParameter(Constants.ACTION);
			model.addAttribute(BEAN_KEY, bean);
			if (StringUtils.isNotBlank(action))
				model.addAttribute(Constants.ACTION, action);
			if (result.hasErrors())
				addError(request, result.getAllErrors());

			boolean isAdd = Constants.ACTION_ADD.equals(action);
			preparePageData(model, isAdd, bean, false);
			prepareRoleFunctions(model, bean.getId());

			List<AppFunction> list = nomenclatureService.getAllAppFunctionAccess();
			String[] assignedFunctions = request.getParameterValues("assignedFunctions");

			checkFunctions(list, assignedFunctions);

			if (ContextHolder.getLoggedUser().isAdmin()) {
				Utils.checkExistsValueInList(bean.getParticipantFk(), model.asMap().get("participantList"), request, result, "participantFk", messageSource);
			} else {
				bean.setParticipantFk(ContextHolder.getLoggedParticipantData().getParticipantId());
			}

			if (!hasErrors(request)) {
				if (!isAdd) {
					checkSecureHiddenFields(bean, request);
					ContextHolder.createUserTransactionLog(request);
					nomenclatureService.updateAppRole(bean, assignedFunctions);
					addMessage(request.getSession(), "message.recordUpdate");
				} else {
					ContextHolder.createUserTransactionLog(request);
					nomenclatureService.addAppRole(bean, assignedFunctions);
					addMessage(request.getSession(), "message.recordInsert");
				}
				return URL_VIEW + bean.getId();
			}
		} catch (Exception t) {
			handleError(request, t, result);
		}
		return SAVE_PAGE;
	}

	private void checkFunctions(List<AppFunction> list, String[] assignedFunctions) {
		if (assignedFunctions == null)
			return;
		for (String functionId : assignedFunctions) {
			boolean exists = false;
			if (list == null || list.isEmpty())
				continue;
			for (AppFunction appFunction : list) {
				if (appFunction.getId().toString().equals(functionId)) {
					exists = true;
					break;
				}
			}
			if (!exists) {
				throw new ValidateException("error.invalidSecureHiddenFields");
			}
		}
	}

	/**
	 * Search database with search criteria
	 * 
	 * @param request
	 * @param bean
	 * @param result
	 * @param model
	 * @return
	 */
	@MasterLogAnnotation
	@RequestMapping(value = "/list")
	public String appRoleList(HttpServletRequest request, Model model, AppRole bean, BindingResult result) {
		try {
			preparePageData(model, false, null, true);
			// check only fields with Length annotation
			Utils.checkAnnotationLength(bean, result);

			if (result.hasErrors())
				addError(request, result.getAllErrors());
			else {
				bean = isRequestFromMenu(request, bean);
				model.addAttribute(BEAN_KEY, bean);

				if (StringUtils.isBlank(bean.getOrderColumn()))
					bean.setOrderColumn("1");
				if (bean.getOrderAsc() == null)
					bean.setOrderAsc(Boolean.TRUE);
				SearchResults sd = nomenclatureService.getAppRoleList(bean);
				if (sd.isEmpty())
					addMessage(request, "error.noRecordsFound");
				else
					model.addAttribute("searchResults", sd);
			}
		} catch (Exception t) {
			handleError(request, t);
		}
		return "appRoleList";
	}

	/**
	 * Delete existing record
	 * 
	 * @param request
	 * @param model
	 * @param appRole
	 * @return
	 */
	@MasterLogAnnotation
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String deleteAppRole(HttpServletRequest request, Model model, AppRole bean, BindingResult result) {
		try {
			nomenclatureService.deleteAppRole(bean);
			addMessage(request.getSession(), "message.recordDelete");
			return URL_LIST;
		} catch (Exception t) {
			handleError(request.getSession(), t);
		}
		return URL_VIEW + bean.getId();
	}

}