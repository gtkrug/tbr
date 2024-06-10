<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>

        <script type="text/javascript">
            const ORGANIZATION_ID = "${organization.id}"

            const IS_READ_ONLY = "${isReadOnly}"

            const ORGANIZATION_VIEW = "${createLink(controller: 'organization', action: 'view')}/"
            const ORGANIZATION_GET = "${createLink(controller:'organization', action: 'get')}"
            const ORGANIZATION_UPDATE = "${createLink(controller: 'organization', action: 'update')}"

            const CONTACT_ADD = "${createLink(controller: 'contact', action: 'add')}"
            const CONTACT_DELETE = "${createLink(controller: 'contact', action: 'delete')}"
            const CONTACT_GET = "${createLink(controller: 'contact', action: 'get')}"
            const CONTACT_LIST = "${createLink(controller: 'contact', action: 'list')}"
            const CONTACT_UPDATE = "${createLink(controller: 'contact', action: 'update')}"
            const CONTACT_TYPES = "${createLink(controller: 'contact', action: 'types')}"

            const ORGANIZATION_ADD_REPO = "${createLink(controller: 'organization', action: 'addRepo')}"
            const ORGANIZATION_DELETE_REPOS = "${createLink(controller: 'organization', action: 'deleteRepos')}"
            const ORGANIZATION_GET_REPO = "${createLink(controller: 'organization', action: 'getRepo')}"
            const ORGANIZATION_REPOS = "${createLink(controller: 'organization', action: 'repos')}"
            const ORGANIZATION_UPDATE_REPO = "${createLink(controller: 'organization', action: 'updateRepo')}"

            const ORGANIZATION_ADD_TRUSTMARK_RECIPIENT_IDENTIFIER = "${createLink(controller: 'organization', action: 'addTrustmarkRecipientIdentifier')}"
            const ORGANIZATION_DELETE_TRUSTMARK_RECIPIENT_IDENTIFIERS = "${createLink(controller: 'organization', action: 'deleteTrustmarkRecipientIdentifiers')}"
            const ORGANIZATION_GET_TRUSTMARK_RECIPIENT_IDENTIFIER = "${createLink(controller: 'organization', action: 'getTrustmarkRecipientIdentifier')}"
            const ORGANIZATION_TRUSTMARK_RECIPIENT_IDENTIFIERS = "${createLink(controller: 'organization', action: 'trustmarkRecipientIdentifiers')}"
            const ORGANIZATION_UPDATE_TRUSTMARK_RECIPIENT_IDENTIFIER = "${createLink(controller: 'organization', action: 'updateTrustmarkRecipientIdentifier')}"

            const ORGANIZATION_ADD_PARTNER_SYSTEMS_TIP = "${createLink(controller: 'organization', action: 'addPartnerSystemsTip')}"
            const ORGANIZATION_DELETE_PARTNER_SYSTEMS_TIPS = "${createLink(controller: 'organization', action: 'deletePartnerSystemsTips')}"
            const ORGANIZATION_PARTNER_SYSTEMS_TIPS = "${createLink(controller: 'organization', action: 'partnerSystemsTips')}"

            const ORGANIZATION_TRUSTMARKS = "${createLink(controller: 'organization', action: 'trustmarks')}"
            const ORGANIZATION_BIND_TRUSTMARKS = "${createLink(controller: 'organization', action: 'bindTrustmarks')}"
            const ORGANIZATION_UPDATE_TRUSTMARK_BINDING_DETAILS = "${createLink(controller: 'organization', action: 'updateTrustmarkBindingDetails')}"

            const FULL_PAGE_DEFAULT_ITEMS_PER_PAGE = "${createLink(controller: 'organization', action: 'getFullPageDefaultItemsPerPage')}"

            const PROVIDER_ADD = "${createLink(controller: 'provider', action: 'add')}"
            const PROVIDER_DELETE = "${createLink(controller: 'provider', action: 'delete')}"
            const PROVIDER_LIST = "${createLink(controller: 'provider', action: 'list')}"
            const PROVIDER_TYPES = "${createLink(controller: 'provider', action: 'types')}"

            const TABLE_INLINE_ITEMS_PER_PAGE = parseInt("${grailsApplication.config.inline_table_default_items_per_page}")
            const TABLE_FULL_PAGE_ITEMS_PER_PAGE = parseInt("${grailsApplication.config.full_page_table_default_items_per_page}")

            // updateItemsPerPage(TABLE_FULL_PAGE_ITEMS_PER_PAGE);
        </script>
        <asset:javascript src="utility/utility_organization.js"/>
        <asset:javascript src="utility/utility_contact_for_organization.js"/>
        <asset:javascript src="utility/utility_assessment_tool_url.js"/>
        <asset:javascript src="utility/utility_trustmark_recipient_identifier.js"/>
        <asset:javascript src="utility/utility_partner_organization_tip.js"/>
        <asset:javascript src="utility/utility_provider.js"/>
        <asset:javascript src="utility/utility_trustmark.js"/>
        <asset:javascript src="organization_view.js"/>
    </head>

    <body>
        <div class="container pt-4">
            <h2>${organization.name}</h2>

            <div id="organization-message"></div>

            <div id="organization-form">
                <div class="border rounded card">
                    <div class="card-header fw-bold">
                        <div class="row">
                            <div class="col-11">Basic Organization Information</div>

                            <div class="col-1 text-end"><a id="organizationFormId" class="btn btn-close p-0 align-middle"></a>
                            </div>
                        </div>
                    </div>

                    <div class="card-body"><input id="org_name" type="hidden" value="Trustmark Initiative">

                        <div class="row pb-2">
                            <label for="org_display" class="col-3 col-form-label">Abbreviation</label>

                            <div class="col-9">
                                <input id="org_display" type="text" class="form-control" placeholder="Enter Organization Abbreviation or Acronym" value="${organization.displayName}" readonly disabled>
                            </div>
                        </div>

                        <div class="row pb-2"><label for="org_url" class="col-3 col-form-label">URL</label>

                            <div class="col-9">
                                <input id="org_url" type="text" class="form-control" placeholder="Enter Organization URL" value="${organization.siteUrl}" readonly disabled>
                            </div>
                        </div>

                        <div class="row pb-2"><label for="org_desc" class="col-3 col-form-label">Description</label>

                            <div class="col-9">
                                <input id="org_desc" type="text" class="form-control" placeholder="Enter Organization Description" value="${organization.description}" readonly disabled>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <h2 class="pt-4">Points of Contact</h2>

            <div id="contact-message"></div>

            <table id="contact-table" class="table table-condensed table-striped table-bordered my-0"></table>

            <div id="contact-form" class="pt-4" style="display: none"></div>

        <sec:authorize access="isAuthenticated()">

            <h2 class="pt-4">Assessment Tool URLs</h2>

            <div id="assessment-tool-url-message"></div>

            <table id="assessment-tool-url-table" class="table table-condensed table-striped table-bordered my-0"></table>

            <div id="assessment-tool-url-form" class="pt-4" style="display: none"></div>

            <h2 class="pt-4">Trustmark Recipient Identifiers</h2>

            <div id="trustmark-recipient-identifier-message"></div>

            <table id="trustmark-recipient-identifier-table" class="table table-condensed table-striped table-bordered my-0"></table>

            <div id="trustmark-recipient-identifier-form" class="pt-4" style="display: none"></div>

            <h2 class="pt-4">Partner Organization Trust Interoperability Profiles</h2>

            <div id="partner-organization-tip-message"></div>

            <table id="partner-organization-tip-table" class="table table-condensed table-striped table-bordered my-0"></table>

            <div id="partner-organization-tip-form" class="pt-4" style="display: none"></div>

        </sec:authorize>

        <h2 class="pt-4">Systems</h2>

        <div id="provider-message"></div>

        <table id="provider-table" class="table table-condensed table-striped table-bordered my-0"></table>

        <div id="provider-form" class="pt-4" style="display: none"></div>

        <h2 class="pt-4">
            <div class="row">
                <div class="col-11">
                    Trustmark Binding Details
                </div>

                <div class="col-1 d-flex justify-content-end">

                    <sec:authorize access="isAuthenticated()">
                        <sec:authorize access="hasAnyAuthority('tbr-admin', 'tbr-org-admin')">
                            <g:if test="${organization.trustmarks.size() == 0}">
                                <button class="btn btn-primary bind-trustmark-button" id="btn-bind-trustmarks" style="white-space: nowrap">Bind Trustmarks</button>
                            </g:if>
                            <g:else>
                                <button class="btn btn-primary bind-trustmark-button" id="btn-refresh-trustmark-bindings" style="white-space: nowrap">Refresh Trustmark Bindings</button>
                            </g:else>
                        </sec:authorize>
                    </sec:authorize>

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
                        <th>Number of Trustmarks Bound</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td id="numberOfTrustmarksBound">${organization.trustmarks.size()}</td>
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
