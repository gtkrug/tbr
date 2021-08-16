<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <script type="text/javascript">
        var ORG_VIEW_BASE_URL = "${createLink(controller:'organization', action: 'view')}/";

        $(document).ready(function()  {

            var isLoggedIn = ${isLoggedIn};

            getContacts(${organization.id});

            getProviders(${organization.id});

            if (isLoggedIn) {
                getRepos(${organization.id});
                getTrustmarkRecipientIdentifiers(${organization.id})
            }
        });

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

        let clearForm = function()  {
            setSuccessStatus("<b>Successfully saved system provider.</b>");
            hideIt('new-provider');
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
            <sec:ifNotLoggedIn>
                <span>${organization.displayName}</span>
            </sec:ifNotLoggedIn>
            <sec:ifLoggedIn>
                <input id="org-display" type="text" size="50" value="${organization.displayName}">
            </sec:ifLoggedIn>
        </td>
    </tr>
    <tr>
        <td style='width: auto;'>
            <b>URL</b>
        </td>
        <td style='width: auto;'>
            <sec:ifNotLoggedIn>
                <span><a href="${organization.siteUrl}" target="_blank">${organization.siteUrl}</a></span>
            </sec:ifNotLoggedIn>
            <sec:ifLoggedIn>
                <input id="org-url" type="url" size="50" value="${organization.siteUrl}">
            </sec:ifLoggedIn>
        </td>
    </tr>
    <tr>
        <td style='width: auto;'>
            <b>Description</b>
        </td>
        <td style='width:auto;'>
            <sec:ifNotLoggedIn>
                <span>${organization.description}</span>
            </sec:ifNotLoggedIn>
            <sec:ifLoggedIn>
                <textarea id="org-description" cols="60" rows="4">${organization.description}</textarea>
            </sec:ifLoggedIn>
        </td>
    </tr>
</table>

<sec:ifLoggedIn>
    <button class="btn btn-info" onclick="updateOrganization('${organization.id}',
        document.getElementById('org-url').value, document.getElementById('org-description').value, document.getElementById('org-display').value);">Update</button>
    <br>
    <br>
</sec:ifLoggedIn>

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
</sec:ifLoggedIn>


<div id="providers"></div>
<br>
<div id="new-provider"></div>
<br>

</body>
</html>