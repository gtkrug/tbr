<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="layout" content="main"/>
    <title>Organization</title>
    <script type="text/javascript">
        $(document).ready(function(){
            listOrganizations([]);
        });
        let checkOrganization = function(name, display, siteUrl, desc) {
            if (name == null || name.length === 0) {
                setDangerStatus("<b>Organization name cannot be blank.</b>");
                document.getElementById('name').focus();
                return false;
            }
            if (display == null || display.length === 0) {
                setDangerStatus("<b>Display name cannot be blank.</b>");
                document.getElementById('displayName').focus();
                return false;
            }
            if (siteUrl == null || siteUrl.length === 0) {
                setDangerStatus("<b>URL cannot be blank.</b>");
                document.getElementById('siteUrl').focus();
                return false;
            }
            if (desc == null || desc.length === 0) {
                setDangerStatus("<b>Description cannot be blank.</b>");
                document.getElementById('description').focus();
                return false;
            }
            return true;
        }

        let addOrganization = function(name, display, siteUrl, desc)  {
            if(checkOrganization(name, display, siteUrl, desc)) {
                add("${createLink(controller:'organization', action: 'add')}"
                    , listOrganizations
                    , { name: name
                    , displayName: display
                    , siteUrl: siteUrl
                    , description: desc
                      }
                   );
                clearForm();
            } else {
                scroll(0,0);
            }
        }

        let listOrganizations = function(data)  {
            list("${createLink(controller:'organization', action: 'list')}"
                , renderResults
                , {name: 'ALL'});
        }

        let renderResults = function(results)  {
            renderOrganizationOffset = curriedOrganization('organization-table')
            ({
                editable: false
                , fnDraw: drawOrganizations
                , title: 'Organizations'
                , hRef: 'javascript:getDetails'
            })
            (results);
            renderOrganizationOffset(0);
            document.getElementById('name').focus();
        }

        let clearForm = function()  {
            setSuccessStatus("<b>Successfully added organization.</b>");
            document.getElementById('name').value = "";
            document.getElementById('displayName').value = "";
            document.getElementById('siteUrl').value = "";
            document.getElementById('description').value = "";
            scroll(0,0);
        }
    </script>
</head>

<body>
<h2>Organizations</h2>
<div id="status-header"></div>
<div id="organization-table"></div>
<hr>
<div id="organization">
    <p>If you don't see your organization in the above table, please enter it in the form below.</p>
    <p><span style="color:red;">&nbsp;&nbsp;*</span> - Indicates required field.</p>
    <form class="form-inline">
        <div class="form-group">
            <input style="width:300px;" id="name" type="text" class="form-control" placeholder="Enter Organization Name" /><span style="color:red;">&nbsp;&nbsp;*</span><br><br>
            <input style="width:300px;" id="displayName" type="text" class="form-control" placeholder="Organization Display Name" /><span style="color:red;">&nbsp;&nbsp;*</span><br><br>
            <input style="width:300px;" id="siteUrl" type="text" class="form-control" placeholder="Organization URL" /><span style="color:red;">&nbsp;&nbsp;*</span><br><br>
            <textarea id="description" rows="4" cols="60" class="form-control" placeholder="Organization Description"></textarea><span style="color:red;">&nbsp;&nbsp;*</span><br><br>
        </div><br><br>
        <button type="button" class="btn btn-info"
                onClick="addOrganization(getElementById('name').value
                    , getElementById('displayName').value
                    , getElementById('siteUrl').value
                    , getElementById('description').value
                    );">Add</button>
    </form>
</div>
</body>
</html>