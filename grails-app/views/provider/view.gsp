<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <style type="text/css">
    </style>
    <script type="text/javascript">
        $(document).ready(function()  {
            showProvider(${provider.id});
        });

        let selectOrganizations = function(id)  {
            list("${createLink(controller:'organization', action: 'list')}"
                , curriedSelectOrganizations('select-organization')(id)
                , {name: 'ALL'});
        }

        let selectContactTypes = function(id)  {
            list("${createLink(controller:'contact', action: 'types')}"
                , curriedContactTypes('select-contact-types')(id)
                , {name: 'ALL'});
        }

        let getTrustmarks = function(pid) {
            list("${createLink(controller:'trustmark', action: 'list')}"
                , trustmarkResults(pid)
                , { id: pid }
            );
        }

        let trustmarkResults = function (pId)  {
            return function(results)  {
                renderTrustmarkOffset = curriedTrustmark('trustmarks-list')(results);
                renderTrustmarkOffset(0);
            }
        }

        /**
         * tags functionality for editing
         * @param pid
         */
        let getTags = function(pid) {
            list("${createLink(controller:'tag', action: 'list')}"
                , tagResults
                , { id: pid }
            );
            hideIt('tag-details');
        }

        let tagResults = function (results)  {
            renderTagOffset = curriedTag('tags-list')
            ({
                editable: true
                , fnAdd: function(){renderTagForm('tag-details'
                        , function(){insertTag(document.getElementById('tagName').value, ${provider.id});});}
                , fnRemove: function(){removeTags('${provider.id}');}
                , title: 'Tags'
            })
            (results);
            renderTagOffset(0);
        }

        let insertTag = function(tag, pid)  {
            add("${createLink(controller:'tag', action: 'add')}"
                , function(data){getTags(pid);}
                , { pId: pid, tag: tag }
            );
        }

        let removeTags = function(pid)  {
            getCheckedIds('edit-tags', function(list){
                update("${createLink(controller:'tag', action: 'delete')}"
                    , function (data){ getTags(pid); }
                    , { ids: list, pid: pid }
                );
            });
        }

        /**
         * contacts functionality for editing contacts
         * @param pid
         */
        let getContacts = function(pid) {
            list("${createLink(controller:'contact', action: 'list')}"
                , contactResults
                , { pid: pid }
            );
            hideIt('contact-details');
        }

        let populateContactForm = function(contact) {
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
            document.getElementById('ctypes').focus();
        }

        let getContactDetails = function(id)  {
            get("${createLink(controller:'contact', action: 'get')}"
                , contactDetail('contact-details')(populateContactForm)
                (function(){updateContact(id, document.getElementById('lastName').value
                    , document.getElementById('firstName').value
                    , document.getElementById('emailAddr').value
                    , document.getElementById('phoneNbr').value
                    , document.getElementById('ctypes').options[document.getElementById('ctypes').selectedIndex].value
                    , ${provider.organization.id});})
                , { id: id }
            );
        }

        let contactResults = function (results)  {
                renderContactOffset = curriedContact('contacts-list')
                ({
                    editable: true
                    , fnAdd: function(){renderContactForm('contact-details', populateContactForm
                            , function(){insertContact(document.getElementById('lastName').value
                                , document.getElementById('firstName').value
                                , document.getElementById('emailAddr').value
                                , document.getElementById('phoneNbr').value
                                , document.getElementById('ctypes').options[document.getElementById('ctypes').selectedIndex].value
                                , ${provider.id});}, {id:0});}
                    , fnRemove: function(){removeContacts('${provider.id}');}
                    , fnDraw: drawContacts
                    , hRef: 'javascript:getContactDetails'
                    , title: 'Contacts'
                })
                (results);
                renderContactOffset(0);
        }

        let insertContact = function(lname, fname, email, phone, type, pid)  {
            if(checkContact(lname, fname, email, phone, type, ${provider.organization.id}))  {
                add("${createLink(controller:'contact', action: 'add')}"
                    , function(data){getContacts(pid);}
                    , { lname: lname
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

        let updateContact = function(id, lname, fname, email, phone, type, orgId)  {
            if(checkContact(lname, fname, email, phone, type, orgId))  {
                update("${createLink(controller:'contact', action: 'update')}"
                    , function(data){getContacts(${provider.id});}
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
                scroll(0,0);
            }
        }

        let removeContacts = function(pid)  {
            getCheckedIds('edit-contacts', function(list) {
                update("${createLink(controller:'contact', action: 'delete')}"
                    , function (data){getContacts(pid);}
                    , { ids: list, pid: pid }
                );
            });
        }

        /**
         * attribute editing functionality
         * @param pid
         */
        let removeAttributes = function(pid)  {
            getCheckedIds('edit-attributes', function(list){
                update("${createLink(controller:'attribute', action: 'delete')}"
                    , function(data){getAttributes(pid);}
                    , { ids: list, pid: pid }
                );
            });
        }

        let getAttributes = function(pid) {
            list("${createLink(controller:'attribute', action: 'list')}"
                , attributeResults
                , { id: pid }
            );
            hideIt('attribute-details');
        }

        let attributeResults = function (results)  {
            renderAttributeOffset = curriedAttribute('attributes-list')
            ({
                editable: true
                , fnAdd: function(){renderAttributeForm('attribute-details'
                        ,function(){insertAttribute(document.getElementById('attrName').value
                            , document.getElementById('attrValue').value
                            , ${provider.id});});}
                , fnRemove: function(){removeAttributes(${provider.id});}
                , fnDraw: drawAttribute
                , title: 'Attributes'
            })
            (results);
            renderAttributeOffset(0);
        }

        let insertAttribute = function(name, value, pid)  {
            add("${createLink(controller:'attribute', action: 'add')}"
                , function(data){getAttributes(pid);}
                , { name: name
                    , value: value
                    , pId: pid
                }
            );
        }

        /**
         * endpoint editing functionality
         * @param pid
         */
        let removeEndpoints = function(pid)  {
            getCheckedIds('edit-endpoints', function(list){
                update("${createLink(controller:'endPoint', action: 'delete')}"
                    , function(data){getEndpoints(pid);}
                    , { ids: list, pid: pid }
                );
            });
        }

        let getEndpoints = function(pid) {
            list("${createLink(controller:'endPoint', action: 'list')}"
                , endPointResults
                , { id: pid }
            );
            hideIt('endpoint-details');
        }

        let endPointResults = function (results)  {
            renderEndpointOffset = curriedEndpoint('endpoints-list')
            ({
                editable: true
                , fnAdd: function(){renderEndpointForm('endpoint-details'
                        , function(){insertEndpoint(document.getElementById('endptName').value
                            , document.getElementById('endptType').value
                            , document.getElementById('endptUrl').value
                            , ${provider.id});});}
                , fnRemove: function(){removeEndpoints('${provider.id}');}
                , fnDraw: drawEndpoints
                , title: 'Endpoints'
            })
            (results);
            renderEndpointOffset(0);
        }

        let insertEndpoint = function(name, binding, url, pid)  {
            add("${createLink(controller:'endPoint', action: 'add')}"
                , function(data){getEndpoints(pid);}
                , { name: name
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
        let removeConformanceTargetTips = function(pid)  {
            getCheckedIds('edit-conformanceTargetTips', function(list){
                update("${createLink(controller:'conformanceTargetTip', action: 'delete')}"
                    , function(data){getConformanceTargetTips(pid);}
                    , { ids: list, pid: pid }
                );
            });
        }

        let getConformanceTargetTips = function(pid) {
            list("${createLink(controller:'conformanceTargetTip', action: 'list')}"
                , conformanceTargetTipResults
                , { id: pid }
            );
            hideIt('conformanceTargetTips-details');
        }

        // {function(*=): function(*=): function(*=): *}
        let conformanceTargetTipResults = function (results)  {
            renderConformanceTargetTipOffset = curriedConformanceTargetTip('conformanceTargetTips-list')
            ({
                editable: true
                , fnAdd: function(){renderConformanceTargetTipForm('conformanceTargetTips-details'
                    , function(){insertConformanceTargetTip(document.getElementById('conformanceTargetTipIdentifier').value
                        , ${provider.id});});}
                , fnRemove: function(){removeConformanceTargetTips('${provider.id}');}
                , fnDraw: drawConformanceTargetTips
                , title: 'Conformance Target Trust Interoperability Profiles'
            })
            (results);
            renderConformanceTargetTipOffset(0);
        }

        let insertConformanceTargetTip = function(identifier, pid)  {
            add("${createLink(controller:'conformanceTargetTip', action: 'add')}"
                , function(data){getConformanceTargetTips(pid);}
                , { identifier: identifier
                , pId: pid
                }
            );
        }

        let showProvider = function(pid)  {
            getTrustmarks(pid);
            getTags(pid);
            getContacts(pid);
            getEndpoints(pid);
            getAttributes(pid);
            getConformanceTargetTips(pid);
            hideIt('trustmarks-list');
        }

        let bindTrustmarks = function(providerId) {
            console.log("bindTrustmarks for provider: " + providerId);

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
                    console.log("Successfully received bindTrustmarks response: "+JSON.stringify(data) + "for provider id: " + providerId);

                    $('#bindTrustmarkStatusMessage').html("Status: " + data['message']);
                },
                error: function (jqXHR, statusText, errorThrown) {
                    console.log("Error: " + errorThrown);

                    $('#bindTrustmarkStatusMessage').html(errorThrown);
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
<h5><b>${provider.entityId}</b></h5>
    <table class='table table-condensed table-striped table-bordered'>
        <tr>
            <td style='width: auto;'><b>Name</b></td>
            <td style='width: auto;'>${provider.name}</td>
    </tr><tr>
        <td style='width: auto;'><b>Type</b></td>
        <td style='width: auto;'>${provider.providerType}</td>
    </tr><tr>
        <td style='width: auto;'><b>Entity ID</b></td>
        <td style='width: auto;'>${provider.entityId}</td>
    </tr>
    </table>
    <table class='table table-condensed table-striped table-bordered'>
    <tr>
        <td style='width: auto;'><b>Protocol</b></td>
        <td style='width: auto;'><g:each in="${provider.protocols}" var="pt">${pt}<br></g:each></td>
    </tr><tr>
        <td style='width: auto;'><b>Name ID Format</b></td>
        <td style='width: auto;'><g:each in="${provider.nameFormats}" var="fm">${fm}<br></g:each></td>
    </tr><tr>
        <td style='width: auto;'><b>Signing Certificate</b></td>
        <td style='width: auto;'><a href='${createLink(controller:'provider', action: 'signCertificate', id: provider.id)}'>view</a></td>
    </tr><tr>
        <td style='width: auto;'><b>Encryption Certificate</b></td>
        <td style='width: auto;'><a href='${createLink(controller:'provider', action: 'encryptCertificate', id: provider.id)}'>view</a></td>
    </tr>
    </table>
    <div id="endpoints-list"></div>
    <br>
    <div id="endpoint-details"></div>

    <br><br>

    <div id="tags-list"></div>
    <br>
    <div id="tag-details"></div>
    <br><br>
    <div id="contacts-list"></div>
    <br>
    <div id="contact-details"></div>
    <br><br>
    <div id="attributes-list"></div>
    <br>
    <div id="attribute-details"></div>
    <br>
    <br>

    <div id="conformanceTargetTips-list"></div>
    <br>
    <div id="conformanceTargetTips-details"></div>

%{--    Bind Trustmarks button--}%
    <button class="btn btn-info" onclick="bindTrustmarks(${provider.id});">Bind Trustmarks</button>

    <div id="bindTrustmarkStatusMessage"></div>

    <br>

    <a class="tm-right" href="#" onclick="toggleIt('trustmarks-list');return false;"><< Trustmarks</a><br>
    <div id="trustmarks-list"></div>
</body>
</html>