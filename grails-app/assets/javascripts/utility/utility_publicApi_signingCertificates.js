MAX_DISPLAY = 10
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
    let html = ``

    if (publicSigningCertificates.length > MAX_DISPLAY) {
        html += buildPagination(offset, MAX_DISPLAY, publicSigningCertificates.length, "renderPublicSigningCertificates")
    }

    html += `<thead>`
    html += `<tr>`
    html += `<th style="width: 100%">URL</th>`
    html += `<th>Active</th>`
    html += `</tr>`
    html += `</thead>`
    html += `<tbody>`

    if (publicSigningCertificates.length == 0) {
        html += `<tr><td colspan="2">There are no signing certificates.</td></tr>`
    } else {
        publicSigningCertificates.forEach((signingCertificate, i) => {
            if (i >= offset && i < MAX_DISPLAY + offset) {
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

    document.getElementById("certificate-table").innerHTML = html
}

function setResultsDiv(count, qstr) {
    document.getElementById("certificate-message").innerHTML = `Total of ${count} signing certificates.`
}
