// list
let listDocument = function () {
    list(DOCUMENT_LIST,
        function (docList) {
            renderDocumentTable(
                "document-table",
                {
                    editable: docList.editable,
                    binaryUrl: docList.binaryUrl,
                    fnAdd: function () {
                        addDocument({
                            id: 0,
                            binaryUrl: docList.binaryUrl
                        })
                    },
                    fnRemove: removeDocument,
                    fnDraw: drawDocumentTr,
                    hRef: "javascript:getDocument"
                },
                docList,
                0)
        },
        {id: 0})
}

// render offset
let renderDocumentOffset = function () {
}

// render table
let renderDocumentTable = function (tableId, tableMetadata, tableData, offset) {
    renderTable(
        tableId,
        tableMetadata,
        tableData.records,
        offset,
        "renderDocumentOffset",
        ["Document Name", "URL", "Description", "Public"],
        "documents")
}

// draw tr
let drawDocumentTr = function (tableMetadata, rowData) {
    return drawTr(
        tableMetadata,
        rowData,
        "edit-document",
        "remove-document",
        [
            rowData.filename,
            `<a href="${rowData.url}"><span>${rowData.url}</span></a>`,
            rowData.description,
            rowData.publicDocument ? `<span class="bi bi-check-lg"></span>` : `<span class="bi bi-x-lg"></span>`
        ],
        {binaryUrl: tableMetadata.binaryUrl})
}

// render form
let renderDocumentForm = function (target, preFn, fn, doc) {
    let html = ``
    html += renderFileHelper("Document File")
    html += renderInputHelper("filename", true, "File Name", "Enter File Name")
    html += renderTextareaHelper("description", true, "Description", "Enter Document Description")
    html += renderRadioHelper("publicDocument", false, "Public Document")

    renderDialogForm(target, decorateForm("Document", "documentFormId", html, "documentOk", doc.id === 0 ? "Add" : "Save", undefined, 'false'))

    document.getElementById("documentFormId").addEventListener("click", () => hideIt(target))
    document.getElementById("documentOk").addEventListener("click", fn)

    preFn(doc)
}

// populate form
let populateDocumentForm = function (doc) {
    if (doc.id !== 0) {
        document.getElementById("filename").value = doc.filename
        document.getElementById("description").value = doc.description
        document.getElementById("publicDocument").checked = doc.publicDocument
    }
    document.getElementById("filename").focus()

    createPlupload1(doc.binaryUrl)
}

// get details
let getDocument = function (id) {
    get(DOCUMENT_GET, addDocument, {
        id: id,
        binaryUrl: document.getElementById("edit-document").getAttribute("binaryUrl")
    })
}

// add
let addDocument = function (doc) {
    resetStatus("document-message")

    renderDocumentForm(
        "document-form",
        populateDocumentForm,
        function () {
            updateDocument(
                doc.id,
                document.getElementById("filename").value,
                document.getElementById("description").value,
                document.getElementById("binaryId1").value,
                document.getElementById("publicDocument").checked
            )
        },
        doc)
}

// remove
let removeDocument = function () {
    resetStatus("document-message")

    if (confirm("The selected documents(s) may be in use by current systems. Do you wish to proceed?")) {
        getCheckedIds("remove-document", function (list) {
            update(DOCUMENT_DELETE,
                listDocument,
                {ids: list}
            )
        })
    }
}

// update
let updateDocument = function (id, filename, description, binaryId, publicDocument) {
    resetStatus("document-message")

    let checkDocument = function (id, filename, description) {
        if (filename == null || filename.length === 0) {
            setDangerStatus("Filename cannot be blank.", "document-message")
            document.getElementById("filename").focus()
            return false
        }
        if (description == null || description.length === 0) {
            setDangerStatus("Description cannot be blank.", "document-message")
            document.getElementById("description").focus()
            return false
        }
        return true
    }

    let clearDocumentForm = function () {
        setSuccessStatus("Successfully saved document.", "document-message")
        hideIt("document-form")
    }

    if (checkDocument(id, filename, description)) {
        if (id === 0) {
            add(
                DOCUMENT_ADD,
                listDocument,
                {
                    filename: filename,
                    description: description,
                    binaryId: binaryId,
                    publicDocument: publicDocument
                })
        } else {
            update(
                DOCUMENT_UPDATE,
                listDocument,
                {
                    id: id,
                    filename: filename,
                    description: description,
                    binaryId: binaryId,
                    publicDocument: publicDocument
                })
        }
        clearDocumentForm()
    }
}

let setUploadedFilename = function (up, files) {
    var firstFilename = files[0].name
    $("#filename").val(firstFilename)
}

let PLUPLOAD1
let LAST_BINARY_ID_1 = -1

let createPlupload1 = function (binaryUrl) {
    var localUrl = binaryUrl

    var uploader = new plupload.Uploader({
        browse_button: "fileUploadButton1",
        multi_selection: false,
        chunk_size: "100kb",
        max_retries: 0,
        url: localUrl,
        flash_swf_url: MOXIE_SWF,
        // Flash settings
        silverlight_xap_url: MOXIE_XAP,
        multipart_params: {
            format: "json",
            context: CONTEXT
        }
    })

    PLUPLOAD1 = uploader
    uploader.init()
    uploader.bind("FilesAdded", handleFilesAdded1)
    uploader.bind("UploadProgress", handleUploadProgress1)
    uploader.bind("Error", handleUploadError1)
    uploader.bind("FileUploaded", handleFileUploaded1)
    uploader.bind("UploadComplete", handleUploadComplete1)
}

function handleUploadComplete1(up) {
    $("#binaryId1").val(LAST_BINARY_ID_1)
}

function handleFileUploaded1(up, file, response) {
    var responseData = jQuery.parseJSON(response.response)
    $("#fileUpload").removeClass("in-progress")
    LAST_BINARY_ID_1 = responseData.binaryId
}

function handleFilesAdded1(up, files) {
    $("#binaryId1").val(-1)
    $("#fileName1").html(files[0].name)
    $("#fileUpload").addClass("in-progress")
    LAST_BINARY_ID_1 = -1

    setUploadedFilename(up, files)

    PLUPLOAD1.start()
}

function handleUploadProgress1(up, file) {
    $("#fileUploadProgressBar1").width(`${file.percent}%`)
    $("#fileUploadProgressBar1").attr("aria-valuenow", file.percent)
    $("#fileUploadProgressBar1").html(`${file.percent}%`)
}

function handleUploadError1(up, err) {
    var msg = `Error Uploading[${err.code}]: ${err.message}`
    if (err.response) {
        try {
            var jsonText = err.response
            msg = jsonText
        } catch (e) {
        }
    }
    $("#fileUploadProgressBar1").html(msg)
}
