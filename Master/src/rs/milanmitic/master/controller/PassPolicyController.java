package rs.milanmitic.master.controller;

import java.sql.Timestamp;
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

import rs.milanmitic.master.common.Constants;
import rs.milanmitic.master.common.ContextHolder;
import rs.milanmitic.master.common.SelectFieldIntf;
import rs.milanmitic.master.common.aop.MasterLogAnnotation;
import rs.milanmitic.master.common.data.LabelValue;
import rs.milanmitic.master.common.exception.ValidateException;
import rs.milanmitic.master.common.pagging.SearchResults;
import rs.milanmitic.master.common.util.Utils;
import rs.milanmitic.master.model.Participant;
import rs.milanmitic.master.model.PassPolicy;
import rs.milanmitic.master.service.NomenclatureService;

/**
 * Handling PassPolicy CRUD operations
 * 
 * @author milan
 */
@Controller
@RequestMapping(value = "/passPolicy")
public class PassPolicyController extends BasicController {

	private static final String BEAN_KEY = "passPolicy";
	private static final String SAVE_PAGE = "passPolicySave";

	@Autowired
	private NomenclatureService nomenclatureService;

	private void preparePassPolicyData(Model model, PassPolicy passPolicy) {
		model.addAttribute("yesNoSelect", getYesNoStringSelect());

		if (ContextHolder.getLoggedUser().isAdmin()) {
			SearchResults list = nomenclatureService.getParticipantList(null);
			List<SelectFieldIntf> participantList = new ArrayList<SelectFieldIntf>();
			participantList.add(new LabelValue("", ""));
			participantList.addAll(list.getResults(Participant.class));
			model.addAttribute("participantList", participantList);
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
	public String addPassPolicy(HttpServletRequest request, Model model) {
		try {
			PassPolicy passPolicy = new PassPolicy();
			model.addAttribute(BEAN_KEY, passPolicy);
			model.addAttribute(Constants.ACTION, Constants.ACTION_ADD);
			passPolicy.setPassMustHaveLowercase("N");
			passPolicy.setPassMustHaveNumber("N");
			passPolicy.setPassMustHaveUppercase("N");
			passPolicy.setPassMustHaveSpecialChars("N");
			passPolicy.setPassUnblockAutomatically("N");
			preparePassPolicyData(model, null);
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
	 * @param instid
	 * @return
	 */
	@MasterLogAnnotation
	@RequestMapping(value = "/view/{passPolicyFk}", method = RequestMethod.GET)
	public String viewPassPolicy(HttpServletRequest request, Model model, @PathVariable Long passPolicyFk) {
		try {
			PassPolicy passPolicy = nomenclatureService.getPassPolicyById(passPolicyFk);
			if (passPolicy == null) {
				addError(request.getSession(), Constants.LABEL_MESSAGE_RECORD_NOT_FOUND, "" + passPolicyFk);
				return "redirect:/app/passPolicy/list";
			}
			model.addAttribute(BEAN_KEY, passPolicy);
			preparePassPolicyData(model, passPolicy);
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
	 * @param passPolicy
	 * @param result
	 * @return
	 */
	@MasterLogAnnotation
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String savePassPolicy(HttpServletRequest request, Model model, @ModelAttribute(BEAN_KEY) @Valid PassPolicy passPolicy, BindingResult result) {
		try {
			String action = request.getParameter(Constants.ACTION);
			// passPolicy.setParticipantFk(ContextHolder.getLoggedParticipantData().getParticipantId())
			
			preparePassPolicyData(model, passPolicy);
			
			if (ContextHolder.getLoggedUser().isAdmin() && passPolicy.getParticipantFk() != null)
				Utils.checkExistsValueInList(passPolicy.getParticipantFk(), model.asMap().get("participantList"), request, result, "participantFk", messageSource);
			Utils.checkExistsValueInList(passPolicy.getPassMustHaveLowercase(), model.asMap().get("yesNoSelect"), request, result, "passMustHaveLowercase", messageSource);
			Utils.checkExistsValueInList(passPolicy.getPassMustHaveNumber(), model.asMap().get("yesNoSelect"), request, result, "passMustHaveNumber", messageSource);
			Utils.checkExistsValueInList(passPolicy.getPassMustHaveUppercase(), model.asMap().get("yesNoSelect"), request, result, "passMustHaveUppercase", messageSource);
			Utils.checkExistsValueInList(passPolicy.getPassMustHaveSpecialChars(), model.asMap().get("yesNoSelect"), request, result, "passMustHaveSpecialChars", messageSource);
			Utils.checkExistsValueInList(passPolicy.getPassUnblockAutomatically(), model.asMap().get("yesNoSelect"), request, result, "passUnblockAutomatically", messageSource);
			
			passPolicy.setChangedDt(new Timestamp(System.currentTimeMillis()));
			model.addAttribute(BEAN_KEY, passPolicy);
			if (StringUtils.isNotBlank(action))
				model.addAttribute(Constants.ACTION, action);
			if (result.hasErrors())
				addError(request, result.getAllErrors());

			boolean isAdd = Constants.ACTION_ADD.equals(action);
			if (!hasErrors(request)) {
				if (!isAdd) {
					try {
						checkSecureHiddenFields(passPolicy, request);
					} catch (ValidateException t) {
						addError(request.getSession(), t.getMessage());
						return "redirect:/app/home";
					}
					ContextHolder.createUserTransactionLog(request);
					nomenclatureService.updatePassPolicy(passPolicy);
					addMessage(request.getSession(), Constants.LABEL_MESSAGE_RECORD_UPDATE);
				} else {
					ContextHolder.createUserTransactionLog(request);
					nomenclatureService.addPassPolicy(passPolicy);
					addMessage(request.getSession(), Constants.LABEL_MESSAGE_RECORD_INSERT);
				}
				return "redirect:/app/passPolicy/view/" + passPolicy.getId();
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
	 * @param passPolicy
	 * @param result
	 * @param model
	 * @return
	 */
	@MasterLogAnnotation
	@RequestMapping(value = "/list")
	public String passPolicyList(HttpServletRequest request, Model model, PassPolicy passPolicy, BindingResult result) {
		try {
			preparePassPolicyData(model, null);
			// check only fields with Length annotation
			Utils.checkAnnotationLength(passPolicy, result);

			if (result.hasErrors())
				addError(request, result.getAllErrors());
			else {
				passPolicy = isRequestFromMenu(request, passPolicy);
				model.addAttribute(BEAN_KEY, passPolicy);

				if (StringUtils.isBlank(passPolicy.getOrderColumn()))
					passPolicy.setOrderColumn("1");
				if (passPolicy.getOrderAsc() == null)
					passPolicy.setOrderAsc(Boolean.TRUE);
				SearchResults sd = nomenclatureService.getPassPolicyList(passPolicy);
				if (sd.isEmpty())
					addMessage(request, "error.noRecordsFound");
				else
					model.addAttribute("searchResults", sd);
			}
		} catch (Exception t) {
			handleError(request, t);
		}
		return "passPolicyList";
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
	public String deletePassPolicy(HttpServletRequest request, Model model, PassPolicy passPolicy, BindingResult result) {
		try {
			nomenclatureService.deletePassPolicy(passPolicy);
			addMessage(request.getSession(), "message.recordDelete");
			return "redirect:/app/passPolicy/list";
		} catch (Exception t) {
			handleError(request.getSession(), t);
		}
		return "redirect:/app/passPolicy/view/" + passPolicy.getId();
	}

}
