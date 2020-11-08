var refreshRate = 2000;
var USER_LIST_URL = buildUrlWithContextPath("usersList");
var AREA_LIST_URL = buildUrlWithContextPath("areaList");
var CURRENT_USER_URL = buildUrlWithContextPath("currentUser");
var UPLOAD_FILE_URL = buildUrlWithContextPath("pages/saleAreas/fileUpload");
var AREA_PAGE_URL = buildUrlWithContextPath("singleSellArea");

// update the users table of the system
function refreshUsersList(users) {
    var usersTableBody = $("#user-table-body");
    usersTableBody.empty();

    $.each(users, function (index, user) {
        $('<tr class="table-row-cell table-row-cell-users">').append(
            $('<td>').text(user.name),
            $('<td>').text(user.userType)
        ).appendTo(usersTableBody);
    });
}

// update the areas table of the system
function refreshAreasList(areas) {
    var areasTableBody = $("#area-table-body");
    areasTableBody.empty();

    $.each(areas, function (index, area) {
        var selector = '#' + area.areaName.toString().replace(/\s/g, '');

       $('<tr class="table-row-cell table-row-cell-areas">').attr('id', area.areaName.replace(/\s/g, '')).append(
           $('<td>').text(area.areaName).attr('id', area.areaName),
           $('<td>').text(area.areaOwner),
           $('<td>').text(area.itemsNum),
           $('<td>').text(area.storesNum),
           $('<td>').text(area.ordersNum),
           $('<td>').text(area.orderItemsAvgPrice.toFixed(2)),
       ).appendTo(areasTableBody);
        $(selector).click(function () {
            ajaxMoveToArea(area.areaName);
        })
    });
}
//<a href="#" onclick="function()">...</a>
function ajaxUsersList() {
    $.ajax({
        url: USER_LIST_URL,
        async: true,
        timeout: 60000,

        error: function () {
            console.log("error loading users list");
        },

        success: function(users) {
            refreshUsersList(users);
        }
    });
}

function ajaxAreasList() {
    $.ajax({
        url: AREA_LIST_URL,
        async: true,
        timeout: 60000,

        error: function () {
            console.log("error loading area list");
        },

        success: function(areas) {
            refreshAreasList(areas);
        }
    });
}

function ajaxMoveToArea(areaName) {
    /*var btn = $(this)[0];
    var areaName = btn.textContent;*/
    $.ajax({
        data: {areaName: areaName},
        url: AREA_PAGE_URL,
        async: true,
        timeout: 6000,
        error: function () {
            console.log("error loading sell area");
        },

        success: function (url) {
            window.location.assign(url);

        }
    })

}

$(function() {
    // connect the navigation bar to the current page
    $('#navBar-placeholder').load("../../common/navBar.html");
    $('#chat-placeholder').load("/WEB_SDM/common/chat/chat.html");
    //The users list is refreshed automatically every second

    ajaxUsersList();
    ajaxAreasList();
    setInterval(ajaxUsersList, refreshRate);
    setInterval(ajaxAreasList, refreshRate);
});