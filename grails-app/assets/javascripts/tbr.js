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

let renderProtocolDetailsOffset = function(){};
/**
 * renders a table of Protocol Details
 *
 * @param target
 * @param obj
 * @param data
 * @param offset
 */
let renderProtocolDetails = function(target, obj, data, offset) {

    let html = renderPagination(offset, data.records.length, 'renderProtocolDetailsOffset');
    html += "<table class='table table-condensed table-striped table-bordered'>";
    html += "<tr><td colspan='2' style='text-align: center'>";

    html += "<b>" + obj.title + "</b></td></tr>"

    if (data.records.length === 0) {
        html += '<tr><td colspan="2"><em>There are no Protocol Details.</em></td></tr>';
    } else {
        html += obj.fnDraw(obj, data.records);
    }
    html += "</table>";

    // The element might be hidden due to logged in and/or roles privileges
    if (document.getElementById(target) != null) {
        document.getElementById(target).innerHTML = html;
    }
}

let drawProtocolDetails = function(obj, entry)  {
    let html = "<tr>";
    html += "<td style='width: auto;'><b>System Type</b></td>";
    html += "<td style='width: auto;'>" + entry.systemType + "</td>";
    html += "</tr>";

    if (entry.entityId && entry.entityId.length > 0) {
        html += "<tr>";
        html += "<td style='width: auto;'><b>Entity ID</b></td>";
        html += "<td style='width: auto;'>" + entry.entityId + "</td>";
        html += "</tr>";

        html += "<tr>";
        html += "<td style='width: auto;'><b>Name ID Format</b></td>";
        html += "<td style='width: auto;'>"
        for (var i = 0; i< entry.nameIdFormats.length; i++) {
            html += entry.nameIdFormats[i]+"<br/>";
        }
        html += "</td>";
        html += "</tr>";

        html += "<tr>";
        html += "<td style='width: auto;'><b>Signing Certificate</b></td>";
        html += "<td style='width: auto;'><a href='" + entry.signingCertificateLink + "' target='_blank'>view</a></td>";
        html += "</tr>";

        html += "<tr>";
        html += "<td style='width: auto;'><b>Encrypting Certificate</b></td>";
        html += "<td style='width: auto;'><a href='" + entry.encryptionCertificateLink + "' target='_blank'>view</a></td>";
        html += "</tr>";

        html += "<tr>";
        html += "<td style='width: auto;'><b>SAML 2 Metadata</b></td>";

        html += "<td style='width: auto;'>";
        if (obj.editable) {
            html += "<a href='javascript:generateSaml2Metadata(" + entry.providerId + ")'>generate</a>";
            html += "<span id='saml2-metadata-generation_status' style='width:10%;'></span>"
        }
        if (entry.hasSamlMetadataGenerated) {
            html += " <a href='" + entry.viewSamlMetadataLink + "' id='viewMetadataLink' target='_blank'>view</a>";
        } else {
            html += " <a href='" + entry.viewSamlMetadataLink + "' id='viewMetadataLink' target='_blank' class='disabledLink'>view</a>";
        }

        if (entry.hasSamlMetadataGenerated) {
            html += "<div id='saml2-metadata-generation_date' class='alert alert-success' style='padding: 0px 10px;'>Generated at " + entry.lastTimeSAMLMetadataGeneratedDate + "</div>";
        } else {
            html += "<div id='saml2-metadata-generation_date' class='alert alert-success' style='padding: 0px 10px;></div>";
        }

        html += "</td>";

        html += "</tr>";
    }

    return html;
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
    let html = renderPagination(offset, data.records.length, 'renderTagOffset');
    html += "<table class='table table-condensed table-striped table-bordered'>";
    html += "<tr><td colspan='6' style='text-align: center'>";
    if(obj.editable)  {
        html += "<div class='tm-left'><a id='plus-"+target+"' title='Add a Tag'><span class='glyphicon glyphicon-plus'></span></a> / <a id='minus-"+target+"' title='Removed Checked Tags'><span class='glyphicon glyphicon-minus'></span></a></div>";
    }
    html += "<b>"+obj.title+"</b></td></tr>";
    if (data.records.length === 0)  {
        html += "<tr><td colspan='6'><em>There are no tags.</em></td></tr>";
    }  else {
        let idx = 0;
        html += "<tr>";
        data.records.forEach(o => {
            if(idx%6 > 0) {
                if(obj.editable) {
                    html += "<td><input type='checkbox' class='edit-tags' value='" + o + "'>&nbsp;" + o + "</td>";
                } else {
                    html += "<td><span>&nbsp;" + o + "</span></td>";
                }
            } else {
                html += "</tr><tr>";
                if(obj.editable) {
                    html += "<td><input type='checkbox' class='edit-tags' value='" + o + "'>&nbsp;" + o + "</td>";
                } else {
                    html += "<td><span>&nbsp;" + o + "</span></td>";
                }
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
    html += "<tr><td colspan='3' style='text-align: center'><b>Trustmarks</b></td></tr>"
    html += "<tr><td style='width: auto;'><b>Name</b></td><td style='width: auto;'><b>Provisional</b></td><td style='width: auto;'><b>Status</b></td></tr>";
    if (data.length === 0)  {
        html += '<tr><td colspan="3"><em>There are no trustmarks.</em></td></tr>';
    }  else {
        let idx = 0;
        data.forEach(o => {
            if(idx >= offset && idx < offset+MAX_DISPLAY) {
                html += "<tr>";
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
    let html = renderPagination(offset, data.records.length, 'renderContactOffset');
    html += "<table class='table table-condensed table-striped table-bordered'>";

    if(obj.editable)  {
        html += "<tr><td colspan='6' style='text-align: center'>";
        html += "<div class='tm-left'><a id='plus-"+target+"' title='Add a Contact'><span class='glyphicon glyphicon-plus'></span></a> / <a id='minus-"+target+"' title='Remove Checked Contacts'><span class='glyphicon glyphicon-minus'></span></a></div>";
    } else {
        html += "<tr><td colspan='5' style='text-align: center'>";
    }
    html += "<b>"+obj.title+"</b></td></tr>"
    if (data.records.length === 0)  {
        if(obj.editable) {
            html += '<tr><td colspan="6"><em>There are no contacts.</em></td></tr>';
        } else {
            html += '<tr><td colspan="5"><em>There are no contacts.</em></td></tr>';
        }
    }  else {
        let idx = 0;
        data.records.forEach(c => {
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
    if(obj.editable) {
        html += "<td><input class='edit-contacts' type='checkbox' value='" + entry.id + "'><a class='tm-right' href='" + obj.hRef + "(" + entry.id + ");'><span class='glyphicon glyphicon-pencil'></span></a></td>";
    }

    html += "<td>"+ entry.lastName + "</td>";
    html += "<td>" + entry.firstName + "</td>";
    html += "<td>" + entry.email + "</td>";
    html += "<td>" + (entry.phone != null ? entry.phone : "") + "</td>";
    html += "<td><a href=" + ORG_VIEW_BASE_URL + entry.organization.id+">" + entry.organization.name + "</a></td>"
    html += "</tr>";
    return html;
}


let renderDocumentOffset = function(){};
/**
 * renders a table of documents
 * @param target
 * @param obj
 * @param data
 * @param offset
 */
let renderDocuments = function(target, obj, data, offset)  {
    let html = renderPagination(offset, data.records.length, 'renderDocumentOffset');
    html += "<table class='table table-condensed table-striped table-bordered'>";

    if(obj.editable)  {
        html += "<tr><td colspan='6' style='text-align: center'>";
        html += "<div class='tm-left'><a id='plus-"+target+"' title='Add a Document'><span class='glyphicon glyphicon-plus'></span></a> / <a id='minus-"+target+"' title='Remove Checked Documents'><span class='glyphicon glyphicon-minus'></span></a></div>";
    } else {
        html += "<tr><td colspan='5' style='text-align: center'>";
    }
    html += "<b>"+obj.title+"</b></td></tr>"
    if (data.records.length === 0)  {
        if(obj.editable) {
            html += '<tr><td colspan="6"><em>There are no documents.</em></td></tr>';
        } else {
            html += '<tr><td colspan="5"><em>There are no documents.</em></td></tr>';
        }
    }  else {
        // Table header
        html += "<th></th>";
        html += "<th>Document Name</th>";
        html += "<th>URL</th>";
        html += "<th>Description</th>";
        html += "<th>Public</th>";

        let idx = 0;
        data.records.forEach(c => {
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

let drawDocuments = function(obj, entry)  {
    let html = "<tr>";
    if(obj.editable) {
        html += "<td><input id='edit-document' class='edit-documents' type='checkbox' value='" + entry.id + "' binaryUrl='" + obj.binaryUrl + "'>" +
            "<a class='tm-right' href='" + obj.hRef + "(" + entry.id + ");'><span class='glyphicon glyphicon-pencil'></span></a></td>";
    }

    html += "<td>"+ entry.filename + "</td>";
    html += "<td><a href=" + entry.url + "><span>" + entry.url + "</span></a></td>";
    html += "<td>" + entry.description + "</td>";
    if (entry.publicDocument) {
        html += "<td><span class='glyphicon glyphicon-ok'></span></td>";
    } else {
        html += "<td><span class='glyphicon glyphicon-remove'></span></td>";
    }
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
    html += "<tr><td colspan='5' style='text-align: center'>";

    if(obj.editable && LOGGED_IN)  {
        html += "<div class='tm-left'><a id='plus-"+target+"' title='Add an Organization'><span class='glyphicon glyphicon-plus'></span></a> / <a id='minus-"+target+"' title='Remove Checked Organizations'><span class='glyphicon glyphicon-minus'></span></a></div>";
    }
    html += "<b>"+obj.title+"</b></td></tr>"
    if (data.length === 0)  {
        html += '<tr><td colspan="5"><em>There are no organizations.</em></td></tr>';
    }  else {
        // table header
        html += "<tr><th style='width: auto;'></th>";
        html += "<th style='width: auto;'>Name</th>";
        html += "<th style='width: auto;'>URL</th>";
        html += "<th style='width: auto;'>System Count</th></tr>";

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
    if(obj.editable && LOGGED_IN)  {
        document.getElementById('plus-'+target).onclick = obj.fnAdd;
        document.getElementById('minus-'+target).onclick = obj.fnRemove;
    }
}

let drawOrganizations = function(obj, entry)  {
    let html = "<tr>";

    html += "<td style='width:min-content;white-space:nowrap;'>";
    if (LOGGED_IN) {
        html += "<input type='checkbox' class='edit-organizations' value='" + entry.id + "'>" +
            "<a class='tm-right' href='javascript:getDetails(" + entry.id + ");'><span class='glyphicon glyphicon-pencil'></span></a>";
    } else {
        html += "&nbsp";
    }
    html += "</td>";

    html += "<td><a href=" + ORG_VIEW_BASE_URL + entry.id+">" + entry.name + "</a></td>";
    html += "<td><a href='" + entry.siteUrl + "' target='_blank'>" + entry.siteUrl + "</a></td>";
    html += "<td id='system_count'>" + entry.providers.length + "</td>";
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
    if (data.length > 0) {
        let html = renderPagination(offset, data.length, 'renderEndpointtOffset');
        html += "<table class='table table-condensed table-striped table-bordered'>";
        html += "<tr><td colspan='3' style='text-align: center'>";

        html += "<b>" + obj.title + "</b></td></tr>"
        if (data.length === 0) {
            html += '<tr><td colspan="3"><em>There are no endpoints.</em></td></tr>';
        } else {
            let idx = 0;
            data.forEach(o => {
                if (idx >= offset && idx < offset + MAX_DISPLAY) {
                    html += obj.fnDraw(obj, o);
                }
                ++idx;
            });
        }
        html += "</table>";

        // The element might be hidden due to logged in and/or roles privileges
        if (document.getElementById(target) != null) {
            document.getElementById(target).innerHTML = html;
        }
    }
}

let drawEndpoints = function(obj, entry)  {
    let html = "<tr>";
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
    let html = renderPagination(offset, data.records.length, 'renderAttributeOffset');
    html += "<table class='table table-condensed table-striped table-bordered'>";

    if(obj.editable)  {
        html += "<tr><td colspan='3' style='text-align: center'>";
        html += "<div class='tm-left'><a id='plus-"+target+"' title='Add an Attribute'><span class='glyphicon glyphicon-plus'></span></a> / <a id='minus-"+target+"' title='Remove Checked Attributes'><span class='glyphicon glyphicon-minus'></span></a></div>"
    } else {
        html += "<tr><td colspan='2' style='text-align: center'>";
    }
    html += "<b>"+obj.title+"</b></td></tr>";
    if (data.records.length === 0)  {
        html += '<tr><td colspan="3"><em>There are no attributes.</em></td></tr>';
    }  else {
        let idx = 0;
        data.records.forEach(o => {
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
    if (obj.editable) {
        html += "<td><input type='checkbox' class='edit-attributes' value='" + entry.id + "'></td>";
    }
    html += "<td><b>" + entry.name + "</b></td>";
    html += "<td>" + entry.value + "</td>";
    html += "</tr>";
    return html;
}

let renderIdpAttributeOffset = function(){};
/**
 * renders a table of Idp attributes
 *
 * @param target
 * @param obj
 * @param data
 * @param offset
 */
let renderIdpAttributes = function(target, obj, data, offset)  {
    console.log("renderIdpAttributes target: " + target);

    if (data.length > 0)  {
        let html = renderPagination(offset, data.length, 'renderIdpAttributeOffset');
        html += "<table class='table table-condensed table-striped table-bordered'>";
        html += "<tr><td colspan='3' style='text-align: center'>";

        html += "<b>"+obj.title+"</b></td></tr>";
        let idx = 0;
        data.forEach(o => {
            if(idx >= offset && idx < offset+MAX_DISPLAY) {
                html += obj.fnDraw(obj, o);
            }
            ++idx;
        });

        html += "</table>";

        document.getElementById(target).innerHTML = html;
    }
}

let drawIdpAttribute = function (obj, entry)  {
    let html = "<tr>";
    html += "<td><b>" + entry + "</b></td>";
    html += "</tr>";
    return html;
}

let renderProviderOffset = function(){};
/**
 * renders a table of provider systems
 *
 * @param target
 * @param obj
 * @param data
 * @param offset
 */

let renderProviders = function(target, obj, data, offset)  {
    let html = renderPagination(offset, data.records.length, 'renderProviderOffset');
    html += "<table class='table table-condensed table-striped table-bordered'>";

    if(obj.editable)  {
        html += "<tr><td colspan='3' style='text-align: center'>";
        html += "<div class='tm-left'><a id='plus-"+target+"' title='Add System'><span class='glyphicon glyphicon-plus'></span></a> / <a id='minus-"+target+"' title='Remove Checked System'><span class='glyphicon glyphicon-minus'></span></a></div>";
    } else {
        html += "<tr><td colspan='2' style='text-align: center'>";
    }
    html += "<b>"+obj.title+"</b></td></tr>"
    if (data.records.length === 0)  {
        html += '<tr><td colspan="3"><em>There are no provider systems.</em></td></tr>';
    }  else {
        // table header
        if(obj.editable) {
            html += "<tr><th style='width: auto;'></th>";
        }
        html += "<th style='width: auto;'>Name</th>";
        html += "<th style='width: auto;'>Type</th></tr>";

        let idx = 0;
        data.records.forEach(o => {
            if(idx >= offset && idx < offset+MAX_DISPLAY) {
                html += obj.fnDraw(obj, o, data.providerBaseUrl);
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

let drawProvider = function(obj, entry, baseHref)  {
    let html = "<tr>";


    if (obj.editable) {
        html += "<td style='width:min-content;white-space:nowrap;'>";
        html += "<input type='checkbox' class='edit-providers' value='" + entry.id + "'>";
        html += "</td>";
    }

    html += "<td><a href=" + baseHref + "/" + entry.id+">" + entry.name + "</a></td>";
    html += "<td>" + entry.providerType + "</td>";
    html += "</tr>";

    return html;
}

let renderRepoOffset = function(){};
/**
 * render the repos in a tabular form
 * @param target
 * @param obj
 * @param data
 * @param offset
 */
let renderRepos = function(target, obj, data, offset)  {
    let html = renderPagination(offset, data.records.length, 'renderRepoOffset');
    html += "<table class='table table-condensed table-striped table-bordered'>";

    if(obj.editable)  {
        html += "<tr><td colspan='2' style='text-align: center'>";
        html += "<div class='tm-left'><a id='plus-"+target+"' title='Add Repo'><span class='glyphicon glyphicon-plus'></span></a> / <a id='minus-"+target+"' title='Remove Checked Repo'><span class='glyphicon glyphicon-minus'></span></a></div>";
    } else {
        html += "<tr><td colspan='1' style='text-align: center'>";
    }
    html += "<b>"+obj.title+"</b></td></tr>"
    if (data.records.length === 0)  {
        html += '<tr><td colspan="2"><em>There are no Assessment Tool URLs.</em></td></tr>';
    }  else {
        let idx = 0;
        data.records.forEach(o => {
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

let drawRepos = function(obj, entry)  {
    let html = "<tr>";
    if(obj.editable) {
        html += "<td><input type='checkbox' id='edit-assessmentToolRepo' class='edit-assessmentToolRepo' value='" + entry.id + "'>" +
            "<a class='tm-right' href='javascript:getRepoDetails(" + entry.id + ");'><span class='glyphicon glyphicon-pencil'></span></a></td>";
    }

    html += "<td>" + entry.repoUrl + "</td>";
    html += "</tr>";

    return html;
}


let renderTrustmarkRecipientIdentifiersOffset = function(){};
/**
 * render the trustmark recipient identifiers in a tabular form
 * @param target
 * @param obj
 * @param data
 * @param offset
 */
let renderTrustmarkRecipientIdentifiers = function(target, obj, data, offset)  {
    let html = renderPagination(offset, data.records.length, 'renderTrustmarkRecipientIdentifiersOffset');
    html += "<table class='table table-condensed table-striped table-bordered'>";

    if(obj.editable)  {
        html += "<tr><td colspan='2' style='text-align: center'>";
        html += "<div class='tm-left'><a id='plus-"+target+"' title='Add Trustmark Recipient Identifier'><span class='glyphicon glyphicon-plus'></span></a> / " +
            "<a id='minus-"+target+"' title='Remove Checked Trustmark Recipient Identifier'><span class='glyphicon glyphicon-minus'></span></a></div>";
    } else {
        html += "<tr><td colspan='1' style='text-align: center'>";
    }
    html += "<b>"+obj.title+"</b></td></tr>"
    if (data.records.length === 0)  {
        html += '<tr><td colspan="2"><em>There are no Trustmark Recipient Identifiers.</em></td></tr>';
    }  else {

        let idx = 0;
        data.records.forEach(o => {
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

let drawTrustmarkRecipientIdentifier = function(obj, entry)  {
    let html = "<tr>";
    if(obj.editable) {
        html += "<td><input type='checkbox' id='edit-trustmarkRecipientIdentifier' class='edit-trustmarkRecipientIdentifier' value='" + entry.id + "'>" +
            "<a class='tm-right' href='javascript:getTrustmarkRecipientIdentifierDetails(" + entry.id + ");'><span class='glyphicon glyphicon-pencil'></span></a></td>";
    }

    html += "<td>" + entry.trustmarkRecipientIdentifierUrl + "</td>";
    html += "</tr>";

    return html;
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

    let html = renderPagination(offset, data.records.length, 'renderConformanceTargetTipOffset');
    html += "<table class='table table-condensed table-striped table-bordered'>";

    if(obj.editable)  {
        html += "<tr><td colspan='5' style='text-align: center'>";
        html += "<div class='tm-left'><a id='plus-"+target+"' title='Add a Conformance Target TIP Identifier'><span class='glyphicon glyphicon-plus'></span></a> / <a id='minus-"+target+"' title='Remove Checked Conformance Target TIP Identifiers'><span class='glyphicon glyphicon-minus'></span></a></div>";
    } else {
        html += "<tr><td colspan='4' style='text-align: center'>";
    }
    html += "<span data-toggle=‘tooltip’ data-placement=‘bottom’ title='" + obj.titleTooltip + "'><b>"+obj.title+"</b></span></td></tr>"

    if (data.records.length === 0)  {
        html += '<tr><td colspan="5"><em>There are no Conformance Target TIP Identifiers.</em></td></tr>';
    }  else {
        let idx = 0;
        data.records.forEach(o => {
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
    if(obj.editable) {
        html += "<td><input type='checkbox' class='edit-conformanceTargetTips' value='" + entry.id + "'></td>";
    }
    html += "<td style='width:auto;'>";
    html += "<a href='" + entry.conformanceTargetTipIdentifier + "' target='_blank'>" + entry.name + "</a>";
    html += "</td>";
    html += "</tr>";

    return html;
}

/**
 * hide the passed in div
 * @param target
 */
let hideIt = function(target)  {

    // make sure the target is visible since some elements might be
    // filtered out based on logged-in user and/or roles

    if(target.startsWith('#'))  {
        if (document.getElementById(target.substring(1)) != null) {
            document.getElementById(target.substring(1)).style.display = 'none';
        }
    } else {
        if (document.getElementById(target) != null) {
            document.getElementById(target).style.display = 'none';
        }
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

    let html = "";

    html += "<div class='form-group'>";
        html += "<label for='tagName' class='col-sm-2 control-label tm-margin'>Name</label>";
        html += "<input id='tagName' type='text' class='col-sm-10 form-control tm-margin' style='width: 70%;' placeholder='Enter Tag Name'/><span style='color:red;'>*</span><br>";
    html += "</div>";

    html += "<div class='form-group'>";
        html += "<div class='col-sm-offset-2 col-sm-10'>";
            html += "<button id='tagOk' type='button' class='btn btn-info tm-margin'>Add</button>";
        html += "</div>";
    html += "</div>";

    renderDialogForm(target, html);
    document.getElementById('tagOk').onclick = fn;
    document.getElementById('tagName').focus();
}

/**
 * render a form for adding an attribute
 */
let renderAttributeForm = function(target, fn)  {
    let html = "";

    html += "<div class='form-group'>";
        html += "<label for='attrName' class='col-sm-2 control-label tm-margin'>Name</label>";
        html += "<input id='attrName' type='text' class='col-sm-10 form-control tm-margin' style='width: 70%;' placeholder='Enter Attribute Name'/><span style='color:red;'>*</span><br>";
    html += "</div>";

    html += "<div class='form-group'>";
        html += "<label for='attrValue' class='col-sm-2 control-label tm-margin'>Value</label>";
        html += "<input id='attrValue' type='text' class='col-sm-10 form-control tm-margin' style='width: 70%;' placeholder='Enter Attribute Value'/><span style='color:red;'>*</span><br>";
    html += "</div>";

    html += "<div class='form-group'>";
        html += "<div class='col-sm-offset-2 col-sm-10'>";
            html += "<button id='attrOk' type='button' class='btn btn-info tm-margin'>Add</button>";
        html += "</div>";
    html += "</div>";

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

    let html = "";

    html += "<div class='form-group'>";
        html += "<label for='conformanceTargetTipIdentifier' class='col-sm-2 control-label tm-margin'>Identifier</label>";
        html += "<input id='conformanceTargetTipIdentifier' type='text' class='col-sm-10 form-control tm-margin' style='width: 70%;' placeholder='Enter Conformance Target TIP Identifier'/><span style='color:red;'>*</span><br>";
    html += "</div>";

    html += "<div class='form-group'>";
        html += "<div class='col-sm-offset-2 col-sm-10'>";
            html += "<button id='conformanceTargetTipIdentifierOk' type='button' class='btn btn-info tm-margin'>Add</button>";
        html += "</div>";
    html += "</div>";

    renderDialogForm(target, html);
    document.getElementById('conformanceTargetTipIdentifierOk').onclick = fn;
    document.getElementById('conformanceTargetTipIdentifier').focus();
}

/**
 * render a form for adding an endpoint
 */
let renderProviderForm = function(target, fn)  {

    let html = "";

    html += "<div class='form-group'>";
        html += "<label style='margin-top: 10px;' for='select-provider-types' class='col-sm-2 control-label'>Types</label>";
        html += "<div class='col-sm-10' id='select-provider-types'></div>";
    html += "</div>";

    html += "<div class='form-group'>";
        html += "<label for='providerName' class='col-sm-2 control-label tm-margin'>System Name</label>";
        html += "<input id='providerName' type='text' class='col-sm-10 form-control tm-margin' style='width: 70%;' placeholder='Enter System Name'/><span style='color:red;'>*</span><br>";
    html += "</div>";

    html += "<div class='form-group'>";
        html += "<div class='col-sm-offset-2 col-sm-10'>";
            html += "<button id='providerOk' type='button' class='btn btn-info tm-margin'>Add</button>";
        html += "</div>";
    html += "</div>";

    renderDialogForm(target, html);
    document.getElementById('providerOk').onclick = fn;
}

/**
 * render a form for entering an organization
 * @param target
 * @param fn
 */
let renderOrganizationForm = function(target, fn, org)  {

    let html = "";

    html += "<div class='form-group'>";
    html += "<label for='org_name' class='col-sm-2 control-label tm-margin'>Full Name</label>";
    html += "<input class='col-sm-10 form-control tm-margin' style='width: 70%;' name='org_name' id='org_name' placeholder='Enter Organization Full Name' data-toggle=‘tooltip’ data-placement=‘bottom’ title='Enter the organization full name.'/><span style='color:red;'>*</span><br>";
    html += "</div>";

    html += "<div class='form-group'>";
    html += "<label for='org_display' class='col-sm-2 control-label tm-margin'>Abbreviation</label>";
    html += "<input type='text' class='col-sm-10 form-control tm-margin' style='width: 70%;' name='org_display' id='org_display' placeholder='Enter Organization Abbreviation or Acronym' data-toggle=‘tooltip’ data-placement=‘bottom’ title='Enter the organization abbreviation or acronym.'/><span style='color:red;'>*</span><br>";
    html += "</div>";

    html += "<div class='form-group'>";
    html += "<label for='org_url' class='col-sm-2 control-label tm-margin'>URL</label>";
    html += "<input type='url' class='col-sm-10 form-control tm-margin' style='width: 70%;' name='org_url' id='org_url' placeholder='Enter Organization URL' data-toggle=‘tooltip’ data-placement=‘bottom’ title='Enter the organization URL.'/><span style='color:red;'>*</span><br>";
    html += "</div>";

    html += "<div class='form-group'>";
    html += "<label for='org_desc' class='col-sm-2 control-label tm-margin'>Description</label>";
    html += "<textarea class='col-sm-10 form-control tm-margin' style='width: 70%;' name='org_desc' id='org_desc' placeholder='Enter Organization Description'></textarea><span style='color:red;'>*</span><br>";
    html += "</div>";

    // An organization id of zero means add a new organization
    let addOrSave = "Add";
    if(org.id !== 0) {
        addOrSave = "Save";
    }

    html += "<div class='form-group'>";
    html += "<div class='col-sm-offset-2 col-sm-10'>";
    html += "<button id='orgOk' type='button' class='btn btn-info tm-margin'>" + addOrSave + "</button>";
    html += "</div>";
    html += "</div>";

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

// TODO: in system view, the contact form renders the org label
let renderContactForm = function(target, preFn, fn, contact)  {
    let html = "";

    html += "<div class='form-group'>";
        html += "<label style='margin-top: 10px;' id='select-organization-label' for='select-organization' class='col-sm-2 control-label'>Organization</label>";
        html += "<div class='col-sm-10' id='select-organization'></div>";
    html += "</div>";

    html += "<div class='form-group'>";
        html += "<label for='select-contact-types' class='col-sm-2 control-label'>Type</label>";
        html += "<div class='col-sm-10' id='select-contact-types'></div>";
    html += "</div>";

    html += "<div class='form-group'>";
    html += "<label for='lastName' class='col-sm-2 control-label tm-margin'>Last Name</label>";
    html += "<input id='lastName' type='text' class='col-sm-10 form-control tm-margin' style='width: 70%;' placeholder='Enter Last Name'/><span style='color:red;'>*</span><br>";
    html += "</div>";

    html += "<div class='form-group'>";
    html += "<label for='firstName' class='col-sm-2 control-label tm-margin'>First Name</label>";
    html += "<input id='firstName' type='text' class='col-sm-10 form-control tm-margin' style='width: 70%;' placeholder='Enter First Name'/><span style='color:red;'>*</span><br>";
    html += "</div>";

    html += "<div class='form-group'>";
    html += "<label for='phoneNbr' class='col-sm-2 control-label tm-margin'>Phone</label>";
    html += "<input id='phoneNbr' type='text' size='16' class='col-sm-10 form-control tm-margin' style='width: 70%;' placeholder='Enter Phone Number'/><br>";
    html += "</div>";

    html += "<div class='form-group'>";
    html += "<label for='emailAddr' class='col-sm-2 control-label tm-margin'>Email</label>";
    html += "<input id='emailAddr' type='text' size='40' class='col-sm-10 form-control tm-margin' style='width: 70%;' placeholder='Enter Email Address'/><span style='color:red;'>*</span><br>";
    html += "</div>";

    // A contact id of zero means add a new contact
    let addOrSave = "Add";
    if(contact.id !== 0) {
        addOrSave = "Save";
    }

    html += "<div class='form-group'>";
    html += "<div class='col-sm-offset-2 col-sm-10'>";
    html += "<button id='contactOk' type='button' class='btn btn-info tm-margin'>" + addOrSave + "</button>";
    html += "</div>";
    html += "</div>";

    renderDialogForm(target, html);
    document.getElementById('contactOk').onclick = fn;
    preFn(contact);
}

/**
 * render a form for adding an assessment tool repo
 */
let renderAssessmentToolReposForm = function(target, preFn, fn, repo)  {

    let html = "";

    html += "<div class='form-group'>";
    html += "<label for='assessmentToolUrlRepo' class='col-sm-2 control-label tm-margin'>URL</label>";
    html += "<input id='assessmentToolUrlRepo' type='text' class='col-sm-10 form-control tm-margin' style='width: 70%;' placeholder='Enter the Assessment Tool URL'/><span style='color:red;'>*</span><br>";
    html += "</div>";

    // A repo id of zero means add a new assessment tool repo
    let addOrSave = "Add";
    if(repo.id !== 0) {
        addOrSave = "Save";
    }

    html += "<div class='form-group'>";
    html += "<div class='col-sm-offset-2 col-sm-10'>";
    html += "<button id='assessmentToolUrlRepoOk' type='button' class='btn btn-info tm-margin'>" + addOrSave + "</button>";
    html += "</div>";
    html += "</div>";

    renderDialogForm(target, html);
    document.getElementById('assessmentToolUrlRepoOk').onclick = fn;
    document.getElementById('assessmentToolUrlRepo').focus();

    preFn(repo);
}

/**
 * render a form for adding a trustmark recipient identifier
 */
let renderTrustmarkRecipientIdentifiersForm = function(target, preFn, fn, trustmarkRecipientIdentifier)  {

    let html = "";

    html += "<div class='form-group'>";
    html += "<label for='trustmarkRecipientIdentifier' class='col-sm-2 control-label tm-margin'>Identifier</label>";
    html += "<input id='trustmarkRecipientIdentifier' type='text' class='col-sm-10 form-control tm-margin' style='width: 70%;' placeholder='Enter the Trustmark Recipient Identifier URI'/><span style='color:red;'>*</span><br>";
    html += "</div>";

    // A trustmark recipient identifier id of zero means add a new trustmark recipient identifier
    let addOrSave = "Add";
    if(trustmarkRecipientIdentifier.id !== 0) {
        addOrSave = "Save";
    }

    html += "<div class='form-group'>";
    html += "<div class='col-sm-offset-2 col-sm-10'>";
    html += "<button id='trustmarkRecipientIdentifierOk' type='button' class='btn btn-info tm-margin'>" + addOrSave + "</button>";
    html += "</div>";
    html += "</div>";

    renderDialogForm(target, html);
    document.getElementById('trustmarkRecipientIdentifierOk').onclick = fn;
    document.getElementById('trustmarkRecipientIdentifier').focus();

    preFn(trustmarkRecipientIdentifier);
}

/**
 * render a form for adding a document
 */
let renderDocumentForm = function(target, binaryUrl, preFn, fn, doc)  {

    let html = "";

    html += "<input type='hidden' id='binaryId1' name='binaryObject' value='-1'>";

    // upload button
    html += "<div class='form-group'>";
    html += "<label for='fileUploadName' class='col-sm-2 control-label'  style='margin-top: 13px; padding: 7px 10px 0px 15px'>Document File</label>";
    html += "<div class='col-sm-10'>";
    html += "<p id='fileUploadName'>" +
        "<a href='#' id='fileUploadButton1' class='btn btn-default'><span class='glyphicon glyphicon-upload'></span>Upload</a>" +
        "<span id='fileName1'>Select a File...</span><div id='fileUploadStatus1'></div>" +
        "</p>";
    html += "</div>";
    html += "</div>";

    // filename input
    html += "<div class='form-group'>";
    html += "<label for='filename' class='col-sm-2 control-label tm-margin'>File Name</label>";
    html += "<input name='filename' id='filename' class='col-sm-10 form-control tm-margin' style='width: 70%;' /><span style='color:red;'>*</span><br>";
    html += "</div>";

    // description input
    html += "<div class='form-group'>";
    html += "<label for='description' class='col-sm-2 control-label tm-margin'>Description</label>";
    html += "<textarea name='description' id='description'  class='col-sm-10 form-control tm-margin' style='width: 70%;' placeholder='Enter Document Description'></textarea><span style='color:red;'>*</span><br>";
    html += "</div>";

    // public document checkbox
    html += "<div class='form-group'>";
    html += "<div class='col-sm-offset-2 col-sm-10'>";
    html += "<div class='checkbox'>";
    html += "<label>";
    html += "<input type='checkbox' id='publicDocument' value='true' checked> Public Document";
    html += "</label>";
    html += "</div>";
    html += "</div>";
    html += "</div>";

    // A document id of zero means add a new document
    let addOrSave = "Add";
    if(doc.id !== 0) {
        addOrSave = "Save";
    }

    html += "<div class='form-group'>";
    html += "<div class='col-sm-offset-2 col-sm-10'>";
    html += "<button id='documentOk' type='button' class='btn btn-info'>" + addOrSave + "</button>";
    html += "</div>";
    html += "</div>";

    renderDialogForm(target, html);
    document.getElementById('documentOk').onclick = fn;

    preFn(doc);

    // initializes plupload for the document form
    createPlupload1(binaryUrl);
}

/**
 * renders a form for updating a registrant's data
 * @param target
 * @param fn
 * @param registrant
 */
let renderRegistrantForm = function(target, fn, registrant) {

    let html = "";

    html += "<div class='form-group'>";
    html += "<label style='margin-top: 10px;' id='select-organization-label' for='select-organization' class='col-sm-2 control-label'>Organization</label>";
    html += "<div class='col-sm-10' id='select-organization'></div>";
    html += "</div>";

    html += "<div class='form-group'>";
    html += "<label for='detail_lastName' class='col-sm-2 control-label tm-margin'>Last Name</label>";
    html += "<input id='detail_lastName' type='text' class='col-sm-10 form-control tm-margin' style='width: 70%;' placeholder='Enter Last Name'/><span style='color:red;'>*</span><br>";
    html += "</div>";

    html += "<div class='form-group'>";
    html += "<label for='detail_firstName' class='col-sm-2 control-label tm-margin'>First Name</label>";
    html += "<input id='detail_firstName' type='text' class='col-sm-10 form-control tm-margin' style='width: 70%;' placeholder='Enter First Name'/><span style='color:red;'>*</span><br>";
    html += "</div>";

    html += "<div class='form-group'>";
    html += "<label for='detail_email' class='col-sm-2 control-label tm-margin'>Email</label>";
    html += "<input id='detail_email' type='text' class='col-sm-10 form-control tm-margin' style='width: 70%;' placeholder='Enter Email Address'/><span style='color:red;'>*</span><br>";
    html += "</div>";

    html += "<div class='form-group'>";
    html += "<label for='detail_phone' class='col-sm-2 control-label tm-margin'>Phone</label>";
    html += "<input id='detail_phone' type='text' class='col-sm-10 form-control tm-margin' style='width: 70%;' placeholder='Enter Phone Number'/><span style='color:red;'>*</span><br>";
    html += "</div>";

    // A registrant id of zero means add a new registrant
    let addOrSave = "Add";
    if(registrant.id !== 0) {
        addOrSave = "Save";
    }

    html += "<div class='form-group'>";
    html += "<div class='col-sm-offset-2 col-sm-10'>";
    html += "<button id='registrantOk' type='button' class='btn btn-info tm-margin'>" + addOrSave + "</button>";
    html += "</div>";
    html += "</div>";

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
    let html = "<form class='form-horizontal'>";
    html += "<div class='full-width-form'>";
    html += "<a class='tm-margin tm-right' href=\"javascript:hideIt('"+target+"');\"><span class='glyphicon glyphicon-remove'></span></a><br>";
    html += content;
    html += "</div></form>";

    html += "<p><span style='color:red;'>*</span> - Indicates required field.</p>"

    document.getElementById(target).innerHTML = html;
    showIt(target);
}
