<%@ page import="grails.plugin.springsecurity.SpringSecurityUtils" %>
<%@ page import="tm.binding.registry.UserRole" %>
<%@ page import="tm.binding.registry.Role" %>

<nav class="navbar navbar-inverse navbar-fixed-top">
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
                            <li><a href="#">Assessments</a></li>
                            <li><a href="#" title="Manage Contacts">Contacts</a></li>
                            <li><a href="#" title="Manage Organizations">Organizations</a></li>
                            <li><a href="#" title="Manage Documents">Documents</a></li>
                            <li><a href="#" title="Resolve undefined substeps">Substep Resolution</a></li>
                            <li><a href="#">Trust Interoperability Profiles</a></li>
                            <li><a href="#">Trustmarks</a></li>
                            <li><a href="#">Trustmark Definitions</a></li>
                            <li><a href="#">Trustmark Metadata</a></li>
                            <sec:ifAllGranted roles="ROLE_ADMIN">
                                <li><a href="#" title="Manage User Accounts">Users</a></li>
                            </sec:ifAllGranted>
                        </ul>
                    </li>
                </sec:ifAllGranted>
                <li>
                    <a href="#" title="Manage Your User Profile">
                        <span class="glyphicon glyphicon-user"></span>
                        Profile
                    </a>
                </li>
                <sec:ifAllGranted roles="ROLE_USER">
                    <li>
                        <a href="${createLink(controller:'reports', action:'index')}" title="Generate Assessment Reports">
                            <span class="glyphicon glyphicon-stats"></span>
                            Reports
                        </a>
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
                        <li><a href="${createLink(controller: 'admin', action: 'importExportView')}">Import/Export</a></li>
                        <li><a href="${createLink(controller: 'error')}">Error Tests</a></li>
                        <li><a href="${createLink(controller: 'tdAndTipUpdate')}">TD & TIP Update</a></li>
                    </ul>
                </li>
            </sec:ifAllGranted>
            <sec:ifLoggedIn>
                <li><a href="${createLink(controller: 'logout')}">Logout</a></li>
            </sec:ifLoggedIn>
            <sec:ifNotLoggedIn>
                <g:if test="${(UserRole.countByRole(Role.findByAuthority(Role.ROLE_ADMIN)) != 0)}">
                    <li><a href="${createLink(controller: 'login')}">Login</a></li>
                </g:if>
            </sec:ifNotLoggedIn>
        </ul>
    </div><!-- /.navbar-collapse -->
</nav>
