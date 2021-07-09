<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="layout" content="main"/>
    <title>Contacts</title>
    <script type="text/javascript">
        $(document).ready(function(){
            listContacts([]);
            selectContactTypes();
        });

        let selectContactTypes = function()  {
            list("${createLink(controller:'contact', action: 'types')}"
                , curriedContactTypes('select-contact-types')
                , {name: 'ALL'});
        }

        let listContacts = function(data)  {
            list("${createLink(controller:'contact', action: 'list')}"
                , renderResults
                , {id: '${registrant.organization.id}'});
        }

        let renderResults = function(results)  {
            let renderContactOffset = curriedContact('contacts-table')
            ({
                editable: true
                , fnAdd: function(){renderContactForm('contact-details', populateContactForm
                    , function(){updateContact(0, document.getElementById('lastName').value
                        , document.getElementById('firstName').value
                        , document.getElementById('emailAddr').value
                        , document.getElementById('phoneNbr').value
                        , document.getElementById('ctypes').options[document.getElementById('ctypes').selectedIndex].value
                        , document.getElementById('orgs').options[document.getElementById('orgs').selectedIndex].value);}
                    , {id:0});}
                , fnRemove: removeContact
                , title: 'Contacts'
                , hRef: 'javascript:getDetails'
            })
            (results);
            renderContactOffset(0);
        }

        let removeContact = function()  {
            if (confirm("The selected contact(s) may be in use by current systems. Do you wish to proceed?")) {
                getCheckedIds('edit-contacts', function (list) {
                    update("${createLink(controller:'contact', action: 'delete')}"
                        , listContacts
                        , {ids: list}
                    );
                });
            }
        }

        let checkContact = function(lname, fname, email, phone, type)  {
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
            if(type == null || type === "0") {
                setDangerStatus("<b>You must select a contact type.</b>");
                document.getElementById('ctypes').focus();
                return false;
            }
            return true;
        }

        let addContact = function(lname, fname, email, phone, type)  {
            if(checkContact(lname, fname, email, phone, type)) {
                add("${createLink(controller:'contact', action: 'add')}"
                    , listContacts
                    , { lname: lname
                        , fname: fname
                        , email: email
                        , phone: phone
                        , organizationId: '${registrant.organization.id}'
                        , type: type
                    });
                clearForm();
            } else {
                scroll(0,0);
            }
        }

        let clearForm = function()  {
            setSuccessStatus("<b>Successfully added contact.</b>");
            document.getElementById('lastName').value = "";
            document.getElementById('firstName').value = "";
            document.getElementById('emailAddr').value = "";
            document.getElementById('phoneNbr').value = "";
            scroll(0,0);
        }
    </script>
</head>
<body>
<h2>Contacts</h2>
<div id="status-header"></div>
<div id="contacts-table"></div>
<hr>
<p><span style="color:red;">&nbsp;&nbsp;*</span> - Indicates required field.</p>
<div id="contact">
    <form class="form-inline">
        <div class="form-group">
            <div style="width:300px" id="select-contact-types"></div><br>
            <input style="width:300px;" id="lastName" type="text" class="form-control" placeholder="Enter Last Name" /><span style="color:red;">&nbsp;&nbsp;*</span><br><br>
            <input style="width:300px;" id="firstName" type="text" class="form-control" placeholder="Enter First Name" /><span style="color:red;">&nbsp;&nbsp;*</span><br><br>
            <input style="width:300px;" id="phoneNbr" type="text" class="form-control" placeholder="Enter Phone Number" /><br><br>
            <input style="width:300px;" id="emailAddr" type="text" class="form-control" placeholder="Enter Email Address" /><span style="color:red;">&nbsp;&nbsp;*</span><br><br>
        </div><br><br>
        <button type="button" class="btn btn-info"
                onClick="addContact(getElementById('lastName').value
                    , getElementById('firstName').value
                    , getElementById('emailAddr').value
                    , getElementById('phoneNbr').value
                    , getElementById('ctypes').options[getElementById('ctypes').selectedIndex].value
                    );">Add</button>
    </form>
</div>
</body>
</html>