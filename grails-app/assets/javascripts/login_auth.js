document.addEventListener("readystatechange", event => {
    if (event.target.readyState === "complete") {
        document.getElementById("username").focus()
    }
})
