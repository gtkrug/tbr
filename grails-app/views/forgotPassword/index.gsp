<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>

        <asset:javascript src="forgotPassword_index.js"/>
        <script type="text/javascript">
            initialize("${createLink(controller: "forgotPassword", action: "resetPassword")}")
        </script>
    </head>

    <body>
        <div class="container container-narrow pt-4">
            <div class="border rounded card" autocomplete="off">
                <div class="card-header fw-bold">
                    <div class="row">
                        <div class="col-12">
                            <div>Reset Password</div>
                        </div>
                    </div>
                </div>

                <sec:ifNotLoggedIn>
                    <div class="card-body">
                        <div class="row pb-2">
                            <label class="col-3 col-form-label text-end" for="email">Username</label>

                            <div class="col-9">
                                <input type="text" id="email" name="email" class="form-control"/>
                            </div>
                        </div>
                    </div>

                    <div class="card-footer">
                        <div class="row">
                            <div class="col-3"></div>

                            <div class="col-9 text-start">
                                <a id="button" class="btn btn-primary">Reset Password</a>
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

        <div class="container container-narrow pt-4" id="message"></div>
    </body>
</html>
