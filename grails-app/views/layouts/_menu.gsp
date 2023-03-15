<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <sec:authorize access="isAuthenticated()">
            <ul class="nav navbar-nav me-auto">
                    <li class="nav-item fw-bold pe-4">
                        <a class="nav-link" href="${createLink(uri: '/')}">
                            ${grailsApplication.config.tbr.org.title}
                        </a>
                    </li>

                    <sec:authorize access="hasAuthority('tbr-admin')">
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" id="dropdown-toggle-administration" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                Administration
                            </a>
                            <ul class="dropdown-menu" aria-labelledby="dropdown-toggle-administration">
                                <li><a class="dropdown-item" href="${createLink(controller: 'index', action: 'index')}" title="Manage Organizations">Organizations</a></li>
                                <li><a class="dropdown-item" href="${createLink(controller: 'user', action: 'administer')}" title="Manage Users">Users</a></li>
                                <li><a class="dropdown-item" href="${createLink(controller: 'contact', action: 'administer')}" title="Manage Contacts">Points of Contact</a></li>
                                <li><a class="dropdown-item" href="${createLink(controller: 'email', action: 'settings')}" title="Manage Email">Email</a></li>
                                <li><a class="dropdown-item" href="${createLink(controller: 'OidcClientRegistration', action: 'manage')}" title="Manage Oauth2 Client Registrations">Client Registrations</a></li>
                                <li><a class="dropdown-item" href="${createLink(controller: 'document', action: 'administer')}" title="Manage Documents">Documents</a></li>
                                <li><a class="dropdown-item" href="${createLink(controller: 'signingCertificates', action: 'administer')}" title="Manage Signing Certificates">Signing Certificates</a></li>
                            </ul>
                        </li>
                    </sec:authorize>
            </ul>
            <ul class="nav navbar-nav">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="dropdown-toggle-user" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                            <g:userProperName/>
                        </a>
                        <ul class="dropdown-menu" aria-labelledby="dropdown-toggle-user">
%{--                            TODO: Remove this or link to Keycloak??? --}%
%{--                            <li><a  class="dropdown-item" href="${createLink(controller: 'changePassword', action: 'editPassword')}">Change Password</a></li>--}%
                            <li><a  class="dropdown-item" href="${createLink(controller: 'logout')}">Logout</a></li>
                        </ul>
                    </li>
            </ul>
        </sec:authorize>
        <sec:authorize access="!isAuthenticated()">
            <ul class="nav navbar-nav me-auto">
                    <li class="nav-item fw-bold pe-4">
                        <a class="nav-link" href="${createLink(uri: '/')}">
                            ${grailsApplication.config.tbr.org.title}
                        </a>
                    </li>
                    <g:noClientAuthorizationRequired>
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" id="dropdown-toggle-view" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                View
                            </a>
                            <ul class="dropdown-menu" aria-labelledby="dropdown-toggle-view">
                                <li><a class="dropdown-item" href="${createLink(controller: 'index', action: 'index')}">Organizations</a></li>
                                <li><a class="dropdown-item" href="${createLink(controller: 'publicApi', action: 'documents')}">Documents</a></li>
                                <li><a class="dropdown-item" href="${createLink(controller: 'publicApi', action: 'signingCertificates')}">Signing Certificates</a></li>
                            </ul>
                        </li>
                    </g:noClientAuthorizationRequired>
            </ul>
            <ul class="nav navbar-nav">
                <li class="nav-item"><a class="nav-link" href="#" onClick="document.location.href=ensureTrailingSlash('${grailsApplication.config.tf.base.url}');">Login</a></li>
                    <script type="text/javascript">
                        function ensureTrailingSlash(url) {
                            url += url.endsWith('/') ? '' : '/';
                            url += 'oauth2/authorize-client/keycloak';
                            return url
                        }
                    </script>
            </ul>
        </sec:authorize>
    </div>
</nav>
