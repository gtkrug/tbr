// list
let listTrustmarkRecipientIdentifier = function (pid) {
    list(PROVIDER_TRUSTMARK_RECIPIENT_IDENTIFIERS,
        function (trustmarkRecipientIdentifierList) {
            renderTrustmarkRecipientIdentifierTable(
                "trustmark-recipient-identifier-table",
                {
                    editable: trustmarkRecipientIdentifierList.editable,
                    fnAdd: function () {
                        addTrustmarkRecipientIdentifierForAdd({id: 0})
                    },
                    fnRemove: function () {
                        removeTrustmarkRecipientIdentifier(PROVIDER_ID)
                    },
                    fnDraw: drawTrustmarkRecipientIdentifierTr,
                    hRef: "javascript:getTrustmarkRecipientIdentifier"
                },
                trustmarkRecipientIdentifierList,
                0)
        },
        {pid: pid})
}

// render offset
let renderTrustmarkRecipientIdentifierOffset = function () {
}

// render table
let renderTrustmarkRecipientIdentifierTable = function (tableId, tableMetadata, tableData, offset) {
    renderTable(
        tableId,
        tableMetadata,
        tableData.records,
        offset,
        "renderTrustmarkRecipientIdentifiersOffset",
        ["Trustmark Recipient Identifier"],
        "Trustmark Recipient Identifiers")
}

// draw tr
let drawTrustmarkRecipientIdentifierTr = function (obj, entry) {
    return drawTr(
        obj,
        entry,
        "edit-trustmark-recipient-identifier",
        "remove-trustmark-recipient-identifier",
        [
            entry.trustmarkRecipientIdentifierUrl
        ],
        {})
}

// render form
let renderTrustmarkRecipientIdentifierForm = function (target, preFn, fn, trustmarkRecipientIdentifier) {
    let html = ``
    html += renderInputHelper("trustmarkRecipientIdentifier", true, "Identifier", "Enter the Trustmark Recipient Identifier URI")

    renderDialogForm(target, decorateForm("Trustmark Recipient Identifier", "trustmarkRecipientIdentifierFormId", html, "trustmarkRecipientIdentifierOk", trustmarkRecipientIdentifier.id === 0 ? "Add" : "Save", undefined, 'false'))

    document.getElementById("trustmarkRecipientIdentifierFormId").addEventListener("click", () => hideIt(target))
    document.getElementById("trustmarkRecipientIdentifierOk").addEventListener("click", fn)

    preFn(trustmarkRecipientIdentifier)
}

// populate form
let populateTrustmarkRecipientIdentifierForm = function (trustmarkRecipientIdentifier) {
    if (trustmarkRecipientIdentifier.id !== 0) {
        document.getElementById("trustmarkRecipientIdentifier").value = trustmarkRecipientIdentifier.trustmarkRecipientIdentifierUrl
    }
    document.getElementById("trustmarkRecipientIdentifier").focus()
}

// get details
let getTrustmarkRecipientIdentifier = function (id) {
    resetStatus("trustmark-recipient-identifier-message")

    get(PROVIDER_GET_TRUSTMARK_RECIPIENT_IDENTIFIER, addTrustmarkRecipientIdentifierForUpdate, {
        pid: PROVIDER_ID,
        rid: id
    })
}

// add
let addTrustmarkRecipientIdentifierForAdd = function (trustmarkRecipientIdentifier) {
    resetStatus("trustmark-recipient-identifier-message")

    renderTrustmarkRecipientIdentifierForm(
        "trustmark-recipient-identifier-form",
        populateTrustmarkRecipientIdentifierForm,
        function () {
            addTrustmarkRecipientIdentifier(
                document.getElementById("trustmarkRecipientIdentifier").value)
        },
        trustmarkRecipientIdentifier)
}

let addTrustmarkRecipientIdentifierForUpdate = function (trustmarkRecipientIdentifier) {
    resetStatus("trustmark-recipient-identifier-message")

    renderTrustmarkRecipientIdentifierForm(
        "trustmark-recipient-identifier-form",
        populateTrustmarkRecipientIdentifierForm,
        function () {
            updateTrustmarkRecipientIdentifier(
                trustmarkRecipientIdentifier.id,
                document.getElementById("trustmarkRecipientIdentifier").value,
                PROVIDER_ID)
        },
        trustmarkRecipientIdentifier)
}

// remove
let removeTrustmarkRecipientIdentifier = function (pid) {
    resetStatus("trustmark-recipient-identifier-message")

    getCheckedIds("remove-trustmark-recipient-identifier", function (list) {
        update(PROVIDER_DELETE_TRUSTMARK_RECIPIENT_IDENTIFIERS,
            function () {
                listTrustmarkRecipientIdentifier(pid)
            },
            {ids: list, pid: pid}
        )
    })
}

// update
let checkTrustmarkRecipientIdentifier = function (trustmarkRecipientIdentifier) {
    if (trustmarkRecipientIdentifier == null || trustmarkRecipientIdentifier.length === 0) {
        setDangerStatus("Trustmark recipient identifier cannot be blank.", "trustmark-recipient-identifier-message")
        document.getElementById("trustmarkRecipientIdentifier").focus()
        return false
    }

    return true
}

let updateTrustmarkRecipientIdentifier = function (id, trustmarkRecipientIdentifier, pid) {
    resetStatus("trustmark-recipient-identifier-message")

    if (checkTrustmarkRecipientIdentifier(trustmarkRecipientIdentifier)) {
        update(PROVIDER_UPDATE_TRUSTMARK_RECIPIENT_IDENTIFIER,
            function (data) {
                if (!data.status) {
                    data.status = {SUCCESS: "Successfully saved trustmark recipient identifier."}
                }
                setStatusMessage("trustmark-recipient-identifier-message", data)
                listTrustmarkRecipientIdentifier(PROVIDER_ID)
            },
            {
                id: id,
                trustmarkRecipientIdentifier: trustmarkRecipientIdentifier,
                providerId: PROVIDER_ID
            })
        hideIt("trustmark-recipient-identifier-form")
    }
}

let addTrustmarkRecipientIdentifier = function (trustmarkRecipientIdentifier) {
    resetStatus("trustmark-recipient-identifier-message")

    if (checkTrustmarkRecipientIdentifier(trustmarkRecipientIdentifier)) {
        add(PROVIDER_ADD_TRUSTMARK_RECIPIENT_IDENTIFIER,
            function (data) {
                if (!data.status) {
                    data.status = {SUCCESS: "Successfully saved trustmark recipient identifier."}
                }
                setStatusMessage("trustmark-recipient-identifier-message", data)
                listTrustmarkRecipientIdentifier(PROVIDER_ID)
            },
            {
                pid: PROVIDER_ID,
                identifier: trustmarkRecipientIdentifier
            }
        )
        hideIt("trustmark-recipient-identifier-form")
    }
}
