package engine.src.SDMEngine.chat;

import engine.src.SDMEngine.Order;
import engine.src.SDMEngine.StoreOwner;
import engine.src.SDMEngine.User;

import java.util.*;

public class ChatManager {
    private final Map<User, MessagesList> userToMessageListMap;

    public ChatManager() { this.userToMessageListMap = new HashMap<>(); }

    public ChatManager(Map<User, MessagesList> userToMessageListMap) { this.userToMessageListMap = userToMessageListMap; }

    public void addMessageToAllUsers(String message, String writer) {
        for(MessagesList messagesList : userToMessageListMap.values()) {
            synchronized (messagesList) {
                messagesList.addNewMessage(message, writer);
            }
        }
    }

    public void addNewUser(User user) {
        if(!userToMessageListMap.containsKey(user)) {
            userToMessageListMap.put(user, new MessagesList());
        }
    }

    public List<Message> getMessagesForUser(User user) {
        List<Message> messages = null;

        if(this.userToMessageListMap.containsKey(user)) {
            messages = this.userToMessageListMap.get(user).getAllNewMessages();
            this.userToMessageListMap.put(user, new MessagesList());
        }

        return messages;
    }

    public Map<User, MessagesList> getUserToMessageListMap() { return userToMessageListMap; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatManager that = (ChatManager) o;
        return userToMessageListMap.equals(that.userToMessageListMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userToMessageListMap);
    }

    @Override
    public String toString() {
        return "ChatManager{" +
                "userToMessageListMap=" + userToMessageListMap +
                '}';
    }


}
