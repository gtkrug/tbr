<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>

        <script type="text/javascript">
            const ORGANIZATION_VIEW = "${createLink(controller:'organization', action: 'view')}/"
            const ORGANIZATION_LIST = "${createLink(controller:'organization', action: 'list')}"

            const FORGOT_PASSWORD_URL = "${createLink(controller:'forgotPassword', action: 'index', absolute: true)}/"

            const REGISTRANT_ACTIVATE = "${createLink(controller:'registrant', action: 'activate')}"
            const REGISTRANT_ADD = "${createLink(controller:'registrant', action: 'add')}"
            const REGISTRANT_DEACTIVATE = "${createLink(controller:'registrant', action: 'deactivate')}"
            const REGISTRANT_DELETE = "${createLink(controller:'registrant', action: 'delete')}"
            const REGISTRANT_GET = "${createLink(controller:'registrant', action: 'get')}"
            const REGISTRANT_LIST = "${createLink(controller:'registrant', action: 'list')}"
            const REGISTRANT_ROLES = "${createLink(controller:'registrant', action: 'roles')}"
            const REGISTRANT_UPDATE = "${createLink(controller:'registrant', action: 'update')}"
        </script>
        <asset:javascript src="utility/utility_registrant.js"/>
        <asset:javascript src="registrant_administer.js"/>
    </head>

    <body>
        <div class="container pt-4">
            <h2>Registrants</h2>

            <div id="registrant-message"></div>

            <table class="table table-bordered table-striped-hack" id="registrant-table"></table>

            <div class="pt-1" id="registrant-form"></div>
        </div>
    </body>
</html>
