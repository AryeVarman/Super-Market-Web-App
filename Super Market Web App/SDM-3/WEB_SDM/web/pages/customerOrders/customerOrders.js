var BUILD_CUSTOMER_ORDERS_TABLE_URL = buildUrlWithContextPath("buildCustomerOrdersTable");
var CURRENT_USER_URL = buildUrlWithContextPath("currentUser");
var AREA_URL = buildUrlWithContextPath("pages/singleSaleArea/singleSaleArea.html");
var SHOW_TABLE_DURATION = 500;

function ajaxBuildCustomerOrdersTable() {
    $.ajax({
        url: BUILD_CUSTOMER_ORDERS_TABLE_URL,
        async: true,
        timeout: 6000,
        error: function () {
            console.log("error loading stores list");
        },

        success: function (jsonObject) {
            console.log(jsonObject);
            refreshOrdersToTable(jsonObject);
        }
    })
}

function refreshOrdersToTable (orders) {
    var ordersList = $("#orders-list")[0];
    $(ordersList).empty(); // get the store list and empty it

    $.each(orders, function (orderIndex, order) {
            $('<div>').attr('id', "div-order" + orderIndex).append( // create new div for each order

                $('<button>').attr('id', "order-button" + orderIndex)).appendTo(ordersList); // add button to the div

            var btn = $("#order-button" + orderIndex)[0];
            $(btn).attr('class', 'block') // write relevant information on button
                .text("Serial #" + order.serialNum)
                .text($(btn).text() + " || Date: " + order.date)
                .text($(btn).text() + " || Deliver To: (" + order.locationX + ", " + order.locationY + ")")
                .text($(btn).text() + " || Number Of Stores: " + order.numOfStores)
                .text($(btn).text() + " || Number Of Items: " + order.numOfItems)
                .text($(btn).text() + " || Items Cost: " + order.itemsCost.toFixed(2))
                .text($(btn).text() + " ||  Delivery Cost: " + order.deliveryCost.toFixed(2))
                .text($(btn).text() + " ||  Total Cost: " + order.totalCost.toFixed(2));


            $('<table>').attr('id', 'table-order' + orderIndex).attr('class' ,'table').append( // create items table
                $('<thead>').append( // create table head
                    $('<tr class="table-header">').append(
                        $('<th>').text("serialNumber"),
                        $('<th>').text("name"),
                        $('<th>').text("purchaseType"),
                        $('<th>').text("store Number"),
                        $('<th>').text("store Name"),
                        $('<th>').text("amount"),
                        $('<th>').text("unit Price"),
                        $('<th>').text("total Price"),
                        $('<th>').text("is on sale?")
                    )
                ),
                $('<tbody>').attr('id', 'tbody' + orderIndex) // create table body and append it to the table head
            ).appendTo('#div-order' + orderIndex); // append the table to the button


            var itemsList = order.itemsList; // for each item creat table row
            $.each(itemsList, function (itemIndex, item) {
                $('<tr class="table-row">').append(
                    $('<td>').text(item.serialNum),
                    $('<td>').text(item.name),
                    $('<td>').text(item.purchaseMethod),
                    $('<td>').text(item.storeNum),
                    $('<td>').text(item.storeName),
                    $('<td>').text(item.amount),
                    $('<td>').text(item.unitPrice),
                    $('<td>').text(item.totalPrice),
                    $('<td>').text((item.isOnSale) ? "yes" : "no")
                ).appendTo('#tbody' + orderIndex); // append row to table body
            })

            $('#table-order' + orderIndex).hide(); // hide table
            $('#order-button' + orderIndex).click(function () {
                $('#table-order' + orderIndex).toggle(SHOW_TABLE_DURATION);
            })
        }
    )
}

function BackToArea() {
    window.location.assign(AREA_URL);
}

$(function() {
    ajaxBuildCustomerOrdersTable();

    // connect the navigation bar to the current page
    $('#navBar-placeholder').load("../../common/navBar.html");
    $('#chat-placeholder').load("/WEB_SDM/common/chat/chat.html");

});