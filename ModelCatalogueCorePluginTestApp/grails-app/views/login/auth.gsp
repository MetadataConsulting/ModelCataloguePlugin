<%@ page import="org.modelcatalogue.core.util.CDN; grails.util.BuildScope; org.modelcatalogue.core.util.DataModelFilter; grails.util.Environment" contentType="text/html;charset=UTF-8" defaultCodec="none" %>
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

    <title><g:message code="springSecurity.login.title"/></title>
    <g:if test="${CDN.preferred}">
        <!-- CDNs -->
        <link rel="stylesheet" type="text/css"
              href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.2.0/css/bootstrap${minSuffix}.css">
        <link rel="stylesheet" type="text/css"
              href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.4.0/css/font-awesome${minSuffix}.css">

        <!-- code -->
        <asset:stylesheet href="modelcatalogue.css"/>
    </g:if>
    <g:else>
        <asset:stylesheet href="bootstrap/dist/css/bootstrap.css"/>
        <asset:stylesheet href="font-awesome/css/font-awesome"/>
        <asset:stylesheet href="modelcatalogue.css"/>
    </g:else>
</head>

<body>
<g:set var="oauth" bean="oauthService"/>
<div id="metadataCurator" ng-app="metadataCurator">
    <div class="navbar navbar-default navbar-fixed-top" role="navigation">
        <div class="container-fluid">
            <div class="navbar-header">
              <a class="navbar-brand" href="${grailsApplication.config.grails.serverURL}/"><span class="fa fa-fw fa-book fa-2x"></span></a>
              <a class="navbar-brand mc-name-parent" href="${grailsApplication.config.grails.serverURL}/"><span class="mc-name">${grailsApplication.config.grails.mc.name ?: 'Model Catalogue'}</span></a>
            </div>
        </div>
    </div>

    <div class="container-fluid container-main">
        <div class="row">
            <g:if test="${oauth.services}">
                <div class="col-md-6">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <h3 class="panel-title">Login with Service Provider</h3>
                        </div>
                        <div class="panel-body">
                            <g:each in="${oauth.services}" var="entry">
                                <a class="btn btn-block btn-primary btn-lg" href="${createLink(controller: 'oauth', action: 'authenticate', params: [provider: entry.key])}"><span class="fa fa-fw fa-${entry.key}"></span> Login with ${entry.key.capitalize()} Account</a>

                            </g:each>
                        </div>
                    </div>

                </div>
            </g:if>
            <div class="${oauth.services ? '' : 'col-md-offset-3'} col-md-6">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h3 class="panel-title">Login with Username or Email and Password</h3>
                    </div>
                    <div class="panel-body">
                        <g:if test='${flash.message}'>
                            <div class='alert alert-danger'>${flash.message}</div>
                        </g:if>
                        <form action='${postUrl}' method='POST' id='loginForm' class="form-horizontal" autocomplete='off'>
                            <div class="form-group">
                                <label for='username' class="control-label col-sm-3"><g:message code="springSecurity.login.username.label"/> or Email:</label>
                                <div class="col-sm-9">
                                    <input type='text' class='form-control' name='j_username' id='username'/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for='password' class="control-label col-sm-3"><g:message code="springSecurity.login.password.label"/>:</label>
                                <div class="col-sm-9">
                                    <input type='password' class='form-control' name='j_password' id='password'/>
                                    <g:if test="${grailsApplication.config.grails.mail.host.asBoolean() || grailsApplication.config.grails.mc.can.reset.password.asBoolean()}">
                                      <p class="help-block"><g:link action="forgotPassword" controller="register">Reset Password</g:link></p>
                                    </g:if>
                                </div>
                            </div>

                            <div class="form-group">
                                <div class="col-sm-offset-3 col-sm-9">
                                    <div class="checkbox">
                                        <label>
                                            <input type="checkbox" class='chk' name='${rememberMeParameter}' id='remember_me' <g:if test='${hasCookie}'>checked='checked'</g:if>> <g:message code="springSecurity.login.remember.me.label"/>
                                        </label>
                                    </div>
                                </div>
                            </div>

                            <div class="form-group">
                                <div class="col-sm-offset-3 col-sm-9">
                                    <button type="submit" class="btn btn-primary"><i class="glyphicon glyphicon-log-in"></i> Login</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>
<script type='text/javascript'>
    <!--
    (function() {
        document.forms['loginForm'].elements['j_username'].focus();
    })();
    // -->
</script>
</body>
</html>
