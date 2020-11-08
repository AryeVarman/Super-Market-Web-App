var GET_FEEDBACKS_PATH = buildUrlWithContextPath("getFeedbacksServlet");
var AREA_URL = buildUrlWithContextPath("pages/singleSaleArea/singleSaleArea.html");
var SHOW_FEEDBACK_DURATION = 100;

function buildFeedbackTable(feedbackList) {

    var tableBody = $('#feedback-tbody');

    $.each(feedbackList, function (index, feedback) {
        if(feedback.verbalFeedback === "") {
            feedback.verbalFeedback = "No Verbal Feedback";
        }

        tableBody.append(
            $('<tr class="table-row">').append(
                $('<td>').text(feedback.storeName),
                $('<td>').text(feedback.customerName),
                $('<td>').text(feedback.date),
                $('<td>').text(feedback.score),
                $('<td>').append(
                    $('<button>').text(feedback.customerName + " says:").attr('id', index + "-feedback-button").attr('class', "feedback-button")
                        .click(
                            function () {
                                console.log("here here");
                                var feedbackNumber = $(this).attr('id')[0].split("-")[0];
                                $("#" + feedbackNumber + "-feedback-tr").toggle(SHOW_FEEDBACK_DURATION);
                            }
                        )
                ),
            ),
            $('<tr>').attr('id', index + "-feedback-tr").attr('class', "verbal-feedback").hide().append(
                $('<td>').attr('colspan', "5").append(
                        $('<p>').text(feedback.verbalFeedback)
                )
            )
        )
    })
}


function ajaxGetFeedbacks() {
    $.ajax({
        url: GET_FEEDBACKS_PATH,
        type: 'GET',
        timeout: 6000,

        error: function () {
            alert("Failed getting feedbacks!");
        },

        success: function (response) {
            if (response.toString().split(" ")[0] === "error!") {
                console.log(response.responseText);
                alert(response);
            } else {
                console.log("got the feedbacks");
                buildFeedbackTable(response);
            }
        }
    })

}

function BackToArea() {
    window.location.assign(AREA_URL);
}

$(function () {
    $('#navBar-placeholder').load("../../common/navBar.html");
    $('#chat-placeholder').load("/WEB_SDM/common/chat/chat.html");

    ajaxGetFeedbacks();
})