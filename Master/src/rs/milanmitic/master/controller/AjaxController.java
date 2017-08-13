package rs.milanmitic.master.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handling of common URLs that all three users can execute
 * 
 * @author milan
 * 
 */
@Controller
@RequestMapping(value = "/ajax")
public class AjaxController extends BasicController {

}
