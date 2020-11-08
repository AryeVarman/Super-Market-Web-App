package engine.src.SDMEngine;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

public class Store implements Coordinatable, Serializable {
    private final AreaManager areaStoreIn;
    private final int serialNumber;
    private String name;
    private final StoreOwner storeOwner;
    private final Coordinate coordinate;
    private Map< Integer,StoreItem> itemsList;
    private List<Order> orderHistory;
    private List<Feedback> feedbackList;
    private Map<String, Sale> saleList;
    private Map<Item, Integer> howManyTimesItemBeenSoldFromStore;
    private final double PPK;
    private double profitFromItems;
    private double profitFromDeliveries;

    public Store(int serialNumber, String name, StoreOwner storeOwner, Coordinate coordinate, double PPK, AreaManager areaStoreIn) {
        this.areaStoreIn = areaStoreIn;
        this.serialNumber = serialNumber;
        this.name = name.toLowerCase();
        this.storeOwner = storeOwner;
        this.coordinate = coordinate;
        this.coordinate.setElement(this);
        this.itemsList = new HashMap<>();
        initializeStoresSellsCounter();
        this.orderHistory = new LinkedList<>();
        this.feedbackList = new LinkedList<>();
        this.saleList = new HashMap<>();
        this.howManyTimesItemBeenSoldFromStore = new HashMap<>();
        this.PPK = PPK;
        this.profitFromDeliveries = 0;
        this.profitFromItems = 0;
    }

    public Store(int serialNumber, String name, StoreOwner storeOwner, Map<Integer,
            StoreItem> itemsList, Coordinate coordinate, double PPK, AreaManager areaStoreIn) {
        this.areaStoreIn = areaStoreIn;
        this.serialNumber = serialNumber;
        this.name = name.toLowerCase();
        this.storeOwner = storeOwner;
        this.coordinate = coordinate;
        this.coordinate.setElement(this);
        this.itemsList = itemsList;
        initializeStoresSellsCounter();
        this.orderHistory = new LinkedList<>();
        this.feedbackList = new LinkedList<>();
        this.saleList = new HashMap<>();
        this.PPK = PPK;
        this.profitFromDeliveries = 0;
        this.profitFromItems = 0;
    }

    public Store(int serialNumber, String name, StoreOwner storeOwner, Map<Integer,
            StoreItem> itemsList, Coordinate coordinate, Map<String, Sale> saleList, double PPK, AreaManager areaStoreIn) {
        this.areaStoreIn = areaStoreIn;
        this.serialNumber = serialNumber;
        this.name = name.toLowerCase();
        this.storeOwner = storeOwner;
        this.coordinate = coordinate;
        this.coordinate.setElement(this);
        this.itemsList = itemsList;
        initializeStoresSellsCounter();
        this.orderHistory = new LinkedList<>();
        this.feedbackList = new LinkedList<>();
        this.saleList = new HashMap<>();
        this.saleList = saleList;
        this.PPK = PPK;
        this.profitFromDeliveries = 0;
        this.profitFromItems = 0;

        for (Sale sale : this.saleList.values()) {
            sale.setStore(this);
        }
    }

    private void initializeStoresSellsCounter() {
        this.howManyTimesItemBeenSoldFromStore = new HashMap<>();

        for(StoreItem storeItem: this.itemsList.values()) {
            this.howManyTimesItemBeenSoldFromStore.put(storeItem.getItem(), 0);
        }
    }

    public void addProfitFromDeliveries(double profitFromDeliveries) { this.profitFromDeliveries += profitFromDeliveries; }

    public void addProfitFromItems(double profitFromItems) { this.profitFromItems += profitFromItems; }

    public void addItemToStore(int itemId, double itemPrice, Map<Integer, Item> itemList) throws Exception {
        String exceptionMsg = "";

        if(!itemList.containsKey(itemId)) {
            exceptionMsg += "Item #" + itemId + " is not registered in the system\n";
        }
        if(this.itemsList.containsKey(itemId)) {
            exceptionMsg += "Item #" + itemId + " is already registered in " + this.getName() + '\n';
        }
        if(itemPrice < 0) {
            exceptionMsg += "Item price can not be negative\n";
        }
        if(itemPrice == 0) {
            exceptionMsg += "Item price can not be zero\n";
        }

        if(exceptionMsg.equals("")) {
            try {
                StoreItem storeItemToAdd = new StoreItem(itemList.get(itemId), itemPrice);
                this.itemsList.put(itemId, storeItemToAdd);

                if(!this.howManyTimesItemBeenSoldFromStore.containsKey(storeItemToAdd.getItem())) {
                    this.howManyTimesItemBeenSoldFromStore.put(storeItemToAdd.getItem(), 0);
                }
            }
            catch(Exception ex) {
                exceptionMsg += ex.getMessage();
            }
        }
        else {
            throw new Exception(exceptionMsg);
        }
    }

    public String removeItemFromStore(int itemId) throws Exception {
        String exceptionMsg = "";
        String deletedSalesMessage = "";

        if (!this.itemsList.containsKey(itemId)) {
            exceptionMsg += "item #" + itemId + " is not being sold by " + this.getName() + '\n';
        }

        Collection<String> salesToDeleteKeys = new LinkedList<>();
        Collection<Sale.SaleOffer> saleOffersToDelete = new LinkedList<>() ;
        if (exceptionMsg.equals("")) {
            for (Map.Entry<String, Sale> entry : saleList.entrySet()) {

                entry.getValue().getSaleOfferList().stream().forEach(saleOffer -> {
                            if (saleOffer.getItemId() == itemId) {
                                saleOffersToDelete.add(saleOffer);
                                if (entry.getValue().getSaleType().equals(Sale.SaleType.allOrNothing)) {
                                    salesToDeleteKeys.add(entry.getKey());
                                }
                            }
                        }
                );
            }

            for (Sale.SaleOffer saleOffer: saleOffersToDelete) {
                for (Map.Entry<String, Sale> entry : saleList.entrySet()) {
                    if(entry.getValue().getSaleOfferList().contains(saleOffer)){
                        entry.getValue().getSaleOfferList().remove(saleOffer);
                    }
                }
            }

            for (Map.Entry<String, Sale> entry : saleList.entrySet()){
                if(entry.getValue().getSaleOfferList().isEmpty()){
                    salesToDeleteKeys.add(entry.getKey());
                }
                if (entry.getValue().getSaleTrigger().getItemId() == itemId) {
                    salesToDeleteKeys.add(entry.getKey());
                }
            }
            if(!salesToDeleteKeys.isEmpty()) {
                deletedSalesMessage += "Because of deleting item #" + itemId + '\n' + "this sales been removed:\n";

                for (String key : salesToDeleteKeys) {
                    int index = 1;
                    deletedSalesMessage += index + ". " + saleList.get(key).getName() + '\n';
                    saleList.remove(key);
                }
            }

            this.itemsList.remove(itemId);
        } else {
            throw new Exception(exceptionMsg);
        }
        return deletedSalesMessage;
    }

    public void changeItemPrice(int itemId, double newPrice) throws Exception {
        String exceptionMsg = "";

        if(newPrice < 0) {
            exceptionMsg += "Item price can not be negative\n";
        }
        if(newPrice == 0) {
            exceptionMsg += "Item price can not be zero\n";
        }
        if(!this.itemsList.containsKey(itemId)) {
            exceptionMsg += "Item #" + itemId + " does not being sold by the store " + this.getName() + '\n';
        }

        if(exceptionMsg.equals("")) {
            this.itemsList.get(itemId).setPrice(newPrice);
        }
        else {
            throw new Exception(exceptionMsg);
        }
    }

    public void addOrderToOrderHistory(Order order) {
        if(order != null) {
            this.orderHistory.add(order);
        }
    }

    public void addNewFeedback(Customer customer, LocalDate orderDate, String verbalFeedback, int score, SystemManager systemManager) {
        Feedback feedback = new Feedback(customer, orderDate, this, verbalFeedback, score);
        this.feedbackList.add(feedback);
        systemManager.getFeedbackAlertManager().addAlert(this.getStoreOwner(), feedback);
    }

    public int getSerialNumber() { return serialNumber; }

    public String getName() { return name; }

    public List<Feedback> getFeedbackList() { return feedbackList; }

    public Coordinate getCoordinate() { return coordinate; }

    public final Map<Integer, StoreItem> getItemsList() { return itemsList; }

    public final List<Order> getOrderHistory() { return orderHistory; }

    public final Map<String, Sale> getSaleList() { return saleList; }

    public AreaManager getAreaStoreIn() { return areaStoreIn; }

    public StoreOwner getStoreOwner() { return storeOwner; }

    public double getPPK() { return PPK; }

    public double getDistanceFromStore(Coordinate coordinate) {
        double distance = -1;
        if(coordinate != null) {
            distance = Math.sqrt(Math.pow(coordinate.getCol() - this.getCoordinate().getCol(), 2) +
                                Math.pow(coordinate.getRow() - this.getCoordinate().getRow(), 2));
        }
        return distance;
    }

    public double getProfitFromDeliveries() { return profitFromDeliveries; }

    public double getProfitFromItems() { return profitFromItems; }

    public Map<Item, Integer> getHowManyTimesItemBeenSoldFromStore() { return howManyTimesItemBeenSoldFromStore; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Store store = (Store) o;
        return serialNumber == store.serialNumber;
    }

    @Override
    public int hashCode() { return Objects.hash(serialNumber); }

    @Override
    public String toString() {
        Currency currency = Currency.getInstance(Locale.US);

        int orderNumber = 1;
        final String tab = "    ";
        String storeStr = "";
        storeStr += "Store\n";
        storeStr += "Serial Number: " + serialNumber + "\n";
        storeStr += "Name: " + name + "\n";
        storeStr += "Store location: " + this.coordinate.showLocation() + "\n";
        storeStr += "PPK: " + PPK + "\n";
        storeStr += "Profit from deliveries: " + String.format("%.2f",profitFromDeliveries) + currency.getSymbol() + "\n\n";

        storeStr += "Items in store:\n";
        for (Map.Entry<Integer,StoreItem> entry: itemsList.entrySet()) {
            storeStr += tab + entry.getValue().toString();
            storeStr += tab + "Total items sold so far from this store: " +
                    (howManyTimesItemBeenSoldFromStore.get(entry.getValue().getItem()) == null ? 0 : howManyTimesItemBeenSoldFromStore.get(entry.getValue().getItem()));
            storeStr += "\n\n";
        }

        storeStr += "Order History:\n\n";
        for (Order order: orderHistory) {
            storeStr += tab + order.toStringFromSingleStore(this)+"\n";
        }

        storeStr += "Store Sales:\n\n";
        for (Sale sale: saleList.values()) {
            storeStr += sale.toString() + '\n';
        }

        return storeStr;
    }

    public String showDetails() {
        return "Serial Number: " + serialNumber + "\n" +
        "Name: " + name + "\n" +
        "Store location: " + this.coordinate.showLocation() + "\n";
    }

    public String showDetailsForOrderSummery(Coordinate orderLocation) {
        double distance = getDistanceFromStore(orderLocation);

        return "Serial Number: " + serialNumber + " | " +
                "Name: " + name + "\n" +
                "PPK: " + this.PPK + " | " +
                "Location: " + this.coordinate.toString() + " | " +
                String.format("DistanceFrom customer: %.2f | ", distance) +
                 String.format("Delivery cost: %.2f", (distance * this.getPPK()));
    }

    public List<Sale> getSalesDeserved(Order order) {
        List<Sale> saleList = new LinkedList<>();

        for (Sale sale : this.saleList.values()) {

            if (order.getOrderItemFromList(sale.getSaleTrigger().getItemId()) != null &&
                    order.getOrderItemFromList(sale.getSaleTrigger().getItemId()).getAmountFromItemForThisOrder() >=
                    sale.getSaleTrigger().getAmountNeeded()) {

                int timesToAddSale = (int) (order.getOrderItemFromList(sale.getSaleTrigger().getItemId()).getAmountFromItemForThisOrder() /
                                        sale.getSaleTrigger().getAmountNeeded());

                for (int i = 0; i < timesToAddSale; i++) {
                    saleList.add(sale);
                }
            }
        }

        return saleList;
    }
}