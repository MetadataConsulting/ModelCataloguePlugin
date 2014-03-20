angular.module('mc.core.ui.bs.catalogueElementView', ['mc.core.ui.catalogueElementView', 'mc.core.ui.decoratedList', 'ui.bootstrap']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/catalogueElementView.html', '''
    <div>
      <h3 class="ce-name">{{element.name}} <small ng:show="element.elementTypeName">({{element.elementTypeName}}: {{element.id}})</small></h3>
      <blockquote class="ce-description" ng-show="element.description">{{element.description}}</blockquote>
      <tabset ng-show="propertiesToShow || listRelationsTabs">
        <tab heading="Properties" active="activeTabs[0]">
            <div ng-show="!propertiesToShow">
                <br/>
                <alert>No additional properties to show</alert>
            </div>
            <properties-pane item="element" properties="propertiesToShow"/>
        </tab>
        <tab heading="{{tab.heading}}" disabled="tab.disabled" ng-repeat="(name, tab) in listRelationsTabs" select="loadRelations(tab)" active="activeTabs[$index + 1]">
            <div><decorated-list list="tab.value" columns="tab.columns" item-click="tab.itemSelect"></decorated-list></div>
        </tab>
      </tabset>
    </div>
    '''
  ]