<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en" >
<head>

    <title>Model Catalogue Demo App</title>

    %{--<!-- example of customization -->--}%
    %{--<script type="text/ng-template" id="modelcatalogue/core/ui/decoratedList.html">--}%
    %{--<div>--}%
    %{--<p ng-hide="list.list">No data</p>--}%
    %{--<ul>--}%
    %{--<li ng-repeat="item in list.list">{{item.name}}</li>--}%
    %{--</ul>--}%
    %{--</div>--}%
    %{--</script>--}%

    <asset:stylesheet href="demo.css"/>
    <asset:javascript src="metadataCurator.js"/>
    <script type="text/javascript">
        angular.module('demo.config', ['mc.core.modelCatalogueApiRoot']).value('modelCatalogueApiRoot', '${request.contextPath ?: ''}/api/modelCatalogue/core')
    </script>

</head>

<body>
<div id="metadataCurator" ng-app="metadataCurator">
    <div class="navbar navbar-default">
        <div class="navbar-inner">
            <ul class="nav navbar-nav">
                <li><a id="modelLink" href="#/catalogueElement/model">Models</a></li>
                <li><a id="dataElementLink" href="#/catalogueElement/dataElement">Data Elements</a></li>
                <li><a id="valueDomainLink" href="#/catalogueElement/valueDomain">Value Domains</a></li>
                <li><a id="conceptualDomainLink" href="#/catalogueElement/conceptualDomain">Conceptual Domains</a></li>
                <li><a id="dataTypeLink" href="#/catalogueElement/dataType">Data Types</a></li>
                <li><a id="relationshipTypeLink" href="#/catalogueElement/relationshipType">Relationship Types</a></li>
            </ul>
            <div class="col-sm-3 col-md-3 pull-right">
                <form ng-controller="metadataCurator.searchCtrl" class="navbar-form" role="search" autocomplete="off" ng-submit="search()">
                    <div class="input-group">
                        <input ng-model="searchSelect" type="text" id="search" name="search-term" id="search-term"  catalogue-element-picker>
                        <div class="input-group-btn">
                            <button class="btn btn-default" type="submit"><i class="glyphicon glyphicon-search"></i></button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="span12">
            <div class="well" ui-view></div>
        </div>
    </div>
</div>
</body>
</html>