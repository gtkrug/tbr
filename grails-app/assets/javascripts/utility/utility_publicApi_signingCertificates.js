MAX_DISPLAY = 10
let PUBLIC_SIGNING_CERT_TABLE_ITEMS_PER_PAGE = TABLE_FULL_PAGE_ITEMS_PER_PAGE;

let publicSigningCertificates = {}

function performSearch(queryString, maxResults) {
    setSuccessStatus(`<span class="spinner-grow spinner-grow-sm"></span> Searching...`, "certificate-message")
    if (!queryString || queryString.trim() === "") {
        setWarningStatus("Please enter some search text.", "certificate-message")
        return
    }
    MAX_DISPLAY = parseInt(maxResults)

    $.ajax({
        url: PUBLIC_API_FIND_SIGNING_CERTIFICATES,
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
            publicSigningCertificates = data
            setResultsDiv(publicSigningCertificates.length, queryString)
            renderPublicSigningCertificates(0)
        },
        error: function () {
            setDangerStatus("An unexpected error occurred communicating with the server.", "certificate-message")
        }
    })
}

function renderPublicSigningCertificates(offset) {
    const tableId = "certificate-table"

    let html = ``

    if (publicSigningCertificates.length > PUBLIC_SIGNING_CERT_TABLE_ITEMS_PER_PAGE) {
        html += buildPagination(tableId, offset, PUBLIC_SIGNING_CERT_TABLE_ITEMS_PER_PAGE, publicSigningCertificates.length, "renderPublicSigningCertificates")
    }

    html += `<thead>`
    html += `<tr>`
    html += `<th style="width: 100%">URL</th>`
    html += `<th>Active</th>`
    html += `</tr>`
    html += `</thead>`
    html += `<tbody>`

    if (publicSigningCertificates.length === 0) {
        html += `<tr><td colspan="2">There are no signing certificates.</td></tr>`
    } else {
        publicSigningCertificates.forEach((signingCertificate, i) => {
            if (i >= offset && i < PUBLIC_SIGNING_CERT_TABLE_ITEMS_PER_PAGE + offset) {
                html += `<tr>`
                html += `<td><a href="${signingCertificate.url}">${signingCertificate.url}</span></a></td>`
                html += `<td class="text-center">`
                html += signingCertificate.active ?
                    `<span class="bi bi-check-circle-fill text-success" title="Certificate is active."></span>` :
                    `<span class="bi bi-x-circle-fill text-danger" title="Certificate is not active."></span>`
                html += `</td>`
                html += `</tr>`
            }
        })
    }
    html += `</tbody>`

    document.getElementById(tableId).innerHTML = html

    if (document.getElementById(`items-per-page-${tableId}`) != null) {
        document.getElementById(`items-per-page-${tableId}`).value = PUBLIC_SIGNING_CERT_TABLE_ITEMS_PER_PAGE;
    }

    publicSigningCertItemsPerPageTableEventHandler(tableId, renderPublicSigningCertificates, offset);
}

let publicSigningCertItemsPerPageTableEventHandler = function (tableId, func, offset) {

    $(`#items-per-page-${tableId}`).on('change', function() {
        const ipp = parseInt(document.getElementById(`items-per-page-${tableId}`).value);

        PUBLIC_SIGNING_CERT_TABLE_ITEMS_PER_PAGE = ipp

        func(0);
    });
}

function setResultsDiv(count, qstr) {
    document.getElementById("certificate-message").innerHTML = `Total of ${count} signing certificates.`
}
