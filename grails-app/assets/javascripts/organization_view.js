$(document).ready(function () {
    listContact()
    listProvider(ORGANIZATION_ID)

    if (LOGGED_IN) {
        getOrganization(ORGANIZATION_ID, false)
        listAssessmentToolUrl(ORGANIZATION_ID)
        listTrustmarkRecipientIdentifier(ORGANIZATION_ID)
        listPartnerOrganizationTip(ORGANIZATION_ID)
    }

    if (document.getElementById("btn-bind-trustmarks") != null) {
        document.getElementById("btn-bind-trustmarks").addEventListener("click", () => bindTrustmarks(ORGANIZATION_ID))
    }

    if (document.getElementById("btn-refresh-trustmark-bindings") != null) {
        document.getElementById("btn-refresh-trustmark-bindings").addEventListener("click", () => bindTrustmarks(ORGANIZATION_ID))
    }

    getBoundTrustmarks(ORGANIZATION_ID);
})
