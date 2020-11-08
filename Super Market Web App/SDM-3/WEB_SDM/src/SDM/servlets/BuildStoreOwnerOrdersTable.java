package SDM.servlets;

import SDM.JsonObjects.JsonCustomerOrder;
import SDM.JsonObjects.JsonStoreOrder;
import SDM.utils.ServletUtils;
import SDM.utils.SessionUtils;
import com.google.gson.Gson;
import engine.src.SDMEngine.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

public class BuildStoreOwnerOrdersTable extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            SystemManager systemManager = ServletUtils.getSystemManager(getServletContext());

            String storeOwnerName = SessionUtils.getUsername(request);
            StoreOwner storeOwner = (StoreOwner) systemManager.getUserManager().getUserByName(storeOwnerName);

            String areaName = SessionUtils.getSessionManager().get(storeOwnerName).getCurrentAreaName();
            AreaManager areaManager = systemManager.getAreasMap().get(areaName);

            String storeNumberStr = request.getParameter("storeNumber");
            int storeNumber = Integer.parseInt(storeNumberStr);

            Store store = areaManager.getStoresList().get(storeNumber);

            List<JsonStoreOrder> jsonOrderList = new LinkedList<>();

            for (Order order : store.getOrderHistory()) {
                JsonStoreOrder jsonStoreOrder = new JsonStoreOrder(order.getSerialNumber(), order.getDate(), order.getCustomer().getName(),
                        order.getCoordinate().getCol(), order.getCoordinate().getRow(), order.getAmountOfItems(), order.getItemsCost(),
                        order.getDeliveryPrice(), order.getGeneralOrderPrice());

                for (Order.OrderItem orderItem : order.getOrderList()) {
                    jsonStoreOrder.addItemToList(orderItem.getSerialNumber(), orderItem.getStoreItem().getName(),
                            orderItem.getStoreItem().getItem().getPurchaseMethod().toString(),
                            orderItem.getAmountFromItemForThisOrder(), orderItem.getPricePerUnit(), orderItem.getGeneralItemPrice(),
                            orderItem.isInSale());
                }

                jsonOrderList.add(jsonStoreOrder);
            }

            Gson gson = new Gson();
            String jsonObject = gson.toJson(jsonOrderList);

            System.out.println(jsonObject);
            out.println(jsonObject);
            out.flush();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
