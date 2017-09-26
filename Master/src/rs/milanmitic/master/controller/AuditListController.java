package rs.milanmitic.master.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import rs.milanmitic.master.common.SelectFieldIntf;
import rs.milanmitic.master.common.aop.MasterLogAnnotation;
import rs.milanmitic.master.common.data.LabelValue;
import rs.milanmitic.master.common.pagging.SearchResults;
import rs.milanmitic.master.common.util.Utils;
import rs.milanmitic.master.model.AuditLog;
import rs.milanmitic.master.service.CommonService;

/**
 * Handling BANK CRUD
 * 
 * @author milan
 */
@Controller
@RequestMapping(value = "/auditLog")
public class AuditListController extends BasicController {

	@Autowired
	private CommonService commonService;

	/**
	 * View existing record
	 * 
	 * @param request
	 * @param model
	 * @param instid
	 * @return
	 */
	@MasterLogAnnotation
	@RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
	public String viewAuditLog(HttpServletRequest request, Model model, @PathVariable Long id) {
		try {
			return "redirect:/app/auditLog/list";
		} catch (Exception t) {
			handleError(request, t);
		}
		return "auditLogView";
	}

	public List<SelectFieldIntf> getStatusList() {
		List<SelectFieldIntf> yesno = new ArrayList<SelectFieldIntf>();
		yesno.add(new LabelValue("", ""));
		yesno.add(new LabelValue(getMessage("label.auditLog.auditStatus." + AuditLog.TRANSSTATUS_SUCCESS), "" + AuditLog.TRANSSTATUS_SUCCESS));
		yesno.add(new LabelValue(getMessage("label.auditLog.auditStatus." + AuditLog.TRANSSTATUS_NOT_SUCCESS), "" + AuditLog.TRANSSTATUS_NOT_SUCCESS));
		return yesno;
	}

	/**
	 * Search database with search criteria
	 * 
	 * @param request
	 * @param auditLog
	 * @param result
	 * @param model
	 * @return
	 */
	@MasterLogAnnotation
	@RequestMapping(value = "/list")
	public String auditLogList(HttpServletRequest request, Model model, @ModelAttribute("auditLog") AuditLog auditLog, BindingResult result) {
		try {
			model.addAttribute("statusList", getStatusList());
			
			if (auditLog.getDateFrom()==null) {
				if (auditLog.getDateFrom()==null) {
					auditLog.setDateFrom(Utils.setMinTime(Calendar.getInstance().getTime()));
				}				
			}
			
			// check only fields with Length annotation
			Utils.checkAnnotationLength(auditLog, result);
			auditLog.setDateFrom(Utils.setTime(auditLog.getDateFrom(), auditLog.getTimeFrom()));
			auditLog.setDateTo(Utils.setTime(auditLog.getDateTo(), auditLog.getTimeTo()));

			if (result.hasErrors())
				addError(request, result.getAllErrors());
			else {
				auditLog = isRequestFromMenu(request, auditLog);
				model.addAttribute("auditLog", auditLog);

				if (StringUtils.isBlank(auditLog.getOrderColumn()))
					auditLog.setOrderColumn("2");
				if (auditLog.getOrderAsc() == null)
					auditLog.setOrderAsc(Boolean.FALSE);
				SearchResults sd = commonService.getAuditLogList(auditLog);
				if (sd.isEmpty())
					addMessage(request, "error.noRecordsFound");
				else
					model.addAttribute("searchResults", sd);
			}
		} catch (Exception t) {
			handleError(request, t);
		}
		return "auditLogList";
	}

}
