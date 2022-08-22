<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>

        <script type="text/javascript">
            const ORGANIZATION_VIEW = "${createLink(controller:'organization', action: 'view')}/"

            const SIGNING_CERTIFICATES_ADD = "${createLink(controller:'signingCertificates', action: 'add')}"
            const SIGNING_CERTIFICATES_DELETE = "${createLink(controller:'signingCertificates', action: 'delete')}"
            const SIGNING_CERTIFICATES_LIST = "${createLink(controller:'signingCertificates', action: 'list')}"
            const SIGNING_CERTIFICATES_SET_DEFAULT_CERTIFICATE = "${createLink(controller:'signingCertificates', action: 'setDefaultCertificate')}"
            const SIGNING_CERTIFICATES_UPDATE = "${createLink(controller:'signingCertificates', action: 'update')}"
            const SIGNING_CERTIFICATES_VIEW = "${createLink(controller:'signingCertificates', action: 'view')}"

            const VALID_PERIOD_FROM_LIST = ${certificateValidPeriodIntervalList}
            const KEY_LENGTH_FROM_LIST = ${keyLengthList}
        </script>
        <asset:javascript src="utility/utility_signingCertificates.js"/>
        <asset:javascript src="signingCertificates_administer.js"/>
    </head>

    <body>
        <div class="container pt-4">
            <h2>Signing Certificates</h2>

            <div id="signing-certificate-message"></div>

            <table class="table table-bordered table-striped-hack" id="signing-certificate-table"></table>

            <div class="pt-1" id="signing-certificate-form"></div>
        </div>
    </body>
</html>
