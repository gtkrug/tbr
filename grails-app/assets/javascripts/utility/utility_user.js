let USER_TABLE_ITEMS_PER_PAGE = TABLE_FULL_PAGE_ITEMS_PER_PAGE;

// list
let listUsers = function () {
    list(USER_LIST,
        userResults(),
        {id: 0})
}

// render offset
let renderUserOffset = function () {
}

// render table
let renderUserTable = function (tableId, tableMetadata, tableData, offset) {
    renderTableWithoutAddOrMinus(
        tableId,
        tableMetadata,
        tableData.records,
        USER_TABLE_ITEMS_PER_PAGE,
        offset,
        "renderUserOffset",
        ["Name", "Email", "Role(s)", "Organization"],
        "users")

    userItemsPerPageTableEventHandler(tableId, renderUserOffset);
}

let userItemsPerPageTableEventHandler = function (tableId, func) {

    $(`#items-per-page-${tableId}`).on('change', function() {
        const ipp = parseInt(document.getElementById(`items-per-page-${tableId}`).value);

        USER_TABLE_ITEMS_PER_PAGE = ipp

        func(0);
    });
}

// draw tr
let drawUserTr = function (tableMetadata, rowData) {

    let rolesHtml = '';

    if (rowData.roles !== null) {
        rolesArray = rowData.roles;
        parsedRolesArray = JSON.parse(rolesArray);

        rolesHtml = '<ul>';
        parsedRolesArray.forEach(function (role) {
            rolesHtml += '<li>' + role + '</li>';
        })
        rolesHtml += '</ul>';
    }

    return drawTrWithoutTrash(
        tableMetadata,
        rowData,
        "edit-user",
        "remove-user", // ?????
        [
            `${rowData.nameFamily}, ${rowData.nameGiven}`,
            `<a href="mailto:${rowData.contactEmail}">${rowData.contactEmail}</a>`,
            rolesHtml,
            `<a href="${rowData.contact != null ?
                ORGANIZATION_VIEW + rowData.contact.organization.id :
                ORGANIZATION_VIEW + '-1'}">${rowData.contact != null ? rowData.contact.organization.name : ""}</a>`
        ],
        {})
}

let curriedUser = curryFour(renderUserTable);

let userResults = function () {
    return function (results) {
        renderUserOffset = curriedUser('user-table')
        ({
            editable: results.editable,
            fnDraw: drawUserTr,
            hRef: "javascript:getUser",
            includeOrganizationColumn: false
        })
        (results);
        renderUserOffset(0);
    }
}

// render form
let renderUserForm = function (target, preFn, fn, user) {

    let html = ``
    html += renderSelectHelper(true, "Organization", "select-organization")

    renderDialogForm(target, decorateForm("Set Organization", "userFormId", html, "userOk", "Update", undefined, 'false'))

    document.getElementById("userFormId").addEventListener("click", () => hideIt(target))
    document.getElementById("userOk").addEventListener("click", fn)

    preFn(user)
}

// populate form
let populateUserForm = function (user) {

    let selectOrganizations = function (id) {
        list(ORGANIZATION_LIST,
            curriedSelectOrganizations("select-organization")(id),
            {name: "ALL"})
    }

    if (user.id === 0) {
        selectOrganizations(0)
    } else {
        if (user.contact == null) {
            selectOrganizations(0)
        } else {
            selectOrganizations(user.contact.organization.id)
        }
    }
    document.getElementById("select-organization").focus()
}

// get details
let getUser = function (id) {
    get(USER_GET, addUser, {id: id})
}

// add
let addUser = function (user) {
    resetStatus("user-message")

    renderUserForm(
        "user-form",
        populateUserForm,
        function () {
            updateUser(
                user.id,
                document.getElementById("orgs").options[document.getElementById("orgs").selectedIndex].value//,
            )
        },
        user)
}

// update
let updateUser = function (regId, orgId) {
    resetStatus("user-message")

    let checkUser = function (orgId) {

        // TODO: DO we need this if we allow users without organizations?

        if (orgId == null || orgId === "0") {
            setDangerStatus("You must select an organization.", "user-message")
            document.getElementById("orgs").focus()
            return false
        }
        return true
    }

    let clearUserForm = function (updated) {
        if (true) {
            setSuccessStatus("Successfully updated the user account!", "user-message")
        }
        hideIt("user-form")
    }

    if (checkUser(orgId)) {
        update(
            USER_UPDATE,
            listUsers,
            {
                id: regId,
                organizationId: orgId,
            })

        clearUserForm(regId !== 0)
    }
}
