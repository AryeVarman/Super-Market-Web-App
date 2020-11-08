package engine.src.SDMEngine;

import engine.src.SDMEngine.chat.ChatManager;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SystemManager {
    private final UserManager userManager;
    private final Map<String, AreaManager> areasMap;
    private final OrderAlertsManager orderAlertsManager;
    private final FeedbackAlertManager feedbackAlertManager;
    private final NewStoreAlertManager newStoreAlertManager;
    private final ChatManager chatManager;

    public SystemManager() {
        userManager = new UserManager();
        areasMap = new HashMap<>();
        orderAlertsManager = new OrderAlertsManager();
        feedbackAlertManager = new FeedbackAlertManager();
        newStoreAlertManager = new NewStoreAlertManager();
        chatManager = new ChatManager();
    }

    public AreaManager createNewArea(InputStream inputStream, StoreOwner storeOwner) throws Exception {
        AreaManager newAreaManager = new AreaManager(storeOwner, this);

        try {
            newAreaManager.loadDataFromFile(inputStream, this.areasMap.keySet());
            areasMap.put(newAreaManager.getAreaName(), newAreaManager);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        return newAreaManager;
    }

    public UserManager getUserManager() { return userManager; }

    public Map<String, AreaManager> getAreasMap() { return areasMap; }

    public OrderAlertsManager getOrderAlertsManager() { return orderAlertsManager; }

    public FeedbackAlertManager getFeedbackAlertManager() { return feedbackAlertManager; }

    public NewStoreAlertManager getNewStoreAlertManager() { return newStoreAlertManager; }

    public ChatManager getChatManager() { return chatManager; }

    public void addNewUserToSystem(String username, String userType) {
        userManager.addUser(username, userType);

        User user = userManager.getUserByName(username);

        if (user != null) {
            chatManager.addNewUser(user);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemManager that = (SystemManager) o;
        return userManager.equals(that.userManager) &&
                areasMap.equals(that.areasMap);
    }

    @Override
    public int hashCode() { return Objects.hash(userManager, areasMap); }

    @Override
    public String toString() {
        return "SystemManager{" +
                "userManager=" + userManager +
                ", areaMap=" + areasMap +
                '}';
    }
}