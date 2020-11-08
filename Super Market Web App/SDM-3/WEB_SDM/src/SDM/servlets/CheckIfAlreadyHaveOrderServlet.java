package SDM.servlets;

import SDM.JsonObjects.JsonItemForCart;
import SDM.JsonObjects.JsonSmartOrder;
import SDM.constants.Constants;
import SDM.utils.ServletUtils;
import SDM.utils.SessionUtils;
import com.google.gson.Gson;
import engine.src.SDMEngine.AreaManager;
import engine.src.SDMEngine.Order;
import engine.src.SDMEngine.Store;
import engine.src.SDMEngine.StoreItem;

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

public class CheckIfAlreadyHaveOrderServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String errorMsg = "";
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String userName = SessionUtils.getUsername(request);
            String currentAreaName = SessionUtils.getSessionManager().get(userName).getCurrentAreaName();
            AreaManager areaManager = ServletUtils.getSystemManager(getServletContext()).getAreasMap().get(currentAreaName);
            String smart = request.getParameter("smart");

            List<JsonItemForCart> jsonItemForCartList = new LinkedList<>();

            if(smart != null && smart.equals("true")){

                if(areaManager.getCurrentCustomersSmartOrders().containsKey(userName)) {
                    Order order = areaManager.getCurrentCustomersSmartOrders().get(userName);
                    if(order != null){
                        Map<Integer, JsonSmartOrder> jsonSmartOrderMap = new HashMap<>();
                        for (Order.OrderItem orderItem: order.getOrderList()) {
                            int storeId = orderItem.getStore().getSerialNumber();
                            Store store = orderItem.getStore();
                            if(jsonSmartOrderMap.containsKey(storeId)){
                                addItemToSmartOrderJson(jsonSmartOrderMap.get(storeId),orderItem);
                            }else{
                                jsonSmartOrderMap.put(storeId,new JsonSmartOrder(storeId,store.getName(),store.getPPK(),store.getCoordinate().getRow(),store.getCoordinate().getCol()));
                                addItemToSmartOrderJson(jsonSmartOrderMap.get(storeId),orderItem);
                            }
                        }
                        Gson gson = new Gson();

                        String JsonCartItemsList = gson.toJson(jsonSmartOrderMap.values());
                        System.out.println(JsonCartItemsList);
                        out.print(JsonCartItemsList);
                    }else{
                        out.print(new Gson().toJson(Constants.ERROR_INIT + "there is no open order"));
                    }
                }else{
                    out.print(new Gson().toJson(Constants.ERROR_INIT + "there is no open order"));
                }
            }else{
                int storeId = Integer.parseInt(request.getParameter("storeId"));
                if(areaManager.checkIfAlreadyHaveOpenOrder(userName,storeId)) {

                    Map<String, Map<Integer, Order>> currentCustomerOrderList = areaManager.getCurrentCustomersOrders();

                    Order currentOrder = currentCustomerOrderList.get(userName).get(areaManager.getStoresList().get(storeId).getSerialNumber());

                    for(Order.OrderItem orderItem : currentOrder.getOrderList()) {
                        Store store = areaManager.getStoresList().get(orderItem.getStore().getSerialNumber());
                        StoreItem orderStoreItem = orderItem.getStoreItem();
                        JsonItemForCart jsonItemForCart = new JsonItemForCart(orderStoreItem.getSerialNumber(), orderStoreItem.getName(), orderStoreItem.getItem().getPurchaseMethod(),
                                orderItem.getAmountFromItemForThisOrder(), orderStoreItem.getPrice(), orderItem.getGeneralItemPrice(), orderItem.isInSale() ? "Yes" : "No", store.getName(), storeId);
                        jsonItemForCartList.add(jsonItemForCart);
                    }
                    Gson gson = new Gson();

                    String JsonCartItemsList = gson.toJson(jsonItemForCartList);
                    System.out.println(JsonCartItemsList);
                    out.print(JsonCartItemsList);
                }else{
                    out.print(new Gson().toJson(Constants.ERROR_INIT + "there is no open order"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            out.print(new Gson().toJson(Constants.ERROR_INIT + ex.getMessage()));
        }
        finally {
            out.flush();
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}
