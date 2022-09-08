<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>

        <asset:javascript src="changePassword_index.js"/>
        <script type="text/javascript">
            initialize(
                "${createLink(controller:'changePassword', action: 'changePassword')}",
                "${token}")
        </script>
    </head>

    <body>
        <div class="container container-narrow pt-4">
            <div class="border rounded card">
                <div class="card-header fw-bold">
                    <div class="row">
                        <div class="col-12">
                            <div>Change Password</div>
                        </div>
                    </div>
                </div>
                <sec:ifNotLoggedIn>
                    <div class="card-body">
                        <div class="row pb-2">
                            <label class="col-5 col-form-label text-end" for="newPassword">New Password</label>

                            <div class="col-7">
                                <input type="password" id="newPassword" name="newPassword" class="form-control"/>
                            </div>
                        </div>

                        <div class="row pb-2">
                            <label class="col-5 col-form-label text-end" for="confirmPassword">Confirm New Password</label>

                            <div class="col-7">
                                <input type="password" id="confirmPassword" name="confirmPassword" class="form-control"/>
                            </div>
                        </div>
                    </div>

                    <div class="card-footer text-start">
                        <div class="row">
                            <div class="col-5"></div>

                            <div class="col-7">
                                <a id="button" class="btn btn-primary">Change Password</a>
                            </div>
                        </div>
                    </div>
                </sec:ifNotLoggedIn>

                <sec:ifLoggedIn>
                    <div class="card-body">
                        <div class="row pb-2">
                            <div class="col-12">
                                You are already logged in, please visit the "Profile" page and reset your password.
                            </div>
                        </div>
                    </div>
                </sec:ifLoggedIn>
            </div>
        </div>

        <div class="container container-narrow pt-4 d-none" id="message"></div>
    </body>
</html>
