package SDM.utils;

import engine.src.SDMEngine.OrderAlertsManager;
import engine.src.SDMEngine.SystemManager;
import javax.servlet.ServletContext;

public class ServletUtils {

	private static final String SDM_SYSTEM_MANAGER = "sdmSystemManager";
	private static final Object systemManagerLock = new Object();

	public static SystemManager getSystemManager(ServletContext servletContext) {

		synchronized (systemManagerLock) {
			if (servletContext.getAttribute(SDM_SYSTEM_MANAGER) == null) {
				servletContext.setAttribute(SDM_SYSTEM_MANAGER, new SystemManager());
			}
		}
		return (SystemManager) servletContext.getAttribute(SDM_SYSTEM_MANAGER);
	}
}