let TAG_TABLE_ITEMS_PER_PAGE = TABLE_INLINE_ITEMS_PER_PAGE;

// list
let listTag = function (pid) {
    list(TAG_LIST,
        tagResults(),
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
        TAG_TABLE_ITEMS_PER_PAGE,
        offset,
        "renderTagOffset",
        ["Keyword Tag"],
        "keyword tags")

    tagItemsPerPageTableEventHandler(tableId, renderTagOffset);
}

let tagItemsPerPageTableEventHandler = function (tableId, func) {

    $(`#items-per-page-${tableId}`).on('change', function() {
        const ipp = parseInt(document.getElementById(`items-per-page-${tableId}`).value);

        TAG_TABLE_ITEMS_PER_PAGE = ipp

        func(0);
    });
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

let curriedTag = curryFour(renderTagTable);

let tagResults = function () {
    return function (results) {
        renderTagOffset = curriedTag('tag-table')
        ({
            editable: results.editable,
            fnAdd: function () {
                addTag({id: 0})
            },
            fnRemove: function () {
                removeTag(PROVIDER_ID)
            },
            fnDraw: drawTagTr
        })
        (results);
        renderTagOffset(0);
    }
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
