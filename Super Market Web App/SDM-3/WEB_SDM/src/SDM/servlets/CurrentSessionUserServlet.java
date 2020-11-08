package SDM.servlets;

import SDM.JsonObjects.JsonUser;
import SDM.utils.ServletUtils;
import SDM.utils.SessionUtils;
import com.google.gson.Gson;
import engine.src.SDMEngine.User;
import engine.src.SDMEngine.UserManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class CurrentSessionUserServlet extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

            //returning JSON objects, not HTML
            response.setContentType("application/json;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                Gson gson = new Gson();
                UserManager userManager = ServletUtils.getSystemManager(getServletContext()).getUserManager();

                String currentUserName = SessionUtils.getUsername(request);
                User currentUser = userManager.getUsers().get(currentUserName);

                JsonUser jsonUser = new JsonUser(currentUser.getName(), currentUser.getUserType().toString());

                String json = gson.toJson(jsonUser);
                System.out.println(json);
                out.println(json);
                out.flush();
            }
            catch (Exception ex) {
                ex.printStackTrace();
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
}
