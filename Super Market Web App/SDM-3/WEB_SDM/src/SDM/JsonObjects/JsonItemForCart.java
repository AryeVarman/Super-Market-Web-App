package SDM.JsonObjects;

public class JsonItemForCart {
    private final String purchaseType;
    private final int serialNumber;
    private final String name;
    private final double amountOfItem;
    private final double pricePerUnit;
    private final double totalCost;
    private final String fromSale;
    private final String storeName;
    private final int storeId;

    public JsonItemForCart(int serialNumber, String name, String purchaseType,
                           double amountOfItem, double pricePerUnit, double totalCost, String fromSale, String storeName, int storeId) {
        this.purchaseType = purchaseType;
        this.serialNumber = serialNumber;
        this.name = name;
        this.amountOfItem = amountOfItem;
        this.pricePerUnit = pricePerUnit;
        this.totalCost = totalCost;
        this.fromSale = fromSale;
        this.storeName = storeName;
        this.storeId = storeId;
    }
}
