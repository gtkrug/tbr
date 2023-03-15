// list
let listTag = function (pid) {
    list(TAG_LIST,
        function (tagList) {
            renderTagTable(
                "tag-table",
                {
                    editable: tagList.editable,
                    fnAdd: function () {
                        addTag({id: 0})
                    },
                    fnRemove: function () {
                        removeTag(PROVIDER_ID)
                    },
                    fnDraw: drawTagTr
                },
                tagList,
                0)
        },
        {id: pid})
}

// render offset
let renderTagOffset = function () {
}

// render table
let renderTagTable = function (tableId, tableMetadata, tableData, offset) {
    renderTable(
        tableId,
        tableMetadata,
        tableData.records,
        offset,
        "renderTagOffset",
        ["Keyword Tag"],
        "keyword tags")
}

// draw tr
let drawTagTr = function (tableMetadata, rowData) {
    return drawTrWithoutEdit(
        tableMetadata,
        {
            id: rowData
        },
        "edit-tag",
        "edit-tags",
        [
            rowData
        ],
        {})
}

// render form
let renderTagForm = function (target, preFn, fn, tag) {
    let html = ``
    html += renderInputHelper("tagName", true, "Name", "Enter Tag Name")

    renderDialogForm(target, decorateForm("Keyword Tag", "tagFormId", html, "tagOk", tag.id === 0 ? "Add" : "Save", undefined, 'false'))

    document.getElementById("tagFormId").addEventListener("click", () => hideIt(target))
    document.getElementById("tagOk").addEventListener("click", fn)

    preFn(tag)
}

// populate form
let populateTagForm = function (tag) {
    if (tag.id !== 0) {
        document.getElementById("tagName").value = tag.tagName
    }
    document.getElementById("tagName").focus()
}

// add
let addTag = function (tag) {
    resetStatus("tag-message")

    renderTagForm(
        "tag-form",
        populateTagForm,
        function () {
            updateTag(
                document.getElementById("tagName").value,
                PROVIDER_ID)
        },
        tag)
}

// remove
let removeTag = function (pid) {
    resetStatus("tag-message")

    getCheckedIds("edit-tags", function (list) {
        update(TAG_DELETE,
            function () {
                listTag(pid)
            },
            {ids: list, pid: pid}
        )
    })
}

// update
let updateTag = function (tag, pid) {
    resetStatus("tag-message")

    let checkTag = function (tag) {
        if (tag == null || tag.length === 0) {
            setDangerStatus("Tag cannot be blank.", "tag-message")
            document.getElementById("tagName").focus()
            return false
        }

        return true
    }

    if (checkTag(tag)) {
        add(TAG_ADD,
            function (data) {
                if (!data.status) {
                    data.status = {SUCCESS: "Successfully saved keyword tag."}
                }
                setStatusMessage("tag-message", data)
                listTag(pid)
            },
            {
                pId: pid,
                tag: tag
            })
        hideIt("tag-form")
    }
}
