document.addEventListener("readystatechange", event => {
    if (event.target.readyState === "complete") {
        if (document.getElementById("button")) {
            document.getElementById("button").addEventListener("click", function () {
                var url = SIGNING_CERTIFICATES_REVOKE
                var reason = prompt("What is the reason you are revoking this certificate?")
                if (reason) {
                    window.location.href = `${url}?reason=${encodeURI(reason)}`
                } else {
                    alert("A reason is required.")
                }
            })
        }
    }
})
