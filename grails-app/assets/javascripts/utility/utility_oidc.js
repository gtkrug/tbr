let getOidcDetail = function (pid) {
    list(PROVIDER_OIDC_DETAILS,
        function (oidc) {
            renderOidcForm("openid-connect-form", oidc)
        },
        {id: pid})
}

let renderOidcForm = function (target, oidc) {
    let html = ""
    if (oidc.records === undefined || oidc.records.length === 0) {
        html += "There are no OpenId Connect Details."
    } else {
        html += renderInputHelper("systemType", false, "System Type", "", oidc.records.systemType, true)
        html += renderInputHelper("uniqueId", false, "Unique ID", "", oidc.records.uniqueId === undefined || oidc.records.uniqueId === null ? "(none)" : oidc.records.uniqueId, true)
        if (oidc.records.hasOidcMetadata) {
            html += renderTextHelper("viewOidcMetadataLink", false, `${oidc.records.systemType} Metadata`, `<a href="${(oidc.records.viewOidcMetadataLink)}">${(oidc.records.viewOidcMetadataLink)}</a>`)
        } else {
            html += renderInputHelper("viewOidcMetadataLink", false, `${oidc.records.systemType} Metadata`, "", "(none)", true)
        }
        if (oidc.records.openIdConnectMetadata) {
            for (const [key, value] of Object.entries(oidc.records.openIdConnectMetadata)) {
                if (Array.isArray(value)) {
                    html += renderTextHelper("openIdConnectMetadata", false, key, value.join("<br>"))
                } else {
                    html += renderInputHelper("openIdConnectMetadata", false, key, "", value, true)
                }
            }
        }
    }
    renderDialogForm(target, decorateForm("OpenID Connect Details", "oidcFormId", html, undefined, oidc.records.id === 0 ? "Add" : "Save", false))
}
