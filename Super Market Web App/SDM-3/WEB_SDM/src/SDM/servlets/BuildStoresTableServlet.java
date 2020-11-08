package SDM.servlets;

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

public class BuildStoresTableServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            SystemManager systemManager = ServletUtils.getSystemManager(getServletContext());
            String areaName = SessionUtils.getSessionManager().get(SessionUtils.getUsername(request)).getCurrentAreaName();

            AreaManager areaManager = systemManager.getAreasMap().get(areaName);
            List<JsonStore> jsonStoreList = new LinkedList<>();

            for (Store store : areaManager.getStoresList().values()) {

                List<JsonItem> itemList = new LinkedList<>();


            /* int serialNumber, String name,String purchaseType, double avgPrice,
                    int amountOfStoresThatSell, double amountOfTimesBeenSold*/
                for (StoreItem storeItem : store.getItemsList().values()) {
                    int itemId = storeItem.getSerialNumber();
                    String itemName = storeItem.getName();
                    String purchaseMethod = storeItem.getItem().getPurchaseMethod();
                    double price = storeItem.getPrice();
                    int sellCount = store.getHowManyTimesItemBeenSoldFromStore().get(storeItem.getItem());

                    JsonItem jsonItem = new JsonItem(itemId, itemName, purchaseMethod, price, 1, sellCount,itemId+store.getName());
                    jsonItem.setPrice(price);
                    itemList.add(jsonItem);
                }

                JsonStore newJsonStore = new JsonStore(store.getSerialNumber(), store.getName(),
                        store.getStoreOwner().getName(), store.getCoordinate().toString(),
                        store.getOrderHistory().size(), store.getProfitFromItems(), store.getPPK(), store.getProfitFromDeliveries(), itemList);
                newJsonStore.setAreaName(areaName);
                jsonStoreList.add(newJsonStore);
            }

            Gson gson = new Gson();
            String jsonObject = gson.toJson(jsonStoreList);

            System.out.println(jsonObject);
            out.println(jsonObject);
            out.flush();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /*list of: {"purchaseType":"Quantity","serialNumber":1,"name":"toilet paper","avgPrice":30.5,"amountOfStoresThatSell":2,"amountOfTimesBeenSold":0.0}
        // and "url" : the url*/
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
