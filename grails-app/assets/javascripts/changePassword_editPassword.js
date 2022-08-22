function initialize(CHANGE_PASSWORD_CHANGE_EXISTING_PASSWORD) {
    function click() {
        resetStatus("message")

        const existingPassword = document.getElementById("existingPassword").value
        const newPassword = document.getElementById("newPassword").value
        const confirmPassword = document.getElementById("confirmPassword").value

        if (newPassword !== confirmPassword) {
            setDangerStatus("Passwords don't match!", "message")
        } else {
            setSuccessStatus(`<span class="spinner-grow spinner-grow-sm"></span> Changing password...`, "message")

            $.ajax({
                url: CHANGE_PASSWORD_CHANGE_EXISTING_PASSWORD,
                data: {
                    existingPassword: existingPassword,
                    newPassword: newPassword,
                    confirmPassword: confirmPassword,
                    format: "json",
                    now: new Date().toString()
                },
                dataType: "json",
                success: function (data, textStatus, jqXHR) {
                    if (data.status === "SUCCESS") {
                        setSuccessStatus(data.message, "message")
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



            document.getElementById("button").addEventListener("click", click)
        }
    })
}
