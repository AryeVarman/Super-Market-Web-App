package engine.src.SDMEngine;

import java.io.Serializable;
import java.util.Objects;

public abstract class Item implements Serializable {



    private final int serialNumber;
    private String name;
    private boolean isBeingSoldByStore;

    public Item(int serialNumber, String name) {
        this.serialNumber = serialNumber;
        this.name = name.toLowerCase();
        isBeingSoldByStore = false;
    }

    public void setBeingSoldByStore(boolean beingSoldByStore) { isBeingSoldByStore = beingSoldByStore; }

    public boolean isBeingSoldByStore() { return isBeingSoldByStore; }

    public int getSerialNumber() { return serialNumber; }

    public String getName() { return name; }

    public abstract String getPurchaseMethod();

    abstract void isValidAmountOfItem(String amountStr) throws Exception;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return serialNumber == item.serialNumber;
    }

    @Override
    public int hashCode() { return Objects.hash(serialNumber); }

    @Override
    public String toString() { return "name: " + name + " | serialNumber: " + serialNumber + " | "; }
}