package engine.src.SDMEngine;

public class SaleOfferForGui{
    private  int itemId;
    private  double amountNeeded;
    private double pricePerUnit;
    private Store store;

    public SaleOfferForGui(int itemId, double amountNeeded, double pricePerUnit) {
        this.itemId = itemId;
        this.amountNeeded = amountNeeded;
        this.pricePerUnit = pricePerUnit;
    }

    public double getAmountNeeded() {
        return amountNeeded;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public int getItemId() {
        return itemId;
    }

    public Store getStore() {
        return store;
    }

}
