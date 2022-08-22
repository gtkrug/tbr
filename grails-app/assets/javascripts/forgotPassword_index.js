function initialize(FORGOT_PASSWORD_RESET_PASSWORD) {
    function click() {
        resetStatus("message")


        const email = document.getElementById("email").value





        setSuccessStatus(`<span class="spinner-grow spinner-grow-sm"></span> Resetting password...`, "message")

        $.ajax({
            url: FORGOT_PASSWORD_RESET_PASSWORD,
            data: {


                email: email,
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


    document.addEventListener("readystatechange", event => {
        if (event.target.readyState === "complete") {



            document.getElementById("button").addEventListener("click", click)
        }
    })
}
