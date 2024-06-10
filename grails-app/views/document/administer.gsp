<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>

        <script type="text/javascript">
            const ORGANIZATION_VIEW = "${createLink(controller: 'organization', action: 'view')}/"

            const DOCUMENT_ADD = "${createLink(controller: 'document', action: 'add')}"
            const DOCUMENT_DELETE = "${createLink(controller: 'document', action: 'delete')}"
            const DOCUMENT_GET = "${createLink(controller: 'document', action: 'get')}"
            const DOCUMENT_LIST = "${createLink(controller: 'document', action: 'list')}"
            const DOCUMENT_UPDATE = "${createLink(controller: 'document', action: 'update')}"

            const MOXIE_SWF = "${asset.assetPath([src: '/javascripts/plupload-2.1.1/js/Moxie.swf'])}"
            const MOXIE_XAP = "${asset.assetPath([src: '/javascripts/plupload-2.1.1/js/Moxie.xap'])}"

            const CONTEXT = "${context}"

            const TABLE_INLINE_ITEMS_PER_PAGE = parseInt("${grailsApplication.config.inline_table_default_items_per_page}");
            const TABLE_FULL_PAGE_ITEMS_PER_PAGE = parseInt("${grailsApplication.config.full_page_table_default_items_per_page}");
        </script>
        <asset:javascript src="utility/utility_document.js"/>
        <asset:javascript src="document_administer.js"/>
    </head>

    <body>
        <div class="container pt-4">
            <h2>Documents</h2>

            <div id="document-message"></div>

            <table class="table table-bordered table-striped-hack" id="document-table"></table>

            <div class="pt-1" id="document-form"></div>
        </div>
    </body>
</html>
