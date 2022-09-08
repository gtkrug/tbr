// list
let listRegistrant = function () {
    list(REGISTRANT_LIST,
        function (registrantList) {
            renderRegistrantTable(
                "registrant-table",
                {
                    editable: true,
                    fnAdd: function () {
                        addRegistrant({id: 0})
                    },
                    fnRemove: removeRegistrant,
                    fnDraw: drawRegistrantTr,
                    hRef: "javascript:getRegistrant",
                    includeOrganizationColumn: false
                },
                registrantList,
                0)
        },
        {id: 0})
}

// render offset
let renderRegistrantOffset = function () {
}

// render table
let renderRegistrantTable = function (tableId, tableMetadata, tableData, offset) {
    renderTable(
        tableId,
        tableMetadata,
        tableData,
        offset,
        "renderRegistrantOffset",
        ["Name", "Email", "Phone", "Role", "Organization"],
        "registrants")
}

// draw tr
let drawRegistrantTr = function (tableMetadata, rowData) {
    return drawTr(
        tableMetadata,
        rowData,
        "edit-registrant",
        "remove-registrant",
        [
            `${rowData.user.contact.lastName}, ${rowData.user.contact.firstName}`,
            `<a href="mailto:${rowData.user.contact.email}">${rowData.user.contact.email}</a>`,
            `${(rowData.user.contact.phone != null ? rowData.user.contact.phone : "")}`,
            rowData.user.role,
            `<a href="${ORGANIZATION_VIEW + rowData.organization.id}">${rowData.organization.name}</a>`
        ],
        {})
}

// render form
let renderRegistrantForm = function (target, preFn, fn, registrant) {
    let html = ``
    html += renderSelectHelper(true, "Organization", "select-organization")
    html += renderSelectHelper(true, "Type", "select-role")
    html += renderInputHelper("detail_lastName", true, "Last Name", "Enter Last Name")
    html += renderInputHelper("detail_firstName", true, "First Name", "Enter First Name")
    html += renderInputHelper("detail_email", true, "Email", "Enter Email Address")
    html += renderInputHelper("detail_phone", true, "Phone", "Enter Phone Number")

    if (registrant.id === 0) {
        html += renderRadioHelper("notify_registrant", false, "Notify")
    }

    renderDialogForm(target, decorateForm("Registrant", "registrantFormId", html, "registrantOk", registrant.id === 0 ? "Add" : "Save"))

    document.getElementById("registrantFormId").addEventListener("click", () => hideIt(target))
    document.getElementById("registrantOk").addEventListener("click", fn)

    preFn(registrant)
}

// populate form
let populateRegistrantForm = function (registrant) {
    let selectOrganizations = function (id) {
        list(ORGANIZATION_LIST,
            curriedSelectOrganizations("select-organization")(id),
            {name: "ALL"})
    }

    let selectRoles = function (id) {
        list(REGISTRANT_ROLES,
            curriedSelectRoles("select-role")(id),
            {name: "ALL"})
    }

    if (registrant.id === 0) {
        selectOrganizations(0)
        selectRoles(0)
    } else {
        selectOrganizations(registrant.organization.id)
        selectRoles(registrant.user.roles[0].id)
        document.getElementById("detail_lastName").value = registrant.user.contact.lastName
        document.getElementById("detail_firstName").value = registrant.user.contact.firstName
        document.getElementById("detail_email").value = registrant.user.contact.email
        document.getElementById("detail_phone").value = registrant.user.contact.phone
    }
    document.getElementById("detail_lastName").focus()
}

// get details
let getRegistrant = function (id) {
    get(REGISTRANT_GET, addRegistrant, {id: id})
}

// add
let addRegistrant = function (registrant) {
    resetStatus("registrant-message")
    
    renderRegistrantForm(
        "registrant-form",
        populateRegistrantForm,
        function () {
            updateRegistrant(
                registrant.id,
                document.getElementById("detail_lastName").value,
                document.getElementById("detail_firstName").value,
                document.getElementById("detail_email").value,
                document.getElementById("detail_phone").value,
                document.getElementById("orgs").options[document.getElementById("orgs").selectedIndex].value,
                document.getElementById("roles").options[document.getElementById("roles").selectedIndex].value,
                document.getElementById("notify_registrant") == null ? false : document.getElementById("notify_registrant").checked)
        },
        registrant)
}

// remove
let removeRegistrant = function () {
    resetStatus("registrant-message")

    if (confirm("The selected registrant(s) may be in use by current systems. Do you wish to proceed?")) {
        getCheckedIds("remove-registrant", function (list) {
            update(REGISTRANT_DELETE,
                listRegistrant,
                {ids: list}
            )
        })
    }
}

// update
let updateRegistrant = function (regId, lname, fname, email, phone, orgId, roleId, notifyRegistrant) {
    resetStatus("registrant-message")

    let checkRegistrant = function (lname, fname, email, orgId, roleId) {
        if (lname == null || lname.length === 0) {
            setDangerStatus("Last name cannot be blank.", "registrant-message")
            document.getElementById("detail_lastName").focus()
            return false
        }
        if (fname == null || fname.length === 0) {
            setDangerStatus("First name cannot be blank.", "registrant-message")
            document.getElementById("detail_firstName").focus()
            return false
        }
        if (email == null || email.length === 0) {
            setDangerStatus("Email cannot be blank.", "registrant-message")
            document.getElementById("detail_email").focus()
            return false
        }
        if (orgId == null || orgId === "0") {
            setDangerStatus("You must select an organization.", "registrant-message")
            document.getElementById("orgs").focus()
            return false
        }
        if (roleId == null || roleId === "0") {
            setDangerStatus("You must select a role.", "registrant-message")
            document.getElementById("roles").focus()
            return false
        }
        return true
    }

    let clearRegistrantForm = function (updated, notifyRegistrant) {
        if (updated) {
            setSuccessStatus("Successfully updated the registrant account!", "registrant-message")
        } else {
            if (notifyRegistrant) {
                setSuccessStatus("Successfully created the registrant account!", "registrant-message")
            } else {
                setSuccessStatus(`<p>Successfully created the registrant account!</p>` +
                    `<p>Please note that the person who you created the account for will not be notified. You must notify them yourself and when you do, tell them to go to this link:</p>` +
                    `<p>${FORGOT_PASSWORD_URL}</p>` +
                    `<p>to reset their password so that they can choose the appropriate password.</p>`, "registrant-message")
            }
        }
        hideIt("registrant-form")
    }

    if (checkRegistrant(lname, fname, email, orgId, roleId)) {
        if (regId === 0) {
            add(
                REGISTRANT_ADD,
                listRegistrant,
                {
                    lname: lname,
                    fname: fname,
                    email: email,
                    phone: phone,
                    pswd: "changeMe!",
                    organizationId: orgId,
                    roleId: roleId,
                    notifyRegistrant: notifyRegistrant
                })
        } else {
            update(
                REGISTRANT_UPDATE,
                listRegistrant,
                {
                    id: regId,
                    lname: lname,
                    fname: fname,
                    email: email,
                    phone: phone,
                    organizationId: orgId,
                    roleId: roleId
                })
        }
        clearRegistrantForm(regId !== 0, notifyRegistrant)
    }
}
