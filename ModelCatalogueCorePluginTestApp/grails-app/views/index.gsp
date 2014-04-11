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
    <script type="text/javascript">
        angular.module('demo.config', ['mc.core.modelCatalogueApiRoot']).value('modelCatalogueApiRoot', '${request.contextPath ?: ''}/api/modelCatalogue/core')
    </script>

    %{--<!-- example of customization -->--}%
    %{--<script type="text/ng-template" id="modelcatalogue/core/ui/decoratedList.html">--}%
    %{--<div>--}%
    %{--<p ng-hide="list.list">No data</p>--}%
    %{--<ul>--}%
    %{--<li ng-repeat="item in list.list">{{item.name}}</li>--}%
    %{--</ul>--}%
    %{--</div>--}%
    %{--</script>--}%

</head>

<body ng-controller="demo.DemoCtrl">

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

    <div>
        <form class="form" role="form" ng-submit="show()">
            <div class="form-group">
                <label for="search">Search</label>
                <input type="text" id="search" ng-model="element" catalogue-element-picker>
                <label for="expression">Expression</label>
                <input type="text" class="form-control" id="expression" ng-model="expression"
                       placeholder="Expression returning list promise">
                <span class="help-block">
                    <a ng-click="expression = indicator         ; show()">Show Indicator</a> ~
                    <a ng-click="expression = listResource      ; show()">Get Resource List</a> ~
                    <a ng-click="expression = listRelTypes      ; show()">Get Relationship Types List</a> ~
                    <a ng-click="expression = searchSomething   ; show()">Global Search</a> ~
                    <a ng-click="expression = searchModel       ; show()">Search Model</a> ~
                    <a ng-click="expression = outgoing          ; show()">Outgoing Relationship</a>
                </span>
            </div>
            <button type="submit" class="btn btn-primary">Show</button>
        </form>
    </div>

    <div ng-show="element.elementType">
        <catalogue-element-view element="element"></catalogue-element-view>
    </div>

    <div ng-show="!list.empty">
        <h2>Decorated List Widget</h2>
        <decorated-list list="list" columns="columns" selection="selection" actions="actions"></decorated-list>
    </div>

    <div ng-show="!list.empty">
        <h2>Configure Columns</h2>
        <table class="table">
            <tr ng-show="columns">
                <th>
                    Header
                </th>
                <th>
                    Value
                </th>
                <th>
                    Class
                </th>
                <th>
                    Show
                </th>
                <th>
                    &nbsp;
                </th>
            </tr>
            <tr ng-repeat="column in columns">
                <td><input class="form-control" ng-model="column.header"/></td>
                <td><input class="form-control" ng-model="column.value"/></td>
                <td><input class="form-control" ng-model="column.classes"/></td>
                <td><input class="form-control" ng-model="column.show"/></td>
                <td>
                    <button class="btn btn-success btn-sm" ng-click="addColumn($index, column)"><span
                            class="glyphicon glyphicon-plus"></span> Add</button>
                    <button class="btn btn-danger btn-sm" ng-class="{disabled: columns.length <= 1}"
                            ng-click="removeColumn($index)"><span class="glyphicon glyphicon-minus"></span> Remove
                    </button>
                </td>
            </tr>
        </table>
    </div>

    <div ng-show="element.elementType">
        <h2>Tree View</h2>
        <div>
            <form class="form" role="form">
                <div class="form-group">
                    <label for="descendPath">Descend Path</label>
                    <input type="text" id="descendPath" ng-model="descendPath" class="form-control">
                    <label for="selectedInTreeview">Search</label>
                    <input type="text" id="selectedInTreeview" ng-model="selectedInTreeview" catalogue-element-picker>
                </div>
            </form>
        </div>
        <div class="col-md-8">
            <catalogue-element-treeview element="element" descend="descend"></catalogue-element-treeview>
        </div>
    </div>

</div>
</body>
</html>