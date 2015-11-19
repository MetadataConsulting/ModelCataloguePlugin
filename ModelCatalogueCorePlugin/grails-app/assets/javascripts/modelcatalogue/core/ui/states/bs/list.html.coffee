angular.module('mc.core.ui.states.bs.list.html', []).run(['$templateCache', ($templateCache) ->

  #language=HTML
  $templateCache.put 'modelcatalogue/core/ui/state/list.html', '''
    <div class="row" ng-if="(resource != 'dataClass' &amp;&amp;  resource != 'dataClass' &amp;&amp; resource != 'dataModel')|| $stateParams.display != undefined">
      <span class="contextual-actions-right">
        <contextual-actions size="sm" no-colors="true" role="list"></contextual-actions>
      </span>
      <h2><small ng-class="catalogue.getIcon(resource)"></small>&nbsp;<span ng-show="$stateParams.status">{{natural($stateParams.status)}}</span> {{title}}</h2>
      <infinite-list  ng-if="$stateParams.display == 'grid'"  list="list"></infinite-list>
      <infinite-table ng-if="$stateParams.display != 'grid'"  list="list" columns="columns" ></infinite-table>
    </div>
    <div ng-if="(resource == 'dataClass' || resource == 'model' || resource == 'xdataModel')&amp;&amp; $stateParams.display == undefined">
      <div class="row">
        <div class="col-xs-3 col-sm-3 col-md-3 col-lg-3 split-view-left" resizable="{'handles': 'e', 'mirror': '.split-view-right', 'maxWidthPct': 60, 'minWidthPct': 20, 'windowWidthCorrection': 91, 'parentWidthCorrection': 31, 'breakWidth': 768}">
          <div class="split-view-content">
            <div class="row">
              <span class="contextual-actions-right">
                   <contextual-actions size="sm" icon-only="true" no-colors="true" role="list"></contextual-actions>
              </span>
              <div class="col-md-12">
                <h3>
                    <small ng-class="catalogue.getIcon('dataClass')"></small>&nbsp;<span ng-show="$stateParams.status">{{natural($stateParams.status)}}</span> {{ resource == 'dataClass' ? 'Data Classes' : 'Data Models' }}
                </h3>
                <catalogue-element-treeview list="list" descend="resource == 'xdataModel' ? 'content' : 'parentOf'" id="model-treeview" on-select="onTreeviewSelected($element)"></catalogue-element-treeview>
              </div>
            </div>
          </div>
        </div>
        <div class="col-xs-9 col-sm-9 col-md-9 col-lg-9 split-view-right" ng-if="element">
          <div class="split-view-content">
            <catalogue-element-view element="element" property="property"></catalogue-element-view>
          </div>
        </div>
      </div>
    </div>
    <div class="row" ng-if="resource == 'dataModel'">
      <div class="col-md-12">
        <infinite-list heading="'Data Models'" on-create-requested="createDataModel()" list="list" no-actions="true"></infinite-list>
      </div>
    </div>
  '''
])