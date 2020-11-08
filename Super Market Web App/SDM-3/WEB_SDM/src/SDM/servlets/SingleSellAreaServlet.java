package SDM.servlets;

import SDM.utils.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class SingleSellAreaServlet extends HttpServlet {

    private final String SINGLE_AREA_URL = "../singleSaleArea/singleSaleArea.html";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try(PrintWriter out = response.getWriter()) {

            String areaName = request.getParameter("areaName");
            SessionUtils.getSessionManager().get(SessionUtils.getUsername(request)).setCurrentAreaName(areaName);
            out.println(SINGLE_AREA_URL);
            out.flush();
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
}