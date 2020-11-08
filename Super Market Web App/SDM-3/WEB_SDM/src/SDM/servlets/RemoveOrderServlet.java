package SDM.servlets;

import SDM.constants.Constants;
import SDM.utils.ServletUtils;
import SDM.utils.SessionUtils;
import com.google.gson.Gson;
import engine.src.SDMEngine.AreaManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class RemoveOrderServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try{
            String userName = SessionUtils.getUsername(request);
            String smart = request.getParameter("smart");

            String currentAreaName = SessionUtils.getSessionManager().get(userName).getCurrentAreaName();
            AreaManager areaManager = ServletUtils.getSystemManager(getServletContext()).getAreasMap().get(currentAreaName);
            if(smart != null && smart.equals("yes")){
                areaManager.getCurrentCustomersSmartOrders().remove(userName);
            }else{
                int storeId = Integer.parseInt(request.getParameter("storeId"));

                areaManager.getCurrentCustomersOrders().get(userName).remove(storeId);
            }
            out.print(new Gson().toJson("success"));
        }catch (Exception e){
            e.printStackTrace();
            out.print(e.getMessage());
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
