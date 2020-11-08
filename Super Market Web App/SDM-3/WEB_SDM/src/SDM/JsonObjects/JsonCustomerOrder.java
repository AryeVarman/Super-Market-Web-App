package SDM.JsonObjects;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class JsonCustomerOrder {

    public class JsonCustomerOrderItem {
        private final int serialNum;
        private final String name;
        private final String purchaseMethod;
        private final int storeNum;
        private final String storeName;
        private final double amount;
        private final double unitPrice;
        private final double totalPrice;
        private final boolean isOnSale;

        public JsonCustomerOrderItem(int serialNum, String name, String purchaseMethod,
                                     int storeNum, String storeName, double amount, double unitPrice, double totalPrice, boolean isOnSale) {
            this.serialNum = serialNum;
            this.name = name;
            this.purchaseMethod = purchaseMethod;
            this.storeNum = storeNum;
            this.storeName = storeName;
            this.amount = amount;
            this.unitPrice = unitPrice;
            this.totalPrice = totalPrice;
            this.isOnSale = isOnSale;
        }
    }

    private final int serialNum;
    private final String date;
    private final int locationX;
    private final int locationY;
    private final int numOfStores;
    private final int numOfItems;
    private final double itemsCost;
    private final double deliveryCost;
    private final double totalCost;
    private final List<JsonCustomerOrderItem> itemsList;

    public JsonCustomerOrder(int serialNum, LocalDate date, int locationX, int locationY, int numOfStores,
                             int numOfItems, double itemsCost, double deliveryCost, double totalCost) {

        this.serialNum = serialNum;
        this.date = date.toString();
        this.locationX = locationX;
        this.locationY = locationY;
        this.numOfStores = numOfStores;
        this.numOfItems = numOfItems;
        this.itemsCost = itemsCost;
        this.deliveryCost = deliveryCost;
        this.totalCost = totalCost;
        this.itemsList = new LinkedList<>();
    }

    public void addItemToList(int serialNum, String name, String purchaseMethod,
                              int storeNum, String storeName, double amount, double unitPrice, double totalPrice, boolean isOnSale) {
        itemsList.add(new JsonCustomerOrderItem(serialNum, name, purchaseMethod, storeNum, storeName, amount, unitPrice, totalPrice, isOnSale));
    }
}