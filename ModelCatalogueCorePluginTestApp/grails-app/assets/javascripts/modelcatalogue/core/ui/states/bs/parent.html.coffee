angular.module('mc.core.ui.states.bs.parent.html', []).run(['$templateCache', ($templateCache) ->

    $templateCache.put 'modelcatalogue/core/ui/state/parent.html', '''
        <ui-view></ui-view>
    '''

])