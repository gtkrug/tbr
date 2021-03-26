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

        let organizationDetail = curryThree(renderOrganizationForm);

        let listOrganizations = function(data)  {
            list("${createLink(controller:'organization', action: 'list')}"
                , renderResults
                , {name: 'ALL'});
        }

        let renderResults = function(results)  {
            renderOrganizationOffset = curriedOrganization('organization-table')
            ({
                editable: true
                , fnAdd: function(){renderOrganizationForm('organization'
                        , function(){addOrganization(document.getElementById('org_name').value
                            , document.getElementById('org_display').value
                            , document.getElementById('org_url').value
                            , document.getElementById('org_desc').value)}
                        , {id:0})}
                , fnRemove: removeOrganization
                , fnDraw: drawOrganizations
                , title: 'Organizations'
                , hRef: 'javascript:getDetails'
            })
            (results);
            renderOrganizationOffset(0);
        }

        let getDetails = function(id)  {
            get("${createLink(controller:'organization', action: 'get')}"
                , organizationDetail('organization')(function(){updateOrganization(id, document.getElementById('org_name').value, document.getElementById('org_display').value, document.getElementById('org_url').value, document.getElementById('org_desc').value);})
                , {id: id});
        }

        let checkOrganization = function(name, display, siteUrl, desc) {
            if (name == null || name.length === 0) {
                setDangerStatus("<b>Organization name cannot be blank.</b>");
                document.getElementById('org_name').focus();
                return false;
            }
            if (display == null || display.length === 0) {
                setDangerStatus("<b>Display name cannot be blank.</b>");
                document.getElementById('org_display').focus();
                return false;
            }
            if (siteUrl == null || siteUrl.length === 0) {
                setDangerStatus("<b>URL cannot be blank.</b>");
                document.getElementById('org_url').focus();
                return false;
            }
            if (desc == null || desc.length === 0) {
                setDangerStatus("<b>Description cannot be blank.</b>");
                document.getElementById('org_desc').focus();
                return false;
            }
            return true;
        }

        let addOrganization =  function(name, display, siteUrl, desc)  {
            if(checkOrganization(name, display, siteUrl, desc))  {
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

        let updateOrganization =  function(id, name, display, siteUrl, desc)  {
            if(checkOrganization(name, display, siteUrl, desc))  {
                update("${createLink(controller:'organization', action: 'update')}"
                    , listOrganizations
                    , {
                        id: id
                        , display: display
                        , url: siteUrl
                        , desc: desc
                    }
                );
                clearForm();
            } else {
                scroll(0,0);
            }
        }

        let removeOrganization = function()  {
            getCheckedIds('edit-organizations', function(list)  {
                update("${createLink(controller:'organization', action: 'delete')}"
                    , listOrganizations
                    , { ids: list }
                );
            });
        }

        let clearForm = function()  {
            setSuccessStatus("<b>Successfully saved organization.</b>");
            hideIt('organization');
            scroll(0,0);
        }
    </script>
</head>

<body>
<div id="status-header"></div>
<div id="organization-table"></div>
<p><span style="color:red;">&nbsp;&nbsp;*</span> - Indicates required field.</p>
<div id="organization"></div>
</body>
</html>
