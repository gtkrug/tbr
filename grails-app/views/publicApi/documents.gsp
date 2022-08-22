<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>

        <script type="text/javascript">
            const PUBLIC_API_FIND_DOCS = "${createLink(controller:"publicApi", action: "findDocs")}"
        </script>
        <asset:javascript src="utility/utility_publicApi_documents.js"/>
        <asset:javascript src="publicApi_documents.js"/>
    </head>

    <body>
        <div class="container pt-4">
            <h2>Public Documents</h2>

            <div id="document-message"></div>

            <table class="table table-bordered table-striped-hack mt-2" id="document-table"></table>
        </div>
    </body>
</html>
