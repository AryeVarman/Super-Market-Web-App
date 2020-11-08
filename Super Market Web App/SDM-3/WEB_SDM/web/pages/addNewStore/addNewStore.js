var ADD_NEW_STORE_URL = buildUrlWithContextPath("addNewStore");
var BUILD_ITEMS_TABLE_URL = buildUrlWithContextPath("buildItemsTable");
var AREA_URL = buildUrlWithContextPath("pages/singleSaleArea/singleSaleArea.html");
var CHECK_VALID_ORDER_LOCATION = buildUrlWithContextPath("checkValidOrderLocation");
var HIDE_STORE_INFO_DURATION = 1000;
var SHOW_STORE_INFO_DURATION = 1000;
var SHOW_ITEMS = 3000;
var HIDE_ITEMS = 1000;
var HIDE_ITEM_IMG_DURATION = 1000;

var storeItemList = [];

function buildLocationButton() {
    var selectX = $('#select-x');
    var selectY = $('#select-y');
    for(var i = 1 ; i <= 50 ;i++){
        selectX.append($("<option></option>").attr("value",i).text(i));
        selectY.append($("<option></option>").attr("value",i).text(i));
    }
}

function addItemToStore() {
    var itemBeenChoose = $('#item-been-choose');
    var newItemPrice = $('#price-of-item').val();
    var newItemId = itemBeenChoose.attr('itemId');
    var newItemName = itemBeenChoose.attr('itemName');

    if(newItemPrice === "") {
        alert("pick a price!");
    }
    else if(newItemPrice <= 0) {
        alert("Price must be above 0!");
    }
    else {
        var item = {
            itemName: newItemName,
           itemId: newItemId,
           itemPrice: newItemPrice
        }
        storeItemList.push(item);
        $('#' + newItemId + '-itemImg').hide(HIDE_ITEM_IMG_DURATION);

        $('#go-to-summery-btn').attr("disabled", false);
        addItemToSummery(item);
        closeModal();
    }
}

function addItemToSummery(item) {
    $('<tr class="table-row">').append(
        $('<td>').text(item.itemName.toLowerCase()),
        $('<td>').text(item.itemId),
        $('<td>').text(item.itemPrice)
    ).appendTo($('#item-in-store-tbody'));
}

function openModal(modal) {
    modal.style.display = "block";
}

function closeModal() {
    $('#price-of-item').val("");
    document.getElementById("addItemToStoreModal").style.display = "none";
}

function buildContentItems(itemsList) {
    var row = $('<div class="row">');
    $.each(itemsList, function (itemIndex, item) {
        var image_url = '../../common/images/groceries/' + item.name.toLowerCase().replace(/\s/g, '') + '.jpg';

        $.get(image_url).done(function () {
            buildImage(image_url, item, row);
        }).fail(function () {
            image_url = '../../common/images/groceries/default.jpg';
            buildImage(image_url, item, row);
        });
    })
    row.appendTo($('#items-placeholder'))

    $('#items-placeholder').append(
        $('<button>').attr('id', "go-to-summery-btn").attr('class', "order-summery-btn")
            .click(showSummery).text("Finish Adding Items").attr("disabled", true)
    )
}

function buildImage(image_url, item, row) {
    $('<div class="col-sm-3">').attr('id', item.serialNumber + "-itemImg")
        .attr('itemId' ,item.serialNumber).attr('itemName', item.name).attr('data',item)
        .append(
            $('<div class="panel panel-default">').attr('id','panel-smart-order' + item.serialNumber).append(
                $('<div class="panel-heading">').text('#'+ item.serialNumber + ' ' + item.name),
                $('<div class="panel-body">').attr('id',"panel-body-smart-order" + item.serialNumber)

                    .append(
                        $('<img class="img-responsive" style="width:200px; height: 200px;" alt="image">')
                            .attr('id','panel-body-smart-order-img' + item.serialNumber)
                            .attr('src', image_url)
                )
        )
    ).click(attachClickToPictureAddNewStore).appendTo(row);
}

function attachClickToPictureAddNewStore() {
    $('#item-been-choose').attr('itemId',$(this).attr("itemId")).attr('itemName',$(this).attr("itemName"))
        .text('#'+$(this).attr("itemId") + ' ' + $(this).attr("itemName"));
    document.getElementById("addItemToStoreModal").style.display = "block";
}

function clickConfirmStoreInformation() {
    var storeName = $('#store-name').val();
    var storePPK = $('#store-ppk').val();
    var XLocation = $('#select-x').val();
    var YLocation = $('#select-y').val();
    var thisBtn = $(this);

    if(storeName !== "" && storePPK !== "" && storePPK >= 0 && XLocation > 0 && YLocation > 0) {
        $('#store-summery-info').text("Store: " + storeName + ", At Location: (" + XLocation + ", " + YLocation + "), PPK: " + storePPK);
        ajaxCheckValidOrderLocation(thisBtn);
    }
    else {
        if(storeName === "" && (storePPK === "" || storePPK < 0) && XLocation * YLocation === 0) {
            alert("store name must be given\nppk must be not negative\nlocation must be chosen")
        }
        else if(storeName === "") {
            alert("store name must be given")
        }
        else if(XLocation * YLocation === 0) {
            alert("location must be chosen")
        }
        else {
            alert("ppk must be not negative 0")
        }
    }
}

function ajaxCheckValidOrderLocation(btnClicked) {

    $.ajax({
        url: CHECK_VALID_ORDER_LOCATION,
        data: {
            locationX: $('#select-x').val(),
            locationY: $('#select-y').val(),
            smart: "true",
        },
         timeout: 6000,

        error: function () {
            alert("Failed to check valid order location");
        },

        success: function (response) {
            if (response.toString().split(" ")[0] === "error!") {
                alert(response);
            }
            else {
                $('#store-name-div').toggle(HIDE_STORE_INFO_DURATION);
                $('#store-ppk-div').toggle(HIDE_STORE_INFO_DURATION + 500);
                $('#location-div').toggle(HIDE_STORE_INFO_DURATION + 1000);
                $('#confirm-store-info').text("Change Store Information");
                $('#items-placeholder').toggle(SHOW_ITEMS);

                btnClicked.unbind('click')
                btnClicked.click(clickedChangeStoreInfo);
            }
        }
    })

}

function clickedChangeStoreInfo() {
    $('#store-name-div').toggle(HIDE_STORE_INFO_DURATION);
    $('#store-ppk-div').toggle(HIDE_STORE_INFO_DURATION + 500);
    $('#location-div').toggle(HIDE_STORE_INFO_DURATION + 1000);
    $('#confirm-store-info').text("Confirm Store Information");
    $('#items-placeholder').toggle(SHOW_ITEMS);
    $(this).unbind('click');

    $(this).click(clickConfirmStoreInformation);
}

function BackToArea() {
    window.location.assign(AREA_URL);
}

function showSummery() {
    $('#items-placeholder').toggle(SHOW_ITEMS);
    $('#confirm-store-info').toggle(SHOW_ITEMS + 250);
    $('#store-summery-div').toggle(SHOW_ITEMS + 500);
}

function clickDeleteAndStartOver() {
    window.location.reload();
}

function clickOpenYourStore() {
    $.ajax({
        url: ADD_NEW_STORE_URL,
        type: 'POST',
        async: true,
        timeout: 6000,
        data: {
            storeName: $('#store-name').val(),
            storePPK: $('#store-ppk').val(),
            storeCol: $('#select-x').val(),
            storeRow: $('#select-y').val(),
            itemList: JSON.stringify(storeItemList)
        },

        error: function () {
            alert("Failed Opening Store");
        },

        success: function (response) {
            if (response.toString().split(" ")[0] === "error!") {
                alert("Failed Opening Store\n" + response.toString());
            }
            else {
                alert("Store Opened Successfully!");
                window.location.assign(AREA_URL);
            }
        }
    })
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

    $('#items-placeholder').hide();
})

$(function () {
    // connect the navigation bar to the current page
    $('#navBar-placeholder').load("../../common/navBar.html");
    $('#chat-placeholder').load("/WEB_SDM/common/chat/chat.html");


    buildLocationButton();
    $('#store-summery-div').hide();
    $('#confirm-store-info').click(clickConfirmStoreInformation);
    $('#delete-btn').click(clickDeleteAndStartOver);
    $('#confirm-btn').click(clickOpenYourStore);
})