/**
 * common pagination function
 * @param offset
 * @param totalCount
 * @param fnName
 * @returns {string}
 */
let renderPagination = function(offset, totalCount, fnName)  {
    if (totalCount > MAX_DISPLAY)  {
        return buildPagination(offset, MAX_DISPLAY, totalCount, fnName);
    }
    return "";
}

let renderTagOffset = function(){};
/**
 * renders a table of tags
 * @param target
 * @param obj
 * @param data
 * @param offset
 */
let renderTags = function(target, obj, data, offset)  {
    let html = renderPagination(offset, data.length, 'renderTagOffset');
    html += "<table class='table table-condensed table-striped table-bordered'>";
    html += "<tr><td colspan='6' style='text-align: center'>";
    if(obj.editable)  {
        html += "<div class='tm-left'><a id='plus-"+target+"' title='Add a Tag'><span class='glyphicon glyphicon-plus'></span></a> / <a id='minus-"+target+"' title='Removed Checked Tags'><span class='glyphicon glyphicon-minus'></span></a></div>";
    }
    html += "<b>"+obj.title+"</b></td></tr>";
    if (data.length === 0)  {
        html += "<tr><td colspan='6'><em>There are no tags.</em></td></tr>";
    }  else {
        let idx = 0;
        html += "<tr>";
        data.forEach(o => {
            if(idx%6 > 0) {
                html += "<td><input type='checkbox' class='edit-tags' value='" + o + "'>&nbsp;"+o+"</td>";
            } else {
                html += "</tr><tr>";
                html += "<td><input type='checkbox' class='edit-tags' value='" + o + "'>&nbsp;"+o+"</td>";
            }
            ++idx;
        });
        html += "</tr>";
    }
    html += "</table>";
    document.getElementById(target).innerHTML = html;
    if(obj.editable) {
        document.getElementById('plus-' + target).onclick = obj.fnAdd;
        document.getElementById('minus-' + target).onclick = obj.fnRemove;
    }
}

let renderTrustmarkOffset = function(){};
/**
 * renders a table of trustmarks
 * @param target
 * @param data
 * @param offset
 */
let renderTrustmarks = function(target, data, offset)  {
    let html = renderPagination(offset, data.length, 'renderTrustmarkOffset');
    html += "<table class='table table-condensed table-striped table-bordered'>";
    html += "<tr><td colspan='4' style='text-align: center'><b>Trustmarks</b></td></tr>"
    html += "<tr><td style='width: auto;'></td><td style='width: auto;'><b>URL</b></td><td style='width: auto;'><b>Provisional</b></td><td style='width: auto;'><b>Status</b></td></tr>";
    if (data.length === 0)  {
        html += '<tr><td colspan="4"><em>There are no trustmarks.</em></td></tr>';
    }  else {
        let idx = 0;
        data.forEach(o => {
            if(idx >= offset && idx < offset+MAX_DISPLAY) {
                html += "<tr>";
                html += "<td><input type='checkbox' class='edit-trustmarks' value='" + o.id + "'></td>";
                html += "<td><a href='" + o.url +"' target='_blank'>"+ o.name +"</a></td>";
                html += "<td>" + (o.provisional ? "<a href='javascript:;' title='"+o.assessorComments+"'>YES</a>" : "NO") + "</td>";
                html += "<td>" + o.status + "</td>";
                html += "</tr>";
            }
            ++idx;
        });
    }
    html += "</table>";
    document.getElementById(target).innerHTML = html;
}

let renderContactOffset = function(){};
/**
 * renders a table of contacts
 * @param target
 * @param obj
 * @param data
 * @param offset
 */
let renderContacts = function(target, obj, data, offset)  {
    let html = renderPagination(offset, data.length, 'renderContactOffset');
    html += "<table class='table table-condensed table-striped table-bordered'>";
    html += "<tr><td colspan='6' style='text-align: center'>";
    if(obj.editable)  {
        html += "<div class='tm-left'><a id='plus-"+target+"' title='Add a Contact'><span class='glyphicon glyphicon-plus'></span></a> / <a id='minus-"+target+"' title='Remove Checked Contacts'><span class='glyphicon glyphicon-minus'></span></a></div>";
    }
    html += "<b>"+obj.title+"</b></td></tr>"
    if (data.length === 0)  {
        html += '<tr><td colspan="6"><em>There are no contacts.</em></td></tr>';
    }  else {
        let idx = 0;
        data.forEach(c => {
            if(idx >= offset && idx < offset+MAX_DISPLAY)  {
                html += obj.fnDraw(obj, c);
            }
            ++idx;
        });
    }
    html += "</table>";
    document.getElementById(target).innerHTML = html;
    if(obj.editable)  {
        document.getElementById('plus-'+target).onclick = obj.fnAdd;
        document.getElementById('minus-'+target).onclick = obj.fnRemove;
    }
}

let drawContacts = function(obj, entry)  {
    let html = "<tr>";
    html += "<td><input class='edit-contacts' type='checkbox' value='"+ entry.id + "'><a class='tm-right' href='"+obj.hRef+"(" + entry.id + ");'><span class='glyphicon glyphicon-pencil'></span></a></td>";
    html += "<td>" + entry.type.name + "</td>";
    html += "<td>"+ entry.lastName + "</td>";
    html += "<td>" + entry.firstName + "</td>";
    html += "<td>" + entry.email + "</td>";
    html += "<td>" + (entry.phone != null ? entry.phone : "") + "</td>";
    html += "</tr>";
    return html;
}

let renderOrganizationOffset = function(){};
/**
 * renders a table of organizations
 *
 * @param target
 * @param obj
 * @param data
 * @param offset
 */
let renderOrganizations = function(target, obj, data, offset)  {
    let html = renderPagination(offset, data.length, 'renderOrganizationOffset');
    html += "<table class='table table-condensed table-striped table-bordered'>";
    html += "<tr><td colspan='4' style='text-align: center'>";
    if(obj.editable)  {
        html += "<div class='tm-left'><a id='plus-"+target+"' title='Add an Organization'><span class='glyphicon glyphicon-plus'></span></a> / <a id='minus-"+target+"' title='Remove Checked Organizations'><span class='glyphicon glyphicon-minus'></span></a></div>";
    }
    html += "<b>"+obj.title+"</b></td></tr>"
    if (data.length === 0)  {
        html += '<tr><td colspan="4"><em>There are no organizations.</em></td></tr>';
    }  else {
        let idx = 0;
        data.forEach(o => {
            if(idx >= offset && idx < offset+MAX_DISPLAY) {
                html += obj.fnDraw(obj, o);
            }
            ++idx;
        });
    }
    html += "</table>";
    document.getElementById(target).innerHTML = html;
    if(obj.editable)  {
        document.getElementById('plus-'+target).onclick = obj.fnAdd;
        document.getElementById('minus-'+target).onclick = obj.fnRemove;
    }
}

let drawOrganizations = function(obj, entry)  {
    let html = "<tr>";
    html += "<td style='width:min-content;white-space:nowrap;'><input type='checkbox' class='edit-organizations' value='" + entry.id + "'>&nbsp;&nbsp;<a class='tm-right' href='javascript:getDetails(" + entry.id + ");'><span class='glyphicon glyphicon-pencil'></span></a></td>";
    html += "<td>" + entry.displayName + "</td>";
    html += "<td>" + entry.name + "</td>";
    html += "<td>" + entry.siteUrl + "</td>";
    html += "</tr>";
    return html;
}

let renderRegistrantOffset = function(){};
/**
 * renders a table of registrants
 *
 * @param target
 * @param fnAdd
 * @param fnRemove
 * @param data
 * @param offset
 */
let renderRegistrants = function(target, obj, data, offset)  {
    let html = renderPagination(offset, data.length, 'renderRegistrantOffset');
    html += "<table class='table table-condensed table-striped table-bordered'>";
    html += "<tr><td colspan='5' style='text-align: center'>";
    if(obj.editable)  {
        html += "<div class='tm-left'><a id='plus-"+target+"' title='Add a Registrant'><span class='glyphicon glyphicon-plus'></span></a> / <a id='minus-"+target+"' title='Remove Checked Registrants'><span class='glyphicon glyphicon-minus'></span></a></div>";
    }
    html += "<b>"+obj.title+"</b></td></tr>";
    html += "<tr><td style='width: auto;'></td><td style='width: auto;'>Name</td><td style='width: auto;'>Email</td><td style='width: auto;'>Phone</td><td style='width: auto;'>Organization</td></tr>";
    if (data.length === 0)  {
        html += '<tr><td colspan="5"><em>There are no registrants.</em></td></tr>';
    }  else {
        let idx = 0;
        data.forEach(r => {
            if( idx >= offset && idx < offset+MAX_DISPLAY) {
                html += obj.fnDraw(obj, r);
            }
            ++idx;
        });
    }
    html += "</table>";
    document.getElementById(target).innerHTML = html;
    if(obj.editable)  {
        document.getElementById('plus-'+target).onclick = obj.fnAdd;
        document.getElementById('minus-'+target).onclick = obj.fnRemove;
    }
}

let drawRegistrants = function(obj, entry)  {
    let html = "<tr>";
    if (entry.user.enabled === true) {
        html += "<td style='width:auto;'><input type='checkbox' class='deactivate' value='" + entry.id + "'>&nbsp;ACTIVE</td>";
    } else {
        html += "<td style='width:auto;'><input type='checkbox' class='activate' value='" + entry.id + "'>&nbsp;INACTIVE</td>";
    }
    html += "<td>" + entry.contact.lastName + ", " + entry.contact.firstName + "&nbsp;<a class='tm-right' href='javascript:getDetails(" + entry.id + ");'><span class='glyphicon glyphicon-pencil'></span></a></td>";
    html += "<td>" + entry.contact.email + "</td>";
    html += "<td>" + (entry.contact.phone != null ? entry.contact.phone : "") + "</td>";
    html += "<td>" + entry.organization.name + "</td>";
    html += "</tr>";

    return html;
}

let renderEndpointOffset = function(){};
/**
 * renders a table of endpoints
 *
 * @param target
 * @param obj
 * @param data
 * @param offset
 */
let renderEndpoints = function(target, obj, data, offset)  {
    let html = renderPagination(offset, data.length, 'renderEndpointtOffset');
    html += "<table class='table table-condensed table-striped table-bordered'>";
    html += "<tr><td colspan='4' style='text-align: center'>";
    if(obj.editable)  {
        html += "<div class='tm-left'><a id='plus-"+target+"' title='Add an Endpoint'><span class='glyphicon glyphicon-plus'></span></a> / <a id='minus-"+target+"' title='Remove Checked Endpoints'><span class='glyphicon glyphicon-minus'></span></a></div>";
    }
    html += "<b>"+obj.title+"</b></td></tr>"
    if (data.length === 0)  {
        html += '<tr><td colspan="4"><em>There are no endpoints.</em></td></tr>';
    }  else {
        let idx = 0;
        data.forEach(o => {
            if(idx >= offset && idx < offset+MAX_DISPLAY) {
                html += obj.fnDraw(obj, o);
            }
            ++idx;
        });
    }
    html += "</table>";
    document.getElementById(target).innerHTML = html;
    if(obj.editable)  {
        document.getElementById('plus-'+target).onclick = obj.fnAdd;
        document.getElementById('minus-'+target).onclick = obj.fnRemove;
    }
}

let drawEndpoints = function(obj, entry)  {
    let html = "<tr>";
    html += "<td><input type='checkbox' class='edit-endpoints' value='" + entry.id + "'></td>";
    if(entry.name === "Attribute Consuming Service")  {
        html += "<td>" + entry.name + "</td>";
        html += "<td>" + entry.serviceName + "</td>";
        let names = "";
        entry.attributes.forEach(a => { if(a.name !== 'ServiceName') names += a.name+"<br>";});
        html += "<td>" + names + "</td>";
    }  else  {
        html += "<td>" + entry.name + "</td>";
        html += "<td>" + entry.binding.substring(entry.binding.lastIndexOf(":")+1) + "</td>";
        html += "<td>" + entry.url + "</td>";
    }
    html += "</tr>";
    return html;
}

let renderAttributeOffset = function(){};
/**
 * renders a table of attributes
 *
 * @param target
 * @param obj
 * @param data
 * @param offset
 */
let renderAttributes = function(target, obj, data, offset)  {
    let html = renderPagination(offset, data.length, 'renderAttributeOffset');
    html += "<table class='table table-condensed table-striped table-bordered'>";
    html += "<tr><td colspan='3' style='text-align: center'>";
    if(obj.editable)  {
        html += "<div class='tm-left'><a id='plus-"+target+"' title='Add an Attribute'><span class='glyphicon glyphicon-plus'></span></a> / <a id='minus-"+target+"' title='Remove Checked Attributes'><span class='glyphicon glyphicon-minus'></span></a></div>"
    }
    html += "<b>"+obj.title+"</b></td></tr>";
    if (data.length === 0)  {
        html += '<tr><td colspan="3"><em>There are no attributes.</em></td></tr>';
    }  else {
        let idx = 0;
        data.forEach(o => {
            if(idx >= offset && idx < offset+MAX_DISPLAY) {
                html += obj.fnDraw(obj, o);
            }
            ++idx;
        });
    }
    html += "</table>";
    document.getElementById(target).innerHTML = html;
    if(obj.editable)  {
        document.getElementById('plus-'+target).onclick = obj.fnAdd;
        document.getElementById('minus-'+target).onclick = obj.fnRemove;
    }
}

let drawAttribute = function (obj, entry)  {
    let html = "<tr>";
    html += "<td><input type='checkbox' class='edit-attributes' value='" + entry.id + "'></td>";
    html += "<td><b>" + entry.name + "</b></td>";
    html += "<td>" + entry.value + "</td>";
    html += "</tr>";
    return html;
}
let renderProviderOffset = function(){};
/**
 * renders a table of registrants
 *
 * @param target
 * @param phref
 * @param data
 * @param offset
 */
let renderProviders = function(target, phref, data, offset)  {
    let html = renderPagination(offset, data.length, 'renderProviderOffset');
    html += "<table class='table table-condensed table-striped table-bordered'>";
    html += "<tr><td colspan='4' style='text-align: center'><b>Systems</b></td></tr>"
    if (data.length === 0)  {
        html += '<tr><td colspan="4"><em>There are no providers.</em></td></tr>';
    }  else {
        let idx = 0;
        data.forEach(p => {
            if( idx >= offset && idx < offset+MAX_DISPLAY) {
                html += "<tr>";
                html += "<td style='width:auto;'><input type='checkbox' class='edit-providers' value='" + p.id + "'>";
                html += "<td><a href='"+phref+"/"+p.id+"'>" + p.name + "</a></td>";
                html += "<td>" + p.entityId + "</td>";
                html += "<td>" + p.providerType.name + "</td>";
                html += "</tr>";
            }
            ++idx;
        });
    }
    html += "</table>";
    document.getElementById(target).innerHTML = html;
}

let renderRepoOffset = function(){};
/**
 * render the repos in a tabular form
 * @param target
 * @param data
 * @param offset
 */
let renderRepos = function(target, data, offset)  {
    let html = renderPagination(offset, data.length, 'renderProviderOffset');
    html += "<table class='table table-condensed table-striped table-bordered'>";
    if (data.length === 0)  {
        html += '<tr><td colspan="2"><em>There are no repos.</em></td></tr>';
    }  else {
        let idx = 0;
        data.forEach(p => {
            if( idx >= offset && idx < offset+MAX_DISPLAY) {
                html += "<tr>";
                html += "<td>" + p.repoUrl + "</td>";
                html += "<td style='width:auto;'>";
                html += "<a href='#' onclick=\"deleteRepo("+p.id+");\"><span class='glyphicon glyphicon-minus'></span></a>";
                html += "</td>";
                html += "</tr>";
            }
            ++idx;
        });
    }
    html += "<tr>";
    html += "<td><input id='new-repo' type='text' size='30' placeholder='Enter Assessment Tool URL' value=''></td>";
    html += "<td style='width:auto;'>";
    html += "<a href='#' onclick=\"addRepo(getElementById('new-repo').value);\"><span class='glyphicon glyphicon-plus'></span></a>";
    html += "</td>";
    html += "</tr>";
    html += "</table>";
    document.getElementById(target).innerHTML = html;
}


let renderTrustmarkRecipientIdentifiersOffset = function(){};
/**
 * render the trustmark recipient identifiers in a tabular form
 * @param target
 * @param data
 * @param offset
 */
let renderTrustmarkRecipientIdentifiers = function(target, data, offset)  {
    let html = renderPagination(offset, data.length, 'renderTrustmarkRecipientIdentifiersOffset');
    html += "<table class='table table-condensed table-striped table-bordered'>";
    if (data.length === 0)  {
        html += '<tr><td colspan="2"><em>There are no trustmark recipient identifiers.</em></td></tr>';
    }  else {
        let idx = 0;
        data.forEach(p => {
            if( idx >= offset && idx < offset+MAX_DISPLAY) {
                html += "<tr>";
                html += "<td>" + p.trustmarkRecipientIdentifierUrl + "</td>";
                html += "<td style='width:auto;'>";
                html += "<a href='#' onclick=\"deleteTrustmarkRecipientIdentifier("+p.id+");\"><span class='glyphicon glyphicon-minus'></span></a>";
                html += "</td>";
                html += "</tr>";
            }
            ++idx;
        });
    }
    html += "<tr>";
    html += "<td><input id='new-trustmarkRecipientIdentifier' type='text' size='30' placeholder='Enter Trustmark Recipient Identifier' value=''></td>";
    html += "<td style='width:auto;'>";
    html += "<a href='#' onclick=\"addTrustmarkRecipientIdentifier(getElementById('new-trustmarkRecipientIdentifier').value);\"><span class='glyphicon glyphicon-plus'></span></a>";
    html += "</td>";
    html += "</tr>";
    html += "</table>";
    document.getElementById(target).innerHTML = html;
}


let renderTabs = function(data)  {
    let html = "<ul class='nav nav-tabs' id='org-tab-list' role='tablist'>";
    data.forEach(pr => {
        html += "<li class='nav-item'>";
        html += "<a class='nav-link' onclick='showProvider('"+pr.id+"');' id='"+pr.id+"-tab' data-toggle='tab' role='tab' href='#"+pr.id+"' aria-controls='"+pr.id+"'>"+pr.name+"</a>";
        html += "</li>";
    });
    html += "<li class='nav-item'>";
    html += "<a class='nav-link' id='plus-tab' onclick='addProvider('#new-provider');' data-toggle='tab' role='tab' href='#plus-id aria-controls='plus-id'>+</a>";
    html += "</li></ul>";
    return html;
}

let renderConformanceTargetTipOffset = function(){};
/**
 * renders a table of Conformance Target Tips
 *
 * @param target
 * @param obj
 * @param data
 * @param offset
 */
let renderConformanceTargetTips = function(target, obj, data, offset)  {

    let html = renderPagination(offset, data.length, 'renderConformanceTargetTipOffset');
    html += "<table class='table table-condensed table-striped table-bordered'>";
    html += "<tr><td colspan='5' style='text-align: center'>";
    if(obj.editable)  {
        html += "<div class='tm-left'><a id='plus-"+target+"' title='Add a Conformance Target TIP Identifier'><span class='glyphicon glyphicon-plus'></span></a> / <a id='minus-"+target+"' title='Remove Checked Conformance Target TIP Identifiers'><span class='glyphicon glyphicon-minus'></span></a></div>";
    }
    html += "<b>"+obj.title+"</b></td></tr>"

    if (data.length === 0)  {
        html += '<tr><td colspan="5"><em>There are no Conformance Target TIP Identifiers.</em></td></tr>';
    }  else {
        let idx = 0;
        data.forEach(o => {
            if(idx >= offset && idx < offset+MAX_DISPLAY) {
                html += obj.fnDraw(obj, o);
            }
            ++idx;
        });
    }
    html += "</table>";

    document.getElementById(target).innerHTML = html;
    if(obj.editable)  {
        document.getElementById('plus-'+target).onclick = obj.fnAdd;
        document.getElementById('minus-'+target).onclick = obj.fnRemove;
    }
}

let drawConformanceTargetTips = function(obj, entry)  {
    let html = "<tr>";
    html += "<td><input type='checkbox' class='edit-conformanceTargetTips' value='" + entry.id + "'></td>";
    html += "<td>" + entry.conformanceTargetTipIdentifier + "</td>";
    html += "</tr>";

    return html;
}

/**
 * hide the passed in div
 * @param target
 */
let hideIt = function(target)  {
    if(target.startsWith('#'))  {
        document.getElementById(target.substring(1)).style.display = 'none';
    } else {
        document.getElementById(target).style.display = 'none';
    }
}

/**
 * hide the passed in div
 * @param target
 */
let showIt = function(target)  {
    if(target.startsWith('#'))  {
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
let toggleIt = function(target)  {
    if(target.startsWith('#')) {
        target = document.getElementById(target.substring(1));
    }
    if(document.getElementById(target).style.display === 'none')  {
        document.getElementById(target).style.display = 'block';
    } else {
        document.getElementById(target).style.display = 'none';
    }
    return false;
}

/**
 * render a form for adding a tag
 */
let renderTagForm = function(target, fn)  {
    let html = "<input id='tagName' type='text' class='form-control tm-margin' size='40' placeholder='Enter Tag Name' /><span style='color:red;'>*</span><br>";
    html += "<button id='tagOk' type='button' class='btn btn-info tm-margin'>Add</button>";
    renderDialogForm(target, html);
    document.getElementById('tagOk').onclick = fn;
    document.getElementById('tagName').focus();
}

/**
 * render a form for adding an attribute
 */
let renderAttributeForm = function(target, fn)  {
    let html = "<input id='attrName' size='40' type='text' class='form-control tm-margin' placeholder='Enter Name' /><span style='color:red;'>*</span><br>";
    html += "<input id='attrValue' size='40' type='text' class='form-control tm-margin' placeholder='Enter Value' /><span style='color:red;'>*</span><br>";
    html += "<button id='attrOk' type='button' class='btn btn-info tm-margin'>Add</button>";
    renderDialogForm(target, html);
    document.getElementById('attrOk').onclick = fn;
    document.getElementById('attrName').focus();
}

/**
 * render a form for adding an endpoint
 */
let renderEndpointForm = function(target, fn)  {
    let html = "<input id='endptName' size='40' type='text' class='form-control tm-margin' placeholder='Enter Name' /><span style='color:red;'>&nbsp;&nbsp;*</span><br>";
    html += "<input id='endptType' size='40' type='text' class='form-control tm-margin' placeholder='Enter Type' /><span style='color:red;'>&nbsp;&nbsp;*</span><br>";
    html += "<input id='endptUrl' size='40' type='text' class='form-control tm-margin' placeholder='Enter URL' /><span style='color:red;'>&nbsp;&nbsp;*</span><br>";
    html += "<button id='endptOk' type='button' class='btn btn-info tm-margin'>Add</button>";
    renderDialogForm(target, html);
    document.getElementById('endptOk').onclick = fn;
    document.getElementById('endptName').focus();
}

/**
 * render a form for adding a conformance target tip
 */
let renderConformanceTargetTipForm = function(target, fn)  {
    let html = "<input id='conformanceTargetTipIdentifier' size='80' type='text' class='form-control tm-margin' placeholder='Enter Conformance Target TIP Identifier' /><span style='color:red;'>&nbsp;&nbsp;*</span><br>";
    html += "<button id='conformanceTargetTipIdentifierOk' type='button' class='btn btn-info tm-margin'>Add</button>";
    renderDialogForm(target, html);
    document.getElementById('conformanceTargetTipIdentifierOk').onclick = fn;
    document.getElementById('conformanceTargetTipIdentifier').focus();
}

/**
 * render a form for adding an endpoint
 */
let renderProviderForm = function(target, fn)  {
    let html = "<div class='tm-margin' id='select-provider-types'></div><br>";
    html += "<input size='40' id='providerName' type='text' class='form-control tm-margin' placeholder='Enter System Name' /><span style='color:red;'>*</span><br>";
    html += "<input size='40' id='providerEntityId' type='text' class='form-control tm-margin' placeholder='Enter Entity ID'/><span style='color:red;'>*</span><br>";
    html += "<button id='providerOk' type='button' class='btn btn-info tm-margin'>Add</button>";
    renderDialogForm(target, html);
    document.getElementById('providerOk').onclick = fn;
}

/**
 * render a form for entering an organization
 * @param target
 * @param fn
 */
let renderOrganizationForm = function(target, fn, org)  {
    let html = "<input type='text' class='tm-margin' size='40' id='org_name' placeholder='Enter Organization Name'><span style='color:red;'>*</span><br>";
    html += "<input type='text' class='tm-margin' size='40' id='org_display' placeholder='Enter Organization Display Name'><span style='color:red;'>*</span><br>";
    html += "<input type='text' class='tm-margin' size='40' id='org_url' placeholder='Enter Organization URL'><span style='color:red;'>*</span><br>";
    html += "<textarea class='tm-margin' cols='50' rows='6' id='org_desc' placeholder='Enter Organization Description'></textarea><span style='color:red;'>*</span><br>";
    html += "<button id='orgOk' type='button' class='btn btn-info tm-margin'>Save</button>";
    renderDialogForm(target, html);
    document.getElementById('org_name').focus();
    document.getElementById('orgOk').onclick = fn;
    if(org.id !== 0)  {
        document.getElementById('org_name').value = org.name;
        document.getElementById('org_display').value = org.displayName;
        document.getElementById('org_url').value = org.siteUrl;
        document.getElementById('org_desc').value = org.description;
    }
    document.getElementById('org_name').focus();
}

/**
 * render a form for adding a contact
 */
let renderContactForm = function(target, preFn, fn, contact)  {
    let html = "";
    html += "<div class='tm-margin' id='select-organization'></div><br>";
    html += "<div class='tm-margin' id='select-contact-types'></div><br>";
    html += "<input id='lastName' type='text' size='40' class='form-control tm-margin' placeholder='Enter Last Name'/><span style='color:red;'>*</span><br>";
    html += "<input id='firstName' type='text' size='40' class='form-control tm-margin' placeholder='Enter First Name'/><span style='color:red;'>*</span><br>";
    html += "<input id='phoneNbr' type='text' size='16' class='form-control tm-margin' placeholder='Enter Phone Number'/><br>";
    html += "<input id='emailAddr' type='text' size='40' class='form-control tm-margin' placeholder='Enter Email Address'/><span style='color:red;'>*</span><br>";
    html += "<button id='contactOk' type='button' class='btn btn-info tm-margin'>Add</button>";
    renderDialogForm(target, html);
    document.getElementById('contactOk').onclick = fn;
    preFn(contact);
}

/**
 * renders a form for updating a registrant's data
 * @param target
 * @param fn
 * @param registrant
 */
let renderRegistrantForm = function(target, fn, registrant) {
    let html = "<div class='tm-margin' id='select-organization'></div><br>";
    html += "<input class='form-control tm-margin' type='text' size='40' id='detail_lastName' placeholder='Enter Last Name'><span style='color:red;'>*</span><br>";
    html += "<input class='form-control tm-margin' type='text' size='30' id='detail_firstName' placeholder='Enter First Name'><span style='color:red;'>*</span><br>";
    html += "<input class='form-control tm-margin' type='text' size='40' id='detail_email' placeholder='Enter Email Address'><span style='color:red;'>*</span><br>";
    html += "<input class='form-control tm-margin' type='text' size='40' id='detail_phone' placeholder='Enter Phone Number'><br>";
    html += "<button id='registrantOk' type='button' class='btn btn-info tm-margin'>Save</button>";
    renderDialogForm(target, html);
    document.getElementById('registrantOk').onclick = fn;
    if(registrant.id === 0)  {
        selectOrganizations(0);
    } else {
        selectOrganizations(registrant.organization.id);
        document.getElementById('detail_lastName').value = registrant.contact.lastName;
        document.getElementById('detail_firstName').value = registrant.contact.firstName;
        document.getElementById('detail_email').value = registrant.contact.email;
        document.getElementById('detail_phone').value = registrant.contact.phone;
    }
    document.getElementById('detail_lastName').focus();
}

/**
 * renders content into a standard dialog with a close X
 * @param target
 * @param content
 */
let renderDialogForm = function(target, content)  {
    let html = "<form class='form-inline'>";
    html += "<div class='tm-form form-group'>";
    html += "<a class='tm-margin tm-right' href=\"javascript:hideIt('"+target+"');\"><span class='glyphicon glyphicon-remove'></span></a><br>";
    html += content;
    html += "</div></form>";
    document.getElementById(target).innerHTML = html;
    showIt(target);
}
