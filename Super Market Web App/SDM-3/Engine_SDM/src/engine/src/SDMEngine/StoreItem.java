package engine.src.SDMEngine;

import java.io.Serializable;
import java.util.Objects;

public class StoreItem implements Serializable {

    private final Item item;
    private double price;

    @Override
    public String toString() {
        return "Item - " + item.toString() +
                " | price: " + price +'\n';
    }

    public StoreItem(Item item, double price) throws RuntimeException {
        String exceptionMsg = "";

        if(item != null) {
            this.item = item;
        }
        else {
            exceptionMsg += "Store item can't be null\n";
            this.item = null;
        }
        if(price >= 0) {
            this.price = price;
        }
        else {
            exceptionMsg += "item name: " + item.getName() + " has negative price\n";
        }

        if (exceptionMsg != "") {
            throw new RuntimeException(exceptionMsg);
        }

    }

    public boolean setPrice(double price) {
        boolean succeed = false;
        if(price > 0) {
            this.price = price;
            succeed = true;
        }

        return succeed;
    }

    public Item getItem() { return item; }

    public int getSerialNumber() { return this.item.getSerialNumber(); }

    public String getName() { return this.item.getName(); }

    public double getPrice() { return price; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoreItem storeItem = (StoreItem) o;
        return Double.compare(storeItem.price, price) == 0 &&
                item.equals(storeItem.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, price);
    }
}