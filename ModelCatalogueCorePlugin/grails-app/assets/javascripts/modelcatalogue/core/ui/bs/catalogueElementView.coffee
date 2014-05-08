angular.module('mc.core.ui.bs.catalogueElementView', ['mc.core.ui.catalogueElementView', 'mc.core.ui.decoratedList', 'ui.bootstrap']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/catalogueElementView.html', '''
    <div>
      <span class="pull-right">
        <div class="btn-group btn-group-sm">
          <button type="button" class="btn btn-primary dropdown-toggle" ng-disabled="reports &amp;&amp; reports.length == 0">
            <span class="glyphicon glyphicon-download-alt"></span> Export <span class="caret"></span>
          </button>
          <ul class="dropdown-menu" role="menu">
            <li><a ng-href="{{report.url}}" target="_blank" ng-repeat="report in reports">{{report.name || 'Export'}}</a></li>
          </ul>
        </div>
        <a class="btn btn-success btn-sm" ng-click="createRelationship()"><span class="glyphicon glyphicon-link"></span> Create Relationship</a>
      </span>
      <h3 class="ce-name">{{element.name}} <small ng-show="element.elementTypeName">({{element.elementTypeName}}: {{element.id}})</small></h3>
      <blockquote class="ce-description" ng-show="element.description">{{element.description}}</blockquote>
      <tabset ng-show="showTabs">
        <tab heading="{{tab.heading}}" disabled="tab.disabled" ng-repeat="tab in tabs" active="tab.active" select="select(tab)">
            <div ng-switch="tab.type">
              <properties-pane item="tab.value" properties="tab.properties" ng-switch-when="properties-pane"></properties-pane>
              <decorated-list list="tab.value" columns="tab.columns" actions="tab.actions" ng-switch-when="decorated-list" id="{{id + '-' + tab.name}}" reports="tab.reports"></decorated-list>
            </div>
        </tab>
      </tabset>
    </div>
    '''
  ]