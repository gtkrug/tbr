// list
let listContact = function () {
    list(CONTACT_LIST,
        contactResults(0),
        {id: 0})
}

// render offset
let renderContactOffset = function () {
}

// render table
let renderContactTable = function (tableId, tableMetadata, tableData, offset) {
    renderTable(
        tableId,
        tableMetadata,
        tableData.records,
        offset,
        "renderContactOffset",
        ["Last Name", "First Name", "Email", "Phone"],
        "points of contact")
}

// draw tr
let drawContactTr = function (tableMetadata, rowData) {
    return drawTr(
        tableMetadata,
        rowData,
        "edit-contact",
        "remove-contact",
        [
            rowData.lastName,
            rowData.firstName,
            rowData.email,
            rowData.phone != null ? rowData.phone : ""
        ],
        {})
}

let curriedContact = curryFour(renderContactTable);

let contactResults = function (id) {
    return function (results) {
        renderContactOffset = curriedContact('contact-table')
        ({
            editable: results.editable,
            fnAdd: function () {
                addContact({id: 0})
            },
            fnRemove: removeContact,
            fnDraw: drawContactTr,
            hRef: "javascript:getContact",
            includeOrganizationColumn: true
        })
        (results);
        renderContactOffset(0);
    }
}

// render form
let renderContactForm = function (target, preFn, fn, contact) {
    let html = ``
    html += renderSelectHelper(true, "Type", "select-contact-types")
    html += renderInputHelper("lastName", true, "Last Name", "Enter Last Name")
    html += renderInputHelper("firstName", true, "First Name", "Enter First Name")
    html += renderInputHelper("phoneNbr", false, "Phone", "Enter Phone Number")
    html += renderInputHelper("emailAddr", true, "Email", "Enter Email Address")

    renderDialogForm(target, decorateForm("Point of Contact", "contactFormId", html, "contactOk", contact.id === 0 ? "Add" : "Save", undefined, 'false'))

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
                ORGANIZATION_ID)
        },
        contact)
}

// remove
let removeContact = function () {
    resetStatus("contact-message")

    if (confirm("The selected contact(s) may be in use by current systems. Do you wish to proceed?")) {
        getCheckedIds("remove-contact", function (list) {
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
                    organizationId: ORGANIZATION_ID,
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
                    organizationId: ORGANIZATION_ID,
                    type: type
                })
        }
        clearContactForm()
    }
}
