$(document).ready(function () {
    function update() {
        if (IS_SAML_IDP || IS_SAML_SP) {
            getProtocol(PROVIDER_ID)
            listEndpoint(PROVIDER_ID)
        }

        if (IS_SAML_IDP) {
            listIdpAttribute(PROVIDER_ID)
        } else if(IS_SAML_SP) {
            document.getElementById("idp-attribute-table").style = "display:none"
        }

        if (IS_CERTIFICATE) {
            getCertificateDetail(PROVIDER_ID)
        }

        if ((IS_OIDC_OP || IS_OIDC_RP) && LOGGED_IN) {
            getOidcDetail(PROVIDER_ID)
        }

        listContact(PROVIDER_ORGANIZATION_ID, PROVIDER_ID)

        if (IS_SAML_IDP || IS_SAML_SP || IS_OIDC_RP || IS_OIDC_OP) {
            listAttribute(PROVIDER_ID)
            listTag(PROVIDER_ID)
        }

        if (LOGGED_IN) {
            listTrustmarkRecipientIdentifier(PROVIDER_ID)
        }

        if (document.getElementById("btn-bind-trustmarks") != null) {
            document.getElementById("btn-bind-trustmarks").addEventListener("click", () => bindTrustmarks(PROVIDER_ID))
        }

        if (document.getElementById("btn-refresh-trustmark-bindings") != null) {
            document.getElementById("btn-refresh-trustmark-bindings").addEventListener("click", () => bindTrustmarks(PROVIDER_ID))
        }

        listPartnerSystemTip(PROVIDER_ID)
        listConformanceTargetTip(PROVIDER_ID)
        getBoundTrustmarks(PROVIDER_ID)
    }

    if (document.getElementById("upload") != null) {
        document.getElementById("uploadButton").addEventListener("click", function (event) {
            $.ajax({
                url: IS_SAML_IDP || IS_SAML_SP ?
                    PROVIDER_UPLOAD :
                    IS_CERTIFICATE ?
                        PROVIDER_UPLOAD_CERTIFICATE :
                        PROVIDER_UPLOAD_OIDC_METADATA,
                type: "POST",
                enctype: "multipart/form-data",
                data: new FormData(document.getElementById("upload")),
                processData: false,
                contentType: false,
                beforeSend: function () {
                },
                success: function (data, statusText, jqXHR) {
                    update()
                },
                error: function (jqXHR, statusText, errorThrown) {
                    update()
                }
            });
        })
    }

    update()
})
