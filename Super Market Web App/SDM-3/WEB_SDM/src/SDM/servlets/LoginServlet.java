package SDM.servlets;

import SDM.utils.ServletUtils;
import SDM.utils.SessionUtils;
import SDM.utils.SingleSession;
import engine.src.SDMEngine.SystemManager;
import engine.src.SDMEngine.UserManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static SDM.constants.Constants.*;

public class LoginServlet extends HttpServlet {

    private final String SALE_AREAS_URL = "pages/saleAreas/saleAreas.html";
    private final String SIGN_UP_URL = "index.html";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        try(PrintWriter out = response.getWriter()) {

            String usernameFromSession = SessionUtils.getUsername(request);
            SystemManager systemManager = ServletUtils.getSystemManager(getServletContext());

            if (usernameFromSession == null) {
                //user is not logged in yet
                String usernameFromParameter = request.getParameter(USERNAME);
                String userType = request.getParameter(USER_TYPE);

                if (usernameFromParameter == null || usernameFromParameter.trim().isEmpty()) {
                    out.print(ILLEGAL_NAME);
                    out.flush();
                } else {
                    usernameFromParameter = usernameFromParameter.trim().toLowerCase();
                    userType = userType.toLowerCase();

                    synchronized (this) {
                        if (systemManager.getUserManager().isUserExists(usernameFromParameter)) {
                            out.print(USER_NAME_TAKEN);
                            out.flush();
                        } else {
                            systemManager.addNewUserToSystem(usernameFromParameter, userType);
                            request.getSession(true).setAttribute(USERNAME, usernameFromParameter);
                            SessionUtils.getSessionManager().put(usernameFromParameter, new SingleSession());
                            System.out.println("On login, request URI is: " + request.getRequestURI());
                            out.print(SALE_AREAS_URL);
                            out.flush();
                        }
                    }
                }
            } else {
                out.print(SALE_AREAS_URL);
                out.flush();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() { return "Short description"; }
}
