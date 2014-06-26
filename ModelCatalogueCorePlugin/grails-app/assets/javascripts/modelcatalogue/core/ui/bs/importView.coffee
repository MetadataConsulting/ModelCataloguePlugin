angular.module('mc.core.ui.bs.importView', ['mc.core.ui.importView', 'mc.core.ui.decoratedList',  'mc.core.ui.propertiesPane', 'mc.core.ui.simpleObjectEditor',  'ui.bootstrap']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/importView.html', '''
    <div>
    <span class="pull-right" show-for-role="CURATOR">
          <div class="btn-group btn-group-sm">
          <button type="button" class="btn btn-primary dropdown-toggle" >
            <span class="glyphicon glyphicon-download-alt"></span> Options <span class="caret"></span>
          </button>
          <ul class="dropdown-menu" role="menu">
            <li><a ng-click="resolveAll()" target="_blank" >Resolve All Pending</a></li>
            <li><a ng-click="ingestQueue()"  target="_blank"">Ingest Queue</a></li>
          </ul>
        </div>
      </span>
          <h3 class="ce-name">{{element.name}} <small ng-show="element.elementTypeName">({{element.elementTypeName}}: {{element.id}})</small></h3>
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