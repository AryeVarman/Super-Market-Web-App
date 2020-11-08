package engine.src.SDMEngine;

import com.sun.corba.se.impl.io.TypeMismatchException;
import com.sun.org.apache.xpath.internal.operations.Or;
import engine.src.generated.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.lang.Exception;

public class AreaManager {

    private final SystemManager systemManager;
    // mapping the smart orders of the customers, <customer name,the order>
    private Map<String,Order> currentCustomersSmartOrders;
    // mapping the orders of the customers, map<customer name, map< storeId from ordering, the order>>
    private Map<String,Map<Integer,Order>> currentCustomersOrders;
    private final StoreOwner areaOwner;
    private String areaName;
    private CoordinateSystem coordinateMap;
    private final int coordinateMapSize = 50;
    private Map<Integer,Store> storesList;
    private Map<Integer, Item> itemsList;
    private Map<Item, ItemSystemStatistic> itemsSystemStatistics;
    private List<Order> ordersHistory;
    private Map<String, Sale> systemSaleList;
    private int storeSerialNumber = 1;
    private int itemSerialNumber = 1;
    private int customerSerialNumber = 1;
    private int orderSerialNumber = 0;
    private final String QUIT = "q";
    private boolean isSystemSet;
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM-kk:mm");

    public boolean checkIfAlreadyHaveOpenOrder(String userName, int serialNumber) {
        if(currentCustomersOrders.containsKey(userName)){
            if(currentCustomersOrders.get(userName).containsKey(serialNumber)){
                return true;
            }
        }
        return false;
    }

    public boolean checkIfAlreadyHaveOpenSmartOrder(String userName) {
        if(currentCustomersSmartOrders.containsKey(userName)){
            return true;
        }
        return false;
    }

    public enum ContinuePurchase{YES,NO}
    private ContinuePurchase continuePurchase = ContinuePurchase.YES;

    public AreaManager(StoreOwner storeOwner, SystemManager systemManager) {
        this.systemManager = systemManager;
        areaOwner = storeOwner;
        coordinateMap = new CoordinateSystem(50,50);
        storesList = new HashMap<>();
        itemsList = new HashMap<>();
        ordersHistory = new LinkedList<>();
        itemsSystemStatistics = new HashMap<>();
        systemSaleList = new HashMap<>();
        isSystemSet = false;
        currentCustomersOrders = new HashMap<>();
        currentCustomersSmartOrders = new HashMap<>();
    }

    public void loadDataFromFile(InputStream inputStream, Set<String> otherAreaNames) throws Exception {
        String errorList = "XML Loading error List:\n";

        JAXBContext jaxbContext = JAXBContext.newInstance(SuperDuperMarketDescriptor.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        SuperDuperMarketDescriptor marketDescriptor = null;
        try {  marketDescriptor = (SuperDuperMarketDescriptor) unmarshaller.unmarshal(inputStream); }
        catch (Exception ex) {
            errorList += "File is not a valid Area XML file\n";
            throw new Exception(errorList);
        }

        Map<Integer,Item> targetItemList = new HashMap<>();
        Map<Integer,Store> targetStoreList = new HashMap<>();
        Map<Integer, Customer> targetCustomerList = new HashMap<>();
        Map<String, Sale> targetSaleList = new HashMap<>();
        CoordinateSystem savedCoordinateMap = this.coordinateMap;
        this.coordinateMap = new CoordinateSystem(coordinateMapSize ,coordinateMapSize);

        errorList += parseXMLItemList(marketDescriptor.getSDMItems(), targetItemList);
        errorList += parseXMLStoreList(marketDescriptor.getSDMStores(), targetStoreList, targetItemList);
        errorList += checkAllItemBeingSoldByStore(targetItemList);
        errorList += initSystemSaleList(targetStoreList, targetSaleList);
        errorList += checkAreaName(marketDescriptor.getSDMZone().getName(), otherAreaNames);

        if(errorList.equals("XML Loading error List:\n")) {
            this.areaName = marketDescriptor.getSDMZone().getName();
            this.itemsList = targetItemList;
            this.storesList = targetStoreList;
            this.systemSaleList = targetSaleList;
            initItemsStatistics();
            this.ordersHistory = new LinkedList<>();
            this.isSystemSet = true;
        }
        else {
            this.coordinateMap = savedCoordinateMap;
            throw new Exception(errorList);
        }
    }

    private String checkAreaName(String areaName, Set<String> otherAreaNames) {
        String errorList = "";

        if (areaName.trim().isEmpty()) {
            errorList += "Area can't have empty name";
        }

        for(String currentAreaName : otherAreaNames) {
            if (currentAreaName.toLowerCase().equals(areaName.toLowerCase())) {
                errorList += areaName + " is taken by another owner";
            }
        }

        return errorList;
    }

    private void checkXMLPath(Path path) throws FileNotFoundException {
        String errorList = "";

        if(!Files.exists(path)) {
            errorList += path.toString() + " is not a file that exist in the file system\n";
        }
        if(!isXMLPath(path.toString())) {
            errorList += path.toString() + " is not a XML file\n";
        }

        if(!errorList.equals("")) {
            throw new FileNotFoundException(errorList);
        }
    }

    private boolean isXMLPath(String path) { return path.toLowerCase().endsWith(".xml"); }

    private String checkAllItemBeingSoldByStore(Map<Integer,Item> targetItemList) {
        String errorList = "";

        Iterator iter = targetItemList.entrySet().iterator();

        while(iter.hasNext()) {
            Map.Entry itemPair = (Map.Entry) iter.next();
            Item currentItem = (Item) itemPair.getValue();

            if(!currentItem.isBeingSoldByStore()) {
                errorList += "Item #"+ currentItem.getSerialNumber()+ " " + currentItem.getName() +
                        " not being sell by any store\n";
            }
        }

        return errorList;
    }

    private void initItemsStatistics() {
        Iterator itemIter = itemsList.entrySet().iterator();

        while(itemIter.hasNext()) {
            Map.Entry itemPair = (Map.Entry)itemIter.next();
            itemsSystemStatistics.put((Item) itemPair.getValue(), new ItemSystemStatistic((Integer) itemPair.getKey()));
        }

        Iterator storeIter = storesList.entrySet().iterator();

        while(storeIter.hasNext()) {
             Map.Entry storePair = (Map.Entry) storeIter.next();
             Store currentStore = (Store) storePair.getValue();

             Iterator storeItemIter = currentStore.getItemsList().entrySet().iterator();

             while(storeItemIter.hasNext()) {
                 Map.Entry storeItemPair = (Map.Entry)storeItemIter.next();

                 StoreItem currentStoreItem = (StoreItem) storeItemPair.getValue();

                 if(this.itemsSystemStatistics.containsKey(currentStoreItem.getItem())) {

                     this.itemsSystemStatistics.get(currentStoreItem.getItem()).
                             updateItemStatistic_NewStoreSellItem(currentStore, this.itemsList);
                 }
             }
        }
    }

    public String showAllActiveStoresInSystem() {
        String storesStr = "Stores in the system:" + '\n';

        for (Store store: storesList.values()) {
            storesStr +="Serial number: " + store.getSerialNumber() + " ; Name: " + store.getName() + " ; PPK: " + store.getPPK() +
                    " ; Located at: " + store.getCoordinate().showLocation() + '\n';
        }
        return storesStr;
    }

    public String showAllItemsInTheStoreForBuying(Store store) {
        String itemsForSell = "Items in the system:\n";
        Collection<StoreItem> storeItems = store.getItemsList().values();

        for (Item item : itemsList.values()) {
            itemsForSell += "Serial number: " + item.getSerialNumber() + " | Name: " + item.getName() + " | Purchase in: " + item.getPurchaseMethod();
            itemsForSell += store.getItemsList().containsKey(item.getSerialNumber())? " | Price: " + store.getItemsList().get(item.getSerialNumber()).getPrice(): " | Doesnt sell";
            itemsForSell += '\n';
        }
        return itemsForSell;
    }

    public String showAllStoresDetails() {
        String storesDetails = "";

        if(storesList.size() > 0) {
            for (Store store : storesList.values()) {
                storesDetails += store.toString();
            }
        }
        else { storesDetails = "there is no stores to show\n"; }

        return storesDetails;
    }

    public String showAllItemsDetails(){
        Currency currency = Currency.getInstance(Locale.US);

        String itemDetails = "";
        if(itemsList.size() > 0) {
            for (Item item : itemsList.values()) {
                itemDetails += '\n' + item.toString();
                itemDetails += "\nNumber of stores that sell the item: ";
                itemDetails += itemsSystemStatistics.get(item).getAmountOfStoresThatSell() + "\n";
                itemDetails += "Average price: " + String.format("%.2f", itemsSystemStatistics.get(item).getAvgPrice()) + currency.getSymbol() + '\n';
                itemDetails += "Item been sold " + itemsSystemStatistics.get(item).getAmountOfTimesBeenSold() +
                        (itemsSystemStatistics.get(item).getAmountOfTimesBeenSold() > 1 ? " times" : " time") + "\n\n";
            }
        }
        else { itemDetails = "there is no items to show\n"; }

        return itemDetails;
    }

    public String oneItemDetails(Item item){
        Currency currency = Currency.getInstance(Locale.US);
        String itemDetails = "";
        itemDetails += '\n' + item.toString();
        itemDetails += "\nNumber of stores that sell the item: ";
        itemDetails += itemsSystemStatistics.get(item).getAmountOfStoresThatSell() + "\n";
        itemDetails += "Average price: " + String.format("%.2f", itemsSystemStatistics.get(item).getAvgPrice()) + currency.getSymbol() + '\n';
        itemDetails += "Amount that been sold: " + String.format("%.2f\n\n",itemsSystemStatistics.get(item).getAmountOfTimesBeenSold());

        return itemDetails;
    }

    public String showOrderHistory() {

        String orderStr = "";

        if(ordersHistory.size() > 0) {
            for (Order order : ordersHistory) {
                orderStr += order.toString();
            }
        }
        else { orderStr = "there is no orders to show\n"; }

        return orderStr;
    }

    public void addItemToOrderFromCheapestStore(int itemId, double amountFromItem, Order theOrder) throws Exception {

        Order.OrderItem orderItemToAdd = null;
        String errorList = "";
        StoreItem itemToAdd = null;
        Store storeToAddItemFrom = null;
        boolean itemExistInSystem = true;

        if(!this.itemsList.containsKey(itemId)) {
            errorList += "There is no item #" + itemId + " in the system\n";
            itemExistInSystem = false;
        }
        if(amountFromItem <= 0) {
            errorList += "You attempted to buy non positive amount of item #" +
                    itemId + " - " + this.itemsList.get(itemId).getName() + '\n';
        }
        if(itemExistInSystem && !isHoleNumber(amountFromItem) && this.itemsList.get(itemId).getPurchaseMethod().equals("Quantity")) {
            errorList += "Item is sold by quantity and therefore amount to buy must be hole number\n";
        }

        if(errorList.equals("")) {
            double minimalPrice = Double.MAX_VALUE;

            for (Store store : this.storesList.values()) {
                if (store.getItemsList().containsKey(itemId) && store.getItemsList().get(itemId).getPrice() < minimalPrice) {
                    storeToAddItemFrom = store;
                    itemToAdd = store.getItemsList().get(itemId);
                    minimalPrice = store.getItemsList().get(itemId).getPrice();
                }
            }

            theOrder.addOrderItemToOrderList(storeToAddItemFrom, itemToAdd, amountFromItem, false);
        }

        else { throw new Exception(errorList); }
    }

    public Order createOptimalOrder(Map<Integer, Double> itemsForOrder, LocalDate orderDate, Customer customer) throws Exception {
        String errorList = "";
        Order order = new Order(orderDate, customer, orderSerialNumber);
        StoreItem itemToAdd = null;
        Store storeToAddItemFrom = null;
        int itemId;
        double amountFromItem;

        for(Map.Entry<Integer, Double> itemData: itemsForOrder.entrySet()) {
            itemId = itemData.getKey();
            amountFromItem = itemData.getValue();

            if(!this.itemsList.containsKey(itemId)) {
                errorList += "There is no item #" + itemId + " in the system\n";
            }
            if(amountFromItem <= 0) {
                errorList += "You attempted to buy non positive amount of item #" +
                        itemId + " - " + this.itemsList.get(itemId).getName() + '\n';
            }

            if(errorList.equals("")) {
                double minimalPrice = Double.MAX_VALUE;

                for(Store store: this.storesList.values()) {
                    if(store.getItemsList().containsKey(itemId) && store.getItemsList().get(itemId).getPrice() < minimalPrice) {
                        storeToAddItemFrom = store;
                        itemToAdd = store.getItemsList().get(itemId);
                    }
                }
                order.addOrderItemToOrderList(storeToAddItemFrom, itemToAdd, itemsForOrder.get(itemId), false);
            }
        }

        if(!errorList.equals("")) { throw new Exception(errorList); }

        return order;
    }

    public String removeItemFromStore(int storeId, int itemId) throws Exception{
        String exceptionMsg = "";
        boolean validProcess = true;
        Item theItem = null;
        String deletedSalesMessage = "";

        if(!this.storesList.containsKey(storeId)) {
            exceptionMsg += "there is no store #" + storeId + " in the system\n";
            validProcess = false;
        }
        else if(!this.storesList.get(storeId).getItemsList().containsKey(itemId)) {
            exceptionMsg += "Item #" + itemId + " is not being sold by " + this.storesList.get(storeId).getName() + "\n";
            validProcess = false;
        }
        if(!this.itemsList.containsKey(itemId)) {
            exceptionMsg += "there is no item #" + itemId + " in the system\n";
            validProcess = false;
        }
        if(validProcess) {
            theItem = this.itemsList.get(itemId);

            if(this.itemsSystemStatistics.get(theItem).getAmountOfStoresThatSell() < 2) {
                exceptionMsg += "there is only one store that sell " + theItem.getName() + " therefore, it can not be removed from the store\n";
                validProcess = false;
            }
            if(this.storesList.get(storeId).getItemsList().size() == 1){
                exceptionMsg += "this store have only one item (#" +theItem.getSerialNumber() + " " + theItem.getName() + ") therefore, it can not be removed from the store\n";
                validProcess = false;
            }
        }
        if(validProcess) {
            double itemPriceInStore = this.storesList.get(storeId).getItemsList().get(itemId).getPrice();
            deletedSalesMessage = this.storesList.get(storeId).removeItemFromStore(itemId);
            this.itemsSystemStatistics.get(theItem).updateItemStatistic_ItemBeenRemovedFromStore(itemPriceInStore);
        }
        else {
            throw new Exception(exceptionMsg);
        }
        return deletedSalesMessage;
    }

    public void addItemToStore(int storeId ,int itemId, double itemPrice) throws Exception {
        if(this.storesList.containsKey(storeId)) {
            Item theItemToAdd = this.itemsList.get(itemId);
            this.storesList.get(storeId).addItemToStore(itemId, itemPrice, this.itemsList);
            this.itemsSystemStatistics.get(theItemToAdd).updateItemStatistic_NewStoreSellItem(storesList.get(storeId), this.itemsList);
        }
        else {
            throw new InvalidStoreException("there is no store #" + storeId + "in the system\n");
        }
    }

    public void changeItemPriceInStore(int storeId, int itemId, double newPrice) throws Exception {
        double oldPrice = 0;

        if(this.storesList.containsKey(storeId)) {
            if(this.storesList.get(storeId).getItemsList().containsKey(itemId)) {
                oldPrice = this.storesList.get(storeId).getItemsList().get(itemId).getPrice();
                this.storesList.get(storeId).changeItemPrice(itemId, newPrice);
                this.itemsSystemStatistics.get(this.itemsList.get(itemId)).updateItemStatistic_PriceChangedInStore(oldPrice, newPrice);
            }
            else {
                throw new Exception("The store " + this.storesList.get(storeId).getName() + " does not have item #" + itemId + '\n');
            }
        }
        else {
            throw new InvalidStoreException("there is no store #" + storeId + "in the system\n");
        }
    }

    public void checkValidStoreIDInput(String storeChoice)throws Exception {
        boolean validParse;
        boolean validInput = false;
        String exceptionMassage = "";

        validParse = tryParseStringToInt(storeChoice);
        if(!validParse) {
            exceptionMassage = "Invalid input, Store choice must be a number, please choose again";
        }
        else{
            validInput = checkingCollectionValues(Integer.parseInt(storeChoice), storesList.keySet());
            if(!validInput) {
                exceptionMassage = "The serial number doesnt exist\n Store choice must be one of the serial numbers, please choose again";
            }
        }

        if(exceptionMassage != ""){
            throw new Exception(exceptionMassage);
        }

    }

    public void checkIfLocationInputValid(String xStr, String yStr, Store selectedStore)throws Exception {
        boolean validX = tryParseStringToInt(xStr);
        boolean validY = tryParseStringToInt(yStr);
        String exceptionMassage = "";

        if(!validX){
            exceptionMassage = "Invalid input, X location must be a number, try again";
        }
        else if(!validY){
            exceptionMassage = "Invalid input, Y location must be a number, try again";
        }
        else{
            try {
                Coordinate userLocation = new Coordinate(Integer.parseInt(xStr),Integer.parseInt(yStr),null);
                this.getCoordinateMap().isValidCoordinateInTheSystem(userLocation);
                if(userLocation.equals(selectedStore.getCoordinate())){
                    exceptionMassage = "your location cannot be the same as the location store, try again";
                    validX = validY = false;
                }
            } catch (Exception e){
                exceptionMassage = e.getMessage();
                validX = validY = false;
            }
        }
        if(exceptionMassage != ""){
            throw new Exception(exceptionMassage);
        }
    }

    private String parseXMLStoreList(SDMStores sdmStores, Map<Integer, Store> targetStoreList,
                                   Map<Integer,Item> systemItemList) {
        String errorList = "";

        for (SDMStore store: sdmStores.getSDMStore()) {
            try {
                addStoreToStoresList(store, targetStoreList, systemItemList);
            }
            catch (Exception ex) {
                errorList += ex.getMessage();
            }
        }

        return errorList;
    }


    private void addStoreToStoresList(SDMStore storeToAdd, Map<Integer, Store> targetStoreList,
                                      Map<Integer,Item> systemItemList) throws Exception {
        String exceptionMsg = "";

        if (storeToAdd.getId() <= 0) {
            exceptionMsg += "store: " + storeToAdd.getName() + "has non positive ID\n";
        }
        if (isStringStartOrEndWithWhiteSpace(storeToAdd.getName())) {
            exceptionMsg += "store # " + storeToAdd.getId() + " has white space in edges of name\n";
        }
        if (targetStoreList.containsKey(storeToAdd.getId())) {
            exceptionMsg += "store: " + storeToAdd.getName() + " and " + targetStoreList.get(storeToAdd.getId()).getName() +
                    " have the same ID\n";
        }
        if (storeToAdd.getDeliveryPpk() < 0) {
            exceptionMsg += "store: " + storeToAdd.getName() + " has non positive PPK\n";
        }

        exceptionMsg += checkLocationAndReturnErrorList(storeToAdd.getLocation(), storeToAdd.getId(), targetStoreList.values(), Store.class);

        Map<Integer,StoreItem> targetStoreItemsList = new HashMap<>();
        exceptionMsg += parseXMLPricesList(storeToAdd.getSDMPrices(), storeToAdd.getName(), targetStoreItemsList, systemItemList);

        Map<String, Sale> targetStoreSaleList = new HashMap<>();

        if (storeToAdd.getSDMDiscounts() != null) {
            exceptionMsg += parseXMLDiscountList(storeToAdd.getSDMDiscounts(), storeToAdd.getName(), targetStoreSaleList, targetStoreItemsList, systemItemList);
        }
        if (exceptionMsg.equals("")) {
            Store newStore = new Store(storeToAdd.getId(), storeToAdd.getName(), this.areaOwner, targetStoreItemsList,
                    new Coordinate(storeToAdd.getLocation().getX(), storeToAdd.getLocation().getY()), targetStoreSaleList,
                    storeToAdd.getDeliveryPpk(), this);

            this.getCoordinateMap().getCoordinate(newStore.getCoordinate().getCol(),newStore.getCoordinate().getRow()).
                    setElement(newStore);

            targetStoreList.put(newStore.getSerialNumber(), newStore);
        }
        else {
            throw new Exception(exceptionMsg);
        }
    }

    private String parseXMLPricesList(SDMPrices sdmPrices, String storeName, Map<Integer, StoreItem> targetItemsList,
                                      Map<Integer, Item> systemItemList) {
        String exceptionMsg = "";

        for (SDMSell sell: sdmPrices.getSDMSell()) {
           try {
               addItemToStoreItemList(sell, targetItemsList, systemItemList, storeName);
           }
           catch (Exception ex) {
               exceptionMsg += ex.getMessage();
           }
        }

        return exceptionMsg;
    }

    private String parseXMLDiscountList(SDMDiscounts sdmDiscounts, String storeName, Map<String, Sale> targetStoreSaleList,
                                        Map<Integer, StoreItem> targetStoreItemsList, Map<Integer, Item> systemItemList) {
        String exceptionMsg = "";

        for (SDMDiscount discount : sdmDiscounts.getSDMDiscount()) {
            exceptionMsg += addSaleToStoreSaleList(discount, targetStoreSaleList, systemItemList, targetStoreItemsList, storeName);
        }

        return exceptionMsg;
    }

    private void addItemToStoreItemList(SDMSell sell, Map<Integer, StoreItem> targetItemsList,
                                        Map<Integer, Item> systemItemList, String storeName) throws Exception {
        String exceptionMsg = "";

        if(!systemItemList.containsKey(sell.getItemId())) {
            exceptionMsg += "The store " + storeName + " trying to register Item #" + sell.getItemId() + ", the item does not registered in the system\n";
        }
        if(sell.getPrice() <= 0) {
            exceptionMsg += "The store " + storeName + " trying to register Item #" + sell.getItemId() + " can't have non positive price\n";
        }
        if(targetItemsList.containsKey(sell.getItemId())) {
            exceptionMsg += "The store " + storeName + " trying to register Item #" + sell.getItemId() + " appears more than once in the store\n";
        }

        if(exceptionMsg.equals("")) {
            targetItemsList.put(sell.getItemId(), new StoreItem(systemItemList.get(sell.getItemId()), sell.getPrice()));
            systemItemList.get(sell.getItemId()).setBeingSoldByStore(true);
        }
        else {
            throw new Exception(exceptionMsg);
        }
    }

    private String addSaleToStoreSaleList(SDMDiscount discount, Map<String, Sale> targetStoreSaleList, Map<Integer, Item> systemItemList,
                                          Map<Integer, StoreItem> targetStoreItemsList, String storeName) {
        String exceptionMsg = "";
        Sale newSale = null;

        try {
            parseXMLIfYouBuy(discount.getIfYouBuy(), systemItemList, targetStoreItemsList, storeName, discount.getName());
            newSale = new Sale(discount.getName(), discount.getIfYouBuy().getItemId(), discount.getIfYouBuy().getQuantity());
            newSale.setSaleType(getSaleTypeFromDiscount(discount.getThenYouGet().getOperator()));
        }
        catch (Exception ex) {
            exceptionMsg += ex.getMessage();
        }

        exceptionMsg += parseXMLOfferList(newSale,discount.getThenYouGet().getSDMOffer(), systemItemList, targetStoreItemsList, storeName, discount.getName());

        if (exceptionMsg.equals("") && !targetStoreSaleList.containsKey(newSale.getName())) {
            targetStoreSaleList.put(newSale.getName(), newSale);
        }

        return exceptionMsg;
    }

    private void parseXMLIfYouBuy(IfYouBuy sdmIfYouBuy, Map<Integer,Item> systemItemList,
                                  Map<Integer,StoreItem> targetStoreItemsList, String storeName, String discountName) throws Exception {
        String errorList = "";

        if(!targetStoreItemsList.containsKey(sdmIfYouBuy.getItemId())) {
            errorList += storeName + " try to add the sale " + discountName + " for 'if you buy' item #" + sdmIfYouBuy.getItemId() +
                    ", the item does not being sold in store\n";
        }
        if(!systemItemList.containsKey(sdmIfYouBuy.getItemId())) {
            errorList += storeName + " try to add the sale " + discountName + " for 'if you buy' item #" + sdmIfYouBuy.getItemId() +
                    ", the item does not belong to the SDM system\n";
        }
        if(sdmIfYouBuy.getQuantity() <= 0) {
            errorList +=  storeName + " try to add the sale " + discountName + " for 'if you buy' item #"  +
                    sdmIfYouBuy.getItemId() + " with non positive 'if you buy' quantity\n";
        }

        if(!errorList.equals("")) {
            throw new Exception(errorList);
        }
    }

    private String parseXMLOfferList(Sale newSale, List<SDMOffer> sdmOfferList, Map<Integer,Item> systemItemList,
                                     Map<Integer,StoreItem> targetStoreItemsList, String storeName, String discountName) {
        String errorList = "";

        for(SDMOffer sdmOffer : sdmOfferList) {
            errorList += addOfferToSaleOfferList(sdmOffer, newSale, systemItemList, targetStoreItemsList, storeName, discountName);
        }

        return errorList;
    }

    private String addOfferToSaleOfferList(SDMOffer sdmOffer, Sale newSale, Map<Integer,Item> systemItemList,
                                           Map<Integer,StoreItem> targetStoreItemsList, String storeName, String discountName) {
        String errorList = "";
        boolean isOfferItemInStore = true;

        if (!targetStoreItemsList.containsKey(sdmOffer.getItemId())) {
            errorList += storeName + " try to add sale " + discountName + ", offering item #" + sdmOffer.getItemId() +
            " which does not being sold by store\n";
            isOfferItemInStore = false;
        }
        if (!systemItemList.containsKey(sdmOffer.getItemId())) {
            errorList += storeName + " try to add sale " + discountName + ", offering item #" + sdmOffer.getItemId() +
                    " which does not belong to the system\n";
        }
        if (sdmOffer.getQuantity() <= 0) {
            errorList += storeName + " try to add sale " + discountName + ", offering item #" + sdmOffer.getItemId() +
                    "at a non positive quantity for the offer\n";
        }
        if (sdmOffer.getForAdditional() < 0) {
            errorList += storeName + " try to add sale " + discountName + ", offering item #" + sdmOffer.getItemId() +
                    "with negative price\n";
        }
        if (isOfferItemInStore && sdmOffer.getForAdditional() >= targetStoreItemsList.get(sdmOffer.getItemId()).getPrice()) {
            errorList += storeName + " try to add sale " + discountName + ", offering item #" + sdmOffer.getItemId() +
                    " with price that is not less then the original price\n";
        }

        if(errorList.equals("")) {
           int newOfferItemId = sdmOffer.getItemId();
            double newOfferAmountNeeded = sdmOffer.getQuantity();
            double newOfferPricePerUnit = sdmOffer.getForAdditional();
            newSale.addOfferToSale(newOfferItemId, newOfferAmountNeeded, newOfferPricePerUnit);
        }

        return errorList;
    }

    private Sale.SaleType getSaleTypeFromDiscount(String operator) {
        if (operator.equals("ONE-OF")) {
            return Sale.SaleType.oneOf;
        }
        if (operator.equals("ALL-OR-NOTHING")) {
            return Sale.SaleType.allOrNothing;
        }
        else {
            return Sale.SaleType.irrelevant;
        }
    }

    private String parseXMLItemList(SDMItems sdmItems, Map<Integer, Item> targetItemList) throws Exception {
        String errorList = "";

        for (SDMItem item : sdmItems.getSDMItem()) {
            try {
                addItemToItemsList(item, targetItemList);
            }
            catch (Exception ex) {
                errorList += ex.getMessage();
            }
        }

        return errorList;
    }

    private void addItemToItemsList(SDMItem itemToAdd, Map<Integer, Item> targetItemList) throws Exception {
        String exceptionMsg = "";

        if (itemToAdd.getId() <= 0) {
            exceptionMsg += "item: " + itemToAdd.getName() + "has non positive ID\n";
        }
        if (isStringStartOrEndWithWhiteSpace(itemToAdd.getName())) {
            exceptionMsg += "item # " + itemToAdd.getId() + " has white space in edges of name\n";
        }
        if (targetItemList.containsKey(itemToAdd.getId())) {
            exceptionMsg += "items: " + itemToAdd.getName() + " and " + targetItemList.get(itemToAdd.getId()).getName() +
                    " have the same ID\n";
        }
        if (!isPurchaseCategoryLegal(itemToAdd.getPurchaseCategory())) {
            exceptionMsg += "illegal purchase category, purchase category can be 'Quantity' or 'Weight'\n";
        }

        if (exceptionMsg.equals("")) {
            if (itemToAdd.getPurchaseCategory().toLowerCase().equals("quantity")) {
                targetItemList.put(itemToAdd.getId(), new ItemByQuantity(itemToAdd.getId(), itemToAdd.getName()));
            }
            if (itemToAdd.getPurchaseCategory().toLowerCase().equals("weight")) {
                targetItemList.put(itemToAdd.getId(), new ItemByWeight(itemToAdd.getId(), itemToAdd.getName()));
            }
        }
        else {
            throw new Exception(exceptionMsg);
        }
    }

    public void addStoreToSystemDynamically(Store storeToAdd) throws Exception {
        String exceptionMsg = "";

        if (storeToAdd.getSerialNumber() <= 0) {
            exceptionMsg += "store: " + storeToAdd.getName() + "has non positive ID\n";
        }
        if (isStringStartOrEndWithWhiteSpace(storeToAdd.getName())) {
            exceptionMsg += "store # " + storeToAdd.getSerialNumber() + " has white space in edges of name\n";
        }
        if (this.storesList.containsKey(storeToAdd.getSerialNumber())) {
            exceptionMsg += "store: " + storeToAdd.getName() + " and " + this.storesList.get(storeToAdd.getSerialNumber()).getName() +
                    " have the same ID\n";
        }
        if (storeToAdd.getPPK() < 0) {
            exceptionMsg += "store: " + storeToAdd.getName() + " has non positive PPK\n";
        }

        exceptionMsg += checkCoordinateAndReturnErrorList(storeToAdd.getCoordinate(), storeToAdd.getSerialNumber(),
                this.storesList.values(), Store.class);

        if (exceptionMsg.equals("")) {
            storeToAdd.getCoordinate().setElement(storeToAdd);

            this.getCoordinateMap().changeElementInCoordinate(
                    storeToAdd.getCoordinate().getCol(), storeToAdd.getCoordinate().getRow(), storeToAdd);
            this.storesList.put(storeToAdd.getSerialNumber(), storeToAdd);
            this.updateItemsStatisticsForNewStoreInSystem(storeToAdd);

            this.systemManager.getNewStoreAlertManager().addAlert(this.areaOwner, storeToAdd);
        }
        else {
            throw new Exception(exceptionMsg);
        }
    }

    public void addItemToSystemDynamically(Item itemToAdd, Map<Integer, Double> storeIdToPrice) throws Exception {

        String exceptionMsg = "";

        if (itemToAdd.getSerialNumber() <= 0) {
            exceptionMsg += "item: " + itemToAdd.getName() + "has non positive ID\n";
        }
        if (isStringStartOrEndWithWhiteSpace(itemToAdd.getName())) {
            exceptionMsg += "item # " + itemToAdd.getSerialNumber() + " has white space in edges of name\n";
        }
        if (this.itemsList.containsKey(itemToAdd.getSerialNumber())) {
            exceptionMsg += "items: " + itemToAdd.getName() + " and " + this.itemsList.get(itemToAdd.getSerialNumber()).getName() +
                    " have the same ID\n";
        }
        if (!isPurchaseCategoryLegal(itemToAdd.getPurchaseMethod())) {
            exceptionMsg += "illegal purchase category, purchase category can be 'Quantity' or 'Weight'\n";
        }

        if (exceptionMsg.equals("")) {
            this.itemsList.put(itemToAdd.getSerialNumber(), itemToAdd);
            this.itemsSystemStatistics.put(itemToAdd, new ItemSystemStatistic(itemToAdd.getSerialNumber()));

            for(Integer storeId : storeIdToPrice.keySet()) {
                this.addItemToStore(storeId, itemToAdd.getSerialNumber(), storeIdToPrice.get(storeId));
            }
        }
        else {
            throw new Exception(exceptionMsg);
        }

    }

    public void addNewSaleInStore(Store store, String saleName, int saleTriggerItemID, double saleTriggerAmount) throws Exception {
        checkIfSaleNameIsValid(store,saleName);
        checkIfSaleAmountIsValid(saleTriggerItemID,saleTriggerAmount);

        Sale sale = new Sale(saleName,saleTriggerItemID,saleTriggerAmount);
        store.getSaleList().put(saleName,sale);

    }

    public void checkIfSaleAmountIsValid(int saleTriggerItemID, double saleTriggerAmount) throws Exception {
        if(saleTriggerAmount == 0){
            throw new Exception("the amount can not be zero");
        }

        if(itemsList.get(saleTriggerItemID).getPurchaseMethod().equals("Quantity")){
            if(Math.floor(saleTriggerAmount) != saleTriggerAmount){
                throw new Exception("the amount of this item must be an integer number");
            }

        }
    }

    public void checkIfSaleNameIsValid(Store store, String saleName) throws Exception {
        for (Sale sale:store.getSaleList().values()) {
            if(sale.getName().equals(saleName)){
                throw new Exception("this sale name already existing!");
            }
        }
    }

    private boolean isStringStartOrEndWithWhiteSpace(String string) {
        return string.charAt(0) == ' ' || string.charAt(0) == '\t' || string.charAt(0) == '\n' ||
                string.charAt(string.length() - 1) == ' ' || string.charAt(string.length() - 1) == '\t' ||
                string.charAt(string.length() - 1) == '\n';
    }

    private boolean isPurchaseCategoryLegal(String string) {
        return string.toLowerCase().equals("quantity") || string.toLowerCase().equals("weight");
    }

    private String initSystemSaleList(Map<Integer,Store> targetStoreList, Map<String, Sale> targetSaleList) {
        String errorList = "";

        for (Store store : targetStoreList.values()) {
            for (Sale sale : store.getSaleList().values()) {
                if(!targetSaleList.containsKey(sale.getName())) {
                    targetSaleList.put(sale.getName(), sale);
                }
                else {
                    errorList += "sale: " + sale.getName() + " is defined more than once in the store\n";
                }
            }
        }

        return errorList;
    }

    private String checkLocationAndReturnErrorList(Location location, int objectID, Collection<? extends Coordinatable> targetObjectList,
                                                   Class<? extends Coordinatable> objectClass) {
        String errorList = "";
        String objectClassName = objectClass.getSimpleName();

        if (location.getX() <= 0 || location.getY() <= 0 ||
                location.getX() > this.getCoordinateMap().getMAX_COL() || location.getY() > this.getCoordinateMap().getMAX_ROW()) {

            errorList +=  objectClassName +" #" + objectID + " has non valid location\n";
        }

        for(Coordinatable coordinatable : targetObjectList) {
            if(location.getX() == coordinatable.getCoordinate().getCol() && location.getY() == coordinatable.getCoordinate().getRow()) {
                errorList +=  objectClassName +" #" + objectID + " has the same location as " + coordinatable.getClass().getSimpleName() +
                        " #" + coordinatable.getSerialNumber() + "\n";
            }
        }

        return errorList;
    }

    private String checkCoordinateAndReturnErrorList(Coordinate location, int objectID, Collection<? extends Coordinatable> targetObjectList,
                                                   Class<? extends Coordinatable> objectClass) {
        String errorList = "";
        String objectClassName = objectClass.getSimpleName();

        if (location.getCol() <= 0 || location.getRow() <= 0 ||
                location.getCol() > this.getCoordinateMap().getMAX_COL() || location.getRow() > this.getCoordinateMap().getMAX_ROW()) {

            errorList +=  objectClassName +" #" + objectID + " has non valid location\n";
        }

        for(Coordinatable coordinatable : targetObjectList) {
            if(location.getCol() == coordinatable.getCoordinate().getCol() && location.getRow() == coordinatable.getCoordinate().getRow()) {
                errorList +=  objectClassName +" #" + objectID + " has the same location as " + coordinatable.getClass().getSimpleName() +
                        " #" + coordinatable.getSerialNumber() + "\n";
            }
        }

        return errorList;
    }

    private boolean tryParseStringToInt(String str) {
        try{
            int num = Integer.parseInt((str));
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean tryParseStringToDouble(String str) {
        try{
            double num = Double.parseDouble((str));
        }
        catch (TypeMismatchException ex) {
            return false;
        }
        return true;
    }

    private boolean checkingRangeValues(int input, int from , int to) { return input >= from && input <= to; }

    private boolean checkingCollectionValues(int input, Collection collection) { return collection.stream().anyMatch(t-> t.equals(input)); }

    private void isValidSerialNumberInCollection(String serialNumberStr, Collection collection)throws Exception {
        String exceptionMassage = "";
        if(!tryParseStringToInt(serialNumberStr)){
            exceptionMassage = "Invalid input, serial number must be a number, try again";
        }
        else {
            int serialNumber = Integer.parseInt(serialNumberStr);
            if (!collection.contains(serialNumber)){
                exceptionMassage = "Invalid input, the serial number: "+ serialNumberStr + " doesnt exist in the store, try again";
            }
        }
        if(exceptionMassage != ""){
            throw new Exception(exceptionMassage);
        }
    }

    public void isValidSerialNumberInput(String serialNumberStr, Set<Integer> keySet) throws Exception {
        if (!serialNumberStr.toLowerCase().equals(QUIT)){
            try{
                isValidSerialNumberInCollection(serialNumberStr,keySet);
            }
            catch (Exception e){
                throw new Exception(e.getMessage());
            }
        }
        else{
            continuePurchase = ContinuePurchase.NO;
        }
    }

    public void isValidAmountInput(StoreItem storeItem, String purchaseAmountStr) throws Exception {
        if(!purchaseAmountStr.toLowerCase().equals(QUIT)){
            try{
                storeItem.getItem().isValidAmountOfItem(purchaseAmountStr);
            }catch (Exception e){
                throw new Exception(e.getMessage());
            }
        }
        else{
            continuePurchase = ContinuePurchase.NO;
        }
    }

    public void updateInformationAfterCompleteOrder(Order order){
        ordersHistory.add(order);
        updateItemsInStoreInformation(order);
        updateSystemInformationForOrder(order);
        updateCustomerInStoreInformation(order);
    }

    private void updateSystemInformationForOrder(Order order) {

        for (Order.OrderItem orderItem : order.getOrderList()) {
                double amountThatBeenSold = orderItem.getAmountFromItemForThisOrder();
                itemsSystemStatistics.get(orderItem.getStoreItem().getItem()).addItemBeenSold(amountThatBeenSold);
        }
    }

    private void updateItemsInStoreInformation(Order order) {
        Set<Integer> itemsByWeight = new HashSet<>();

        Map<Integer, Order> storesOrder = new HashMap<>();

        for (Order.OrderItem orderItem : order.getOrderList()) {
            if(!storesOrder.containsKey(orderItem.getStore().getSerialNumber())){
                Order subOrder = new Order(order.getDate(), order.getCustomer(), order.getSerialNumber(), orderItem.getStore(), order.coordinate);
                subOrder.addDeliveryCostToOrder();
                storesOrder.put(orderItem.getStore().getSerialNumber(),subOrder);

                this.systemManager.getOrderAlertsManager().addAlert(orderItem.getStore().getStoreOwner(), subOrder);
            }
            storesOrder.get(orderItem.getStore().getSerialNumber())
                    .addOrderItemToOrderList(orderItem.getStore(),orderItem.getStoreItem(),
                            orderItem.getAmountFromItemForThisOrder(), orderItem.isInSale());
        }

        for (Order.OrderItem orderItem : order.getOrderList()) {

            int sellCountSoFar = orderItem.getStore().getHowManyTimesItemBeenSoldFromStore().get(orderItem.getStoreItem().getItem());

            if (orderItem.getStoreItem().getItem().getPurchaseMethod().equals("Weight")) {
                if (!itemsByWeight.contains(orderItem.getSerialNumber())) {
                    orderItem.getStore().getHowManyTimesItemBeenSoldFromStore().put(orderItem.getStoreItem().getItem(), sellCountSoFar + 1);
                    itemsByWeight.add(orderItem.getSerialNumber());
                }
            } else {
                orderItem.getStore().getHowManyTimesItemBeenSoldFromStore().put(orderItem.getStoreItem().getItem(), sellCountSoFar +
                        (int) orderItem.getAmountFromItemForThisOrder());
            }
        }

        for (Map.Entry<Integer, Order> subOrder: storesOrder.entrySet()) {
            storesList.get(subOrder.getKey()).getOrderHistory().add(subOrder.getValue());
            StoreOwner storeOwner = storesList.get(subOrder.getKey()).getStoreOwner();
            storeOwner.makeTransaction(DigitalWallet.TransactionType.RECEIVE,Date.from(subOrder.getValue().getDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),subOrder.getValue().getGeneralOrderPrice());
        }

        for (Store store : order.getOrderStoresList()) {
            store.addProfitFromDeliveries(store.getPPK() * order.getDistance(store.getCoordinate()));
        }

        for (int storeId : storesOrder.keySet()) {
            double profitFromItemsInSubOrder = 0;
            Order subOrder = storesOrder.get(storeId);

            for(Order.OrderItem orderItem : subOrder.getOrderList()) {
                profitFromItemsInSubOrder += orderItem.getGeneralItemPrice();
            }

            this.storesList.get(storeId).addProfitFromItems(profitFromItemsInSubOrder);
        }
    }

    private void updateCustomerInStoreInformation(Order order) {
        order.getCustomer().updateAverageCostForOrdersWithoutDelivery(order.getItemsCost());
        order.getCustomer().updateAverageCostForDelivery(order.getDeliveryPrice());
        order.getCustomer().addOrderToList(order);
    }

    private void updateItemsStatisticsForNewStoreInSystem(Store newStore) {
        for (StoreItem storeItem : newStore.getItemsList().values()) {
            if (this.itemsSystemStatistics.containsKey(storeItem.getItem())) {
                this.itemsSystemStatistics.get(storeItem.getItem()).updateItemStatistic_NewStoreSellItem(newStore, this.itemsList);
            }
        }
    }

    public void loadOrderHistoryFromFile(Path path) throws Exception {
        String errorMsg = "";

        if (!Files.exists(path)) {
            errorMsg = path.toString() + " is not a file that exist in the file system\n";
        }

        if (!errorMsg.equals("")) {
            throw new Exception(errorMsg);
        }


        try (ObjectInputStream in =
                     new ObjectInputStream(
                             new FileInputStream(path.toString()))) {
            ordersHistory = (LinkedList<Order>) in.readObject();
            orderSerialNumber = ordersHistory.get(ordersHistory.size() - 1).getSerialNumber() + 1;
        }


    }

    public void saveOrderHistoryInFile(Path filePath) throws Exception {
        try(ObjectOutputStream out =
                    new ObjectOutputStream(
                            new FileOutputStream(filePath.toString()))){
            out.writeObject(ordersHistory);
        }
    }

    public StoreOwner getAreaOwner() { return areaOwner; }

    public CoordinateSystem getCoordinateMap() { return coordinateMap; }

    public Map<Integer, Store> getStoresList() { return storesList; }

    public Map<Integer, Item> getItemsList() { return itemsList; }

    public List<Order> getOrdersHistory() { return ordersHistory; }

    public Map<Item, ItemSystemStatistic> getItemsSystemStatistics() { return itemsSystemStatistics; }

    public Map<String, Sale> getSystemSaleList() { return systemSaleList; }

    public int getStoreSerialNumber() {
        while(this.storesList.containsKey(this.storeSerialNumber)) {
            this.storeSerialNumber++;
        }
        return this.storeSerialNumber;
    }

    public int getItemSerialNumber() {
        while(this.itemsList.containsKey(this.itemSerialNumber)) {
            this.itemSerialNumber++;
        }
        return this.itemSerialNumber;
    }

    public int getCustomerSerialNumber() { return customerSerialNumber; }

    public boolean isSystemSet() { return isSystemSet; }

    public String getQUIT() { return QUIT; }

    public Order getOrderByOrderSerialNumber(int serialNumber) {
        Order orderToReturn = null;

        for(Order order : this.ordersHistory) {
            if(order.getSerialNumber() == serialNumber) {
                orderToReturn = order;
                break;
            }
        }

        return orderToReturn;
    }

    public ContinuePurchase getContinuePurchase() { return continuePurchase; }

    public DateTimeFormatter getFORMATTER() { return FORMATTER; }

    public int getOrderSerialNumberForNewOrder() { return ++orderSerialNumber; }

    public String getAreaName() { return areaName; }

    public int getOrderSerialNumber() { return orderSerialNumber; }

    public void setContinuePurchase(ContinuePurchase continuePurchase) { this.continuePurchase = continuePurchase; }

    private boolean isHoleNumber(double number) { return number == (int) number; }

    public Map<String, Map<Integer, Order>> getCurrentCustomersOrders() {
        return currentCustomersOrders;
    }

    public Map<String, Order> getCurrentCustomersSmartOrders() {
        return currentCustomersSmartOrders;
    }
}