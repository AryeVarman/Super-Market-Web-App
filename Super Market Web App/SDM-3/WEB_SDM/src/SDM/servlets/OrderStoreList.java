package SDM.servlets;

import SDM.JsonObjects.JsonStoreShort;
import SDM.utils.ServletUtils;
import SDM.utils.SessionUtils;
import com.google.gson.Gson;
import engine.src.SDMEngine.AreaManager;
import engine.src.SDMEngine.Order;
import engine.src.SDMEngine.Store;
import engine.src.SDMEngine.SystemManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

public class OrderStoreList extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        try(PrintWriter out = response.getWriter()) {

            int orderNum = Integer.parseInt(request.getParameter("orderNumber"));

            SystemManager systemManager = ServletUtils.getSystemManager(getServletContext());
            String areaName = SessionUtils.getSessionManager().get(SessionUtils.getUsername(request)).getCurrentAreaName();
            AreaManager areaManager = systemManager.getAreasMap().get(areaName);

            Order order = areaManager.getOrderByOrderSerialNumber(orderNum);

            List<JsonStoreShort> jsonStoreList = new LinkedList<>();

            for(Store store : order.getOrderStoresList()) {
                jsonStoreList.add(new JsonStoreShort(store.getSerialNumber(), store.getName(), store.getStoreOwner().getName()));
            }

            Gson gson = new Gson();
            String jsonObject = gson.toJson(jsonStoreList);

            System.out.println(jsonObject);
            out.print(jsonObject);
            out.flush();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}