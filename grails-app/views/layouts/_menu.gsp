<%@ page import="grails.plugin.springsecurity.SpringSecurityUtils" %>
<%@ page import="tm.binding.registry.UserRole" %>
<%@ page import="tm.binding.registry.Role" %>

<nav class="navbar navbar-inverse navbar-fixed-top">
<div class="container">
    <!--  <nav class="navbar navbar-default tatmenu" role="navigation">  -->
    <!-- Brand and toggle get grouped for better mobile display -->
    <div class="navbar-header">
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#tbr-navbar-collapse-1">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
        <a class="navbar-brand" target="_self" href="${createLink(uri:'/')}">
            ${grailsApplication.config.tbr.org.title}
        </a>
    </div>

    <!-- Collect the nav links, forms, and other content for toggling -->
    <div class="collapse navbar-collapse" id="tbr-navbar-collapse-1">
        <ul class="nav navbar-nav navbar-center">
            <li>
                <a href="${createLink(uri:'/')}">
                    <span class="glyphicon glyphicon-home"></span>
                    Home
                </a>
            </li>
            <sec:ifLoggedIn>
                <sec:ifAllGranted roles="ROLE_USER">
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                            <span class="glyphicon glyphicon-list"></span>
                            Manage <b class="caret"></b>
                        </a>
                        <ul class="dropdown-menu">
                            <li><a href="${createLink(controller: 'provider', action: 'upload')}">Providers</a></li>
                            <li><a href="${createLink(controller: 'registrant', action: 'manage')}">Profile</a></li>
                        </ul>
                    </li>
                </sec:ifAllGranted>
            </sec:ifLoggedIn>
            <sec:ifAllGranted roles="ROLE_ADMIN">
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                        <span class="glyphicon glyphicon-list-alt"></span>
                        Administration <b class="caret"></b>
                    </a>
                    <ul class="dropdown-menu" role="menu">
                        <li><a href="${createLink(controller: 'index', action: 'index')}" title="Manage Organizations">Organizations</a></li>
                <sec:ifAllGranted roles="ROLE_ADMIN">
                        <li><a href="${createLink(controller: 'registrant', action: 'administer')}" title="Manage Registrants">Registrants</a></li>
                </sec:ifAllGranted>
                        <li><a href="${createLink(controller: 'contact', action: 'administer')}" title="Manage Contacts">Points of Contact</a></li>
                        <li><a href="${createLink(controller: 'email', action: 'settings')}" title="Manage Email">Email</a></li>
                        <li><a href="${createLink(controller: 'document', action: 'administer')}" title="Manage Documents">Documents</a></li>
                        <li><a href="${createLink(controller: 'signingCertificates', action: 'administer')}" title="Manage Signing Certificates">Signing Certificates</a></li>
                    </ul>
                </li>
            </sec:ifAllGranted>
            <sec:ifNotLoggedIn>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                        <span class="glyphicon glyphicon-list"></span>
                        View <b class="caret"></b>
                    </a>
                    <ul class="dropdown-menu" role="menu">
                        <li><a href="${createLink(controller: 'index', action: 'index')}">Organizations</a></li>
                        <li><a href="${createLink(controller: 'publicApi', action: 'documents')}">Documents</a></li>
                        <li><a href="${createLink(controller: 'publicApi', action: 'signingCertificates')}">Signing Certificates</a></li>
                    </ul>
                </li>
            </sec:ifNotLoggedIn>
        </ul>
        <sec:ifNotLoggedIn>
            <g:if test="${(UserRole.countByRole(Role.findByAuthority(Role.ROLE_ADMIN)) != 0)}">
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="${createLink(controller: 'login')}">Login</a></li>
                </ul>
            </g:if>
        </sec:ifNotLoggedIn>
        <sec:ifLoggedIn>
            <ul class="nav navbar-nav navbar-right">
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                            <span id="profile-name"><g:registrantName/></span> <b class="caret"></b>
                    </a>
                    <ul class="dropdown-menu" role="menu">
                        <sec:ifAllGranted roles="ROLE_ORG_ADMIN">
                            <g:isRegistrant>
                                <li><a href="${createLink(controller: 'registrant', action: 'edit')}">Edit</a></li>
                            </g:isRegistrant>
                        </sec:ifAllGranted>
                        <li><a href="${createLink(controller: 'changePassword', action: 'editPassword')}">Change Password</a></li>
                        <li><a href="${createLink(controller: 'logout')}">Logout</a></li>
                    </ul>
                </li>
            </ul>
        </sec:ifLoggedIn>
    </div><!-- /.navbar-collapse -->
</div>
</nav>
