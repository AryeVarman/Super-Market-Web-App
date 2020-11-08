package SDM.servlets;

import SDM.JsonObjects.JsonStoreItem;
import SDM.constants.Constants;
import SDM.utils.ServletUtils;
import SDM.utils.SessionUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

public class AddNewStore extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String itemsListString = request.getParameter("itemList");
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<JsonStoreItem>>(){}.getType();

            ArrayList<JsonStoreItem> JsonStoreItemArray = gson.fromJson(itemsListString, type);
            String storeName = request.getParameter("storeName");
            double storePPK = Double.parseDouble(request.getParameter("storePPK"));
            int storeCol = Integer.parseInt(request.getParameter("storeCol"));
            int storeRow = Integer.parseInt(request.getParameter("storeRow"));

            SystemManager systemManager = ServletUtils.getSystemManager(getServletContext());

            String areaName = SessionUtils.getSessionManager().get(SessionUtils.getUsername(request)).getCurrentAreaName();
            AreaManager areaManager = systemManager.getAreasMap().get(areaName);

            String storeOwnerName = SessionUtils.getUsername(request);
            StoreOwner storeOwner = (StoreOwner) systemManager.getUserManager().getUserByName(storeOwnerName);

            Store store = new Store(areaManager.getStoreSerialNumber(), storeName, storeOwner,
                    new Coordinate(storeCol, storeRow), storePPK, areaManager);

            for(JsonStoreItem jsonStoreItem : JsonStoreItemArray) {
                store.addItemToStore(jsonStoreItem.getItemId(), jsonStoreItem.getItemPrice(), areaManager.getItemsList());
            }

            areaManager.addStoreToSystemDynamically(store);

            out.print(Constants.STORE_OPENED);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            out.print(Constants.STORE_OPENING_ERROR + "\n" + ex.getMessage());
        }
        finally {
            out.flush();
            out.close();
        }
    }
}