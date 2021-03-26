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
        let orgOffset = function (){};

        let listOrganizations = function(orgname)  {
            list("${createLink(controller:'organization', action: 'list')}"
                , renderResults
                , {name: orgname});
        }

        let showOrganizations = function(target, data, offset)  {
            let html = renderPagination(offset, data.length, 'orgOffset');
            html += "<table class='table table-condensed table-striped table-bordered'>";
            html += "<thead><tr><th style='width: auto;'>Name</th>";
            html += "<th style='width: auto;'>URL</th>";
            html += "<th style='width: auto;'>Endpoint Count</th></tr></thead>";
            html += "<tbody>";
            if (data.length === 0)  {
                html += '<tr><td colspan="3"><em>There are no organizations.</em></td></tr>';
            }  else {
                let idx = 0;
                data.forEach(o => {
                    if(idx >= offset && idx < offset+MAX_DISPLAY) {
                        html += "<tr>";
                        html += "<td><a href='${createLink(controller:'organization', action: 'view')}/"+o.id+"'>" + o.name + "</a></td>";
                        html += "<td>" + o.siteUrl + "</td>";
                        html += "<td>"+o.providers.length+"</td>";
                        html += "</tr>";
                    }
                    ++idx;
                });
            }
            html += "</tbody></table>";
            document.getElementById(target).innerHTML = html;
        }

        let curriedIndex = curryThree(showOrganizations);

        let renderResults = function(results)  {
            orgOffset = curriedIndex('organization-table')(results)
            orgOffset(0);
        }
    </script>
</head>
<body>
<div id="status-header"></div>
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
    </g:if><g:else>
    <div class="row">
        <div class="col-md-offset-4 col-md-4">
            <div class="mustLoginDetails alert alert-warning">
                <div class="mustLoginText">
                    This page requires authentication.  Please click below to start the process.
                </div>
                <a href="${createLink(controller:'login')}" class="btn btn-primary">Login &raquo;</a>
            </div>
        </div>
    </div>

    <div style="height: 600px;">&nbsp;</div>
</g:else>
</sec:ifNotLoggedIn>

<sec:ifLoggedIn>
    <script type="text/javascript">
        $(document).ready(function()  {
            listOrganizations('${orgname}');
        });
    </script>
    <sec:ifAnyGranted roles="ROLE_ADMIN, ROLE_USER">
        <h3>Organizations</h3>
        <div id="organization-table"></div>
        <sec:ifAnyGranted roles="ROLE_ADMIN">
            <div style="float:right;" id="uploadForm">
                <form method="post" enctype="multipart/form-data" class="form-inline">
                    <input name="filename" type="file" class="form-control" accept=".xml"/>
                    <button type="submit" class="btn btn-default">Upload</button>
                </form>
            </div>
        </sec:ifAnyGranted>
    </sec:ifAnyGranted>

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
</body>
</html>
