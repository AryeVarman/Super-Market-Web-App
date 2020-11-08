package SDM.JsonObjects;

import java.util.List;

public class JsonStore {
    private final int serialNumber;
    private final String name;
    private final String ownerName;
    private final String location;
    private final int numOfOrderMade;
    private final double profitFromItems;
    private final double PPK;
    private final double profitFromDeliveries;
    private final List<JsonItem> itemList;
    private final String uniqId;
    private String areaName;

    public JsonStore(int serialNumber, String name, String ownerName, String location,
                     int numOfOrderMade, double profitFromItems, double PPK, double profitFromDeliveries, List<JsonItem> itemList) {
        this.serialNumber = serialNumber;
        this.name = name;
        this.ownerName = ownerName;
        this.location = location;
        this.numOfOrderMade = numOfOrderMade;
        this.profitFromItems = profitFromItems;
        this.PPK = PPK;
        this.profitFromDeliveries = profitFromDeliveries;
        this.itemList = itemList;
        this.uniqId = serialNumber + name;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}
