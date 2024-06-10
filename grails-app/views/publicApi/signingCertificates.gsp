<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>

        <script type="text/javascript">
            const PUBLIC_API_FIND_SIGNING_CERTIFICATES = "${createLink(controller:"publicApi", action: "findSigningCertificates")}"

            const TABLE_INLINE_ITEMS_PER_PAGE = parseInt("${grailsApplication.config.inline_table_default_items_per_page}");
            const TABLE_FULL_PAGE_ITEMS_PER_PAGE = parseInt("${grailsApplication.config.full_page_table_default_items_per_page}");
        </script>
        <asset:javascript src="utility/utility_publicApi_signingCertificates.js"/>
        <asset:javascript src="publicApi_signingCertificates.js"/>
    </head>

    <body>
        <div class="container pt-4">
            <h2>Signing Certificates</h2>

            <div id="certificate-message"></div>

            <table class="table table-bordered table-striped-hack mt-2" id="certificate-table"></table>
        </div>
    </body>
</html>
