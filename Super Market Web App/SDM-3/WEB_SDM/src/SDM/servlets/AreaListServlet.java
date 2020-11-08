package SDM.servlets;

import SDM.JsonObjects.JsonArea;
import SDM.utils.ServletUtils;
import com.google.gson.Gson;
import engine.src.SDMEngine.AreaManager;
import engine.src.SDMEngine.Order;
import engine.src.SDMEngine.SystemManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;

public class AreaListServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //returning JSON objects, not HTML
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            SystemManager systemManager = ServletUtils.getSystemManager(getServletContext());

            Collection<JsonArea> areaList = new LinkedList<>();

            for (AreaManager areaManager : systemManager.getAreasMap().values()) {
                int itemsNumInArea = getItemsNumInArea(areaManager);
                int storesNumInArea = getStoresNumInArea(areaManager);
                int ordersNumInArea = getOrdersNumInArea(areaManager);
                double orderItemsAvgPrice = getOrdersItemsAvgPriceInArea(areaManager);


                areaList.add(new JsonArea(areaManager.getAreaName(), areaManager.getAreaOwner().getName(),
                        itemsNumInArea, storesNumInArea, ordersNumInArea, orderItemsAvgPrice));
            }

            String json = gson.toJson(areaList);

            System.out.println(json);
            out.println(json);
            out.flush();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double getOrdersItemsAvgPriceInArea(AreaManager areaManager) {
        double orderItemsSum = 0;

        if(areaManager.getOrdersHistory().size() != 0) {
            for (Order order : areaManager.getOrdersHistory()) {
                orderItemsSum += order.getItemsCost();
            }
            orderItemsSum = orderItemsSum / areaManager.getOrdersHistory().size();
        }
        return orderItemsSum;
    }

    private int getOrdersNumInArea(AreaManager areaManager) {
        return areaManager.getOrdersHistory().size();
    }

    private int getStoresNumInArea(AreaManager areaManager) {
        return areaManager.getStoresList().size();
    }

    private int getItemsNumInArea(AreaManager areaManager) {
        return areaManager.getItemsList().size();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}

