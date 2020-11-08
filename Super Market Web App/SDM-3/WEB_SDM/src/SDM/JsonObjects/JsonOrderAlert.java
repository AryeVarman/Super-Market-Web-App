package SDM.JsonObjects;

public class JsonOrderAlert {
    private final String storeName;
    private final int orderNumber;
    private final String clientName;
    private final int numOfItemsTypes;
    private final double itemsCost;
    private final double deliveryCost;

    public JsonOrderAlert(String storeName, int orderNumber, String clientName,
                          int numOfItemsTypes, double itemsCost, double deliveryCost) {
        this.storeName = storeName;
        this.orderNumber = orderNumber;
        this.clientName = clientName;
        this.numOfItemsTypes = numOfItemsTypes;
        this.itemsCost = itemsCost;
        this.deliveryCost = deliveryCost;
    }
}
