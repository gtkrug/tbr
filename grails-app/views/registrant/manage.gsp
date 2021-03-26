<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="layout" content="main"/>
    <title>Add Registrant</title>
    <script type="text/javascript">
        $(document).ready(function(){
            $('#passwordOne').focus();
        });

        let changePswd = function(origpswd, pswd, repswd)  {
            if(pswd !== repswd)  {
                setDangerStatus("<b>Passwords don't match!</b>");
                return;
            }
            update("${createLink(controller:'registrant', action: 'pswd')}"
                , renderResults
                , { id: ${registrant.id}
                    , pswd0: origpswd
                    , pswd1: pswd
                    , pswd2: repswd
                });
            clearForm();
        }

        let renderResults = function(result)  {
            if (result.length === 0)  {
                setDangerStatus("<b>Changing password failed!</b>");
            }  else {
                setSuccessStatus("Password changed! "+result.contact.firstName + " " + result.contact.lastName);
            }
        }

        let clearForm = function()  {
            document.getElementById('passwordOne').value = "";
            document.getElementById('passwordTwo').value = "";
            document.getElementById('passwordThree').value = "";
        }
    </script>
</head>
<body>
<h2>Registrant</h2>
<div id="status-header"></div>
<div id="registrant-table"></div>
<hr>
<div id="registrant">
    <form class="form-inline">
        <div class="form-group">
        </div><br>
        <div class="form-group">
            <div><b>Last Name:</b> ${registrant.contact.lastName}</div><br>
            <div><b>First Name:</b> ${registrant.contact.firstName}</div><br>
            <div><b>Phone:</b> ${registrant.contact.phone}</div><br>
            <div><b>Email Address:</b> ${registrant.contact.email}</div><br>
        </div><br>
        <div class="form-group">
            <input style="width:200px;" id="passwordOne" type="password" class="form-control" placeholder="Enter Password" /><br><br>
            <input style="width:200px;" id="passwordTwo" type="password" class="form-control" placeholder="Enter a New Password" /><br><br>
            <input style="width:200px;" id="passwordThree" type="password" class="form-control" placeholder="Re-Enter New Password" /><br><br>
        </div><br><br>
        <button type="button" class="btn btn-info"
                onClick="changePswd( getElementById('passwordOne').value
                    , getElementById('passwordTwo').value
                    , getElementById('passwordThree').value
                );">Change Password</button>
    </form>
</div>
</body>
</html>
