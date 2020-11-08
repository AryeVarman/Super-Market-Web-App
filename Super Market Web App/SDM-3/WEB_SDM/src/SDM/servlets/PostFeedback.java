package SDM.servlets;

import SDM.constants.Constants;
import SDM.utils.ServletUtils;
import SDM.utils.SessionUtils;
import engine.src.SDMEngine.AreaManager;
import engine.src.SDMEngine.Customer;
import engine.src.SDMEngine.Store;
import engine.src.SDMEngine.SystemManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;

public class PostFeedback extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            int storeNum = Integer.parseInt(request.getParameter("storeNumber"));
            int clientScore = Integer.parseInt(request.getParameter("score"));
            String clientComments = request.getParameter("comments");
            int orderNumber = Integer.parseInt(request.getParameter("orderNumber"));

            SystemManager systemManager = ServletUtils.getSystemManager(getServletContext());
            String areaName = SessionUtils.getSessionManager().get(SessionUtils.getUsername(request)).getCurrentAreaName();
            AreaManager areaManager = systemManager.getAreasMap().get(areaName);

            String customerName = SessionUtils.getUsername(request);
            Customer customer = (Customer) ServletUtils.getSystemManager(getServletContext()).getUserManager().getUserByName(customerName);

            LocalDate orderDate = areaManager.getOrderByOrderSerialNumber(orderNumber).getDate();

            Store storeToAddFeedback = areaManager.getStoresList().get(storeNum);

            storeToAddFeedback.addNewFeedback(customer, orderDate, clientComments, clientScore, systemManager);

            out.print("Feedback to store: " + storeToAddFeedback.getName() + " added successfully");
        }
        catch (Exception ex) {
            out.print(Constants.POST_FEEDBACK_ERROR);
            ex.printStackTrace();
        }
        finally {
            out.flush();
            out.close();
        }
    }
}
