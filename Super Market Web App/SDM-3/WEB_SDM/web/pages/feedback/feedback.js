var ORDER_STORE_LIST = buildUrlWithContextPath("orderStoreList");
var POST_FEEDBACK = buildUrlWithContextPath("postFeedback");
var AREA_URL = buildUrlWithContextPath("pages/singleSaleArea/singleSaleArea.html");
var HIDE_FEEDBACK_DURATION = 1000;
var NUMBER_OF_VISIBLE_STORES;

function ajaxStoresListForFeedback(orderNum) {
    $.ajax({
        url: ORDER_STORE_LIST,
        type: 'GET',
        async: true,
        timeout: 6000,
        data: {orderNumber: orderNum},

        error: function () {
            console.log("Failed getting stores!");
        },

        success: function (storeList) {
            NUMBER_OF_VISIBLE_STORES = storeList.length;
            console.log(storeList);
            buildFeedbackList(storeList);
        }
    })
}

function buildFeedbackList (storeList) {
    var feedbackList = $('#feedback-list');
    feedbackList.empty();

    $.each(storeList, function (index, store) {
        var storeName = store.name;
        var storeNum = store.serialNumber;
        buildStoreFeedback(storeName, storeNum);
    })
}

function buildStoreFeedback(storeName, storeNumber) {
    var feedbackList = $('#feedback-list');

    $('<div>').attr('class', "row text-center col-md-6 col-md-offset-3 form-container").attr('id', storeNumber + "store-feedback-div").append(
        buildFeedbackHeader(storeName, storeNumber),
        buildFeedbackRadioButtons(storeName, storeNumber),
        buildFeedbackTextBox(storeName, storeNumber),
        buildFeedbackPostButton(storeName, storeNumber)
 ).appendTo(feedbackList);
}

function buildFeedbackHeader(storeName, storeNumber) {
    return $('<h2>').attr('id', storeNumber + " storeHeader").text(storeName);
}

function buildFeedbackRadioButtons(storeName, storeNumber) {
    return $('<div>').attr('class', "row").append(
                $('<div>').attr('class', "col-sm-12 form-group").attr('id', storeNumber + "score-div").append(
                    $('<label>').text("How do you rate your overall experience?"),
                    $('<form>').attr('id', storeNumber + "score-form").append(

                        $('<label>').attr('class', "radio-inline").text("1").append(
                            $('<input>').attr('type', "radio").attr('id', storeNumber + "radio1").attr('value', "1")
                                .attr('name', storeNumber + "radio").attr('class', storeNumber + "radio-class")
                        ),
                        $('<label>').attr('class', "radio-inline").text("2").append(
                            $('<input>').attr('type', "radio").attr('id', storeNumber + "radio2").attr('value', "2")
                                .attr('name', storeNumber + "radio").attr('class', storeNumber + "radio-class")
                        ),
                        $('<label>').attr('class', "radio-inline").text("3").append(
                            $('<input>').attr('type', "radio").attr('id', storeNumber + "radio3").attr('value', "3")
                                .attr('name', storeNumber + "radio").attr('class', storeNumber + "radio-class")
                        ),
                        $('<label>').attr('class', "radio-inline").text("4").append(
                            $('<input>').attr('type', "radio").attr('id', storeNumber + "radio4").attr('value', "4")
                                .attr('name', storeNumber + "radio").attr('class', storeNumber + "radio-class")
                        ),
                        $('<label>').attr('class', "radio-inline").text("5").append(
                            $('<input>').attr('type', "radio").attr('id', storeNumber + "radio5").attr('value', "5")
                                .attr('name', storeNumber + "radio").attr('class', storeNumber + "radio-class").attr('checked', "checked")
                        )
                    )
                )
    )
}

function buildFeedbackTextBox(storeName, storeNumber) {
    return $('<div>').attr('class', "row").attr('id', storeNumber + " textBox-div").append(
                $('<label>').text("Comments:"),
                $('<textarea>').attr('class', "form-control feedback-textarea").attr('type', "textarea").attr('id', storeNumber + "comments")
                    .attr('placeholder', "Your Comments").attr('rows', "4")
                )
}

function buildFeedbackPostButton(storeName, storeNumber) {
    return $('<button>').attr('class', "post-btn").attr('id', storeNumber + " post-button").text("Post").click(function () {
        var btn = $(this);
        var storeNumber = btn.attr('id')[0].split(" ")[0];
        ajaxSendFeedback(storeNumber);
    });
}

function ajaxSendFeedback(storeNum) {
    var scoreSelected = $("#" + storeNum + "score-form input[type='radio']:checked").val();
    var clientComments = $("#" + storeNum + "comments").val();
    var searchParams = new URLSearchParams(window.location.search);
    var orderNum = searchParams.get('orderNumber');

    /*var theStore = storeNum;*/

    $.ajax({
        url: POST_FEEDBACK,
        type: 'POST',
        async: true,
        timeout: 6000,
        data: {
            orderNumber: orderNum,
            storeNumber: storeNum,
            score: scoreSelected,
            comments: clientComments
        },

        error: function () {
            alert("Failed posting feedback");
        },

        success: function (response) {
            NUMBER_OF_VISIBLE_STORES--;

            console.log(response);
            alert(response.toString());

            if(response.toString().split(" ")[0] !== "error!") {
                $("#" + storeNum + "store-feedback-div").toggle(HIDE_FEEDBACK_DURATION);

                if(NUMBER_OF_VISIBLE_STORES === 0) {
                    alert("Thanks for rating us!")
                    BackToArea();
                }
            }
        }
    })
}

function BackToArea() {
    window.location.assign(AREA_URL);
}

$(function () {
    $('#chat-placeholder').load("/WEB_SDM/common/chat/chat.html");

    var searchParams = new URLSearchParams(window.location.search);
    var orderNumber = searchParams.get('orderNumber');

    ajaxStoresListForFeedback(orderNumber);
})
