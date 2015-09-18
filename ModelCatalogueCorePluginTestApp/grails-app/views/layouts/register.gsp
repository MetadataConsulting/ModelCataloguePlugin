<%@ page import="org.modelcatalogue.core.util.CDN; grails.util.BuildScope; org.modelcatalogue.core.util.FriendlyErrors; org.modelcatalogue.core.util.ClassificationFilter; grails.util.Environment" contentType="text/html;charset=UTF-8" defaultCodec="none" %>
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

    <title><g:layoutTitle default='User Registration'/></title>

    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon"/>

    <s2ui:resources module='register'/>

    <g:if test="${CDN.preferred}">
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

    <g:layoutHead/>

</head>

<body>
<s2ui:layoutResources module='register'/>
<div class="navbar navbar-default navbar-fixed-top" role="navigation">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="${grailsApplication.config.grails.serverURL}"><span class="fa fa-fw fa-book"></span><span
                    class="visible-md-inline">&nbsp; Model Catalogue</span></a>
        </div>
    </div>
</div>

<div class="container-fluid container-main">
    <div class="row">
        <%
            String message = flash.remove('message')
            String error = flash.remove('error')

            String flashType = message ? 'info' : 'danger'
            String flashText = message ?: error

        %>
        <g:if test="${flashText}">
            <div class="col-md-6 col-md-offset-3">
                <div class="alert alert-${flashType}">$flashText</div>
            </div>
        </g:if>

    </div>
    <div class="row">
        <g:layoutBody/>
    </div>
</div>

</body>
</html>
