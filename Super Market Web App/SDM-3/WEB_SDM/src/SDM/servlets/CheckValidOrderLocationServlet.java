package SDM.servlets;

import SDM.JsonObjects.JsonSmartOrder;
import SDM.constants.Constants;
import SDM.utils.ServletUtils;
import SDM.utils.SessionUtils;
import com.google.gson.Gson;
import engine.src.SDMEngine.AreaManager;
import engine.src.SDMEngine.Customer;
import engine.src.SDMEngine.Order;
import engine.src.SDMEngine.UserManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class CheckValidOrderLocationServlet extends HttpServlet {


    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            UserManager userManager = ServletUtils.getSystemManager(getServletContext()).getUserManager();
            String userName = SessionUtils.getUsername(request);

            String locationX = request.getParameter("locationX");
            String locationY = request.getParameter("locationY");
            String smart = request.getParameter("smart");

            String currentAreaName = SessionUtils.getSessionManager().get(userName).getCurrentAreaName();

            AreaManager areaManager = ServletUtils.getSystemManager(getServletContext()).getAreasMap().get(currentAreaName);

            if(smart != null && smart.equals("true")){
                Object element = areaManager.getCoordinateMap().getElementInLocationXY(Integer.parseInt(locationX),Integer.parseInt(locationY));
                if(element != null){
                    out.print(new Gson().toJson(Constants.ERROR_INIT + "your location cannot be as a location store"));
                }else{
                    out.print(new Gson().toJson("success"));
                }
            }else{
                int storeId = Integer.parseInt(request.getParameter("storeId"));
                areaManager.checkIfLocationInputValid(locationX,locationY,areaManager.getStoresList().get(storeId));
                out.print(new Gson().toJson("success"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.print(new Gson().toJson(Constants.ERROR_INIT + e.getMessage()));
        }
        finally {
            out.flush();
            out.close();
        }
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
