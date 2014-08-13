angular.module('mc.core.ui.bs.batchView', ['mc.core.ui.batchView', 'mc.core.ui.decoratedList',  'mc.core.ui.propertiesPane', 'mc.core.ui.simpleObjectEditor', 'mc.util.ui.bs.contextualActions' , 'ui.bootstrap', 'ngSanitize']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/batchView.html', '''
    <div>
      <span class="pull-right">
        <contextual-actions size="sm" no-colors="true"></contextual-actions>
      </span>
      <h3 class="ce-name">{{batch.name}} <small ng-show="batch.elementTypeName"><span class="label" ng-show="batch.archived" ng-class="{'label-danger': batch.archived}">{{batch.status}}</span> (created {{batch.dateCreated | date:'short'}})</small></h3>
      <blockquote class="ce-description" ng-show="batch.description" ng-bind-html="'' + batch.description | linky:'_blank'"></blockquote>
      <div class="row">
        <div class="col-md-6">
          <h4>Pending Actions</h4>
          <alert type="'info'" ng-hide="loading || pendingActions.length > 0">There no pending actions</alert>
          <alert ng-repeat="action in pendingActions" type="getType(action)" ng-show="action">
            <div class="pull-right"><contextual-actions group="true" icon-only="true" size="sm" no-colors="true"/></div>
            <div class="preserve-new-lines">{{action.message}}</div>
            <br ng-show="action.outcome"/>
            <pre ng-show="action.outcome">{{action.outcome}}</pre>
          </alert>
        </div>
        <div class="col-md-6">
          <h4>Performed Actions</h4>
          <alert type="'info'" ng-hide="loading || performedActions.length > 0">There no actions performed or failed</alert>
          <alert ng-repeat="action in performedActions" type="getType(action)" ng-show="action">
            <div class="pull-right"><contextual-actions group="true" icon-only="true" size="sm" no-colors="true"/></div>
            <div class="preserve-new-lines">{{action.outcome}}</div>
          </alert>
        </div>
      </div>
    </div>
    '''
  ]