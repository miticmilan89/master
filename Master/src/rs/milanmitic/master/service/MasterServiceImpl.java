package rs.milanmitic.master.service;

import java.util.MissingResourceException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;

public abstract class MasterServiceImpl {

	protected final Logger log = LogManager.getLogger(this.getClass());

	protected String getMessage(MessageSource messageSource, String key, Object... params) {
		String oneParam = null;
		try {
			Object[] newParams = null;
			if (params == null)
				return messageSource.getMessage(key, newParams, null);

			newParams = new Object[params.length];
			int i = 0;
			for (Object p : params) {
				if (p instanceof String) {
					oneParam = (String) p;
					newParams[i] = oneParam.startsWith("#") ? messageSource.getMessage(oneParam.substring(1), null, null) : oneParam;
				} else {
					newParams[i] = p;
				}
				i++;
			}
			return messageSource.getMessage(key, newParams, null);
		} catch (MissingResourceException e) {
			log.error("MissingResourceException, key=" + key + ", subkey=" + oneParam, e);
			return key;
		}
	}
}
