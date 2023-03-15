// list
let listAttribute = function (pid) {
    list(ATTRIBUTE_LIST,
        function (attributeList) {
            renderAttributeTable(
                "attribute-table",
                {
                    editable: attributeList.editable,
                    fnAdd: function () {
                        addAttribute({id: 0})
                    },
                    fnRemove: function () {
                        removeAttribute(PROVIDER_ID)
                    },
                    fnDraw: drawAttributeTr
                },
                attributeList,
                0)
        },
        {id: pid})
}

// render offset
let renderAttributeOffset = function () {
}

// render table
let renderAttributeTable = function (tableId, tableMetadata, tableData, offset) {
    renderTable(
        tableId,
        tableMetadata,
        tableData.records,
        offset,
        "renderAttributeOffset",
        ["Name", "Value"],
        "attributes")
}

// draw tr
let drawAttributeTr = function (tableMetadata, rowData) {
    return drawTrWithoutEdit(
        tableMetadata,
        rowData,
        "edit-attribute",
        "remove-attribute",
        [
            rowData.name,
            rowData.value
        ],
        {})
}

// render form
let renderAttributeForm = function (target, preFn, fn, attribute) {
    let html = ``
    html += renderInputHelper("attrName", true, "Name", "Enter Attribute Name")
    html += renderInputHelper("attrValue", true, "Value", "Enter Attribute Value")

    renderDialogForm(target, decorateForm("System", "attributeFormId", html, "attributeOk", attribute.id === 0 ? "Add" : "Save", undefined, 'false'))

    document.getElementById("attributeFormId").addEventListener("click", () => hideIt(target))
    document.getElementById("attributeOk").addEventListener("click", fn)

    preFn(attribute)
}

// populate form
let populateAttributeForm = function (attribute) {
    if (attribute.id !== 0) {
        document.getElementById("attrName").value = attribute.name
        document.getElementById("attrValue").value = attribute.value
    }
    document.getElementById("attrName").focus()
}

// add
let addAttribute = function (attribute) {
    resetStatus("attribute-message")
    
    renderAttributeForm(
        "attribute-form",
        populateAttributeForm,
        function () {
            updateAttribute(
                document.getElementById("attrName").value,
                document.getElementById("attrValue").value,
                PROVIDER_ID)
        },
        attribute)
}


// remove
let removeAttribute = function (pid) {
    resetStatus("attribute-message")
    
    getCheckedIds("remove-attribute", function (list) {
        update(ATTRIBUTE_DELETE,
            function () {
                listAttribute(pid)
            },
            {ids: list, pid: pid}
        )
    })
}

// update
let updateAttribute = function (name, value, pid) {
    resetStatus("attribute-message")
    
    let checkAttribute = function (name, value) {
        if (name == null || name.length === 0) {
            setDangerStatus("Name cannot be blank.", "attribute-message")
            document.getElementById("attrName").focus()
            return false
        }
        if (value == null || value.length === 0) {
            setDangerStatus("Value cannot be blank.", "attribute-message")
            document.getElementById("attrValue").focus()
            return false
        }

        return true
    }

    if (checkAttribute(name, value)) {
        add(ATTRIBUTE_ADD,
            function (data) {
                if (!data.status) {
                    data.status = {SUCCESS: "Successfully saved system attribute."}
                }
                setStatusMessage("attribute-message", data)
                listAttribute(pid)
            },
            {
                name: name,
                value: value,
                pId: pid
            })
        hideIt("attribute-form")
    }
}
