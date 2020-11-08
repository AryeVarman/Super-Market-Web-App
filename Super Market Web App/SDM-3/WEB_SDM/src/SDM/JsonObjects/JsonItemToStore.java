package SDM.JsonObjects;

public class JsonItemToStore {

    private String storeName;
    private int storeId;
    private double PriceInStore;

    public JsonItemToStore(String storeName, int storeId, double PriceInStore) {
        this.storeName = storeName;
        this.storeId = storeId;
        this.PriceInStore = PriceInStore;
    }


    public String getStoreName() {
        return storeName;
    }

    public int getStoreId() {
        return storeId;
    }

    public double getItemPriceInStore() {
        return PriceInStore;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public void setItemPriceInStore(double PriceInStore) {
        this.PriceInStore = PriceInStore;
    }
}
