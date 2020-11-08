var ORDER_ALERT_FULL_PATH = buildUrlWithContextPath("orderAlertServlet");
var FEEDBACK_ALERT_FULL_PATH = buildUrlWithContextPath("feedbackAlertServlet");
var NEW_STORE_ALERT_FULL_PATH = buildUrlWithContextPath("newStoreAlertServlet");
var CURRENT_USER_FULL_PATH = buildUrlWithContextPath("currentUser");
var Alert_INTERVAL_TIME = 10000;

function ajaxCurrentUser() {
    $.ajax({
        url: CURRENT_USER_FULL_PATH,
        timeout: 6000,

        success: function(currentUser) {
            activateOrderAlertForStoreOwner(currentUser);
        }
    });
}

function activateOrderAlertForStoreOwner(currentUser) {
    if(currentUser.userType.toLowerCase() === "store_owner") {
        var storeOwnerName = currentUser.name;
        setInterval(ajaxOrderAlert, Alert_INTERVAL_TIME);
        setInterval(ajaxFeedbackAlert, Alert_INTERVAL_TIME);
        setInterval(ajaxNewStoreAlert, Alert_INTERVAL_TIME);
    }
}


function ajaxOrderAlert() {
    $.ajax({
        url: ORDER_ALERT_FULL_PATH,
        async: true,
        timeout:6000,

        error: function (response) {
            console.log(response.responseText);
        },

        success: function (response) {
            if (response.toString().split(" ")[0] === "error!") {
                console.log(response.responseText);
            } else {
                $.each(response, function (index, alert) {
                    displayOrderAlerts(alert);
                })
            }
        }
    })
}

function ajaxFeedbackAlert() {
    $.ajax({
        url: FEEDBACK_ALERT_FULL_PATH,
        async: true,
        timeout: 6000,

        error: function (response) {
            console.log(response.responseText);
        },

        success: function (response) {
            if (response.toString().split(" ")[0] === "error!") {
                console.log(response.responseText);
            } else {
                $.each(response, function (index, alert) {
                    displayFeedbackAlerts(alert);
                })
            }
        }
    })
}

function ajaxNewStoreAlert() {
    $.ajax({
        url: NEW_STORE_ALERT_FULL_PATH,
        async: true,
        timeout:6000,

        error: function (response) {
            console.log(response.responseText);
        },

        success: function (response) {
            if (response.toString().split(" ")[0] === "error!") {
                console.log(response.responseText);
            } else {
                $.each(response, function (index, alert) {
                    displayNewStoreAlerts(alert);
                })
            }
        }
    })
}

function displayOrderAlerts(alert) {
    var stringToDisplay = "New order!!!\t From Store: ";
    stringToDisplay += alert.storeName;

    stringToDisplay += "\tOrder Number: ";
    stringToDisplay += alert.orderNumber;

    stringToDisplay += "\tClient Name: ";
    stringToDisplay += alert.clientName;

    stringToDisplay += "\tNumber Of Item Bought: ";
    stringToDisplay += alert.numOfItemsTypes;

    stringToDisplay += "\tItem Cost: ";
    stringToDisplay += alert.itemsCost;

    stringToDisplay += "\tDelivery Cost: ";
    stringToDisplay += alert.deliveryCost.toFixed(2);

    $('#alert-box').append(
        $('<div>').attr('class', "alert").append(
            $('<span>').attr('class', "closebtn").click(function () {
                this.parentElement.style.display='none';
            }).text(stringToDisplay)
        )
    );
}

function displayFeedbackAlerts(alert) {
    var stringToDisplay = "New feedback!!!\t for Store: ";
    stringToDisplay += alert.storeName;

    stringToDisplay += "\tClient: ";
    stringToDisplay += alert.customerName;

    stringToDisplay += " gave you a score of: ";
    stringToDisplay += alert.score;

    if (alert.verbalFeedback !== "") {
        stringToDisplay += "\tAnd says: ";
        stringToDisplay += alert.verbalFeedback;
    }

    $('#alert-box').append(
        $('<div>').attr('class', "alert").append(
            $('<span>').attr('class', "closebtn").click(function () {
                this.parentElement.style.display='none';
            }).text(stringToDisplay)
        )
    );
}

function displayNewStoreAlerts(alert) {
    var stringToDisplay = "New Store!!!\t In: ";
    stringToDisplay += alert.areaName + " Area";

    stringToDisplay += "\tStore Owner Name: ";
    stringToDisplay += alert.storeOwnerName;

    stringToDisplay += "\tStore Name: ";
    stringToDisplay += alert.storeName;

    stringToDisplay += "\tLocated At: (" + alert.locationX + ", " + alert.locationY + ")";

    stringToDisplay += "\tAnd Has " + alert.numberOfItemsInStore + " Items From " + alert.numberOfItemsArea + " In The Area";

    $('#alert-box').append(
        $('<div>').attr('class', "alert").append(
            $('<span>').attr('class', "closebtn").click(function () {
                this.parentElement.style.display='none';
            }).text(stringToDisplay)
        )
    );
}

$(function() {
    ajaxCurrentUser();
})