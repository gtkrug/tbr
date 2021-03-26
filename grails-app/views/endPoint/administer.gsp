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

            list("${createLink(controller:'organization', action: 'list')}"
                , renderOrganizations
                , {name: 'ALL'});
        });

        let data = [];

        function addEndpoint(endptUrl, signKey, encKey, postBindUrl, reBindUrl, paosBindUrl, aCnctId, tCnctId, type, orgId)  {
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
                    , organizationId: orgId
                });
            clearForm();
        }

        function listEndpoints(data)  {
            list("${createLink(controller:'endPoint', action: 'list')}"
                , renderResults
                , {id: 0});
        }

        function renderResults(results)  {
            data = results;
            $('#resultContainer').html(renderEndpoints(0));
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
            document.getElementById('signingKey').value = "";
            document.getElementById('encryptionKey').value = "";
            document.getElementById('postBindingUrl').value = "";
            document.getElementById('redirectBindingUrl').value = "";
            document.getElementById('paosBindingUrl').value = "";
            document.getElementById('adminContact').selectedIndex = 0;
            document.getElementById('techContact').selectedIndex = 0;
            document.getElementById('orgs').selectedIndex = 0;
        }
    </script>
</head>

<body>
<h2>Endpoints</h2>
<div id="statusHeader"></div>
<div id="resultContainer"></div>
<hr>
<div id="endpoint">
    <form class="form-inline">
        <div class="form-group">
            <div style="width:300px" id="select-organization"></div><br>
            <input style="width:300px;" id="endptUrl" type="text" class="form-control" placeholder="Endpoint Url" /><br>
            <input style="width:300px;" id="signingKey" type="text" class="form-control" placeholder="Signing Key" /><br>
            <input style="width:300px;" id="encryptionKey" type="text" class="form-control" placeholder="Encryption Key" /><br>
        </div><br>
        <div class="form-group">
            <input style="width:300px;" id="postBindingUrl" type="text" class="form-control" placeholder="Post Binding Url" />
            <input style="width:300px;" id="redirectBindingUrl" type="text" class="form-control" placeholder="Redirect Binding Url" />
            <input style="width:300px;" id="paosBindingUrl" type="text" class="form-control" placeholder="Paos Binding Url" />
        </div><br>
        <div class="form-group">
            Administrative Contact:&nbsp<div style="width:200px" id="adminContact"></div><br>
            Technical Contact:&nbsp<div style="width:200px" id="techContact"></div><br>
            Endpoint Type:&nbsp<div style="width:200px" id="endpointType"></div><br>
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
                    , getElementById('orgs').options[getElementById('orgs').selectedIndex].value
                );">Add</button>
    </form>
</div>
</body>
</html>