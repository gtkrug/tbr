<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="layout" content="main"/>
    <title>Add Endpoint</title>
    <script type="text/javascript">
        $(document).ready(function(){
            $('#endptUrl').focus();

            listEndpoints([]);

            list("${createLink(controller:'contact', action: 'list')}"
                , renderContacts
                , {id: 0});

            list("${createLink(controller:'endPoint', action: 'etypes')}"
                , renderTypes
                , {name: 'ALL'});
        });

        let data = [];

        function addEndpoint(endptUrl, signKey, encKey, postBindUrl, reBindUrl, paosBindUrl, aCnctId, tCnctId, type)  {
            add("${createLink(controller:'endPoint', action: 'add')}"
                , listEndpoints
                , { url: endptUrl
                    , signingKey: signKey
                    , encryptionKey: encKey
                    , postUrl: postBindUrl
                    , redirectUrl: reBindUrl
                    , paosUrl: paosBindUrl
                    , adminContact: aCnctId
                    , techContact: tCnctId
                    , endptType: type
                    , organizationId: ${registrant.organization.id}
                });
            clearForm();
        }

        function listEndpoints(data)  {
            list("${createLink(controller:'endPoint', action: 'list')}"
                , renderResults
                , {id: '${registrant.organization.id}'});
        }

        function renderResults(results)  {
            data = results;
            renderEndpoints(0);
        }

        function renderEndpoints(offset)  {
                let html = "";
                if (data.length > MAX_DISPLAY)  {
                    html += buildPagination(offset, MAX_DISPLAY, data.length, 'renderEndpoints');
                }
                html += "<table class='table table-condensed table-striped table-bordered'><thead><th style='width: auto;'>Url</th><th style='width: auto;'>Type</th><th style='width: auto;'>Recipient</th><th style='width: auto;'>Provider</th></tr></thead><tbody>";
                if (data.length == 0)  {
                    html += '<tr><td colspan="4"><em>There are no endpoints.</em></td></tr>';
                }  else {
                    let idx = 0;
                    data.forEach(e => {
                        if(idx >= offset && idx < offset+MAX_DISPLAY) {
                            html += "<tr><td>" + e.url + "</td>";
                            html += "<td>" + e.type.name + "</td>";
                            html += "<td>" + e.redirectBindingUrl + "</td>";
                            html += "<td>" + e.postBindingUrl + "</td>";
                            html += "</tr>";
                        }
                        ++idx;
                    });
                }
                html += "</tbody></table>";
                $('#resultContainer').html(html);
        }

        function renderContacts(data)  {
            renderAdminContacts(data);
            renderTechContacts(data);
        }

        function renderAdminContacts(data)  {
            html = "<select class='form-control' id='adminCntct'>";
            html += "<option value='0'>Select a Contact</option>";
            data.forEach(o => {
                html += "<option value='"+o.id+"'>"+o.email+"</option>";
            });
            html += "</select>";
            $('#adminContact').html(html);
        }

        function renderTechContacts(data)  {
            html = "<select class='form-control' id='techCntct'>";
            html += "<option value='0'>Select a Contact</option>";
            data.forEach(o => {
                html += "<option value='"+o.id+"'>"+o.email+"</option>";
            });
            html += "</select>";
            $('#techContact').html(html);
        }

        function renderTypes(data)  {
            html = "<select class='form-control' id='types'>";
            data.forEach(o => {
                html += "<option value='"+o.name+"'>"+o.name+"</option>";
            });
            html += "</select>";
            $('#endpointType').html(html);
        }

        function clearForm()  {
            document.getElementById('endptUrl').value = "";
            document.getElementById('postBindingUrl').value = "";
            document.getElementById('adminContact').selectedIndex = 0;
            document.getElementById('techContact').selectedIndex = 0;
        }
    </script>
</head>

<body>
<h2>Endpoint</h2>
<div id="status-header"></div>
<div id="resultContainer"></div>
<hr>
<div id="endpoint">
    <form class="form-inline">
        <div class="form-group">
            <input style="width:300px;" id="endptUrl" type="text" class="form-control" placeholder="Endpoint Url" /><br>
            <input style="width:300px;" id="signingKey" type="text" class="form-control" placeholder="Signing Key" />
            <input style="width:300px;" id="encryptionKey" type="text" class="form-control" placeholder="Encryption Key" /><br>
        </div><br>
        <div class="form-group">
            <input style="width:300px;" id="postBindingUrl" type="text" class="form-control" placeholder="Post Binding Url" />
            <input style="width:300px;" id="redirectBindingUrl" type="text" class="form-control" placeholder="Redirect Binding Url" />
            <input style="width:300px;" id="paosBindingUrl" type="text" class="form-control" placeholder="Paos Binding Url" />
        </div><br>
        <div class="form-group">
            Administrative Contact:&nbsp<div id="adminContact"></div><br>
            Technical Contact:&nbsp<div id="techContact"></div><br>
            Endpoint Type:&nbsp<div id="endpointType"></div><br>
        </div><br><br>
        <button type="button" class="btn btn-info"
                onClick="addEndpoint(getElementById('endptUrl').value
                    , getElementById('signingKey').value
                    , getElementById('encryptionKey').value
                    , getElementById('postBindingUrl').value
                    , getElementById('redirectBindingUrl').value
                    , getElementById('paosBindingUrl').value
                    , getElementById('adminCntct').options[getElementById('adminCntct').selectedIndex].value
                    , getElementById('techCntct').options[getElementById('techCntct').selectedIndex].value
                    , getElementById('types').options[getElementById('types').selectedIndex].value
                );">Add</button>
    </form>
</div>
</body>
</html>