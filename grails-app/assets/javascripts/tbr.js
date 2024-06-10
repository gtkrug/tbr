let renderPagination = function (tableId, max, offset, totalCount, fnName, numOfColumns) {
    if (totalCount > max) {
        return buildPagination(tableId, offset, max, totalCount, fnName, numOfColumns, true)
    }

    return ""
}

let renderTable = function (tableId, tableMetadata, tableData, max, offset, fnName, columnNameArray, entityName) {
    numOfColumns = columnNameArray.length
    if (LOGGED_IN && tableMetadata.editable) {
        numOfColumns += 2
    }

    if (tableMetadata.includeOrganizationColumn) {
        numOfColumns++
    }

    let html = renderPagination(tableId, max, offset, tableData.length, fnName, numOfColumns)

    html += `<thead>`
    html += `<tr>`
    html += LOGGED_IN && tableMetadata.editable ? `<th scope="col"><a href="javascript:" id="plus-${tableId}" class="bi-plus-lg"></a></th>` : ``
    html += LOGGED_IN && tableMetadata.editable ? `<th scope="col"><a href="javascript:" id="minus-${tableId}" class="bi-trash"></a></th>` : ``
    columnNameArray.forEach(columnName => {
        html += `<th scope="col" style="width: ${100 / (columnNameArray.length + (tableMetadata.includeOrganizationColumn ? 1 : 0))}%">${columnName}</th>`
    })

    html += tableMetadata.includeOrganizationColumn ? `<th scope="col" style="width: ${100 / (columnNameArray.length + (tableMetadata.includeOrganizationColumn ? 1 : 0))}%">Organization</th>` : ``
    html += `</tr>`
    html += `</thead>`
    html += `<tbody>`

    if (tableData.length === 0) {
        html += `<tr><td colspan="${columnNameArray.length + (LOGGED_IN && tableMetadata.editable ? 2 : 0) + (tableMetadata.includeOrganizationColumn ? 1 : 0)}">There are no ${entityName}.</td></tr>`
    } else {
        tableData.forEach((c, index) => {
            if (index >= offset && index < offset + max) {
                html += tableMetadata.fnDraw(tableMetadata, c)
            }
        })
    }

    html += `</tbody>`

    document.getElementById(tableId).innerHTML = html

    if (LOGGED_IN && tableMetadata.editable) {
        document.getElementById(`plus-${tableId}`).onclick = tableMetadata.fnAdd
        document.getElementById(`minus-${tableId}`).onclick = tableMetadata.fnRemove
    }

    if (document.getElementById(`items-per-page-${tableId}`) != null) {
        document.getElementById(`items-per-page-${tableId}`).value = max;
    }
}

let renderTableWithoutAddOrMinus = function (tableId, tableMetadata, tableData, max, offset, fnName, columnNameArray, entityName) {
    numOfColumns = columnNameArray.length
    if (LOGGED_IN && tableMetadata.editable) {
        numOfColumns += 2
    }

    if (tableMetadata.includeOrganizationColumn) {
        numOfColumns++
    }

    let html = renderPagination(tableId, max, offset, tableData.length, fnName, numOfColumns)

    html += `<thead>`

    html += LOGGED_IN && tableMetadata.editable ? `<th scope="col"><span class="bi-plus-lg" style="visibility: hidden"></span></th>` : ``

    html += ``
    columnNameArray.forEach(columnName => {
        html += `<th scope="col" style="width: ${100 / (columnNameArray.length + (tableMetadata.includeOrganizationColumn ? 1 : 0))}%">${columnName}</th>`
    })

    html += tableMetadata.includeOrganizationColumn ? `<th scope="col" style="width: ${100 / (columnNameArray.length + (tableMetadata.includeOrganizationColumn ? 1 : 0))}%">Organization</th>` : ``
    html += `</tr>`
    html += `</thead>`
    html += `<tbody>`

    if (tableData.length === 0) {
        html += `<tr><td colspan="${columnNameArray.length + (LOGGED_IN && tableMetadata.editable ? 2 : 0) + (tableMetadata.includeOrganizationColumn ? 1 : 0)}">There are no ${entityName}.</td></tr>`
    } else {
        tableData.forEach((c, index) => {
            if (index >= offset && index < offset + max) {
                html += tableMetadata.fnDraw(tableMetadata, c)
            }
        })
    }

    html += `</tbody>`

    document.getElementById(tableId).innerHTML = html

    if (document.getElementById(`items-per-page-${tableId}`) != null) {
        document.getElementById(`items-per-page-${tableId}`).value = max;
    }
}

let drawTr = function (tableMetadata, rowData, inputId, removeClassName, rowDataArray, attributeObject) {
    let html = `<tr>`

    html += LOGGED_IN && tableMetadata.editable ? `<td><a href="${tableMetadata.hRef === undefined ? "javascript:getDetails" : tableMetadata.hRef}(${rowData.id})"><span class="bi bi-pencil"></span></a></td>` : ``
    html += LOGGED_IN && tableMetadata.editable ? `<td><input id="${inputId}" class="${removeClassName} form-check-input" type="checkbox" value="${rowData.id}" ${Object.keys(attributeObject).map(attributeName => `${attributeName}="${attributeObject[attributeName]}"`).join(" ")}></td>` : ``

    rowDataArray.forEach(rowData => html += `<td>${rowData}</td>`)

    html += tableMetadata.includeOrganizationColumn ? `<td><a href="${ORGANIZATION_VIEW + rowData.organization.id}">${rowData.organization.name}</a></td>` : ``
    html += `</tr>`

    return html
}

let drawTrWithoutTrash = function (tableMetadata, rowData, inputId, removeClassName, rowDataArray, attributeObject) {
    let html = `<tr>`

    html += LOGGED_IN && tableMetadata.editable ? `<td><a href="${tableMetadata.hRef === undefined ? "javascript:getDetails" : tableMetadata.hRef}(${rowData.id})"><span class="bi bi-pencil"></span></a></td>` : ``

    rowDataArray.forEach(rowData => html += `<td>${rowData}</td>`)

    html += tableMetadata.includeOrganizationColumn ? `<td><a href="${ORGANIZATION_VIEW + rowData.organization.id}">${rowData.organization.name}</a></td>` : ``
    html += `</tr>`

    return html
}

let drawTrWithoutEdit = function (tableMetadata, rowData, inputId, removeClassName, rowDataArray, attributeObject) {
    let html = `<tr>`

    html += LOGGED_IN && tableMetadata.editable ? `<td></td>` : ``
    html += LOGGED_IN && tableMetadata.editable ? `<td><input id="${inputId}" class="${removeClassName} form-check-input" type="checkbox" value="${rowData.id}" ${Object.keys(attributeObject).map(attributeName => `${attributeName}="${attributeObject[attributeName]}"`).join(" ")} ${rowData.checked ? "checked" : ""}></td>` : ``

    rowDataArray.forEach(rowData => html += `<td>${rowData}</td>`)

    html += tableMetadata.includeOrganizationColumn ? `<td><a href="${ORGANIZATION_VIEW + rowData.organization.id}">${rowData.organization.name}</a></td>` : ``
    html += `</tr>`

    return html
}

/**
 * hide the passed in div
 * @param target
 */
let hideIt = function (target) {

    // make sure the target is visible since some elements might be
    // filtered out based on logged-in user and/or roles

    if (target.startsWith('#')) {
        if (document.getElementById(target.substring(1)) != null) {
            document.getElementById(target.substring(1)).style.display = 'none';
        }
    } else {
        if (document.getElementById(target) != null) {
            document.getElementById(target).style.display = 'none';
        }
    }
}

/**
 * hide the passed in div
 * @param target
 */
let showIt = function (target) {
    if (target.startsWith('#')) {
        document.getElementById(target.substring(1)).style.display = 'block';
    } else {
        document.getElementById(target).style.display = 'block';
    }
}

/**
 * hides or displays the target
 * @param target
 * @returns {boolean}
 */
let toggleIt = function (target) {
    if (target.startsWith('#')) {
        target = document.getElementById(target.substring(1));
    }
    if (document.getElementById(target).style.display === 'none') {
        document.getElementById(target).style.display = 'block';
    } else {
        document.getElementById(target).style.display = 'none';
    }
    return false;
}

let decorateForm = function (title, aId, cardBodyContent, buttonId, buttonText, hide, readOnly, status) {
    let html = ``

    html += `<div class="border rounded card">`

    html += `<div class="card-header fw-bold">`
    html += `<div class="row">`
    html += hide === undefined || hide ? `<div class="col-11">${title}</div>` : `<div class="col-12">${title}</div>`
    html += hide === undefined || hide ? `<div class="col-1 text-end"><a id="${aId}" class="btn btn-close p-0 align-middle"></a></div>` : ``
    html += `</div>`
    html += `</div>`

    html += `<div class="card-body">`
    html += cardBodyContent
    html += `</div>`

    if (readOnly !== undefined && readOnly === 'false') {
        if (buttonId !== undefined) {
            html += `<div class="card-footer text-start">`
            html += `<div class="row">`
            html += `<div class="col-3"></div>`
            html += `<div class="col-9"><button id="${buttonId}" type="button" class="btn btn-primary">${buttonText}</button>`

            // render status near button
            if (status !== undefined) {
                html += status
            }
            html += `</div>`
            html += `</div>`
            html += `</div>`
        }
    }

    html += `</div>`

    return html
}

let renderSelectHelper = function (labelRequired, labelContent, divId) {
    let html = ``

    html += `<div class="row pb-2">`
    html += `<label for="${divId}" class="col-3 col-form-label ${labelRequired ? "label-required" : ""}">${labelContent}</label>`
    html += `<div class="col-9" id="${divId}"></div>`
    html += `</div>`

    return html
}

let renderSelectHelperWithOptionList = function (labelRequired, labelContent, divId, optionList) {
    let html = ``

    html += `<div class="row pb-2">`
    html += `<label for="${divId}" class="col-3 col-form-label ${labelRequired ? "label-required" : ""}">${labelContent}</label>`
    html += `<div class="col-9">`
    html += `<select class="form-select" id="${divId}">`
    optionList.forEach(option => html += `<option value="${option}">${option}</option>`)
    html += `</select>`
    html += `</div>`
    html += `</div>`

    return html
}

let renderInputHelper = function (inputId, labelRequired, labelContent, placeholderText, inputValue, readonly) {
    let html = ``

    html += `<div class="row pb-2">`
    html += `<label for="${inputId}" class="col-3 col-form-label ${labelRequired ? "label-required" : ""}">${labelContent}</label>`
    html += `<div class="col-9"><input id="${inputId}" type="text" class="form-control" placeholder="${placeholderText}" value="${inputValue === undefined ? "" : inputValue}" ${readonly === 'true' ? "readonly" : ""}/></div>`
    html += `</div>`

    return html
}

let renderFileHelper = function (labelContent) {
    let html = ``

    html += `<input type="hidden" id="binaryId1" name="binaryObject" value="-1">`
    html += `<div class="row pb-2">`
    html += `<label for="fileUploadName" class="col-3 col-form-label">${labelContent}</label>`
    html += `<div class="col-9" id="fileUpload">`
    html += `<div class="input-group" id="fileUploadName">`
    html += `<a href="#" id="fileUploadButton1" class="btn btn-primary">Browse...</a>`
    html += `<span type="text" class="form-control" id="fileName1">No file selected.</span>`
    html += `</div>`
    html += `<div class="progress" id="fileUploadStatus1">`
    html += `<div class="progress-bar" id="fileUploadProgressBar1" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100"></div>`
    html += `</div>`
    html += `</div>`
    html += `</div>`

    return html
}

let renderTextareaHelper = function (inputId, labelRequired, labelContent, placeholderText) {
    let html = ``

    html += `<div class="row pb-2">`
    html += `<label for="${inputId}" class="col-3 col-form-label ${labelRequired ? "label-required" : ""}">${labelContent}</label>`
    html += `<div class="col-9"><textarea id="${inputId}" type="text" class="form-control" placeholder="${placeholderText}"></textarea></div>`
    html += `</div>`

    return html
}

let renderRadioHelper = function (inputId, labelRequired, labelContent) {
    let html = ``

    html += `<div class="row pb-2">`
    html += `<label for="${inputId}" class="col-3 col-form-label ${labelRequired ? "label-required" : ""}">${labelContent}</label>`
    html += `<div class="col-9 col-form-label"><div class="form-check form-switch"><input id="${inputId}" type="checkbox" class="form-check-input"/></div></div>`
    html += `</div>`

    return html
}

let renderTextHelper = function (inputId, labelRequired, labelContent, content) {
    let html = ``

    html += `<div class="row pb-2">`
    html += `<label for="${inputId}" class="col-3 col-form-label ${labelRequired ? "label-required" : ""}">${labelContent}</label>`
    html += `<div class="col-9">`
    html += `<div class="form-control" style="background-color: rgba(0, 0, 0, .03)">${content}</div>`
    html += `</div>`
    html += `</div>`

    return html
}

let renderHiddenHelper = function (inputId, inputValue) {
    let html = ``

    html += `<input id="${inputId}" type="hidden" value="${inputValue === undefined ? "" : inputValue}"/>`

    return html
}

let renderHeaderHelper = function (content) {
    let html = ``

    html += `</div>`
    html += `<div class="card-header fw-bold">`
    html += `<div class="row">`
    html += `<div class="col-12">${content}</div>`
    html += `</div>`
    html += `</div>`
    html += `<div class="card-body">`

    return html
}

/**
 * renders content into a standard dialog with a close X
 * @param target
 * @param content
 */
let renderDialogForm = function (target, content) {
    document.getElementById(target).innerHTML = content
    showIt(target)
}

/**
 * renders a status message into a container div that fades out after 3 seconds
 * @param target: container div for message
 * @param data: content of the message
 */
let setStatusMessage = function (target, data) {
    let html = "";

    if (!isEmtpy(data.status['SUCCESS'])) {
        html += `<div class="alert alert-primary" class="bi bi-check-circle">${data.status["SUCCESS"]}</div>`;
    }

    if (!isEmtpy(data.status['WARNING'])) {
        html += `<div class="alert alert-warning" class="bi bi-exclamation-triangle">${data.status["WARNING"]}</div>`;
    }

    if (!isEmtpy(data.status['ERROR'])) {
        html += `<div class="alert alert-danger" class="bi bi-exclamation-circle">${data.status["ERROR"]}</div>`;
    }

    if (!isEmtpy(html)) {
        document.getElementById(target).classList.remove("d-none")
        document.getElementById(target).innerHTML = html
    }
}

function isEmtpy(str) {
    return (!str || str.length === 0);
}
