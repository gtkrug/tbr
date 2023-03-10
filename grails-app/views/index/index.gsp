<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>

        <style type="text/css">
            .mustLoginDetails {
                text-align: center;
                margin-top: 5em;
            }

            .mustLoginText {
                margin-bottom: 1em;
            }

            .searchFormContainer {
                margin-bottom: 0.5em;
            }
        </style>

        <script type="text/javascript">
            const ORGANIZATION_VIEW = "${createLink(controller:'organization', action: 'view')}/"
            const ORGANIZATION_LIST = "${createLink(controller:'organization', action: 'list')}"
            const ORGANIZATION_GET = "${createLink(controller:'organization', action: 'get')}"
            const ORGANIZATION_ADD = "${createLink(controller:'organization', action: 'add')}"
            const ORGANIZATION_UPDATE = "${createLink(controller:'organization', action: 'update')}"
            const ORGANIZATION_DELETE = "${createLink(controller:'organization', action: 'delete')}"
        </script>
        <asset:javascript src="utility/utility_organization.js"/>
        <asset:javascript src="index_index.js"/>
    </head>

    <body>
        <div class="container pt-4">

            <g:if test="${showOrganizations}">
                <h2>Organizations</h2>

                <div id="organization-message"></div>

                <table id="organization-table" class="table table-condensed table-striped table-bordered"></table>

                <div class="pt-1" id="organization-form"></div>

                <g:if test='${flash.message}'>
                    setDangerStatus("Authentication Failed! ${flash.message}");
                </g:if>
            </g:if>
            <g:else>
                <div class="h-100 d-flex align-item-center justify-content-center">
                    <div class="mustLoginDetails alert alert-warning">
                        <div class="mustLoginText">
                            This page requires authentication.  Please click below to start the process.
                        </div>
                        <a href="oauth2/authorize-client/keycloak" class="btn btn-primary">Login &raquo;</a>
                    </div>
                </div>

                <div style="height: 600px;">&nbsp;</div>
            </g:else>

            <sec:authorize access="!isAuthenticated()">
                <g:if test="${firstTimeLogin}">
                    <script type="text/javascript">
                        $(document).ready(function () {
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
                        let validate = function (frm) {
                            if (frm.username.value === "") {
                                setDangerStatus('User name cannot be blank!');
                                frm.username.focus();
                                return false;
                            }
                            if (frm.password.value === "") {
                                setDangerStatus('Password cannot be blank!');
                                frm.password.focus();
                                return false;
                            }
                            if (frm.passwordAgain.value === "") {
                                setDangerStatus('Password re-enter cannot be blank!');
                                frm.passwordAgain.focus();
                                return false;
                            }
                            if (frm.password.value !== frm.passwordAgain.value) {
                                setDangerStatus('Passwords do not match!' + frm.password.value + ' - ' + frm.passwordAgain.value);
                                frm.passwordAgain.focus();
                                return false;
                            }
                            if (frm.registryUrl.value == null || frm.registryUrl.value === "") {
                                setDangerStatus('Registry Url cannot be blank!');
                                frm.registryUrl.focus();
                                return false;
                            }
                            if (frm.contactResponder.value === "") {
                                setDangerStatus('Contact name cannot be blank!');
                                frm.contactResponder.focus();
                                return false;
                            }
                            if (frm.contactAddr.value == null || frm.contactAddr.value === "") {
                                setDangerStatus('Contact address cannot be blank!');
                                frm.contactAddr.focus();
                                return false;
                            }
                            if (frm.contactEmail.value == null || frm.contactEmail.value === "") {
                                setDangerStatus('Contact Email cannot be blank!');
                                frm.contactEmail.focus();
                                return false;
                            }
                            if (frm.contactPhone.value == null || frm.contactPhone.value === "") {
                                setDangerStatus('Contact phone cannot be blank!');
                                frm.contactPhone.focus();
                                return false;
                            }
                            if (frm.organizationName.value == null || frm.organizationName.value === "") {
                                setDangerStatus('Organization Name cannot be blank!');
                                frm.organizationName.focus();
                                return false;
                            }
                            if (frm.organizationId.value == null || frm.organizationId.value === "") {
                                setDangerStatus('Organization Id cannot be blank!');
                                frm.organizationId.focus();
                                return false;
                            }
                            if (frm.organizationUri.value == null || frm.organizationUri.value === "") {
                                setDangerStatus('Organization URI cannot be blank!');
                                frm.organizationUri.focus();
                                return false;
                            }
                            return true;
                        }
                    </script>

                    <div class="pt-4">

                        <form action='${createLink(controller: 'index', action: 'initialize')}' method='POST' id='loginForm'
                              class='form-horizontal' autocomplete='off' role="form" onsubmit="return validate(this)">

                            <div class="border rounded card">

                                <div class="card-header fw-bold">
                                    <div class="row">
                                        <div class="col-12"><g:message code="first.time.message1"/></div>
                                    </div>
                                </div>

                                <div class="card-body">
                                    <div class="row pb-2">
                                        <label for="registryUrl" class="col-3 col-form-label"><g:message
                                                code="registry.tpat.url.label"/></label>

                                        <div class="col-9">
                                            <input type="text" class="form-control" id="registryUrl" name="registryUrl"
                                                   placeholder="${grailsApplication.config.registry.url}">
                                        </div>
                                    </div>
                                </div>

                                <div class="card-header fw-bold">
                                    <div class="row">
                                        <div class="col-12">
                                            <g:message code="first.time.message4"/>
                                        </div>
                                    </div>
                                </div>

                                <div class="card-body">
                                    <div class="row pb-2">
                                        <label for="contactResponder" class="col-3 col-form-label"><g:message
                                                code="contact.responder.label"/></label>

                                        <div class="col-9">
                                            <input type="text" class="form-control" id="contactResponder"
                                                   name="contactResponder"
                                                   placeholder='<g:message code="contact.responder.label"/>'>
                                        </div>
                                    </div>

                                    <div class="row pb-2">
                                        <label for="contactAddr" class="col-3 col-form-label"><g:message
                                                code="contact.address.label"/></label>

                                        <div class="col-9">
                                            <input type="text" class="form-control" id="contactAddr" name="contactAddr"
                                                   placeholder='<g:message code="contact.address.label"/>'>
                                        </div>
                                    </div>

                                    <div class="row pb-2">
                                        <label for="contactEmail" class="col-3 col-form-label"><g:message
                                                code="contact.email.label"/></label>

                                        <div class="col-9">
                                            <input type="text" class="form-control" id="contactEmail" name="contactEmail"
                                                   placeholder='<g:message code="contact.email.label"/>'>
                                        </div>
                                    </div>

                                    <div class="row pb-2">
                                        <label for="contactPhone" class="col-3 col-form-label"><g:message
                                                code="contact.phone.label"/></label>

                                        <div class="col-9">
                                            <input type="text" class="form-control" id="contactPhone" name="contactPhone"
                                                   placeholder='<g:message code="contact.phone.label"/>'>
                                        </div>
                                    </div>
                                </div>

                                <div class="card-header fw-bold">
                                    <div class="row">
                                        <div class="col-12">
                                            <g:message code="first.time.message5"/>
                                        </div>
                                    </div>
                                </div>

                                <div class="card-body">

                                    <div class="row pb-2">
                                        <label for="organizationName" class="col-3 col-form-label"><g:message
                                                code="organization.name.label"/></label>

                                        <div class="col-9">
                                            <input type="text" class="form-control" id="organizationName"
                                                   name="organizationName"
                                                   placeholder='<g:message code="organization.name.label"/>'>
                                        </div>
                                    </div>

                                    <div class="row pb-2">
                                        <label for="organizationId" class="col-3 col-form-label"><g:message
                                                code="organization.identifier.label"/></label>

                                        <div class="col-9">
                                            <input type="text" class="form-control" id="organizationId" name="organizationId"
                                                   placeholder='<g:message code="organization.identifier.label"/>'>
                                        </div>
                                    </div>

                                    <div class="row pb-2">
                                        <label for="organizationUri" class="col-3 col-form-label"><g:message
                                                code="organization.uri.label"/></label>

                                        <div class="col-9">
                                            <input type="text" class="form-control" id="organizationUri" name="organizationUri"
                                                   placeholder='<g:message code="organization.uri.label"/>'>
                                        </div>
                                    </div>
                                </div>

                                <div class="card-header fw-bold">
                                    <div class="row">
                                        <div class="col-12">
                                            <g:message code="first.time.message2"/>
                                        </div>
                                    </div>
                                </div>

                                <div class="card-body">

                                    <div class="row pb-2">
                                        <label for="username" class="col-3 col-form-label"><g:message
                                                code="initialize.username.label"/></label>

                                        <div class="col-9">
                                            <input type="text" class="form-control" id="username" name="username"
                                                   placeholder='<g:message code="initialize.username.label"/>'>
                                        </div>
                                    </div>

                                    <div class="row pb-2">
                                        <label for="password" class="col-3 col-form-label"><g:message
                                                code="initialize.password.label"/></label>

                                        <div class="col-9">
                                            <input type="password" class="form-control" id="password" name="password"
                                                   placeholder= <g:message code="initialize.password.label"/>>
                                        </div>
                                    </div>

                                    <div class="row pb-2">
                                        <label for="passwordAgain" class="col-3 col-form-label"><g:message
                                                code="reenter.password.label"/></label>

                                        <div class="col-9">
                                            <input type="password" class="form-control" id="passwordAgain" name="passwordAgain"
                                                   placeholder='<g:message code="reenter.password.label"/>'>
                                        </div>
                                    </div>
                                </div>

                                <div class="card-footer text-start">
                                    <div class="row">
                                        <div class="col-3"></div>

                                        <div class="col-9">
                                            <input class="btn btn-primary" type='submit' id="submit"
                                                   value='${message(code: "tf.initialize.label")}'/>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </g:if>
            </sec:authorize>
        </div>
    </body>
</html>
