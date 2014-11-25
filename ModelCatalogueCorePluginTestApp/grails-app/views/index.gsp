<%@ page import="grails.util.Environment" contentType="text/html;charset=UTF-8" defaultCodec="none" %>
<!DOCTYPE html>
<html lang="en">
<head>

    <title>Model Catalogue Demo App</title>
    <g:if test="${Environment.current in [Environment.PRODUCTION, Environment.TEST, Environment.CUSTOM]}">
        <!-- CDNs -->
        <link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.2.0/css/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.2.0/css/font-awesome.min.css">

        <script type="application/javascript" src="//cdnjs.cloudflare.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
        <script type="application/javascript" src="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.2.0/js/bootstrap.min.js"></script>
        <script type="application/javascript" src="//cdnjs.cloudflare.com/ajax/libs/angular.js/1.3.0/angular.min.js"></script>
        <script type="application/javascript" src="//cdnjs.cloudflare.com/ajax/libs/angular-ui-bootstrap/0.11.2/ui-bootstrap-tpls.min.js"></script>

        <!-- i18n 1.3.0 not present but hopefuly it's the same -->
        <script type="application/javascript" src="//cdnjs.cloudflare.com/ajax/libs/angular-i18n/1.2.15/angular-locale_en-gb.js"></script>

        <script type="application/javascript" src="//cdnjs.cloudflare.com/ajax/libs/angular.js/1.3.0/angular-animate.min.js"></script>
        <script type="application/javascript" src="//cdnjs.cloudflare.com/ajax/libs/angular.js/1.3.0/angular-sanitize.min.js"></script>
        <script type="application/javascript" src="//cdnjs.cloudflare.com/ajax/libs/angular.js/1.3.0/angular-cookies.min.js"></script>

        <!-- code -->
        <asset:stylesheet href="modelcatalogue.css"/>
        <asset:javascript src="modelcatalogue.js"/>
    </g:if>
    <g:else>
        <asset:stylesheet href="bootstrap/dist/css/bootstrap.css"/>
        <asset:stylesheet href="font-awesome/css/font-awesome"/>
        <asset:stylesheet href="modelcatalogue.css"/>

        <asset:javascript src="jquery/dist/jquery.js"/>
        <asset:javascript src="bootstrap/dist/js/bootstrap.js"/>
        <asset:javascript src="angular/angular.js"/>
        <asset:javascript src="angular-animate/angular-animate.js"/>
        <asset:javascript src="angular-bootstrap/ui-bootstrap-tpls.js"/>
        <asset:javascript src="angular-cookies/angular-cookies.js"/>
        <asset:javascript src="angular-sanitize/angular-sanitize.js"/>
        <asset:javascript src="angular-animate/angular-animate.js"/>
        <asset:javascript src="modelcatalogue.js"/>
    </g:else>
    <script type="text/javascript">
        var demoConfig = angular.module('demo.config', ['mc.core.modelCatalogueApiRoot', 'mc.util.security']);
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
                    username: '${sec.username()}',
                    id: ${sec.loggedInUserInfo(field:"id")},
                    classifications: ${(org.modelcatalogue.core.security.User.get(sec.loggedInUserInfo(field:"id"))?.classifications?.collect({ org.modelcatalogue.core.util.marshalling.CatalogueElementMarshallers.minimalCatalogueElementJSON(it) }) ?: []).encodeAsJSON() }
                }
                </sec:ifLoggedIn>
            })
        }]);
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
                <a class="navbar-brand" href="#/dashboard"><span class="fa fa-fw fa-book"></span><span class="visible-md-inline">&nbsp; Model Catalogue</span></a>
            </div>

            <div class="navbar-collapse collapse">
                <contextual-menu></contextual-menu>
                <ul class="nav navbar-nav">
                    <li class="hidden-sm hidden-md hidden-lg" ng-controller="defaultStates.userCtrl">
                        <a show-if-logged-in ng-click="logout()" type="submit">Log out</a>
                        <a hide-if-logged-in ng-click="login()"  type="submit">Log in</a>
                    </li>

                </ul>

                <form class="navbar-form navbar-right hidden-xs" ng-controller="defaultStates.userCtrl">
                    <button show-if-logged-in ng-click="logout()" class="btn btn-danger"  type="submit"><i class="glyphicon glyphicon-log-out"></i></button>
                    <button hide-if-logged-in ng-click="login()"  class="btn btn-primary" type="submit"><i class="glyphicon glyphicon-log-in"></i></button>
                </form>

                <ng-include src="'modelcatalogue/core/ui/omnisearch.html'"></ng-include>

            </div><!--/.nav-collapse -->
        </div>
    </div>

    <div class="container container-main">
        <div class="row">
            <div class="col-md-12">
                <ui-view></ui-view>
            </div>
        </div>
    </div>

    <nav class="navbar navbar-default navbar-fixed-bottom" role="navigation">
        <div class="container">
            <contextual-menu role="navigation-bottom-left"></contextual-menu>
            <contextual-menu role="navigation-bottom-right" right="true"></contextual-menu>
            <g:if env="text">
                <h4 style="float: right; color: red;">TEST ENVIRONMENT - WON'T PERSIST NEXT UPDATE</h4>
            </g:if>
        </div>
    </nav>

    <messages-panel max="3" growl="true"></messages-panel>
</div>
</body>
</html>