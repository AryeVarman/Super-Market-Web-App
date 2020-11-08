package SDM.JsonObjects;

import engine.src.SDMEngine.Sale;
import engine.src.SDMEngine.Store;

import java.util.Collection;
import java.util.LinkedList;

public class JsonSale {

    public class JsonSaleTrigger{
        private final int itemId;
        private final double amountNeeded;
        private final String itemName;

        public JsonSaleTrigger(int itemId, String itemName, double amountNeeded) {
            this.itemId = itemId;
            this.amountNeeded = amountNeeded;
            this.itemName = itemName;
        }
    }

    public class JsonSaleOffer{
        private final int itemId;
        private final double amountNeeded;
        private final double pricePerUnit;
        private final String itemName;

        public JsonSaleOffer(int itemId, String itemName, double amountNeeded, double pricePerUnit) {
            this.itemId = itemId;
            this.amountNeeded = amountNeeded;
            this.pricePerUnit = pricePerUnit;
            this.itemName = itemName;
        }
    }

    private final String name;
    private JsonSaleTrigger saleTrigger;
    private Collection<JsonSaleOffer> saleOfferList;
    private final Sale.SaleType saleType;
    private final String storeName;
    private final int storeId;


    public JsonSale(String name, Sale.SaleType saleType, String storeName, int storeId) {
        this.name = name;
        this.saleType = saleType;
        this.storeName = storeName;
        this.storeId = storeId;
    }


    public void addJsonSaleOffer(int itemId, String itemName, double amountNeeded, double pricePerUnit){
        if(saleOfferList == null){
            saleOfferList = new LinkedList<>();
        }
        saleOfferList.add(new JsonSaleOffer(itemId, itemName,  amountNeeded, pricePerUnit));
    }

    public void setJsonSaleTrigger(int itemId, String itemName, double amountNeeded){
        saleTrigger = new JsonSaleTrigger(itemId, itemName, amountNeeded);
    }
}
