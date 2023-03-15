// render offset
let renderTrustmarkOffset = function () {
}

// render table
let renderTrustmarkTable = function (tableId, tableMetadata, tableData, offset) {
    renderTable(
        tableId,
        tableMetadata,
        tableData,
        offset,
        "renderTrustmarkOffset",
        ["Name", "Provisional", "Status"],
        "trustmarks")
}

// draw tr
let drawTrustmarkTr = function (tableMetadata, rowData) {
    return drawTr(
        tableMetadata,
        rowData,
        "edit-trustmark",
        "edit-trustmarks",
        [
            `<a href="${rowData.url}" target="_blank">${rowData.name}</a>`,
            (rowData.provisional ? `<a href="javascript:" title="${rowData.assessorComments}">YES</span>` : "NO"),
            rowData.status
        ],
        {})
}

let curriedTrustmark = curryFour(renderTrustmarkTable);

let trustmarkResults = function (pId) {
    return function (results) {
        renderTrustmarkOffset = curriedTrustmark('trustmarks-list')
        ({
            editable: false,
            fnAdd: function () {
            },
            fnRemove: function () {
            },
            fnDraw: drawTrustmarkTr,
            hRef: "",
            includeOrganizationColumn: false
        })
        (results);
        renderTrustmarkOffset(0);
    }
}

var STOP_LOOP = false
var CANCEL_LOOP = false

let bindTrustmarks = function (providerId) {
    setSuccessStatus(`<span class="spinner-grow spinner-grow-sm me-2"></span>Started the trustmark binding process trustmarks should be available once bound.`, "bindTrustmarkStatusMessage")

    initTrustmarkBindingState(providerId)
    STOP_LOOP = false
    trustmarkBindingStatusLoop(providerId)

    $.ajax({
        url: PROVIDER_BIND_TRUSTMARKS,
        dataType: "json",
        data: {
            id: providerId,
            format: "json"
        },
        beforeSend: function () {
        },
        success: function (data, statusText, jqXHR) {
            STOP_LOOP = true
            updateTrustmarkBindingDetails(providerId)

            if (!isEmtpy(data.status["SUCCESS"])) {
                setSuccessStatus(data.status["SUCCESS"], "bindTrustmarkStatusMessage")
            }

            if (!isEmtpy(data.status["WARNING"])) {
                setWarningStatus(data.status["WARNING"], "bindTrustmarkStatusMessage")
            }

            if (!isEmtpy(data.status["ERROR"])) {
                setDangerStatus(data.status["ERROR"], "bindTrustmarkStatusMessage")
            }
        },
        error: function (jqXHR, statusText, errorThrown) {
            setDangerStatus(errorThrown, "bindTrustmarkStatusMessage")
        },
        timeout: 120000 // 2 minutes
    })
}

let updateTrustmarkBindingDetails = function (providerId) {
    clearStatusMessage()

    $.ajax({
        url: PROVIDER_UPDATE_TRUSTMARK_BINDING_DETAILS,
        dataType: "json",
        async: false,
        data: {
            id: providerId,
            format: "json"
        },
        beforeSend: function () {
        },
        success: function (data, statusText, jqXHR) {
            getBoundTrustmarks(providerId)

            $("#numberOfConformanceTargetTIPs").text(data["numberOfConformanceTargetTIPs"])
            $("#numberOfTrustmarksBound").html(data["numberOfTrustmarksBound"])

            if (data["numberOfTrustmarksBound"] > 0) {
                $(".bind-trustmark-button").text("Refresh Trustmark Bindings")
            } else {
                $(".bind-trustmark-button").text("Bind Trustmarks")
            }
        },
        error: function (jqXHR, statusText, errorThrown) {
            setDangerStatus(errorThrown, "bindTrustmarkStatusMessage")
        }
    })
}

let clearStatusMessage = function () {
    $("#bindTrustmarkStatusMessage").html("")
    $("#bindTrustmarkWarningMessage").html("")
}

let getBoundTrustmarks = function (pid) {
    list(TRUSTMARK_LIST,
        trustmarkResults(pid),
        {id: pid})
}

let initTrustmarkBindingState = function (providerId) {
    clearStatusMessage()

    $.ajax({
        url: PROVIDER_INIT_TRUSTMARK_BINDING_STATE,
        dataType: "json",
        async: false,
        data: {
            id: providerId,
            format: "json"
        },
        beforeSend: function () {
        },
        success: function (data, statusText, jqXHR) {
        },
        error: function (jqXHR, statusText, errorThrown) {
            setDangerStatus(errorThrown, "bindTrustmarkStatusMessage")
        }
    })
}

function trustmarkBindingStatusLoop(providerId) {
    if (!CANCEL_LOOP) {
        if (STOP_LOOP) {
            updateTrustmarkBindingDetails(providerId)
            return
        }
        updateTrustmarkBindingStatus()
        setTimeout(trustmarkBindingStatusLoop, 250, providerId)
    }
}

function updateTrustmarkBindingStatus() {
    $.ajax({
        url: PROVIDER_TRUSTMARK_BINDING_STATUS_UPDATE,
        method: "GET",
        dataType: "json",
        cache: false,
        data: {
            format: "json",
            timestamp: new Date().getTime()
        },
        success: function (data, textStatus, jqXHR) {
            TD_INFO_STATUS_UPDATE = data
            renderTrustmarkBindingInfoStatus(data)
            if (data && data.status == "SUCCESS")
                STOP_LOOP = true
        },
        error: function (jqXHR, textStatus, errorThrown) {
            setDangerStatus(errorThrown, "bindTrustmarkStatusMessage")
        }
    })
}

function renderTrustmarkBindingInfoStatus(data) {
    function buildProgressBarHtml(data) {
        const percent = Math.floor(data.percent)
        if (data.status == "PRE-PROCESSING") {
            return `<div class="progress mb-2"><div class="progress-bar progress-bar-striped progress-bar-animated" role="progressbar" aria-valuenow="${percent}" aria-valuemin="0" aria-valuemax="100" style="width: 100%"></div></div>`
        } else {
            return `<div class="progress mb-2"><div class="progress-bar" role="progressbar" aria-valuenow="${percent}" aria-valuemin="0" aria-valuemax="100" style="width: ${percent}%"><span class="sr-only">${percent}% Complete</span></div></div>`
        }
    }

    let html = ""
    if (data && data.status) {
        html += buildProgressBarHtml(data)
        html += `<div>${data.status == "SUCCESS" ? `<span class="spinner-grow spinner-grow-sm me-2"></span>` : ``}${data.status}: ${data.message}</div>`
    }
    setSuccessStatus(html, "bindTrustmarkStatusMessage")
}

let cancelTrustmarkBindings = function (providerId) {
    CANCEL_LOOP = true
    $('#bindTrustmarkStatusMessage').html('Canceled the trustmark binding process.')
    document.getElementById("cancelTrusmarkBindings").innerHTML = ""
    $.ajax({
        url: PROVIDER_CANCEL_TRUSTMARK_BINDINGS,
        dataType: 'json',
        data: {
            id: providerId,
            format: 'json'
        },
        beforeSend: function () {
        },
        success: function (data, statusText, jqXHR) {
            // reload trustmarks
            getBoundTrustmarks(providerId)
            $('#bindTrustmarkStatusMessage').html("Status: " + data['message'])
            $('#numberOfConformanceTargetTIPs').text(data['numberOfConformanceTargetTIPs'])
            $('#numberOfTrustmarksBound').html(data['numberOfTrustmarksBound'])

        },
        error: function (jqXHR, statusText, errorThrown) {
            $('#bindTrustmarkStatusMessage').html(errorThrown)
        }
    })
}
