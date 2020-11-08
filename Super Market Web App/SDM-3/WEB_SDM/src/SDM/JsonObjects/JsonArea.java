package SDM.JsonObjects;

public class JsonArea {
    private final String areaName;
    private final String areaOwner;
    private final int itemsNum;
    private final int storesNum;
    private final int ordersNum;
    private final double orderItemsAvgPrice;

    public JsonArea(String areaName, String areaOwner, int itemsNum, int storesNum, int ordersNum, double orderItemsAvgPrice) {
        this.areaName = areaName;
        this.areaOwner = areaOwner;
        this.itemsNum = itemsNum;
        this.storesNum = storesNum;
        this.ordersNum = ordersNum;
        this.orderItemsAvgPrice = orderItemsAvgPrice;
    }

}
