<!doctype html>
<html lang="en" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>${grailsApplication.config.tbr.org.title}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>

    <asset:stylesheet src="application.css"/>
    <asset:javascript src="application.js"/>

    <g:layoutHead/>
</head>

<body>
<tmpl:/layouts/menu />

<a name="top"></a>
<div class="container" style="margin-top: 5em; margin-bottom: 5em;">
    <div class="row header">
        <div class="col-md-2 headerTopLeft">
            <div id="header-logo">
                <asset:image height="90em" src="${grailsApplication.config.tbr.org.banner}" />
            </div>
        </div>
    </div>
    <div class="content row">
        <div class="col-md-12">
            <g:layoutBody/>
        </div>
    </div>

    <div style="margin-bottom: 4em;">&nbsp;</div>
</div>

<footer>
    <div class="container-fluid">
        <div class="row">
            <div class="col-md-6">
                <span class="text-muted">&copy;2020 Georgia Tech Research Institute</span>
            </div>
            <div>
                v.<g:meta name="info.app.version"/>,
                Build Date: <g:meta name="info.app.buildDate"/>
            </div>
        </div>
    </div>
</footer>

</body>
</html>
