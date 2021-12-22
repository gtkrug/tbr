<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title>Change Existing Password</title>
	</head>
	<body>

        <h1>Change Existing Password</h1>

        <sec:ifLoggedIn>
            <div class="col-md-12" style="margin-top: 2em;">

                <form class="form-horizontal">
                    <div class="form-group">
                        <label for="existingPassword" class="col-sm-2 control-label">Existing Password</label>
                        <div class="col-sm-10">
                            <input type="password" class="form-control" id="existingPassword" name="existingPassword" placeholder="Existing Password">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="newPassword" class="col-sm-2 control-label">New Password</label>
                        <div class="col-sm-10">
                            <input type="password" class="form-control" id="newPassword" name="newPassword" placeholder="New Password">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="confirmPassword" class="col-sm-2 control-label">Confirm Password</label>
                        <div class="col-sm-10">
                            <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" placeholder="Confirm Password">
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-offset-2 col-sm-10">
                            <a href="javascript:changePasswordRequest()" class="btn btn-default">Submit</a>
                        </div>
                    </div>
                </form>

                <div id="messageFeedbackWindow" style="margin-top: 2em;">&nbsp;</div>

            </div>

            <script type="text/javascript">
                function changePasswordRequest() {

                    var existingPassword = $('#existingPassword').val();
                    var newPassword = $('#newPassword').val();
                    var confirmPassword = $('#confirmPassword').val();

                    if (newPassword != confirmPassword) {
                        $("#messageFeedbackWindow").html("<div class=\"alert alert-danger\">Passwords don't match!</div>");
                    } else {
                        $('#messageFeedbackWindow').html('<div><asset:image src="spinner.gif" /> Changing password... </div>');

                        $.ajax({
                            url: '${createLink(controller:'changePassword', action: 'changeExistingPassword')}',
                            data: {
                                existingPassword: existingPassword,
                                newPassword: newPassword,
                                confirmPassword: confirmPassword,
                                format: 'json',
                                now: new Date().toString()
                            },
                            dataType: 'json',
                            success: function (data, status, xhr) {
                                if (data.status == "SUCCESS") {
                                    $('#messageFeedbackWindow').html("<div class=\"alert alert-success\">" + data.message + "</div>")
                                } else {
                                    $('#messageFeedbackWindow').html("<div class=\"alert alert-danger\">" + data.message + "</div>")
                                }
                            },
                            error: function (xhr, statusText, errorThrown) {
                                console.log("Error: " + statusText + ", Error: " + errorThrown);
                            }
                        })
                    }

                }//end sendNewPasswordRequest()

                function isEmpty(str) {
                    return (!str || str.length === 0);
                }
            </script>

        </sec:ifLoggedIn>
	</body>
</html>
