var BUILD_STORES_TABLE_URL = buildUrlWithContextPath("buildStoresTable");
var ADD_ITEM_TO_CART_URL = buildUrlWithContextPath("addItemToCart");
var CHECK_IF_DESERVE_SALES = buildUrlWithContextPath("checkIfDeserveSales");
var CONFIRM_ORDER = buildUrlWithContextPath("confirmOrderServlet");
var REMOVE_ORDER = buildUrlWithContextPath("removeOrder");
var FEEDBACK = buildUrlWithContextPath("pages/feedback/feedback.html");
var CHECK_VALID_ORDER_LOCATION = buildUrlWithContextPath("checkValidOrderLocation");
var CHECK_IF_ALREADY_HAVE_ORDER = buildUrlWithContextPath("checkIfAlreadyHaveOrder");
var SALE_TYPE_IRRELEVANT = "irrelevant";
var SALE_TYPE_ONE_OF = "oneOf";
var SALE_TYPE_ALL_OR_NOTHING = "allOrNothing";
var CURRENT_STORE_TAB = "";
var ALREADY_CHOOSE_TAB = 0;
var AREA_URL = buildUrlWithContextPath("pages/singleSaleArea/singleSaleArea.html");

function buildLocationButton() {
    var selectX = $('#select-x');
    var selectY = $('#select-y');
    for(var i = 1 ; i <= 50 ;i++){
        selectX.append($("<option></option>").attr("value",i).text(i));
        selectY.append($("<option></option>").attr("value",i).text(i));
    }
}

/*"serialNumber":1,"name":"rami","ownerName":"12","location":"(3, 4),"numOfOrderMade":0,"profitFromItems":0.0,"PPK":10.0,"profitFromDeliveries":0.0},
"itemList":{"purchaseType":"Quantity","serialNumber":1,"name":"toilet paper","avgPrice":10.0,"amountOfStoresThatSell":1,"amountOfTimesBeenSold":0.0}*/
function updateCurrentTab(store) {
    CURRENT_STORE_TAB = store;
    ALREADY_CHOOSE_TAB = 1;
    updateDeliveryPrice();
    ajaxCheckIfAlreadyHaveOrder();
}

function ajaxCheckIfAlreadyHaveOrder() {
    $.ajax({
        url: CHECK_IF_ALREADY_HAVE_ORDER,
        data: {storeId: CURRENT_STORE_TAB.serialNumber, smart: "false"},
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

function calculateDistance(valueX, valueY, storeLocationX, storeLocationY) {
    var distance;
    var X = Math.pow(valueX - storeLocationX, 2);
    var Y = Math.pow(valueY - storeLocationY, 2);
    distance = (Math.sqrt(X + Y)).toFixed(2);
    return distance;
}

function ajaxCheckValidOrderLocation(){
    if(CURRENT_STORE_TAB === ""){
        alert("choose store first please")
    }else {
        $.ajax({
            url: CHECK_VALID_ORDER_LOCATION,
            data: {
                storeId: CURRENT_STORE_TAB.serialNumber,
                locationX: $('#select-x').val(),
                locationY: $('#select-y').val(),
            },
            /* timeout: 6000,*/

            error: function () {
                alert("Failed to check valid order location");
                $('#cancel-cart'+CURRENT_STORE_TAB.serialNumber).off('click').on('click',function () {
                    document.getElementById("modal" + CURRENT_STORE_TAB.serialNumber).style.display = "none";
                    $('html').css('overflow','auto');
                })
            },

            success: function (response) {
                if (response.toString().split(" ")[0] === "error!") {
                    alert(response);
                    $('#cancel-cart'+CURRENT_STORE_TAB.serialNumber).off('click').on('click',function () {
                        document.getElementById("modal" + CURRENT_STORE_TAB.serialNumber).style.display = "none";
                        $('html').css('overflow','auto');
                    })
                } else {
                    ajaxCheckIfDeserveSales();
                }
            }
        })
    }
}

function updateDeliveryPrice() {
    var valueX = $('#select-x').val()
    var valueY = $('#select-y').val()

    if(valueX !== "0" && valueY !== "0" && CURRENT_STORE_TAB !== ""){
        var storeLocationX = CURRENT_STORE_TAB.location.substring(1,CURRENT_STORE_TAB.location.indexOf(","));
        var storeLocationY = CURRENT_STORE_TAB.location.substring(CURRENT_STORE_TAB.location.indexOf(" ")+1,CURRENT_STORE_TAB.location.length-1);
        var distance = calculateDistance(valueX,valueY,storeLocationX,storeLocationY);
        var deliveryPrice = (distance * CURRENT_STORE_TAB.PPK).toFixed(2);
        $('#delivery-price' + CURRENT_STORE_TAB.serialNumber).text("delivery price: "+ (distance * CURRENT_STORE_TAB.PPK).toFixed(2));
        $('#distance-value'+CURRENT_STORE_TAB.serialNumber).text(distance);
        $('#delivery-cost-value'+CURRENT_STORE_TAB.serialNumber).text(deliveryPrice);
        $('#payment-label'+CURRENT_STORE_TAB.serialNumber).text(+$('#payment-label'+CURRENT_STORE_TAB.serialNumber).attr("onlyItemsCost") + +deliveryPrice);
    }else{
        deliveryPrice = 0;
    }
    return deliveryPrice;
}

function closeModal() {
    document.getElementById("addItemToCartModal").style.display = "none";
    document.getElementById("modal"+CURRENT_STORE_TAB.serialNumber).style.display = "none";
}

function buildContentItems(store) {
    var row = $('<div class="row">');
    $.each(store.itemList,function (itemIndex, item) {
        /*var idForPanelBody = item.serialNumber + store.name.replace(/\s+/g, '');*/
        var image_url = '../../common/images/groceries/' + item.name.toLowerCase().replace(/\s/g, '') + '.jpg';

        $.get(image_url).done(function () {
            buildImage(image_url, item, row, store);
        }).fail(function () {
            image_url = '../../common/images/groceries/default.jpg';
            buildImage(image_url, item, row, store);
        });
    })

    return row;
}

function buildImage(image_url, item, row, store) {
    $('<div class="col-sm-3">').attr('itemId',item.serialNumber).attr('itemName',item.name).attr('storeId',store.serialNumber)
        .attr('storeName',store.name).attr('data',item).append(
        $('<div class="panel panel-default">').append(
            $('<div class="panel-heading">').text('#'+ item.serialNumber + ' ' + item.name + ' | ' + 'cost: '+ item.price),
            $('<div class="panel-body">').attr('id',item.uniqId)
                .append(
                    $('<img class="img-responsive" style="width:200px; height: 200px;" alt="image">')
                        .attr('id','panel-body-smart-order-img' + item.serialNumber).attr('src',image_url)
                ),
        )
    ).click(attachClickToPictureSimpleOrder).appendTo(row);
}

function attachClickToPictureSimpleOrder() {
    $('#item-been-choose').attr('itemId',$(this).attr("itemId")).attr('itemName',$(this).attr("itemName"))
        .attr('storeId',$(this).attr("storeId")).attr('storeName',$(this).attr("storeName")).attr('data',$(this).attr("data"))
        .text('#' + $(this).attr("itemId") + ' ' + $(this).attr("itemName"));
    document.getElementById("addItemToCartModal").style.display = "block";
}


/*{"serialNumber":1,"name":"rami","ownerName":"12","location":"(3, 4),"numOfOrderMade":0,"profitFromItems":0.0,"PPK":10.0,"profitFromDeliveries":0.0},
 "itemList":{"purchaseType":"Quantity","serialNumber":1,"name":"toilet paper","avgPrice":10.0,"amountOfStoresThatSell":1,"amountOfTimesBeenSold":0.0}*/
function buildStoresTabs(storesList) {
    var navTabs = $('#nav-tabs');
    $.each(storesList,function (storeIndex, store) {
        $('<li class="store-tab">').append(
            $('<a>').attr('id',storeIndex + 'store').attr('class','nav-tabs').attr('data-toggle','tab').attr('href','#' + store.serialNumber).attr('onclick',updateCurrentTab(store)).text(store.name)
        ).appendTo(navTabs);
        $('#'+storeIndex+'store').click(function (){updateCurrentTab(store)});

        var tabContent = $('#tab-content');
        $('<div class="tab-pane fade">').attr('id', store.serialNumber).append(
            $('<div class="container">').append(
                $('<h3 class="second-header">').text(store.name),
                $('<label class="inTab-text">').text("location: "+store.location),
                $('<label class="inTab-text">').text("PPK: " + store.PPK),
                $('<label class="inTab-text">').attr("id","delivery-price"+store.serialNumber).text("delivery price: "),
                $('<br>'),

                buildContentItems(store)
            )
        ).appendTo(tabContent);
    });
}

function openCartModal() {
    if(ALREADY_CHOOSE_TAB === 0){
        alert("choose store first please");
    }else{
        document.getElementById("modal"+CURRENT_STORE_TAB.serialNumber).style.display = "block";
        $('html').css('overflow','hidden');
    }

}

function openModal(modal) {
    modal.style.display = "block";
}

function updateCartItems(itemsList){
    var cartTable = $('#items-table-body-cart'+CURRENT_STORE_TAB.serialNumber);
    var newPrice = 0;
    cartTable.empty();

    $.each(itemsList, function (itemIndex,item) {
        $('<tr class="table-row">').append(
            $('<td>').text(item.serialNumber),
            $('<td>').text(item.name),
            $('<td>').text(item.purchaseType),
            $('<td>').text(item.amountOfItem),
            $('<td>').text(item.pricePerUnit),
            $('<td>').text(item.totalCost),
            $('<td>').text(item.fromSale)
        ).appendTo(cartTable);
        newPrice = +newPrice + +item.totalCost;
    })
    $('#payment-label'+CURRENT_STORE_TAB.serialNumber).attr("onlyItemsCost",newPrice);
    var deliveryPrice = $('#delivery-cost-value' + CURRENT_STORE_TAB.serialNumber).text();
    $('#payment-label'+CURRENT_STORE_TAB.serialNumber).text(+newPrice + +deliveryPrice);
}

function addItemToCart(itemId, storeId, amount, fromSale, pricePerUnit) {
    $.ajax({
        url: ADD_ITEM_TO_CART_URL,
        data: {itemId: itemId, storeId: storeId, amount: amount, price: pricePerUnit, fromSale: fromSale},
        /*timeout: 6000,*/

        error: function () {
            alert("Failed add item to cart!");
        },

        /*{"purchaseType":"Quantity","serialNumber":1,"name":"toilet paper","amountOfItem":1.0,"pricePerUnit":10.0,"totalCost":10.0,"fromSale":"No","storeName":"rami","storeId":1}]*/
        success: function (response) {

            if(response.toString().split(" ")[0] === "error!") {
                if(amount === ""){

                }else{
                    alert(response);
                }

            }
            else
            {
                updateCartItems(response);
                document.getElementById("addItemToCartModal").style.display = "none";
                alert("item added successfully to the cart!")
                $('#amount-of-item').val('');
            }
        }
    })
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

        if(selectOption.length === 0){

        }else{
            console.log(selectOption.attr('amount'));
            addItemToCart(selectOption.attr('itemId'),storeId,selectOption.attr('amount'),'true',selectOption.attr('pricePerUnit'));
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

function ajaxRemoveOrder(serialNumber) {
    $.ajax({
        url: REMOVE_ORDER,
        data: {storeId: serialNumber, smart:"no"},
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
            addItemToCart($(selectOption).attr('itemId'),storeId,$(selectOption).attr('amount'),'true',$(selectOption).attr('pricePerUnit'));
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
                addItemToCart($(selectOption).attr('itemId'),storeId,$(selectOption).attr('amount'),'true',$(selectOption).attr('pricePerUnit'));
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

// "saleType":"oneOf","storeName":"rami","storeId":1}
function displaySales(saleList) {
    var saleModal = $('#sale-modal-placeholder')
    $.each(saleList, function (saleIndex,sale) {
        $('<div class="modal container-modal">').attr('id','sale-modal'+saleIndex).append(
            $('<div class="modal-content pre-scrollable sale-modal">').attr('id','sale-modal-content'+saleIndex).append(
                $('<span class="close">').attr('onclick',closeModal()),
                $('<h4 class="text-center">').text("Sale!"),
                $('<h3 class="text-center">').text(sale.name),
                $('<div class="container container-sale-modal">').append(
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

    document.getElementById("modal" + CURRENT_STORE_TAB.serialNumber).style.display = "block";

    $('#confirm-payment-btn' + CURRENT_STORE_TAB.serialNumber).off('click').on('click',function () {
        ajaxConfirmOrder();
        $('#confirm-payment-btn' + CURRENT_STORE_TAB.serialNumber).off('click').on('click',function () {
            confirmOrder();
        })
        $('#cancel-cart'+CURRENT_STORE_TAB.serialNumber).off('click').on('click',function () {
            document.getElementById("modal" + CURRENT_STORE_TAB.serialNumber).style.display = "none";
            $('html').css('overflow','auto');
        })
    })

    $('#cancel-cart'+CURRENT_STORE_TAB.serialNumber).off('click').on('click',function () {
        document.getElementById("are-you-sure-modal").style.display = "block";
        $('#no-are-you-sure-btn').click(function () {
            document.getElementById("are-you-sure-modal").style.display = "none";
        })

        $('#yes-are-you-sure-btn').click(function () {
            document.getElementById("are-you-sure-modal").style.display = "none";
            $('#items-table-body-cart'+CURRENT_STORE_TAB.serialNumber).empty();
            $('#payment-label' + CURRENT_STORE_TAB.serialNumber).text((updateDeliveryPrice()));
            $('#confirm-payment-btn' + CURRENT_STORE_TAB.serialNumber).off('click').on('click',function () {
                confirmOrder();
            })
            $('#cancel-cart'+CURRENT_STORE_TAB.serialNumber).off('click').on('click',function () {
                document.getElementById("modal" + CURRENT_STORE_TAB.serialNumber).style.display = "none";
                $('html').css('overflow','auto');
            })
            ajaxRemoveOrder(CURRENT_STORE_TAB.serialNumber);
        })
    })

}

function ajaxCheckIfDeserveSales(){
    $.ajax({
        url: CHECK_IF_DESERVE_SALES,
        data: {storeId: CURRENT_STORE_TAB.serialNumber, smart: "no"},
        async: true,
        timeout: 6000,

        error: function () {
            console.log("error to find sale list");
        },

        // return list of:{"name":"YallA BaLaGaN","saleTrigger":{"itemId":1,"amountNeeded":1.0},"saleOfferList":[{"itemId":1,"amountNeeded":1.0,"pricePerUnit":0.0},{"itemId":7,"amountNeeded":2.0,"pricePerUnit":20.0}],"saleType":"oneOf","storeName":"rami","storeId":1}
        success: function (saleList) {
            console.log(saleList);
            if (saleList.length !== 0) {
                displaySales(saleList);
            } else {
                ajaxConfirmOrder();
            }
        }
    })
}

function checkIfDeserveSales() {
    ajaxCheckValidOrderLocation();

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
        data: {storeId: CURRENT_STORE_TAB.serialNumber, locationX: $('#select-x').val(),
            locationY: $('#select-y').val(), date: $('#order-date').val() , smart: "false"},
        async: true,
        timeout: 6000,
        error: function () {
            console.log("error confirm order");

        },

        success: function (response) {
            if(response.toString().split(" ")[0] !== "error!") {
                document.getElementById("modal"+CURRENT_STORE_TAB.serialNumber).style.display = "none";
                $('#items-table-body-cart'+CURRENT_STORE_TAB.serialNumber).empty();
                alert("Order confirmed successfully!");
                displayRateUsModal(response);
            }else{
                alert(response.toString());
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
        checkIfDeserveSales()
    }
}

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

function buildCartModal(storeList) {
    var cartPlaceHolder = $('#cart-modal-placeholder')
    $.each(storeList,function (storeIndex,store) {
        $('#store-name-and-id'+store.serialNumber).text('#'+store.serialNumber + ' ' + store.name);
        $('#close-cart-modal'+store.serialNumber).click(function () {closeModal()});


        $('<div class="modal cart-modal">').attr('id','modal'+store.serialNumber).append(
            $('<div class="modal-dialog modal-dialog-cart" style="overflow-y: scroll; max-height:100%;  margin-top: 50px; margin-bottom:50px;">').append(
                $('<div class="modal-content cart-model-content">').append(
                    $('<span class="close">').attr('id','close-cart-modal'+store.serialNumber).text("x").click(function () {$('#cancel-cart'+store.serialNumber).click()}),
                    $('<div class="modal-header">').append(
                        $('<h4 class="text-center cart-header">').text("Your Cart!"),$('<h3 class="text-left">').attr('id','store-name-and-id'+store.serialNumber),
                        $('<div class="row center-block">').append(
                            $('<label class="inTab-text">').text("PPK: "+store.PPK),
                            $('<label class="inTab-text">').text("Distance: "),$('<label class="inTab-text">').attr('id','distance-value'+store.serialNumber),
                            $('<label class="inTab-text">').text("Delivery Cost: "),$('<label class="inTab-text">').attr('id','delivery-cost-value'+store.serialNumber).text('0'),
                        )
                    )
                    ,
                    $('<div class="items-cart-table modal-body-cart">').append(
                        $('<table class="table">').append(
                            $('<thead>').append(
                                $('<tr class="table-header">').append(
                                    $('<th>').text("Serial Number"),
                                    $('<th>').text("Name"),
                                    $('<th>').text("Purchase Method"),
                                    $('<th>').text("Amount"),
                                    $('<th>').text("Price Per Unit"),
                                    $('<th>').text("Total Cost(without delivery)"),
                                    $('<th>').text("From Sale"),
                                )
                            ),
                            $('<tbody>').attr('id','items-table-body-cart'+store.serialNumber)
                        )
                    ),
                    $('<div class="modal-footer">').append(
                        $('<div class="row footer-row text-center">').append(
                            $('<label class="payment-label">').text("Payment: "),
                            $('<label class="payment-label-amount cart-footer-text">').attr('id','payment-label'+store.serialNumber).text("0").attr('onlyItemsCost',"0"),
                            $('<button class="center-block cart-btn">').attr('id','cancel-cart'+store.serialNumber).text('Cancel').click(function () {
                                document.getElementById("modal"+CURRENT_STORE_TAB.serialNumber).style.display = "none";
                                $('html').css('overflow','auto');
                            }),
                            $('<button class="center-block cart-btn">').attr('id','confirm-payment-btn'+store.serialNumber).text("Confirm").click(function () {
                                confirmOrder();
                            }),
                            $('<button class="center-block cart-btn">').attr('id','delete-cart'+store.serialNumber).text('Delete').click(function () {
                                document.getElementById("are-you-sure-delete-modal").style.display = "block";

                                $('#no-are-you-sure-delete-btn').click(function () {
                                    document.getElementById("are-you-sure-delete-modal").style.display = "none";
                                })

                                $('#yes-are-you-sure-delete-btn').click(function () {
                                    document.getElementById("are-you-sure-delete-modal").style.display = "none";
                                    $('#items-table-body-cart'+CURRENT_STORE_TAB.serialNumber).empty();
                                    ajaxRemoveOrder(CURRENT_STORE_TAB.serialNumber);
                                    $('#payment-label' + CURRENT_STORE_TAB.serialNumber).text(updateDeliveryPrice());
                                    $('#cancel-cart'+CURRENT_STORE_TAB.serialNumber).off('click').on('click',function () {
                                        document.getElementById("modal" + CURRENT_STORE_TAB.serialNumber).style.display = "none";
                                        $('html').css('overflow','auto');
                                    })
                                    $('html').css('overflow','auto');
                                })
                            }),
                        )
                    )
                )
            )
        ).appendTo(cartPlaceHolder);
    })

    $('#add-to-cart-btn').click(function () {
        addItemToCart ($('#item-been-choose').attr('itemId'), $('#item-been-choose').attr('storeId'), $('#amount-of-item').val(), "false");
    })
}

function buildSimpleOrderPage(jsonObject) {
    buildLocationButton();
    buildStoresTabs(jsonObject);
    buildCartModal(jsonObject);
    ALREADY_CHOOSE_TAB = 0;
    CURRENT_STORE_TAB = "";
}

function BackToArea() {
    window.location.assign(AREA_URL);
}

$(function (){

    $.ajax({
        url: BUILD_STORES_TABLE_URL,
        async: true,
        timeout: 6000,
        error: function () {
            console.log("error loading store list");
        },

        /*{"serialNumber":1,"name":"rami","ownerName":"12","location":"(3, 4),"numOfOrderMade":0,"profitFromItems":0.0,"PPK":10.0,"profitFromDeliveries":0.0},
            "itemList":{"purchaseType":"Quantity","serialNumber":1,"name":"toilet paper","avgPrice":10.0,"amountOfStoresThatSell":1,"amountOfTimesBeenSold":0.0}*/
        success: function (jsonObject) {
            console.log(jsonObject);
            buildSimpleOrderPage(jsonObject);
        }
    })
})

$(function () {
    // connect the navigation bar to the current page
    $('#navBar-placeholder').load("../../common/navBar.html");
    $('#chat-placeholder').load("/WEB_SDM/common/chat/chat.html");


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