<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en" ng-app="demo">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Model Catalogue Core Demo Page</title>
    <asset:stylesheet href="demo.css"/>
    <asset:javascript src="demo.js"/>
    <g:javascript>

        angular.module('demo.config', ['mc.core.modelCatalogueApiRoot']) .value('modelCatalogueApiRoot', '${request.contextPath ?: ''}/api/modelCatalogue/core')

    </g:javascript>
</head>

<body>

    <!-- Fixed navbar -->
    <div class="navbar navbar-default navbar-fixed-top" role="navigation">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="#">Model Catalogue Core Plugin</a>
            </div>
        </div>
    </div>

    <div class="container">
        <h1>Model Catalogue Core Demo Page</h1>
        <div ng-controller="demo.DemoCtrl">
            <decorated-list-table list="list" columns="columns"/>
        </div>
    </div>
</body>
</html>