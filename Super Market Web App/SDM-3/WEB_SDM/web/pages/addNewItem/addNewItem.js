var ADD_NEW_ITEM_URL = buildUrlWithContextPath("addNewItem");
var BUILD_STORE_TABLE_URL = buildUrlWithContextPath("buildStoreOwnerStoresButtons");
var AREA_URL = buildUrlWithContextPath("pages/singleSaleArea/singleSaleArea.html");
var CURRENT_USER_FULL_PATH = buildUrlWithContextPath("currentUser");

var HIDE_ITEM_INFO_DURATION = 1000;
var SHOW_ITEM_INFO_DURATION = 1000;
var SHOW_STORES = 3000;
var HIDE_STORES = 1000;
var HIDE_STORE_BTN_DURATION = 1000;

var storeThatSellList = [];
var currentUserName = "";

function addItemToStore() {
    var storeBeenChoose = $('#store-been-choose');
    var newPriceInStore = $('#price-of-item').val();
    var newStoreId = storeBeenChoose.attr('storeId');
    var newStoreName = storeBeenChoose.attr('storeName');

    if(newPriceInStore === "") {
        alert("pick a price!");
    }
    else if(newPriceInStore <= 0) {
        alert("Price must be above 0!");
    }
    else {
        var store = {
            storeName: newStoreName,
            storeId: newStoreId,
            PriceInStore: newPriceInStore
        }
        storeThatSellList.push(store);
        $('#' + newStoreId + '-storeBtn').hide(HIDE_STORE_BTN_DURATION);

        $('#go-to-summery-btn').attr("disabled", false);
        addStoreToSummery(store);
        closeModal();
    }
}

function addStoreToSummery(store) {
    $('<tr class="table-row">').append(
        $('<td>').text(store.storeName.toLowerCase()),
        $('<td>').text(store.storeId),
        $('<td>').text(store.PriceInStore)
    ).appendTo($('#store-that-sell-tbody'));
}

function openModal(modal) {
    modal.style.display = "block";
}

function closeModal() {
    $('#price-of-item').val("");
    document.getElementById("addStoreThatSellModal").style.display = "none";
}

function buildContentStores(storesList) {
    var userHasStoreInArea = false;

    var row = $('<div class="row">');
    $.each(storesList, function (storeIndex, store) {
        var storeOwnerNameLabel = store.storeOwnerName.toLowerCase();

        if(storeOwnerNameLabel === currentUserName.toLowerCase()) {
            userHasStoreInArea = true;

            $('<div class="col-sm-3">').attr('id', store.serialNumber + "-storeBtn").append(
                $('<button>').attr('id', store.serialNumber + "-store-button").attr('class', "store-button").text(store.name)
                    .attr('storeId', store.serialNumber).attr('storeName', store.name)
            ).appendTo(row);
        }
    })

    if(!userHasStoreInArea) {
        window.alert("You dont have stores in this area");
        BackToArea();
    }

    row.appendTo($('#stores-placeholder'))


    $('#stores-placeholder').append(
        $('<button>').attr('id', "go-to-summery-btn").attr('class', "item-summery-btn")
            .click(showSummery).text("Finish Adding Items").attr("disabled", true)
    )
}

function clickConfirmItemInformation() {
    var itemName = $('#item-name').val();
    var itemType = $("#item-type-form input[type='radio']:checked").val();
    var thisBtn = $(this);

    if(itemName !== "") {
        $('#item-summery-info').text("Item: " + itemName + " Purchase Type: " + itemType.toString());

        $('#item-name-div').toggle(HIDE_ITEM_INFO_DURATION);
        $('#purchase-type-div').toggle(HIDE_ITEM_INFO_DURATION + 500);
        $('#confirm-item-info').text("Change Item Information");
        $('#stores-placeholder').toggle(SHOW_STORES);

        $(this).unbind('click')
        $(this).click(clickedChangeItemInfo);
    }
    else {
        alert("store name must be given");
    }
}

function clickedChangeItemInfo() {
    $('#item-name-div').toggle(HIDE_ITEM_INFO_DURATION);
    $('#purchase-type-div').toggle(HIDE_ITEM_INFO_DURATION + 500);
    $('#confirm-item-info').text("Confirm Item Information");
    $('#stores-placeholder').toggle(SHOW_STORES);

    $(this).unbind('click');
    $(this).click(clickConfirmItemInformation);
}

function BackToArea() {
    window.location.assign(AREA_URL);
}

function showSummery() {
    $('#stores-placeholder').toggle(SHOW_STORES);
    $('#confirm-item-info').toggle(SHOW_STORES + 250);
    $('#item-summery-div').toggle(SHOW_STORES + 500);
}

function clickDeleteAndStartOver() {
    window.location.reload();
}

function clickAddYourItem() {
    $.ajax({
        url: ADD_NEW_ITEM_URL,
        type: 'POST',
        async: true,
        timeout: 6000,
        data: {
            itemName: $('#item-name').val(),
            itemType: $("#item-type-form input[type='radio']:checked").val().toString(),
            storeList: JSON.stringify(storeThatSellList)
        },

        error: function () {
            alert("Failed Adding Item");
        },

        success: function (response) {
            if (response.toString().split(" ")[0] === "error!") {
                alert("Failed Adding Item\n" + response.toString());
            }
            else {
                alert("Item Added Successfully!");
                window.location.assign(AREA_URL);
            }
        }
    })
}

function buildStoreTable() {

    $.ajax({
        url: BUILD_STORE_TABLE_URL,
        async: true,
        timeout: 6000,
        error: function () {
            console.log("error loading stores list");
        },

        success: function (jsonObject) {
            console.log(jsonObject);
            buildContentStores(jsonObject);
            var allPanelBodies = document.getElementsByClassName("store-button");
            $.each(allPanelBodies,function (index, storeButton) {
                $(storeButton).click(function () {
                    $('#store-been-choose').attr('storeId', $(this).attr("storeId")).attr('storeName',$(this).attr("storeName"))
                        .text('#'+$(this).attr("storeId") + ' ' + $(this).attr("storeName"));
                    document.getElementById("addStoreThatSellModal").style.display = "block";
                })
            });}
    })

}

function ajaxCurrentUserName() {
    $.ajax({
        url: CURRENT_USER_FULL_PATH,
        timeout: 6000,

        success: function(currentUser) {
            currentUserName = currentUser.name.toLowerCase();
            $('#navBar-placeholder').load("../../common/navBar.html");
            $('#chat-placeholder').load("/WEB_SDM/common/chat/chat.html");
            buildStoreTable();
        }
    });
}

$(function () {
    ajaxCurrentUserName();
    // connect the navigation bar to the current page

    $('#item-summery-div').hide();
    $('#stores-placeholder').hide();
    $('#items-placeholder').hide();
    $('#confirm-item-info').click(clickConfirmItemInformation);
    $('#delete-btn').click(clickDeleteAndStartOver);
    $('#confirm-btn').click(clickAddYourItem);
})