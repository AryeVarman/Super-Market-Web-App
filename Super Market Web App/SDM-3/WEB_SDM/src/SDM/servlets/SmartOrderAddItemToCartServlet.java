package SDM.servlets;

import SDM.JsonObjects.JsonItemForCart;
import SDM.JsonObjects.JsonSmartOrder;
import SDM.constants.Constants;
import SDM.utils.ServletUtils;
import SDM.utils.SessionUtils;
import com.google.gson.Gson;
import engine.src.SDMEngine.*;

import javax.persistence.criteria.CriteriaBuilder;
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

public class SmartOrderAddItemToCartServlet extends HttpServlet {
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
            String amountStr = request.getParameter("amount");
            String fromSale = request.getParameter("fromSale");


            Double amount = Double.parseDouble(amountStr);
            String currentAreaName = SessionUtils.getSessionManager().get(userName).getCurrentAreaName();

            AreaManager areaManager = ServletUtils.getSystemManager(getServletContext()).getAreasMap().get(currentAreaName);


            Map<String,Order> currentCustomerSmartOrder = areaManager.getCurrentCustomersSmartOrders();

            // key: serial number of the store
            Map<Integer, JsonSmartOrder> jsonSmartOrderMap = new HashMap<>();


            try{
                if(fromSale.equals("true")){
                    int storeId = Integer.parseInt(request.getParameter("storeId"));
                    double price = Double.parseDouble(request.getParameter("price"));
                    Store store = areaManager.getStoresList().get(storeId);
                    StoreItem storeItem = new StoreItem(store.getItemsList().get(itemId).getItem(),price);
                    Order currentOrder = currentCustomerSmartOrder.get(userName);
                    currentOrder.addOrderItemToOrderList(store, storeItem, amount,true);
                }
                else{
                    if(areaManager.checkIfAlreadyHaveOpenSmartOrder(userName)){
                        Order currentOrder = currentCustomerSmartOrder.get(userName);
                        areaManager.addItemToOrderFromCheapestStore(itemId,amount,currentOrder);
                    }else{
                        Order newOrder = new Order(areaManager.getOrderSerialNumberForNewOrder(), customer);
                        areaManager.addItemToOrderFromCheapestStore(itemId,amount,newOrder);
                        currentCustomerSmartOrder.put(userName, newOrder);
                    }
                }
            }catch (Exception ex) {
                errorMsg = Constants.ERROR_INIT + ex.getMessage();
            }
            if(errorMsg.equals("")){
                Order currentOrder = currentCustomerSmartOrder.get(userName);

                for (Order.OrderItem orderItem: currentOrder.getOrderList()) {
                    int storeId = orderItem.getStore().getSerialNumber();
                    Store store = orderItem.getStore();
                    if(jsonSmartOrderMap.containsKey(storeId)){
                        addItemToSmartOrderJson(jsonSmartOrderMap.get(storeId),orderItem);
                    }else{
                        jsonSmartOrderMap.put(storeId,new JsonSmartOrder(storeId,store.getName(),store.getPPK(),store.getCoordinate().getCol(),store.getCoordinate().getRow()));
                        addItemToSmartOrderJson(jsonSmartOrderMap.get(storeId),orderItem);
                    }
                }
                Gson gson = new Gson();

                String JsonCartItemsList = gson.toJson(jsonSmartOrderMap.values());
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

    private void addItemToSmartOrderJson(JsonSmartOrder jsonSmartOrder, Order.OrderItem orderItem) {
        StoreItem orderStoreItem = orderItem.getStoreItem();
        Store store = orderItem.getStore();
        jsonSmartOrder.getItemsList().add(new JsonItemForCart(orderStoreItem.getSerialNumber(),orderStoreItem.getName(),orderStoreItem.getItem().getPurchaseMethod(),
                orderItem.getAmountFromItemForThisOrder(),orderStoreItem.getPrice(),orderItem.getGeneralItemPrice(),orderItem.isInSale()?"Yes":"No", store.getName(), store.getSerialNumber()));
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
