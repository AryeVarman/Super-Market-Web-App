var BUILD_ITEMS_TABLE_URL = buildUrlWithContextPath("buildItemsTable");
var BUILD_STORES_TABLE_URL = buildUrlWithContextPath("buildStoresTable");
var CURRENT_USER_URL = buildUrlWithContextPath("currentUser");
var CUSTOMER_ORDERS_URL = buildUrlWithContextPath("pages/customerOrders/customerOrders.html");
var STORE_OWNER_ORDERS_URL = buildUrlWithContextPath("pages/storeOwnerOrders/storeOwnerOrders.html");
var STORE_OWNER_FEEDBACKS_URL = buildUrlWithContextPath("pages/storeOwnerFeedbacks/storeOwnerFeedbacks.html");
var ALL_SELL_AREA_URL = buildUrlWithContextPath("pages/saleAreas/saleAreas.html");
var OPEN_NEW_STORE_URL = buildUrlWithContextPath("pages/addNewStore/addNewStore.html");
var ADD_NEW_ITEM_URL = buildUrlWithContextPath("pages/addNewItem/addNewItem.html");
var SHOW_TABLE_DURATION = 500;
var REFRESH_RATE = 4000;

function ajaxBuildItemsTable() {
    $.ajax({
        url: BUILD_ITEMS_TABLE_URL,
        async: true,
        timeout: 6000,
        error: function () {
            console.log("error loading items list");
        },

        success: function (jsonObject) {
            console.log(jsonObject);
            refreshItemsTable(jsonObject);
        }
    })
}

function ajaxBuildStoresTable() {
    $.ajax({
        url: BUILD_STORES_TABLE_URL,
        async: true,
        timeout: 6000,
        error: function () {
            console.log("error loading stores list");
        },

        success: function (jsonObject) {
            console.log(jsonObject);
            refreshStoresToTable(jsonObject);
        }
    })
}

// list of: {"purchaseType":"Quantity","serialNumber":1,"name":"toilet paper",
// "avgPrice":30.5,"amountOfStoresThatSell":2,"amountOfTimesBeenSold":0.0}
// and "url" : the url
function refreshItemsTable (items) {
    var itemsTableBody = $("#items-table-body");
    itemsTableBody.empty();

    $.each(items, function (index, item) {
        $('<tr class="table-row">').append(
            $('<td>').text(item.serialNumber),
            $('<td>').text(item.name),
            $('<td>').text(item.purchaseType),
            $('<td>').text(item.amountOfStoresThatSell),
            $('<td>').text(item.avgPrice.toFixed(2)),
            $('<td>').text(item.amountOfTimesBeenSold),
        ).appendTo(itemsTableBody).attr('id', "item" + index);
    });
}

/*{"serialNumber":1,"name":"rami","ownerName":"12","location":"(3, 4)",
"numOfOrderMade":0,"profitFromItems":0.0,"PPK":10.0,"profitFromDeliveries":0.0}, "itemList":{...}*/
function refreshStoresToTable (stores) {
    var storesList = $("#stores-list")[0];
    $(storesList).empty(); // get the store list and empty it

    $.each(stores, function (storeIndex, store) {
        $('#welcomeHeader').text("Welcome to " + store.areaName + " Area!");

        $('<div>').attr('id', "div-store" + storeIndex).append( // create new div for each store

                $('<button>').attr('id', "store-button" + storeIndex)).appendTo(storesList); // add button to the div

            var btn = $("#store-button" + storeIndex)[0];
            $(btn).attr('class', 'block') // write relevant information on button
                .text("Serial #" + store.serialNumber)
                .text($(btn).text() + " || Name: " + store.name)
                .text($(btn).text() + " || Owner: " + store.ownerName)
                .text($(btn).text() + " || Order Made: " + store.numOfOrderMade)
                .text($(btn).text() + " || Profit From Items: " + store.profitFromItems.toFixed(2))
                .text($(btn).text() + " || location: " + store.location)
                .text($(btn).text() + " || PPK: " + store.PPK)
                .text($(btn).text() + " || Profit From Deliveries: " + store.profitFromDeliveries.toFixed(2));

            /*{"purchaseType":"Quantity","serialNumber":1,"name":"toilet paper","avgPrice":10.0,
            "amountOfStoresThatSell":1,"amountOfTimesBeenSold":0.0}*/
            $('<table class="table">').attr('id', 'table-store' + storeIndex).append( // create items table
                $('<thead>').append( // create table head
                    $('<tr class="table-header">').append(
                        $('<th>').text("serialNumber"),
                        $('<th>').text("name"),
                        $('<th>').text("purchaseType"),
                        $('<th>').text("Price"),
                        $('<th>').text("amountOfTimesBeenSold"),
                    )
                ),
                $('<tbody>').attr('id', 'tbody' + storeIndex) // create table body and append it to the table head
            ).appendTo('#div-store' + storeIndex); // append the table to the button

            var itemsList = store.itemList; // for each item creat table row
            $.each(itemsList, function (itemIndex, item) {
                $('<tr class="table-row">').append(
                    $('<td>').text(item.serialNumber),
                    $('<td>').text(item.name),
                    $('<td>').text(item.purchaseType),
                    $('<td>').text(item.price),
                    $('<td>').text(item.amountOfTimesBeenSold),
                ).appendTo('#tbody' + storeIndex); // append row to table body
            })

            $('#table-store' + storeIndex).hide(); // hide table
            $('#store-button' + storeIndex).click(function () {
                $('#table-store' + storeIndex).toggle(SHOW_TABLE_DURATION);
            })
        }
    )
}

function showButtonsForCustomerOrStoreOwner() {
    $.ajax({
        url: CURRENT_USER_URL,
        async: true,
        timeout: 6000,
        error: function () {
            console.log("error loading stores list");
        },

        success: function (user) {
            if(user.userType.toLowerCase() === "customer"){
                $('#store-owner-buttons').hide();
            }
            else{
                $('#customer-buttons').hide();
            }
        }
    })
}

function BackToAllSellAreas() {
    window.location.assign(ALL_SELL_AREA_URL);
}

$(function() {
    ajaxBuildItemsTable();
    ajaxBuildStoresTable();

    // connect the navigation bar to the current page
    $('#navBar-placeholder').load("../../common/navBar.html");
    $('#chat-placeholder').load("/WEB_SDM/common/chat/chat.html");

    setInterval(ajaxBuildItemsTable, REFRESH_RATE);
    showButtonsForCustomerOrStoreOwner();

    $('#customer-order-history').click(function () {
        window.location.assign(CUSTOMER_ORDERS_URL);
    })

    $('#store-owner-order-history').click(function () {
        window.location.assign(STORE_OWNER_ORDERS_URL);
    })

    $('#store-owner-feedbacks').click(function () {
        window.location.assign(STORE_OWNER_FEEDBACKS_URL);
    })

    $('#open-new-store').click(function () {
        window.location.assign(OPEN_NEW_STORE_URL);
    })

    $('#add-new-item').click(function () {
        window.location.assign(ADD_NEW_ITEM_URL);
    })
});

