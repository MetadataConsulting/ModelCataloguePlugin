angular.module('mc.core.ui.bs.catalogueElementView', ['mc.core.ui.catalogueElementView', 'mc.core.ui.decoratedList', 'ui.bootstrap']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/catalogueElementView.html', '''
    <div>
      <h3 class="ce-name">{{element.name}} <small ng-show="element.elementTypeName">({{element.elementTypeName}}: {{element.id}})</small></h3>
      <blockquote class="ce-description" ng-show="element.description">{{element.description}}</blockquote>
      <tabset ng-show="showTabs">
        <tab heading="{{tab.heading}}" disabled="tab.disabled" ng-repeat="tab in tabs" active="tab.active" select="doLoad(tab)">
            <div ng-switch="tab.type">
              <properties-pane item="tab.value" properties="tab.properties" ng-switch-when="properties-pane"></properties-pane>
              <decorated-list list="tab.value" columns="tab.columns" actions="tab.actions" ng-switch-when="decorated-list"></decorated-list>
            </div>
        </tab>
      </tabset>
    </div>
    '''
  ]