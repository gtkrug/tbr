<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="layout" content="main"/>
    <title>Documents</title>
    <script type="text/javascript">
        $(document).ready(function(){

            listDocuments([]);
        });

        let listDocuments = function(data)  {
            list("${createLink(controller:'document', action: 'list')}"
                , renderResults
                , {id: 0});
        }

        let populateDocumentForm = function(doc) {

            if(doc.id === 0)  {
                // selectDocumentTypes('0');
            } else {

                // selectDocumentTypes(doc.type.name)
                document.getElementById('filename').value = doc.filename;
                document.getElementById('description').value = doc.description;
                document.getElementById('publicDocument').checked = doc.publicDocument;
            }
            document.getElementById('filename').focus();
        }

        let getDetails = function(id)  {

            get("${createLink(controller:'document', action: 'get')}"
                , documentDetail('document-details')
                (document.getElementById('edit-document').getAttribute('binaryUrl'))
                (populateDocumentForm)
                (function(){updateDocument(id
                    , document.getElementById('filename').value
                    , document.getElementById('description').value
                    , document.getElementById('binaryId1').value
                    , document.getElementById('publicDocument').checked
                );})
                , { id: id }
            );
        }

        let renderResults = function(results)  {

            renderDocumentOffset = curriedDocument('documents-table')
            ({
                editable: results.editable
                , binaryUrl: results.binaryUrl
                , fnAdd: function(){renderDocumentForm('document-details'
                        , results.binaryUrl
                        , populateDocumentForm
                        , function(){updateDocument(0
                            , document.getElementById('filename').value
                            , document.getElementById('description').value
                            , document.getElementById('binaryId1').value
                            , document.getElementById('publicDocument').checked
                         );}
                        , {id:0});}
                , fnRemove: removeDocument
                , fnDraw: drawDocuments
                , title: 'Documents'
                , hRef: 'javascript:getDetails'
            })
            (results);
            renderDocumentOffset(0);
        }

        let removeDocument = function()  {
            getCheckedIds('edit-documents', function (list) {
                update("${createLink(controller:'document', action: 'delete')}"
                    , listDocuments
                    , {ids: list}
                );
            });
        }

        let updateDocument = function(id, filename, description, binaryId, publicDocument)  {

            if(checkDocument(id, filename, description))  {
                if(id === 0)  {
                    add("${createLink(controller:'document', action: 'add')}"
                        , listDocuments
                        , { filename: filename
                            , description: description
                            , binaryId: binaryId
                            , publicDocument: publicDocument
                        });
                }  else {
                    update("${createLink(controller:'document', action: 'update')}"
                        , listDocuments
                        , {
                            id: id
                            , filename: filename
                            , description: description
                            , binaryId: binaryId
                            , publicDocument: publicDocument
                        });
                }
                clearForm();
            } else {
                scroll(0,0);
            }
        }

        let clearForm = function()  {
            setSuccessStatus("<b>Successfully saved document.</b>");
            hideIt('document-details');
            scroll(0,0);
        }

        /**
         * Called by pluploader template code after file added (because we specified it on the templ inclusion.)
         */
        let setUploadedFilename = function(up, files){
            var firstFilename = files[0].name;
            console.log("Setting display name to file name["+firstFilename+"]...")
            $('#filename').val(firstFilename);
        }//end setUploadedFilename()

        let PLUPLOAD1;
        let LAST_BINARY_ID_1 = -1;

        let createPlupload1 = function(binaryUrl){
            console.log('Initializing plupload [1]...')

            var localUrl = binaryUrl;
            console.log('Relative Binary URL: ' + localUrl)

            var uploader = new plupload.Uploader({
                browse_button: 'fileUploadButton1',
                multi_selection: false,
                chunk_size: '100kb',
                max_retries: 0,
                url: localUrl,
                flash_swf_url : "${asset.assetPath([src: '/javascripts/plupload-2.1.1/js/Moxie.swf'])}",
                // Flash settings
                silverlight_xap_url : "${asset.assetPath([src: '/javascripts/plupload-2.1.1/js/Moxie.xap'])}",
                multipart_params: {
                    format: 'json',
                    context: '${context}'
                }
            });

            PLUPLOAD1 = uploader;
            uploader.init();
            uploader.bind('FilesAdded', handleFilesAdded1);
            uploader.bind('UploadProgress', handleUploadProgress1);
            uploader.bind('Error', handleUploadError1);
            uploader.bind('FileUploaded', handleFileUploaded1);
            uploader.bind('UploadComplete', handleUploadComplete1);
            console.log('Successfully initialized plupload [1]')
        }

        function handleUploadComplete1(up) {
//        console.log("handleUploadComplete[1]: "+JSON.stringify(up, null, "   "));
            console.log("Upload complete, setting hidden field 'binaryId1' to "+LAST_BINARY_ID_1);
            $('#binaryId1').val(LAST_BINARY_ID_1);
        }
        function handleFileUploaded1(up, file, response){
            var responseData = jQuery.parseJSON(response.response);
            console.log("handleFileUploaded1: "+JSON.stringify(file, null, "   ")+"\n\n RESPONSE: \n"+JSON.stringify(responseData, null, "   "));
            $('#fileUploadStatus1').html('');
            LAST_BINARY_ID_1 = responseData.binaryId;
        }
        function handleFilesAdded1(up, files){
            console.log("handleFilesAdded1: "+JSON.stringify(files, null, "   "));
            $('#binaryId1').val(-1);
            $('#fileName1').html(files[0].name)
            $('#fileUploadStatus1').html(
                '<div class="progress" style="width: 400px; height: 5px;">'+
                '<div class="progress-bar" id="fileUploadProgressBar1" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;">'+
                '<span class="sr-only" id="fileUploadProgressText1">0%</span></div></div>'
            )
            LAST_BINARY_ID_1 = -1;

            setUploadedFilename(up, files);

            PLUPLOAD1.start();

        }
        function handleUploadProgress1(up, file){
//        console.log("handleUploadProgress1: "+JSON.stringify(file, null, "   "));
            console.log("PLUPLOAD1 Setting progress bar percentage to: "+file.percent);
            $('#fileUploadProgressBar1').width(file.percent+'%');
            $('#fileUploadProgressBar1').attr('aria-valuenow', file.percent);
            $('#fileUploadProgressText1').html(file.percent+'%');
        }
        function handleUploadError1(up, err) {
            console.log("handleUploadError1: "+JSON.stringify(err, null, "   "));
            var msg = 'Error Uploading['+err.code+']: '+err.message;
            if( err.response ){
                try {
                    var jsonText = err.response;
                    console.log("Attemting to parse JSON: "+jsonText);

                    // TODO: Problem here is that the JSON returned by grails is not valid JSON and will not parse.
//                var responseData =  $.parseJSON(jsonText);
//                if( responseData && responseData.code && responseData.exception.message ) {
//                    msg = 'Error Uploading[' + responseData.code + ']: ' + responseData.exception.message;
//                }

                    msg = jsonText;
                }catch(e){
                    console.log("Could not parse error response: " + e);
                }
            }

            $('#fileUploadStatus1').html('<div style="color: darkred;">'+msg+'</div>')
        }
    </script>
</head>

<body>
<div id="status-header"></div>
<div id="documents-table"></div>

<div id="document-details"></div>

</body>
</html>