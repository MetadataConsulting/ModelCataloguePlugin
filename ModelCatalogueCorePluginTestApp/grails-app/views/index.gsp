<%@ page contentType="text/html;charset=UTF-8" defaultCodec="none" %>
<!DOCTYPE html>
<html lang="en">
<head>

    <title>Model Catalogue Demo App</title>
    <asset:stylesheet href="metaDataCurator.css"/>
    <asset:javascript src="metaDataCurator.js"/>
    <script type="text/javascript">
        var demoConfig = angular.module('demo.config', ['mc.core.modelCatalogueApiRoot', 'mc.util.security'])
        demoConfig.config(['securityProvider', function (securityProvider) {
            securityProvider.springSecurity({
                contextPath: '${request.contextPath ?: ''}',
                roles: {
                    VIEWER:     ['ROLE_USER', 'ROLE_METADATA_CURATOR', 'ROLE_ADMIN'],
                    CURATOR:    ['ROLE_METADATA_CURATOR', 'ROLE_ADMIN'],
                    ADMIN:      ['ROLE_ADMIN']
                },
                <sec:ifLoggedIn>
                currentUser: {
                    roles: ${grails.plugin.springsecurity.SpringSecurityUtils.getPrincipalAuthorities()*.authority.encodeAsJSON()},
                    username: '${sec.username()}'
                }
                </sec:ifLoggedIn>
            })
        }])
        demoConfig.value('modelCatalogueApiRoot', '${request.contextPath ?: ''}/api/modelCatalogue/core')
    </script>

</head>

<body>
<div id="metadataCurator" ng-app="metadataCurator" >
    <div class="navbar navbar-default navbar-fixed-top" role="navigation">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="#"><span class="fa fa-fw fa-book"></span>&nbsp; Model Catalogue</a>
            </div>

            <div class="navbar-collapse collapse">
                <contextual-menu></contextual-menu>
                <ul class="nav navbar-nav">
                    <li class="hidden-sm hidden-md hidden-lg" ng-controller="metadataCurator.userCtrl">
                        <a show-if-logged-in ng-click="logout()" type="submit">Log out</a>
                        <a hide-if-logged-in ng-click="login()"  type="submit">Log in</a>
                    </li>

                </ul>

                <form class="navbar-form navbar-right hidden-xs" ng-controller="metadataCurator.userCtrl">
                    <button show-if-logged-in ng-click="logout()" class="btn btn-danger"  type="submit"><i class="glyphicon glyphicon-log-out"></i></button>
                    <button hide-if-logged-in ng-click="login()"  class="btn btn-primary" type="submit"><i class="glyphicon glyphicon-log-in"></i></button>
                </form>

                <form class="navbar-form navbar-right navbar-input-group search-form hidden-xs" role="search" autocomplete="off"
                      ng-submit="search()" ng-controller="metadataCurator.searchCtrl">
                    <a ng-click="clearSelection()" ng-class="{'invisible': !$stateParams.q}" class="clear-selection btn btn-link"><span class="glyphicon glyphicon-remove"></span></a>
                    <div class="form-group">
                        <input
                               ng-model="searchSelect"
                               type="text"
                               name="search-term"
                               id="search-term"
                               placeholder="Search"
                               typeahead="result.term as result.label for result in getResults($viewValue)"
                               typeahead-on-select="search($item, $model, $label)"
                               typeahead-template-url="modelcatalogue/core/ui/omnisearchItem.html"
                               typeahead-wait-ms="300"
                               class="form-control"
                               ng-class="{'expanded': searchSelect}"
                        >
                    </div>
                    <button class="btn btn-default" ng-click="select(searchSelect)"><i class="glyphicon glyphicon-search"></i></button>
                </form>
            </div><!--/.nav-collapse -->
        </div>
    </div>

    <div class="container">

        <div class="row">
            <div class="col-md-12">
                <ui-view></ui-view>
            </div>
        </div>
    </div>
    <messages-panel max="3" growl="true"></messages-panel>
</div>
</body>
</html>