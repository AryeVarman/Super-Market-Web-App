

$(function () {
    $("#page-header")[0].textContent = "Welcome to Super Duper Market!";
    $("#customer-label").addClass("tab-after")[0].textContent = "customer";
    $("#store-owner-label")[0].textContent ="store owner";

    $("#login-form").submit(function () {
        clickLogin();
        return false;
    })
})

$(function () {
    $('#login-button').click(clickLogin);
})

function clickLogin () {
    var selected = $("#login-form input[type='radio']:checked");
    var selectedVal = selected.val();
    var userName = $("#user-name-text").val();

    $.ajax({
        data: {username: userName, userType: selectedVal},
        url: "login",
        async: "true",
        error: function () {
            console.log("error");
        },

        success: function (response) {
            if (response.split(" ")[0] === "error!") {
                $("#error-label")[0].textContent = response;
            } else {
                window.location.assign(response);
            }
        }
    })
}