<%@ page import="org.modelcatalogue.core.util.DataModelFilter; org.modelcatalogue.core.util.CDN; grails.plugin.springsecurity.SpringSecurityUtils; org.modelcatalogue.core.security.User; grails.util.BuildScope; org.modelcatalogue.core.util.DataModelFilter; grails.util.Environment" contentType="text/html;charset=UTF-8" defaultCodec="none" %>
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
    <asset:javascript src="spring-websocket" />
    <script type="text/javascript">
        window.SockJSURL = "${createLink(uri: '/stomp')}";
    </script>

    <title>Model Catalogue Demo App</title>
    <g:if test="${Environment.current in [Environment.DEVELOPMENT, Environment.TEST, Environment.CUSTOM]}">
        <script type="text/javascript">
            window.pendingErrorsPres = [];
            window.printErrorInPre = function(errorMsg, url, lineNumber) {
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
            };
            window.onerror = function(errorMsg, url, lineNumber) {
                window.printErrorInPre(errorMsg, url, lineNumber)
            }
        </script>
    </g:if>
    <g:if test="${CDN.preferred}">
        <g:set var="minSuffix" value="${Environment.current == Environment.TEST ? '' : '.min'}"/>
        <!-- CDNs -->
        <link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.5/css/bootstrap${minSuffix}.css">
        <link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.4.0/css/font-awesome${minSuffix}.css">

        <script type="application/javascript" src="//cdnjs.cloudflare.com/ajax/libs/rxjs/4.0.8/rx.all${minSuffix}.js"></script>
        <script type="application/javascript" src="//cdnjs.cloudflare.com/ajax/libs/jquery/2.1.4/jquery${minSuffix}.js"></script>
        <script type="application/javascript" src="//cdnjs.cloudflare.com/ajax/libs/jqueryui/1.11.4/jquery-ui${minSuffix}.js"></script>
        <script type="application/javascript" src="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.5/js/bootstrap${minSuffix}.js"></script>
        <script type="application/javascript" src="//cdnjs.cloudflare.com/ajax/libs/angular.js/1.4.7/angular${minSuffix}.js"></script>
        <script type="application/javascript" src="//cdnjs.cloudflare.com/ajax/libs/angular-ui-bootstrap/0.13.4/ui-bootstrap-tpls${minSuffix}.js"></script>

        <!-- i18n 1.3.15 not present but hopefuly it's the same -->
        <script type="application/javascript" src="//cdnjs.cloudflare.com/ajax/libs/angular.js/1.4.7/i18n/angular-locale_en-gb.js"></script>

        <script type="application/javascript" src="//cdnjs.cloudflare.com/ajax/libs/angular.js/1.4.7/angular-animate${minSuffix}.js"></script>
        <script type="application/javascript" src="//cdnjs.cloudflare.com/ajax/libs/angular.js/1.4.7/angular-sanitize${minSuffix}.js"></script>
        <script type="application/javascript" src="//cdnjs.cloudflare.com/ajax/libs/angular.js/1.4.7/angular-cookies${minSuffix}.js"></script>
        <script type="application/javascript" src="//cdnjs.cloudflare.com/ajax/libs/rx-angular/1.1.3/rx.angular${minSuffix}.js"></script>
        <script type="application/javascript" src="//cdnjs.cloudflare.com/ajax/libs/URI.js/1.17.1/URI${minSuffix}.js"></script>

        <!-- code -->
        <asset:stylesheet href="modelcatalogue.css"/>
        <asset:javascript src="modelcatalogue/modelcatalogue.js"/>
    </g:if>
    <g:else>
        <asset:stylesheet href="bootstrap/dist/css/bootstrap.css"/>
        <asset:stylesheet href="font-awesome/css/font-awesome"/>
        <asset:stylesheet href="modelcatalogue.css"/>

        <asset:javascript src="rxjs/dist/rx.all.js"/>
        <asset:javascript src="jquery/dist/jquery.js"/>
        <asset:javascript src="jquery-ui/jquery-ui.js"/>
        <asset:javascript src="bootstrap/dist/js/bootstrap.js"/>
        <asset:javascript src="angular/angular.js"/>
        <asset:javascript src="angular-animate/angular-animate.js"/>
        <asset:javascript src="angular-bootstrap/ui-bootstrap-tpls.js"/>
        <asset:javascript src="angular-cookies/angular-cookies.js"/>
        <asset:javascript src="angular-sanitize/angular-sanitize.js"/>
        <asset:javascript src="angular-animate/angular-animate.js"/>
        <asset:javascript src="angular-rx/dist/rx.angular.js"/>
        <asset:javascript src="modelcatalogue/modelcatalogue.js"/>
        <asset:javascript src="urijs/src/URI.js"/>
    </g:else>
    <g:set var="configurationProvider" bean="frontendConfigurationProviderRegistry"/>
    <g:set var="oauthService" bean="oauthService"/>
    <script type="text/javascript">
        ${configurationProvider.frontendConfiguration}
        var demoConfig = angular.module('demo.config', ['mc.core.modelCatalogueApiRoot', 'mc.util.security']);
        demoConfig.config(['securityProvider', function (securityProvider) {
            securityProvider.springSecurity({
                oauthProviders: ${oauthService.services.keySet().collect{"'$it'"}},
                contextPath:      '${grailsApplication.config.grails.app.context ?: request.contextPath ?: ''}',
                allowRegistration: ${grailsApplication.config.mc.allow.signup.asBoolean()},
                canResetPassword:  ${grailsApplication.config.grails.mail.host.asBoolean() || grailsApplication.config.grails.mc.can.reset.password.asBoolean()},
                roles: {
                    VIEWER:     ['ROLE_USER', 'ROLE_METADATA_CURATOR', 'ROLE_ADMIN'],
                    CURATOR:    ['ROLE_METADATA_CURATOR', 'ROLE_ADMIN'],
                    ADMIN:      ['ROLE_ADMIN']
                },
                <sec:ifLoggedIn>
                currentUser: {
                    roles: ${SpringSecurityUtils.getPrincipalAuthorities()*.authority.encodeAsJSON()},
                    username: '${sec.username()}',
                    id: ${sec.loggedInUserInfo(field:"id")},
                    dataModels: ${(DataModelFilter.from(User.get(sec.loggedInUserInfo(field:"id"))).toMap()).encodeAsJSON() }
                }
                </sec:ifLoggedIn>
            })
        }]);

        demoConfig.run(['$templateCache', function ($templateCache) {
            $templateCache.put("/info/version.html", '${render(template:"/version")}')

        }]);

        demoConfig.run(function(editableOptions, editableThemes) {
            editableThemes.bs3.inputClass = 'input-xs';
            editableThemes.bs3.buttonsClass = 'btn-sm';
            editableOptions.theme = 'bs3';
        });

        modelcatalogue.registerModule('demo.config');

        modelcatalogue.welcome = {};
        modelcatalogue.welcome.jumbo = "${grailsApplication.config.mc.welcome.jumbo.encodeAsJSON()}";
        modelcatalogue.welcome.info = "${grailsApplication.config.mc.welcome.info.encodeAsJSON()}";

        // create an app module based on registered modules
        angular.module('metadataCurator', window.modelcatalogue.getModules()).run(function($state){
            // workaround https://github.com/angular-ui/ui-router/issues/2051
        })
    </script>
    <g:if test="${Environment.current in [Environment.DEVELOPMENT, Environment.TEST, Environment.CUSTOM]}">
        <script type="text/javascript">
            angular.module('demo.config').factory('$exceptionHandler', function($log, $window) {
                return function(exception, cause) {
                    $log.error(exception, cause);
                    window.printErrorInPre($window.location.href);
                    window.printErrorInPre(exception.stack);
                };
            });

        </script>
    </g:if>

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
                    <a class="navbar-brand" href="#/"><span class="fa fa-fw fa-book fa-2x"></span></a>
                    <a class="navbar-brand mc-name-parent" href="#/"><span class="mc-name">${grailsApplication.config.grails.mc.name ?: 'Model Catalogue'}</span></a>
                </div>

                <div class="navbar-collapse collapse">
                    <div ui-view="navbar-left">
                        <contextual-menu role="navigation"></contextual-menu>
                    </div>
                    <div ui-view="navbar-right">
                        <contextual-menu role="navigation-right" right="true"></contextual-menu>
                    </div>
                </div><!--/.nav-collapse -->
            </div>
        </div>

        <div class="container-fluid container-main">
          <%-- especially for re-authentication --%>
          <div class="row">
            <%
              String message = flash.remove('message')
              String error = flash.remove('error')

              String flashType = message ? 'info' : 'danger'
              String flashText = error ?: message

            %>
            <g:if test="${flashText}">
              <div class="col-md-12 top">
                <div class="alert alert-${flashType}">${flashText}</div>
              </div>
            </g:if>

          </div>
            <div class="row content-row">
                <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                    <div id="jserrors"></div>
                    <ui-view>
                    <div class="jumbotron">
                        <div>
                            <div class="text-center"><span class="fa fa-fw fa-5x fa-spin fa-spinner"></span></div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-12 col-sm-12 col-md-12">
                            <div class="panel panel-default">
                                <div class="panel-body">
                                    <div class="row">
                                        <div class=" col-xs-12 col-sm-12 col-md-4 col-lg-4"><p>Model catalogue development supported by</p></div>
                                        <div class=" col-xs-3 col-sm-3 col-md-2 col-lg-2">
                                            <p>
                                                <a href="http://www.genomicsengland.co.uk/">
                                                    <asset:image src="/modelcatalogue/GEL.jpg" class="img-thumbnail sponsor-logo-small" alt="Genomics England" />
                                                </a>
                                            </p>
                                            <p class="hidden-xs"><a href="http://www.genomicsengland.co.uk/" class="text-muted">Genomics England</a></p>
                                        </div>
                                        <div class=" col-xs-3 col-sm-3 col-md-2 col-lg-2">
                                            <p><a href="http://www.mrc.ac.uk"><asset:image src="/modelcatalogue/MRC.png" class="img-thumbnail sponsor-logo-small" alt="Medical Research Council"/></a></p>
                                            <p class="hidden-xs"><a href="http://www.mrc.ac.uk" class="text-muted">Medical Research Council</a></p>
                                        </div>
                                        <div class=" col-xs-3 col-sm-3 col-md-2 col-lg-2">
                                            <p><a href="http://www.nihr.ac.uk/"><asset:image src="/modelcatalogue/NIHR.png" class="img-thumbnail sponsor-logo-small" alt="NIHR"/></a></p>
                                            <p class="hidden-xs"><a href="http://www.nihr.ac.uk/" class="text-muted">National Institute for Health Research</a></p>
                                        </div>
                                        <div class=" col-xs-3 col-sm-3 col-md-2 col-lg-2">
                                            <p><a href="http://www.metadataconsusting.co.uk"><asset:image src="/modelcatalogue/MDC.png" class="img-thumbnail sponsor-logo-small" alt="Metadata Consulting Ltd" /></a></p>
                                            <p class="hidden-xs"><a href="http://www.metadataconsusting.co.uk" class="text-muted">Metadata Consulting</a></p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    </ui-view>
                </div>
            </div>
        </div>

    <messages-panel max="3" growl="true"></messages-panel>
</div>
</body>
</html>
