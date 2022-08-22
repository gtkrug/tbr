MAX_DISPLAY = 10
let publicDocuments = {}

function performSearch(queryString, maxResults) {
    setSuccessStatus(`<span class="spinner-grow spinner-grow-sm"></span> Searching...`, "document-message")
    if (!queryString || queryString.trim() === "") {
        setWarningStatus("Please enter some search text.", "document-message")
        return
    }
    MAX_DISPLAY = parseInt(maxResults)

    $.ajax({
        url: PUBLIC_API_FIND_DOCS,
        method: "GET",
        type: "GET",
        data: {
            timestamp: new Date().getTime(),
            id: queryString,
            max: maxResults
        },
        dataType: "json",
        format: "json",
        success: function (data) {
            publicDocuments = data
            setResultsDiv(publicDocuments.length, queryString)
            renderPublicDocuments(0)
        },
        error: function () {
            setDangerStatus("An unexpected error occurred communicating with the server.", "document-message")
        }
    })
}

function renderPublicDocuments(offset) {
    let html = ``

    if (publicDocuments.length > MAX_DISPLAY) {
        html += buildPagination(offset, MAX_DISPLAY, publicDocuments.length, "renderPublicDocuments")
    }

    html += `<thead>`
    html += `<tr>`
    html += `<th style="width: 33%">Name</th>`
    html += `<th style="width: 33%">Create Date</th>`
    html += `<th style="width: 33%">Description</th>`
    html += `</tr>`
    html += `</thead>`
    html += `<tbody>`

    if (publicDocuments.length == 0) {
        html += `<tr><td colspan="3">There are no documents.</td></tr>`
    } else {
        publicDocuments.forEach((document, i) => {
            if (i >= offset && i < MAX_DISPLAY + offset) {
                html += `<tr><td><a href="${document.url}" target="_blank">${document.filename}</span></a></td>`
                html += `<td>${document.dateCreated.split("T")[0]} ${document.dateCreated.split("T")[1].split(":")[0]}:${document.dateCreated.split("T")[1].split(":")[1]}</td>`
                html += `<td>${document.description}</td>`
            }
        })
    }
    html += `</tbody>`

    document.getElementById("document-table").innerHTML = html
}

function setResultsDiv(count, qstr) {
    document.getElementById("document-message").innerHTML = `Total of ${count} public documents.`
}
