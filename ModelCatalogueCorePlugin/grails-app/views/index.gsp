<%@ page contentType="text/html;charset=UTF-8" %>
<html ng-app="demo">
<head>
    <title>Model Catalogue Core Demo Page</title>
    <asset:javascript src="demo.js"/>
    <g:javascript>

        angular.module('demo.config', ['mc.core.modelCatalogueApiRoot']) .value('modelCatalogueApiRoot', '${request.contextPath ?: ''}/api/modelCatalogue/core')

    </g:javascript>
</head>

<body>
<h1>Model Catalogue Core Demo Page</h1>
<div ng-controller="demo.DemoCtrl">
    <decorated-list-table list="list" columns="columns"></decorated-list-table>
</div>
</body>
</html>