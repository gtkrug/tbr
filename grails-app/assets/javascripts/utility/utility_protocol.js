let getProtocol = function (pid) {
    list(
        PROVIDER_PROTOCOL_DETAILS,
        function (protocol) {
            renderProtocolForm(
                "protocol-form",
                {
                    editable: protocol.editable
                },
                protocol)
        },
        {id: pid})
}

let renderProtocolForm = function (target, protocolMetadata, protocol) {
    let html = ""
    if (protocol.records === undefined || protocol.records.length === 0) {
        html += "There are no Protocol Details."
    } else {
        html += renderInputHelper("systemType", false, "System Type", "", protocol.records.systemType, true)
        if (protocol.records.entityId && protocol.records.entityId.length > 0) {
            html += renderInputHelper("subject", false, "Entity ID", "", protocol.records.entityId, true)
            html += renderTextHelper("nameIdFormats", false, "Name ID Format", protocol.records.nameIdFormats.length === 0 ? "&nbsp;" : protocol.records.nameIdFormats.join("<br/>"))
            html += renderTextHelper("signingCertificateLink", false, "Signing Certificate", `<a href="${(protocol.records.signingCertificateLink)}" target="_blank">view</a>`)
            html += renderTextHelper("encryptionCertificateLink", false, "Encrypting Certificate", `<a href="${(protocol.records.encryptionCertificateLink)}" target="_blank">view</a>`)

            if (protocol.records.hasSamlMetadataGenerated) {
                html += renderTextHelper("viewSamlMetadataLink", false, "SAML 2 Metadata", `<a href="${(protocol.records.viewSamlMetadataLink)}" target="_blank">view</a>`)
                html += renderInputHelper("lastTimeSAMLMetadataGeneratedDate", false, "SAML 2 Metadata Generation Date", "", protocol.records.lastTimeSAMLMetadataGeneratedDate, true)
            }
        }
    }

    renderDialogForm(target, decorateForm("Protocol Details", "protocolFormId",
        html, protocolMetadata.editable ? "protocolOk" : undefined, "Generate", false,
        "<span id='saml2-metadata-generation_status' style='width:10%; margin-left: 10px;'></span>"))

    if(document.getElementById("protocolOk") !== null) {
        document.getElementById("protocolOk").addEventListener("click", () => generateSaml2Metadata(PROVIDER_ID))
    }
}

function generateSaml2Metadata(providerId) {
    alert("Warning: This process will generate a fresh metadata object, but it will use the trustmark binding data that is currently cached in the local registry database.");

    setGenerateMetadataStatus(`<span class="spinner-grow spinner-grow-sm"></span> Generating metadata...`,
        "saml2-metadata-generation_status")

    $.ajax({
        url: PROVIDER_GENERATE_SAML_2_METADATA,
        beforeSend: function () {
        },
        success: function (data, statusText, jqXHR) {

            setGenerateMetadataStatus(``, "saml2-metadata-generation_status")

            getProtocol(providerId)
        },
        error: function (jqXHR, statusText, errorThrown) {
        }
    });
}

function setGenerateMetadataStatus(content, elementId) {
    document.getElementById(elementId).classList.remove("d-none")
    document.getElementById(elementId).innerHTML = content;
}
