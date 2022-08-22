let listIdpAttribute = function (pid) {
    list(
        PROVIDER_LIST_IDP_ATTRIBUTES,
        function (idpAttributeList) {
            renderIdpAttributeTable("idp-attribute-table", idpAttributeList)
        },
        {id: pid})
}

let renderIdpAttributeTable = function (target, idpAttributeList) {
    let html = ``

    html += `<thead>`
    html += `<tr>`
    html += `<th scope="col" style="width: 100%">SAML Attributes Supported</th>`
    html += `</tr>`
    html += `</thead>`
    html += `<tbody>`

    if(idpAttributeList.length > 0) {
        idpAttributeList.forEach(idpAttribute => html += `<tr><td>${idpAttribute}</td></tr>`)
    } else {
        html += `<tr><td>There are no SAML Attributes supported.</td></tr>`
    }

    html += `</tbody>`

    document.getElementById(target).innerHTML = html
}
