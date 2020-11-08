package SDM.servlets;

import SDM.JsonObjects.JsonCustomerOrder;
import SDM.JsonObjects.JsonItem;
import SDM.JsonObjects.JsonStore;
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

public class BuildCustomerOrdersTable extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            String customerName = SessionUtils.getUsername(request);
            Customer customer = (Customer) ServletUtils.getSystemManager(getServletContext()).getUserManager().getUserByName(customerName);

            List<JsonCustomerOrder> jsonOrderList = new LinkedList<>();

            for (Order order : customer.getOrderHistory()) {
                JsonCustomerOrder jsonCustomerOrder = new JsonCustomerOrder(order.getSerialNumber(), order.getDate(),
                        order.getCoordinate().getCol(), order.getCoordinate().getRow(), order.getOrderStoresList().size(),
                        order.getAmountOfItems(), order.getItemsCost(), order.getDeliveryPrice(), order.getGeneralOrderPrice());

                for (Order.OrderItem orderItem : order.getOrderList()) {
                    jsonCustomerOrder.addItemToList(orderItem.getSerialNumber(), orderItem.getStoreItem().getName(),
                            orderItem.getStoreItem().getItem().getPurchaseMethod().toString(),
                            orderItem.getStore().getSerialNumber(), orderItem.getStore().getName(), orderItem.getAmountFromItemForThisOrder(),
                            orderItem.getPricePerUnit(), orderItem.getGeneralItemPrice(), orderItem.isInSale());
                }

                jsonOrderList.add(jsonCustomerOrder);
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
