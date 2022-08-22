function initialize(CHANGE_PASSWORD_CHANGE_PASSWORD, token) {
    function click() {
        resetStatus("message")


        const newPassword = document.getElementById("newPassword").value
        const confirmPassword = document.getElementById("confirmPassword").value

        if (newPassword !== confirmPassword) {
            setDangerStatus("Passwords don't match!", "message")
        } else {
            setSuccessStatus(`<span class="spinner-grow spinner-grow-sm"></span> Changing password...`, "message")

            $.ajax({
                url: CHANGE_PASSWORD_CHANGE_PASSWORD,
                data: {
                    token: token,
                    newPassword: newPassword,
                    confirmPassword: confirmPassword,
                    format: "json",
                    now: new Date().toString()
                },
                dataType: "json",
                success: function (data, textStatus, jqXHR) {
                    if (data.status === "SUCCESS") {
                        setSuccessStatus(`<p>${data.message}</p><p><a href="${data.loginUrl}">Return to Login!</a></p>`, "message")
                    } else {
                        setDangerStatus(data.message ? "No message." : data.message, "message")
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    setDangerStatus(textStatus, "message")
                }
            })
        }
    }

    document.addEventListener("readystatechange", event => {
        if (event.target.readyState === "complete") {
            if (!token) {
                setDangerStatus("The system requires a token.", "message")
            } else {
                document.getElementById("button").addEventListener("click", click)
            }
        }
    })
}
