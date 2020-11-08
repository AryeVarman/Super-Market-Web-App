var USER_LIST_URL = buildUrlWithContextPath("usersList");
var CHAT_MESSAGES_URL = buildUrlWithContextPath("chatMessages");
var POST_MESSAGE_URL = buildUrlWithContextPath("postChatMessage");
var INTERVAL_TIME_USERS = 6000;
var INTERVAL_TIME_MESSAGES = 1000;

function refreshUsersListForChat(users) {
    var usersList = $("#chat-users-list");
    usersList.empty();

    $.each(users, function (index, user) {
        usersList.append(
            $('<li>').attr('id', user.name).text(user.name)
        )
    });
}

function AddToMessages(messages) {
    $.each(messages, function (index, message) {
        $('#chat-messages-list').append(
            $('<li>').text(message.messageWriter + " => " + message.message)
        )
    })
    var msgDiv = $("#chat-msg-div");
    msgDiv.animate({ scrollTop: msgDiv[0].scrollHeight}, 1000);
}

function ajaxUsersForChat() {
    $.ajax({
        url: USER_LIST_URL,
        async: true,
        timeout: 60000,

        error: function () {
            console.log("error loading users list");
        },

        success: function(users) {
            refreshUsersListForChat(users);
        }
    });
}

function ajaxMessages() {
    $.ajax({
        url: CHAT_MESSAGES_URL,
        async: true,
        timeout:6000,

        error: function (response) {
            console.log(response.responseText);
        },

        success: function (messages) {
            if (messages === null || messages.toString().split(" ")[0] === "error!") {
                console.log("no new msg");
            } else {
                AddToMessages(messages);
            }
        }
    })
}

function ajaxSendMessage() {
    var textInput = $('#messages-text-area');

    if(textInput.val() !== "" && textInput.val() !== null) {
        $.ajax({
            url: POST_MESSAGE_URL,
            type: 'POST',
            async: true,
            timeout: 6000,
            data: {message: textInput.val()},

            error: function (response) {
                console.log(response.responseText);
            },

            success: function (messages) {
                console.log(messages);
            }
        })
    }
    textInput.val("");
}

$(function () {
    ajaxUsersForChat();

    setInterval(ajaxUsersForChat, INTERVAL_TIME_USERS);
    setInterval(ajaxMessages, INTERVAL_TIME_MESSAGES);
    $('#send-messages-button').click(ajaxSendMessage);
})