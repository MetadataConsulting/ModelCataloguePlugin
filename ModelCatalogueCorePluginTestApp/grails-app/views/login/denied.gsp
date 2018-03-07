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

    <title><g:message code="springSecurity.denied.title"/></title>
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
<div id="metadataCurator" ng-app="metadataCurator">
    <div class="navbar navbar-default navbar-fixed-top" role="navigation">
        <div class="container-fluid">
            <div class="navbar-header">
                <a class="navbar-brand" href="/dashboard/index"><span class="fa fa-fw fa-book"></span><span
                        class="visible-md-inline">&nbsp; Model Catalogue</span></a>
            </div>
        </div>
    </div>

    <div class="container-fluid container-main">
        <div class="row">
            <div class="col-md-12">
                <div class='alert alert-danger'><g:message code="springSecurity.denied.message"/></div>
            </div>
        </div>
    </div>
</div>
</body>
</html>

