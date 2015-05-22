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
            <div class="col-md-offset-3 col-md-6">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h3 class="panel-title"><g:message code="springSecurity.login.header"/></h3>
                    </div>
                    <div class="panel-body">
                        <g:if test='${flash.message}'>
                            <div class='alert alert-danger'>${flash.message}</div>
                        </g:if>

                        <form action='${postUrl}' method='POST' id='loginForm' class="form-horizontal" autocomplete='off'>
                            <div class="form-group">
                                <label for='username' class="control-label col-sm-3"><g:message code="springSecurity.login.username.label"/>:</label>
                                <div class="col-sm-9">
                                    <input type='text' class='form-control' name='j_username' id='username'/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for='password' class="control-label col-sm-3"><g:message code="springSecurity.login.password.label"/>:</label>
                                <div class="col-sm-9">
                                    <input type='password' class='form-control' name='j_password' id='password'/>
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
