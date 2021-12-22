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

        $(document).ready(function()  {

            var isLoggedIn = ${isLoggedIn};

            getContacts(${organization.id});

            getProviders(${organization.id});

            if (isLoggedIn) {
                getRepos(${organization.id});
                getTrustmarkRecipientIdentifiers(${organization.id})
                getPartnerSystemsTips(${organization.id})
                getBoundTrustmarks(${organization.id});
            }

            hideIt('trustmarks-list');
        });

        let getBoundTrustmarks = function (oid) {
            list("${createLink(controller:'organization', action: 'trustmarks')}"
                , trustmarkResults(oid)
                , {id: oid}
            );
        }

        let trustmarkResults = function (pId) {
            return function (results) {
                renderTrustmarkOffset = curriedTrustmark('trustmarks-list')(results);
                renderTrustmarkOffset(0);
            }
        }

        /**
         * contacts functionality for editing contacts
         * @param pid
         */
        let getContacts = function(oid) {
            list("${createLink(controller:'contact', action: 'list')}"
                , contactResults
                , { id: oid }
            );
            hideIt('contact-details');
        }

        let populateContactForm = function(contact) {
            hideIt('select-organization-label');
            hideIt('select-organization');
            if(contact.id === 0)  {
                selectContactTypes('0');
            } else {
                selectContactTypes(contact.type.name)
                document.getElementById('lastName').value = contact.lastName;
                document.getElementById('firstName').value = contact.firstName;
                document.getElementById('emailAddr').value = contact.email;
                document.getElementById('phoneNbr').value = contact.phone;
            }
            document.getElementById('lastName').focus();
        }

        let getContactDetails = function(id)  {
            get("${createLink(controller:'contact', action: 'get')}"
                , contactDetail('contact-details')(populateContactForm)
                (function(){updateContact(id, document.getElementById('lastName').value
                    , document.getElementById('firstName').value
                    , document.getElementById('emailAddr').value
                    , document.getElementById('phoneNbr').value
                    , document.getElementById('ctypes').options[document.getElementById('ctypes').selectedIndex].value
                    , ${organization.id});})
                , { id: id }
            );
        }

        let contactResults = function (results)  {
            renderContactOffset = curriedContact('contacts-list')
            ({
                editable: results.editable
                , fnAdd: function(){renderContactForm('contact-details', populateContactForm
                    , function(){insertContact(document.getElementById('lastName').value
                        , document.getElementById('firstName').value
                        , document.getElementById('emailAddr').value
                        , document.getElementById('phoneNbr').value
                        , document.getElementById('ctypes').options[document.getElementById('ctypes').selectedIndex].value
                        , ${organization.id});}, {id:0});}
                , fnRemove: function(){removeContacts('${organization.id}');}
                , fnDraw: drawContacts
                , hRef: 'javascript:getContactDetails'
                , title: 'Points of Contact'
                , includeOrganizationColumn: false
            })
            (results);
            renderContactOffset(0);
        }

        let insertContact = function(lname, fname, email, phone, type, oid)  {
            if(checkContact(lname, fname, email, phone, type, ${organization.id}))  {
                add("${createLink(controller:'contact', action: 'add')}"
                    , function(data){getContacts(oid);}
                    , { lname: lname
                        , fname: fname
                        , email: email
                        , phone: phone
                        , organizationId: ${organization.id}
                        , type: type
                        , id: oid
                    }
                );
            }
        }

        let updateContact = function(id, lname, fname, email, phone, type, orgId)  {
            if(checkContact(lname, fname, email, phone, type, orgId))  {
                update("${createLink(controller:'contact', action: 'update')}"
                    , function(data){getContacts(${organization.id});}
                    , {
                        id: id
                        , lname: lname
                        , fname: fname
                        , email: email
                        , phone: phone
                        , organizationId: ${organization.id}
                        , type: type
                    });
            } else {
                scroll(0,0);
            }
        }

        let removeContacts = function(oid)  {
            getCheckedIds('edit-contacts', function(list) {
                update("${createLink(controller:'contact', action: 'delete')}"
                    , function (data){getContacts(oid);}
                    , { ids: list, id: oid }
                );
            });
        }

        let selectContactTypes = function(id)  {
            list("${createLink(controller:'contact', action: 'types')}"
                , curriedContactTypes('select-contact-types')(id)
                , {name: 'ALL'});
        }


        let selectProviderTypes = function()  {
            list("${createLink(controller:'provider', action: 'types')}"
                , curriedProviderTypes('select-provider-types')
                , {name: 'ALL'});
        }

        let removeProviders = function()  {
            if (confirm("This operation will delete all data associated with the system. Do you want to continue?")) {
                getCheckedIds('edit-providers', function (list) {
                    update("${createLink(controller:'provider', action: 'delete')}"
                        , function (data) {
                            getProviders('${organization.id}');
                        }
                        , {ids: list, oid: ${organization.id}}
                    );
                });
            }
        }

        let insertProvider = function(name, type)  {
            add("${createLink(controller:'provider', action: 'add')}"
                , function(data){getProviders(${organization.id});}
                , {
                    orgid: ${organization.id}
                    , type: type
                    , name: name
                    , entity: ""
                }
            );
            clearForm();
        }

        let addProvider = function(target, fn)  {
            renderProviderForm(target, fn);
            selectProviderTypes();
            document.getElementById('providerName').focus();
        }

        let getProviders = function(oid) {
            list("${createLink(controller:'provider', action: 'list')}"
                , providerResults
                , { orgid: oid }
            );
        }

        let providerResults = function(results)  {
            renderProviderOffset = curriedProvider('providers')
            ({
                editable: results.editable
                , fnAdd: function(){addProvider('new-provider'
                    , function(){
                        insertProvider(document.getElementById('providerName').value, document.getElementById('pType').options[document.getElementById('pType').selectedIndex].value);})}
                , fnRemove: removeProviders
                , fnDraw: drawProvider
                , title: 'Systems'
                , hRef: 'javascript:getDetails'
            })
            (results)
            renderProviderOffset(0);
        }


        // Assessment Tool URLs
        let getRepos = function(oid) {
            list("${createLink(controller:'organization', action: 'repos')}"
                , repoResults
                , { oid: oid }
            );
            hideIt('assessment-tool-url-details');
        }

        let addRepo = function(repoNm) {
            if(checkRepo(repoNm)) {
                add("${createLink(controller:'organization', action: 'addRepo')}"
                    , function (data) {
                        if (data.statusMessage && data.statusMessage.length > 0) {
                            repoStatus(data.statusMessage)
                        }
                        getRepos(${organization.id});
                    }
                    , {
                        orgid: ${organization.id}
                        , name: repoNm
                    }
                );
            }
        }

        let removeRepos = function(oid)  {
            getCheckedIds('edit-assessmentToolRepo', function(list) {
                update("${createLink(controller:'organization', action: 'deleteRepos')}"
                    , function (data){getRepos(oid);}
                    , { ids: list, orgid: oid }
                );
            });
        }

        let deleteRepo = function(repoid)  {
            console.log("deleteRepo: " + repoid.toString());

            add("${createLink(controller:'organization', action: 'deleteRepo')}"
                , function(data){getRepos(${organization.id});}
                , {
                    orgid: ${organization.id}
                  , rid: repoid
                }
            );
        }

        let repoResults = function(results)  {
            renderRepoOffset = curriedRepos('assessment-tool-url-list')
            ({
                editable: results.editable
                , fnAdd: function(){renderAssessmentToolReposForm('assessment-tool-url-details'
                    , populateRepoForm
                    , function(){
                        addRepo(document.getElementById('assessmentToolUrlRepo').value);}, {id:0})}
                , fnRemove: function(){removeRepos('${organization.id}');}
                , fnDraw: drawRepos
                , title: 'Assessment Tool URLs'
                , hRef: 'javascript:getRepoDetails'
            })
            (results)
            renderRepoOffset(0);
        }

        let populateRepoForm = function(repo) {
            if(repo.id !== 0) {
                document.getElementById('assessmentToolUrlRepo').value = repo.repoUrl;
                document.getElementById('assessmentToolUrlRepo').focus();
            }
        }

        let getRepoDetails = function(id)  {
            get("${createLink(controller:'organization', action: 'getRepo')}"
                , repoDetail('assessment-tool-url-details')(populateRepoForm)
                (function(){updateRepo(id, document.getElementById('assessmentToolUrlRepo').value
                    , ${organization.id});})
                , { orgid: ${organization.id}, rid:id }
            );
        }

        let updateRepo = function(id, repoUrl, orgId)  {
            if(checkRepo(repoUrl))  {
                update("${createLink(controller:'organization', action: 'updateRepo')}"
                    , function(data){getRepos(${organization.id});}
                    , {
                        id: id
                        , repoUrl: repoUrl
                        , organizationId: ${organization.id}
                    });
            } else {
                scroll(0,0);
            }
        }

        let repoStatus = function(status) {
            $('#assessment-tool-urls-status').text(status);
            $('#assessment-tool-urls-status').fadeTo(200, 1);
            $('#assessment-tool-urls-status').delay(3000).fadeTo(300, 0);
        }

        // Trustmark Recipient Identifiers

        let getTrustmarkRecipientIdentifiers = function(oid) {
            list("${createLink(controller:'organization', action: 'trustmarkRecipientIdentifiers')}"
                , trustmarkRecipientIdentifierResults
                , { oid: oid }
            );
            hideIt('trustmark-revipient-identifiers-details');
        }

        let addTrustmarkRecipientIdentifier = function(trustmarkRecipientIdentifier)  {
            if(checkTrustmarkRecipientIdentifier(trustmarkRecipientIdentifier)) {
                add("${createLink(controller:'organization', action: 'addTrustmarkRecipientIdentifier')}"
                    , function (data) {
                        getTrustmarkRecipientIdentifiers(${organization.id});
                    }
                    , {
                        orgid: ${organization.id}
                        , identifier: trustmarkRecipientIdentifier
                    }
                );
            }
        }

        let removeTrustmarkRecipientIdentifiers = function(oid)  {
            getCheckedIds('edit-trustmarkRecipientIdentifier', function(list) {
                update("${createLink(controller:'organization', action: 'deleteTrustmarkRecipientIdentifiers')}"
                    , function (data){getTrustmarkRecipientIdentifiers(oid);}
                    , { ids: list, orgid: oid }
                );
            });
        }

        let deleteTrustmarkRecipientIdentifier = function(tmrid)  {
            add("${createLink(controller:'organization', action: 'deleteTrustmarkRecipientIdentifier')}"
                , function(data){getTrustmarkRecipientIdentifiers(${organization.id});}
                , {
                    orgid: ${organization.id}
                    , tmrid: tmrid
                }
            );
        }

        let trustmarkRecipientIdentifierResults = function(results)  {
            renderTrustmarkRecipientIdentifiersOffset = curriedTrustmarkRecipientIdentifier('trustmark-revipient-identifiers-list')
            ({
                editable: results.editable
                , fnAdd: function(){renderTrustmarkRecipientIdentifiersForm('trustmark-revipient-identifiers-details'
                    , populateTrustmarkRecipientIdentifiersForm
                    , function(){
                        addTrustmarkRecipientIdentifier(document.getElementById('trustmarkRecipientIdentifier').value);}, {id:0})}
                , fnRemove: function(){removeTrustmarkRecipientIdentifiers('${organization.id}');}
                , fnDraw: drawTrustmarkRecipientIdentifier
                , title: 'Trustmark Recipient Identifiers'
                , hRef: 'javascript:getTrustmarkRecipientIdentifierDetails'
            })
            (results)
            renderTrustmarkRecipientIdentifiersOffset(0);
        }

        let populateTrustmarkRecipientIdentifiersForm = function(trustmarkRecipientIdentifier) {
            if(trustmarkRecipientIdentifier.id !== 0) {
                document.getElementById('trustmarkRecipientIdentifier').value = trustmarkRecipientIdentifier.trustmarkRecipientIdentifierUrl;
                document.getElementById('trustmarkRecipientIdentifier').focus();
            }
        }

        let getTrustmarkRecipientIdentifierDetails = function(id)  {
            get("${createLink(controller:'organization', action: 'getTrustmarkRecipientIdentifier')}"
                , trustmarkRecipientIdentifierDetail('trustmark-revipient-identifiers-details')(populateTrustmarkRecipientIdentifiersForm)
                (function(){updateTrustmarkRecipientIdentifier(id, document.getElementById('trustmarkRecipientIdentifier').value
                    , ${organization.id});})
                , { orgid: ${organization.id}, rid:id }
            );
        }

        let updateTrustmarkRecipientIdentifier = function(id, trustmarkRecipientIdentifier, orgId)  {
            if(checkTrustmarkRecipientIdentifier(trustmarkRecipientIdentifier))  {
                update("${createLink(controller:'organization', action: 'updateTrustmarkRecipientIdentifier')}"
                    , function(data){getTrustmarkRecipientIdentifiers(${organization.id});}
                    , {
                        id: id
                        , trustmarkRecipientIdentifier: trustmarkRecipientIdentifier
                        , organizationId: ${organization.id}
                    });
            } else {
                scroll(0,0);
            }
        }

        // Partner Systems TIPs

        /**
         * Partner Systems TIPs editing functionality
         * @param pid
         */
        let removePartnerSystemsTips = function (oid) {
            getCheckedIds('edit-partnerSystemsTips', function (list) {
                update("${createLink(controller:'organization', action: 'deletePartnerSystemsTips')}"
                    , function (data) {
                        $('#partnerSystemsTips-status').html('');
                        getPartnerSystemsTips(oid);
                    }
                    , {ids: list, oid: oid}
                );
            });
        }

        let getPartnerSystemsTips = function (oid) {
            list("${createLink(controller:'organization', action: 'partnerSystemsTips')}"
                , partnerSystemsTipResults
                , {oid: oid}
            );
            hideIt('partner-systems-tips-details');
        }

        // {function(*=): function(*=): function(*=): *}
        let partnerSystemsTipResults = function (results) {
            renderPartnerOrganizationTipOffset = curriedPartnerOrganizationTip('partner-systems-tips-list')
            ({
                editable: results.editable
                ,
                fnAdd: function () {
                    $('#partner-systems-tips-status').html('');
                    renderPartnerOrganizationTipForm('partner-systems-tips-details'
                        , function () {
                            insertPartnerSystemsTip(document.getElementById('partnerSystemsTipIdentifier').value
                                , ${organization.id});
                        });
                }
                ,
                fnRemove: function () {
                    removePartnerSystemsTips('${organization.id}');
                }
                ,
                fnDraw: drawPartnerSystemsTips
                ,
                title: 'Partner Organization Trust Interoperability Profiles'
                ,
                titleTooltip: 'This list of trust interoperability profiles (TIPs) represents ' +
                    'the requirements of this organization for potential partner organizations that will engage in trusted information exchanges.'
            })
            (results);
            renderPartnerOrganizationTipOffset(0);
        }

        let insertPartnerSystemsTip = function (identifier, oid) {
            $('#partner-systems-tips-status').html('');
            add("${createLink(controller:'organization', action: 'addPartnerSystemsTip')}"
                , function (data) {

                    let html = "<br>";
                    if (!isEmtpy(data.status['SUCCESS'])) {
                        html += "<div class='alert alert-success' class='glyphicon glyphicon-ok-circle'>" + data.status['SUCCESS'] + "</div>";
                    }

                    if (!isEmtpy(data.status['WARNING'])) {
                        html += "<div class='alert alert-warning' class='glyphicon glyphicon-warning-sign'>" + data.status['WARNING'] + "</div>";
                    }

                    if (!isEmtpy(data.status['ERROR'])) {
                        html += "<div class='alert alert-danger' class='glyphicon glyphicon-exclamation-sign'>" + data.status['ERROR'] + "</div>";
                    }

                    $('#partner-systems-tips-status').html(html);

                    getPartnerSystemsTips(oid);
                }
                , {
                    identifier: identifier
                    , oid: oid
                }
            );
        }

        /**
         * render a form for adding a conformance target tip
         */
        let renderInternalPartnerSystemsTipForm = function (target, fn) {
            let html = "<input id='partnerSystemsTipIdentifier' size='80' type='text' class='form-control tm-margin' placeholder='Enter Partner Systems TIP Identifier' /><span style='color:red;'>&nbsp;&nbsp;*</span><br>";
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

        let updateOrganization = function(orgId, url, desc, display)  {
            update("${createLink(controller:'organization', action: 'update')}"
                , function(data){}
                , { url: url
                  , desc: desc
                  , id: orgId
                  , display: display
                }
            );
        }

        let bindTrustmarks = function (organizationId) {
            $('#bindTrustmarkStatusMessage').html('Started the trustmark binding process; trustmarks should be available once bound. ${raw(asset.image(src: 'spinner.gif'))}');

            // reset the state variables
            // initTrustmarkBindingState(organizationId);

            STOP_LOOP = false;
            // trustmarkBindingStatusLoop(organizationId);

            var url = '${createLink(controller: 'organization',  action: 'bindTrustmarks')}';
            $.ajax({
                url: url,
                dataType: 'json',
                data: {
                    id: organizationId,
                    format: 'json'
                },
                beforeSend: function () {
                },
                success: function (data, statusText, jqXHR) {
                    updateTrustmarkBindingDetails(organizationId);
                },
                error: function (jqXHR, statusText, errorThrown) {
                    console.log("Error: " + errorThrown);

                    $('#bindTrustmarkStatusMessage').html(errorThrown);
                },
                timeout: 120000 // 2 minutes
            });
        }

        let updateTrustmarkBindingDetails = function (organizationId) {

            $('#bindTrustmarkStatusMessage').html('');

            var url = '${createLink(controller: 'organization',  action: 'updateTrustmarkBindingDetails')}';
            $.ajax({
                url: url,
                dataType: 'json',
                async: false,
                data: {
                    id: organizationId,
                    format: 'json'
                },
                beforeSend: function () {
                },
                success: function (data, statusText, jqXHR) {

                    // reload trustmarks
                    getBoundTrustmarks(organizationId);

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

        let clearForm = function()  {
            setSuccessStatus("<b>Successfully saved system provider.</b>");
            hideIt('new-provider');
        }

        function isEmtpy(str) {
            return (!str || str.length === 0);
        }
    </script>
    <meta charset="UTF-8">
    <meta name="layout" content="main"/>
    <title>Organization</title>
</head>
<body>
<div id="status-header"></div>
<h4><b>${organization.name}</b></h4>
<br>
<table class='table table-condensed table-striped table-bordered'>
    <tr>
        <td colspan='2' style='text-align: center'>
            <b>Basic Organization Information</b>
        </td>
    </tr>

    <tr>
        <td style='width: auto;'>
            <b>Display Name</b>
        </td>
        <td style='width: auto;'>
            <g:ifReadOnly orgId="${organization.id}">
                <span>${organization.displayName}</span>
            </g:ifReadOnly>
            <g:ifNotReadOnly orgId="${organization.id}">
                <input id="org-display" type="text" size="50" value="${organization.displayName}">
            </g:ifNotReadOnly>
        </td>
    </tr>
    <tr>
        <td style='width: auto;'>
            <b>URL</b>
        </td>
        <td style='width: auto;'>
            <g:ifReadOnly orgId="${organization.id}">
                <span><a href="${organization.siteUrl}" target="_blank">${organization.siteUrl}</a></span>
            </g:ifReadOnly>
            <g:ifNotReadOnly orgId="${organization.id}">
                <input id="org-url" type="url" size="50" value="${organization.siteUrl}">
            </g:ifNotReadOnly>
        </td>
    </tr>
    <tr>
        <td style='width: auto;'>
            <b>Description</b>
        </td>
        <td style='width:auto;'>
            <g:ifReadOnly orgId="${organization.id}">
                <span>${organization.description}</span>
            </g:ifReadOnly>
            <g:ifNotReadOnly orgId="${organization.id}">
                <textarea id="org-description" cols="60" rows="4">${organization.description}</textarea>
            </g:ifNotReadOnly>
        </td>
    </tr>
</table>

<g:ifNotReadOnly orgId="${organization.id}">
    <button class="btn btn-info" onclick="updateOrganization('${organization.id}',
        document.getElementById('org-url').value, document.getElementById('org-description').value, document.getElementById('org-display').value);">Update</button>
    <br>
    <br>
</g:ifNotReadOnly>

<br>
<br>

<div id="contacts-list"></div>
<br>
<div id="contact-details"></div>

<br>

<sec:ifLoggedIn>

    <div id="assessment-tool-urls-status" class='alert alert-danger p-1' style="opacity: 0; margin-bottom: 0; padding: 5px;"></div>
    <div id="assessment-tool-url-list"></div>
    <br>
    <div id="assessment-tool-url-details"></div>
    <br>
    <br>
    <div id="trustmark-revipient-identifiers-list"></div>
    <br>
    <div id="trustmark-revipient-identifiers-details"></div>
    <br>
    <br>
    <div id="partner-systems-tips-list"></div>
    <br>
    <div id="partner-systems-tips-status"></div>
    <div id="partner-systems-tips-details"></div>
    <br>
    <br>
</sec:ifLoggedIn>


<div id="providers"></div>
<br>
<div id="new-provider"></div>
<br>
<br>

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
</script>

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
                    <td id="numberOfTrustmarksBound" style='width: auto;'>${organization.trustmarks.size()}</td>
                </tr>
            </table>
            <br>

            <sec:ifLoggedIn>
                <g:if test="${organization.trustmarks.size() == 0}">
                    <button class="btn btn-info bind-trustmark-button"
                            onclick="bindTrustmarks(${organization.id});">Bind Trustmarks</button>
                </g:if>
                <g:else>
                    <button class="btn btn-info bind-trustmark-button"
                            onclick="bindTrustmarks(${organization.id});">Refresh Trustmark Bindings</button>
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