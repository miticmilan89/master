package rs.milanmitic.master.controller;

import java.util.Calendar;

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

import rs.milanmitic.master.common.aop.MasterLogAnnotation;
import rs.milanmitic.master.common.pagging.SearchResults;
import rs.milanmitic.master.common.util.Utils;
import rs.milanmitic.master.model.ActivityLog;
import rs.milanmitic.master.service.CommonService;

/**
 * Handling BANK CRUD
 * 
 * @author milan
 */
@Controller
@RequestMapping(value = "/activityLog")
public class ActivityLogListController extends BasicController {

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
	public String viewActivityLog(HttpServletRequest request, Model model, @PathVariable Long id) {
		try {
			return "redirect:/app/activityLog/list";
		} catch (Exception t) {
			handleError(request, t);
		}
		return "activityLogView";
	}

	/**
	 * Search database with search criteria
	 * 
	 * @param request
	 * @param activityLog
	 * @param result
	 * @param model
	 * @return
	 */
	@MasterLogAnnotation
	@RequestMapping(value = "/list")
	public String activityLogList(HttpServletRequest request, Model model, @ModelAttribute("activityLog") ActivityLog activityLog, BindingResult result) {
		try {
			// check only fields with Length annotation
			Utils.checkAnnotationLength(activityLog, result);

			if (activityLog.getDateFrom()==null) {
				activityLog.setDateFrom(Utils.setMinTime(Calendar.getInstance().getTime()));
			}
			
			activityLog.setDateFrom(Utils.setTime(activityLog.getDateFrom(), activityLog.getTimeFrom()));
			activityLog.setDateTo(Utils.setTime(activityLog.getDateTo(), activityLog.getTimeTo()));

			if (result.hasErrors())
				addError(request, result.getAllErrors());
			else {
				activityLog = isRequestFromMenu(request, activityLog);
				model.addAttribute("activityLog", activityLog);

				if (StringUtils.isBlank(activityLog.getOrderColumn()))
					activityLog.setOrderColumn("1");
				if (activityLog.getOrderAsc() == null)
					activityLog.setOrderAsc(Boolean.FALSE);
				SearchResults sd = commonService.getActivityLogList(activityLog);
				if (sd.isEmpty())
					addMessage(request, "error.noRecordsFound");
				else
					model.addAttribute("searchResults", sd);
			}
		} catch (Exception t) {
			handleError(request, t);
		}
		return "activityLogList";
	}

}
