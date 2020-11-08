package SDM.servlets;

import SDM.constants.Constants;
import SDM.utils.ServletUtils;
import SDM.utils.SessionUtils;
import com.google.gson.Gson;
import engine.src.SDMEngine.DigitalWallet;
import engine.src.SDMEngine.User;
import engine.src.SDMEngine.UserManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChargeWalletServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            UserManager userManager = ServletUtils.getSystemManager(getServletContext()).getUserManager();

            String userName = SessionUtils.getUsername(request);
            String transactionTypeStr = request.getParameter("transactionType");
            String transactionAmountStr = request.getParameter("transactionAmount");

            double transactionAmount = Double.parseDouble(transactionAmountStr);
            String dateStr = request.getParameter("date");

            Date date = getTransactionDate(dateStr);
            DigitalWallet.TransactionType transactionType = getTransactionType(transactionTypeStr);

            if (date != null && userManager.getUsers().containsKey(userName)) {
                User user = userManager.getUsers().get(userName);
                user.makeTransaction(transactionType, date, transactionAmount);

                Gson gson = new Gson();
                DigitalWallet digitalWallet = user.getDigitalWallet();

                String walletJson = gson.toJson(digitalWallet);
                System.out.println(walletJson);
                out.print(walletJson);
            }
            else { out.print(Constants.WALLET_CHARGE_FAIL); }

        } catch (Exception ex) {
            ex.printStackTrace();
            out.print(Constants.WALLET_CHARGE_FAIL);
        }
        finally {
            out.flush();
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            UserManager userManager = ServletUtils.getSystemManager(getServletContext()).getUserManager();
            String userName = SessionUtils.getUsername(request);

            if (userManager.getUsers().containsKey(userName)) {
                DigitalWallet digitalWallet =  userManager.getUsers().get(userName).getDigitalWallet();

                Gson gson = new Gson();
                String walletJson = gson.toJson(digitalWallet);

                System.out.println(walletJson);
                out.print(walletJson);
            }
            else { out.print(Constants.WALLET_CHARGE_FAIL); }

        } catch (Exception ex) {
            ex.printStackTrace();
            out.print(Constants.WALLET_CHARGE_FAIL);
        }
        finally {
            out.close();
        }
    }

    private DigitalWallet.TransactionType getTransactionType(String typeAsString) {
        DigitalWallet.TransactionType transactionType;

        if(typeAsString.toUpperCase().equals("PAY")) {
            transactionType = DigitalWallet.TransactionType.PAY;
        }
        else if(typeAsString.toUpperCase().equals("RECEIVE")) {
            transactionType = DigitalWallet.TransactionType.RECEIVE;
        }
        else {
            transactionType = DigitalWallet.TransactionType.CHARGE;
        }

        return transactionType;
    }

    private Date getTransactionDate(String dateAsString) {
        Date date = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-dd-MM");
        try {
            date = dateFormat.parse(dateAsString);
        } catch (Exception ignored) { }

        return date;
    }
}