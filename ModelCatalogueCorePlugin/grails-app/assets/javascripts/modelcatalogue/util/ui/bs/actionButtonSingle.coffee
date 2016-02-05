angular.module('mc.util.ui.bs.actionButtonSingle', []).run [ '$templateCache', ($templateCache) ->
  $templateCache.put 'modelcatalogue/util/ui/actionButtonSingle.html', '''
        <a ng-click="action.run()" class="btn" ng-class="::('btn-' + (noColors ? 'default' : action.type) + ' btn-' + size + ' ' + (action.active ? 'active' : ''))" ng-disabled="::action.disabled" id="{{::action.id}}Btn" title="{{::action.label}}"><span ng-class="::action.icon"></span></span><span ng-if="::!iconOnly"> {{::action.label}}</span></a>
    '''

  $templateCache.put 'modelcatalogue/util/ui/actionButtonSingleSubmit.html', '''
        <button type="submit" class="btn" ng-class="::('btn-' + (noColors ? 'default' : action.type) + ' btn-' + size + ' ' + (action.active ? 'active' : ''))" ng-disabled="::action.disabled" id="{{::action.id}}Btn" title="{{::action.label}}"><span ng-class="::action.icon"></span></span><span ng-if="::!iconOnly"> {{::action.label}}</span></button>
    '''
]