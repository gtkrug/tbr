// list
let listContact = function () {
    list(PROVIDER_LIST_CONTACTS,
        function (contactList) {
            renderContactTable(
                "contact-table",
                {
                    editable: contactList.editable,
                    fnAdd: function () {
                        addContact({id: 0})
                    },
                    fnRemove: removeContact,
                    fnDraw: drawContactTr,
                    hRef: "javascript:getContact",
                    includeOrganizationColumn: false
                },
                contactList,
                0)
        },
        {id: PROVIDER_ORGANIZATION_ID, pid: PROVIDER_ID})
}

// render offset
let renderContactOffset = function () {
}

// render table
let renderContactTable = function (tableId, tableMetadata, tableData, offset) {
    const columnNameArray = ["Last Name", "First Name", "Email", "Phone"]

    let html = renderPagination(offset, tableData.length, "renderContactOffset")

    html += `<thead>`
    html += `<tr>`
    html += LOGGED_IN && tableMetadata.editable ? `<th scope="col" class="d-flex justify-content-center" style="min-width: 51px"><span class="bi bi-link "></span></th>` : ``

    columnNameArray.forEach(columnName => {
        html += `<th scope="col" style="width: ${100 / (columnNameArray.length + (tableMetadata.includeOrganizationColumn ? 1 : 0))}%">${columnName}</th>`
    })

    html += tableMetadata.includeOrganizationColumn ? `<th scope="col" style="width: ${100 / (columnNameArray.length + (tableMetadata.includeOrganizationColumn ? 1 : 0))}%">Organization</th>` : ``
    html += `</tr>`
    html += `</thead>`
    html += `<tbody>`

    if (tableData.records.length === 0) {
        html += `<tr><td colspan="${columnNameArray.length + (LOGGED_IN && tableMetadata.editable ? 1 : 0) + (tableMetadata.includeOrganizationColumn ? 1 : 0)}">There are no points of contact.</td></tr>`
    } else {
        tableData.records.forEach((c, index) => {
            if (index >= offset && index < offset + MAX_DISPLAY) {
                html += tableMetadata.fnDraw(tableMetadata, c)
            }
        })
    }

    html += `</tbody>`

    document.getElementById(tableId).innerHTML = html
}

// draw tr
let drawContactTr = function (tableMetadata, rowData) {
    let html = `<tr>`

    html += LOGGED_IN && tableMetadata.editable ? `<td><div class="form-switch form-check d-flex justify-content-center align-middle"><input id="edit-contact" class="edit-contacts form-check-input" type="checkbox" value="${rowData.contact.id}" ${rowData.inSystem ? "checked" : ""} onclick="addOrRemoveSystemContact(this)"></div></td>` : ``
    html += `<td>${rowData.contact.lastName}</td>`
    html += `<td>${rowData.contact.firstName}</td>`
    html += `<td>${rowData.contact.email}</td>`
    html += `<td>${(rowData.contact.phone != null ? rowData.contact.phone : "")}</td>`
    html += `</tr>`

    return html
}

// render form
let renderContactForm = function (target, preFn, fn, contact) {
    let html = ``
    html += renderSelectHelper(true, "Type", "select-contact-types")
    html += renderInputHelper("lastName", true, "Last Name", "Enter Last Name")
    html += renderInputHelper("firstName", true, "First Name", "Enter First Name")
    html += renderInputHelper("phoneNbr", false, "Phone", "Enter Phone Number")
    html += renderInputHelper("emailAddr", true, "Email", "Enter Email Address")

    renderDialogForm(target, decorateForm("Point of Contact", "contactFormId", html, "contactOk", contact.id === 0 ? "Add" : "Save"))

    document.getElementById("contactFormId").addEventListener("click", () => hideIt(target))
    document.getElementById("contactOk").addEventListener("click", fn)

    preFn(contact)
}

// populate form
let populateContactForm = function (contact) {
    let selectContactTypes = function (id) {
        list(CONTACT_TYPES,
            curriedContactTypes("select-contact-types")(id),
            {name: "ALL"})
    }

    if (contact.id === 0) {
        selectContactTypes(0)
    } else {
        selectContactTypes(contact.type.name)
        document.getElementById("lastName").value = contact.lastName
        document.getElementById("firstName").value = contact.firstName
        document.getElementById("emailAddr").value = contact.email
        document.getElementById("phoneNbr").value = contact.phone
    }
    document.getElementById("lastName").focus()
}

// get details
let getContact = function (id) {
    resetStatus("contact-message")

    get(CONTACT_GET, addContact, {id: id})
}

// add
let addContact = function (contact) {
    resetStatus("contact-message")

    renderContactForm(
        "contact-form",
        populateContactForm,
        function () {
            updateContact(
                contact.id,
                document.getElementById("lastName").value,
                document.getElementById("firstName").value,
                document.getElementById("emailAddr").value,
                document.getElementById("phoneNbr").value,
                document.getElementById("ctypes").options[document.getElementById("ctypes").selectedIndex].value,
                PROVIDER_ORGANIZATION_ID)
        },
        contact)
}

// remove
let removeContact = function () {
    resetStatus("contact-message")

    if (confirm("The selected contact(s) may be in use by current systems. Do you wish to proceed?")) {
        getCheckedIds("edit-contacts", function (list) {
            update(CONTACT_DELETE,
                listContact,
                {ids: list}
            )
        })
    }
}

// update
let updateContact = function (id, lname, fname, email, phone, type, orgId) {
    resetStatus("contact-message")

    let checkContact = function (lname, fname, email, phone, type, orgId) {
        if (lname == null || lname.length === 0) {
            setDangerStatus("Last name cannot be blank.", "contact-message")
            document.getElementById("lastName").focus()
            return false
        }
        if (fname == null || fname.length === 0) {
            setDangerStatus("First name cannot be blank.", "contact-message")
            document.getElementById("firstName").focus()
            return false
        }
        if (email == null || email.length === 0) {
            setDangerStatus("Email cannot be blank.", "contact-message")
            document.getElementById("emailAddr").focus()
            return false
        }
        if (type == null || type === "0") {
            setDangerStatus("You must select a contact type.", "contact-message")
            document.getElementById("ctypes").focus()
            return false
        }
        return true
    }

    let clearContactForm = function () {
        setSuccessStatus("Successfully saved contact.", "contact-message")
        hideIt("contact-form")
    }

    if (checkContact(lname, fname, email, phone, type, orgId)) {
        if (id === 0) {
            add(
                CONTACT_ADD,
                listContact,
                {
                    lname: lname,
                    fname: fname,
                    email: email,
                    phone: phone,
                    organizationId: PROVIDER_ORGANIZATION_ID,
                    type: type
                })
        } else {
            update(
                CONTACT_UPDATE,
                listContact,
                {
                    id: id,
                    lname: lname,
                    fname: fname,
                    email: email,
                    phone: phone,
                    organizationId: PROVIDER_ORGANIZATION_ID,
                    type: type
                })
        }
        clearContactForm()
    }
}

// other
let addOrRemoveSystemContact = function (checkBox) {
    if (checkBox.checked) {
        update(
            PROVIDER_ADD_CONTACT_TO_SYSTEM,
            function (data) {
                if (!isEmtpy(data.id)) {
                    setSuccessStatus(`Added point of contact, ${data.firstName} ${data.lastName}, to system.`, "contact-message")
                }
            },
            {contactId: checkBox.value, providerId: PROVIDER_ID})
    } else {
        update(
            PROVIDER_REMOVE_CONTACT_FROM_SYSTEM, function (data) {
                if (!isEmtpy(data.id)) {
                    setSuccessStatus(`Removed point of contact, ${data.firstName} ${data.lastName}, from system.`, "contact-message")
                }
            },
            {contactId: checkBox.value, providerId: PROVIDER_ID})
    }
}
