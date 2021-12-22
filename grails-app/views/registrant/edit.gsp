<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="layout" content="main"/>
    <title>Edit Registrant</title>
    <style type="text/css">
    </style>
    <script type="text/javascript">

        $(document).ready(function(){
            getDetails(${registrant.id});
        });

        let registrantDetail = curryFour(renderRegistrantForm);

        let selectOrganizations = function(id)  {
            list("${createLink(controller:'organization', action: 'list')}"
                , curriedSelectOrganizations('select-organization')(id)
                , {name: 'ALL'});
        }

        let selectRoles = function(id)  {
            list("${createLink(controller:'registrant', action: 'roles')}"
                , curriedSelectRoles('select-role')(id)
                , {name: 'ALL'});
        }

        let updateRegistrant = function(regId, lname, fname, email, phone) {
            if (checkRegistrant(lname, fname, email, phone)) {
                update("${createLink(controller:'registrant', action: 'updateContactInfo')}"
                    , function(registrant) {
                        // update profile menu with potential changes from registrant
                        $('#profile-name').html(registrant.user.contact.lastName + ", " + registrant.user.contact.firstName);

                        setSuccessStatus("Successfully updated the registrant account!");
                    }
                    , {
                        id: regId
                        , lname: lname
                        , fname: fname
                        , email: email
                        , phone: phone
                    });
            }
        }

        let getDetails = function(id)  {
            get("${createLink(controller:'registrant', action: 'get')}"
                , registrantDetail('registrant-detail')('Edit Registrant')(function(){updateRegistrant(id,
                    document.getElementById('detail_lastName').value,
                    document.getElementById('detail_firstName').value, document.getElementById('detail_email').value,
                    document.getElementById('detail_phone').value
                );})
                , { id: id }
            );
        }

        let checkRegistrant = function(lname, fname, email, phone)  {
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
            if(phone == null || phone.length === 0) {
                setDangerStatus("<b>Phone cannot be blank.</b>");
                document.getElementById('emailAddr').focus();
                return false;
            }
            return true;
        }

        let clearForm = function(notifyRegistrant)  {
            hideIt('registrant-detail')
            scroll(0,0);
        }
    </script>
</head>
<body>
<div id="status-header"></div>
<div id="registrant-detail"></div>
</body>
</html>