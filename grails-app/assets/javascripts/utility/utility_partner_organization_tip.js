// list
let listPartnerOrganizationTip = function (oid) {
    list(ORGANIZATION_PARTNER_SYSTEMS_TIPS,
        function (organizationList) {
            renderPartnerOrganizationTipTable(
                "partner-organization-tip-table",
                {
                    editable: organizationList.editable,
                    fnAdd: function () {
                        addPartnerOrganizationTip({id: 0})
                    },
                    fnRemove: function () {
                        removePartnerOrganizationTip(ORGANIZATION_ID)
                    },
                    fnDraw: drawPartnerOrganizationTr,
                    titleTooltip: "This list of trust interoperability profiles (TIPs) represents the requirements of this organization for potential partner organizations that will engage in trusted information exchanges."
                },
                organizationList,
                0)
        },
        {oid: oid})
}

// render offset
let renderPartnerOrganizationTipOffset = function () {
}

// render table
let renderPartnerOrganizationTipTable = function (tableId, tableMetadata, tableData, offset) {
    renderTable(
        tableId,
        tableMetadata,
        tableData.records,
        offset,
        "renderPartnerOrganizationTipOffset",
        ["Partner Organization TIP Identifier"],
        "Partner Organization TIP Identifiers")
}

// draw tr
let drawPartnerOrganizationTr = function (tableMetadata, rowData) {
    return drawTrWithoutEdit(
        tableMetadata,
        rowData,
        "edit-partner-organization-tip",
        "remove-partner-organization-tip",
        [
            `<a href="${rowData.partnerSystemsTipIdentifier}" target="_blank">${rowData.name}</a>`
        ],
        {})
}

// render form
let renderPartnerOrganizationTipForm = function (target, preFn, fn, partnerOrganizationTip) {
    let html = ``
    html += renderInputHelper("partnerSystemsTipIdentifier", true, "Identifier", "Enter Partner Organization TIP Identifier")

    renderDialogForm(target, decorateForm("Partner Organization TIP Identifier", "partnerOrganizationTipFormId", html, "partnerOrganizationTipOk", partnerOrganizationTip.id === 0 ? "Add" : "Save"))

    document.getElementById("partnerOrganizationTipFormId").addEventListener("click", () => hideIt(target))
    document.getElementById("partnerOrganizationTipOk").addEventListener("click", fn)

    preFn(partnerOrganizationTip)
}

// populate form
let populatePartnerOrganizationTipForm = function (partnerOrganizationTip) {
    if (partnerOrganizationTip.id !== 0) {
        document.getElementById("partnerSystemsTipIdentifier").value = partnerOrganizationTip.partnerSystemsTipIdentifier
    }
    document.getElementById("partnerSystemsTipIdentifier").focus()
}

// add
let addPartnerOrganizationTip = function (partnerOrganizationTip) {
    resetStatus("partner-organization-tip-message")

    renderPartnerOrganizationTipForm(
        "partner-organization-tip-form",
        populatePartnerOrganizationTipForm,
        function () {
            updatePartnerOrganizationTip(
                document.getElementById("partnerSystemsTipIdentifier").value,
                ORGANIZATION_ID)
        },
        partnerOrganizationTip)
}

// remove
let removePartnerOrganizationTip = function (oid) {
    resetStatus("partner-organization-tip-message")

    getCheckedIds("remove-partner-organization-tip", function (list) {
        update(ORGANIZATION_DELETE_PARTNER_SYSTEMS_TIPS,
            function () {
                listPartnerOrganizationTip(oid)
            },
            {ids: list, oid: oid}
        )
    })
}

// update
let updatePartnerOrganizationTip = function (identifier, oid) {
    resetStatus("partner-organization-tip-message")

    let checkPartnerOrganizationTip = function (trustmarkRecipientIdentifier) {
        if (trustmarkRecipientIdentifier == null || trustmarkRecipientIdentifier.length === 0) {
            setDangerStatus("Identifier cannot be blank.", "partner-organization-tip-message")
            document.getElementById("partnerSystemsTipIdentifier").focus()
            return false
        }

        return true
    }

    if (checkPartnerOrganizationTip(identifier)) {
        add(ORGANIZATION_ADD_PARTNER_SYSTEMS_TIP,
            function (data) {
                if (!data.status) {
                    data.status = {SUCCESS: "Successfully saved partner organization TIP identifier."}
                }
                setStatusMessage("partner-organization-tip-message", data)
                listPartnerOrganizationTip(oid)
            },
            {
                identifier: identifier,
                oid: oid
            })
        hideIt("partner-organization-tip-form")
    }
}

// other
