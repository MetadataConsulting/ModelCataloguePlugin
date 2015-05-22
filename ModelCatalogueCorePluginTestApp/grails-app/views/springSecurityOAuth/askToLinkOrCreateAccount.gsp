<%@ page import="org.modelcatalogue.core.util.FriendlyErrors; org.modelcatalogue.core.util.ClassificationFilter; grails.util.Environment" contentType="text/html;charset=UTF-8" defaultCodec="none" %>
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
    <g:if test="${Environment.current in [Environment.PRODUCTION, Environment.TEST, Environment.CUSTOM]}">
        <!-- CDNs -->
        <link rel="stylesheet" type="text/css"
              href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.2.0/css/bootstrap${minSuffix}.css">
        <link rel="stylesheet" type="text/css"
              href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.2.0/css/font-awesome${minSuffix}.css">

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
<g:set var="messageSource" bean="messageSource" />
<div id="metadataCurator" ng-app="metadataCurator">
    <div class="navbar navbar-default navbar-fixed-top" role="navigation">
        <div class="container-fluid">
            <div class="navbar-header">
                <a class="navbar-brand" href="#"><span class="fa fa-fw fa-book"></span><span
                        class="visible-md-inline">&nbsp; Model Catalogue</span></a>
            </div>
        </div>
    </div>

    <div class="container-fluid container-main">
        <div class="row">
            <div class="${grailsApplication.config.grails.mc.allow.signup ? 'col-md-12' : 'col-md-9 col-md-offset-3'}">
                <h3><g:message code="springSecurity.oauth.registration.link.not.exists" default="No user was found with this account." args="[session.springSecurityOAuthToken?.providerName]"/></h3>
                <g:if test='${flash.message}'>
                    <div class='alert alert-danger'>${flash.message}</div>
                </g:if>
            </div>
            <g:if test="${grailsApplication.config.grails.mc.allow.signup}">
                <div class="col-md-6">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <h3 class="panel-title"><g:message code="springSecurity.oauth.registration.create.legend" default="Create a new account"/></h3>
                        </div>
                        <div class="panel-body">
                            <g:hasErrors bean="${createAccountCommand}">
                                <g:each in="${createAccountCommand.errors.allErrors}" var="error">
                                    <div class='alert alert-danger'><g:message error="${error}"/></div>
                                </g:each>
                            </g:hasErrors>

                            <g:form action="createAccount" method="post" autocomplete="off" class="form-horizontal">
                                <div class="form-group ${hasErrors(bean: createAccountCommand, field: 'username', 'has-error')} ">
                                    <label for='username-new' class="control-label col-sm-3"><g:message code="OAuthCreateAccountCommand.username.label" default="Username"/>:</label>
                                    <div class="col-sm-9">
                                        <input type='text' class='form-control' name='username' id="username-new" value='${linkAccountCommand?.username}'/>
                                    </div>
                                </div>

                                <div class="form-group ${hasErrors(bean: createAccountCommand, field: 'password1', 'has-error')} ">
                                    <label for='password1' class="control-label col-sm-3"><g:message code="OAuthCreateAccountCommand.password1.label" default="Password"/>:</label>
                                    <div class="col-sm-9">
                                        <input type='password' class='form-control' name='password1' id="password1" value='${createAccountCommand?.password1}'/>
                                    </div>
                                </div>

                                <div class="form-group ${hasErrors(bean: createAccountCommand, field: 'password2', 'has-error')} ">
                                    <label for='password2' class="control-label col-sm-3"><g:message code="OAuthCreateAccountCommand.password2.label" default="Password re-type"/>:</label>
                                    <div class="col-sm-9">
                                        <input type='password' class='form-control' name='password2' id="password2" value='${createAccountCommand?.password2}'/>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-sm-offset-3 col-sm-9">
                                        <button type="submit" class="btn btn-primary"><i class="glyphicon glyphicon-user"></i> <g:message code="springSecurity.oauth.registration.create.button" default="Create"/></button>
                                    </div>
                                </div>
                            </g:form>
                        </div>
                    </div>
                </div>
            </g:if>
            <div class="col-md-6 ${grailsApplication.config.grails.mc.allow.signup ? '' : 'col-md-offset-3'}">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h3 class="panel-title"><g:message code="springSecurity.oauth.registration.login.legend" default="Link to an existing account"/></h3>
                    </div>
                    <div class="panel-body">
                        <g:hasErrors bean="${linkAccountCommand}">
                            <g:each in="${linkAccountCommand.errors.allErrors}" var="error">
                                <div class='alert alert-danger'><g:message error="${error}"/></div>
                            </g:each>
                        </g:hasErrors>

                        <g:form action="linkAccount" method="post" autocomplete="off" class="form-horizontal">
                            <div class="form-group ${hasErrors(bean: linkAccountCommand, field: 'username', 'has-error')} ">
                                <label for='username-link' class="control-label col-sm-3"><g:message code="springSecurity.login.username.label"/>:</label>
                                <div class="col-sm-9">
                                    <input type='text' class='form-control' name='username' id="username-link" value='${linkAccountCommand?.username}'/>
                                </div>
                            </div>

                            <div class="form-group ${hasErrors(bean: linkAccountCommand, field: 'password', 'has-error')} ">
                                <label for='password-link' class="control-label col-sm-3"><g:message code="springSecurity.login.password.label"/>:</label>
                                <div class="col-sm-9">
                                    <input type='password' class='form-control' name='password' id="password-link" value='${linkAccountCommand?.password}'/>
                                </div>
                            </div>

                            <div class="form-group">
                                <div class="col-sm-offset-3 col-sm-9">
                                    <button type="submit" class="btn btn-primary"><i class="glyphicon glyphicon-log-in"></i> Login</button>
                                </div>
                            </div>
                        </g:form>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>

</body>
