let PSTIP_TABLE_ITEMS_PER_PAGE = TABLE_INLINE_ITEMS_PER_PAGE;

// list
let listPartnerSystemTip = function (pid) {
    list(PROVIDER_PARTNER_SYSTEMS_TIPS,
        partnerSystemTipResults(),
        {pid: pid})
}

// render offset
let renderPartnerSystemTipOffset = function () {
}

// render table
let renderPartnerSystemTipTable = function (tableId, tableMetadata, tableData, offset) {
    renderTable(
        tableId,
        tableMetadata,
        tableData.records,
        PSTIP_TABLE_ITEMS_PER_PAGE,
        offset,
        "renderPartnerSystemTipOffset",
        ["Partner System TIP"],
        "Partner System TIPs")

    pstipItemsPerPageTableEventHandler(tableId, renderPartnerSystemTipOffset);
}

let pstipItemsPerPageTableEventHandler = function (tableId, func) {

    $(`#items-per-page-${tableId}`).on('change', function() {
        const ipp = parseInt(document.getElementById(`items-per-page-${tableId}`).value);

        PSTIP_TABLE_ITEMS_PER_PAGE = ipp

        func(0);
    });
}

// draw tr
let drawPartnerSystemTipTr = function (tableMetadata, rowData) {
    return drawTrWithoutEdit(
        tableMetadata,
        rowData,
        "edit-partner-system-tip",
        "remove-partner-system-tip",
        [
            `<a href="${rowData.partnerSystemsTipIdentifier}" target="_blank">${rowData.name}</a>`
        ],
        {})
}

let curriedPartnerSystemTip = curryFour(renderPartnerSystemTipTable);

let partnerSystemTipResults = function () {
    return function (results) {
        renderPartnerSystemTipOffset = curriedPartnerSystemTip('partner-system-tip-table')
        ({
            editable: results.editable,
            fnAdd: function () {
                addPartnerSystemTip({id: 0})
            },
            fnRemove: function () {
                removePartnerSystemTip(PROVIDER_ID)
            },
            fnDraw: drawPartnerSystemTipTr,
            titleTooltip: "This list of trust interoperability profiles (TIPs) represents the requirements of this system for potential partner systems that will engage in trusted information exchanges."
        })
        (results);
        renderPartnerSystemTipOffset(0);
    }
}

// render form
let renderPartnerSystemTipForm = function (target, preFn, fn, partnerSystemTip) {
    let html = ``
    html += renderInputHelper("partnerSystemTipIdentifier", true, "Identifier", "Enter Conformance Target TIP Identifier")

    renderDialogForm(target, decorateForm("Partner Organization Trust Interoperability Profile", "partnerSystemTipFormId", html, "partnerSystemTipOk", partnerSystemTip.id === 0 ? "Add" : "Save", undefined, 'false'))

    document.getElementById("partnerSystemTipFormId").addEventListener("click", () => hideIt(target))
    document.getElementById("partnerSystemTipOk").addEventListener("click", fn)

    preFn(partnerSystemTip)
}

// populate form
let populatePartnerSystemTipForm = function (partnerSystemTip) {
    if (partnerSystemTip.id !== 0) {
        document.getElementById("partnerSystemTipIdentifier").value = partnerSystemTip.partnerSystemsTipIdentifier
    }
    document.getElementById("partnerSystemTipIdentifier").focus()
}

// add
let addPartnerSystemTip = function (partnerSystemTip) {
    resetStatus("partner-system-tip-message")

    renderPartnerSystemTipForm(
        "partner-system-tip-form",
        populatePartnerSystemTipForm,
        function () {
            updatePartnerSystemTip(
                document.getElementById("partnerSystemTipIdentifier").value,
                PROVIDER_ID)
        },
        partnerSystemTip)
}

// remove
let removePartnerSystemTip = function (pid) {
    resetStatus("partner-system-tip-message")

    getCheckedIds("remove-partner-system-tip", function (list) {
        update(PROVIDER_DELETE_PARTNER_SYSTEMS_TIPS,
            function () {
                listPartnerSystemTip(pid)
            },
            {ids: list, pid: pid}
        )
    })
}

// update
let updatePartnerSystemTip = function (identifier, pid) {
    resetStatus("partner-system-tip-message")

    let checkPartnerSystemTip = function (identifier) {
        if (identifier == null || identifier.length === 0) {
            setDangerStatus("Identifier cannot be blank.", "partner-system-tip-message")
            document.getElementById("partnerSystemTipIdentifier").focus()
            return false
        }

        return true
    }

    if (checkPartnerSystemTip(identifier)) {
        add(PROVIDER_ADD_PARTNER_SYSTEMS_TIP,
            function (data) {
                if (!data.status) {
                    data.status = {SUCCESS: "Successfully saved conformance target TIP identifier."}
                }
                setStatusMessage("partner-system-tip-message", data)
                listPartnerSystemTip(pid)
            },
            {
                identifier: identifier,
                pId: pid
            })
        hideIt("partner-system-tip-form")
    }
}
