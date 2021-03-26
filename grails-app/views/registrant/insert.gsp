<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="layout" content="main"/>
    <title>Add Registrant</title>
    <script type="text/javascript">
        $(document).ready(function(){
            listOrganizations([]);
            $('#lastName').focus();
        });

        let listOrganizations = function(data)  {
            list("${createLink(controller:'organization', action: 'list')}"
                , renderResults
                , {name: 'ALL'});
        }

        let renderResults = function(results)  {
            renderOrganizationOffset = curriedOrganization('organization-list')(results);
            renderOrganizationOffset(0);
        }

        let addOrganization = function(name, display, siteUrl, desc)  {
            if (checkOrganization(name, display, siteUrl, desc)) {
                add("${createLink(controller:'organization', action: 'add')}"
                    , listOrganizations
                    , { name: name
                        , displayName: display
                        , siteUrl: siteUrl
                        , description: desc
                    }
                );
                hideIt('organization-detail');
            }
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

        let checkRegistrant = function(lname, fname, email, pswd, repswd)  {
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
            if(pswd !== repswd)  {
                setDangerStatus("<b>Passwords don't match!</b>");
                document.getElementById('passwordOne').focus();
                return false;
            }
            return true;
        }

        let addRegistrant = function(lname, fname, email, pswd, repswd)  {
            if(checkRegistrant(lname, fname, email, pswd, repswd))  {
                getCheckedIds('edit-organizations', function(list) {
                    if(list.length === 0)  {
                        setWarningStatus("<b>You must select/check an organization.</b>");
                    } else {
                        let orgs = list.split(':');
                        add("${createLink(controller:'registrant', action: 'add')}"
                            , showRegistrant('registrant-table')
                            , { lname: lname
                                , fname: fname
                                , email: email
                                , pswd: pswd
                                , organizationId: orgs[0]
                            });
                    }
                });
            }
        }

        let renderResult = function(target, data)  {
            let html = "<table class='table table-condensed table-striped table-bordered'><thead><th style='width: auto;'>Name</th><th style='width: auto;'>Email Address</th><th style='width: auto;'>Organization</th></tr></thead><tbody>";
            let fName = "";
            let lName = "";
            data.forEach(r => {
                html += "<tr><td>"+r.contact.lastName+", "+r.contact.firstName+"</td>";
                html += "<td>"+r.contact.email+"</td>";
                html += "<td>"+r.organization.name+"</td></tr>";
                lName = r.contact.lastName;
                fName = r.contact.firstName;
            });
            html += "</tbody></table>";
            setSuccessStatus("Thank you for your application, "+ fName+ " " +lName);
            document.getElementById(target).innerHTML = html;
            scroll(0,0);
        }

        let showRegistrant = curryTwo(renderResult);
    </script>
</head>
<body>
<h2>Registrant Sign Up</h2>
<div id="verbiage">
    <p>Please fill out the form below to apply for a registrant account with this Trustmark Binding Registry.
    An administrator will review your request and notify you by email as soon as possible.</p>
</div>
<div id="status-header"></div>
<div id="registrant-table"></div>
<div id="registrant">
    <form class="form-inline">
        <div class="form-group">
            <div id="organization-list"></div><br>
            <p> If you don't see your organization in the list, please add it here first:</p> <button type="button" class="btn btn-info" onClick="javascript:renderOrganizationForm('organization-detail');">New Organization</button><br>
            <p><span style="color:red;">&nbsp;&nbsp;*</span> - Indicates required field.</p>
            <div id="organization-detail"></div><br>
        </div><br><br>
        <div class="form-group">
            <input style="width:300px;" id="lastName" type="text" class="form-control" placeholder="Enter Last Name" /><span style="color:red;">&nbsp;&nbsp;*</span><br><br>
            <input style="width:300px;" id="firstName" type="text" class="form-control" placeholder="Enter First Name" /><span style="color:red;">&nbsp;&nbsp;*</span><br><br>
            <input style="width:300px;" id="emailAddr" type="text" class="form-control" placeholder="Enter Email Address" /><span style="color:red;">&nbsp;&nbsp;*</span><br><br>
        </div><br>
        <div class="form-group">
            <input style="width:200px;" id="passwordOne" type="password" class="form-control" placeholder="Choose a Password" value=""/><span style="color:red;">&nbsp;&nbsp;*</span>&nbsp;
          &nbsp;&nbsp;<input style="width:200px;" id="passwordTwo" type="password" class="form-control" placeholder="Re-Enter Password" value=""/><span style="color:red;">&nbsp;&nbsp;*</span>
        </div><br><br>
        <button type="button" class="btn btn-info"
                onClick="addRegistrant(getElementById('lastName').value
                                    , getElementById('firstName').value
                                    , getElementById('emailAddr').value
                                    , getElementById('passwordOne').value
                                    , getElementById('passwordTwo').value);">Add</button>
    </form>
</div>
</body>
</html>