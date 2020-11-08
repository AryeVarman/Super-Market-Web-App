package SDM.JsonObjects;

public class JsonUser {
    private final String name;
    private final String userType;

    public JsonUser(String name, String userType) {
        this.name = name;
        this.userType = userType;
    }

    public String getName() { return name; }
    public String getUserType() { return userType; }
}
