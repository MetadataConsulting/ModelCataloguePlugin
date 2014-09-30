angular.module('mc.core.ui.bs.importView', ['mc.core.ui.importView', 'mc.core.ui.decoratedList',  'mc.core.ui.propertiesPane', 'mc.core.ui.simpleObjectEditor',  'ui.bootstrap']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/importView.html', '''
    <div>

    <span class="contextual-actions-right">
        <contextual-actions size="sm" no-colors="true"></contextual-actions>
     </span>

          <h3 class="ce-name">{{element.name}} <small ng-show="element.getElementTypeName()">({{element.getElementTypeName()}}: {{element.id}})</small></h3>
          <blockquote class="ce-description" ng-show="element.description">{{element.description}}</blockquote>
          <tabset ng-show="showTabs">
            <tab heading="{{tab.heading}}" disabled="tab.disabled" ng-repeat="tab in tabs" active="tab.active" select="select(tab)">
                <div ng-switch="tab.type">
                  <div ng-switch-when="simple-object-editor">
                    <simple-object-editor object="tab.value"></simple-object-editor>
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
                  <properties-pane item="tab.value" properties="tab.properties" ng-switch-when="properties-pane"></properties-pane>
                  <decorated-list list="tab.value" columns="tab.columns" actions="tab.actions" ng-switch-when="decorated-list" id="{{id + '-' + tab.name}}" reports="tab.reports"></decorated-list>
                </div>
            </tab>
          </tabset>
    </div>
    '''
  ]
