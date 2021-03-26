<%@ page import="tm.binding.registry.SigningCertificateStatus; tm.binding.registry.SigningCertificate" contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>View Signing Certificate</title>

    <style type="text/css">
    .customWidth {
        white-space: normal;
        word-wrap: break-word;
        width: 480px;
    }
    .tab {
        display:inline-block;
        margin-left: 40px;
    }
    </style>
</head>

<body>

<div class="row">
    <div class="col-md-9">
        <h1>View Signing Certificate</h1>
    </div>
    <div class="col-md-3" style="text-align: right;">

    </div>
</div>

<div id="errorContainer">
    <g:if test="${flash.error}">
        <div class="alert alert-danger">${flash.error}</div>
    </g:if>
</div>

<div class="pageContent">

    <!-- Signing Certificate Info -->
    <div class="row" style="margin-top: 2em;">
        <div class="col-sm-12">
            <h4>Signing Certificate Information</h4>
            <table class="infoTable table table-striped table-bordered table-condensed">
                <tr>
                    <th style="width:15%">Version</th>
                    <td style="width:85%">${version}</td>
                </tr>

                <tr>
                    <th style="width:15%">Serial Number</th>
                    <td style="width:85%">${serialNumber}</td>
                </tr>

                <tr>
                    <th style="width:15%">Signature Algorithm</th>
                    <td style="width:85%">${signatureAlgorithm}</td>
                </tr>

                <tr>
                    <th style="width:15%">Issuer</th>
                    <td style="width:85%">${issuer}</td>
                </tr>

                <tr>
                    <th style="width:15%">Valid Not Before</th>
                    <td style="width:85%">${notBefore}</td>
                </tr>

                <tr>
                    <th style="width:15%">Valid Not After</th>
                    <td style="width:85%">${notAfter}</td>
                </tr>

                <tr>
                    <th style="width:15%">Subject</th>
                    <td style="width:85%">${subject}</td>
                </tr>

                <tr>
                    <th style="width:15%">Public Key Algorithm</th>
                    <td style="width:85%">${publicKeyAlgorithm}</td>
                </tr>

                <tr>
                    <th style="width:15%">Email Address</th>
                    <td style="width:85%">${cert.emailAddress}</td>
                </tr>
                <tr>
                    <th style="width:15%">Thumbprint</th>
                    <td style="width:85%;white-space: pre">${cert.thumbPrintWithColons}</td>
                </tr>

                <tr>
                    <th style="width:15%">Key Usage</th>
                    <td style="width:85%">${keyUsageString}</td>
                </tr>

                <tr>
                    <th style="width:15%">URL</th>
                    <td style="width:85%">${cert.certificatePublicUrl}</td>
                </tr>
            </table>

            <h4 style="margin-top: 3em;">Certificate Status</h4>
            <table class="infoTable table table-striped table-bordered table-condensed">
                <tr>
                    <th style="width:15%">Status</th>
                    <td style="width:85%">
                        ${cert.status}
                        <g:if test="${cert.status == tm.binding.registry.SigningCertificateStatus.ACTIVE}">
                            <span style="color: darkgreen;" class="glyphicon glyphicon-ok-sign" title="Certificate still valid"></span>
                        </g:if>
                        <g:if test="${cert.status == tm.binding.registry.SigningCertificateStatus.REVOKED}">
                            <span style="color: darkred;" class="glyphicon glyphicon-remove-sign" title="Certificate has been revoked."></span>
                        </g:if>
                        <g:if test="${cert.status == tm.binding.registry.SigningCertificateStatus.EXPIRED}">
                            <span style="color: rgb(150, 150, 0);" class="glyphicon glyphicon-minus-sign" title="Certificate has expired."></span>
                        </g:if>
                    </td>
                </tr>
                <g:if test="${cert.status != tm.binding.registry.SigningCertificateStatus.ACTIVE}">
                    <tr>
                        <th style="width:15%">Revoked Timestamp</th>
                        <td style="width:85%"><g:formatDate date="${cert.revokedTimestamp}" format="yyyy-MM-dd" /></td>
                    </tr>
                    <tr>
                        <th style="width:15%">Revoked By</th>
                        <td style="width:85%">${cert.revokingUser?.username}</td>
%{--                        <td style="width:85%">${cert.revokingUser?.contactInformation?.responder}</td>--}%
                    </tr>
                    <tr>
                        <th style="width:15%">Revoked Reason</th>
                        <td style="width:85%">${cert.revokedReason}</td>
                    </tr>
                </g:if>
            </table>

        </div>
    </div>

    <hr />

    <g:if test="${cert.status == tm.binding.registry.SigningCertificateStatus.ACTIVE}">
        <div style="margin-top: 2em; margin-bottom: 3em;">
            <a href="javascript:revoke()" class="btn btn-danger">Revoke</a>
        </div>
    </g:if>
</div>

<script type="text/javascript">

    var queryInProgress = false;

    var SELECTED_ORG = null;

    $(document).ready(function(){

    })

    function revoke() {
        var url = '${createLink(controller:'signingCertificates', action: 'revoke', id: cert.id)}';

        var reason = prompt("What is the reason you are revoking this certificate?");
        if (reason) {
            window.location.href = url + "?reason=" + encodeURI(reason);
        } else {
            alert("A reason is required.");
        }
    }

</script>

</body>
</html>
