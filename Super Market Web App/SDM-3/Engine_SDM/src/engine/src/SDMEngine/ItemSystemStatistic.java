package engine.src.SDMEngine;

import java.util.Map;
import java.util.Objects;

public class ItemSystemStatistic {
    private final int itemNumber;
    private double avgPrice;
    private int amountOfStoresThatSell;
    private double amountOfTimesBeenSold;

    public ItemSystemStatistic(int itemNumber) {
        this.itemNumber = itemNumber;
        avgPrice = 0;
        amountOfTimesBeenSold = 0;
        amountOfStoresThatSell = 0;
    }

    public ItemSystemStatistic(int itemNumber, double avgPrice, int amountOfStoresThatSell, int amountOfTimesBeenSold) {
        this.itemNumber = itemNumber;
        this.avgPrice = avgPrice;
        this.amountOfStoresThatSell = amountOfStoresThatSell;
        this.amountOfTimesBeenSold = amountOfTimesBeenSold;
    }

    public void addItemBeenSold(double amountThatBeenSold) { amountOfTimesBeenSold += amountThatBeenSold; }

    public boolean updateItemStatistic_NewStoreSellItem(Store newStoreThatSell, Map<Integer, Item> itemList) {
        boolean succeed = false;

        if(newStoreThatSell.getItemsList().containsKey(this.itemNumber) &&
                itemList.containsKey(this.itemNumber)) {
                succeed = true;

                double overallPriceOfAllItems = this.avgPrice * this.amountOfStoresThatSell;
                overallPriceOfAllItems += newStoreThatSell.getItemsList().get(this.itemNumber).getPrice();
                this.amountOfStoresThatSell++;
                this.avgPrice = overallPriceOfAllItems / this.amountOfStoresThatSell;
        }

        return succeed;
    }

    public boolean updateItemStatistic_PriceChangedInStore(double oldPrice, double newPrice) {
        boolean succeed = false;

        if(oldPrice >= 0 && newPrice >=0) {
            double avgPriceTimesNumberThatSell = this.amountOfStoresThatSell * this.avgPrice;
            avgPriceTimesNumberThatSell += (newPrice - oldPrice);
            this.avgPrice = avgPriceTimesNumberThatSell / this.amountOfStoresThatSell;
            succeed = true;
        }

        return succeed;
    }

    public boolean updateItemStatistic_ItemBeenRemovedFromStore(double priceInTheStore) {
        boolean succeed = false;

        if(this.amountOfStoresThatSell > 0 && priceInTheStore > 0) {
            succeed = true;

            double overAllPriceOfAllItems = this.avgPrice * this.amountOfStoresThatSell;
            this.amountOfStoresThatSell--;
            overAllPriceOfAllItems -= priceInTheStore;
            this.avgPrice = overAllPriceOfAllItems / this.amountOfStoresThatSell;
        }

        return succeed;
    }

    @Override
    public String toString() {
        return "ItemSystemStatistic{" +
                "itemNumber=" + itemNumber +
                ", avgPrice=" + avgPrice +
                ", amountOfStoresThatSell=" + amountOfStoresThatSell +
                ", amountOfTimesBeenSold=" + amountOfTimesBeenSold +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemSystemStatistic that = (ItemSystemStatistic) o;
        return itemNumber == that.itemNumber &&
                Double.compare(that.avgPrice, avgPrice) == 0 &&
                amountOfStoresThatSell == that.amountOfStoresThatSell &&
                amountOfTimesBeenSold == that.amountOfTimesBeenSold;
    }

    @Override
    public int hashCode() { return Objects.hash(itemNumber, avgPrice, amountOfStoresThatSell, amountOfTimesBeenSold); }

    public int getItemNumber() { return itemNumber; }

    public double getAvgPrice() { return avgPrice; }

    public int getAmountOfStoresThatSell() { return amountOfStoresThatSell; }

    public double getAmountOfTimesBeenSold() { return amountOfTimesBeenSold; }
}