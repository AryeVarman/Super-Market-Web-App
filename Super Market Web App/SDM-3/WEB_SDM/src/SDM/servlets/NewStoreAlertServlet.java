package SDM.servlets;

import SDM.JsonObjects.JsonNewStore;
import SDM.constants.Constants;
import SDM.utils.ServletUtils;
import SDM.utils.SessionUtils;
import com.google.gson.Gson;
import engine.src.SDMEngine.Store;
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

public class NewStoreAlertServlet extends HttpServlet {
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
                Collection<Store> storeList = systemManager.getNewStoreAlertManager().getAlertsForStoreOwner(storeOwner);

                if(storeList != null && !storeList.isEmpty()) {
                    List<JsonNewStore> newStoreAlertList = new LinkedList<>();

                    for (Store store : storeList) {
                        newStoreAlertList.add(new JsonNewStore(store.getAreaStoreIn().getAreaName(),
                                store.getStoreOwner().getName(), store.getName(),
                                store.getCoordinate().getCol(), store.getCoordinate().getRow(), store.getItemsList().size(),
                                store.getAreaStoreIn().getItemsList().size()));
                    }

                    String alertsJson = new Gson().toJson(newStoreAlertList);
                    out.print(alertsJson);
                    System.out.println(alertsJson);
                }
                else {
                    out.print(Constants.ERROR_NO_NEW_STORES_ALERT);
                    System.out.println(Constants.ERROR_NO_NEW_STORES_ALERT);
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
