<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="layout" content="main"/>
    <title>Registrant</title>
    <style type="text/css">
    </style>
    <script type="text/javascript">
        $(document).ready(function(){
            listRegistrants([]);
        });

        let registrantDetail = curryThree(renderRegistrantForm);

        let listRegistrants = function(data)  {
            list("${createLink(controller:'registrant', action: 'list')}"
                , renderResults
                , {id: 0});
        }

        let activate = function(list){
            update("${createLink(controller:'registrant', action: 'activate')}"
                , listRegistrants
                , { ids: list }
            );
        }

        let deactivate = function(list){
            update("${createLink(controller:'registrant', action: 'deactivate')}"
                , listRegistrants
                , { ids: list }
            );
        }

        let deleteRegistrants = function(list){
            update("${createLink(controller:'registrant', action: 'delete')}"
                , listRegistrants
                , { ids: list }
            );
        }

        let selectOrganizations = function(id)  {
            list("${createLink(controller:'organization', action: 'list')}"
                , curriedSelectOrganizations('select-organization')(id)
                , {name: 'ALL'});
        }

        let updateRegistrant = function(regId, lname, fname, email, phone, orgId)  {
            if(checkRegistrant(lname, fname, email, orgId))  {
                if(regId === 0)  {
                    add("${createLink(controller:'registrant', action: 'add')}"
                        , listRegistrants
                        , { lname: lname
                            , fname: fname
                            , email: email
                            , phone: phone
                            , pswd: 'changeMe!'
                            , organizationId: orgId
                        });
                } else {
                    update("${createLink(controller:'registrant', action: 'update')}"
                        , listRegistrants
                        , { id: regId
                            , lname: lname
                            , fname: fname
                            , email: email
                            , phone: phone
                            , organizationId: orgId
                        });
                }
                clearForm();
            } else {
                scroll(0,0);
            }
        }

        let getDetails = function(id)  {
            get("${createLink(controller:'registrant', action: 'get')}"
                , registrantDetail('registrant-detail')(function(){updateRegistrant(id, document.getElementById('detail_lastName').value, document.getElementById('detail_firstName').value, document.getElementById('detail_email').value, document.getElementById('detail_phone').value, document.getElementById('orgs').options[document.getElementById('orgs').selectedIndex].value);})
                , { id: id }
            );
        }

        let renderResults = function(results)  {
            renderRegistrantOffset = curriedRegistrant('registrant-table')
            ({
                editable: true
                , fnAdd: function(){renderRegistrantForm('registrant-detail'
                    , function(){updateRegistrant(0, document.getElementById('detail_lastName').value
                        , document.getElementById('detail_firstName').value
                        , document.getElementById('detail_email').value
                        , document.getElementById('detail_phone').value
                        , document.getElementById('orgs').options[document.getElementById('orgs').selectedIndex].value);}
                    , {id: 0});}
                , fnRemove: removeRegistrant
                , fnDraw: drawRegistrants
                , title: 'Registrants'
            })
            (results);
            renderRegistrantOffset(0);
        }

        let activateRegistrant = function()  {
            getCheckedIds('activate', activate);
            getCheckedIds('deactivate', deactivate);
        }

        let removeRegistrant = function()  {
            getCheckedIds('activate', deleteRegistrants);
            getCheckedIds('deactivate', deleteRegistrants);
        }

        let checkRegistrant = function(lname, fname, email, orgId)  {
            if(lname == null || lname.length === 0) {
                setDangerStatus("<b>Last name cannot be blank.</b>");
                document.getElementById('lastName').focus();
                return false;
            }
            if(fname == null || fname.length === 0) {
                setDangerStatus("<b>First name cannot be blank.</b>");
                document.getElementById('firstName').focus();
                return false;
            }
            if(email == null || email.length === 0) {
                setDangerStatus("<b>Email cannot be blank.</b>");
                document.getElementById('emailAddr').focus();
                return false;
            }
            if(orgId == null || orgId === "0") {
                setWarningStatus("<b>You must select an organization.</b>");
                document.getElementById('orgs').focus();
                return false;
            }
            return true;
        }

        let clearForm = function()  {
            setSuccessStatus("Successfully saved registrant!");
            hideIt('registrant-detail')
            scroll(0,0);
        }
    </script>
</head>
<body>
<div id="status-header"></div>
<div id="registrant-table"></div>
<div  class="tm-right" id="activate">
    <form class="form-inline">
        <button type="button" class="btn btn-info"
                onClick="activateRegistrant();">Activate / Deactivate</button>
    </form>
</div>
<br><br>
<div id="registrant-detail"></div>
</body>
</html>