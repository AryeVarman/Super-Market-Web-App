package SDM.servlets;

import SDM.JsonObjects.JsonItemForCart;
import SDM.constants.Constants;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AddItemToCartServlet extends HttpServlet {


    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String errorMsg = "";
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            UserManager userManager = ServletUtils.getSystemManager(getServletContext()).getUserManager();
            String userName = SessionUtils.getUsername(request);
            Customer customer = (Customer)userManager.getUsers().get(userName);
            int itemId = Integer.parseInt(request.getParameter("itemId"));
            int storeId = Integer.parseInt(request.getParameter("storeId"));
            String amountStr = request.getParameter("amount");
            String fromSaleStr = request.getParameter("fromSale");
            String currentAreaName = SessionUtils.getSessionManager().get(userName).getCurrentAreaName();

            AreaManager areaManager = ServletUtils.getSystemManager(getServletContext()).getAreasMap().get(currentAreaName);
            Store store = areaManager.getStoresList().get(storeId);
            boolean fromSaleBool = fromSaleStr.equals("true");
            StoreItem storeItem;
            if(fromSaleBool){
                double price = Double.parseDouble(request.getParameter("price"));
                storeItem = new StoreItem(store.getItemsList().get(itemId).getItem(),price);
            }else{
                storeItem = store.getItemsList().get(itemId);
            }

            Map<String, Map<Integer,Order>> currentCustomerOrderList = areaManager.getCurrentCustomersOrders();

            try{
                areaManager.isValidAmountInput(storeItem, amountStr);
                Map<Integer,Order> orderMap;

                if(areaManager.checkIfAlreadyHaveOpenOrder(userName,store.getSerialNumber())){
                    Order currentOrder = currentCustomerOrderList.get(userName).get(store.getSerialNumber());
                    currentOrder.addOrderItemToOrderList(store,storeItem,Double.parseDouble(amountStr),fromSaleBool);
                }else{
                    if(currentCustomerOrderList.containsKey(userName)){
                        orderMap = currentCustomerOrderList.get(userName);
                    }else{
                        orderMap = new HashMap<>();
                    }
                    Order newOrder = new Order(areaManager.getOrderSerialNumberForNewOrder(), customer);
                    newOrder.addOrderItemToOrderList(store,storeItem,Double.parseDouble(amountStr),fromSaleBool);
                    orderMap.put(storeId,newOrder);
                    currentCustomerOrderList.put(userName, orderMap);

                }
            }catch (Exception ex) {
                errorMsg = Constants.ERROR_INIT + ex.getMessage();
            }
            String fromSaleAnswer = fromSaleBool ? "Yes" : "No";
            if(errorMsg.equals("")){
                Order currentOrder = currentCustomerOrderList.get(userName).get(store.getSerialNumber());
                List<JsonItemForCart> jsonItemForCartList = new LinkedList<>();
                for (Order.OrderItem orderItem: currentOrder.getOrderList()) {
                    StoreItem orderStoreItem = orderItem.getStoreItem();
                    JsonItemForCart jsonItemForCart = new JsonItemForCart(orderStoreItem.getSerialNumber(),orderStoreItem.getName(),orderStoreItem.getItem().getPurchaseMethod(),
                            orderItem.getAmountFromItemForThisOrder(),orderStoreItem.getPrice(),orderItem.getGeneralItemPrice(),orderItem.isInSale()?"Yes":"No", store.getName(), storeId);
                    jsonItemForCartList.add(jsonItemForCart);
                }
                Gson gson = new Gson();

                String JsonCartItemsList = gson.toJson(jsonItemForCartList);
                System.out.println(JsonCartItemsList);
                out.print(JsonCartItemsList);
            }else{
                Gson gson = new Gson();
                out.print(gson.toJson(errorMsg));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            out.print(Constants.ERROR_INIT + ex.getMessage());
        }
        finally {
            out.close();
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}
