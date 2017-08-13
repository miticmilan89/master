package rs.milanmitic.master.controller;

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
import rs.milanmitic.master.common.pagging.SearchResults;
import rs.milanmitic.master.common.util.Utils;
import rs.milanmitic.master.model.Participant;
import rs.milanmitic.master.service.NomenclatureService;

/**
 * Handling Participant CRUD operations
 * 
 * @author milanmitic
 */
@Controller
@RequestMapping(value = "/admin/participant")
public class ParticipantController extends BasicController {

	private static final String SAVE_PAGE = "participantSave";
	private static final String BEAN_KEY = "participant";
	private static final String URL_LIST = "redirect:/app/admin/participant/list";
	private static final String URL_VIEW = "redirect:/app/admin/participant/view/";

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
	private void preparePageData(Model model, boolean isAdd, Participant bean, boolean isList) {
		// for future use
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
	public String addParticipant(HttpServletRequest request, Model model) {
		try {
			Participant b = new Participant();
			model.addAttribute(BEAN_KEY, b);
			model.addAttribute(Constants.ACTION, Constants.ACTION_ADD);
			preparePageData(model, true, null, false);
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
	@RequestMapping(value = "/view/{participantId}", method = RequestMethod.GET)
	public String viewParticipant(HttpServletRequest request, Model model, @PathVariable("participantId") Long id) {
		try {
			Participant bean = nomenclatureService.getParticipantById(id);
			if (bean == null) {
				addError(request.getSession(), Constants.LABEL_MESSAGE_RECORD_NOT_FOUND, id != null ? id.toString() : "null");
				return URL_LIST;
			}
			model.addAttribute(BEAN_KEY, bean);
			preparePageData(model, false, bean, false);

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
	public String saveParticipant(HttpServletRequest request, Model model, @ModelAttribute(BEAN_KEY) @Valid Participant bean, BindingResult result) {
		try {
			String action = request.getParameter(Constants.ACTION);
			model.addAttribute(BEAN_KEY, bean);
			if (StringUtils.isNotBlank(action))
				model.addAttribute(Constants.ACTION, action);
			if (result.hasErrors())
				addError(request, result.getAllErrors());

			boolean isAdd = Constants.ACTION_ADD.equals(action);
			preparePageData(model, isAdd, bean, false);
			if (!hasErrors(request)) {
				if (!isAdd) {
					checkSecureHiddenFields(bean, request);
					ContextHolder.createUserTransactionLog(request);
					nomenclatureService.updateParticipant(bean);
					addMessage(request.getSession(), Constants.LABEL_MESSAGE_RECORD_UPDATE);
				} else {
					ContextHolder.createUserTransactionLog(request);
					nomenclatureService.addParticipant(bean);
					addMessage(request.getSession(), Constants.LABEL_MESSAGE_RECORD_INSERT);
				}
				return URL_VIEW + bean.getId();
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
	 * @param bean
	 * @param result
	 * @param model
	 * @return
	 */
	@MasterLogAnnotation
	@RequestMapping(value = "/list")
	public String participantList(HttpServletRequest request, Model model, Participant bean, BindingResult result) {
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
				SearchResults sd = nomenclatureService.getParticipantList(bean);
				if (sd.isEmpty())
					addMessage(request, "error.noRecordsFound");
				else
					model.addAttribute("searchResults", sd);
			}
		} catch (Exception t) {
			handleError(request, t);
		}
		return "participantList";
	}

	/**
	 * Delete existing record
	 * 
	 * @param request
	 * @param model
	 * @param participant
	 * @return
	 */
	@MasterLogAnnotation
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String deleteParticipant(HttpServletRequest request, Model model, Participant bean, BindingResult result) {
		try {
			nomenclatureService.deleteParticipant(bean);
			addMessage(request.getSession(), "message.recordDelete");
			return URL_LIST;
		} catch (Exception t) {
			handleError(request.getSession(), t);
		}
		return URL_VIEW + bean.getId();
	}

}