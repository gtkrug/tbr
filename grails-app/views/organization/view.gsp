<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <script type="text/javascript">
        $(document).ready(function()  {
            getProviders(${organization.id});
            getRepos(${organization.id});
            getTrustmarkRecipientIdentifiers(${organization.id})
        });

        let selectProviderTypes = function()  {
            list("${createLink(controller:'provider', action: 'types')}"
                , curriedProviderTypes('select-provider-types')
                , {name: 'ALL'});
        }

        let removeProviders = function()  {
            getCheckedIds('edit-providers', function(list){
                update("${createLink(controller:'provider', action: 'delete')}"
                    , function (data){ getProviders('${organization.id}'); }
                    , { ids: list, oid: ${organization.id} }
                );
            });
        }

        let insertProvider = function(name, entityId, tYpe)  {
            add("${createLink(controller:'provider', action: 'add')}"
                , function(data){getProviders(${organization.id});}
                , {
                    orgid: ${organization.id}
                    , type: tYpe
                    , name: name
                    , entity: entityId
                }
            );
        }

        let addProvider = function(target, fn)  {
            renderProviderForm(target, fn);
            selectProviderTypes();
            document.getElementById('providerName').focus();
        }

        let getProviders = function(oid) {
            list("${createLink(controller:'provider', action: 'list')}"
                , providerResults
                , { orgid: oid }
            );
        }

        let providerResults = function(results)  {
            renderProviderOffset = curriedProvider('providers')("${createLink(controller:'provider', action: 'view')}")(results)
            renderProviderOffset(0);
        }

        let addRepo = function(repoNm)  {
            add("${createLink(controller:'organization', action: 'addRepo')}"
                , function(data){getRepos(${organization.id});}
                , {
                    orgid: ${organization.id}
                  , name: repoNm
                }
            );
        }

        let deleteRepo = function(repoid)  {
            add("${createLink(controller:'organization', action: 'deleteRepo')}"
                , function(data){getRepos(${organization.id});}
                , {
                    orgid: ${organization.id}
                  , rid: repoid
                }
            );
        }

        let getRepos = function(oid) {
            list("${createLink(controller:'organization', action: 'repos')}"
                , repoResults
                , { oid: oid }
            );
        }

        let repoResults = function(results)  {
            renderRepoOffset = curriedRepos('assessmentRepos')(results)
            renderRepoOffset(0);
        }



        let addTrustmarkRecipientIdentifier = function(trustmarkRecipientIdentifier)  {
            add("${createLink(controller:'organization', action: 'addTrustmarkRecipientIdentifier')}"
                , function(data){getTrustmarkRecipientIdentifiers(${organization.id});}
                , {
                    orgid: ${organization.id}
                    , identifier: trustmarkRecipientIdentifier
                }
            );
        }

        let deleteTrustmarkRecipientIdentifier = function(tmrid)  {
            add("${createLink(controller:'organization', action: 'deleteTrustmarkRecipientIdentifier')}"
                , function(data){getTrustmarkRecipientIdentifiers(${organization.id});}
                , {
                    orgid: ${organization.id}
                    , tmrid: tmrid
                }
            );
        }

        let getTrustmarkRecipientIdentifiers = function(oid) {
            list("${createLink(controller:'organization', action: 'trustmarkRecipientIdentifiers')}"
                , trustmarkRecipientIdentifierResults
                , { oid: oid }
            );
        }

        let trustmarkRecipientIdentifierResults = function(results)  {
            renderTrustmarkRecipientIdentifiersOffset = curriedTrustmarkRecipientIdentifier('trustmarkRecipientIdentifiers')(results)
            renderTrustmarkRecipientIdentifiersOffset(0);
        }

        let updateOrganization = function(orgId, url, desc, display)  {
            update("${createLink(controller:'organization', action: 'update')}"
                , function(data){}
                , { url: url
                  , desc: desc
                  , id: orgId
                  , display: display
                }
            );
        }
    </script>
    <meta charset="UTF-8">
    <meta name="layout" content="main"/>
    <title>Organization</title>
</head>
<body>
<div id="status-header"></div>
<h4><b>${organization.name}</b></h4>
<table class='table table-condensed table-striped table-bordered'>
    <tr>
        <td style='width: auto;'><b>Display Name</b></td><td style='width: auto;'><input id="org-display" type="text" size="50" value="${organization.displayName}"></td>
    </tr>
    <tr>
        <td style='width: auto;'><b>URL</b></td><td style='width: auto;'><input id="org-url" type="text" size="50" value="${organization.siteUrl}"></td>
    </tr><tr>
    <td style='width: auto;'><b>Description</b></td><td style='width:auto;'><textarea id="org-description" cols="60" rows="4">${organization.description}</textarea></td>
    </tr>
    <tr>
    <td style='width: auto;'><b>Assessment Tool URLs</b></td>
        <td><div id="assessmentRepos"></div></td>
    </tr>

    <tr>
        <td style='width: auto;'><b>Trustmark Recipient Identifiers</b></td>
        <td><div id="trustmarkRecipientIdentifiers"></div></td>
    </tr>

</table>
<button class="btn btn-info" onclick="updateOrganization('${organization.id}', document.getElementById('org-url').value, document.getElementById('org-description').value, document.getElementById('org-display').value);">Update</button>&nbsp
<div style="float:right;" id="uploadForm">
    <form method="post" enctype="multipart/form-data" class="form-inline">
        <div class="form-group">
            <input name="filename" type="file" class="form-control" accept=".xml"/>
            <input name="id" type="hidden" value="${organization.id}"/>
        </div>
        <button type="submit" class="btn btn-default">Upload</button>
    </form>
</div>
<br><br><br>
<button class="btn btn-info" onclick="addProvider('new-provider', function(){insertProvider(document.getElementById('providerName').value, document.getElementById('providerEntityId').value, document.getElementById('pType').options[document.getElementById('pType').selectedIndex].value);});">Add</button>&nbsp;
<button class="btn btn-info" onclick="removeProviders();">Remove</button>
<br>
<div id="new-provider"></div>
<br>
<div id="providers"></div>
<br>
%{--
<ul class="nav nav-tabs" id="org-tab-list" role="tablist">
    <g:each in="${organization.providers}" var="pr">
        <li class="nav-item">
            <a class="nav-link" onclick="showProvider('${pr.id}');" id="${pr.id}-tab" data-toggle="tab" role="tab" href="#${pr.id}" aria-controls="${pr.id}">${pr.name}</a>
        </li>
    </g:each>
    <li class="nav-item">
        <a class="nav-link" id="plus-tab" onclick="addProvider('#new-provider');" data-toggle="tab" role="tab" href="#plus-id" aria-controls="plus-id"><span class='glyphicon glyphicon-plus'></span></a>
    </li>
</ul>
<div class="tab-content" id="org-content">
    <g:each in="${organization.providers}" var="pr">
        <div class="tab-pane fade" id="${pr.id}" role="tab-panel" aria-labelledby="${pr.id}-tab"></div>
    </g:each>
</div>
--}%
</body>
</html>