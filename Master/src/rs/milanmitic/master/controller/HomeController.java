package rs.milanmitic.master.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import rs.milanmitic.master.common.aop.MasterLogAnnotation;
import rs.milanmitic.master.common.util.Utils;

/**
 * Handling of common URLs (not related to complex logic)
 * 
 * @author milan
 * 
 */
@Controller
public class HomeController extends BasicController {

	/**
	 * Master home page
	 * 
	 * @param request
	 * @return
	 */
	@MasterLogAnnotation
	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String home(HttpServletRequest request, Model model) {
		String lang = (String) request.getSession().getAttribute("_CHG_LOC");
		if (lang != null) {
			request.getSession().removeAttribute("_CHG_LOC");
			Utils.changeLanguage(request, null, lang);
		} 
		return "home";
	}

	/**
	 * Change language
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @param lang
	 * @return
	 */
	@MasterLogAnnotation
	@RequestMapping(value = "/changeLocale/{lang}", method = RequestMethod.GET)
	public String changeLocale(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String lang) {
		if (StringUtils.isNotBlank(lang))
			Utils.changeLanguage(request, response, lang);
		return getLoggedUser(request) != null ? "redirect:/app/home" : "redirect:/app/login";
	}

	@MasterLogAnnotation
	@RequestMapping(value = "/changeLanguage/{lang}/{loginType}", method = RequestMethod.GET)
	public String changeLanguage(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String lang, @PathVariable String loginType) {
		if (StringUtils.isNotBlank(lang) && StringUtils.isNotBlank(loginType))
			Utils.changeLanguage(request, response, lang);
		return getLoggedUser(request) != null ? "redirect:/app/home" : "redirect:/app/login/" + loginType;
	}

	/**
	 * Save filter status
	 * 
	 * @param request
	 * @param model
	 * @param filterName
	 * @param filterStatus
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@MasterLogAnnotation
	@RequestMapping(value = "/saveFilter/{filterName}/{filterStatus}", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public void saveFilter(HttpServletRequest request, Model model, @PathVariable String filterName, @PathVariable String filterStatus) {
		HashMap<String, String> hm = (HashMap<String, String>) request.getSession().getAttribute("filters");
		if (hm == null) {
			hm = new HashMap<String, String>();
			request.getSession().setAttribute("filters", hm);
		}
		hm.put(filterName, filterStatus);
	}
}
