package SDM.JsonObjects;

import engine.src.SDMEngine.Coordinate;

import java.util.LinkedList;
import java.util.List;


public class JsonSmartOrder {
    private final int storeId;
    private final String storeName;
    private final double PPK;
    private final int locationX;
    private final int locationY;
    private List<JsonItemForCart> itemsList;

    public JsonSmartOrder(int storeId, String storeName, double PPK, int locationX, int locationY) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.PPK = PPK;
        this.itemsList = new LinkedList<>();
        this.locationX = locationX;
        this.locationY = locationY;
    }

    public List<JsonItemForCart> getItemsList() {
        return itemsList;
    }
}
