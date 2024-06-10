let TRUSTMARK_TABLE_ITEMS_PER_PAGE = TABLE_FULL_PAGE_ITEMS_PER_PAGE;

// render offset
let renderTrustmarkOffset = function () {
}

// render table
let renderTrustmarkTable = function (tableId, tableMetadata, tableData, offset) {
    renderTable(
        tableId,
        tableMetadata,
        tableData,
        TRUSTMARK_TABLE_ITEMS_PER_PAGE,
        offset,
        "renderTrustmarkOffset",
        ["Name", "Provisional", "Status"],
        "trustmarks")

     trustmarkItemsPerPageTableEventHandler(tableId, renderTrustmarkOffset);
}


let trustmarkItemsPerPageTableEventHandler = function (tableId, func) {

    $(`#items-per-page-${tableId}`).on('change', function() {
        const ipp = parseInt(document.getElementById(`items-per-page-${tableId}`).value);

        TRUSTMARK_TABLE_ITEMS_PER_PAGE = ipp

        func(0);
    });
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
            (rowData.provisional ? `<a href="javascript:" title="${rowData.assessorComments}">YES</a>` : "NO"),
            rowData.status
        ],
        {})
}

let curriedTrustmark = curryFour(renderTrustmarkTable);

let trustmarkResults = function () {
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

let bindTrustmarks = function (organizationId) {
    setSuccessStatus(`<span class="spinner-grow spinner-grow-sm me-2"></span>Started the trustmark binding process trustmarks should be available once bound.`, "bindTrustmarkStatusMessage")

    STOP_LOOP = false

    $.ajax({
        url: ORGANIZATION_BIND_TRUSTMARKS,
        dataType: "json",
        data: {
            id: organizationId,
            format: "json"
        },
        beforeSend: function () {
        },
        success: function (data, statusText, jqXHR) {
            STOP_LOOP = true
            updateTrustmarkBindingDetails(organizationId)

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

let updateTrustmarkBindingDetails = function (organizationId) {
    clearStatusMessage()

    $.ajax({
        url: ORGANIZATION_UPDATE_TRUSTMARK_BINDING_DETAILS,
        dataType: "json",
        async: false,
        data: {
            id: organizationId,
            format: "json"
        },
        beforeSend: function () {
        },
        success: function (data, statusText, jqXHR) {
            getBoundTrustmarks(organizationId)

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

let getBoundTrustmarks = function (oid) {
    list(ORGANIZATION_TRUSTMARKS,
        trustmarkResults(oid),
        {id: oid})
}
