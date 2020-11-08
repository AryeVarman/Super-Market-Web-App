package SDM.servlets;

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
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ConfirmOrderServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String errorMsg = "";
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try{
            String smart = request.getParameter("smart");
            String locationX = request.getParameter("locationX");
            String locationY = request.getParameter("locationX");
            int x = Integer.parseInt(locationX);
            int y = Integer.parseInt(locationY);
            String dateStr = request.getParameter("date");

            UserManager userManager = ServletUtils.getSystemManager(getServletContext()).getUserManager();
            String userName = SessionUtils.getUsername(request);
            User user = userManager.getUserMap().get(userName);
            String currentAreaName = SessionUtils.getSessionManager().get(userName).getCurrentAreaName();
            AreaManager areaManager = ServletUtils.getSystemManager(getServletContext()).getAreasMap().get(currentAreaName);

            if(smart != null && smart.equals("true")){
                if(areaManager.getCurrentCustomersSmartOrders().containsKey(userName)){
                    Order order = areaManager.getCurrentCustomersSmartOrders().get(userName);
                    order.updateParametersForSimpleOrder(new Coordinate(x, y), parseStringDate(dateStr));
                    areaManager.updateInformationAfterCompleteOrder(order);
                    user.makeTransaction(DigitalWallet.TransactionType.PAY,Date.from(order.getDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),order.getGeneralOrderPrice());
                    areaManager.getCurrentCustomersSmartOrders().remove(userName,order);
                    out.print( new Gson().toJson(order.getSerialNumber()));
                }else{
                    errorMsg = Constants.ERROR_INIT + "Cannot confirm order, your cart is empty";
                }
            }else{
                if(areaManager.getCurrentCustomersOrders().containsKey(userName)){
                    int storeId = Integer.parseInt(request.getParameter("storeId"));
                    if(areaManager.getCurrentCustomersOrders().get(userName).containsKey(storeId)){
                        areaManager.checkIfLocationInputValid(locationX,locationY,areaManager.getStoresList().get(storeId));
                        Order order = areaManager.getCurrentCustomersOrders().get(userName).get(storeId);
                        order.updateParametersForSimpleOrder(new Coordinate(x, y), parseStringDate(dateStr));
                        areaManager.updateInformationAfterCompleteOrder(order);
                        user.makeTransaction(DigitalWallet.TransactionType.PAY,Date.from(order.getDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),order.getGeneralOrderPrice());
                        areaManager.getCurrentCustomersOrders().get(userName).remove(storeId);
                        out.print( new Gson().toJson(order.getSerialNumber()));
                    }
                    else{
                        errorMsg = Constants.ERROR_INIT + "Cannot confirm order, your cart is empty";
                    }
                }else{
                    errorMsg = Constants.ERROR_INIT + "Cannot confirm order, your cart is empty";
                }
                if(errorMsg != ""){
                    out.print( new Gson().toJson(errorMsg));
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            out.print(Constants.ERROR_INIT + e.getMessage());
        }
        finally {
            out.flush();
            out.close();
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    private LocalDate parseStringDate(String dateAsString) {
        LocalDate date = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            date = LocalDate.parse(dateAsString, formatter);
        } catch (Exception ignored) { }

        return date;
    }

}
