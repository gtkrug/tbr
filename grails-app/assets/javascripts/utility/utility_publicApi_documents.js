MAX_DISPLAY = 10
let PUBLIC_DOCUMENT_TABLE_ITEMS_PER_PAGE = TABLE_FULL_PAGE_ITEMS_PER_PAGE;

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
    const tableId = "document-table"

    let html = ``

    if (publicDocuments.length > PUBLIC_DOCUMENT_TABLE_ITEMS_PER_PAGE) {
        html += buildPagination(tableId, offset, PUBLIC_DOCUMENT_TABLE_ITEMS_PER_PAGE, publicDocuments.length, "renderPublicDocuments")
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
            if (i >= offset && i < PUBLIC_DOCUMENT_TABLE_ITEMS_PER_PAGE + offset) {
                html += `<tr><td><a href="${document.url}" target="_blank">${document.filename}</span></a></td>`
                html += `<td>${document.dateCreated.split("T")[0]} ${document.dateCreated.split("T")[1].split(":")[0]}:${document.dateCreated.split("T")[1].split(":")[1]}</td>`
                html += `<td>${document.description}</td>`
            }
        })
    }
    html += `</tbody>`

    document.getElementById(tableId).innerHTML = html

    if (document.getElementById(`items-per-page-${tableId}`) != null) {
        document.getElementById(`items-per-page-${tableId}`).value = PUBLIC_DOCUMENT_TABLE_ITEMS_PER_PAGE;
    }

    publicDocumentItemsPerPageTableEventHandler(tableId, renderPublicDocuments, offset);
}

let publicDocumentItemsPerPageTableEventHandler = function (tableId, func, offset) {

    $(`#items-per-page-${tableId}`).on('change', function() {
        const ipp = parseInt(document.getElementById(`items-per-page-${tableId}`).value);

        PUBLIC_DOCUMENT_TABLE_ITEMS_PER_PAGE = ipp

        func(0);
    });
}

function setResultsDiv(count, qstr) {
    document.getElementById("document-message").innerHTML = `Total of ${count} public documents.`
}
