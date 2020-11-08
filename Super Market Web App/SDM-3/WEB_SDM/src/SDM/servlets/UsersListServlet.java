package SDM.servlets;

import SDM.JsonObjects.JsonUser;
import SDM.utils.ServletUtils;
import engine.src.SDMEngine.SystemManager;
import engine.src.SDMEngine.User;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UsersListServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //returning JSON objects, not HTML
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            SystemManager systemManager = ServletUtils.getSystemManager(getServletContext());

            Collection<JsonUser> usersList = new LinkedList<>();

            for(User user : systemManager.getUserManager().getUsers().values()) {
                usersList.add(new JsonUser(user.getName(), user.getUserType().toString()));
            }

            String json = gson.toJson(usersList);

            System.out.println(json);
            out.println(json);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
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