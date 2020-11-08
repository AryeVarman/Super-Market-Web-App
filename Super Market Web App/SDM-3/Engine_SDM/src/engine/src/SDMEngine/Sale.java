package engine.src.SDMEngine;

import java.util.*;

public class Sale {

    public class SaleTrigger {

        private final int itemId;
        private final double amountNeeded;
        private Store store;

        public SaleTrigger(int itemId, double amountNeeded) {
            this.itemId = itemId;
            this.amountNeeded = amountNeeded;
        }

        public SaleTrigger(int itemId, double amountNeeded,Store store) {
            this.itemId = itemId;
            this.amountNeeded = amountNeeded;
            this.store = store;
        }

        public void setStore(Store store) {
            if (store != null) {
                this.store = store;
            }
        }

        public Store getStore() { return store; }

        public int getItemId() {
            return itemId;
        }

        public double getAmountNeeded() {
            return amountNeeded;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SaleTrigger that = (SaleTrigger) o;
            return itemId == that.itemId &&
                    Double.compare(that.amountNeeded, amountNeeded) == 0;
        }

        @Override
        public int hashCode() { return Objects.hash(itemId, amountNeeded); }

        @Override
        public String toString() { return "Buy " + this.amountNeeded + " from " + this.store.getItemsList().get(itemId).getName(); }

        public String details() { return this.amountNeeded + " of " + this.store.getItemsList().get(itemId).getName() +
                " from " + store.getName(); }

    }
    public class SaleOffer {

        private final int itemId;
        private final double amountNeeded;
        private double pricePerUnit;
        private Store store;

        public SaleOffer(int itemId, double amountNeeded, double pricePerUnit) {
            this.itemId = itemId;
            this.amountNeeded = amountNeeded;
            this.pricePerUnit = pricePerUnit;
        }

        public int getItemId() { return itemId; }

        public double getAmountNeeded() { return amountNeeded; }

        public Store getStore() { return store; }

        public double getPricePerUnit() { return pricePerUnit; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SaleOffer saleOffer = (SaleOffer) o;
            return itemId == saleOffer.itemId &&
                    Double.compare(saleOffer.amountNeeded, amountNeeded) == 0 &&
                    Double.compare(saleOffer.pricePerUnit, pricePerUnit) == 0;
        }

        @Override
        public int hashCode() { return Objects.hash(itemId, amountNeeded, pricePerUnit); }

        @Override
        public String toString() {
            Currency currency = Currency.getInstance(Locale.US);
            return amountNeeded + " from " + this.store.getItemsList().get(this.itemId).getName() +
                    " for " + String.format("%.2f", pricePerUnit) + currency.getSymbol() + " per one";
        }

        public void setStore(Store store) {
            if(store != null) {
                this.store = store;
            }
        }


    }

    public enum SaleType {
        irrelevant, oneOf, allOrNothing
    }

    private String name;
    private final SaleTrigger saleTrigger;
    private Collection<SaleOffer> saleOfferList;
    private SaleType saleType;
    private Store store;

    public Sale(String name, int triggerItemId, double triggerAmountNeeded) {
        this.name = name;
        this.saleTrigger = new SaleTrigger(triggerItemId, triggerAmountNeeded);
        this.saleOfferList = new HashSet<>();
        this.saleType = SaleType.irrelevant;
    }

    public Sale(Store store ,String name, int triggerItemId, double triggerAmountNeeded,SaleType saleType) {
        this.name = name;
        this.saleTrigger = new SaleTrigger(triggerItemId, triggerAmountNeeded,store);
        this.saleOfferList = new HashSet<>();
        this.saleType = saleType;
        this.store = store;
    }

    public boolean addOfferToSale(int itemId, double amountNeeded, double pricePerUnit) {
        boolean succeed = false;

        if(itemId > 0 && amountNeeded > 0 && pricePerUnit >= 0) {
            SaleOffer saleOffer = new SaleOffer(itemId, amountNeeded, pricePerUnit);

            if(this.store != null) {
                saleOffer.setStore(this.store);
            }

            succeed = this.saleOfferList.add(saleOffer);

        }
        return succeed;
    }

    public Store getStore() { return store; }

    public SaleTrigger getSaleTrigger() { return saleTrigger; }

    public Collection<SaleOffer> getSaleOfferList() { return saleOfferList; }

    public void setSaleOfferList(Collection<SaleOffer> saleOfferList) {
        if(saleOfferList != null) {
            this.saleOfferList = saleOfferList;
        }
    }

    public void setStore(Store store) {
        if(store != null) {
            this.store = store;
            this.saleTrigger.setStore(store);
            for(SaleOffer saleOffer : saleOfferList) {
                saleOffer.setStore(store);
            }
        }
    }

    public String getName() { return name; }

    public void setName(String name) {
        if(name != null) {
            this.name = name;
        }
    }

    public SaleType getSaleType() { return saleType; }

    public void setSaleType(SaleType saleType) {
        if(saleType != null) {
            this.saleType = saleType;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sale sale = (Sale) o;
        return saleTrigger.equals(sale.saleTrigger) &&
                saleOfferList.equals(sale.saleOfferList) &&
                saleType == sale.saleType;
    }

    @Override
    public int hashCode() { return Objects.hash(saleTrigger, saleOfferList, saleType); }

    @Override
    public String toString() {
        String saleString = saleTrigger.toString() + '\n';

        if(saleType == SaleType.allOrNothing){
            saleString += "You can choose if to take all this offers:\n";
            for (SaleOffer saleOffer : saleOfferList) {
                saleString += saleOffer.toString() + '\n';
            }
        }
        else if(saleType == SaleType.oneOf){
            saleString += "You can choose only one of this offers:\n";
            for (SaleOffer saleOffer : saleOfferList) {
                saleString += saleOffer.toString() + '\n';
            }
        }
        else{
            saleString += "You can choose from this offers:\n";
            for (SaleOffer saleOffer : saleOfferList) {
                saleString += saleOffer.toString() + '\n';
            }
        }
        return saleString;
    }
}
