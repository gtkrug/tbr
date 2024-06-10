let SIGNING_CERT_TABLE_ITEMS_PER_PAGE = TABLE_FULL_PAGE_ITEMS_PER_PAGE;

// list
let listSigningCertificate = function () {
    list(SIGNING_CERTIFICATES_LIST,
        signingCertificateResults(),
        {name: "ALL"})
}

// render offset
let renderSigningCertificateOffset = function () {
}

// render table
let renderSigningCertificateTable = function (tableId, tableMetadata, tableData, offset) {
    let html = renderPagination(tableId, SIGNING_CERT_TABLE_ITEMS_PER_PAGE, offset, tableData.length, "renderSigningCertificateOffset", 5)

    html += `<thead>`
    html += `<tr>`
    html += `<th scope="col"><a href="#" id="plus-${tableId}" class="bi-plus-lg ${(tableMetadata.editable ? "" : "d-none")}"></a></th>`
    html += `<th style="width: 33%">Distinguished Name</th>`
    html += `<th style="width: 33%">Email Address</th>`
    html += `<th style="width: 33%">URL</th>`
    html += `<th>Status</th>`
    html += `</tr>`
    html += `</thead>`

    html += `</tbody>`

    if (tableData.length === 0) {
        html += `<tr><td colspan="5">There are no signing certificates.</td></tr>`
    } else {
        let idx = 0
        tableData.forEach(o => {
            if (idx >= offset && idx < offset + SIGNING_CERT_TABLE_ITEMS_PER_PAGE) {
                html += tableMetadata.fnDraw(tableMetadata, o)
            }
            ++idx
        })
    }
    html += `</tbody>`

    document.getElementById(tableId).innerHTML = html
    if (tableMetadata.editable) {
        document.getElementById(`plus-${tableId}`).onclick = tableMetadata.fnAdd
    }

    if (document.getElementById(`items-per-page-${tableId}`) != null) {
        document.getElementById(`items-per-page-${tableId}`).value = SIGNING_CERT_TABLE_ITEMS_PER_PAGE;
    }

    signingCertItemsPerPageTableEventHandler(tableId, renderSigningCertificateOffset);
}

let signingCertItemsPerPageTableEventHandler = function (tableId, func) {

    $(`#items-per-page-${tableId}`).on('change', function() {
        const ipp = parseInt(document.getElementById(`items-per-page-${tableId}`).value);

        SIGNING_CERT_TABLE_ITEMS_PER_PAGE = ipp

        func(0);
    });
}

// draw tr
let drawSigningCertificateTr = function (tableMetadata, rowData) {

    let html = `<tr>`

    if (rowData.status == "ACTIVE") {
        if (rowData.defaultCertificate === true) {
            html += `<td><input class="form-check-input" type="radio" name="activeCertificateRadioGroup" onclick="setDefaultCertificate(${rowData.id}, ${rowData.defaultCertificate})" checked value="${rowData.id}"></td>`
        } else {
            html += `<td><input class="form-check-input" type="radio" name="activeCertificateRadioGroup" onclick="setDefaultCertificate(${rowData.id}, ${rowData.defaultCertificate})" value="${rowData.id}"></td>`
        }
    } else {
        html += "<td></td>"
    }

    // distinguished name
    var href = `${SIGNING_CERTIFICATES_VIEW}?id=${rowData.id}`
    var title = `View ${rowData.distinguishedName}`
    var link = `<td><a href="${href}" title="${title}"><span>${rowData.distinguishedName}</span></a></td>`
    html += link

    // email address
    html += `<td>${rowData.emailAddress}</td>`

    // url
    href = rowData.url
    title = `Download ${rowData.distinguishedName}`
    link = `<td><a href="${href}" title="${title}"><span>${rowData.url}</span></a></td>`
    html += link

    // status
    if (rowData.status == "ACTIVE") {
        html += `<td class="text-center"><span class="bi bi-check-circle-fill text-success" title="Certificate still valid."></span></td>`
    } else if (rowData.status == "REVOKED") {
        html += `<td class="text-center"><span class="bi bi-x-circle-fill text-danger" title="Certificate has been revoked."></span></td>`
    } else if (rowData.status == "EXPIRED") {
        html += `<td class="text-center"><span class="bi bi-dash-circle-fill text-warning" title="Certificate has expired."></span></td>`
    }

    html += `</tr>`

    return html
}

let curriedSigningCertificate = curryFour(renderSigningCertificateTable);

let signingCertificateResults = function () {
    return function (results) {
        renderSigningCertificateOffset = curriedSigningCertificate('signing-certificate-table')
        ({
            editable: true,
            fnAdd: function () {
                addSigningCertificate({id: 0})
            },
            fnRemove: removeSigningCertificate,
            fnDraw: drawSigningCertificateTr,
            hRef: "javascript:getSigningCertificate",
            includeOrganizationColumn: false
        })
        (results);
        renderSigningCertificateOffset(0);
    }
}

// render form
let renderSigningCertificateForm = function (target, preFn, fn, signingCertificate) {
    let html = ``;

    html += renderInputHelper("commonName", true, "Common Name", "Enter Signing Certificate Organization Id")
    html += renderInputHelper("localityName", true, "Locality Name", "Enter Signing Certificate Locality Name")
    html += renderInputHelper("stateName", true, "State Or Province Name", "Enter Signing Certificate State Name")
    html += renderInputHelper("countryName", true, "Country Name", "Enter Signing Certificate Country Name")
    html += renderInputHelper("emailAddress", true, "Email Address", "Enter Signing Certificate Email Address")
    html += renderInputHelper("organizationName", true, "Organization Name", "Enter Signing Certificate Organization Name")
    html += renderInputHelper("organizationUnitName", true, "Organizational Unit Name", "Enter Signing Certificate Organization Unit Name")
    html += renderSelectHelperWithOptionList(false, "Period of Validity (years)", "validPeriod", VALID_PERIOD_FROM_LIST)
    html += renderSelectHelperWithOptionList(false, "Key Length (bits)", "keyLength", KEY_LENGTH_FROM_LIST)

    renderDialogForm(target, decorateForm("Signing Certificate", "certificateFormId", html, "certificateOk", signingCertificate.id === 0 ? "Add" : "Save", undefined, 'false'))
    document.getElementById("certificateFormId").addEventListener("click", () => hideIt(target))
    document.getElementById("certificateOk").addEventListener("click", fn)

    preFn(signingCertificate)
}

// populate form
let populateSigningCertificate = function (signingCertificate) {
    if (signingCertificate.id !== 0) {
        document.getElementById("commonName").value = signingCertificate.commonName
        document.getElementById("localityName").value = signingCertificate.localityName
        document.getElementById("stateName").value = signingCertificate.stateName
        document.getElementById("countryName").value = signingCertificate.countryName
        document.getElementById("emailAddress").value = signingCertificate.emailAddress
        document.getElementById("organizationName").value = signingCertificate.organizationName
        document.getElementById("organizationUnitName").value = signingCertificate.organizationUnitName
    }
    document.getElementById("commonName").focus()
}

// get details
let getSigningCertificate = function (id) {
}

// add
let addSigningCertificate = function (signingCertificate) {
    renderSigningCertificateForm("signing-certificate-form",
        populateSigningCertificate,
        function () {
            updateSigningCertificate(
                document.getElementById("commonName").value,
                document.getElementById("localityName").value,
                document.getElementById("stateName").value,
                document.getElementById("countryName").value,
                document.getElementById("emailAddress").value,
                document.getElementById("organizationName").value,
                document.getElementById("organizationUnitName").value,
                document.getElementById("validPeriod").value,
                document.getElementById("keyLength").value)
        },
        signingCertificate)
}

// remove
let removeSigningCertificate = function () {
    resetStatus("signing-certificate-message")

    if (confirm("The selected certificates(s) may be in use by current systems. Do you wish to proceed?")) {
        getCheckedIds("edit-signing-certificate", function (list) {
            update(SIGNING_CERTIFICATES_DELETE,
                listSigningCertificate,
                {ids: list}
            )
        })
    }
}

// update
let updateSigningCertificate = function (commonName, localityName, stateName, countryName, emailAddress, organizationName, organizationUnitName, validPeriod, keyLength) {
    resetStatus("signing-certificate-message")

    let checkSigningCertificate = function (commonName, localityName, stateName, countryName, emailAddress, organizationName, organizationUnitName, validPeriod, keyLength) {
        if (commonName == null || commonName.length === 0) {
            setDangerStatus("Common name cannot be blank.", "signing-certificate-message")
            document.getElementById("commonName").focus()
            return false
        }
        if (localityName == null || localityName.length === 0) {
            setDangerStatus("Locality name cannot be blank.", "signing-certificate-message")
            document.getElementById("localityName").focus()
            return false
        }
        if (stateName == null || stateName.length === 0) {
            setDangerStatus("State name cannot be blank.", "signing-certificate-message")
            document.getElementById("stateName").focus()
            return false
        }
        if (countryName == null || countryName.length === 0) {
            setDangerStatus("Country name cannot be blank.", "signing-certificate-message")
            document.getElementById("countryName").focus()
            return false
        }
        if (emailAddress == null || emailAddress.length === 0) {
            setDangerStatus("Email address cannot be blank.", "signing-certificate-message")
            document.getElementById("emailAddress").focus()
            return false
        }
        if (organizationName == null || organizationName.length === 0) {
            setDangerStatus("Organization name cannot be blank.", "signing-certificate-message")
            document.getElementById("organizationName").focus()
            return false
        }
        if (organizationUnitName == null || organizationUnitName.length === 0) {
            setDangerStatus("Organization unit name cannot be blank.", "signing-certificate-message")
            document.getElementById("organizationUnitName").focus()
            return false
        }
        if (validPeriod == null || validPeriod.length === 0) {
            setDangerStatus("Valid period cannot be blank.", "signing-certificate-message")
            document.getElementById("validPeriod").focus()
            return false
        }
        if (keyLength == null || keyLength.length === 0) {
            setDangerStatus("Key length cannot be blank.", "signing-certificate-message")
            document.getElementById("keyLength").focus()
            return false
        }
        return true
    }

    let clearSigningCertificateForm = function () {
        document.getElementById("commonName").value = ""
        document.getElementById("localityName").value = ""
        document.getElementById("stateName").value = ""
        document.getElementById("countryName").value = ""
        document.getElementById("emailAddress").value = ""
        document.getElementById("organizationName").value = ""
        document.getElementById("organizationUnitName").value = ""

        setSuccessStatus("Successfully added signing certificate.", "signing-certificate-message")
        hideIt("signing-certificate-form")
    }

    if (checkSigningCertificate(commonName, localityName, stateName, countryName, emailAddress, organizationName, organizationUnitName, validPeriod, keyLength)) {
        add(
            SIGNING_CERTIFICATES_ADD,
            listSigningCertificate,
            {
                commonName: commonName,
                localityName: localityName,
                stateName: stateName,
                countryName: countryName,
                emailAddress: emailAddress,
                organizationName: organizationName,
                organizationUnitName: organizationUnitName,
                validPeriod: validPeriod,
                keyLength: keyLength
            })
        clearSigningCertificateForm()
    }
}

let setDefaultCertificate = function (id, defaultCertificate) {
    if (!defaultCertificate) {
        add(SIGNING_CERTIFICATES_SET_DEFAULT_CERTIFICATE,
            listSigningCertificate,
            {id: id})
        setSuccessStatus("The active signing certificate has been changed.", "signing-certificate-message")
    } else {
        resetStatus()
    }
}
