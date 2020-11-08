package SDM.servlets;

import SDM.JsonObjects.JsonOrderAlert;
import SDM.constants.Constants;
import SDM.utils.ServletUtils;
import SDM.utils.SessionUtils;
import com.google.gson.Gson;
import engine.src.SDMEngine.Order;
import engine.src.SDMEngine.StoreOwner;
import engine.src.SDMEngine.SystemManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class OrderAlertServlet  extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            SystemManager systemManager = ServletUtils.getSystemManager(getServletContext());
            String storeOwnerName = SessionUtils.getUsername(request);
            StoreOwner storeOwner = (StoreOwner) systemManager.getUserManager().getUserByName(storeOwnerName);

            if(storeOwner != null) {
                Collection<Order> orderList = systemManager.getOrderAlertsManager().getAlertsForStoreOwner(storeOwner);

                if(orderList != null && !orderList.isEmpty()) {
                    List<JsonOrderAlert> orderAlertList = new LinkedList<>();
                    for (Order order : orderList) {
                        String storeName = order.getOrderStoresList().iterator().next().getName();
                        orderAlertList.add(new JsonOrderAlert(storeName, order.getSerialNumber(), order.getCustomer().getName(),
                                order.getAmountOfItemTypes(), order.getItemsCost(), order.getDeliveryPrice()));
                    }

                    String alertsJson = new Gson().toJson(orderAlertList);
                    out.print(alertsJson);
                    System.out.println(alertsJson);
                }
                else {
                    out.print(Constants.ERROR_NO_NEW_ORDER_ALERT);
                    System.out.println(Constants.ERROR_NO_NEW_ORDER_ALERT);
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            out.print(Constants.ERROR_ALERT);

        }
        finally {
            out.flush();
            out.close();
        }
    }
}