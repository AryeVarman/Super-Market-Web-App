package SDM.servlets;

import SDM.JsonObjects.JsonItem;
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

public class BuildItemsTableServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            SystemManager systemManager = ServletUtils.getSystemManager(getServletContext());
            String areaName = SessionUtils.getSessionManager().get(SessionUtils.getUsername(request)).getCurrentAreaName();

            AreaManager areaManager = systemManager.getAreasMap().get(areaName);
            List<JsonItem> jsonItemList = new LinkedList<>();

            if(areaManager != null && areaManager.getItemsList() != null) {
                for (Item item : areaManager.getItemsList().values()) {
                    ItemSystemStatistic itemSystemStatistic = areaManager.getItemsSystemStatistics().get(item);
                    JsonItem newJsonItem = new JsonItem(
                            item.getSerialNumber(), item.getName(), item.getPurchaseMethod(), itemSystemStatistic.getAvgPrice(),
                            itemSystemStatistic.getAmountOfStoresThatSell(), itemSystemStatistic.getAmountOfTimesBeenSold(), item.getSerialNumber() + areaName);

                    jsonItemList.add(newJsonItem);
                }
            }


            Gson gson = new Gson();
            String jsonObject = gson.toJson(jsonItemList);

            System.out.println(jsonObject);
            out.println(jsonObject);
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