angular.module('mc.core.ui.bs.catalogueElementView', ['mc.core.ui.catalogueElementView', 'mc.core.ui.decoratedList',  'mc.core.ui.propertiesPane', 'mc.core.ui.simpleObjectEditor', 'mc.util.ui.bs.contextualActions' , 'ui.bootstrap', 'ngSanitize']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/catalogueElementView.html', '''
    <div>
      <span class="contextual-actions-right">
        <contextual-actions size="sm" no-colors="true" icon-only="true"></contextual-actions>
      </span>
      <h3 class="ce-name"><small ng-class="element.getIcon()" title="{{element.getElementTypeName()}}"></small> {{element.name}} <small><span class="label" ng-show="element.status" ng-class="{'label-warning': element.status == 'DRAFT', 'label-info': element.status == 'PENDING', 'label-primary': element.status == 'FINALIZED', 'label-danger': element.status == 'ARCHIVED'}">{{element.status}}</span></small></h3>
      <blockquote class="ce-description" ng-show="element.description" ng-bind-html="'' + element.description | linky:'_blank'"></blockquote>
      <tabset ng-show="showTabs">
        <tab disabled="tab.disabled" ng-repeat="tab in tabs" active="tab.active" select="select(tab)">
            <tab-heading>{{tab.heading}}<span ng-show="tab.value.total"> <span class="badge">{{tab.value.total}}</span></span></tab-heading>
            <div ng-switch="tab.type">
              <div ng-switch-when="simple-object-editor">
                <simple-object-editor ng-if="tab.name != 'enumerations'" object="tab.value" title="Key" value-title="Value"></simple-object-editor>
                <simple-object-editor ng-if="tab.name == 'enumerations'" object="tab.value" title="Value" value-title="Description" key-placeholder="Value" value-placeholder="Description"></simple-object-editor>
                <div class="row">
                  <div class="col-md-12">
                    <div class=" text-center">
                      <button class="btn btn-primary" ng-disabled="tab.isDirty()" ng-click="tab.update()"><span class="glyphicon glyphicon-ok"></span> Update</button>
                      <button class="btn btn-default" ng-disabled="tab.isDirty()" ng-click="tab.reset()"><span class="glyphicon glyphicon-remove"></span> Reset</button>
                      <br/>
                      <hr/>
                    </div>
                  </div>
                </div>
              </div>
              <properties-pane id="{{tab.heading}}" item="tab.value" properties="tab.properties" ng-switch-when="properties-pane" ng-if="tab.name == 'enumerations'" title="Value" value-title="Description"></properties-pane>
              <properties-pane id="{{tab.heading}}" item="tab.value" properties="tab.properties" ng-switch-when="properties-pane" ng-if="tab.name != 'properties' &amp;&amp; tab.name != 'enumerations' " title="Key" value-title="Value"></properties-pane>
              <properties-pane id="{{tab.heading}}" item="tab.value" properties="tab.properties" ng-switch-when="properties-pane" ng-if="tab.name == 'properties'"></properties-pane>
              <decorated-list list="tab.value" columns="tab.columns" actions="tab.actions" ng-switch-when="decorated-list" id="{{id + '-' + tab.name}}" reports="tab.reports"></decorated-list>
            </div>
        </tab>
      </tabset>
    </div>
    '''
  ]