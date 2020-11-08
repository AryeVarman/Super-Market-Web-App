package SDM.servlets;

import SDM.JsonObjects.JsonStoreShort;
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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class BuildStoreOwnerStoresButtons extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            SystemManager systemManager = ServletUtils.getSystemManager(getServletContext());
            String storeOwnerName = SessionUtils.getUsername(request);
            StoreOwner storeOwner = (StoreOwner) systemManager.getUserManager().getUserByName(storeOwnerName);

            String areaName = SessionUtils.getSessionManager().get(SessionUtils.getUsername(request)).getCurrentAreaName();
            AreaManager areaManager = systemManager.getAreasMap().get(areaName);

            if(storeOwner != null) {
                Collection<Store> storeList = areaManager.getStoresList().values();

                if(!storeList.isEmpty()) {
                    List<JsonStoreShort> jsonStoreList = new LinkedList<>();

                    for (Store store : storeList) {
                        if(store.getStoreOwner().equals(storeOwner)) {
                            jsonStoreList.add(new JsonStoreShort(store.getSerialNumber(), store.getName(), store.getStoreOwner().getName()));
                        }
                    }

                    String storesJson = new Gson().toJson(jsonStoreList);
                    out.print(storesJson);
                    System.out.println(storesJson);
                }
                else {
                    out.print(Constants.ERROR_NO_STORES);
                    System.out.println(Constants.ERROR_NO_STORES);
                }
            }
            out.flush();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
