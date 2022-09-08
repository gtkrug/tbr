<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>

        <script type="text/javascript">
            const ORGANIZATION_VIEW = "${createLink(controller:'organization', action: 'view')}/"
            const SIGNING_CERTIFICATES_REVOKE = "${createLink(controller:'signingCertificates', action: 'revoke', id: cert.id)}"
        </script>
        <asset:javascript src="signingCertificates_view.js"/>
    </head>

    <body>
        <div class="container pt-4">
            <h2>View Signing Certificate</h2>

            <div id="errorContainer" class="mt-2">
                <g:if test="${flash.error}">
                    <div class="alert alert-danger">${flash.error}</div>
                </g:if>
            </div>

            <div class="border rounded card">

                <div class="card-header fw-bold">
                    <div class="row">
                        <div class="col-12">Signing Certificate Information</div>
                    </div>
                </div>

                <div class="card-body">
                    <div class="row pb-2">
                        <label class="col-2 col-form-label">Version</label>

                        <div class="col-10">
                            <input type="text" class="form-control" value="${version}" readonly>
                        </div>
                    </div>

                    <div class="row pb-2">
                        <label class="col-2 col-form-label">Serial Number</label>

                        <div class="col-10">
                            <input type="text" class="form-control" value="${serialNumber}" readonly>
                        </div>
                    </div>

                    <div class="row pb-2">
                        <label class="col-2 col-form-label">Signature Algorithm</label>

                        <div class="col-10">
                            <input type="text" class="form-control" value="${signatureAlgorithm}" readonly>
                        </div>
                    </div>

                    <div class="row pb-2">
                        <label class="col-2 col-form-label">Issuer</label>

                        <div class="col-10">
                            <input type="text" class="form-control" value="${issuer}" readonly>
                        </div>
                    </div>

                    <div class="row pb-2">
                        <label class="col-2 col-form-label">Valid Not Before</label>

                        <div class="col-10">
                            <input type="text" class="form-control" value="${notBefore}" readonly>
                        </div>
                    </div>

                    <div class="row pb-2">
                        <label class="col-2 col-form-label">Valid Not After</label>

                        <div class="col-10">
                            <input type="text" class="form-control" value="${notAfter}" readonly>
                        </div>
                    </div>

                    <div class="row pb-2">
                        <label class="col-2 col-form-label">Subject</label>

                        <div class="col-10">
                            <input type="text" class="form-control" value="${subject}" readonly>
                        </div>
                    </div>

                    <div class="row pb-2">
                        <label class="col-2 col-form-label">Public Key Algorithm</label>

                        <div class="col-10">
                            <input type="text" class="form-control" value="${publicKeyAlgorithm}" readonly>
                        </div>
                    </div>

                    <div class="row pb-2">
                        <label class="col-2 col-form-label">Email Address</label>

                        <div class="col-10">
                            <input type="text" class="form-control" value="${cert.emailAddress}" readonly>
                        </div>
                    </div>

                    <div class="row pb-2">
                        <label class="col-2 col-form-label">Thumbprint</label>

                        <div class="col-10">
                            <input type="text" class="form-control" value="${cert.thumbPrintWithColons}" readonly>
                        </div>
                    </div>

                    <div class="row pb-2">
                        <label class="col-2 col-form-label">Key Usage</label>

                        <div class="col-10">
                            <input type="text" class="form-control" value="${keyUsageString}" readonly>
                        </div>
                    </div>

                    <div class="row pb-2">
                        <label class="col-2 col-form-label">URL</label>

                        <div class="col-10">
                            <input type="text" class="form-control" value="${cert.certificatePublicUrl}" readonly>
                        </div>
                    </div>
                </div>

                <div class="card-header fw-bold">
                    <div class="row">
                        <div class="col-12">Certificate Status</div>
                    </div>
                </div>

                <div class="card-body">
                    <div class="row pb-2">
                        <label class="col-2 col-form-label">Status</label>

                        <div class="col-10">
                            <div class="input-group">
                                <span class="input-group-text">
                                    <g:if test="${cert.status == tm.binding.registry.SigningCertificateStatus.ACTIVE}">
                                        <span class="bi bi-check-circle-fill text-success" title="Certificate still valid"></span>
                                    </g:if>
                                    <g:if test="${cert.status == tm.binding.registry.SigningCertificateStatus.REVOKED}">
                                        <span class="bi bi-x-circle-fill text-danger" title="Certificate has been revoked."></span>
                                    </g:if>
                                    <g:if test="${cert.status == tm.binding.registry.SigningCertificateStatus.EXPIRED}">
                                        <span class="bi bi-dash-circle-fill text-warning" title="Certificate has expired."></span>
                                    </g:if>
                                </span>
                                <input type="text" class="form-control" value="${cert.status}" readonly>
                            </div>

                        </div>
                    </div>

                    <g:if test="${cert.status != tm.binding.registry.SigningCertificateStatus.ACTIVE}">
                        <div class="row pb-2">
                            <label class="col-2 col-form-label">Revoked Timestamp</label>

                            <div class="col-10">
                                <input type="text" class="form-control" value="<g:formatDate date="${cert.revokedTimestamp}" format="yyyy-MM-dd"/>" readonly>
                            </div>
                        </div>

                        <div class="row pb-2">
                            <label class="col-2 col-form-label">Revoked By</label>

                            <div class="col-10">
                                <input type="text" class="form-control" value="${cert.revokingUser?.username}" readonly>
                            </div>
                        </div>

                        <div class="row pb-2">
                            <label class="col-2 col-form-label">Revoked Reason</label>

                            <div class="col-10">
                                <input type="text" class="form-control" value="${cert.revokedReason}" readonly>
                            </div>
                        </div>
                    </g:if>
                </div>

                <g:if test="${cert.status == tm.binding.registry.SigningCertificateStatus.ACTIVE}">
                    <div class="card-footer text-start">
                        <div class="row">
                            <div class="col-2"></div>

                            <div class="col-10">
                                <a id="button" class="btn btn-danger">Revoke</a>
                            </div>
                        </div>
                    </div>
                </g:if>
            </div>
        </div>
    </body>
</html>
