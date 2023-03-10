<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1"/>

        <asset:stylesheet src="application.css"/>
        <script>
            const LOGGED_IN = <sec:authorize access="isAuthenticated()">true</sec:authorize><sec:authorize access="!isAuthenticated()">false</sec:authorize>
            const HAS_TBR_ROLES = <sec:authorize access="!hasAuthority('tbr-admin') and !hasAuthority('tbr-org-admin')">false</sec:authorize><sec:authorize access="hasAnyAuthority('tbr-admin', 'tbr-org-admin')">true</sec:authorize>
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


%{--        Warning banner for adminstrators with no assigned organizations. --}%
            <sec:authorize access="isAuthenticated()">
                <sec:authorize access="hasAnyAuthority('tbr-admin', 'tbr-org-admin')">
                    <g:isUserNotAssignedToAnOrganization>
                        <div class="container pt-4" id="admins-with-no-organization-warning-message">
                            <div class="alert alert-warning d-flex " role="alert">
                                <i class="bi bi-exclamation-triangle-fill"></i>
                                <div>
                                    You are not assigned to an organization, please contact the TBR administrator to request an organization assignment.
                                </div>
                            </div>
                        </div>
                    </g:isUserNotAssignedToAnOrganization>
                </sec:authorize>
            </sec:authorize>

%{--        Warning banner for users with no TBR roles. --}%
            <sec:authorize access="isAuthenticated()">
                <sec:authorize access="!hasAuthority('tbr-admin') and !hasAuthority('tbr-org-admin')">
                    <div class="container pt-4" id="users-with-no-tbr-roles-warning-message">
                        %{--            align-items-center--}%
                        <div class="alert alert-warning d-flex " role="alert">
                            <i class="bi bi-exclamation-triangle-fill"></i>
                            <div>
                                You have no TBR role assigned.  Your administrator must assign you a role to use the TBR.  Contact your TBR administrator for help.
                            </div>
                        </div>
                    </div>
                </sec:authorize>
            </sec:authorize>

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
