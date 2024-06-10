let CTTIP_TABLE_ITEMS_PER_PAGE = TABLE_INLINE_ITEMS_PER_PAGE;

// list
let listConformanceTargetTip = function (pid) {
    list(CONFORMANCE_TARGET_TIP_LIST,
        conformanceTargetTipResults(),
        {id: pid})
}

// render offset
let renderConformanceTargetTipOffset = function () {
}

// render table
let renderConformanceTargetTipTable = function (tableId, tableMetadata, tableData, offset) {
    renderTable(
        tableId,
        tableMetadata,
        tableData.records,
        CTTIP_TABLE_ITEMS_PER_PAGE,
        offset,
        "renderConformanceTargetTipOffset",
        ["Conformance Target TIP"],
        "Conformance Target TIPs")

    cttipItemsPerPageTableEventHandler(tableId, renderConformanceTargetTipOffset);
}

let cttipItemsPerPageTableEventHandler = function (tableId, func) {

    $(`#items-per-page-${tableId}`).on('change', function() {
        const ipp = parseInt(document.getElementById(`items-per-page-${tableId}`).value);

        CTTIP_TABLE_ITEMS_PER_PAGE = ipp

        func(0);
    });
}

// draw tr
let drawConformanceTargetTipTr = function (tableMetadata, rowData) {
    return drawTrWithoutEdit(
        tableMetadata,
        rowData,
        "edit-conformance-target-tip",
        "remove-conformance-target-tip",
        [
            `<a href="${rowData.conformanceTargetTipIdentifier}" target="_blank">${rowData.name}</a>`
        ],
        {})
}

let curriedConformanceTargetTip = curryFour(renderConformanceTargetTipTable);

let conformanceTargetTipResults = function () {
    return function (results) {
        renderConformanceTargetTipOffset = curriedConformanceTargetTip('conformance-target-tip-table')
        ({
            editable: results.editable,
            fnAdd: function () {
                addConformanceTargetTip({id: 0})
            },
            fnRemove: function () {
                removeConformanceTargetTip(PROVIDER_ID)
            },
            fnDraw: drawConformanceTargetTipTr,
            titleTooltip: "Conformance Target TIPs are trust interoperability profiles that this system aspires to fully earn, and frequently has earned most or all of the required trustmarks."
        })
        (results);
        renderConformanceTargetTipOffset(0);
    }
}

// render form
let renderConformanceTargetTipForm = function (target, preFn, fn, conformanceTargetTip) {
    let html = ``
    html += renderInputHelper("conformanceTargetTipIdentifier", true, "Identifier", "Enter Conformance Target TIP Identifier")

    renderDialogForm(target, decorateForm("Conformance Target Trust Interoperability Profile", "conformanceTargetTipFormId", html, "conformanceTargetTipOk", conformanceTargetTip.id === 0 ? "Add" : "Save", undefined, 'false'))

    document.getElementById("conformanceTargetTipFormId").addEventListener("click", () => hideIt(target))
    document.getElementById("conformanceTargetTipOk").addEventListener("click", fn)

    preFn(conformanceTargetTip)
}

// populate form
let populateConformanceTargetTipForm = function (conformanceTargetTip) {
    if (conformanceTargetTip.id !== 0) {
        document.getElementById("conformanceTargetTipIdentifier").value = conformanceTargetTip.partnerSystemsTipIdentifier
    }
    document.getElementById("conformanceTargetTipIdentifier").focus()
}

// add
let addConformanceTargetTip = function (conformanceTargetTip) {
    resetStatus("conformance-target-tip-message")

    renderConformanceTargetTipForm(
        "conformance-target-tip-form",
        populateConformanceTargetTipForm,
        function () {
            updateConformanceTargetTip(
                document.getElementById("conformanceTargetTipIdentifier").value,
                PROVIDER_ID)
        },
        conformanceTargetTip)
}

// remove
let removeConformanceTargetTip = function (pid) {
    resetStatus("conformance-target-tip-message")

    getCheckedIds("remove-conformance-target-tip", function (list) {
        update(CONFORMANCE_TARGET_TIP_DELETE,
            function () {
                listConformanceTargetTip(pid)
            },
            {ids: list, pid: pid}
        )
    })
}

// update
let updateConformanceTargetTip = function (identifier, pid) {
    resetStatus("conformance-target-tip-message")

    let checkConformanceTargetTip = function (identifier) {
        if (identifier == null || identifier.length === 0) {
            setDangerStatus("Identifier cannot be blank.", "conformance-target-tip-message")
            document.getElementById("conformanceTargetTipIdentifier").focus()
            return false
        }

        return true
    }

    if (checkConformanceTargetTip(identifier)) {
        add(CONFORMANCE_TARGET_TIP_ADD,
            function (data) {
                if (!data.status) {
                    data.status = {SUCCESS: "Successfully saved conformance target TIP identifier."}
                }
                setStatusMessage("conformance-target-tip-message", data)
                listConformanceTargetTip(pid)
            },
            {
                identifier: identifier,
                pId: pid
            })
        hideIt("conformance-target-tip-form")
    }
}
