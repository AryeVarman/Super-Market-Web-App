package SDM.JsonObjects;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class JsonStoreOrder {

    public class JsonStoreItemOrder {

        private final int serialNum;
        private final String name;
        private final String purchaseMethod;
        private final double amount;
        private final double unitPrice;
        private final double totalPrice;
        private final boolean isOnSale;

        public JsonStoreItemOrder(int serialNum, String name, String purchaseMethod, double amount, double unitPrice,
                                  double totalPrice, boolean isOnSale) {
            this.serialNum = serialNum;
            this.name = name;
            this.purchaseMethod = purchaseMethod;
            this.amount = amount;
            this.unitPrice = unitPrice;
            this.totalPrice = totalPrice;
            this.isOnSale = isOnSale;
        }
    }

    private final int serialNum;
    private final String date;
    private final String customerName;
    private final int locationX;
    private final int locationY;
    private final int numOfItems;
    private final double itemsCost;
    private final double deliveryCost;
    private final double totalCost;
    private final List<JsonStoreItemOrder> itemList;

    public JsonStoreOrder(int serialNum, LocalDate date, String customerName, int locationX, int locationY,
                          int numOfItems, double itemsCost, double deliveryCost, double totalCost) {
        this.serialNum = serialNum;
        this.date = date.toString();
        this.customerName = customerName;
        this.locationX = locationX;
        this.locationY = locationY;
        this.numOfItems = numOfItems;
        this.itemsCost = itemsCost;
        this.deliveryCost = deliveryCost;
        this.totalCost = totalCost;
        this.itemList = new LinkedList<>();
    }

    public void addItemToList(int serialNum, String name, String purchaseMethod, double amount, double unitPrice,
                              double totalPrice, boolean isOnSale) {
        this.itemList.add(new JsonStoreItemOrder(serialNum, name, purchaseMethod, amount, unitPrice, totalPrice, isOnSale));
    }
}
