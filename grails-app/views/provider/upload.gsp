<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="layout" content="main"/>
    <title>Upload</title>
</head>
<body>
<div id="uploadForm">
    <form method="post" enctype="multipart/form-data" class="form-inline">
        <div class="form-group">
            <input name="filename" type="file" class="form-control" accept=".xml"/>
        </div>
        <button type="submit" class="btn btn-default">Upload</button>
    </form>
</div>
</body>
</html>
