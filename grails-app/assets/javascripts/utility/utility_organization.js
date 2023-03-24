// list
let listOrganization = function () {
    list(ORGANIZATION_LIST,
        organizationResults(),
        {name: "ALL"})
}

// render offset
let renderOrganizationOffset = function () {
}

// render table
let renderOrganizationTable = function (tableId, tableMetadata, tableData, offset) {
    renderTable(
        tableId,
        tableMetadata,
        tableData.records,
        offset,
        "renderOrganizationOffset",
        ["Name", "URL", "System Count"],
        "organizations")
}

// draw tr
let drawOrganizationTr = function (obj, entry) {
    return drawTr(
        obj,
        entry,
        "edit-organization",
        "remove-organization",
        [
            `<a href="${ORGANIZATION_VIEW}${entry.id}">${entry.name}</a>`,
            `<a href="${entry.siteUrl}" target="_blank">${entry.siteUrl}</a>`,
            entry.providers.length
        ],
        {})
}

let curriedOrganization = curryFour(renderOrganizationTable);

let organizationResults = function () {
    return function (results) {
        renderOrganizationOffset = curriedOrganization('organization-table')
        ({
            editable: results.editable,
            fnAdd: function () {
                addOrganization({id: 0}, true, !results.editable)
            },
            fnRemove: removeOrganization,
            fnDraw: drawOrganizationTr,
            hRef: "javascript:getOrganization"
        })
        (results);
        renderOrganizationOffset(0);
    }
}

// render form
let renderOrganizationForm = function (target, preFn, fn, organization, hide, readOnly) {
    let html = ``
    html += hide ? renderInputHelper("org_name", true, "Full Name", "Enter Organization Full Name", undefined, readOnly) : renderHiddenHelper("org_name")
    html += renderInputHelper("org_display", true, "Abbreviation", "Enter Organization Abbreviation or Acronym", undefined, readOnly)
    html += renderInputHelper("org_url", true, "URL", "Enter Organization URL", undefined, readOnly)
    html += renderInputHelper("org_desc", true, "Description", "Enter Organization Description", undefined, readOnly)

    renderDialogForm(target, decorateForm("Basic Organization Information", "organizationFormId", html, "organizationOk", organization.id === 0 ? "Add" : "Save", hide, readOnly.toString()))

    if (hide) {
        document.getElementById("organizationFormId").addEventListener("click", () => hideIt(target))
    }

    if (readOnly === false) {
        document.getElementById("organizationOk").addEventListener("click", fn)
    }

    preFn(organization)
}

// populate form
let populateOrganizationForm = function (organization) {
    if (organization.id !== 0) {
        document.getElementById("org_name").value = organization.name
        document.getElementById("org_display").value = organization.displayName
        document.getElementById("org_url").value = organization.siteUrl
        document.getElementById("org_desc").value = organization.description
    }
    document.getElementById("org_name").focus()
}

// get details
let getOrganization = function (id, hide, isReadOnly) {
    if(hide) {
        resetStatus("organization-message")
    }

    get(ORGANIZATION_GET, (organization) => addOrganization(organization, hide === undefined ? true : hide, isReadOnly), {id: id})
}

// add
let addOrganization = function (organization, hide, readOnly) {
    if(hide) {
        resetStatus("organization-message")
    }

    renderOrganizationForm(
        "organization-form",
        populateOrganizationForm,
        function () {
            updateOrganization(
                organization.id,
                document.getElementById("org_name").value,
                document.getElementById("org_display").value,
                document.getElementById("org_url").value,
                document.getElementById("org_desc").value,
                hide,
                readOnly)
        },
        organization,
        hide,
        readOnly)
}

// remove
let removeOrganization = function () {
    resetStatus("organization-message")

    if (confirm("This operation will delete all data for the selected organizations including all systems that have been added to each organization. Do you want to continue?")) {
        getCheckedIds("remove-organization", function (list) {
            update(ORGANIZATION_DELETE,
                listOrganization,
                {ids: list}
            )
        })
    }
}

// update
let updateOrganization = function (id, name, display, siteUrl, desc, hide, readOnly) {
    resetStatus("organization-message")

    let checkOrganization = function (name, display, siteUrl, desc) {
        function isValidUrl(string) {
            let url
            let validUrl = false
            try {
                url = new URL(string)
            } catch (_) {
                return false
            }
            validUrl = url.protocol === "http:" || url.protocol === "https:"

            return validUrl
        }

        if (name == null || name.length === 0) {
            setDangerStatus("Organization name cannot be blank.", "organization-message")
            document.getElementById("org_name").focus()
            return false
        }
        if (display == null || display.length === 0) {
            setDangerStatus("Display name cannot be blank.", "organization-message")
            document.getElementById("org_display").focus()
            return false
        }
        if (siteUrl == null || siteUrl.length === 0) {
            setDangerStatus("URL cannot be blank.", "organization-message")
            document.getElementById("org_url").focus()
            return false
        }
        if (!isValidUrl(siteUrl)) {
            setDangerStatus("URL is not valid.", "organization-message")
            document.getElementById("org_url").focus()
            return false
        }
        if (desc == null || desc.length === 0) {
            setDangerStatus("Description cannot be blank.", "organization-message")
            document.getElementById("org_desc").focus()
            return false
        }
        return true
    }

    let clearOrganizationForm = function () {
        setSuccessStatus("Successfully saved organization.", "organization-message")
        if (hide) {
            hideIt("organization-form")
        }
    }

    if (checkOrganization(name, display, siteUrl, desc)) {
        if (id === 0) {
            add(
                ORGANIZATION_ADD,
                listOrganization,
                {
                    name: name,
                    displayName: display,
                    siteUrl: siteUrl,
                    description: desc
                })
        } else {
            if (hide) {
                update(
                    ORGANIZATION_UPDATE,
                    listOrganization,
                    {
                        id: id,
                        display: display,
                        url: siteUrl,
                        desc: desc
                    })
            } else {
                update(
                    ORGANIZATION_UPDATE,
                    function () {
                        getOrganization(id, hide, readOnly)
                    },
                    {
                        id: id,
                        display: display,
                        url: siteUrl,
                        desc: desc
                    })
            }
        }
        clearOrganizationForm()
    }
}
