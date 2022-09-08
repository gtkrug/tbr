<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <sec:ifLoggedIn>
            <ul class="nav navbar-nav me-auto">
                <li class="nav-item fw-bold pe-4">
                    <a class="nav-link" href="${createLink(uri: '/')}">
                        ${grailsApplication.config.tbr.org.title}
                    </a>
                </li>
                <sec:ifAllGranted roles="ROLE_USER">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="dropdown-toggle-manage" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                            Manage
                        </a>
                        <ul class="dropdown-menu" aria-labelledby="dropdown-toggle-manage">
                            <li><a class="dropdown-item" href="${createLink(controller: 'provider', action: 'upload')}">Providers</a></li>
                            <li><a class="dropdown-item" href="${createLink(controller: 'registrant', action: 'manage')}">Profile</a></li>
                        </ul>
                    </li>
                </sec:ifAllGranted>
                <sec:ifAllGranted roles="ROLE_ADMIN">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="dropdown-toggle-administration" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                            Administration
                        </a>
                        <ul class="dropdown-menu" aria-labelledby="dropdown-toggle-administration">
                            <li><a class="dropdown-item" href="${createLink(controller: 'index', action: 'index')}" title="Manage Organizations">Organizations</a></li>
                            <li><a class="dropdown-item" href="${createLink(controller: 'registrant', action: 'administer')}" title="Manage Registrants">Registrants</a></li>
                            <li><a class="dropdown-item" href="${createLink(controller: 'contact', action: 'administer')}" title="Manage Contacts">Points of Contact</a></li>
                            <li><a class="dropdown-item" href="${createLink(controller: 'email', action: 'settings')}" title="Manage Email">Email</a></li>
                            <li><a class="dropdown-item" href="${createLink(controller: 'document', action: 'administer')}" title="Manage Documents">Documents</a></li>
                            <li><a class="dropdown-item" href="${createLink(controller: 'signingCertificates', action: 'administer')}" title="Manage Signing Certificates">Signing Certificates</a></li>
                        </ul>
                    </li>
                </sec:ifAllGranted>
            </ul>
            <ul class="nav navbar-nav">
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="dropdown-toggle-user" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                        <g:registrantName/>
                    </a>
                    <ul class="dropdown-menu" aria-labelledby="dropdown-toggle-user">
                        <sec:ifAllGranted roles="ROLE_ORG_ADMIN">
                            <g:isRegistrant>
                                <li><a class="dropdown-item" href="${createLink(controller: 'registrant', action: 'edit')}">Edit</a></li>
                            </g:isRegistrant>
                        </sec:ifAllGranted>
                        <li><a  class="dropdown-item" href="${createLink(controller: 'changePassword', action: 'editPassword')}">Change Password</a></li>
                        <li><a  class="dropdown-item" href="${createLink(controller: 'logout')}">Logout</a></li>
                    </ul>
                </li>
            </ul>
        </sec:ifLoggedIn>
        <sec:ifNotLoggedIn>
            <ul class="nav navbar-nav me-auto">
                <li class="nav-item fw-bold pe-4">
                    <a class="nav-link" href="${createLink(uri: '/')}">
                        ${grailsApplication.config.tbr.org.title}
                    </a>
                </li>
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
            </ul>
            <ul class="nav navbar-nav">
                <li class="nav-item"><a class="nav-link" href="${createLink(controller: 'login')}">Login</a></li>
            </ul>
        </sec:ifNotLoggedIn>
    </div>
</nav>
