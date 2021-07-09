<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="layout" content="main"/>
    <title>Signing Certificates</title>
    <script type="text/javascript">
        $(document).ready(function(){

            listSigningCertificates([]);
        });

        let renderSigningCertificatesForm = function(target, fn, org)  {
            let html  = "";

            html += "<div class='form-group'>";
            html += "<label for='commonName' class='col-sm-2 control-label tm-margin'>Common Name</label>";
            html += "<input id='commonName' type='text' class='col-sm-10 form-control tm-margin' style='width: 70%;' placeholder='Enter Signing Certificate Organization Id'/><span style='color:red;'>*</span><br>";
            html += "</div>";

            html += "<div class='form-group'>";
            html += "<label for='localityName' class='col-sm-2 control-label tm-margin'>Locality Name</label>";
            html += "<input id='localityName' type='text' class='col-sm-10 form-control tm-margin' style='width: 70%;' placeholder='Enter Signing Certificate Locality Name'/><span style='color:red;'>*</span><br>";
            html += "</div>";

            html += "<div class='form-group'>";
            html += "<label for='stateName' class='col-sm-2 control-label tm-margin'>State Or Province Name</label>";
            html += "<input id='stateName' type='text' class='col-sm-10 form-control tm-margin' style='width: 70%;' placeholder='Enter Signing Certificate State Name'/><span style='color:red;'>*</span><br>";
            html += "</div>";

            html += "<div class='form-group'>";
            html += "<label for='countryName' class='col-sm-2 control-label tm-margin'>Country Name</label>";
            html += "<input id='countryName' type='text' class='col-sm-10 form-control tm-margin' style='width: 70%;' placeholder='Enter Signing Certificate Country Name'/><span style='color:red;'>*</span><br>";
            html += "</div>";

            html += "<div class='form-group'>";
            html += "<label for='emailAddress' class='col-sm-2 control-label tm-margin'>Email Address</label>";
            html += "<input id='emailAddress' type='text' class='col-sm-10 form-control tm-margin' style='width: 70%;' placeholder='Enter Signing Certificate Email Address'/><span style='color:red;'>*</span><br>";
            html += "</div>";

            html += "<div class='form-group'>";
            html += "<label for='organizationName' class='col-sm-2 control-label tm-margin'>Organization Name</label>";
            html += "<input id='organizationName' type='text' class='col-sm-10 form-control tm-margin' style='width: 70%;' placeholder='Enter Signing Certificate Organization Name'/><span style='color:red;'>*</span><br>";
            html += "</div>";

            html += "<div class='form-group'>";
            html += "<label for='organizationUnitName' class='col-sm-2 control-label tm-margin'>Organizational Unit Name</label>";
            html += "<input id='organizationUnitName' type='text' class='col-sm-10 form-control tm-margin' style='width: 70%;' placeholder='Enter Signing Certificate Organization Unit Name'/><span style='color:red;'>*</span><br>";
            html += "</div>";


            // Valid Period drop-down
            html += "<input type='hidden' id='validPeriod' name='validPeriod' value='${certificateValidPeriodIntervalList[0] ?: ''}' />";
            html += "<div class='form-group'>";
            html += "<label for='validPeriod' class='col-sm-2 control-label'>Period of Validity (years)</label>";
            html += "<div class='col-sm-10'>";
            html += "<div class='btn-group' style='margin: 0;'>";
            html += "<button type='button' class='btn btn-default dropdown-toggle' data-toggle='dropdown' aria-expanded='false'>";
            html += "<span id='selectedValidPeriodTitle'><em>Select Period of Validity...</em></span> <span class='caret'></span>";
            html += "</button>";
            html += "<ul class='dropdown-menu' role='menu'>";
            html += "<g:each in='${certificateValidPeriodIntervalList}' var='validPeriodFromList'>";
            html += "<li>";
            html += "<a href=javascript:setValidPeriod('${validPeriodFromList}')>";
            html += "<div style='font-weight: bold;'>${validPeriodFromList}</div>";
            html += "</a>";
            html += "</li>";
            html += "</g:each>";
            html += "</ul>";
            html += "</div>";
            html += "</div>";
            html += "</div>";

            // Key length drop-down
            html += "<input type='hidden' id='keyLength' name='keyLength' value='${keyLengthList[0] ?: ''}' />";
            html += "<div class='form-group'>";
            html += "<label for='keyLength' class='col-sm-2 control-label'>Key Length (bits)</label>";
            html += "<div class='col-sm-10'>";
            html += "<div class='btn-group' style='margin: 0;'>";
            html += "<button type='button' class='btn btn-default dropdown-toggle' data-toggle='dropdown' aria-expanded='false'>";
            html += "<span id='selectedKeyLengthTitle'><em>Select Key Length...</em></span> <span class='caret'></span>";
            html += "</button>";
            html += "<ul class='dropdown-menu' role='menu'>";
            html += "<g:each in='${keyLengthList}' var='keyLengthFromList'>";
            html += "<li>";
            html += "<a href=javascript:setKeyLength('${keyLengthFromList}')>";
            html += "<div style='font-weight: bold;'>${keyLengthFromList}</div>";
            html += "</a>";
            html += "</li>";
            html += "</g:each>";
            html += "</ul>";
            html += "</div>";
            html += "</div>";
            html += "</div>";

            html += "<div class='form-group'>";
            html += "<div class='col-sm-offset-2 col-sm-10'>";
            html += "<button id='certOk' type='button' class='btn btn-info tm-margin'>Add</button>";
            html += "</div>";
            html += "</div>";

            renderDialogForm(target, html);

            document.getElementById('commonName').focus();
            document.getElementById('certOk').onclick = fn;
            if(org.id !== 0)  {
                document.getElementById('commonName').value = org.commonName;
                document.getElementById('localityName').value = org.localityName;
                document.getElementById('stateName').value = org.stateName;
                document.getElementById('countryName').value = org.countryName;
                document.getElementById('emailAddress').value = org.emailAddress;
                document.getElementById('organizationName').value = org.organizationName;
                document.getElementById('organizationUnitName').value = org.organizationUnitName;
            }
            document.getElementById('commonName').focus();
        }

        let signingCertificatesDetail = curryThree(renderSigningCertificatesForm);

        let listSigningCertificates = function(data)  {
            console.log("List Signing Certificates administer view");

            list("${createLink(controller:'signingCertificates', action: 'list')}"
                , renderResults
                , {name: 'ALL'});
        }

        let renderSigningCertificateOffset = function(){};
        /**
         * renders a table of signing certificates
         *
         * @param target
         * @param obj
         * @param data
         * @param offset
         */
        let renderSigningCertificates = function(target, obj, data, offset)  {
            let html = renderPagination(offset, data.length, 'renderSigningCertificateOffset');
            html += "<table class='table table-condensed table-striped table-bordered'>";
            html += "<tr><td colspan='5' style='text-align: center'>";
            if(obj.editable)  {
                html += "<div class='tm-left'><a id='plus-"+target+"' title='Add a Signing Certificate'><span class='glyphicon glyphicon-plus'></span></a></div>";
            }
            html += "<b>"+obj.title+"</b></td></tr>"
            if (data.length === 0)  {
                html += '<tr><td colspan="5"><em>There are no signing certificates.</em></td></tr>';
            }  else {
                // Table header
                html += "<th>Active</th>";
                html += "<th>Distinguished Name</th>";
                html += "<th>Email Address</th>";
                html += "<th>URL</th>";
                html += "<th>Status</th>";

                let idx = 0;
                data.forEach(o => {
                    if(idx >= offset && idx < offset+MAX_DISPLAY) {
                        html += obj.fnDraw(obj, o);
                    }
                    ++idx;
                });
            }
            html += "</table>";
            document.getElementById(target).innerHTML = html;
            if(obj.editable)  {
                document.getElementById('plus-'+target).onclick = obj.fnAdd;
            }
        }

        let drawSigningCertificates = function(obj, entry)  {
            // console.log("entry: " + JSON.stringify(entry));

            let html = "<tr>";

            if (entry.status == "ACTIVE") {
                if (entry.defaultCertificate === true) {
                    html += "<td><input type='radio' name='activeCertificateRadioGroup' onclick='setDefaultCertificate(" + entry.id + "," + entry.defaultCertificate + ");' checked value='" + entry.id + "'></td>";
                } else {
                    html += "<td><input type='radio' name='activeCertificateRadioGroup' onclick='setDefaultCertificate(" + entry.id + "," + entry.defaultCertificate + ");' value='" + entry.id + "'></td>";
                }
            } else {
                html += "<td></td>";
            }

            // distinguished name
            var href = '${createLink(controller:'signingCertificates', action: 'view')}?id='+entry.id;
            var title = "View " + entry.distinguishedName;
            var link = "<td><a href=" + href + " title=" + title + "><span>" + entry.distinguishedName + "</span></a></td>";
            html += link;

            // email address
            html += "<td>" + entry.emailAddress + "</td>";

            // url
            href = entry.url;
            title = "Download " + entry.distinguishedName;
            link = "<td><a href=" + href + " title=" + title + "><span>" + entry.url + "</span></a></td>";
            html += link;

            // status
            if (entry.status == "ACTIVE") {
                html += "<td><span style='color: darkgreen;' class='glyphicon glyphicon-ok-sign' title='Certificate still valid.'></span></td>";
            } else if (entry.status == "REVOKED") {
                html += "<td><span style='color: darkred;' class='glyphicon glyphicon-remove-sign' title='Certificate has been revoked.'></span>"
                "</td>";
            } else if (entry.status == "EXPIRED") {
                html += "<td><span style='color: rgb(150, 150, 0);' class='glyphicon glyphicon-minus-sign' title='Certificate has expired.'></span></td>";
            }

            html += "</tr>";

            return html;
        }

        let setDefaultCertificate = function(id, defaultCertificate) {

            if (!defaultCertificate) {

                add("${createLink(controller:'signingCertificates', action: 'setDefaultCertificate')}"
                    , listSigningCertificates
                    , {
                        id: id
                    }
                );

                setSuccessStatus("<b>The active signing certificate has been changed.</b>");
            } else {
                resetStatus();
            }
        }

        let curriedSigningCertificate = curryFour(renderSigningCertificates);

        let renderResults = function(results)  {
            renderSigningCertificateOffset = curriedSigningCertificate('signingCertificates-table')
            ({
                editable: true
                , fnAdd: function(){renderSigningCertificatesForm('signingCertificate'
                    , function(){addSigningCertificate(
                          document.getElementById('commonName').value
                        , document.getElementById('localityName').value
                        , document.getElementById('stateName').value
                        , document.getElementById('countryName').value
                        , document.getElementById('emailAddress').value
                        , document.getElementById('organizationName').value
                        , document.getElementById('organizationUnitName').value
                        , document.getElementById('validPeriod').value
                        , document.getElementById('keyLength').value)}
                    , {id:0})}
                , fnRemove: removeSigningCertificate
                , fnDraw: drawSigningCertificates
                , title: 'Signing Certificates'
                , hRef: 'javascript:getDetails'
            })
            (results);
            renderSigningCertificateOffset(0);
        }

        let getDetails = function(id)  {
        }

        let checkSigningCertificate = function(commonName, localityName, stateName, countryName, emailAddress,
                                               organizationName, organizationUnitName, validPeriod, keyLength) {
            if (commonName == null || commonName.length === 0) {
                setDangerStatus("<b>Common name cannot be blank.</b>");
                document.getElementById('commonName').focus();
                return false;
            }
            if (localityName == null || localityName.length === 0) {
                setDangerStatus("<b>Locality name cannot be blank.</b>");
                document.getElementById('localityName').focus();
                return false;
            }
            if (stateName == null || stateName.length === 0) {
                setDangerStatus("<b>State name cannot be blank.</b>");
                document.getElementById('stateName').focus();
                return false;
            }
            if (countryName == null || countryName.length === 0) {
                setDangerStatus("<b>Country name cannot be blank.</b>");
                document.getElementById('countryName').focus();
                return false;
            }
            if (emailAddress == null || emailAddress.length === 0) {
                setDangerStatus("<b>Email address cannot be blank.</b>");
                document.getElementById('emailAddress').focus();
                return false;
            }
            if (organizationName == null || organizationName.length === 0) {
                setDangerStatus("<b>Organization name cannot be blank.</b>");
                document.getElementById('organizationName').focus();
                return false;
            }
            if (organizationUnitName == null || organizationUnitName.length === 0) {
                setDangerStatus("<b>Organization unit name cannot be blank.</b>");
                document.getElementById('organizationUnitName').focus();
                return false;
            }
            if (validPeriod == null || validPeriod.length === 0) {
                setDangerStatus("<b>Valid period cannot be blank.</b>");
                document.getElementById('validPeriod').focus();
                return false;
            }
            if (keyLength == null || keyLength.length === 0) {
                setDangerStatus("<b>Key length cannot be blank.</b>");
                document.getElementById('keyLength').focus();
                return false;
            }
            return true;
        }

        let addSigningCertificate = function(commonName, localityName, stateName, countryName, emailAddress,
                                             organizationName, organizationUnitName, validPeriod, keyLength)  {
            if(checkSigningCertificate(commonName, localityName, stateName, countryName, emailAddress,
                organizationName, organizationUnitName, validPeriod, keyLength)) {
                add("${createLink(controller:'signingCertificates', action: 'add')}"
                    , listSigningCertificates
                    , { commonName: commonName
                        , localityName: localityName
                        , stateName: stateName
                        , countryName: countryName
                        , emailAddress: emailAddress
                        , organizationName: organizationName
                        , organizationUnitName: organizationUnitName
                        , validPeriod: validPeriod
                        , keyLength: keyLength
                    }
                );
                clearForm();
            } else {
                scroll(0,0);
            }
        }

        // TODO: probably remove since it might never be used
        let updateSigningCertificate =  function(id, commonName, localityName, stateName, countryName, emailAddress,
                                                 organizationName, organizationUnitName, validPeriod, keyLength)  {
            if(checkSigningCertificate(commonName, localityName, stateName, countryName, emailAddress,
                organizationName, organizationUnitName, validPeriod, keyLength))  {
                update("${createLink(controller:'signingCertificates', action: 'update')}"
                    , listSigningCertificates
                    , {
                        id: id
                        , commonName: commonName
                        , localityName: localityName
                        , stateName: stateName
                        , countryName: countryName
                        , emailAddress: emailAddress
                        , organizationName: organizationName
                        , organizationUnitName: organizationUnitName
                        , validPeriod: validPeriod
                        , keyLength: keyLength
                    }
                );
                clearForm();
            } else {
                scroll(0,0);
            }
        }

        // TODO: probably remove since it might never be used
        let removeSigningCertificate = function()  {
            getCheckedIds('edit-signingCertificates', function(list)  {
                update("${createLink(controller:'signingCertificates', action: 'delete')}"
                    , listSigningCertificates
                    , { ids: list }
                );
            });
        }

        let clearForm = function()  {
            setSuccessStatus("<b>Successfully added signing certificate.</b>");

            document.getElementById('commonName').value = "";
            document.getElementById('localityName').value = "";
            document.getElementById('stateName').value = "";
            document.getElementById('countryName').value = "";
            document.getElementById('emailAddress').value = "";
            document.getElementById('organizationName').value = "";
            document.getElementById('organizationUnitName').value = "";

            hideIt('signingCertificate');

            scroll(0,0);
        }

        function setValidPeriod(validPeriod) {
            $('#selectedValidPeriodTitle').html(validPeriod);
            $('#validPeriod').val(validPeriod);
        }

        function setKeyLength(keyLength) {
            $('#selectedKeyLengthTitle').html(keyLength);
            $('#keyLength').val(keyLength);
        }
    </script>
</head>
<body>
<div id="status-header"></div>
<div id="signingCertificates-table"></div>
<div id="signingCertificate"></div>
</body>
</html>