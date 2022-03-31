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
    var selectId = id;

    if (data.length > 0) {
        selectId = (id === 0 ? data[0].id : id);
    }

    let html = "<select class='form-control col-sm-10' id='orgs' style='width: 70%;'>";
    html += "<option value='0'>-- Select an Organization --</option>";
    data.forEach(o => {
        html += "<option value='"+o.id+"'>"+o.name+"</option>";
    });
    html += "</select><span style='color:red;'>&nbsp;&nbsp;*</span>";
    document.getElementById(target).innerHTML = html;
    document.getElementById('orgs').value = selectId;
}

/**
 *  render organizations in a select control
 */
let renderSelectRoles = function(target, id, data)  {

    let html = "<select class='form-control col-sm-10' id='roles' style='width: 70%;'>";
    html += "<option value='0'>-- Select a Role --</option>";
    data.forEach(o => {
        html += "<option value='"+o.id+"'>"+o.label+"</option>";
    });
    html += "</select><span style='color:red;'>&nbsp;&nbsp;*</span>";
    document.getElementById(target).innerHTML = html;
    document.getElementById('roles').value = id;

    // hide organization select if role is ROLE_ADMIN
    $('#roles').on('change', function() {
        console.log("Selected ROLE: " + this.value);
        console.log("Selected HTML: " + this.options[this.selectedIndex].text);

        if (this.options[this.selectedIndex].text == 'TBR Administrator') {
            $('#select-organization-group').hide();
        } else {
            $('#select-organization-group').show();
        }
    });
}

/**
 *  render all contact types in a select control
 */
let renderContactTypes = function(target, id, data)  {
    let html = "<select id='ctypes' class='form-control col-sm-8' style='width: 70%;'>";
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
    let html = "<select id='pType' class='form-control col-sm-8' style='width: 70%;'>";
    html += "<option value='0'> -- Select a System Type -- </option>";
    data.forEach(o => {
        html += "<option value='"+o+"'>"+o+"</option>";
    });
    html += "</select><span style='color:#ff0000;'>&nbsp;&nbsp;*</span>";
    document.getElementById(target).innerHTML = html;
}

/**
 * checks the form contents for completeness
 * @param filename
 * @param description
 * @param type
 * @returns {boolean}
 */
let checkDocument = function(id, filename, description)  {

    if(filename == null || filename.length === 0) {
        setDangerStatus("<b>Filename cannot be blank.</b>");
        document.getElementById('filename').focus();
        return false;
    }
    if(description == null || description.length === 0) {
        setDangerStatus("<b>Description cannot be blank.</b>");
        document.getElementById('description').focus();
        return false;
    }
    return true;
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

/**
 * checks the form contents for completeness
 * @param repo
 * @returns {boolean}
 */
let checkRepo = function(repo)  {

    if(repo == null || repo.length === 0) {
        setDangerStatus("<b>URL cannot be blank.</b>");
        document.getElementById('assessmentToolUrlRepo').focus();
        return false;
    }

    return true;
}

/**
 * checks the form contents for completeness
 * @param trustmarkRecipientIdentifier
 * @returns {boolean}
 */
let checkTrustmarkRecipientIdentifier = function(trustmarkRecipientIdentifier)  {

    if(trustmarkRecipientIdentifier == null || trustmarkRecipientIdentifier.length === 0) {
        setDangerStatus("<b>Trustmark recipient identifier cannot be blank.</b>");
        document.getElementById('trustmarkRecipientIdentifier').focus();
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
 * @returns {function(*=): function(*=): function(*=): function(*=): *}
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

/**
 * transforms the passed in function to 5 separate argument functions
 * @param f
 * @returns {function(*=): function(*=): function(*=): function(*=): function(*=):*}
 */
let curryFive = function(f)  {
    return function(a)  {
        return function(b) {
            return function(c) {
                return function(d) {
                    return function(e) {
                        return f(a, b, c, d, e);
                    }
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

let resetStatus = renderStatus(STATUS_HEADER)(function(){return ''});

let curriedDocument = curryFour(renderDocuments);

let documentDetail = curryFive(renderDocumentForm);

let curriedContact = curryFour(renderContacts);

let curriedSystemContacts = curryFour(renderSystemContacts);

let contactDetail = curryFour(renderContactForm);

let curriedRegistrant = curryFour(renderRegistrants);

let curriedOrganization = curryFour(renderOrganizations);

let curriedEndpoint = curryFour(renderEndpoints);

let curriedTrustmark = curryThree(renderTrustmarks);

let curriedAttribute = curryFour(renderAttributes);

let curriedIdpAttribute = curryFour(renderIdpAttributes);

let curriedProtocolDetails = curryFour(renderProtocolDetails);

let curriedCertificateDetails = curryFour(renderCertificateDetails);

let curriedProvider = curryFour(renderProviders);

let curriedTag = curryFour(renderTags);

let curriedConformanceTargetTip = curryFour(renderConformanceTargetTips);

let curriedPartnerOrganizationTip = curryFour(renderPartnerOrganizationTips);

let curriedPartnerSystemsTip = curryFour(renderPartnerSystemsTips);

let curriedRepos = curryFour(renderRepos);

let repoDetail = curryFour(renderAssessmentToolReposForm);

let curriedTrustmarkRecipientIdentifier = curryFour(renderTrustmarkRecipientIdentifiers);

let trustmarkRecipientIdentifierDetail = curryFour(renderTrustmarkRecipientIdentifiersForm);

let curriedContactTypes = curryThree(renderContactTypes);

let curriedSelectOrganizations = curryThree(renderSelectOrganizations);

let curriedSelectRoles = curryThree(renderSelectRoles);

let curriedProviderTypes = curryTwo(renderProviderTypes);
