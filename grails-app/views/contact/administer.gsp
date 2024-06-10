<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>

        <script type="text/javascript">
            const ORGANIZATION_VIEW = "${createLink(controller: 'organization', action: 'view')}/"
            const ORGANIZATION_LIST = "${createLink(controller: 'organization', action: 'list')}"

            const CONTACT_ADD = "${createLink(controller: 'contact', action: 'add')}"
            const CONTACT_DELETE = "${createLink(controller: 'contact', action: 'delete')}"
            const CONTACT_GET = "${createLink(controller: 'contact', action: 'get')}"
            const CONTACT_LIST = "${createLink(controller: 'contact', action: 'list')}"
            const CONTACT_TYPES = "${createLink(controller: 'contact', action: 'types')}"
            const CONTACT_UPDATE = "${createLink(controller: 'contact', action: 'update')}"

            const TABLE_INLINE_ITEMS_PER_PAGE = parseInt("${grailsApplication.config.inline_table_default_items_per_page}");
            const TABLE_FULL_PAGE_ITEMS_PER_PAGE = parseInt("${grailsApplication.config.full_page_table_default_items_per_page}");
        </script>
        <asset:javascript src="utility/utility_contact.js"/>
        <asset:javascript src="contact_administer.js"/>
    </head>

    <body>
        <div class="container pt-4">
            <h2>Points of Contact</h2>

            <div id="contact-message"></div>

            <table class="table table-bordered table-striped-hack" id="contact-table"></table>

            <div class="pt-1" id="contact-form"></div>
        </div>
    </body>
</html>
