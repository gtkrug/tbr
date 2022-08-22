<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>

        <script type="text/javascript">
            const ORGANIZATION_VIEW = "${createLink(controller:'organization', action: 'view')}/";

            const CONTACT_ADD = "${createLink(controller:'contact', action: 'add')}"
            const CONTACT_DELETE = "${createLink(controller:'contact', action: 'delete')}"
            const CONTACT_GET = "${createLink(controller:'contact', action: 'get')}"
            const CONTACT_TYPES = "${createLink(controller:'contact', action: 'types')}"
            const CONTACT_UPDATE = "${createLink(controller:'contact', action: 'update')}"

            const TAG_ADD = "${createLink(controller:'tag', action: 'add')}"
            const TAG_DELETE = "${createLink(controller:'tag', action: 'delete')}"
            const TAG_LIST = "${createLink(controller:'tag', action: 'list')}"

            const ATTRIBUTE_ADD = "${createLink(controller:'attribute', action: 'add')}"
            const ATTRIBUTE_DELETE = "${createLink(controller:'attribute', action: 'delete')}"
            const ATTRIBUTE_LIST = "${createLink(controller:'attribute', action: 'list')}"

            const PROVIDER_ADD_TRUSTMARK_RECIPIENT_IDENTIFIER = "${createLink(controller:'provider', action: 'addTrustmarkRecipientIdentifier')}"
            const PROVIDER_DELETE_TRUSTMARK_RECIPIENT_IDENTIFIER = "${createLink(controller:'provider', action: 'deleteTrustmarkRecipientIdentifier')}"
            const PROVIDER_DELETE_TRUSTMARK_RECIPIENT_IDENTIFIERS = "${createLink(controller:'provider', action: 'deleteTrustmarkRecipientIdentifiers')}"
            const PROVIDER_GET_TRUSTMARK_RECIPIENT_IDENTIFIER = "${createLink(controller:'provider', action: 'getTrustmarkRecipientIdentifier')}"
            const PROVIDER_TRUSTMARK_RECIPIENT_IDENTIFIERS = "${createLink(controller:'provider', action: 'trustmarkRecipientIdentifiers')}"
            const PROVIDER_UPDATE_TRUSTMARK_RECIPIENT_IDENTIFIER = "${createLink(controller:'provider', action: 'updateTrustmarkRecipientIdentifier')}"

            const CONFORMANCE_TARGET_TIP_ADD = "${createLink(controller:'conformanceTargetTip', action: 'add')}"
            const CONFORMANCE_TARGET_TIP_DELETE = "${createLink(controller:'conformanceTargetTip', action: 'delete')}"
            const CONFORMANCE_TARGET_TIP_LIST = "${createLink(controller:'conformanceTargetTip', action: 'list')}"

            const ENDPOINT_ADD = "${createLink(controller:'endPoint', action: 'add')}"
            const ENDPOINT_DELETE = "${createLink(controller:'endPoint', action: 'delete')}"
            const ENDPOINT_LIST = "${createLink(controller:'endPoint', action: 'list')}"

            const PROVIDER_ADD_CONTACT_TO_SYSTEM = "${createLink(controller: 'provider', action: 'addContactToSystem')}"
            const PROVIDER_ADD_PARTNER_SYSTEMS_TIP = "${createLink(controller: 'provider', action: 'addPartnerSystemsTip')}"
            const PROVIDER_BIND_TRUSTMARKS = "${createLink(controller: 'provider', action: 'bindTrustmarks')}"
            const PROVIDER_CANCEL_TRUSTMARK_BINDINGS = "${createLink(controller: 'provider', action: 'cancelTrustmarkBindings')}"
            const PROVIDER_CERTIFICATE_DETAILS = "${createLink(controller: 'provider', action: 'certificateDetails')}"
            const PROVIDER_DELETE_PARTNER_SYSTEMS_TIPS = "${createLink(controller: 'provider', action: 'deletePartnerSystemsTips')}"
            const PROVIDER_GENERATE_SAML_2_METADATA = "${createLink(controller: 'provider', action: 'generateSaml2Metadata', id: provider.id)}"
            const PROVIDER_INIT_TRUSTMARK_BINDING_STATE = "${createLink(controller: 'provider', action: 'initTrustmarkBindingState')}"
            const PROVIDER_LIST_CONTACTS = "${createLink(controller: 'provider', action: 'listContacts')}"
            const PROVIDER_LIST_IDP_ATTRIBUTES = "${createLink(controller: 'provider', action: 'listIdpAttributes')}"
            const PROVIDER_OIDC_DETAILS = "${createLink(controller: 'provider', action: 'oidcDetails')}"
            const PROVIDER_PARTNER_SYSTEMS_TIPS = "${createLink(controller: 'provider', action: 'partnerSystemsTips')}"
            const PROVIDER_PROTOCOL_DETAILS = "${createLink(controller: 'provider', action: 'protocolDetails')}"
            const PROVIDER_REMOVE_CONTACT_FROM_SYSTEM = "${createLink(controller: 'provider', action: 'removeContactFromSystem')}"
            const PROVIDER_TRUSTMARK_BINDING_STATUS_UPDATE = "${createLink(controller: 'provider', action: 'trustmarkBindingStatusUpdate')}"
            const PROVIDER_UPDATE_TRUSTMARK_BINDING_DETAILS = "${createLink(controller: 'provider', action: 'updateTrustmarkBindingDetails')}"
            const PROVIDER_UPLOAD = "${createLink(controller: 'provider', action: 'upload')}"
            const PROVIDER_UPLOAD_CERTIFICATE = "${createLink(controller: 'provider', action: 'uploadCertificate')}"
            const PROVIDER_UPLOAD_OIDC_METADATA = "${createLink(controller: 'provider', action: 'uploadOidcMetadata')}"

            const TRUSTMARK_LIST = "${createLink(controller:'trustmark', action: 'list')}"

            const ORGANIZATION_LIST = "${createLink(controller:'organization', action: 'list')}"

            const PROVIDER_ID = "${provider.id}"
            const PROVIDER_ORGANIZATION_ID = "${provider.organization.id}"

            const IS_SAML_IDP =
            ${provider.providerType == tm.binding.registry.ProviderType.SAML_IDP}
            const IS_SAML_SP =
            ${provider.providerType == tm.binding.registry.ProviderType.SAML_SP}
            const IS_CERTIFICATE =
            ${provider.providerType == tm.binding.registry.ProviderType.CERTIFICATE}
            const IS_OIDC_RP =
            ${provider.providerType == tm.binding.registry.ProviderType.OIDC_RP}
            const IS_OIDC_OP =
            ${provider.providerType == tm.binding.registry.ProviderType.OIDC_OP}
        </script>
        <asset:javascript src="utility/utility_protocol.js"/>
        <asset:javascript src="utility/utility_endpoint.js"/>
        <asset:javascript src="utility/utility_idp_attribute.js"/>
        <asset:javascript src="utility/utility_certificate.js"/>
        <asset:javascript src="utility/utility_oidc.js"/>
        <asset:javascript src="utility/utility_contact_for_provider.js"/>
        <asset:javascript src="utility/utility_attribute.js"/>
        <asset:javascript src="utility/utility_tag.js"/>
        <asset:javascript src="utility/utility_trustmark_recipient_identifier_for_provider.js"/>
        <asset:javascript src="utility/utility_partner_system_tip.js"/>
        <asset:javascript src="utility/utility_conformance_target_tip.js"/>
        <asset:javascript src="utility/utility_trustmark_for_provider.js"/>
        <asset:javascript src="provider_view.js"/>
    </head>

    <body>
        <div class="container pt-4">

            <h2>System Information for ${provider.name}</h2>

            <div class="border rounded card">
                <div class="card-header fw-bold">
                    <div class="row">
                        <div class="col-12">Basic System Information</div>
                    </div>
                </div>

                <div class="card-body">
                    <div class="row pb-2">
                        <label for="org-display" class="col-3 col-form-label">System Name</label>

                        <div class="col-9">
                            <input id="org-display" type="text" class="form-control" value="${provider.name}" readonly disabled/>
                        </div>
                    </div>

                    <div class="row pb-2">
                        <label for="org-url" class="col-3 col-form-label">Organization</label>

                        <div class="col-9">
                            <div class="form-control" style="background-color: rgba(0, 0, 0, .03)">
                                <a href="${createLink(controller: 'organization', action: 'view', id: provider.organization.id)}">${provider.organization.name}</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <g:if test="${provider.providerType == tm.binding.registry.ProviderType.SAML_IDP || provider.providerType == tm.binding.registry.ProviderType.SAML_SP}">
                <h2 class="pt-4">
                    <div class="row">
                        <div class="col-11">
                            Protocol-Specific Details
                        </div>

                        <div class="col-1 d-flex justify-content-end">

                            <button
                                    class="btn btn-primary ms-2"
                                    type="button"
                                    data-bs-toggle="collapse"
                                    data-bs-target="#collapseProtocols"
                                    aria-expanded="false"
                                    aria-controls="collapseProtocols">
                                <span class="bi bi-plus-lg"></span>
                                <span class="bi bi-dash-lg"></span></button>
                        </div>
                    </div>
                </h2>

                <div class="collapse" id="collapseProtocols">
                    <sec:ifLoggedIn>
                        <div id="uploadStatusMessage" class="pt-2">
                            <g:if test="${successMessage != null && !successMessage.isEmpty()}">
                                <div class="alert alert-primary">${successMessage}</div>
                            </g:if>
                            <g:if test="${warningMessage != null && !warningMessage.isEmpty()}">
                                <div class="alert alert-warning">${warningMessage}</div>
                            </g:if>
                            <g:if test="${errorMessage != null && !errorMessage.isEmpty()}">
                                <div class="alert alert-danger">${errorMessage}</div>
                            </g:if>
                        </div>

                        <div class="border rounded card">

                            <div class="card-header fw-bold">
                                <div class="row">
                                    <div class="col-12">Upload</div>
                                </div>
                            </div>

                            <div class="card-body">
                                <div class="row pb-2">
                                    <label for="filename" class="col-3 col-form-label">Metadata File</label>

                                    <div class="col-9">
                                        <form id="upload">
                                            <input name="id" type="hidden" value="${provider.organization.id}"/>
                                            <input name="providerId" type="hidden" value="${provider.id}"/>
                                            <input name="isIdp" type="hidden" value="${provider.providerType == tm.binding.registry.ProviderType.SAML_IDP}"/>
                                            <input name="filename" type="file" class="form-control" accept=".xml"/>
                                        </form>
                                    </div>
                                </div>

                                <g:if test="${!provider.entityId.empty}">
                                    <div class="row pb-2">
                                        <div class="col-3 col-form-label"></div>

                                        <div class="col-9">
                                            <input type="text" class="form-control alert alert-warning" style="padding: 5.25px 10.25px; margin: 0px;" value="A metadata file has already been uploaded. Uploading again will overwrite the currently loaded data."/>
                                        </div>
                                    </div>
                                </g:if>
                            </div>

                            <div class="card-footer text-start">
                                <div class="row">
                                    <div class="col-3"></div>

                                    <div class="col-9">
                                        <button class="btn btn-primary" id="uploadButton">Upload</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </sec:ifLoggedIn>

                    <div id="protocol-form" class="pt-4"></div>

                    <table id="endpoint-table" class="table table-condensed table-striped table-bordered mt-4 mb-0"></table>

                    <table id="idp-attribute-table" class="table table-condensed table-striped table-bordered mt-4 mb-0"></table>
                </div>
            </g:if>
            <g:elseif test="${provider.providerType == tm.binding.registry.ProviderType.CERTIFICATE}">
                <h2 class="pt-4">
                    <div class="row">
                        <div class="col-11">
                            Certificate-Specific Details
                        </div>

                        <div class="col-1 d-flex justify-content-end">

                            <button
                                    class="btn btn-primary ms-2"
                                    type="button"
                                    data-bs-toggle="collapse"
                                    data-bs-target="#collapseProtocols"
                                    aria-expanded="false"
                                    aria-controls="collapseProtocols">
                                <span class="bi bi-plus-lg"></span>
                                <span class="bi bi-dash-lg"></span></button>
                        </div>
                    </div>
                </h2>

                <div class="collapse" id="collapseProtocols">
                    <sec:ifLoggedIn>
                        <div id="uploadStatusMessage" class="pt-2">
                            <g:if test="${successMessage != null && !successMessage.isEmpty()}">
                                <div class="alert alert-primary">${successMessage}</div>
                            </g:if>
                            <g:if test="${warningMessage != null && !warningMessage.isEmpty()}">
                                <div class="alert alert-warning">${warningMessage}</div>
                            </g:if>
                            <g:if test="${errorMessage != null && !errorMessage.isEmpty()}">
                                <div class="alert alert-danger">${errorMessage}</div>
                            </g:if>
                        </div>

                        <div class="border rounded card">
                            <div class="card-header fw-bold">
                                <div class="row">
                                    <div class="col-12">Upload</div>
                                </div>
                            </div>

                            <div class="card-body">
                                <div class="row pb-2">

                                    <label for="filename" class="col-3 col-form-label">Certificate File</label>

                                    <div class="col-9">
                                        <form id="upload">
                                            <input name="id" type="hidden" value="${provider.organization.id}"/>
                                            <input name="providerId" type="hidden" value="${provider.id}"/>
                                            <input name="isIdp" type="hidden" value="${provider.providerType == tm.binding.registry.ProviderType.SAML_IDP}"/>
                                            <input name="filename" type="file" class="form-control" accept=".pem"/>
                                        </form>
                                    </div>
                                </div>

                                <g:if test="${provider.systemCertificate && !provider.systemCertificate.empty}">
                                    <div class="row pb-2">
                                        <div class="col-3 col-form-label"></div>

                                        <div class="col-9">
                                            <input type="text" class="form-control alert alert-warning" style="padding: 5.25px 10.25px; margin: 0px;" value="A certificate file has already been uploaded. Uploading again will overwrite the currently loaded data."/>
                                        </div>
                                    </div>
                                </g:if>
                            </div>

                            <div class="card-footer text-start">
                                <div class="row">
                                    <div class="col-3"></div>

                                    <div class="col-9">
                                        <button class="btn btn-primary" id="uploadButton">Upload</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </sec:ifLoggedIn>

                    <div id="certificate-form" class="pt-4"></div>
                </div>
            </g:elseif>
            <g:elseif test="${provider.providerType == tm.binding.registry.ProviderType.OIDC_RP || provider.providerType == tm.binding.registry.ProviderType.OIDC_OP}">
                <h2 class="pt-4">
                    <div class="row">
                        <div class="col-11">
                            OpenId Connect-Specific Details
                        </div>

                        <div class="col-1 d-flex justify-content-end">

                            <button
                                    class="btn btn-primary ms-2"
                                    type="button"
                                    data-bs-toggle="collapse"
                                    data-bs-target="#collapseProtocols"
                                    aria-expanded="false"
                                    aria-controls="collapseProtocols">
                                <span class="bi bi-plus-lg"></span>
                                <span class="bi bi-dash-lg"></span></button>
                        </div>
                    </div>
                </h2>

                <div class="collapse" id="collapseProtocols">
                    <sec:ifLoggedIn>
                        <div id="uploadStatusMessage" class="pt-2">
                            <g:if test="${successMessage != null && !successMessage.isEmpty()}">
                                <div class="alert alert-primary">${successMessage}</div>
                            </g:if>
                            <g:if test="${warningMessage != null && !warningMessage.isEmpty()}">
                                <div class="alert alert-warning">${warningMessage}</div>
                            </g:if>
                            <g:if test="${errorMessage != null && !errorMessage.isEmpty()}">
                                <div class="alert alert-danger">${errorMessage}</div>
                            </g:if>
                        </div>

                        <div class="border rounded card">
                            <div class="card-header fw-bold">
                                <div class="row">
                                    <div class="col-12">Upload OIDC Metadata File</div>
                                </div>
                            </div>

                            <div class="card-body">
                                <div class="row pb-2">

                                    <label for="filename" class="col-3 col-form-label">Metadata File</label>

                                    <div class="col-9">
                                        <form id="upload">
                                            <input name="id" type="hidden" value="${provider.organization.id}"/>
                                            <input name="providerId" type="hidden" value="${provider.id}"/>
                                            <input name="isIdp" type="hidden" value="${provider.providerType == tm.binding.registry.ProviderType.SAML_IDP}"/>
                                            <input name="filename" type="file" class="form-control" accept=".json"/>
                                        </form>
                                    </div>
                                </div>

                                <g:if test="${provider.openIdConnectMetadata && !provider.openIdConnectMetadata.empty}">
                                    <div class="row pb-2">
                                        <div class="col-3 col-form-label"></div>

                                        <div class="col-9">
                                            <input type="text" class="form-control alert alert-warning" style="padding: 5.25px 10.25px; margin: 0px;" value="An OIDC metadata file has already been uploaded. Uploading again will overwrite the currently loaded data."/>
                                        </div>
                                    </div>
                                </g:if>
                            </div>

                            <div class="card-footer text-start">
                                <div class="row">
                                    <div class="col-3"></div>

                                    <div class="col-9">
                                        <button class="btn btn-primary" id="uploadButton">Upload</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </sec:ifLoggedIn>

                    <div id="openid-connect-form" class="pt-4"></div>
                </div>
            </g:elseif>
            <g:else>
                <h2 class="pt-4">
                    <div class="row">
                        <div class="col-11">
                            Unknown System Type
                        </div>
                    </div>
                </h2>
            </g:else>

            <h2 class="pt-4">Points of Contact</h2>

            <div id="contact-message"></div>

            <table id="contact-table" class="table table-condensed table-striped table-bordered my-0"></table>

            <div id="contact-form" class="pt-4" style="display:none"></div>

            <g:if test="${provider.providerType == tm.binding.registry.ProviderType.SAML_IDP || provider.providerType == tm.binding.registry.ProviderType.SAML_SP || provider.providerType == tm.binding.registry.ProviderType.OIDC_RP || provider.providerType == tm.binding.registry.ProviderType.OIDC_OP}">

                <h2 class="pt-4">System Attributes</h2>

                <div id="attribute-message"></div>

                <table class="table table-condensed table-striped table-bordered my-0" id="attribute-table"></table>

                <div id="attribute-form" class="pt-4" style="display:none"></div>

                <h2 class="pt-4">
                    <div class="row">
                        <div class="col-11">
                            Keyword Tags Details
                        </div>

                        <div class="col-1 d-flex justify-content-end">

                            <button
                                    class="btn btn-primary ms-2"
                                    type="button"
                                    data-bs-toggle="collapse"
                                    data-bs-target="#collapseTags"
                                    aria-expanded="false"
                                    aria-controls="collapseTags">
                                <span class="bi bi-plus-lg"></span>
                                <span class="bi bi-dash-lg"></span></button>
                        </div>
                    </div>
                </h2>

                <div class="collapse" id="collapseTags">

                    <div id="tag-message"></div>

                    <table class="table table-condensed table-striped table-bordered my-0" id="tag-table"></table>

                    <div id="tag-form" class="pt-4" style="display:none"></div>
                </div>
            </g:if>

            <sec:ifLoggedIn>
                <h2 class="pt-4">Trustmark Recipient Identifiers</h2>

                <div id="trustmark-recipient-identifier-message"></div>

                <table class="table table-condensed table-striped table-bordered my-0" id="trustmark-recipient-identifier-table"></table>

                <div id="trustmark-recipient-identifier-form" class="pt-4" style="display:none"></div>
            </sec:ifLoggedIn>

            <h2 class="pt-4">Partner System Trust Interoperability Profiles</h2>

            <div id="partner-system-tip-message"></div>

            <table class="table table-condensed table-striped table-bordered my-0" id="partner-system-tip-table"></table>

            <div id="partner-system-tip-form" class="pt-4" style="display:none"></div>

            <h2 class="pt-4">Conformance Target Trust Interoperability Profiles</h2>

            <div id="conformance-target-tip-message"></div>

            <table class="table table-condensed table-striped table-bordered my-0" id="conformance-target-tip-table"></table>

            <div id="conformance-target-tip-form" class="pt-4" style="display:none"></div>

            <h2 class="pt-4">
                <div class="row">
                    <div class="col-11">
                        Trustmark Binding Details
                    </div>

                    <div class="col-1 d-flex justify-content-end">

                        <sec:ifLoggedIn>
                            <g:if test="${provider.trustmarks.size() == 0}">
                                <button class="btn btn-primary bind-trustmark-button" id="btn-bind-trustmarks" style="white-space: nowrap">Bind Trustmarks</button>
                            </g:if>
                            <g:else>
                                <button class="btn btn-primary bind-trustmark-button" id="btn-refresh-trustmark-bindings" style="white-space: nowrap">Refresh Trustmark Bindings</button>
                            </g:else>
                        </sec:ifLoggedIn>

                        <button
                                class="btn btn-primary ms-2"
                                type="button"
                                data-bs-toggle="collapse"
                                data-bs-target="#collapseTrustmarks"
                                aria-expanded="false"
                                aria-controls="collapseTrustmarks">
                            <span class="bi bi-plus-lg"></span>
                            <span class="bi bi-dash-lg"></span></button>
                    </div>
                </div>
            </h2>

            <div id="bindTrustmarkStatusMessage" class="pt-2"></div>

            <div class="collapse" id="collapseTrustmarks">
                <table class="table table-condensed table-striped table-bordered my-0">
                    <thead>
                        <tr>
                            <th style="width: 50%">Number of Trustmarks Bound</th>
                            <th style="width: 50%">Number of Conformance Target TIPs</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td id="numberOfTrustmarksBound">${provider.trustmarks.size()}</td>
                            <td id="numberOfConformanceTargetTIPs">${provider.conformanceTargetTips.size()}</td>
                        </tr>
                    </tbody>
                </table>

                <h2 class="pt-4">
                    <div class="row">
                        <div class="col-11">
                            Trustmarks
                        </div>

                        <div class="col-1 d-flex justify-content-end">
                            <button
                                    class="btn btn-primary"
                                    type="button"
                                    data-bs-toggle="collapse"
                                    data-bs-target="#trustmarks-list"
                                    aria-expanded="false"
                                    aria-controls="trustmarks-list">
                                <span class="bi bi-plus-lg"></span>
                                <span class="bi bi-dash-lg"></span></button>
                        </div>
                    </div>
                </h2>

                <div id="trustmarks-status" class="pt-2"></div>

                <table class="table table-condensed table-striped table-bordered collapse my-0" id="trustmarks-list"></table>
            </div>
        </div>
    </body>
</html>
