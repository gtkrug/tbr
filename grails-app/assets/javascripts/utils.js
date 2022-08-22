let STATUS_HEADER = "status-header"

/**
 * gets checked ids by class name, puts in a list and applies the function argument to them
 * @param str
 * @param fn
 */
let getCheckedIds = function (str, fn) {
    let elements = document.getElementsByClassName(str)
    let idList = ""
    for (let i = 0; i < elements.length; ++i) {
        if (elements[i].checked === true) {
            idList += elements[i].value + ":"
        }
    }
    fn(idList)
}

/**
 *  render organizations in a select control
 */
let renderSelectOrganizations = function (selectParentId, optionSelectId, optionArray) {
    renderSelect(selectParentId, "orgs", "-- Select an Organization --", optionArray.map(option => {
        return {
            value: option.id,
            text: option.name
        }
    }), optionSelectId)
}

/**
 *  render organizations in a select control
 */
let renderSelectRoles = function (selectParentId, optionSelectId, optionArray) {
    renderSelect(selectParentId, "roles", "-- Select a Role --", optionArray.map(option => {
        return {
            value: option.id,
            text: option.label
        }
    }), optionSelectId)

    // hide organization select if role is ROLE_ADMIN
    $("#roles").on("change", function () {
        if (this.options[this.selectedIndex].text == "TBR Administrator") {
            $("#select-organization-group").hide()
        } else {
            $("#select-organization-group").show()
        }
    })
}

/**
 *  render all contact types in a select control
 */
let renderContactTypes = function (selectParentId, optionSelectId, optionArray) {
    renderSelect(selectParentId, "ctypes", "-- Select a Contact Type --", optionArray.map(option => {
        return {
            value: option.name,
            text: option.name
        }
    }), optionSelectId)
}

let renderSelect = function (selectParentId, selectId, optionDefaultText, optionArray, optionSelectId) {
    let html = `<select class="form-select" id="${selectId}">`
    html += `<option value="0">${optionDefaultText}</option>`
    optionArray.forEach(option => {
        html += `<option value="${option.value}">${option.text}</option>`
    })
    html += `</select>`

    document.getElementById(selectParentId).innerHTML = html
    document.getElementById(selectId).value = optionSelectId === 0 ? optionArray[optionSelectId].value : optionSelectId
}

//  we can write a single curry function to take an undetermined number of functions TODO
/**
 * transforms the function into 2 separate argument functions
 * @param f
 * @returns {function(*=): function(*=): *}
 */
let curryTwo = function (f) {
    return function (a) {
        return function (b) {
            return f(a, b);
        }
    }
}

/**
 * transforms the passed in function to 3 separate argument functions
 * @param f
 * @returns {function(*=): function(*=): function(*=): *}
 */
let curryThree = function (f) {
    return function (a) {
        return function (b) {
            return function (c) {
                return f(a, b, c);
            }
        }
    }
}

/**
 * transforms the passed in function to 4 separate argument functions
 * @param f
 * @returns {function(*=): function(*=): function(*=): function(*=): *}
 */
let curryFour = function (f) {
    return function (a) {
        return function (b) {
            return function (c) {
                return function (d) {
                    return f(a, b, c, d);
                }
            }
        }
    }
}

let setDangerStatus = function (content, elementId = STATUS_HEADER) {
    document.getElementById(elementId).classList.remove("d-none")
    document.getElementById(elementId).innerHTML = `<div class="alert alert-danger">${content}</div>`;
}

let setWarningStatus = function (content, elementId = STATUS_HEADER) {
    document.getElementById(elementId).classList.remove("d-none")
    document.getElementById(elementId).innerHTML = `<div class="alert alert-warning">${content}</div>`;
}

let setSuccessStatus = function (content, elementId = STATUS_HEADER) {
    document.getElementById(elementId).classList.remove("d-none")
    document.getElementById(elementId).innerHTML = `<div class="alert alert-primary">${content}</div>`;
}

let resetStatus = function (elementId = STATUS_HEADER) {
    document.getElementById(elementId).classList.add("d-none")
    document.getElementById(elementId).innerHTML = ``;
}

let curriedContactTypes = curryThree(renderContactTypes)

let curriedSelectOrganizations = curryThree(renderSelectOrganizations)

let curriedSelectRoles = curryThree(renderSelectRoles);
