let REPO_URL_TABLE_ITEMS_PER_PAGE = TABLE_INLINE_ITEMS_PER_PAGE;

// list
let listAssessmentToolUrl = function (oid) {
    list(ORGANIZATION_REPOS,
        assessmentToolUrlResults(),
        {oid: oid})
}

// render offset
let renderAssessmentToolUrlOffset = function () {
}

// render table
let renderAssessmentToolUrlTable = function (tableId, tableMetadata, tableData, offset) {
    renderTable(
        tableId,
        tableMetadata,
        tableData.records,
        REPO_URL_TABLE_ITEMS_PER_PAGE,
        offset,
        "renderAssessmentToolUrlOffset",
        ["Assessment Tool URL"],
        "Assessment Tool URLs")

    repoUrlItemsPerPageTableEventHandler(tableId, renderAssessmentToolUrlOffset);
}

let repoUrlItemsPerPageTableEventHandler = function (tableId, func) {

    $(`#items-per-page-${tableId}`).on('change', function() {
        const ipp = parseInt(document.getElementById(`items-per-page-${tableId}`).value);

        REPO_URL_TABLE_ITEMS_PER_PAGE = ipp

        func(0);
    });
}

// draw tr
let drawAssessmentToolUrlTr = function (tableMetadata, rowData) {
    return drawTr(
        tableMetadata,
        rowData,
        "edit-assessment-tool-url",
        "remove-assessment-tool-url",
        [
            rowData.repoUrl
        ],
        {})
}

let curriedAssessmentToolUrl = curryFour(renderAssessmentToolUrlTable);

let assessmentToolUrlResults = function () {
    return function (results) {
        renderAssessmentToolUrlOffset = curriedAssessmentToolUrl('assessment-tool-url-table')
        ({
            editable: results.editable,
            fnAdd: function () {
                addAssessmentToolUrlForAdd({id: 0})
            },
            fnRemove: function () {
                removeAssessmentToolUrl(ORGANIZATION_ID)
            },
            fnDraw: drawAssessmentToolUrlTr,
            hRef: "javascript:getAssessmentToolUrl"
        })
        (results);
        renderAssessmentToolUrlOffset(0);
    }
}

// render form
let renderAssessmentToolUrlForm = function (target, preFn, fn, repo) {
    let html = ``
    html += renderInputHelper("assessmentToolUrlRepo", true, "URL", "Enter the Assessment Tool URL")

    renderDialogForm(target, decorateForm("Assessment Tool URL", "repoFormId", html, "repoOk", repo.id === 0 ? "Add" : "Save", undefined, 'false'))

    document.getElementById("repoFormId").addEventListener("click", () => hideIt(target))
    document.getElementById("repoOk").addEventListener("click", fn)

    preFn(repo)
}

// populate form
let populateAssessmentToolUrlForm = function (repo) {
    if (repo.id !== 0) {
        document.getElementById("assessmentToolUrlRepo").value = repo.repoUrl
    }
    document.getElementById("assessmentToolUrlRepo").focus()
}

// get details
let getAssessmentToolUrl = function (id) {
    resetStatus("assessment-tool-url-message")

    get(ORGANIZATION_GET_REPO, addAssessmentToolUrlForUpdate, {orgid: ORGANIZATION_ID, rid: id})
}

// add
let addAssessmentToolUrlForAdd = function (repo) {
    resetStatus("assessment-tool-url-message")

    renderAssessmentToolUrlForm(
        "assessment-tool-url-form",
        populateAssessmentToolUrlForm,
        function () {
            addAssessmentToolUrl(
                document.getElementById("assessmentToolUrlRepo").value)
        },
        repo)
}

let addAssessmentToolUrlForUpdate = function (repo) {
    resetStatus("assessment-tool-url-message")

    renderAssessmentToolUrlForm(
        "assessment-tool-url-form",
        populateAssessmentToolUrlForm,
        function () {
            updateAssessmentToolUrl(
                repo.id,
                document.getElementById("assessmentToolUrlRepo").value,
                ORGANIZATION_ID)
        },
        repo)
}

// remove
let removeAssessmentToolUrl = function (oid) {
    resetStatus("assessment-tool-url-message")

    getCheckedIds("remove-assessment-tool-url", function (list) {
        update(ORGANIZATION_DELETE_REPOS,
            function () {
                listAssessmentToolUrl(oid)
            },
            {ids: list, orgid: oid}
        )
    })
}

// update
let checkAssessmentToolUrl = function (repo) {
    if (repo == null || repo.length === 0) {
        setDangerStatus("URL cannot be blank", "assessment-tool-url-message")
        document.getElementById("assessmentToolUrlRepo").focus()
        return false
    }

    return true
}

let updateAssessmentToolUrl = function (id, repoUrl, orgId) {
    resetStatus("assessment-tool-url-message")

    if (checkAssessmentToolUrl(repoUrl)) {
        update(ORGANIZATION_UPDATE_REPO,
            function (data) {
                if (!data.status) {
                    data.status = {SUCCESS: "Successfully saved URL."}
                }
                setStatusMessage("assessment-tool-url-message", data)
                listAssessmentToolUrl(ORGANIZATION_ID)
            },
            {
                id: id,
                repoUrl: repoUrl,
                organizationId: ORGANIZATION_ID
            })
        hideIt("assessment-tool-url-form")
    }
}

let addAssessmentToolUrl = function (repoUrl) {
    resetStatus("assessment-tool-url-message")

    if (checkAssessmentToolUrl(repoUrl)) {
        add(ORGANIZATION_ADD_REPO,
            function (data) {
                if (!data.status) {
                    data.status = {SUCCESS: "Successfully saved URL."}
                }
                setStatusMessage("assessment-tool-url-message", data)
                listAssessmentToolUrl(ORGANIZATION_ID)
            },
            {
                orgid: ORGANIZATION_ID,
                name: repoUrl
            }
        )
        hideIt("assessment-tool-url-form")
    }
}
