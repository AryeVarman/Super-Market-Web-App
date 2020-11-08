package engine.src.SDMEngine;

import java.util.*;

/*
Adding and retrieving users is synchronized and in that manner - these actions are thread safe
Note that asking if a user exists (isUserExists) does not participate in the synchronization and it is the responsibility
of the user of this class to handle the synchronization of isUserExists with other methods here on it's own
 */
public class UserManager {

    public enum UserType{CUSTOMER, STORE_OWNER}
    private int serialNumber;
    private final Map<String, User> userMap;

    public UserManager() {
        userMap = new HashMap<>();
        serialNumber = 1;
    }

    public synchronized void addUser(String username, String userType) {
        User newUser = createUser(username,userType);
        userMap.put(username, newUser);
        userMap.values().stream().forEach((user)-> System.out.println(user.toString()));
    }

    private User createUser(String usernameFromParameter, String userType) {
        User newUser;
        if(userType.equals(UserManager.UserType.CUSTOMER.toString().toLowerCase())){
            newUser = new Customer(serialNumber, usernameFromParameter);
        } else{
            newUser  =  new StoreOwner(serialNumber, usernameFromParameter);
        }
        serialNumber++;

        return newUser;
    }

    public synchronized void removeUser(String username) { userMap.remove(username); }

    public synchronized Map<String, User> getUsers() { return userMap; }

    public synchronized StoreOwner getStoreOwner(String name){
        if(userMap.containsKey(name) && userMap.get(name).getClass().equals(StoreOwner.class)){
            return (StoreOwner) userMap.get(name);
        }
        return null;
    }

    public User getUserByName(String storeOwnerName) {
        User user = null;
        if(this.userMap.containsKey(storeOwnerName)) {
            user = userMap.get(storeOwnerName);
        }
        return user;
    }

    public boolean isUserExists(String username) { return userMap.containsKey(username); }

    public Map<String, User> getUserMap() { return userMap; }
}
