var CURRENT_USER_FULL_PATH = buildUrlWithContextPath("currentUser");
var FILE_UPLOAD_FULL_PATH = buildUrlWithContextPath("fileUpload");
var DIGITAL_WALLET_FULL_PATH = buildUrlWithContextPath("pages/digitalWallet/digitalWallet.html");

function ajaxCurrentUser() {
    $.ajax({
        url: CURRENT_USER_FULL_PATH,
        timeout: 6000,

        success: function(currentUser) {
            refreshCurrentUser(currentUser);
            HideAddNewSaleAreaButton(currentUser);
        }
    });
}

function refreshCurrentUser(currentUser) {
    $('#current-user')[0].textContent = "Welcome  " + currentUser.name + "!";
    $('#current-userType')[0].textContent = "UserType: " + currentUser.userType;
}

function HideAddNewSaleAreaButton(user) {
    if (user.userType.toLowerCase() === "customer") {
        $('#file-selector').hide();
        $('#file-selector-label').hide();
    }
}


function clearSession () { sessionStorage.clear(); }

$(function() {
    ajaxCurrentUser();
    $("#file-selector")[0].addEventListener('change', (event) => {
        var file = $("#file-selector")[0].files[0];
        var formData = new FormData();
        formData.append('file', file);

        $.ajax({
            url: FILE_UPLOAD_FULL_PATH,
            type: 'POST',
            data: formData, // The form with the file inputs.
            processData: false,
            contentType: false,

            error: function () {
                console.log("error uploading file!");
            },

            success: function (response) {
                console.log(response);
                alert(response);
            }
        });
    })
})

$(function() {
    $("#Digital-wallet").click(function() {
        window.location.assign(DIGITAL_WALLET_FULL_PATH);
    })
})