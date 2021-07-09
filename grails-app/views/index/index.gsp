<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="layout" content="main"/>
    <title>Home</title>
    <style type="text/css">
        .mustLoginDetails {
            text-align: center;
            margin-top: 5em;
        }
        .mustLoginText {
            margin-bottom: 1em;
        }
        .reportContainer {
        }
        .reportLink {
            color: #333;
        }
        .reportLink :HOVER {
            color: #333;
        }
        .reportLink:HOVER .reportTitle {
            color: #337ab7;
        }
        .reportIcon {
            font-size: 55px;
        }
        .reportTextContainer {
            padding-top: 1em;
        }
    </style>
    <script type="text/javascript">

        var LOGGED_IN = false;
        var ORG_VIEW_BASE_URL = "${createLink(controller:'organization', action: 'view')}/";


        let orgOffset = function (){};

        let organizationDetail = curryThree(renderOrganizationForm);

        let listOrganizations = function(data)  {
            list("${createLink(controller:'organization', action: 'list')}"
                , renderResults
                , {name: 'ALL'});
        }

        let renderResults = function(results)  {
            renderOrganizationOffset = curriedOrganization('organization-table')
            ({
                editable: true
                , fnAdd: function(){renderOrganizationForm('organization'
                    , function(){addOrganization(document.getElementById('org_name').value
                        , document.getElementById('org_display').value
                        , document.getElementById('org_url').value
                        , document.getElementById('org_desc').value)}
                    , {id:0})}
                , fnRemove: removeOrganization
                , fnDraw: drawOrganizations
                , title: 'Organizations'
                , hRef: 'javascript:getDetails'
            })
            (results);
            renderOrganizationOffset(0);
        }

        let getDetails = function(id)  {
            get("${createLink(controller:'organization', action: 'get')}"
                , organizationDetail('organization')(function(){updateOrganization(id, document.getElementById('org_name').value, document.getElementById('org_display').value, document.getElementById('org_url').value, document.getElementById('org_desc').value);})
                , {id: id});
        }

        let checkOrganization = function(name, display, siteUrl, desc) {

            if (name == null || name.length === 0) {
                setDangerStatus("<b>Organization name cannot be blank.</b>");
                document.getElementById('org_name').focus();
                return false;
            }
            if (display == null || display.length === 0) {
                setDangerStatus("<b>Display name cannot be blank.</b>");
                document.getElementById('org_display').focus();
                return false;
            }
            if (siteUrl == null || siteUrl.length === 0) {
                setDangerStatus("<b>URL cannot be blank.</b>");
                document.getElementById('org_url').focus();
                return false;
            }
            if (!isValidUrl(siteUrl)) {
                setDangerStatus("<b>URL is not valid.</b>");
                document.getElementById('org_url').focus();
                return false;
            }
            if (desc == null || desc.length === 0) {
                setDangerStatus("<b>Description cannot be blank.</b>");
                document.getElementById('org_desc').focus();
                return false;
            }
            return true;
        }

        function isValidUrl(string) {
            let url;
            let validUrl = false;
            try {
                url = new URL(string);
            } catch (_) {
                return false;
            }
            validUrl = url.protocol === "http:" || url.protocol === "https:";

            return validUrl;
        }


        let addOrganization =  function(name, display, siteUrl, desc)  {
            if(checkOrganization(name, display, siteUrl, desc))  {
                add("${createLink(controller:'organization', action: 'add')}"
                    , listOrganizations
                    , { name: name
                        , displayName: display
                        , siteUrl: siteUrl
                        , description: desc
                    }
                );
                clearForm();
            } else {
                scroll(0,0);
            }
        }

        let updateOrganization =  function(id, name, display, siteUrl, desc)  {
            if(checkOrganization(name, display, siteUrl, desc))  {
                update("${createLink(controller:'organization', action: 'update')}"
                    , listOrganizations
                    , {
                        id: id
                        , display: display
                        , url: siteUrl
                        , desc: desc
                    }
                );
                clearForm();
            } else {
                scroll(0,0);
            }
        }

        let removeOrganization = function()  {
            if (confirm("This operation will delete all data for the selected organizations including all systems that have been added to each organization. Do you want to continue?")) {
                getCheckedIds('edit-organizations', function (list) {
                    update("${createLink(controller:'organization', action: 'delete')}"
                        , listOrganizations
                        , {ids: list}
                    );
                });
            }
        }

        let clearForm = function()  {
            setSuccessStatus("<b>Successfully saved organization.</b>");
            hideIt('organization');
            scroll(0,0);
        }

    </script>
</head>
<body>

<sec:ifLoggedIn>
    <div id="isLoogedIn">
    </div>
</sec:ifLoggedIn>

<div id="status-header"></div>

<script type="text/javascript">

    $(document).ready(function()  {
        console.log("$(document).ready(function()");

        var isLoggedIn = $('#isLoogedIn').val();

        if(!(isLoggedIn === undefined)) {

            // Need a global variable to share with external javascript libraries
            LOGGED_IN = true;
        }

        listOrganizations('${orgname}');
    });
</script>

<div id="organization-table"></div>

<div id="organization"></div>

<sec:ifLoggedIn>
    <sec:ifNotGranted roles="ROLE_USER,ROLE_ADMIN">
        <h3 style="margin-top: 2em;">Report Only Account</h3>
        <div class="text-muted">
            On this page, you can view the organization report for ${user?.organization?.name},
            or share it with another user.
        </div>

        <div style="margin-top: 2em;" class="row">
            <div class="col-md-4">
                <div class="reportContainer">
                    <a href="${createLink(controller:'reports', action: 'organizationReport')}" class="reportLink">
                        <div class="row">
                            <div class="col-md-2 reportIcon">
                                <span class="glyphicon glyphicon-home"></span>
                            </div>
                            <div class="col-md-10 reportTextContainer">
                                <h4 class="reportTitle">Organization Details</h4>
                                <div class="reportDescription">
                                    Given a single organization, this report will provide a look into what the status
                                    of each trustmark assessment is.
                                </div>
                            </div>
                        </div>
                    </a>
                </div>
            </div>
            <div class="col-md-8">
            </div>
        </div>
    </sec:ifNotGranted>
</sec:ifLoggedIn>

<g:if test='${flash.message}'>
    setDangerStatus("<b>Authentication Failed!</b> ${flash.message}");
</g:if>
<sec:ifNotLoggedIn>
    <g:if test="${firstTimeLogin}">
        <script type="text/javascript">
            $(document).ready(function(){
                document.getElementById('registryUrl').value = '${grailsApplication.config.registry.url}';
                document.getElementById('contactResponder').value = "${grailsApplication.config.contact.'1'.responder}";
                document.getElementById('contactAddr').value = "${grailsApplication.config.contact.'1'.mailingAddress}";
                document.getElementById('contactEmail').value = "${grailsApplication.config.contact.'1'.email}".replace("&#64;", "@");
                document.getElementById('contactPhone').value = "${grailsApplication.config.contact.'1'.telephone}";
                document.getElementById('organizationName').value = "${grailsApplication.config.org.'1'.name}";
                document.getElementById('organizationId').value = "${grailsApplication.config.org.'1'.identifier}";
                document.getElementById('organizationUri').value = "${grailsApplication.config.org.'1'.uri}";
                document.getElementById('username').value = "${grailsApplication.config.user.'1'.username}";
                document.getElementById('registryUrl').focus();
            });
            let validate = function(frm)  {
                if (frm.username.value === "")  {
                    setDangerStatus('User name cannot be blank!');
                    frm.username.focus();
                    return false;
                }
                if (frm.password.value === "")  {
                    setDangerStatus('Password cannot be blank!');
                    frm.password.focus();
                    return false;
                }
                if (frm.passwordAgain.value === "")  {
                    setDangerStatus('Password re-enter cannot be blank!');
                    frm.passwordAgain.focus();
                    return false;
                }
                if (frm.password.value !== frm.passwordAgain.value)  {
                    setDangerStatus('Passwords do not match!'+ frm.password.value +' - '+frm.passwordAgain.value);
                    frm.passwordAgain.focus();
                    return false;
                }
                if (frm.registryUrl.value == null || frm.registryUrl.value === "")  {
                    setDangerStatus('Registry Url cannot be blank!');
                    frm.registryUrl.focus();
                    return false;
                }
                if (frm.contactResponder.value === "")  {
                    setDangerStatus('Contact name cannot be blank!');
                    frm.contactResponder.focus();
                    return false;
                }
                if (frm.contactAddr.value == null || frm.contactAddr.value === "")  {
                    setDangerStatus('Contact address cannot be blank!');
                    frm.contactAddr.focus();
                    return false;
                }
                if (frm.contactEmail.value == null || frm.contactEmail.value === "")  {
                    setDangerStatus('Contact Email cannot be blank!');
                    frm.contactEmail.focus();
                    return false;
                }
                if (frm.contactPhone.value == null || frm.contactPhone.value === "")  {
                    setDangerStatus('Contact phone cannot be blank!');
                    frm.contactPhone.focus();
                    return false;
                }
                if (frm.organizationName.value == null || frm.organizationName.value === "")  {
                    setDangerStatus('Organization Name cannot be blank!');
                    frm.organizationName.focus();
                    return false;
                }
                if (frm.organizationId.value == null || frm.organizationId.value === "")  {
                    setDangerStatus('Organization Id cannot be blank!');
                    frm.organizationId.focus();
                    return false;
                }
                if (frm.organizationUri.value == null || frm.organizationUri.value === "")  {
                    setDangerStatus('Organization URI cannot be blank!');
                    frm.organizationUri.focus();
                    return false;
                }
                return true;
            }
        </script>
        <div class="row">
            <h2><g:message code="first.time.message1"/></h2><br>
            <form action='${createLink(controller: 'index', action: 'initialize')}' method='POST' id='loginForm' class='form-horizontal' autocomplete='off' role="form" onsubmit="return validate(this)">
                <fieldset>
                    <legend><h5><g:message code="first.time.message3"/></h5></legend>
                    <div class="form-group">
                        <label for="registryUrl" class="col-md-3 control-label"><g:message code="registry.tpat.url.label"/></label>
                        <div class="col-md-4">
                            <input type="text" class="form-control" id="registryUrl" name="registryUrl" placeholder="${grailsApplication.config.registry.url}">
                        </div>
                    </div>
                </fieldset>
                <fieldset>
                    <legend><h5><g:message code="first.time.message4"/></h5></legend>
                    <div class="form-group">
                        <label for="contactResponder" class="col-md-3 control-label"><g:message code="contact.responder.label"/></label>
                        <div class="col-md-4">
                            <input type="text" class="form-control" id="contactResponder" name="contactResponder" placeholder='<g:message code="contact.responder.label"/>' >
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="contactAddr" class="col-md-3 control-label"><g:message code="contact.address.label"/></label>
                        <div class="col-md-4">
                            <input type="text" class="form-control" id="contactAddr" name="contactAddr" placeholder='<g:message code="contact.address.label"/>' >
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="contactEmail" class="col-md-3 control-label"><g:message code="contact.email.label"/></label>
                        <div class="col-md-4">
                            <input type="text" class="form-control" id="contactEmail" name="contactEmail" placeholder='<g:message code="contact.email.label"/>' >
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="contactPhone" class="col-md-3 control-label"><g:message code="contact.phone.label"/></label>
                        <div class="col-md-4">
                            <input type="text" class="form-control" id="contactPhone" name="contactPhone" placeholder='<g:message code="contact.phone.label"/>' >
                        </div>
                    </div>
                </fieldset>
                <legend><h5><g:message code="first.time.message5"/></h5></legend>
                <fieldset>
                    <div class="form-group">
                        <label for="organizationName" class="col-md-3 control-label"><g:message code="organization.name.label"/></label>
                        <div class="col-md-4">
                            <input type="text" class="form-control" id="organizationName" name="organizationName" placeholder='<g:message code="organization.name.label"/>' >
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="organizationId" class="col-md-3 control-label"><g:message code="organization.identifier.label"/></label>
                        <div class="col-md-4">
                            <input type="text" class="form-control" id="organizationId" name="organizationId" placeholder='<g:message code="organization.identifier.label"/>' >
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="organizationUri" class="col-md-3 control-label"><g:message code="organization.uri.label"/></label>
                        <div class="col-md-4">
                            <input type="text" class="form-control" id="organizationUri" name="organizationUri" placeholder='<g:message code="organization.uri.label"/>' >
                        </div>
                    </div>
                </fieldset>
                <fieldset>
                    <legend><h5><g:message code="first.time.message2"/></h5></legend>
                    <div class="form-group">
                        <label for="username" class="col-md-3 control-label"><g:message code="initialize.username.label"/></label>
                        <div class="col-md-4">
                            <input type="text" class="form-control" id="username" name="username" placeholder='<g:message code="initialize.username.label"/>' >
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="password" class="col-md-3 control-label"><g:message code="initialize.password.label"/></label>
                        <div class="col-md-4">
                            <input type="password" class="form-control" id="password" name="password" placeholder=<g:message code="initialize.password.label"/> >
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="passwordAgain" class="col-md-3 control-label"><g:message code="reenter.password.label"/></label>
                        <div class="col-md-4">
                            <input type="password" class="form-control" id="passwordAgain" name="passwordAgain" placeholder='<g:message code="reenter.password.label"/>' >
                        </div>
                    </div>
                </fieldset>
                <div class="form-group">
                    <div class="col-md-3"></div>
                    <div class="col-md-2">
                        <input class="btn btn-default" type='submit' id="submit" value='${message(code: "tf.initialize.label")}'/>
                    </div>
                </div>
            </form>
        </div>
    </g:if>
</sec:ifNotLoggedIn>
</body>
</html>
