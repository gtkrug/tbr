// list
let listProvider = function (oid) {
    list(PROVIDER_LIST,
        providerResults(),
        {orgid: oid})
}

// render offset
let renderProviderOffset = function () {
}

// render table
let renderProviderTable = function (tableId, tabeMetadata, tableData, offset) {
    tabeMetadata.baseHref = tableData.providerBaseUrl
    renderTable(
        tableId,
        tabeMetadata,
        tableData.records,
        offset,
        "renderProviderOffset",
        ["Name", "Type"],
        "provider systems")
}

// draw tr
let drawProviderTr = function (tableMetadata, rowData) {
    return drawTrWithoutEdit(
        tableMetadata,
        rowData,
        "edit-provider",
        "remove-provider",
        [
            `<a href="${tableMetadata.baseHref}/${rowData.id}">${rowData.name}</a>`,
            rowData.providerType
        ],
        {})
}

let curriedProvider = curryFour(renderProviderTable);

let providerResults = function () {
    return function (results) {
        renderProviderOffset = curriedProvider('provider-table')
        ({
            editable: results.editable,
            fnAdd: function () {
                addProvider({id: 0})
            },
            fnRemove: removeProvider,
            fnDraw: drawProviderTr,
            hRef: "javascript:getProvider"
        })
        (results);
        renderProviderOffset(0);
    }
}

// render form
let renderProviderForm = function (target, preFn, fn, provider) {
    let html = ``
    html += renderSelectHelper(true, "Types", "select-provider-types")
    html += renderInputHelper("providerName", true, "System Name", "Enter System Name")

    renderDialogForm(target, decorateForm("System", "providerFormId", html, "providerOk", provider.id === 0 ? "Add" : "Save", undefined, 'false'))

    document.getElementById("providerFormId").addEventListener("click", () => hideIt(target))
    document.getElementById("providerOk").addEventListener("click", fn)

    preFn(provider)
}

// populate form
let populateProviderForm = function (provider) {
    selectProviderTypes()
    document.getElementById("providerName").focus()
}

// get details

// add
let addProvider = function (provider) {
    resetStatus("provider-message")

    renderProviderForm(
        "provider-form",
        populateProviderForm,
        function () {
            updateProvider(
                document.getElementById("providerName").value,
                document.getElementById("pType").options[document.getElementById("pType").selectedIndex].value)
        },
        provider)
}

// remove
let removeProvider = function () {
    resetStatus("provider-message")

    if (confirm("This operation will delete all data associated with the system. Do you want to continue?")) {
        getCheckedIds("remove-provider", function (list) {
            update(PROVIDER_DELETE,
                function () {
                    listProvider(ORGANIZATION_ID)
                },
                {ids: list, oid: ORGANIZATION_ID}
            )
        })
    }
}

// update
let updateProvider = function (name, type) {
    resetStatus("provider-message")

    add(PROVIDER_ADD,
        function (data) {
            if (!data.status) {
                data.status = {SUCCESS: "Successfully saved system provider."}
            }
            setStatusMessage("provider-message", data)
            listProvider(ORGANIZATION_ID)
        },
        {
            orgid: ORGANIZATION_ID,
            type: type,
            name: name,
            entity: ""
        }
    )
    hideIt("provider-form")
}

// other
let renderProviderTypes = function (selectParentId, optionArray) {
    renderSelect(selectParentId, "pType", "-- Select a System Type --", optionArray.map(option => {
        return {
            value: option,
            text: option
        }
    }), 0)
}

let curriedProviderTypes = curryTwo(renderProviderTypes)

let selectProviderTypes = function () {
    list(PROVIDER_TYPES, curriedProviderTypes("select-provider-types"), {name: "ALL"})
}
