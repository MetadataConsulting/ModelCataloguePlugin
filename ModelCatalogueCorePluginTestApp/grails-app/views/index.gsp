<%@ page import="org.modelcatalogue.core.util.ClassificationFilter; grails.util.Environment" contentType="text/html;charset=UTF-8" defaultCodec="none" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->

    <asset:javascript src="ng-file-upload-shim/angular-file-upload-shim.min.js"/>

    <title>Model Catalogue Demo App</title>
    <g:if test="${Environment.current in [Environment.DEVELOPMENT, Environment.TEST, Environment.CUSTOM]}">
        <script type="text/javascript">
            window.pendingErrorsPres = [];
            window.onerror = function(errorMsg, url, lineNumber) {
                var message = document.createElement('div');
                message.innerHTML = errorMsg + ' at ' + url + ' at ' + lineNumber;
                message.className = 'pre-js-error well';
                if (document.body) {
                    document.getElementById('jserrors').appendChild(message);
                } else {
                    window.pendingErrorsPres.push(message);
                    window.onload = function() {
                        for(var i = 0; i < window.pendingErrorsPres.length ; i++) {
                            document.getElementById('jserrors').appendChild(window.pendingErrorsPres[i]);
                        }
                    }
                }
            }
        </script>
    </g:if>
    <g:if test="${Environment.current in [Environment.PRODUCTION, Environment.TEST, Environment.CUSTOM]}">
        <!-- CDNs -->
        <link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.2.0/css/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.2.0/css/font-awesome.min.css">

        <script type="application/javascript" src="//cdnjs.cloudflare.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
        %{--<script type="application/javascript" src="//cdnjs.cloudflare.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.js"></script>--}%
        %{--this is minimized jquery ui needed for ui-sortable plugin, if there is a need to add more functionality from jquery ui--}%
        <script type="application/javascript" src="//cdn.jsdelivr.net/g/jquery.ui@1.10%28jquery.ui.core.min.js+jquery.ui.widget.min.js+jquery.ui.mouse.min.js+jquery.ui.sortable.min.js%29"></script>
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
        <asset:javascript src="jquery-ui/jquery-ui.js"/>
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
                    classifications: ${(org.modelcatalogue.core.util.ClassificationFilter.from(org.modelcatalogue.core.security.User.get(sec.loggedInUserInfo(field:"id"))).toMap()).encodeAsJSON() }
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
        <div class="container-fluid">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="#"><span class="fa fa-fw fa-book"></span><span class="visible-md-inline">&nbsp; Model Catalogue</span></a>
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

    <div class="container-fluid container-main">
        <div class="row">
            <div class="col-md-12">
                <g:if test="${Environment.current in [Environment.DEVELOPMENT, Environment.TEST, Environment.CUSTOM]}">
                    <div id="jserrors"></div>
                </g:if>
                <ui-view></ui-view>
            </div>
        </div>
    </div>

    <nav class="navbar navbar-default navbar-fixed-bottom" role="navigation" show-if-logged-in>
        <div class="container-fluid">
            <contextual-menu role="navigation-bottom-left"></contextual-menu>
            <contextual-menu role="navigation-bottom-right" right="true"></contextual-menu>
            <div class="mc-version small">
                <g:render template="/version"/>
            </div>
            <g:if env="test">
                <h4 style="float: right; color: red;">TEST ENVIRONMENT - WON'T PERSIST NEXT UPDATE</h4>
            </g:if>
        </div>
    </nav>

    <messages-panel max="3" growl="true"></messages-panel>
</div>
</body>
</html>