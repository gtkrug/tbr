<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Signing Certificates</title>
    <script type="text/javascript">
        MAX_DISPLAY = 10;
        let publicSigningCertificates = {};

        $(function() {
            performSearch('ALL', 50);
        });

        function performSearch(queryString, maxResults)  {
            $('#resultsContainer').html('${raw(asset.image(src: 'spinner.gif'))} Searching...');
            if( !queryString || queryString.trim() === '' ){
                $('#resultsContainer').html('<div class="alert alert-warning">Please enter some search text.</div>');
                return;
            }
            MAX_DISPLAY = parseInt(maxResults);

            $.ajax({
                url: '${createLink(controller:'publicApi', action: 'findSigningCertificates')}',
                method: 'GET',
                type: 'GET',
                data: {
                    timestamp: new Date().getTime(),
                    id: queryString,
                    max: maxResults
                },
                dataType: 'json',
                format: 'json',
                success: function(data){
                    publicSigningCertificates = data;
                    setResultsDiv(publicSigningCertificates.length, queryString);
                    renderPublicSigningCertificates(0);
                },
                error: function(){
                    $('#resultsContainer').html('<div class="alert alert-danger">An unexpected error occurred communicating with the server.</div>');
                }
            });
        }

        function renderPublicSigningCertificates(offset)  {
            let html = "";
            if (publicSigningCertificates.length > MAX_DISPLAY)  {
                html += buildPagination(offset, MAX_DISPLAY, publicSigningCertificates.length, 'renderPublicSigningCertificates');
            }
            html += "<table class='table table-condensed table-striped table-bordered'><thead><tr><th style='width: auto;'>URL</th><th style='width: auto;'>Active</th></tr></thead>";
            html += "<tbody>";
            if (publicSigningCertificates.length == 0)  {
                html += '<tr><td colspan="4"><em>There are no signing certificates.</em></td></tr>';
                html += "</tbody></table>";
            }  else {
                let i = 0;

                publicSigningCertificates.forEach(c => {
                    if(i >= offset && i < MAX_DISPLAY+offset)   {

                        html += "<tr>";

                        html += "<td><a href='"+c.url +"'>" + c.url + "</span></a></td>";

                        if (c.active) {
                            html += "<td><span style='color: darkgreen;' class='glyphicon glyphicon-ok-sign' title='Certificate is active.'></span></td>";
                        } else  {
                            html += "<td><span style='color: darkred;' class='glyphicon glyphicon-remove-sign' title='Certificate is not active.'></span></td>";
                        }

                        html += "</tr>";
                    }
                    ++i;
                });
                html += "</tbody></table>";
            }
            $('#resultsContainer').html(html);
        }

        function setResultsDiv(count, qstr)  {
            $('.pageSubsection').html('Total of '+ count +' signing certificates');
        }

    </script>
</head>
<body>
<h1>Signing Certificates</h1>
<div class="pageSubsection"></div>

<div class="pageContent">
    <div id="resultsContainer"></div>
</div>
</body>
</html>