let STATUS_HEADER = 'status-header';

/**
 * gets checked ids by class name, puts in a list and applies the function argument to them
 * @param str
 * @param fn
 */
let getCheckedIds = function(str, fn) {
    let elements = document.getElementsByClassName(str);
    let idList = "";
    for( let i=0; i < elements.length; ++i)  {
        if(elements[i].checked === true)  {
            idList += elements[i].value+":";
        }
    }
    fn(idList);
}

/**
 *  render organizations in a select control
 */
let renderSelectOrganizations = function(target, id, data)  {
    let html = "<select class='form-control' id='orgs'>";
    html += "<option value='0'>-- Select an Organization --</option>";
    data.forEach(o => {
        html += "<option value='"+o.id+"'>"+o.name+"</option>";
    });
    html += "</select><span style='color:red;'>&nbsp;&nbsp;*</span>";
    document.getElementById(target).innerHTML = html;
    document.getElementById('orgs').value = id;
}

/**
 *  render all contact types in a select control
 */
let renderContactTypes = function(target, id, data)  {
    let html = "<select id='ctypes' class='form-control'>";
    html += "<option value='0'> -- Select a Contact Type -- </option>";
    data.forEach(o => {
        html += "<option value='"+o.name+"'>"+o.name+"</option>";
    });
    html += "</select><span style='color:red;'>&nbsp;&nbsp;*</span>";
    document.getElementById(target).innerHTML = html;
    document.getElementById('ctypes').value = id;
}

/**
 *  render all contact types in a select control
 */
let renderProviderTypes = function(target, data)  {
    let html = "<select id='pType' class='form-control'>";
    html += "<option value='0'> -- Select a Provider Type -- </option>";
    data.forEach(o => {
        html += "<option value='"+o.name+"'>"+o.name+"</option>";
    });
    html += "</select><span style='color:red;'>&nbsp;&nbsp;*</span>";
    document.getElementById(target).innerHTML = html;
}

/**
 * checks the form contents for completeness
 * @param lname
 * @param fname
 * @param email
 * @param phone
 * @param type
 * @param orgId
 * @returns {boolean}
 */
let checkContact = function(lname, fname, email, phone, type, orgId)  {
    if(lname == null || lname.length === 0) {
        setDangerStatus("<b>Last name cannot be blank.</b>");
        document.getElementById('lastName').focus();
        return false;
    }
    if(fname == null || fname.length === 0) {
        setDangerStatus("<b>First name cannot be blank.</b>");
        document.getElementById('firstName').focus();
        return false;
    }
    if(email == null || email.length === 0) {
        setDangerStatus("<b>Email cannot be blank.</b>");
        document.getElementById('emailAddr').focus();
        return false;
    }
    if(orgId == null || orgId === "0") {
        setDangerStatus("<b>You must select an organization.</b>");
        document.getElementById('orgs').focus();
        return false;
    }
    if(type == null || type === "0") {
        setDangerStatus("<b>You must select a contact type.</b>");
        document.getElementById('ctypes').focus();
        return false;
    }
    return true;
}

//  we can write a single curry function to take an undetermined number of functions TODO
/**
 * transforms the function into 2 separate argument functions
 * @param f
 * @returns {function(*=): function(*=): *}
 */
let curryTwo = function(f)  {
    return function(a) {
        return function(b)  {
            return f(a, b);
        }
    }
}

/**
 * transforms the passed in function to 3 separate argument functions
 * @param f
 * @returns {function(*=): function(*=): function(*=): *}
 */
let curryThree = function(f)  {
    return function(a)  {
        return function(b) {
            return function(c) {
                return f(a, b, c);
            }
        }
    }
}

/**
 * transforms the passed in function to 4 separate argument functions
 * @param f
 * @returns {function(*=): function(*=): function(*=): *}
 */
let curryFour = function(f)  {
    return function(a)  {
        return function(b) {
            return function(c) {
                return function(d) {
                    return f(a, b, c, d);
                }
            }
        }
    }
}

let renderStatus = curryThree(function(target, fn, msg) {
    document.getElementById(target).innerHTML = fn(msg);
});

let setDangerStatus = renderStatus(STATUS_HEADER)(function(msg){return '<div class=\'alert alert-danger\'>'+msg+'</div>'});

let setWarningStatus = renderStatus(STATUS_HEADER)(function(msg){return '<div class=\'alert alert-warning\'>'+msg+'</div>'});

let setSuccessStatus = renderStatus(STATUS_HEADER)(function(msg){return '<div class=\'alert alert-success\'>'+msg+'</div>'});

let curriedContact = curryFour(renderContacts);

let contactDetail = curryFour(renderContactForm);

let curriedRegistrant = curryFour(renderRegistrants);

let curriedOrganization = curryFour(renderOrganizations);

let curriedEndpoint = curryFour(renderEndpoints);

let curriedTrustmark = curryThree(renderTrustmarks);

let curriedAttribute = curryFour(renderAttributes);

let curriedProvider = curryFour(renderProviders);

let curriedTag = curryFour(renderTags);

let curriedConformanceTargetTip = curryFour(renderConformanceTargetTips);

let curriedRepos = curryThree(renderRepos);

let curriedTrustmarkRecipientIdentifier = curryThree(renderTrustmarkRecipientIdentifiers);

let curriedContactTypes = curryThree(renderContactTypes);

let curriedSelectOrganizations = curryThree(renderSelectOrganizations);

let curriedProviderTypes = curryTwo(renderProviderTypes);
