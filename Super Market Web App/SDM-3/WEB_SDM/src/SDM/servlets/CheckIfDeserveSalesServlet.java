package SDM.servlets;

import SDM.JsonObjects.JsonSale;
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

public class CheckIfDeserveSalesServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String userName = SessionUtils.getUsername(request);
            String currentAreaName = SessionUtils.getSessionManager().get(userName).getCurrentAreaName();

            String smart = request.getParameter("smart");
            AreaManager areaManager = ServletUtils.getSystemManager(getServletContext()).getAreasMap().get(currentAreaName);
            Order order;
            List<Sale> saleList = new LinkedList<>();

            if(smart != null && smart.equals("yes")){
                order = areaManager.getCurrentCustomersSmartOrders().get(userName);
                for (Store store: order.getOrderStoresList()) {
                    List<Sale> newSaleList = store.getSalesDeserved(order);
                    for (Sale sale: newSaleList) {
                        saleList.add(sale);
                    }
                }
            }
            else{
                int storeId = Integer.parseInt(request.getParameter("storeId"));
                Store store = areaManager.getStoresList().get(storeId);
                order = areaManager.getCurrentCustomersOrders().get(userName).get(storeId);
                saleList = store.getSalesDeserved(order);
            }


            List<JsonSale> jsonSaleList = new LinkedList<>();
            for (Sale sale: saleList) {
                JsonSale jsonSale = new JsonSale(sale.getName(),sale.getSaleType(),sale.getStore().getName(),sale.getStore().getSerialNumber());
                for (Sale.SaleOffer saleOffer: sale.getSaleOfferList()) {
                    jsonSale.addJsonSaleOffer(saleOffer.getItemId(),areaManager.getItemsList().get(saleOffer.getItemId()).getName() ,saleOffer.getAmountNeeded(),saleOffer.getPricePerUnit());
                }
                jsonSale.setJsonSaleTrigger(sale.getSaleTrigger().getItemId(),areaManager.getItemsList().get(sale.getSaleTrigger().getItemId()).getName() ,sale.getSaleTrigger().getAmountNeeded());
                jsonSaleList.add(jsonSale);
            }
            out.print(new Gson().toJson(jsonSaleList));
            System.out.println(new Gson().toJson(jsonSaleList));
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}
