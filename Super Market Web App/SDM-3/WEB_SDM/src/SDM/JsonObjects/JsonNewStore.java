package SDM.JsonObjects;

public class JsonNewStore {
    private final String areaName;
    private final String storeOwnerName;
    private final String storeName;
    private final int locationX;
    private final int locationY;
    private final int numberOfItemsInStore;
    private final int numberOfItemsArea;

    public JsonNewStore(String areaName, String storeOwnerName, String storeName, int locationX, int locationY,
                        int numberOfItemsInStore, int numberOfItemsArea) {
        this.areaName = areaName;
        this.storeOwnerName = storeOwnerName;
        this.storeName = storeName;
        this.locationX = locationX;
        this.locationY = locationY;
        this.numberOfItemsInStore = numberOfItemsInStore;
        this.numberOfItemsArea = numberOfItemsArea;
    }
}
