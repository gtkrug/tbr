<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1"/>

        <asset:stylesheet src="application.css"/>
        <script>
            const LOGGED_IN = <sec:ifLoggedIn>true</sec:ifLoggedIn><sec:ifNotLoggedIn>false</sec:ifNotLoggedIn>
        </script>
        <asset:javascript src="application.js"/>

        <title>${grailsApplication.config.tbr.org.title}</title>

        <g:layoutHead/>
    </head>

    <body>

        <main>
            <tmpl:/layouts/menu/>

            <div class="container pt-4" id="top">
                <div class="row">
                    <div class="col-12">
                        <asset:image src="tmi-header.png" height="90em"/>
                    </div>
                </div>
            </div>

            <g:layoutBody/>
        </main>

        <footer class="navbar navbar-expand-lg navbar-dark bg-dark mt-4 p-2">
            <div class="container">
                <div class="navbar-nav mx-auto">
                    <a class="nav-link">Version <g:meta name="info.app.version"/>; Build Date <g:meta name="info.app.buildDate"/></a>
                </div>
            </div>
        </footer>
    </body>
</html>
