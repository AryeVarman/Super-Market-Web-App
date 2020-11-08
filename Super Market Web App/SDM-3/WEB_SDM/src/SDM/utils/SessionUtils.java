package SDM.utils;

import SDM.constants.Constants;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class SessionUtils {

    private static Map<String, SingleSession> sessionManager = new HashMap<>();

    public static String getUsername (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute(Constants.USERNAME) : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }

    public static void clearSession (HttpServletRequest request) { request.getSession().invalidate(); }

    public static Map<String, SingleSession> getSessionManager() { return sessionManager; }
}