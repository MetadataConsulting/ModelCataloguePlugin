<!doctype html>
<html lang="en" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>
        <g:layoutTitle default="Metadata Exchange"/>
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
    <link href="https://use.fontawesome.com/releases/v5.0.3/css/all.css" rel="stylesheet">


    <asset:stylesheet src="application.css"/>

    <g:layoutHead/>
</head>
<body>
<ul id="topmenu" class="nav nav-pills">
    <li>
        <a id="home-link" class="navbar-brand" href="${g.createLink(controller: 'dashboard', action: 'index')}">
            <i class="fas fa-book"></i> <span><g:message code="navigation.home" default="Model Catalogue"/></span>
        </a>
    </li>
    <sec:ifLoggedIn>
        <li role="presentation" class="dropdown navbar-right">
            <a id="usermenu-link" class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
                <i class="fas fa-user"></i> <span class="caret"></span>
            </a>
            <ul class="dropdown-menu">
                <li><a href="#" id="username-link"><i class="fas fa-user"></i> <sec:loggedInUserInfo field='username'/></a></li>
                <li><a href="/#/catalogue/favourites" id="favourite-link"><i class="fas fa-thumbs-up"></i> Favourites</a></li>
                <li><a href="${g.createLink(controller: 'apiKey')}" id="apikey-link"><i class="fas fa-key"></i> API Key</a></li>
                <li><a href="${g.createLink(controller: 'logout')}" id="logout-link"><i class="fas fa-sign-out-alt"></i>Logout</a></li>
            </ul>
        </li>
    </sec:ifLoggedIn>
    <sec:ifLoggedIn>
        <li role="presentation" class="dropdown navbar-right">
            <a id="cogmenu-link" class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
                <i class="fas fa-cog"></i> <span class="caret"></span>
            </a>
            <ul class="dropdown-menu">
                <sec:ifAllGranted roles="ROLE_SUPERVISOR">
                    <li><a href="/userAdmin" id="users-link"><i class="fas fa-user-plus"></i> Users</a></li>
                    <li><g:link controller="dataModelPermission" action="index" id="dataModelPermission-link"><i class="fas fa-lock"></i> <g:message code="nav.dataModelPermission" default="Data Model ACL"/></g:link></li>
                </sec:ifAllGranted>
                <li><a href="${g.createLink(controller: 'modelCatalogueVersion')}" id="codeversion-link"><i class="fas fa-code-branch"></i> Code Version</a></li>
                <sec:ifAnyGranted roles="ROLE_METADATA_CURATOR,ROLE_SUPERVISOR">
                    <li><a href="/batch/all" id="mappingutility-link"><i class="fas fa-bolt"></i> Mapping Utility</a></li>
                </sec:ifAnyGranted>
                <!--
                <li><a href="/#/catalogue/csvTransformation/all"><i class="fas fa-long-arrow-alt-right"></i> CSV Transformations</a></li>
                -->
                <sec:ifAllGranted roles="ROLE_SUPERVISOR">
                    <li><a href="${g.createLink(controller: 'lastSeen')}" id="activity-link"><i class="fas fa-eye"></i> Activity</a></li>
                </sec:ifAllGranted>
                <sec:ifAllGranted roles="ROLE_SUPERVISOR">
                    <li><a href="${g.createLink(controller: 'reindexCatalogue', action: 'index')}" id="reindexcatalogue-link"><i class="fas fa-search"></i> Reindex Catalogue</a></li>
                </sec:ifAllGranted>
                <li><a href="/#/catalogue/relationshipType/all" id="relationshiptypes-link"><i class="fas fa-link"></i> Relationship Types</a></li>
                <li><a href="/#/catalogue/dataModelPolicy/all" id="datamodelpolicy-link"><i class="far fa-check-square"></i> Data Model Policies</a></li>
                <sec:ifAllGranted roles="ROLE_SUPERVISOR">
                    <li><a href="/monitoring" id="monitoring-link"><i class="fas fa-cogs"></i> Monitoring</a></li>
                </sec:ifAllGranted>
                <sec:ifAllGranted roles="ROLE_SUPERVISOR">
                    <li><a href="${g.createLink(controller: 'logs')}" id="logs-link"><i class="fas fa-archive"></i> Logs</a></li>
                </sec:ifAllGranted>
                <li><a href="/#/catalogue/feedback/all" id="feedbacks-link"><i class="fas fa-server"></i> <g:message code="menu.feedbacks" default="Feedbacks"/></a></li>
            </ul>
        </li>
    </sec:ifLoggedIn>
    <sec:ifAnyGranted roles="ROLE_METADATA_CURATOR,ROLE_SUPERVISOR">
        <li role="presentation" class="dropdown navbar-right">
            <a  id="importmenu-link" class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
                <i class="fas fa-upload"></i> <span class="caret"></span>
            </a>
            <ul class="dropdown-menu">
                <li><a id="importexcel-link" href="${g.createLink(controller: 'dataImportCreate', action: 'importExcel')}"><i class="fas fa-file-excel"></i> <g:message code="dataImport.excel" default="Import Excel"/></a></li>
                <li><a id="importobo-link" href="${g.createLink(controller: 'dataImportCreate', action: 'importObo')}"><i class="fas fa-file"></i> <g:message code="dataImport.obo" default="Import OBO"/></a></li>
                <li><a id="importdsl-link" href="${g.createLink(controller: 'dataImportCreate', action: 'importModelCatalogueDSL')}"><i class="fas fa-file-alt"></i> <g:message code="dataImport.dsl" default="Import Model Catalogue DSL File"/></a></li>
                <li><a id="importxml-link"  href="${g.createLink(controller: 'dataImportCreate', action: 'importXml')}"><i class="fas fa-file-code"></i> <g:message code="dataImport.xml" default="Import Catalogue XML"/></a></li>
            </ul>
        </li>
    </sec:ifAnyGranted>
    <sec:ifAnyGranted roles="ROLE_METADATA_CURATOR,ROLE_SUPERVISOR">
        <li role="presentation" class="navbar-right">
            <a href="/dataModel/create" id="createdatamodel-link" ><i class="fas fa-plus"></i></a>
        </li>
    </sec:ifAnyGranted>
</ul>
<g:layoutBody/>


<div class="footer" role="contentinfo">
    <g:render template="/templates/sponsorsfooter"/>
</div>

<div id="spinner" class="spinner" style="display:none;">
    <g:message code="spinner.alt" default="Loading&hellip;"/>
</div>
<asset:javascript src="application.js"/>

</body>
</html>
