package SDM.JsonObjects;


public class JsonStoreItem {
    private String itemName;
    private int itemId;
    private double itemPrice;

    public JsonStoreItem(String itemName, int itemId, double itemPrice) {
        this.itemName = itemName;
        this.itemId = itemId;
        this.itemPrice = itemPrice;
    }


    public String getItemName() {
        return itemName;
    }

    public int getItemId() {
        return itemId;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }
}
