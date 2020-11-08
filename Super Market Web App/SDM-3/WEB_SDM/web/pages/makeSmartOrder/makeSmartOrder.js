var BUILD_ITEMS_TABLE_URL = buildUrlWithContextPath("buildItemsTable");
var ADD_ITEM_TO_CART_SMART_ORDER_URL = buildUrlWithContextPath("addItemToCartSmartOrder");
var CHECK_IF_DESERVE_SALES = buildUrlWithContextPath("checkIfDeserveSales");
var REMOVE_ORDER = buildUrlWithContextPath("removeOrder");
var CONFIRM_ORDER = buildUrlWithContextPath("confirmOrderServlet");
var AREA_URL = buildUrlWithContextPath("pages/singleSaleArea/singleSaleArea.html");
var FEEDBACK = buildUrlWithContextPath("pages/feedback/feedback.html");
var CHECK_VALID_ORDER_LOCATION = buildUrlWithContextPath("checkValidOrderLocation");
var CHECK_IF_ALREADY_HAVE_ORDER = buildUrlWithContextPath("checkIfAlreadyHaveOrder");
var FIND_ITEM_IMAGE_URL = buildUrlWithContextPath("findItemImage");
var SHOW_TABLE_DURATION = 500;
var SALE_TYPE_IRRELEVANT = "irrelevant";
var SALE_TYPE_ONE_OF = "oneOf";
var SALE_TYPE_ALL_OR_NOTHING = "allOrNothing";
var ORDER_PAYMENT = 0;


function checkDate(date) {
    var valid = true;

    var day = date.getDate();
    var month = date.getMonth();
    var year = date.getFullYear();

    var toDaysDay = new Date().getDate();
    var toDaysMonth = new Date().getMonth();
    var toDaysYear = new Date().getFullYear();

    if(year < toDaysYear || (year === toDaysYear && month < toDaysMonth) || (year === toDaysYear && month === toDaysMonth && day < toDaysDay)) {
        valid = false;
    }

    return valid;
}

function buildLocationButton() {
    var selectX = $('#select-x');
    var selectY = $('#select-y');
    for(var i = 1 ; i <= 50 ;i++){
        selectX.append($("<option></option>").attr("value",i).text(i));
        selectY.append($("<option></option>").attr("value",i).text(i));
    }
}

function calculateDistance(valueX, valueY, storeLocationX, storeLocationY) {
    var distance;
    var X = Math.pow(valueX - storeLocationX, 2);
    var Y = Math.pow(valueY - storeLocationY, 2);
    distance = Math.sqrt(X + Y);
    return distance;
}

function updateDeliveryPrice() {
    var valueX = $('#select-x').val()
    var valueY = $('#select-y').val()
    var deliveryPrice = 0;
    if(valueX !== "0" && valueY !== "0"){
        var deliveryCostElements = document.getElementsByClassName("delivery-cost-smart-order-num")
        $.each(deliveryCostElements,function (elementIndex,element) {
            var locationX = $(element).attr("locationX");
            var locationY = $(element).attr("locationY");
            var distance = calculateDistance(valueX,valueY,locationX,locationY);
            var singleStoreDeliveryPrice = (distance * $(element).attr("PPK")).toFixed(2);
            $(element).text(singleStoreDeliveryPrice);
            deliveryPrice = +deliveryPrice + +singleStoreDeliveryPrice;
        })
        var payment = +$('#payment-label').attr('totalItemsCost') + +deliveryPrice;
        $('#payment-label').text("Payment: " + (payment).toFixed(2));
    }
    return deliveryPrice;
}

/*List of: {"storeId":2,"storeName":"hakol beshekel","PPK":0.0,"locationX":5,"locationY":1,
"itemsList":[{"purchaseType":"Weight","serialNumber":2,"name":"banana","amountOfItem":2.0,"pricePerUnit":1.0,"totalCost":2.0,"fromSale":"No","storeName":"hakol beshekel","storeId":2}]*/
function updateCartItems(smartOrderItems){
    var newPrice = 0;
    var smartCartContent =  $('#smart-cart-content')
    smartCartContent.empty();
    $('#payment-label').attr('totalItemsCost','0');
    $.each(smartOrderItems,function (smartOrderIndex,smartOrder) {

        $('<div>').attr('id', "div-store" + smartOrder.storeId).append( // create new div for each store

            $('<button>').attr('id', "store-button" + smartOrder.storeId)).appendTo(smartCartContent); // add button to the div



        var btn = $("#store-button" + smartOrder.storeId);
        $(btn).attr('class', 'block') // write relevant information on button
            .text("Serial #" + smartOrder.storeId)
            .text($(btn).text() + " || Name: " + smartOrder.storeName)
            .text($(btn).text() + " || location: " + '(' + smartOrder.locationX + ', ' + smartOrder.locationY + ')')
            .text($(btn).text() + " || PPK: " + smartOrder.PPK)


        $('<table>').attr('id', 'table-store' + smartOrder.storeId).attr('class' ,'table').append( // create items table
            $('<thead>').append( // create table head
                $('<tr class="table-header">').append(
                    $('<th>').text("serial Number"),
                    $('<th>').text("name"),
                    $('<th>').text("purchase Type"),
                    $('<th>').text("amount"),
                    $('<th>').text("Price per unit"),
                    $('<th>').text("total cost(without delivery)"),
                    $('<th>').text("from sale"),
                )
            ),
            $('<tbody>').attr('id', 'tbody' + smartOrder.storeId) // create table body and append it to the table head
        ).appendTo('#div-store' + smartOrder.storeId); // append the table to the button

        $('<label class="delivery-cost-smart-order-label">').attr('id','delivery-cost-smart-order-label'+ smartOrder.storeId).text('delivery cost: ').hide().appendTo('#div-store' + smartOrder.storeId);
        $('<label class="delivery-cost-smart-order-num">').attr("PPK",smartOrder.PPK).attr('locationY',smartOrder.locationY).attr('locationX',smartOrder.locationX).attr('id','delivery-cost-smart-order-num'+ smartOrder.storeId).text('0').hide().appendTo('#div-store' + smartOrder.storeId);

        /*{"purchaseType":"Weight","serialNumber":2,"name":"banana","amountOfItem":1.0,"pricePerUnit":1.0,"totalCost":1.0,"fromSale":"No","storeName":"hakol beshekel","storeId":2}]}]*/
        $.each(smartOrder.itemsList, function (itemIndex,item) {
            $('<tr class="table-row">').append(
                $('<td>').text(item.serialNumber),
                $('<td>').text(item.name),
                $('<td>').text(item.purchaseType),
                $('<td>').text(item.amountOfItem),
                $('<td>').text(item.pricePerUnit),
                $('<td>').text(item.totalCost),
                $('<td>').text(item.fromSale),
            ).appendTo('#tbody' + smartOrder.storeId); // append row to table body
            ORDER_PAYMENT = +ORDER_PAYMENT + +item.totalCost;
            var totalItemsCost = +$('#payment-label').attr('totalItemsCost') +  +item.totalCost;
            $('#payment-label').attr('totalItemsCost',totalItemsCost);
        })



        $('#table-store' + smartOrder.storeId).hide(); // hide table
        $('#store-button' + smartOrder.storeId).click(function () {
            $('#delivery-cost-smart-order-label'+ smartOrder.storeId).toggle(SHOW_TABLE_DURATION);
            $('#delivery-cost-smart-order-num'+ smartOrder.storeId).toggle(SHOW_TABLE_DURATION);
            $('#table-store' + smartOrder.storeId).toggle(SHOW_TABLE_DURATION);
        })
    })
    ORDER_PAYMENT = (+ORDER_PAYMENT + +updateDeliveryPrice()).toFixed(2);
    $('#payment-label').text("Payment: "+ ORDER_PAYMENT);
    ORDER_PAYMENT = 0;
}

function ajaxRemoveOrder(serialNumber) {
    $.ajax({
        url: REMOVE_ORDER,
        data: {smart: "yes"},
        async: true,
        /*timeout: 6000,*/

        error: function () {
            console.log("error to remove order");
        },

        success: function (response) {
            console.log(response);
        }
    })
}

function displayIrrelevantSale(name, saleOfferList, storeName, storeId, saleIndex) {
    $('#sale-offer-type-text'+saleIndex).text("You can choose what you want from the following options:");
    var saleOffersPlaceholder = $('#sale-offers-placeholder' + saleIndex);

    $.each(saleOfferList, function (saleOfferIndex,saleOffer) {
        $('<input type="checkbox">').attr('name','sale-offer-checkbox' /*+ saleIndex*/).attr('itemId',saleOffer.itemId)
            .attr('amount',saleOffer.amountNeeded).attr('pricePerUnit',saleOffer.pricePerUnit).attr('id','sale-offer'+saleOfferIndex).appendTo(saleOffersPlaceholder);
        $('<label>').attr('for','sale-offer-checkbox' /*+ saleIndex*/).text(saleOffer.amountNeeded + " " + saleOffer.itemName + " for " + saleOffer.pricePerUnit + "₪" + " each").appendTo(saleOffersPlaceholder);
        $('<br>').appendTo(saleOffersPlaceholder);
    })

    $('#add-sale-to-cart-btn' + saleIndex).click(function () {
        $("input:checkbox[name=sale-offer-checkbox]:checked").each(function (selectIndex,selectOption) {
            console.log($(selectOption).attr('amount'));
            ajaxAddItemToCart($(selectOption).attr('itemId'),storeId,$(selectOption).attr('amount'),'true',$(selectOption).attr('pricePerUnit'));
            $('#sale-modal-content'+saleIndex).fadeOut('slow',function () {
                $('#sale-modal'+saleIndex).remove();
            })
        })
    })
    $('#cancel-sale-btn'+saleIndex).click(function () {
        $('#sale-modal-content'+saleIndex).fadeOut('slow',function () {
            $('#sale-modal'+saleIndex).remove();
        })

    })
    var pos = $('#sale-modal'+saleIndex).position();
    $('#sale-modal'+saleIndex).css({top: pos.top - saleIndex, left: pos.left + saleIndex, position:'absolute'});
    console.log($('#sale-modal'+saleIndex).position());
    openModal(document.getElementById("sale-modal" + saleIndex));
    $('html').css('overflow','hidden');
}


function displayAllOrNothingSale(name, saleOfferList, storeName, storeId, saleIndex) {
    $('#sale-offer-type-text'+saleIndex).text("You must to take all this offers or noting:");
    var saleOffersPlaceholder = $('#sale-offers-placeholder' + saleIndex);

    $.each(saleOfferList, function (saleOfferIndex,saleOffer) {
        $('<input type="checkbox">').attr('name','sale-offer-checkbox' /*+ saleIndex*/).attr('itemId',saleOffer.itemId)
            .attr('amount',saleOffer.amountNeeded).attr('pricePerUnit',saleOffer.pricePerUnit).attr('id','sale-offer'+saleOfferIndex).appendTo(saleOffersPlaceholder);
        $('<label>').attr('for','sale-offer-checkbox' /*+ saleIndex*/).text(saleOffer.amountNeeded + " " + saleOffer.itemName + " for " + saleOffer.pricePerUnit + "₪" + " each").appendTo(saleOffersPlaceholder);
        $('<br>').appendTo(saleOffersPlaceholder);
    })

    $('#add-sale-to-cart-btn' + saleIndex).click(function () {
        var selectedList = $("input:checkbox[name=sale-offer-checkbox]:checked");
        if(selectedList.length < saleOfferList.length && selectedList !== 0){
            alert("you must choose all the offers or nothing!")
        }
        else{
            selectedList.each(function (selectIndex,selectOption) {
                console.log($(selectOption).attr('amount'));
                ajaxAddItemToCart($(selectOption).attr('itemId'),storeId,$(selectOption).attr('amount'),'true',$(selectOption).attr('pricePerUnit'));
                $('#sale-modal-content'+saleIndex).fadeOut('slow',function () {
                    $('#sale-modal'+saleIndex).remove();
                })
            })
        }
    })

    $('#cancel-sale-btn'+saleIndex).click(function () {
        $('#sale-modal-content'+saleIndex).fadeOut('slow',function () {
            $('#sale-modal'+saleIndex).remove();
        })
    })

    var pos = $('#sale-modal'+saleIndex).position();
    $('#sale-modal'+saleIndex).css({top: pos.top - saleIndex, left: pos.left + saleIndex, position:'absolute'});
    console.log($('#sale-modal'+saleIndex).position());
    openModal(document.getElementById("sale-modal" + saleIndex));
    $('html').css('overflow','hidden');
}

function openModal(modal) {
    modal.style.display = "block";
}

function closeModal() {
    document.getElementById("addItemToCartModal").style.display = "none";
    document.getElementById("smart-order-cart").style.display = "none";
}

// "saleOfferList":[{"itemId":1,"amountNeeded":1.0,"pricePerUnit":0.0},{"itemId":7,"amountNeeded":2.0,"pricePerUnit":20.0}]
function displayOneOfSale(name, saleOfferList, storeName, storeId, saleIndex) {
    $('#sale-offer-type-text'+saleIndex).text("you can choose one of the options:");
    var saleOffersPlaceholder = $('#sale-offers-placeholder' + saleIndex);

    $.each(saleOfferList, function (saleOfferIndex,saleOffer) {
        $('<input type="radio">').attr('name','sale-offer-radio' /*+ saleIndex*/).attr('itemId',saleOffer.itemId)
            .attr('amount',saleOffer.amountNeeded).attr('pricePerUnit',saleOffer.pricePerUnit).attr('id','sale-offer'+saleOfferIndex).appendTo(saleOffersPlaceholder);
        $('<label>').attr('for','sale-offer-radio' /*+ saleIndex*/).text(saleOffer.amountNeeded + " " + saleOffer.itemName + " for " + saleOffer.pricePerUnit + "₪" + " each").appendTo(saleOffersPlaceholder);
        $('<br>').appendTo(saleOffersPlaceholder);
    })

    $('#add-sale-to-cart-btn' + saleIndex).click(function () {
        var selectOption = $("input[name='sale-offer-radio']").filter(":checked");
        console.log(selectOption.attr('amount'));

        if(selectOption.length === 0){

        }else{
            ajaxAddItemToCart(selectOption.attr('itemId'),storeId,selectOption.attr('amount'),'true',selectOption.attr('pricePerUnit'));
            $('#sale-modal-content'+saleIndex).fadeOut('slow',function () {
                $('#sale-modal'+saleIndex).remove();
            })
        }
    })
    $('#cancel-sale-btn'+saleIndex).click(function () {
        $('#sale-modal-content'+saleIndex).fadeOut('slow',function () {
            $('#sale-modal'+saleIndex).remove();
        })

    })
    var pos = $('#sale-modal'+saleIndex).position();
    $('#sale-modal'+saleIndex).css({top: pos.top - saleIndex, left: pos.left + saleIndex, position:'absolute'});
    console.log($('#sale-modal'+saleIndex).position());
    openModal(document.getElementById("sale-modal" + saleIndex));
    $('html').css('overflow','hidden');

}


function displaySales(saleList) {
    var saleModal = $('#sale-modal-placeholder')
    $.each(saleList, function (saleIndex,sale) {
        $('<div class="modal container-modal">').attr('id','sale-modal'+saleIndex).append(
            $('<div class="modal-content pre-scrollable sale-modal">').attr('id','sale-modal-content'+saleIndex).append(
                $('<span class="close">').attr('onclick',closeModal()),
                $('<h4 class="text-center">').text("Sale!"),
                $('<h3 class="text-center">').text(sale.name),
                $('<div class="container">').append(
                    $('<label class="text">').attr('id','sale-trigger-type-text' + saleIndex)
                        .text("you bought " + sale.saleTrigger.amountNeeded + " of item #" + sale.saleTrigger.itemId + " " + sale.saleTrigger.itemName),
                    $('<br>'),
                    $('<label class="text">').attr('id','sale-offer-type-text' + saleIndex),
                    $('<div>').attr('id','sale-offers-placeholder' + saleIndex)
                ),
                $('<br>'),
                $('<div class="row text-center">').append(
                    $('<button class="cancel-sale-btn btn center-block">').attr('id','cancel-sale-btn'+saleIndex).text("Cancel"),
                    $('<button class="add-to-sale-btn btn center-block">').attr('id','add-sale-to-cart-btn'+saleIndex).text("Add")
                )
            )
        ).appendTo(saleModal);

        if(sale.saleType === SALE_TYPE_ONE_OF){
            displayOneOfSale(sale.name,sale.saleOfferList,sale.storeName, sale.storeId, saleIndex);
        }
        if(sale.saleType === SALE_TYPE_IRRELEVANT){
            displayIrrelevantSale(sale.name,sale.saleOfferList,sale.storeName, sale.storeId, saleIndex);
        }
        if(sale.saleType === SALE_TYPE_ALL_OR_NOTHING){
            displayAllOrNothingSale(sale.name,sale.saleOfferList,sale.storeName, sale.storeId, saleIndex);
        }
    })

    document.getElementById("smart-order-cart").style.display = "block";

    $('#close-smart-order-modal').off('click').on('click',function () {
        $('#cancel-smart-order').click();
    })

    $('#confirm-smart-order').off('click').on('click',function () {
        ajaxConfirmOrder();
        $('#confirm-smart-order').off('click').on('click',function () {
            confirmOrder();
        })
        $('#cancel-smart-order').off('click').on('click',function () {
            document.getElementById("smart-order-cart").style.display = "none";
            $('html').css('overflow','auto');
        })
    })

    $('#cancel-smart-order').off('click').on('click',function () {
        $('#delete-smart-order').click()

        $('#yes-are-you-sure-btn').click(function () {
            document.getElementById("are-you-sure-modal").style.display = "none";
            $('#smart-cart-content').empty();
            ORDER_PAYMENT = 0;
            $('#payment-label').text("Payment: "+ ORDER_PAYMENT);
            $('#confirm-smart-order').off('click').on('click',function () {
                confirmOrder();
            })
            $('#cancel-smart-order').off('click').on('click',function () {
                document.getElementById("smart-order-cart").style.display = "none";
                $('html').css('overflow','auto');
            })
            $('#close-smart-order-modal').off('click').on('click',function () {
                document.getElementById("smart-order-cart").style.display = "none";
                $('html').css('overflow','auto');
            })
            ajaxRemoveOrder();
        })
    })

}

function displayRateUsModal(orderSerialNumber){
    document.getElementById("rate-us-modal").style.display = "block";

    $('#no-rate-us-btn').click(function () {
        document.getElementById("rate-us-modal").style.display = "none";
        BackToArea();
    })

    $('#yes-rate-us-btn').click(function () {
        document.getElementById("rate-us-modal").style.display = "none";
        window.location.assign(FEEDBACK + "?orderNumber=" + orderSerialNumber);
    })

}


function ajaxConfirmOrder() {
    $('html').css('overflow','auto');

    $.ajax({
        url: CONFIRM_ORDER,
        data: { locationX: $('#select-x').val(), locationY: $('#select-y').val(), date: $('#order-date').val() , smart:"true"},
        async: true,
        timeout: 6000,
        error: function () {
            console.log("error confirm order");

        },

        success: function (response) {
            if(response.toString().split(" ")[0] !== "error!") {
                document.getElementById("smart-order-cart").style.display = "none";
                $('#smart-cart-content').empty();
                alert("Order confirmed successfully!");
                displayRateUsModal(response);
            }
            else{
                alert(response.toString());
            }

        }
    })
}

function ajaxCheckIfDeserveSales() {
    $.ajax({
        url: CHECK_IF_DESERVE_SALES,
        data: {smart:'yes'},
        async: true,
        /*timeout: 6000,*/

        error: function () {
            console.log("error to find sale list");
        },

        // return list of:{"name":"YallA BaLaGaN","saleTrigger":{"itemId":1,"amountNeeded":1.0},"saleOfferList":[{"itemId":1,"amountNeeded":1.0,"pricePerUnit":0.0},{"itemId":7,"amountNeeded":2.0,"pricePerUnit":20.0}],"saleType":"oneOf","storeName":"rami","storeId":1}
        success: function (saleList) {
            console.log(saleList);
            if(saleList.length !== 0){
                displaySales(saleList);
            }
            else {
                ajaxConfirmOrder();
            }
        }
    })
}

function confirmOrder() {
    var selectX = $('#select-x').val();
    var selectY = $('#select-y').val();
    var date = $('#order-date').val();
    var dateForCheck = new Date(date);

    if(selectX === '0' || selectY === '0'){
        alert("Please enter delivery location before confirm the order!");
    }else if(date === null || date === ""){
        alert("Please enter delivery Date before confirm the order!");
    }else if(!checkDate(dateForCheck)) {
        alert("Can't make an order in the past!");
    }else{
        ajaxCheckValidOrderLocation();
    }
}

function cancelCartBtn() {
    document.getElementById("smart-order-cart").style.display = "none";
    $('html').css('overflow','auto');
}

function ajaxCheckValidOrderLocation() {

    $.ajax({
        url: CHECK_VALID_ORDER_LOCATION,
        data: {
            locationX: $('#select-x').val(),
            locationY: $('#select-y').val(),
            smart: "true",
        },
        /* timeout: 6000,*/

        error: function () {
            alert("Failed to check valid order location");
            $('#cancel-cart').off('click').on('click', function () {
                cancelCartBtn();
            })
        },

        success: function (response) {
            if (response.toString().split(" ")[0] === "error!") {
                alert(response);
                $('#cancel-cart').off('click').on('click', function () {
                    cancelCartBtn();
                })
            }
            else {
                ajaxCheckIfDeserveSales();
            }
        }
    })
}

function ajaxCheckIfAlreadyHaveOrder(){
    $.ajax({
        url: CHECK_IF_ALREADY_HAVE_ORDER,
        data: {smart: "true"},
        async: true,
        /*timeout: 6000,*/

        error: function () {
            alert("failed to check if already have open order")
        },

        /*{"purchaseType":"Quantity","serialNumber":1,"name":"toilet paper","amountOfItem":1.0,"pricePerUnit":10.0,"totalCost":10.0,"fromSale":"No","storeName":"rami","storeId":1}]*/
        success: function (response) {
            console.log(response.toString());
            if(response.toString().split(" ")[0] !== "error!"){
                updateCartItems(response);
            }
        }
    })
}

function deleteOrderBtn() {
    document.getElementById("are-you-sure-delete-modal").style.display = "block";

    $('#no-are-you-sure-delete-btn').click(function () {
        document.getElementById("are-you-sure-delete-modal").style.display = "none";
        document.getElementById("smart-order-cart").style.display = "block";
        $('html').css('overflow','hidden');
    })

    $('#yes-are-you-sure-delete-btn').click(function () {
        document.getElementById("are-you-sure-delete-modal").style.display = "none";
        $('#smart-cart-content').empty();
        ajaxRemoveOrder();
        ORDER_PAYMENT = 0;
        $('#payment-label').text("Payment: "+ ORDER_PAYMENT);
        $('#payment-label').attr('totalItemsCost','0');
        $('#cancel-smart-order').off('click').on('click',function () {
            document.getElementById("smart-order-cart").style.display = "none";
            $('html').css('overflow','auto');
        })
        $('html').css('overflow','auto');
    })
}

function addItemToCart() {
    if($('#amount-of-item').val() === ""){

    }else{
        ajaxAddItemToCart( $('#item-been-choose').attr('itemId'),"null", $('#amount-of-item').val(),"false","null");
    }
}

function ajaxAddItemToCart(itemId,storeId,amount,fromSale,price) {
    $.ajax({
        url: ADD_ITEM_TO_CART_SMART_ORDER_URL,
        data: {itemId: itemId, storeId: storeId, amount: amount ,fromSale: fromSale, price: price },
        /*timeout: 6000,*/

        error: function () {
            alert("Failed add item to cart! - smart order");
        },

        success: function (response) {

            if(response.toString().split(" ")[0] === "error!") {
                alert(response);
            }
            else
            {
                updateCartItems(response);
                document.getElementById("addItemToCartModal").style.display = "none";
                alert("item added successfully to the cart!")
                updateDeliveryPrice();
                $('#amount-of-item').val('');
            }
        }
    })
}

function openCartModal() {
    document.getElementById("smart-order-cart").style.display = "block";
    $('html').css('overflow','hidden');
}

function findImageSrc(name , serialNumber) {

    $.ajax({
        url: FIND_ITEM_IMAGE_URL,
        async: true,
        data: {name: name},
       /* timeout: 6000,*/
        error: function () {
            console.log("error find item image");
        },

        success: function (imageSrc) {
            $('#panel-body-smart-order-img' + serialNumber).attr('src',imageSrc);
        }
    })
}

function buildContentItems(itemsList) {
    var row = $('<div class="row">');
    $.each(itemsList, function (itemIndex,item) {
        var image_url = '../../common/images/groceries/' + item.name.toLowerCase().replace(/\s/g, '') + '.jpg';

        $.get(image_url).done(function () {
            buildImage(image_url, item, row);
        }).fail(function () {
            image_url = '../../common/images/groceries/default.jpg';
            buildImage(image_url, item, row);
        });

    })
    row.appendTo($('#items-placeholder-smart-order'))
}

function buildImage(image_url, item, row) {
    $('<div class="col-sm-3">').attr('itemId',item.serialNumber).attr('itemName',item.name).attr('data',item).append(
        $('<div class="panel panel-default">').attr('id','panel-smart-order' + item.serialNumber).append(
            $('<div class="panel-heading">').text('#'+ item.serialNumber + ' ' + item.name),
            $('<div class="panel-body" style="max-height: 100%; max-width: 100%">').attr('id',"panel-body-smart-order" + item.serialNumber)
                .append(
                    $('<img class="img-responsive" style="width:200px; height: 200px;" alt="image">').attr('id','panel-body-smart-order-img' + item.serialNumber)
                        .attr('src', image_url)
                )
        )
    ).click(attachClickToPictureSmartOrder).appendTo(row);
    //findImageSrc(item.name, item.serialNumber);
}

function attachClickToPictureSmartOrder() {
    $('#item-been-choose').attr('itemId',$(this).attr("itemId")).attr('itemName',$(this).attr("itemName"))
        .text('#'+$(this).attr("itemId") + ' ' + $(this).attr("itemName"));
    document.getElementById("addItemToCartModal").style.display = "block";

}

function BackToArea() {
    window.location.assign(AREA_URL);
}

$(function (){

    $.ajax({
        url: BUILD_ITEMS_TABLE_URL,
        async: true,
        timeout: 6000,
        error: function () {
            console.log("error loading items list");
        },

        success: function (jsonObject) {
            console.log(jsonObject);
            buildContentItems(jsonObject);
            buildLocationButton();
        }
    })
    ajaxCheckIfAlreadyHaveOrder();
})

$(function () {
    // connect the navigation bar to the current page
    $('#navBar-placeholder').load("../../common/navBar.html");
    $('#chat-placeholder').load("/WEB_SDM/common/chat/chat.html");

    $('#confirm-smart-order').click(function () {
        confirmOrder();
    })

    window.onclick = function(event) {
        if (event.target === document.getElementById("addItemToCartModal")) {
            document.getElementById("addItemToCartModal").style.display = "none";
            $('#amount-of-item').val('');
        }
        if(event.target === document.getElementById("sale-modal")) {
            document.getElementById("sale-modal").style.display = "none";
            $('#sale-offers-placeholder').empty();
        }
    }
})

