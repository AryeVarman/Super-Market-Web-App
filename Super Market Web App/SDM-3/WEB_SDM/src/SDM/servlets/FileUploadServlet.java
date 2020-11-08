package SDM.servlets;

import SDM.utils.ServletUtils;
import SDM.utils.SessionUtils;
import engine.src.SDMEngine.AreaManager;
import engine.src.SDMEngine.StoreOwner;
import engine.src.SDMEngine.SystemManager;
import engine.src.SDMEngine.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.SequenceInputStream;
import java.util.*;
import java.util.stream.Stream;

import static SDM.constants.Constants.USER_TYPE_ERROR;

//@WebServlet("/uploadJSNative")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class FileUploadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("fileupload/form.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try(PrintWriter out = response.getWriter()) {

            Collection<Part> parts = request.getParts();

            List<InputStream> inputStreamList = new LinkedList<>();

            StringBuilder fileContent = new StringBuilder();
            for (Part part : parts) {
                fileContent.append(part.toString());
                inputStreamList.add(part.getInputStream());

            }

            InputStream inputStream = new SequenceInputStream(Collections.enumeration(inputStreamList));
            System.out.println(fileContent.toString());

            try {
                String currentUserName = SessionUtils.getUsername(request);
                StoreOwner currentUser = ServletUtils.getSystemManager(request.getServletContext()).getUserManager().getStoreOwner(currentUserName);
                if (currentUser != null) {
                    AreaManager areaManager = ServletUtils.getSystemManager(request.getServletContext()).createNewArea(inputStream, currentUser);
                    out.print(areaManager.getAreaName() + " Area is added successfully!");
                } else {
                    out.print(USER_TYPE_ERROR);
                }
            } catch (Exception e) {
                out.print(e.getMessage());
            }
            out.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }
}