let listEndpoint = function (pid) {
    list(
        ENDPOINT_LIST,
        function (endpointList) {
            renderEndpointTable("endpoint-table", endpointList)
        },
        {id: pid})
}

let renderEndpointTable = function (target, endpointList) {
    let html = ``

    html += `<thead>`
    html += `<tr>`
    html += `<th scope="col" colspan="3" style="width: 100%">Endpoints</th>`
    html += `</tr>`
    html += `</thead>`
    html += `<tbody>`
    if (endpointList.length > 0) {
        endpointList.forEach(endpoint => {
            html += "<tr>";
            if (endpoint.name === "Attribute Consuming Service") {
                html += "<td>" + endpoint.name + "</td>";
                html += "<td>" + endpoint.serviceName + "</td>";
                let names = "";
                endpoint.attributes.forEach(a => {
                    if (a.name !== 'ServiceName') names += a.name + "<br>";
                });
                html += "<td>" + names + "</td>";
            } else {
                html += "<td>" + endpoint.name + "</td>";
                html += "<td>" + endpoint.binding.substring(endpoint.binding.lastIndexOf(":") + 1) + "</td>";
                html += "<td>" + endpoint.url + "</td>";
            }
            html += "</tr>";
        })
    } else {
        html += `<tr><td>There are no endpoints supported.</td></tr>`
    }
    html += `</tbody>`

    document.getElementById(target).innerHTML = html
}
