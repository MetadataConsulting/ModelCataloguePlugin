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
<ul class="nav nav-pills">
    <li>
        <a class="navbar-brand" href="${g.createLink(controller: 'dashboard', action: 'index')}">
            <i class="fas fa-book"></i> <span><g:message code="navigation.home" default="Model Catalogue"/></span>
        </a>
    </li>
    <sec:ifLoggedIn>
        <li role="presentation" class="dropdown navbar-right">
            <a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
                <i class="fas fa-user"></i> <span class="caret"></span>
            </a>
            <ul class="dropdown-menu">
                <li><a href="#"><i class="fas fa-user"></i> <sec:loggedInUserInfo field='username'/></a></li>
                <li><a href="/#/catalogue/favourites"><i class="fas fa-thumbs-up"></i> Favourites</a></li>
                <li><a href="${g.createLink(controller: 'apiKey')}"><i class="fas fa-key"></i> API Key</a></li>
                <li><a href="${g.createLink(controller: 'logout')}"><i class="fas fa-sign-out-alt"></i>Logout</a></li>
            </ul>
        </li>
    </sec:ifLoggedIn>
    <sec:ifLoggedIn>
        <li role="presentation" class="dropdown navbar-right">
            <a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
                <i class="fas fa-cog"></i> <span class="caret"></span>
            </a>
            <ul class="dropdown-menu">
                <sec:ifAllGranted roles="ROLE_SUPERVISOR">
                    <li><a href="/userAdmin"><i class="fas fa-user-plus"></i> Users</a></li>
                </sec:ifAllGranted>
                <li><a href="${g.createLink(controller: 'modelCatalogueVersion')}"><i class="fas fa-code-branch"></i> Code Version</a></li>
                <sec:ifAnyGranted roles="ROLE_METADATA_CURATOR,ROLE_SUPERVISOR">
                    <li><a href="/batch/all"><i class="fas fa-bolt"></i> Mapping Utility</a></li>
                </sec:ifAnyGranted>
                <!--
                <li><a href="/#/catalogue/csvTransformation/all"><i class="fas fa-long-arrow-alt-right"></i> CSV Transformations</a></li>
                -->
                <sec:ifAllGranted roles="ROLE_SUPERVISOR">
                    <li><a href="${g.createLink(controller: 'lastSeen')}"><i class="fas fa-eye"></i> Activity</a></li>
                </sec:ifAllGranted>
                <sec:ifAllGranted roles="ROLE_SUPERVISOR">
                    <li><a href="${g.createLink(controller: 'reindexCatalogue', action: 'index')}"><i class="fas fa-search"></i> Reindex Catalogue</a></li>
                </sec:ifAllGranted>
                <li><a href="/#/catalogue/relationshipType/all"><i class="fas fa-link"></i> Relationship Types</a></li>
                <li><a href="/#/catalogue/dataModelPolicy/all"><i class="far fa-check-square"></i> Data Model Policies</a></li>
                <sec:ifAllGranted roles="ROLE_SUPERVISOR">
                    <li><a href="/monitoring"><i class="fas fa-cogs"></i> Monitoring</a></li>
                </sec:ifAllGranted>
                <sec:ifAllGranted roles="ROLE_SUPERVISOR">
                    <li><a href="${g.createLink(controller: 'logs')}"><i class="fas fa-archive"></i> Logs</a></li>
                </sec:ifAllGranted>
                <li><a href="/#/catalogue/feedback/all"><i class="fas fa-server"></i> <g:message code="menu.feedbacks" default="Feedbacks"/></a></li>
            </ul>
        </li>
    </sec:ifLoggedIn>
    <sec:ifAnyGranted roles="ROLE_METADATA_CURATOR,ROLE_SUPERVISOR">
        <li role="presentation" class="dropdown navbar-right">
            <a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
                <i class="fas fa-upload"></i> <span class="caret"></span>
            </a>
            <ul class="dropdown-menu">
                <li><a href="${g.createLink(controller: 'dataImportCreate', action: 'importExcel')}"><i class="fas fa-file-excel"></i> <g:message code="dataImport.excel" default="Import Excel"/></a></li>
                <li><a href="${g.createLink(controller: 'dataImportCreate', action: 'importObo')}"><i class="fas fa-file"></i> <g:message code="dataImport.obo" default="Import OBO"/></a></li>
                <li><a href="${g.createLink(controller: 'dataImportCreate', action: 'importModelCatalogueDSL')}"><i class="fas fa-file-alt"></i> <g:message code="dataImport.dsl" default="Import Model Catalogue DSL File"/></a></li>
                <li><a href="${g.createLink(controller: 'dataImportCreate', action: 'importXml')}"><i class="fas fa-file-code"></i> <g:message code="dataImport.xml" default="Import Catalogue XML"/></a></li>
            </ul>
        </li>
    </sec:ifAnyGranted>
    <sec:ifAnyGranted roles="ROLE_METADATA_CURATOR,ROLE_SUPERVISOR">
        <li role="presentation" class="navbar-right">
            <a href="/#/dataModels"><i class="fas fa-plus"></i></a>
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
