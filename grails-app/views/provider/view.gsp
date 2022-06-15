<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <style type="text/css">
    .clickable {
        cursor: pointer;
    }

    .clickable .glyphicon {
        background: rgba(0, 0, 0, 0.15);
        display: inline-block;
        padding: 6px 12px;
        border-radius: 4px
    }

    .panel-heading span {
        font-size: 15px;
    }

    .panel-heading button {
        margin-top: -25px;
    }

    a.disabledLink {
        pointer-events: none;
        color: #ccc;
    }
    </style>
    <script type="text/javascript">
        var ORG_VIEW_BASE_URL = "${createLink(controller:'organization', action: 'view')}/";

        $(document).ready(function () {

            var isSAMLSystem = ${provider.providerType == tm.binding.registry.ProviderType.SAML_IDP} ||
                                ${provider.providerType == tm.binding.registry.ProviderType.SAML_SP};
            var isCertificateSystem = ${provider.providerType == tm.binding.registry.ProviderType.CERTIFICATE}
            var isOpenIdSystem = ${provider.providerType == tm.binding.registry.ProviderType.OIDC_RP} ||
                                 ${provider.providerType == tm.binding.registry.ProviderType.OIDC_OP};

            if (isCertificateSystem) {
                showCertificateSystem(${provider.id}, ${provider.organization.id})
            } else if (isSAMLSystem) {
                showSAMLSystem(${provider.id}, ${provider.organization.id}, ${provider.providerType == tm.binding.registry.ProviderType.SAML_IDP})
            } else if (isOpenIdSystem) {
                showOidcSystem(${provider.id}, ${provider.organization.id})
            } else {
                console.error("Unknown system type.")
            }
        });

        let selectOrganizations = function (id) {
            list("${createLink(controller:'organization', action: 'list')}"
                , curriedSelectOrganizations('select-organization')(id)
                , {name: 'ALL'});
        }

        let selectContactTypes = function (id) {
            list("${createLink(controller:'contact', action: 'types')}"
                , curriedContactTypes('select-contact-types')(id)
                , {name: 'ALL'});
        }

        let getTrustmarks = function (pid) {
            list("${createLink(controller:'trustmark', action: 'list')}"
                , trustmarkResults(pid)
                , {id: pid}
            );
        }

        let trustmarkResults = function (pId) {
            return function (results) {
                renderTrustmarkOffset = curriedTrustmark('trustmarks-list')(results);
                renderTrustmarkOffset(0);
            }
        }

        /**
         * tags functionality for editing
         * @param pid
         */
        let getTags = function (pid) {
            list("${createLink(controller:'tag', action: 'list')}"
                , tagResults
                , {id: pid}
            );
            hideIt('tag-details');
        }

        let tagResults = function (results) {
            renderTagOffset = curriedTag('tags-list')
            ({
                editable: results.editable
                , fnAdd: function () {
                    renderTagForm('tag-details'
                        , function () {
                            insertTag(document.getElementById('tagName').value, ${provider.id});
                        });
                }
                , fnRemove: function () {
                    removeTags('${provider.id}');
                }
                , title: 'Keyword Tags'
            })
            (results);
            renderTagOffset(0);
        }

        let insertTag = function (tag, pid) {
            add("${createLink(controller:'tag', action: 'add')}"
                , function (data) {
                    getTags(pid);
                }
                , {pId: pid, tag: tag}
            );
        }

        let removeTags = function (pid) {
            getCheckedIds('edit-tags', function (list) {
                update("${createLink(controller:'tag', action: 'delete')}"
                    , function (data) {
                        getTags(pid);
                    }
                    , {ids: list, pid: pid}
                );
            });
        }

        /**
         * contacts functionality for editing contacts
         * @param pid
         */
        let getContacts = function (orgId, pId) {
            list("${createLink(controller:'provider', action: 'listContacts')}"
                , contactResults
                , {id: orgId, pid: pId}
            );
            hideIt('contact-details');
        }

        let populateContactForm = function (contact) {

            hideIt('select-organization-label');
            hideIt('select-organization');
            if (contact.id === 0) {
                selectPointsOfContact('0', ${provider.organization.id})
                selectContactTypes('0');
                document.getElementById('lastName').value = "";
                document.getElementById('firstName').value = "";
                document.getElementById('emailAddr').value = "";
                document.getElementById('phoneNbr').value = "";
            } else {
                selectPointsOfContact('0', ${provider.organization.id})
                selectContactTypes(contact.type.name)
                document.getElementById('lastName').value = contact.lastName;
                document.getElementById('firstName').value = contact.firstName;
                document.getElementById('emailAddr').value = contact.email;
                document.getElementById('phoneNbr').value = contact.phone;
            }
            document.getElementById('lastName').focus();
        }

        let populateSelectedContact = function(obj) {
            var selectedValue = obj.options[obj.selectedIndex].value;

            if (selectedValue === 'newPointOfContact') {
                populateContactForm({id: 0});
            } else {
                getContactDetails(selectedValue); // this works almost
            }
        }

        let getContactDetails = function (id) {
            get("${createLink(controller:'contact', action: 'get')}"
                , contactDetail('contact-details')(populateContactForm)
                (function () {
                    updateContact(id, document.getElementById('lastName').value
                        , document.getElementById('firstName').value
                        , document.getElementById('emailAddr').value
                        , document.getElementById('phoneNbr').value
                        , document.getElementById('ctypes').options[document.getElementById('ctypes').selectedIndex].value
                        , ${provider.organization.id});
                })
                , {id: id}
            );
        }

        let contactResults = function (results) {

            renderSystemContactsOffset = curriedSystemContacts('contacts-list')
            ({
                editable: results.editable
                , fnDraw: drawSystemContact
                , title: 'Points of Contact'
                , providerId: ${provider.id}
            })
            (results);
            renderSystemContactsOffset(0);
        }

        let addOrRemoveSystemContact = function(checkBox) {
            let contactId = checkBox.value;
            let providerId = checkBox.getAttribute("providerId");

            if (checkBox.checked) {

                update("${createLink(controller:'provider', action: 'addContactToSystem')}"
                    , function (data) {
                        if (!isEmtpy(data.id)) {
                            let statusMessage = "Added point of contact, " + data.firstName + " " + data.lastName + ", to system.";
                            systemContactStatus(statusMessage);
                        }
                    }
                    , {contactId: contactId, providerId: providerId}
                );
            } else {
                update("${createLink(controller:'provider', action: 'removeContactFromSystem')}"
                    , function (data) {
                        if (!isEmtpy(data.id)) {
                            let statusMessage = "Removed point of contact, " + data.firstName + " " + data.lastName + ", from system.";
                            systemContactStatus(statusMessage);
                        }
                    }
                    , {contactId: contactId, providerId: providerId}
                );
            }
        }

        let systemContactStatus = function(status) {
            $('#systemContacts-status').text(status);
            $('#systemContacts-status').fadeTo(200, 1);
            $('#systemContacts-status').delay(2000).fadeTo(300, 0);
        }

        let insertContact = function (lname, fname, email, phone, type, pid) {
            if (checkContact(lname, fname, email, phone, type, ${provider.organization.id})) {
                add("${createLink(controller:'contact', action: 'add')}"
                    , function (data) {
                        getContacts(pid);
                    }
                    , {
                        lname: lname
                        , fname: fname
                        , email: email
                        , phone: phone
                        , organizationId: ${provider.organization.id}
                        , type: type
                        , pId: pid
                    }
                );
            }
        }

        let updateContact = function (id, lname, fname, email, phone, type, orgId) {
            if (checkContact(lname, fname, email, phone, type, orgId)) {
                update("${createLink(controller:'contact', action: 'update')}"
                    , function (data) {
                        getContacts(orgId, ${provider.id});
                    }
                    , {
                        id: id
                        , lname: lname
                        , fname: fname
                        , email: email
                        , phone: phone
                        , organizationId: ${provider.organization.id}
                        , type: type
                    });
            } else {
                scroll(0, 0);
            }
        }

        let removeContacts = function (pid) {
            getCheckedIds('edit-contacts', function (list) {
                update("${createLink(controller:'contact', action: 'delete')}"
                    , function (data) {
                        getContacts(pid);
                    }
                    , {ids: list, pid: pid}
                );
            });
        }

        /**
         * attribute editing functionality
         * @param pid
         */
        let removeAttributes = function (pid) {
            getCheckedIds('edit-attributes', function (list) {
                update("${createLink(controller:'attribute', action: 'delete')}"
                    , function (data) {
                        getAttributes(pid);
                    }
                    , {ids: list, pid: pid}
                );
            });
        }

        let getAttributes = function (pid) {
            list("${createLink(controller:'attribute', action: 'list')}"
                , attributeResults
                , {id: pid}
            );
            hideIt('attribute-details');
        }

        let attributeResults = function (results) {
            renderAttributeOffset = curriedAttribute('attributes-list')
            ({
                editable: results.editable
                , fnAdd: function () {
                    renderAttributeForm('attribute-details'
                        , function () {
                            insertAttribute(document.getElementById('attrName').value
                                , document.getElementById('attrValue').value
                                , ${provider.id});
                        });
                }
                , fnRemove: function () {
                    removeAttributes(${provider.id});
                }
                , fnDraw: drawAttribute
                , title: 'System Attributes'
            })
            (results);
            renderAttributeOffset(0);
        }

        let insertAttribute = function (name, value, pid) {
            add("${createLink(controller:'attribute', action: 'add')}"
                , function (data) {
                    getAttributes(pid);
                }
                , {
                    name: name
                    , value: value
                    , pId: pid
                }
            );
        }

        // idp attributes (id="idp-attributes")
        let getIdpAttributes = function (pid) {
            list("${createLink(controller:'provider', action: 'listIdpAttributes')}"
                , idpAttributeResults
                , {id: pid}
            );
        }

        let idpAttributeResults = function (results) {
            renderIdpAttributeOffset = curriedIdpAttribute('idp-attributes')
            ({
                fnDraw: drawIdpAttribute
                , title: 'SAML Attributes Supported'
            })
            (results);
            renderIdpAttributeOffset(0);
        }

        /**
         * endpoint editing functionality
         * @param pid
         */
        let removeEndpoints = function (pid) {
            getCheckedIds('edit-endpoints', function (list) {
                update("${createLink(controller:'endPoint', action: 'delete')}"
                    , function (data) {
                        getEndpoints(pid);
                    }
                    , {ids: list, pid: pid}
                );
            });
        }

        let getEndpoints = function (pid) {
            console.log("getEndpoints....");
            list("${createLink(controller:'endPoint', action: 'list')}"
                , endPointResults
                , {id: pid}
            );
            hideIt('endpoint-details');
        }

        let endPointResults = function (results) {
            renderEndpointOffset = curriedEndpoint('endpoints-list')
            ({
                editable: true
                , fnAdd: function () {
                    renderEndpointForm('endpoint-details'
                        , function () {
                            insertEndpoint(document.getElementById('endptName').value
                                , document.getElementById('endptType').value
                                , document.getElementById('endptUrl').value
                                , ${provider.id});
                        });
                }
                , fnRemove: function () {
                    removeEndpoints('${provider.id}');
                }
                , fnDraw: drawEndpoints
                , title: 'Endpoints'
            })
            (results);
            renderEndpointOffset(0);
        }

        let insertEndpoint = function (name, binding, url, pid) {
            add("${createLink(controller:'endPoint', action: 'add')}"
                , function (data) {
                    getEndpoints(pid);
                }
                , {
                    name: name
                    , url: url
                    , binding: binding
                    , pId: pid
                }
            );
        }

        /**
         * conformance target tips editing functionality
         * @param pid
         */
        let removeConformanceTargetTips = function (pid) {
            getCheckedIds('edit-conformanceTargetTips', function (list) {
                update("${createLink(controller:'conformanceTargetTip', action: 'delete')}"
                    , function (data) {
                        $('#conformanceTargetTips-status').html('');
                        getConformanceTargetTips(pid);
                        updateTrustmarkBindingDetails(pid);
                    }
                    , {ids: list, pid: pid}
                );
            });
        }

        let getConformanceTargetTips = function (pid) {
            list("${createLink(controller:'conformanceTargetTip', action: 'list')}"
                , conformanceTargetTipResults
                , {id: pid}
            );
            // reload trustmarks
            getTrustmarks(pid);
            hideIt('conformanceTargetTips-details');
        }

        // {function(*=): function(*=): function(*=): *}
        let conformanceTargetTipResults = function (results) {
            renderConformanceTargetTipOffset = curriedConformanceTargetTip('conformanceTargetTips-list')
            ({
                editable: results.editable
                ,
                fnAdd: function () {
                    $('#conformanceTargetTips-status').html('');
                    renderConformanceTargetTipForm('conformanceTargetTips-details'
                        , function () {
                            insertConformanceTargetTip(document.getElementById('conformanceTargetTipIdentifier').value
                                , ${provider.id});
                        });
                }
                ,
                fnRemove: function () {
                    removeConformanceTargetTips('${provider.id}');
                }
                ,
                fnDraw: drawConformanceTargetTips
                ,
                title: 'Conformance Target Trust Interoperability Profiles'
                ,
                titleTooltip: 'Conformance Target TIPs are trust interoperability profiles that this system aspires to fully earn, and frequently has earned most or all of the required trustmarks.'
            })
            (results);
            renderConformanceTargetTipOffset(0);
        }

        let insertConformanceTargetTip = function (identifier, pid) {
            $('#conformanceTargetTips-status').html('');
            add("${createLink(controller:'conformanceTargetTip', action: 'add')}"
                , function (data) {
                    setStatusMessage('conformanceTargetTips-status', data);

                    getConformanceTargetTips(pid);
                    updateTrustmarkBindingDetails(pid);
                }
                , {
                    identifier: identifier
                    , pId: pid
                }
            );
        }

        /**
         * render a form for adding a conformance target tip
         */
        let renderInternalConformanceTargetTipForm = function (target, fn) {
            let html = "<input id='conformanceTargetTipIdentifier' size='80' type='text' class='form-control tm-margin' placeholder='Enter Conformance Target TIP Identifier' /><span style='color:red;'>&nbsp;&nbsp;*</span><br>";
            html += "<button id='conformanceTargetTipIdentifierOk' type='button' class='btn btn-info tm-margin'>Add</button>";
            renderInternalDialogForm(target, html);
            document.getElementById('conformanceTargetTipIdentifierOk').onclick = fn;
            document.getElementById('conformanceTargetTipIdentifier').focus();
        }


        /**
         * Partner Systems TIPs editing functionality
         * @param pid
         */
        let removePartnerSystemsTips = function (pid) {
            getCheckedIds('edit-partnerSystemsTips', function (list) {
                update("${createLink(controller:'provider', action: 'deletePartnerSystemsTips')}"
                    , function (data) {
                        $('#partnerSystemsTips-status').html('');
                        getPartnerSystemsTips(pid);
                    }
                    , {ids: list, pid: pid}
                );
            });
        }

        let getPartnerSystemsTips = function (pid) {
            list("${createLink(controller:'provider', action: 'partnerSystemsTips')}"
                , partnerSystemsTipResults
                , {pid: pid}
            );
            hideIt('partner-systems-tips-details');
        }

        // {function(*=): function(*=): function(*=): *}
        let partnerSystemsTipResults = function (results) {
            renderPartnerSystemsTipOffset = curriedPartnerSystemsTip('partner-systems-tips-list')
            ({
                editable: results.editable
                ,
                fnAdd: function () {
                    $('#partner-systems-tips-status').html('');
                    renderPartnerSystemTipForm('partner-systems-tips-details'
                        , function () {
                            insertPartnerSystemsTip(document.getElementById('partnerSystemsTipIdentifier').value
                                , ${provider.id});
                        });
                }
                ,
                fnRemove: function () {
                    removePartnerSystemsTips('${provider.id}');
                }
                ,
                fnDraw: drawPartnerSystemsTips
                ,
                title: 'Partner System Trust Interoperability Profiles'
                ,
                titleTooltip: 'This list of trust interoperability profiles (TIPs) represents ' +
                    'the requirements of this system for potential partner systems that will engage in trusted information exchanges.'
            })
            (results);
            renderPartnerSystemsTipOffset(0);
        }

        let insertPartnerSystemsTip = function (identifier, pid) {
            $('#partner-systems-tips-status').html('');
            add("${createLink(controller:'provider', action: 'addPartnerSystemsTip')}"
                , function (data) {
                    setStatusMessage('partner-systems-tips-status', data);

                    getPartnerSystemsTips(pid);
                }
                , {
                    identifier: identifier
                    , pId: pid
                }
            );
        }

        /**
         * render a form for adding a partner systems tip
         */
        let renderInternalPartnerSystemsTipForm = function (target, fn) {
            let html = "<input id='partnerSystemsTipIdentifier' size='80' type='text' class='form-control tm-margin' placeholder='Enter Partner System TIP Identifier' /><span style='color:red;'>&nbsp;&nbsp;*</span><br>";
            html += "<button id='partnerSystemsTipIdentifierOk' type='button' class='btn btn-info tm-margin'>Add</button>";
            renderInternalDialogForm(target, html);
            document.getElementById('partnerSystemsTipIdentifierOk').onclick = fn;
            document.getElementById('partnerSystemsTipIdentifier').focus();
        }


        /**
         * renders content into a standard dialog with a close X
         * @param target
         * @param content
         */
        let renderInternalDialogForm = function (target, content) {
            let html = "<form class='form-inline'>";
            html += "<div class='full-width-form form-group'>";
            html += "<a class='tm-margin tm-right' href=\"javascript:hideIt('" + target + "');\"><span class='glyphicon glyphicon-remove'></span></a><br>";
            html += content;
            html += "</div></form>";

            html += "<p><span style='color:red;'>*</span> - Indicates required field.</p>"

            document.getElementById(target).innerHTML = html;
            showIt(target);
        }

        let getCertificateDetails = function (pid) {
            list("${createLink(controller:'provider', action: 'certificateDetails')}"
                , certificateDetailsResults
                , {id: pid}
            );
        }


        let certificateDetailsResults = function (results) {
            renderCertificateDetailsOffset = curriedCertificateDetails('certificate-details')
            ({
                readonly: results.readonly
                , fnDraw: drawCertificateDetails
                , title: 'Certificate Details'
            })
            (results);
            renderCertificateDetailsOffset(0);
        }

        let showCertificateSystem = function (pid, orgId) {
            getCertificateDetails(pid);
            getTrustmarks(pid);
            getContacts(orgId, pid);

            var isLoggedIn = ${isLoggedIn};

            if (isLoggedIn) {
                getTrustmarkRecipientIdentifiers(${provider.id})
            }

            getPartnerSystemsTips(${provider.id})
            getConformanceTargetTips(pid);
            hideIt('trustmarks-list');
        }


        // OIDC System

        let getOidcDetails = function (pid) {
            list("${createLink(controller:'provider', action: 'oidcDetails')}"
                , oidcDetailsResults
                , {id: pid}
            );
        }


        let oidcDetailsResults = function (results) {
            renderOidcDetailsOffset = curriedOidcDetails('openid-connect-details')
            ({
                readonly: results.readonly
                , fnDraw: drawOidcDetails
                , title: 'OpenId Connect Details'
            })
            (results);
            renderOidcDetailsOffset(0);
        }

        let showOidcSystem = function (pid, orgId) {
            getOidcDetails(pid);
            getTrustmarks(pid);
            getContacts(orgId, pid);

            getTags(pid);
            getAttributes(pid);

            var isLoggedIn = ${isLoggedIn};

            if (isLoggedIn) {
                getTrustmarkRecipientIdentifiers(${provider.id})
            }

            getPartnerSystemsTips(${provider.id})
            getConformanceTargetTips(pid);
            hideIt('trustmarks-list');
        }

        // End OIDC System


        let getProtocolDetails = function (pid) {
            list("${createLink(controller:'provider', action: 'protocolDetails')}"
                , protocolDetailsResults
                , {id: pid}
            );
        }

        let showSAMLSystem = function (pid, orgId, isIdp) {
            getProtocolDetails(pid);
            getTrustmarks(pid);
            getTags(pid);
            getContacts(orgId, pid);
            getEndpoints(pid);
            if (isIdp) {
                getIdpAttributes(pid);
            }
            getAttributes(pid);

            var isLoggedIn = ${isLoggedIn};

            if (isLoggedIn) {
                getTrustmarkRecipientIdentifiers(${provider.id})
            }

            getPartnerSystemsTips(${provider.id})
            getConformanceTargetTips(pid);
            hideIt('trustmarks-list');
        }

        let protocolDetailsResults = function (results) {
            renderProtocolDetailsOffset = curriedProtocolDetails('protocol-details')
            ({
                editable: results.editable
                , fnDraw: drawProtocolDetails
                , title: 'Protocol Details'
            })
            (results);
            renderProtocolDetailsOffset(0);
        }

        // Trustmark Recipient Identifiers

        let getTrustmarkRecipientIdentifiers = function (pid) {
            list("${createLink(controller:'provider', action: 'trustmarkRecipientIdentifiers')}"
                , trustmarkRecipientIdentifierResults
                , {pid: pid}
            );
            hideIt('trustmark-recipient-identifiers-details');
        }

        let addTrustmarkRecipientIdentifier = function (trustmarkRecipientIdentifier) {
            if (checkTrustmarkRecipientIdentifier(trustmarkRecipientIdentifier)) {
                add("${createLink(controller:'provider', action: 'addTrustmarkRecipientIdentifier')}"
                    , function (data) {
                        setStatusMessage('trustmark-recipient-identifiers-status', data);

                        getTrustmarkRecipientIdentifiers(${provider.id});
                    }
                    , {
                        pid: ${provider.id}
                        , identifier: trustmarkRecipientIdentifier
                    }
                );
            }
        }

        let removeTrustmarkRecipientIdentifiers = function (pid) {
            getCheckedIds('edit-trustmarkRecipientIdentifier', function (list) {
                update("${createLink(controller:'provider', action: 'deleteTrustmarkRecipientIdentifiers')}"
                    , function (data) {
                        getTrustmarkRecipientIdentifiers(pid);
                    }
                    , {ids: list, pid: pid}
                );
            });
        }

        let deleteTrustmarkRecipientIdentifier = function (tmrid) {
            add("${createLink(controller:'provider', action: 'deleteTrustmarkRecipientIdentifier')}"
                , function (data) {
                    getTrustmarkRecipientIdentifiers(${provider.id});
                }
                , {
                    pid: ${provider.id}
                    , tmrid: tmrid
                }
            );
        }

        let trustmarkRecipientIdentifierResults = function (results) {
            renderTrustmarkRecipientIdentifiersOffset = curriedTrustmarkRecipientIdentifier('trustmark-recipient-identifiers-list')
            ({
                editable: results.editable
                , fnAdd: function () {
                    renderTrustmarkRecipientIdentifiersForm('trustmark-recipient-identifiers-details'
                        , populateTrustmarkRecipientIdentifiersForm
                        , function () {
                            addTrustmarkRecipientIdentifier(document.getElementById('trustmarkRecipientIdentifier').value);
                        }, {id: 0})
                }
                , fnRemove: function () {
                    removeTrustmarkRecipientIdentifiers('${provider.id}');
                }
                , fnDraw: drawTrustmarkRecipientIdentifier
                , title: 'Trustmark Recipient Identifiers'
                , hRef: 'javascript:getTrustmarkRecipientIdentifierDetails'
            })
            (results)
            renderTrustmarkRecipientIdentifiersOffset(0);
        }

        let populateTrustmarkRecipientIdentifiersForm = function (trustmarkRecipientIdentifier) {
            if (trustmarkRecipientIdentifier.id !== 0) {
                document.getElementById('trustmarkRecipientIdentifier').value = trustmarkRecipientIdentifier.trustmarkRecipientIdentifierUrl;
                document.getElementById('trustmarkRecipientIdentifier').focus();
            }
        }

        let getTrustmarkRecipientIdentifierDetails = function (id) {
            get("${createLink(controller:'provider', action: 'getTrustmarkRecipientIdentifier')}"
                , trustmarkRecipientIdentifierDetail('trustmark-recipient-identifiers-details')(populateTrustmarkRecipientIdentifiersForm)
                (function () {
                    updateTrustmarkRecipientIdentifier(id, document.getElementById('trustmarkRecipientIdentifier').value
                        , ${provider.id});
                })
                , {pid: ${provider.id}, rid: id}
            );
        }

        let updateTrustmarkRecipientIdentifier = function (id, trustmarkRecipientIdentifier, pId) {
            if (checkTrustmarkRecipientIdentifier(trustmarkRecipientIdentifier)) {
                update("${createLink(controller:'provider', action: 'updateTrustmarkRecipientIdentifier')}"
                    , function (data) {
                        getTrustmarkRecipientIdentifiers(${provider.id});
                    }
                    , {
                        id: id
                        , trustmarkRecipientIdentifier: trustmarkRecipientIdentifier
                        , providerId: ${provider.id}
                    });
            } else {
                scroll(0, 0);
            }
        }

        let updateTrustmarkBindingDetails = function (providerId) {

            $('#bindTrustmarkStatusMessage').html('');

            var url = '${createLink(controller: 'provider',  action: 'updateTrustmarkBindingDetails')}';
            $.ajax({
                url: url,
                dataType: 'json',
                async: false,
                data: {
                    id: providerId,
                    format: 'json'
                },
                beforeSend: function () {
                },
                success: function (data, statusText, jqXHR) {

                    // reload trustmarks
                    getTrustmarks(providerId);

                    $('#numberOfConformanceTargetTIPs').text(data['numberOfConformanceTargetTIPs']);
                    $('#numberOfTrustmarksBound').html(data['numberOfTrustmarksBound']);


                    // update binding button
                    if (data['numberOfTrustmarksBound'] > 0) {
                        $('.bind-trustmark-button').text("Refresh Trustmark Bindings");
                    } else {
                        $('.bind-trustmark-button').text("Bind Trustmarks");
                    }

                },
                error: function (jqXHR, statusText, errorThrown) {
                    console.log("Error: " + errorThrown);

                    $('#bindTrustmarkStatusMessage').html(errorThrown);
                }
            });
        }

        let initTrustmarkBindingState = function (providerId) {

            $('#bindTrustmarkStatusMessage').html('');

            var url = '${createLink(controller: 'provider',  action: 'initTrustmarkBindingState')}';
            $.ajax({
                url: url,
                dataType: 'json',
                async: false,
                data: {
                    id: providerId,
                    format: 'json'
                },
                beforeSend: function () {
                },
                success: function (data, statusText, jqXHR) {
                },
                error: function (jqXHR, statusText, errorThrown) {
                    console.log("Error: " + errorThrown);

                    $('#bindTrustmarkStatusMessage').html(errorThrown);
                }
            });
        }

        var STOP_LOOP = false;
        var CANCEL_LOOP = false;

        let bindTrustmarks = function (providerId) {
            console.log("** bindTrustmarks for provider: " + providerId);

            $('#bindTrustmarkStatusMessage').html('Started the trustmark binding process; trustmarks should be available once bound. ${raw(asset.image(src: 'spinner.gif'))}');

            // reset the state variables
            initTrustmarkBindingState(providerId);

            STOP_LOOP = false;
            trustmarkBindingStatusLoop(providerId);

            var url = '${createLink(controller: 'provider',  action: 'bindTrustmarks')}';
            $.ajax({
                url: url,
                dataType: 'json',
                data: {
                    id: providerId,
                    format: 'json'
                },
                beforeSend: function () {
                },
                success: function (data, statusText, jqXHR) {
                    console.log("*^^^** Successfully received bindTrustmarks response: " + JSON.stringify(data) + "for provider id: " + providerId);

                    updateTrustmarkBindingDetails(providerId);
                },
                error: function (jqXHR, statusText, errorThrown) {
                    console.log("Error: " + errorThrown);

                    $('#bindTrustmarkStatusMessage').html(errorThrown);
                },
                timeout: 120000 // 2 minutes
            });
        }

        function trustmarkBindingStatusLoop(providerId) {

            if (!CANCEL_LOOP) {
                if (STOP_LOOP) {

                    updateTrustmarkBindingDetails(providerId);

                    return;
                }

                updateTrustmarkBindingStatus();

                setTimeout(trustmarkBindingStatusLoop, 250, providerId);
            }
        }

        function updateTrustmarkBindingStatus() {

            $.ajax({
                url: '${createLink(controller:'provider', action: 'trustmarkBindingStatusUpdate')}',
                method: 'GET',
                dataType: 'json',
                cache: false,
                data: {
                    format: 'json',
                    timestamp: new Date().getTime()
                },
                success: function (data, textStatus, jqXHR) {
                    TD_INFO_STATUS_UPDATE = data;
                    renderTrustmarkBindingInfoStatus(data);
                    if (data && data.status == "SUCCESS")
                        STOP_LOOP = true;
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    console.log("Error updating trustmark definitions info list status!");
                }
            })
        }

        function renderTrustmarkBindingInfoStatus(data) {

            var html = '';
            if (data && data.status) {
                html += '<div class="well"><h5>';
                if (data.status == "SUCCESS") {
                    html += '<span class="glyphicon glyphicon-ok-circle"></span> SUCCESS';
                } else {
                    html += '<span class="glyphicon glyphicon-time"></span> ' + data.status;
                }
                html += '</h5>';
                html += buildProgressBarHtml(data);
                html += '<div>' + data.message + '</div>';

                html += '</div>';

            }

            $('#bindTrustmarkStatusMessage').html(html);
        }

        /**
         * Given a percent value (ie between 0-100) this method will create a progress bar HTML snippet.
         */
        function buildProgressBarHtml(data) {
            console.log("buildProgressBarHtml percent: " + data.percent);

            var percent = data.percent;

            percent = Math.floor(percent);

            if (data.status == "PRE-PROCESSING") {
                return '<div class="progress">' +
                    '<div class="progress-bar progress-bar-striped progress-bar-animated" role="progressbar" aria-valuenow="' + percent + '" aria-valuemin="0" aria-valuemax="100" style="width: ' + 100 + '%;">' +
                    '</div></div>';
            }

            return '<div class="progress">' +
                '<div class="progress-bar" role="progressbar" aria-valuenow="' + percent + '" aria-valuemin="0" aria-valuemax="100" style="width: ' + percent + '%;">' +
                '<span class="sr-only">' + percent + '% Complete</span>' +
                '</div></div>';
        }

        let cancelTrustmarkBindings = function (providerId) {
            CANCEL_LOOP = true;

            $('#bindTrustmarkStatusMessage').html('Canceled the trustmark binding process.');
            // remove binding cancel button
            document.getElementById("cancelTrusmarkBindings").innerHTML = "";

            var url = '${createLink(controller: 'provider',  action: 'cancelTrustmarkBindings')}';
            $.ajax({
                url: url,
                dataType: 'json',
                data: {
                    id: providerId,
                    format: 'json'
                },
                beforeSend: function () {
                },
                success: function (data, statusText, jqXHR) {

                    // reload trustmarks
                    getTrustmarks(providerId);

                    $('#bindTrustmarkStatusMessage').html("Status: " + data['message']);

                    $('#numberOfConformanceTargetTIPs').text(data['numberOfConformanceTargetTIPs']);
                    $('#numberOfTrustmarksBound').html(data['numberOfTrustmarksBound']);

                },
                error: function (jqXHR, statusText, errorThrown) {

                    $('#bindTrustmarkStatusMessage').html(errorThrown);
                }
            });
        }

        let showSystem = function(showFnc, url, formData, isIdp) {

            $.ajax({
                url: url,
                type: 'POST',
                enctype: 'multipart/form-data',
                data: formData,
                processData: false,
                contentType: false,

                beforeSend: function () {
                },
                success: function (data, statusText, jqXHR) {

                    if (isIdp != null) {
                        showFnc(data.providerId, data.organizationId, isIdp);
                    } else {
                        showFnc(data.providerId, data.organizationId);
                    }

                    $(document).ajaxStop(function () {
                        // keep the protocols section open
                        $('#collapseProtocols').collapse('show');

                        let html = "<br>";
                        if (!isEmtpy(data.messageMap['SUCCESS'])) {
                            html += "<div class='alert alert-success' class='glyphicon glyphicon-ok-circle'>" + data.messageMap['SUCCESS'] + "</div>";
                        }

                        if (!isEmtpy(data.messageMap['WARNING'])) {
                            html += "<div class='alert alert-warning' class='glyphicon glyphicon-warning-sign'>" + data.messageMap['WARNING'] + "</div>";
                        }

                        if (!isEmtpy(data.messageMap['ERROR'])) {
                            html += "<div class='alert alert-danger' class='glyphicon glyphicon-exclamation-sign'>" + data.messageMap['ERROR'] + "</div>";
                        }

                        $('#uploadStatusMessage').html(html);
                    });
                },
                error: function (jqXHR, statusText, errorThrown) {
                    console.log("Error: " + errorThrown);

                    $('#uploadStatusMessage').html(errorThrown);
                }
            });
        }
    </script>
    <meta charset="UTF-8">
    <meta name="layout" content="main"/>
    <title>Provider</title>
</head>

<body>
<div id="status-header"></div>
<h4><b>System Information for ${provider.name}</b></h4>

<table class='table table-condensed table-striped table-bordered'>
    <tr>
        <td style='width: auto;'><b>System Name</b></td>
        <td style='width: auto;'>${provider.name}</td>
    </tr>
    <tr>
        <td style='width: auto;'><b>Organization</b></td>
        <td style='width: auto;'><a
                href='${createLink(controller: 'organization', action: 'view', id: provider.organization.id)}'>${provider.organization.name}</a>
        </td>
    </tr>
</table>

<br>

<g:if test="${successMessage != null && !successMessage.isEmpty()}">
    <div class="alert alert-success">${successMessage}</div>
</g:if>
<g:if test="${warningMessage != null && !warningMessage.isEmpty()}">
    <div class="alert alert-warning">${warningMessage}</div>
</g:if>
<g:if test="${errorMessage != null && !errorMessage.isEmpty()}">
    <div class="alert alert-danger">${errorMessage}</div>
</g:if>

<div style="margin-top: 2em;">

    <div class="panel panel-primary">
        <div class="panel-heading">
            <g:if test="${provider.providerType == tm.binding.registry.ProviderType.SAML_IDP || provider.providerType == tm.binding.registry.ProviderType.SAML_SP}">
                <h4 class="panel-title">Protocol-Specific Details</h4>
            </g:if>
            <g:elseif test="${provider.providerType == tm.binding.registry.ProviderType.CERTIFICATE}">
                <h4 class="panel-title">Certificate-Specific Details</h4>
            </g:elseif>
            <g:elseif test="${provider.providerType == tm.binding.registry.ProviderType.OIDC_RP || provider.providerType == tm.binding.registry.ProviderType.OIDC_OP}">
                <h4 class="panel-title">OpenId Connect-Specific Details</h4>
            </g:elseif>
            <g:else>

            </g:else>

            <button class="btn btn-primary pull-right" type="button" data-toggle="collapse"
                    data-target="#collapseProtocols" aria-expanded="false" aria-controls="collapseProtocols">
                <i class="glyphicon glyphicon-plus"></i>
            </button>
        </div>

        <div class="collapse" id="collapseProtocols">
            <div class="panel-body">

                <sec:ifLoggedIn>
                    <div style="height: auto; float:left; margin-bottom: 2em;" id="uploadForm">

                        <g:if test="${provider.providerType == tm.binding.registry.ProviderType.SAML_IDP || provider.providerType == tm.binding.registry.ProviderType.SAML_SP}">
                            <form id="uploadSAMLMetadataForm" method="post" enctype="multipart/form-data"
                                  class="form-inline">
                                <div class="form-group">
                                    <input id="filename" name="filename" type="file" class="form-control" accept=".xml"/>
                                    <input name="id" type="hidden" value="${provider.organization.id}"/>
                                    <g:hiddenField name="providerId" value="${provider.id}"></g:hiddenField>
                                    <g:hiddenField name="isIdp"
                                                   value="${provider.providerType == tm.binding.registry.ProviderType.SAML_IDP}"></g:hiddenField>
                                </div>
                                <button type="submit" class="btn btn-default">Upload</button>

                            </form>
                            <script>
                                // attach a submit handler to the form
                                $("#uploadSAMLMetadataForm").submit(function (event) {

                                    var form = $("#uploadSAMLMetadataForm").find('input[type="file"]');
                                    var formData = new FormData(this);
                                    formData.append("id", $("#providerId").val());

                                    // stop form from submitting normally
                                    event.preventDefault();

                                    var url = '${createLink(controller: 'provider',  action: 'upload')}';

                                    showSystem(showSAMLSystem, url, formData, $("#isIdp").val());

                                    return false;
                                });

                                function isEmtpy(str) {
                                    return (!str || str.length === 0);
                                }
                            </script>

                            <div style="height: 100%;">
                                <g:if test="${!provider.entityId.empty}">
                                    <div><span
                                            class="label label-warning">A metadata file has already been uploaded. Uploading again will overwrite the currently loaded data.</span>
                                    </div>
                                </g:if>
                            </div>
                        </g:if>
                        <g:elseif test="${provider.providerType == tm.binding.registry.ProviderType.CERTIFICATE}">
                            <form id="uploadCertificateForm" method="post" enctype="multipart/form-data"
                                  class="form-inline">
                                <div class="form-group">
                                    <input id="filename" name="filename" type="file" class="form-control" accept=".pem"/>
                                    <input name="id" type="hidden" value="${provider.organization.id}"/>
                                    <g:hiddenField name="providerId" value="${provider.id}"></g:hiddenField>
                                </div>
                                <button type="submit" class="btn btn-default">Upload</button>

                            </form>
                            <script>
                                // attach a submit handler to the form
                                $("#uploadCertificateForm").submit(function (event) {

                                    var form = $("#uploadCertificateForm").find('input[type="file"]');
                                    var formData = new FormData(this);
                                    formData.append("id", $("#providerId").val());

                                    // stop form from submitting normally
                                    event.preventDefault();

                                    var url = '${createLink(controller: 'provider',  action: 'uploadCertificate')}';

                                    showSystem(showCertificateSystem, url, formData);

                                    return false;
                                });

                                function isEmtpy(str) {
                                    return (!str || str.length === 0);
                                }
                            </script>

                            <div style="height: 100%;">
                                <g:if test="${provider.systemCertificate && !provider.systemCertificate.empty}">
                                    <div><span
                                            class="label label-warning">A certificate file has already been uploaded. Uploading again will overwrite the currently loaded data.</span>
                                    </div>
                                </g:if>
                            </div>
                        </g:elseif>
                        <g:elseif test="${provider.providerType == tm.binding.registry.ProviderType.OIDC_RP || provider.providerType == tm.binding.registry.ProviderType.OIDC_OP}">
                            <form id="uploadOidcMetadataForm" method="post" enctype="multipart/form-data"
                                  class="form-inline">
                                <div class="form-group">
                                    <input id="filename" name="filename" type="file" class="form-control" accept=".json"/>
                                    <input name="id" type="hidden" value="${provider.organization.id}"/>
                                    <g:hiddenField name="providerId" value="${provider.id}"></g:hiddenField>
                                </div>
                                <button type="submit" class="btn btn-default">Upload</button>

                            </form>
                            <script>
                                // attach a submit handler to the form
                                $("#uploadOidcMetadataForm").submit(function (event) {

                                    var form = $("#uploadOidcMetadataForm").find('input[type="file"]');
                                    var formData = new FormData(this);
                                    formData.append("id", $("#providerId").val());

                                    // stop form from submitting normally
                                    event.preventDefault();

                                    var url = '${createLink(controller: 'provider',  action: 'uploadOidcMetadata')}';

                                    showSystem(showOidcSystem, url, formData);

                                    return false;
                                });

                                function isEmtpy(str) {
                                    return (!str || str.length === 0);
                                }
                            </script>

                            <div style="height: 100%;">
                                <g:if test="${provider.openIdConnectMetadata && !provider.openIdConnectMetadata.empty}">
                                    <div><span
                                            class="label label-warning">An OIDC metadata file has already been uploaded. Uploading again will overwrite the currently loaded data.</span>
                                   </div>
                               </g:if>
                            </div>

                        </g:elseif>
                        <g:else>

                        </g:else>

                        <div id="uploadStatusMessage"></div>

                    </div>
                </sec:ifLoggedIn>
                <br>
                <br>

                <g:if test="${provider.providerType == tm.binding.registry.ProviderType.SAML_IDP || provider.providerType == tm.binding.registry.ProviderType.SAML_SP}">

                    <div id="protocol-details"></div>

                    <br>

                    <div id="endpoints-list"></div>

                    <br>

                    <div id="endpoint-details"></div>

                    <br>

                    <div id="idp-attributes"></div>

                    <br>
                </g:if>
                <g:elseif test="${provider.providerType == tm.binding.registry.ProviderType.CERTIFICATE}">
                    <div id="certificate-details"></div>

                    <br>
                </g:elseif>
                <g:elseif test="${provider.providerType == tm.binding.registry.ProviderType.OIDC_RP || provider.providerType == tm.binding.registry.ProviderType.OIDC_OP}">
                    <div id="openid-connect-details"></div>

                    <br>
                </g:elseif>
                <g:else>
                    <div class="alert alert-warning"><h3>Invalid System Type Specified</h3></div>
                    <br>
                </g:else>

            </div>
        </div>
    </div>

    <script>
        $(document).on('click', '.panel-heading button', function (e) {
            var $this = $(this);
            var icon = $this.find('i');
            if (icon.hasClass('glyphicon-plus')) {
                $this.find('i').removeClass('glyphicon-plus').addClass('glyphicon-minus');
            } else {
                $this.find('i').removeClass('glyphicon-minus').addClass('glyphicon-plus');
            }
        });

        function generateSaml2Metadata(providerId) {
            $('#saml2-metadata-generation_status').html('${raw(asset.image(src: 'spinner.gif'))}');

            var url = '${createLink(controller:'provider', action: 'generateSaml2Metadata', id: provider.id)}';

            alert("Warning: This process will generate a fresh metadata object, but it will use the trustmark binding data that is currently cached in the local registry database.");

            $.ajax({
                url: url,
                beforeSend: function () {
                },
                success: function (data, statusText, jqXHR) {

                    $('#viewMetadataLink').removeClass("disabledLink")

                    $('#saml2-metadata-generation_status').html("");
                    $('#saml2-metadata-generation_date').html("Generated at " + data.dateSAMLMetadataGenerated);
                },
                error: function (jqXHR, statusText, errorThrown) {
                    console.log("Error: " + errorThrown);
                }
            });
        }
    </script>

    <br>
</div>

<br>

<sec:ifLoggedIn>
    <div id="systemContacts-status" class='alert alert-success' class='glyphicon glyphicon-ok-circle' style="opacity: 0"></div>
</sec:ifLoggedIn>

<div id="contacts-list"></div>
<br>

<div id="contact-details"></div>

<g:if test="${provider.providerType == tm.binding.registry.ProviderType.SAML_IDP || provider.providerType == tm.binding.registry.ProviderType.SAML_SP ||
    provider.providerType == tm.binding.registry.ProviderType.OIDC_RP || provider.providerType == tm.binding.registry.ProviderType.OIDC_OP}">

    <br>
    <br>

    <div id="attributes-list"></div>
    <br>

    <div id="attribute-details"></div>

    <br>
    <br>

    <div class="panel panel-primary">
        <div class="panel-heading">
            <h4 class="panel-title">Keyword Tags Details</h4>
            <button class="btn btn-primary pull-right" type="button" data-toggle="collapse"
                    data-target="#collapseTags" aria-expanded="false" aria-controls="collapseTags">
                <i class="glyphicon glyphicon-plus"></i>
            </button>
        </div>

        <div class="collapse" id="collapseTags">
            <div class="panel-body">
                <div id="tags-list"></div>
                <br>

                <div id="tag-details"></div>
            </div>
        </div>
    </div>
</g:if>


<br>
<br>

<sec:ifLoggedIn>
    <div id="trustmark-recipient-identifiers-list"></div>
    <div id="trustmark-recipient-identifiers-status"></div>
    <br>
    <div id="trustmark-recipient-identifiers-details"></div>
    <br>
    <br>

</sec:ifLoggedIn>
<div id="partner-systems-tips-list"></div>
<div id="partner-systems-tips-status"></div>
<br>
<div id="partner-systems-tips-details"></div>
<br>
<br>
<div id="conformanceTargetTips-list"></div>
<div id="conformanceTargetTips-status"></div>
<br>
<div id="conformanceTargetTips-details"></div>


%{--    Bind Trustmarks section--}%
<div class="panel panel-primary">
    <div class="panel-heading">
        <h4 class="panel-title">Trustmark Binding Details</h4>
        <button class="btn btn-primary pull-right" type="button" data-toggle="collapse"
                data-target="#collapseTrustmarks" aria-expanded="false" aria-controls="collapseTrustmarks">
            <i class="glyphicon glyphicon-plus"></i>
        </button>
    </div>

    <div class="collapse" id="collapseTrustmarks">
        <div class="panel-body">

            <table class='table table-condensed table-striped table-bordered'>
                <tr>
                    <td style='width: auto;'><b>Number of Trustmarks Bound</b></td>
                    <td id="numberOfTrustmarksBound" style='width: auto;'>${provider.trustmarks.size()}</td>
                </tr>
                <tr>
                    <td style='width: auto;'><b>Number of Conformance Target TIPs</b></td>
                    <td id="numberOfConformanceTargetTIPs"
                        style='width: auto;'>${provider.conformanceTargetTips.size()}</td>
                </tr>
            </table>
            <br>

            <sec:ifLoggedIn>
                <g:if test="${provider.trustmarks.size() == 0}">
                    <button class="btn btn-info bind-trustmark-button"
                            onclick="bindTrustmarks(${provider.id});">Bind Trustmarks</button>
                </g:if>
                <g:else>
                    <button class="btn btn-info bind-trustmark-button"
                            onclick="bindTrustmarks(${provider.id});">Refresh Trustmark Bindings</button>
                </g:else>

                <div id="cancelTrusmarkBindings"></div>

                <div id="bindTrustmarkStatusMessage"></div>
            </sec:ifLoggedIn>
            <br>

            <a class="tm-right" href="#" onclick="toggleIt('trustmarks-list');
            return false;"><< Trustmarks</a><br>

            <div id="trustmarks-list"></div>

        </div>
    </div>
</div>

</body>
</html>