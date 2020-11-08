package SDM.servlets;

import SDM.constants.Constants;
import SDM.utils.ServletUtils;
import SDM.utils.SessionUtils;
import com.google.gson.Gson;
import engine.src.SDMEngine.SystemManager;
import engine.src.SDMEngine.User;
import engine.src.SDMEngine.chat.Message;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class PostChatMessage extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            SystemManager systemManager = ServletUtils.getSystemManager(getServletContext());
            String userName = SessionUtils.getUsername(request);
            User user = systemManager.getUserManager().getUserByName(userName);

            String msg = request.getParameter("message");

            systemManager.getChatManager().addMessageToAllUsers(msg, userName);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            out.print(new Gson().toJson(Constants.POST_MESSAGES_ERROR));
        }
        finally {
            out.flush();
            out.close();
        }


    }
}
