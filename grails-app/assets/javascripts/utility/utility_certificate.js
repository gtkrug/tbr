let getCertificateDetail = function (pid) {
    list(PROVIDER_CERTIFICATE_DETAILS,
        function (certificate) {
            renderCertificateForm("certificate-form", certificate)
        },
        {id: pid})
}

let renderCertificateForm = function (target, certificate) {
    let html = ""
    if (certificate.records === undefined) {
        html += "There are no Certificate Details."
    } else {
        html += renderInputHelper("systemType", false, "System Type", "", certificate.records.systemType, true)
        if (certificate.records.subject && certificate.records.subject.length > 0) {
            html += renderInputHelper("subject", false, "Subject", "", certificate.records.subject, true)
            html += renderInputHelper("issuer", false, "Issuer", "", certificate.records.issuer, true)
            html += renderInputHelper("notBefore", false, "Valid Not Before", "", certificate.records.notBefore, true)
            html += renderInputHelper("notAfter", false, "Valid Not After", "", certificate.records.notAfter, true)
            html += renderTextHelper("systemCertificateUrl", false, "X509 Certificate", `<a href = "${certificate.records.systemCertificateUrl}">${certificate.records.systemCertificateUrl}</a>`)
        }
    }
    renderDialogForm(target, decorateForm("Certificate Details", "certificateFormId", html, undefined, certificate.records.id === 0 ? "Add" : "Save", false, 'false'))
}
