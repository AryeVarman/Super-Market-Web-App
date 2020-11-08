package SDM.JsonObjects;

public class JsonItem {
    private final String purchaseType;
    private final int serialNumber;
    private final String name;
    private final double avgPrice;
    private final int amountOfStoresThatSell;
    private final double amountOfTimesBeenSold;
    private final String uniqId;
    private double price;
    private double amountOfItem;
    private double pricePerUnit;
    private double totalCost;


    public JsonItem(int serialNumber, String name,String purchaseType, double avgPrice,
                    int amountOfStoresThatSell, double amountOfTimesBeenSold, String uniqId) {
        this.purchaseType = purchaseType;
        this.serialNumber = serialNumber;
        this.name = name;
        this.avgPrice = avgPrice;
        this.amountOfStoresThatSell = amountOfStoresThatSell;
        this.amountOfTimesBeenSold = amountOfTimesBeenSold;
        this.uniqId = uniqId.replace(' ','_');
    }

    public void setPrice(double price) {
        this.price = price;
    }

}
