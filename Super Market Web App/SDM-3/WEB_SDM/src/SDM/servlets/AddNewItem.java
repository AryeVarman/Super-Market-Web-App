package SDM.servlets;

import SDM.JsonObjects.JsonItemToStore;
import SDM.JsonObjects.JsonStoreItem;
import SDM.constants.Constants;
import SDM.utils.ServletUtils;
import SDM.utils.SessionUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import engine.src.SDMEngine.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddNewItem extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String storeListString = request.getParameter("storeList");
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<JsonItemToStore>>(){}.getType();

            ArrayList<JsonItemToStore> jsonItemToStoresList = gson.fromJson(storeListString, type);
            String itemName = request.getParameter("itemName");
            String itemType = request.getParameter("itemType");

            SystemManager systemManager = ServletUtils.getSystemManager(getServletContext());

            String areaName = SessionUtils.getSessionManager().get(SessionUtils.getUsername(request)).getCurrentAreaName();
            AreaManager areaManager = systemManager.getAreasMap().get(areaName);

            String storeOwnerName = SessionUtils.getUsername(request);
            StoreOwner storeOwner = (StoreOwner) systemManager.getUserManager().getUserByName(storeOwnerName);

            Item item;

            if (itemType.toLowerCase().equals("quantity")) {
                item = new ItemByQuantity(areaManager.getItemSerialNumber(), itemName);
            }
            else {
                item = new ItemByWeight(areaManager.getItemSerialNumber(), itemName);
            }

            Map<Integer, Double> storeIdToPriceMap = new HashMap<>();
            for(JsonItemToStore jsonItemToStore : jsonItemToStoresList) {

                Store store = areaManager.getStoresList().get(jsonItemToStore.getStoreId());

                if(store != null) {
                    storeIdToPriceMap.put(store.getSerialNumber(), jsonItemToStore.getItemPriceInStore());
                }
            }

            areaManager.addItemToSystemDynamically(item, storeIdToPriceMap);

            out.print(Constants.ITEM_ADDED);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            out.print(Constants.ADDING_ITEM_ERROR + "\n" + ex.getMessage());
        }
        finally {
            out.flush();
            out.close();
        }
    }
}
