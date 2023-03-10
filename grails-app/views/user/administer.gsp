<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>

        <script type="text/javascript">
            const ORGANIZATION_VIEW = "${createLink(controller:'organization', action: 'view')}/"
            const ORGANIZATION_LIST = "${createLink(controller:'organization', action: 'list')}"

            // TODO: Remove this and all related components
            %{--const REGISTRANT_GET = "${createLink(controller:'registrant', action: 'get')}"--}%
            %{--const REGISTRANT_LIST = "${createLink(controller:'registrant', action: 'list')}"--}%
            %{--const REGISTRANT_UPDATE = "${createLink(controller:'registrant', action: 'update')}"--}%
            const USER_GET = "${createLink(controller:'user', action: 'get')}"
            const USER_LIST = "${createLink(controller:'user', action: 'list')}"
            const USER_UPDATE = "${createLink(controller:'user', action: 'update')}"
        </script>
        <asset:javascript src="utility/utility_user.js"/>
        <asset:javascript src="user_administer.js"/>
    </head>

    <body>
        <div class="container pt-4">
            <h2>Users</h2>

%{--            <div id="registrant-message"></div>--}%

            <table class="table table-bordered table-striped-hack" id="user-table"></table>

            <div class="pt-1" id="user-form"></div>

            <div id="user-message"></div>
        </div>
    </body>
</html>
