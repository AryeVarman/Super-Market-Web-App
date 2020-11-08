package engine.src.SDMEngine;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

public class Order implements Serializable, Coordinatable {

    public class OrderItem implements Serializable {
        private StoreItem storeItem;
        private double amountFromItemForThisOrder;
        private Store store;
        private double pricePerUnit;
        private double generalItemPrice;
        private boolean isInSale = false;

        public OrderItem(Store store, StoreItem item, double amountFromItemForThisOrder){
            this.store = store;
            this.storeItem = item;
            this.amountFromItemForThisOrder = amountFromItemForThisOrder;
            this.pricePerUnit = storeItem.getPrice();
            this.generalItemPrice = this.pricePerUnit * amountFromItemForThisOrder;

        }

        public StoreItem getStoreItem() { return storeItem; }

        public double getAmountFromItemForThisOrder() { return amountFromItemForThisOrder; }

        public Store getStore() { return store; }

        public int getSerialNumber() { return this.getStoreItem().getItem().getSerialNumber(); }

        public double getPricePerUnit() { return pricePerUnit; }

        public double getGeneralItemPrice() { return generalItemPrice; }

        public boolean isInSale() { return isInSale; }

        public void setInSale(boolean inSale) { isInSale = inSale; }

        public void addAmountToTheItem(double amountFromItemToAdd) {
            if(this.storeItem.getItem().getPurchaseMethod().equals("Quantity")) {
                this.amountFromItemForThisOrder += amountFromItemToAdd;
            }
            this.generalItemPrice += this.pricePerUnit * amountFromItemToAdd;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OrderItem orderItem = (OrderItem) o;
            return Double.compare(orderItem.pricePerUnit, pricePerUnit) == 0 &&
                    Objects.equals(storeItem, orderItem.storeItem) &&
                    Objects.equals(store, orderItem.store);
        }

        @Override
        public int hashCode() {
            return Objects.hash(storeItem, store, pricePerUnit);
        }

        @Override
        public String toString() {
            Currency currency = Currency.getInstance(Locale.US);
            Item item = this.getStoreItem().getItem();

           return "Serial number: " + item.getSerialNumber() +
                    " | Name: " + item.getName() +
                    " | Purchase in: " + item.getPurchaseMethod() +
                    " | Amount: " + this.getAmountFromItemForThisOrder() +
                    " | Price: " + String.format("%.2f", this.getStoreItem().getPrice()) + currency.getSymbol() +
                    " | General cost: " + String.format("%.2f", this.generalItemPrice) + currency.getSymbol() +
                    " | Is item On Sale: " + (this.isInSale ? "Yes\n" : "No\n");
        }
    }

    private final int serialNumber;
    private LocalDate date;
    private List<OrderItem> orderList;
    private Set<Store> orderStoresList;
    private double generalOrderPrice;
    private double deliveryPrice;
    private double itemsCost;
    private int amountOfItems;
    private Customer customer;
    private List<Sale> saleDeserved;
    Coordinate coordinate;

    public Order(int serialNumber, LocalDate date, List<OrderItem> orderList, double generalOrderPrice,
                 double deliveryPrice, double itemsPrice, int amountOfItems, Customer customer) {
        this.serialNumber = serialNumber;
        this.date = date;
        this.orderList = orderList;
        this.generalOrderPrice = generalOrderPrice;
        this.orderStoresList = new HashSet<>();
        this.deliveryPrice = deliveryPrice;
        this.itemsCost = itemsPrice;
        this.amountOfItems = amountOfItems;
        this.customer = customer;
        this.saleDeserved = new LinkedList<>();
    }

    public Order(LocalDate date, Store store, Customer customer, int serialNumber){
        this.serialNumber = serialNumber;
        this.date = date;
        this.orderStoresList = new HashSet<Store>();
        this.orderStoresList.add(store);
        this.orderList = new LinkedList<>();
        this.customer = customer;
        this.amountOfItems = 0;
        this.saleDeserved = new LinkedList<>();
    }

    public Order(LocalDate date, Customer customer, int serialNumber) {
        this.serialNumber = serialNumber;
        this.date = date;
        this.orderStoresList = new HashSet<Store>();
        this.orderList = new LinkedList<>();
        this.customer = customer;
        this.amountOfItems = 0;
        this.saleDeserved = new LinkedList<>();
    }

    public Order(LocalDate date, Customer customer, int serialNumber, Store store, Coordinate coordinate) {
        this.serialNumber = serialNumber;
        this.date = date;
        this.orderStoresList = new HashSet<Store>();
        this.orderStoresList.add(store);
        this.orderList = new LinkedList<>();
        this.customer = customer;
        this.amountOfItems = 0;
        this.saleDeserved = new LinkedList<>();
        this.coordinate = coordinate;
    }

    public Order(int serialNumber, Customer customer) {
        this.serialNumber = serialNumber;
        this.date = null;
        orderList = new LinkedList<>();
        orderStoresList = new HashSet<>();
        generalOrderPrice = 0;
        deliveryPrice = 0;
        itemsCost = 0;
        amountOfItems = 0;
        this.customer = customer;
        this.saleDeserved = new LinkedList<>();
        this.coordinate = null;
    }

    public void addOrderItemToOrderList(Store store, StoreItem item, double amountFromItemForOrder, boolean isInSale){
        OrderItem orderItem;
        int indexInList = this.itemIndexInOrder(item.getSerialNumber(), isInSale);

        if(indexInList == -1) {
            orderItem = new OrderItem(store, item, amountFromItemForOrder);
        }
        else {
            orderItem = this.orderList.get(indexInList);
        }

        if (item.getItem().getPurchaseMethod().equals("Quantity")) {
            this.amountOfItems += amountFromItemForOrder;
        }
        else if (!this.isItemExistInItemList(orderItem)) {
            this.amountOfItems++;
        }

        if (indexInList == -1 || isInSale != orderList.get(indexInList).isInSale) {
            this.orderList.add(orderItem);
        }
        else {
            orderItem.addAmountToTheItem(amountFromItemForOrder);
        }

        this.itemsCost += item.getPrice() * amountFromItemForOrder;
        this.generalOrderPrice += item.getPrice() * amountFromItemForOrder;

        orderItem.isInSale = isInSale;

        if(!this.orderStoresList.contains(store)) {
            this.orderStoresList.add(store);
            this.addDeliveryCostToOrder();
        }
    }

    public void updateParametersForSimpleOrder(Coordinate orderLocation, LocalDate orderDate) {
        this.coordinate = new Coordinate(orderLocation.getRow(), orderLocation.getCol(), this);
        setDate(orderDate);
        addDeliveryCostToOrder();
    }

    public void setSaleDeservedList() {
        System.out.println(Thread.currentThread().getName() + "set Sale Deserved List");

        for (Store store : this.orderStoresList) {
            List<Sale> salesFromStore = store.getSalesDeserved(this);

            for (Sale sale : salesFromStore) {
                this.saleDeserved.add(sale);
            }
        }
    }

    private boolean isItemExistInItemList(OrderItem orderItemToFind) {
        boolean found = false;

        for(OrderItem orderItem: this.orderList) {
            if(orderItem.getSerialNumber() == orderItemToFind.getSerialNumber()) {
                found = true;
                break;
            }
        }

        return found;
    }

    public void addDeliveryCostToOrder() {
        this.deliveryPrice = 0;

        for (Store store: this.orderStoresList) {
            this.deliveryPrice += store.getPPK() * getDistance(store.getCoordinate());
        }

        this.generalOrderPrice = this.itemsCost + this.deliveryPrice;
    }

    public void setCustomer(Customer customer) {
        if(customer != null) {
            this.customer = customer;
        }
    }

    public void setDate(LocalDate localDate) {
        if (localDate != null) {
            this.date = localDate;
        }
    }

    public double getDistance(Coordinate storeCoordinate){
        double distance = -1;
        if(this.getCoordinate() != null) {
            distance = Math.sqrt(Math.pow(storeCoordinate.getCol() - this.getCoordinate().getCol(), 2) +
                    Math.pow(storeCoordinate.getRow() - this.getCoordinate().getRow(), 2));
        }
        return distance;
    }

    @Override
    public Coordinate getCoordinate() { return coordinate; }

    public int getSerialNumber() { return serialNumber; }

    public LocalDate getDate() { return date; }

    public List<OrderItem> getOrderList() { return orderList; }

    public double getGeneralOrderPrice() { return generalOrderPrice; }

    public double getDeliveryPrice() { return deliveryPrice; }

    public double getItemsCost() { return itemsCost; }

    public int getAmountOfItems() { return amountOfItems; }

    public int getAmountOfItemTypes() {
        Set<Integer> orderItemIDSet = new HashSet<>();

        for(OrderItem orderItem: this.orderList) {
            orderItemIDSet.add(orderItem.getSerialNumber());
        }

        return orderItemIDSet.size();
    }

    public Set<Store> getOrderStoresList() { return orderStoresList; }

    public List<Sale> getSaleDeserved() { return saleDeserved; }

    public int itemIndexInOrder(int serialNumber, boolean isOnSale) {
        for (OrderItem orderItem : this.orderList) {

            if (orderItem.getSerialNumber() == serialNumber && isOnSale == orderItem.isInSale) {
                return this.orderList.indexOf(orderItem);
            }
        }

        return -1;
    }

    public String showOrderSummery() {
        Currency currency = Currency.getInstance(Locale.US);
        String showOrder = "Order Summery:\n\n";

        for(Store store : this.orderStoresList) {
            showOrder += "\nStore " + store.showDetailsForOrderSummery(this.getCoordinate());

            showOrder += "\nItems From " + store.getName() + ":\n";

            for (OrderItem orderItem : orderList) {
                if(orderItem.store.equals(store)) {
                    Item item = orderItem.getStoreItem().getItem();
                    showOrder += "Serial number: " + item.getSerialNumber() +
                    " | Name: " + item.getName() +
                    " | Purchase in: " + item.getPurchaseMethod() +
                    " | Amount: " + orderItem.getAmountFromItemForThisOrder() +
                    " | Price: " + String.format("%.2f", orderItem.getStoreItem().getPrice()) + currency.getSymbol() +
                    " | General cost: " + String.format("%.2f", orderItem.generalItemPrice) + currency.getSymbol() +
                    " | Is item On Sale: " + (orderItem.isInSale ? "Yes\n" : "No\n");
                }
            }
        }

        showOrder += "\n\n Total items types for the order: " + this.orderList.size() + "\n";
        showOrder += "Total items price for the order " + String.format("%.2f" + currency.getSymbol() + "\n", (this.generalOrderPrice - this.deliveryPrice));
        showOrder += "Total delivery price for the order " + String.format("%.2f" + currency.getSymbol() + "\n", this.deliveryPrice);
        showOrder += "Total order price is: " + String.format("%.2f" + currency.getSymbol() + '\n', this.generalOrderPrice);

        return showOrder;
    }

    public OrderItem getOrderItemFromList(int serialNumber) {
        for (OrderItem orderItem : this.orderList) {
            if(orderItem.getSerialNumber() == serialNumber) {
                return orderItem;
            }
        }

        return null;
    }

    public boolean isValidAnswer(String answer)throws Exception{
        if(answer.toLowerCase().equals("yes") || answer.toLowerCase().equals("no")){
            return true;
        }
        else{
            throw new Exception("Invalid input, please type 'Yes' for complete the order or 'No' for cancel");
        }
    }

    public Customer getCustomer() { return customer; }

    /*@Override
    public String toString() {
        Currency currency = Currency.getInstance(Locale.US);

        String orderStr = "Serial number: " + getSerialNumber() +
         "\nCustomer " + this.customer.customerDetails() +
        "\nDate: " + this.date.toString() +
        "\n\nOrder from stores: " + '\n';

        for (Store store : getOrderStoresList()) {
            orderStr += "Store: Serial number: " + String.format("%d",store.getSerialNumber()) + "  Name: " + store.getName() + '\n';
        }
        orderStr += "\nTotal item types: " + getAmountOfItemTypes() +
        "\nTotal amount of items: " + getAmountOfItems() +
        "\n\nTotal items cost: " + String.format("%.2f", getItemsCost()) + currency.getSymbol() +
        "\nDelivery cost: " + String.format("%.2f", getDeliveryPrice()) + currency.getSymbol() +
        "\nTotal order cost: " + String.format("%.2f", getGeneralOrderPrice()) + currency.getSymbol() + "\n\n";

        return orderStr;
    }*/

    public String toStringFromSingleStore(Store store) {
        Currency currency = Currency.getInstance(Locale.US);

        String orderStr = "Serial number: " + getSerialNumber() +
                "\nCustomer " + this.customer.customerDetails() +
                "\nDate: " + this.date.toString() + "\n";

        double itemCount = 0;
        double itemCost = 0;
        double delivery = store.getPPK() * store.getDistanceFromStore(this.getCoordinate());

        for(OrderItem orderItem : orderList) {
            if (orderItem.getStore().equals(store)) {
              itemCount += orderItem.amountFromItemForThisOrder;
              itemCost += orderItem.generalItemPrice;
            }
        }

        orderStr += String.format("Amount of items from %S: %.2f\n", store.getName(), itemCount);
        orderStr += String.format("Items cost for order from %S: %.2f\n", store.getName(), itemCost);
        orderStr += String.format("Delivery cost from %S: %.2f\n", store.getName(), delivery);
        orderStr += String.format("General cost from %S: %.2f\n\n", store.getName(), (delivery + itemCost));

        return orderStr;
    }
}