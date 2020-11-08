var CURRENT_USER_FULL_PATH = buildUrlWithContextPath("currentUser");
var CHARGE_WALLET_PATH = buildUrlWithContextPath("chargeWallet");
var SELL_AREA_URL = buildUrlWithContextPath("pages/saleAreas/saleAreas.html");
var SHOW_TABLE_DURATION = 500;

function refreshDigitalWallet(digitalWalletStr) {
    var digitalWallet = JSON.parse(digitalWalletStr);

    $("#your-balance").text(digitalWallet.accountBalance.toFixed(2));
    var transactions = digitalWallet.transactionsHistory;

    var tableBody = $('#transaction-tbody');
    tableBody.empty();

    $.each(transactions, function (index, transaction) {
       $('<tr>').attr('class', "table-row").append(
           $('<td>').text(transaction.transactionType.toLowerCase()),
           $('<td>').text(transaction.dateStr),
           $('<td>').text(transaction.transactionAmount.toFixed(2)),
           $('<td>').text(transaction.balanceBeforeTransaction.toFixed(2)),
           $('<td>').text(transaction.balanceAfterTransaction.toFixed(2))
       ).appendTo(tableBody);
    });
}

function ajaxChargeWallet() {
    $.ajax({
        url: CHARGE_WALLET_PATH,
        type: 'POST',
        data: {transactionType: "charge", transactionAmount: $("#charge-wallet-amount").val(),
            date: $("#charge-wallet-date").val()},
        timeout: 6000,

        error: function () {
          alert("Failed charging wallet!");
        },

        success: function (digitalWallet) {

            if(digitalWallet.split(" ")[0] === "error!") {
                alert("Failed charging wallet!");
            }
            else
            {
                alert("charging wallet succeeded!");
                refreshDigitalWallet(digitalWallet);
            }
        }
    })
}

function ajaxGetWallet() {
    $.ajax({
        url: CHARGE_WALLET_PATH,
        type: 'GET',
        timeout: 6000,

        error: function () {
            alert("Failed charging wallet!");
        },

        success: function (digitalWallet) {
            console.log("got the wallet");
            refreshDigitalWallet(digitalWallet);
        }
    })
}

function hideChargeBalance(currentUser) {
    if(currentUser.userType.toLowerCase() === "store_owner") {
        $("#charge-wallet-div").hide();
    }
    else {
        $("#charge-wallet-info-div").hide();
        $("#charge-wallet-button").click(function () {
            $("#charge-wallet-info-div").toggle(SHOW_TABLE_DURATION);
        })
    }
}

function adjustPageToCurrentUser() {
    $.ajax({
        url: CURRENT_USER_FULL_PATH,
        timeout: 6000,
        async: true,

        error: function () {
            console.log("error getting current user type");
        },

        success: function (currentUser) {
            hideChargeBalance(currentUser);
        }
    });
}

function clickChargeWallet() {
    var amount =  $("#charge-wallet-amount").val();
    var date = $("#charge-wallet-date").val();
    var valid = true;

    if(amount === null || amount === "" || date === null || date === "") {
        alert("Please fill all fields!");
        valid = false;
    }
     if(valid && amount <= 0) {
         alert("Can't charge negative amount!");
         valid = false;
     }

    var date = new Date($("#charge-wallet-date").val());
    if(valid && !checkDate(date)) {
         alert("Can't charge wallet in the past!");
         valid = false;
     }
     if(valid){
         ajaxChargeWallet();
         $("#charge-wallet-amount").val("");
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

function BackToArea() {
    window.location.assign(SELL_AREA_URL);
}

$(function () {
    adjustPageToCurrentUser();
    $('#navBar-placeholder').load("../../common/navBar.html");
    $('#chat-placeholder').load("/WEB_SDM/common/chat/chat.html");

    ajaxGetWallet();
    $('#charge-wallet-submit').click(clickChargeWallet);
})