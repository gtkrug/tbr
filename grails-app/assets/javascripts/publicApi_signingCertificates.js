document.addEventListener("readystatechange", event => {
    if (event.target.readyState === "complete") {
        performSearch("ALL", 50)
    }
})
