angular.module('mc.util.ui.bs.actionButtonSingle', ['mc.util.ui.actionButtonSingle']).run [ '$templateCache', ($templateCache) ->
  $templateCache.put 'modelcatalogue/util/ui/actionButtonSingle.html', '''
        <button ng-click="action.run()" class="btn" ng-class="'btn-' + action.type + ' btn-' + size" ng-disabled="action.disabled" title="{{action.label}}"><span ng-show="action.icon" class="glyphicon" ng-class="'glyphicon-' + action.icon"></span></span><span ng-hide="iconOnly"> {{action.label}}</span></button>
    '''
]