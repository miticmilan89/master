package rs.milanmitic.master.common;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.ContextLoaderListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import rs.milanmitic.master.common.util.Utils;
import rs.milanmitic.master.filter.SecurityFilter;

/**
 * Started only once during system initialization.
 * 
 * @author zd
 * 
 */
public class StartupListener extends ContextLoaderListener implements ServletContextListener {

	private final Logger log = LogManager.getLogger(StartupListener.class);

	private static boolean initialized = false;

	/**
	 * Init context
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			log.debug("initializing context...");
			ServletContext context = event.getServletContext();

			// get application path
			Constants.setApplicationPath(context.getRealPath(""));
			if (!Constants.getApplicationPath().endsWith(File.separator))
				Constants.setApplicationPath(Constants.getApplicationPath() + File.separator);

			// read app_config file
			initAppConfig();

			// setup
			setupContext(context);

		} catch (Exception t) {
			log.error("Error", t);
		} finally {
			setInitialized(Boolean.TRUE);
		}
	}

	private void initAppConfig() throws ParserConfigurationException, SAXException, IOException {
		String filePath = Constants.getApplicationPath() + "WEB-INF" + File.separator + "conf" + File.separator + "app_config.xml";

		File f = new File(filePath);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(f);
		Element root = doc.getDocumentElement();
		if (root == null)
			return;

		String x = Utils.getXMLValue(root, "DEBUG_MODE");
		if (StringUtils.isNotBlank(x))
			Constants.setDebugMode("true".equals(x));

		NodeList ignoredList = root.getElementsByTagName("SECURITY_IGNORED_URL_LIST");
		SecurityFilter.setIgnoreUrls(new HashSet<String>());
		if (ignoredList != null && ignoredList.getLength() > 0) {
			NodeList adapter = ((Element) ignoredList.item(0)).getElementsByTagName("SECURITY_IGNORED_URL");
			int n = adapter.getLength();
			for (int i = 0; i < n; i++) {
				Element adapterElem = (Element) adapter.item(i);
				SecurityFilter.getIgnoreUrls().add(adapterElem.getTextContent());
			}
		}
		log.debug("SecurityFilter.ignoreUrls:{}", SecurityFilter.getIgnoreUrls());

		x = Utils.getXMLValue(root, "SERVER_ID");
		if (StringUtils.isNotBlank(x))
			Constants.setServerId(x.trim());

		log.debug("debugMode:{}", Constants.isDebugMode());
	}

	/**
	 * Set data to application context
	 * 
	 * @param context
	 */
	public void setupContext(ServletContext context) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("CURRENT_THEME", Constants.CURRENT_THEME);
		map.put("LOGGED_USER", Constants.LOGGED_USER);
		map.put("YES", Constants.YES);
		map.put("NO", Constants.NO);
		map.put("ACTION", Constants.ACTION);
		map.put("ACTION_ADD", Constants.ACTION_ADD);
		map.put("SECURE_FIELD", Constants.SECURE_FIELD);
		map.put("DEBUG_MODE", Constants.isDebugMode());
		map.put("SHOW_FLAT_COMMISSIONS", "false");

		context.setAttribute("Constants", map);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.context.ContextLoaderListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		Constants.setShutDownInProgress(Boolean.TRUE);

		super.contextDestroyed(event);
	}

	public static boolean isInitialized() {
		return initialized;
	}

	public static void setInitialized(boolean initialized) {
		StartupListener.initialized = initialized;
	}

}