package SDM.servlets;

import SDM.JsonObjects.JsonFeedbackAlert;
import SDM.JsonObjects.JsonItem;
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
import java.util.LinkedList;
import java.util.List;

public class GetFeedbacksServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {

            SystemManager systemManager = ServletUtils.getSystemManager(getServletContext());
            String areaName = SessionUtils.getSessionManager().get(SessionUtils.getUsername(request)).getCurrentAreaName();
            AreaManager areaManager = systemManager.getAreasMap().get(areaName);

            String userName = SessionUtils.getUsername(request);
            StoreOwner storeOwner = (StoreOwner) systemManager.getUserManager().getUserByName(userName);

            List<JsonFeedbackAlert> jsonFeedbackList = new LinkedList<>();

            if(areaManager != null && areaManager.getStoresList() != null) {
                for (Store store : areaManager.getStoresList().values()) {

                    if (store.getStoreOwner().equals(storeOwner)) {

                        for (Feedback feedback : store.getFeedbackList()) {
                            jsonFeedbackList.add(new JsonFeedbackAlert(feedback.getStore().getName(), feedback.getCustomer().getName(),
                                    feedback.getScore(), feedback.getVerbalFeedback(), feedback.getDate().toString()));
                        }

                    }
                }
            }

            Gson gson = new Gson();
            String jsonObject = gson.toJson(jsonFeedbackList);

            System.out.println(jsonObject);
            out.println(jsonObject);

        } catch (Exception ex) {
            ex.printStackTrace();
            out.print(Constants.ERROR_CANT_GET_FEEDBACKS);
        }
        finally {
            out.flush();
            out.close();
        }
    }
}
