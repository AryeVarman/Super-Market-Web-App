package SDM.JsonObjects;

public class JsonStoreShort {

    private final int serialNumber;
    private final String name;
    private final String storeOwnerName;

    public JsonStoreShort(int serialNumber, String name, String storeOwnerName) {
        this.serialNumber = serialNumber;
        this.name = name;
        this.storeOwnerName = storeOwnerName;
    }
}
