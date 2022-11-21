<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>

        <asset:javascript src="login_auth.js"/>

        <style type="text/css">
            .disabled-link {
                pointer-events: none;
                color: #c0c0c0;
            }
        </style>
    </head>

    <body>
        <form action="${postUrl ?: "/login/authenticate"}" method="POST" autocomplete="off" role="form">
            <div class="container container-narrow pt-4">
                <div class="border rounded card">
                    <div class="card-header fw-bold">
                        <div class="row">
                            <div class="col-12">
                                <div>Login</div>
                            </div>
                        </div>
                    </div>

                    <div class="card-body">
                        <div class="row pb-2">
                            <label class="col-3 col-form-label text-end" for="username">Username</label>

                            <div class="col-9">
                                <input type="text" id="username" name="username" class="form-control"/>
                            </div>
                        </div>

                        <div class="row pb-2">
                            <label class="col-3 col-form-label text-end" for="password">Password</label>

                            <div class="col-9">
                                <input type="password" id="password" name="password" class="form-control">
                            </div>
                        </div>
                    </div>

                    <div class="card-footer">
                        <div class="row">
                            <div class="col-3"></div>

                            <div class="col-2 text-start">
                                <input type="submit" class="btn btn-primary" value="Login">
                            </div>

                            <div class="col-7 d-flex justify-content-end align-items-center">
                                <g:isMailEnabled>
                                    <a href="${createLink(controller: "forgotPassword")}">Reset Password</a>
                                </g:isMailEnabled>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <g:if test="${flash.message}">
                <div class="container container-narrow pt-4">
                    <div class="alert alert-danger text-center">
                        ${flash.message}
                    </div>
                </div>
            </g:if>
        </form>
    </body>
</html>
