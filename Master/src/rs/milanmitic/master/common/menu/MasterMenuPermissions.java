package rs.milanmitic.master.common.menu;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sf.navigator.menu.MenuComponent;
import net.sf.navigator.menu.PermissionsAdapter;
import rs.milanmitic.master.common.Constants;
import rs.milanmitic.master.common.data.LoggedUserData;
import rs.milanmitic.master.common.util.Utils;

/**
 * Menu permission
 * 
 * @author milan
 */
public class MasterMenuPermissions implements PermissionsAdapter {

	private static Logger log = LogManager.getLogger(MasterMenuPermissions.class.getName());

	private HttpServletRequest request = null;

	public MasterMenuPermissions(HttpServletRequest request) {
		log.debug("MasterMenuPermissions constructor started...");
		this.request = request;

	}

	/**
	 * Check user role
	 * 
	 * @return true or false
	 */
	@Override
	public boolean isAllowed(MenuComponent menu) {
		LoggedUserData user = (LoggedUserData) request.getSession().getAttribute(Constants.LOGGED_USER);
		if (user == null)
			return false;

		try {
			// SECURITY_COMMENT
			if (StringUtils.isNotBlank(menu.getPage())) {
				return Utils.isUrlVisible(menu.getPage());
			}
			return true;

		} catch (Exception t) {
			log.error("Error, isAllowed", t);
			return false;
		}
	}

}
