<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <style type="text/css">
    .certificate {
        font-family: monospace;
        font-size: 1em;
    }
    </style>
<title>Certificate</title>
</head>
<body class="certificate">
-----BEGIN CERTIFICATE-----<br>
${provider.signingCertificate}
<br>-----END CERTIFICATE-----
</body>
</html>