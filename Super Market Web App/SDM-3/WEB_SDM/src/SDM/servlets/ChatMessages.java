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

public class ChatMessages extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            SystemManager systemManager = ServletUtils.getSystemManager(getServletContext());
            String userName = SessionUtils.getUsername(request);
            User user = systemManager.getUserManager().getUserByName(userName);

            List<Message> messageList = systemManager.getChatManager().getMessagesForUser(user);

            Gson gson = new Gson();
            String messagesJson = gson.toJson(messageList);
            out.print(messagesJson);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            out.print(new Gson().toJson(Constants.MESSAGES_ERROR));
        }
        finally {
            out.flush();
            out.close();
        }
    }
}
