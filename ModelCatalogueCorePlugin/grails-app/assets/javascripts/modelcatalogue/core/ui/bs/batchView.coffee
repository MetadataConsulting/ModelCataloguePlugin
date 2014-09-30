angular.module('mc.core.ui.bs.batchView', ['mc.core.ui.batchView', 'mc.core.ui.decoratedList',  'mc.core.ui.propertiesPane', 'mc.core.ui.simpleObjectEditor', 'mc.util.ui.bs.contextualActions' , 'ui.bootstrap', 'ngSanitize']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/batchView.html', '''
    <div>
      <span class="contextual-actions-right">
        <contextual-actions size="sm" no-colors="true"></contextual-actions>
      </span>
      <h3 class="ce-name">{{batch.name}} <small ng-show="batch.getElementTypeName()"><span class="label" ng-show="batch.archived" ng-class="{'label-danger': batch.archived}">{{batch.status}}</span> (created {{batch.dateCreated | date:'short'}})</small></h3>
      <blockquote class="ce-description" ng-show="batch.description" ng-bind-html="'' + batch.description | linky:'_blank'"></blockquote>
      <div class="row">
        <div class="col-md-6 pending-actions">
          <h4>Pending Actions</h4>
          <alert type="'info'" ng-hide="loading || pendingActions.length > 0">There no pending actions</alert>
          <alert ng-repeat="action in pendingActions" type="getType(action)" id="action-{{action.id}}">
            <div class="action-header">
              <div class="pull-right">
                <contextual-actions group="true" icon-only="true" size="sm" no-colors="true"/>
              </div>
              <h4>{{action.naturalName}}
                <small>
                  <span class="label label-warning action-label" ng-click="highlight(action.dependsOn)" class="btn btn-warning btn-sm" ng-show="action.dependsOn.length > 0" title="Depends on {{action.dependsOn.length}} action(s)"><span class="glyphicon glyphicon-open"></span> {{action.dependsOn.length}}</span>
                  <span class="label label-warning action-label" ng-click="highlight(action.dependencies)" class="btn btn-warning btn-sm" ng-show="action.dependencies.length > 0" title="{{action.dependencies.length}} other action(s) depends on this action"><span class="glyphicon glyphicon-save"></span> {{action.dependencies.length}}</span>
                  <span class="label label-warning" ng-show="action.highlighted" title="This action is in role '{{action.highlighted}}'">{{natural(action.highlighted)}}</span>
                </small>
              </h4>
            </div>
            <div class="preserve-new-lines" ng-bind-html="action.message"></div>
            <br ng-show="action.outcome"/>
            <pre ng-show="action.outcome">{{action.outcome}}</pre>
          </alert>
        </div>
        <div class="col-md-6 performed-actions">
          <h4>Performed Actions</h4>
          <alert type="'info'" ng-hide="loading || performedActions.length > 0">There no actions performed or failed</alert>
          <alert ng-repeat="action in performedActions" type="getType(action)" id="action-{{action.id}}">
            <div>
              <div class="pull-right">
                <contextual-actions group="true" icon-only="true" size="sm" no-colors="true"/>
              </div>
              <h4>{{action.naturalName}}
                <small>
                  <span class="label label-warning action-label" ng-click="highlight(action.dependsOn)" class="btn btn-warning btn-sm" ng-show="action.dependsOn" title="Depends on {{action.dependsOn.length}} action(s)"><span class="glyphicon glyphicon-open"></span> {{action.dependsOn.length}}</span>
                  <span class="label label-warning action-label" ng-click="highlight(action.dependencies)" class="btn btn-warning btn-sm" ng-show="action.dependencies" title="{{action.dependencies.length}} other action(s) depends on this action"><span class="glyphicon glyphicon-save"></span> {{action.dependencies.length}}</span>
                </small>
              </h4>
            </div>
            <div class="preserve-new-lines" ng-show="action.state == 'PERFORMING'" ng-bind-html="action.message"></div>
            <div class="preserve-new-lines" ng-show="action.state == 'FAILED'">Failed to "<span ng-bind-html="action.message">{{action.message}}</span>"</div>
            <br ng-show="action.outcome &amp;&amp; action.state == 'FAILED' &amp;&amp; action.message"/>
            <div class="preserve-new-lines" ng-show="action.outcome &amp;&amp; action.state == 'PERFORMED'" ng-bind-html="action.outcome"></div>
            <pre ng-show="action.outcome &amp;&amp; action.state == 'FAILED'">{{action.outcome}}</pre>
          </alert>
        </div>
      </div>
    </div>
    '''
  ]