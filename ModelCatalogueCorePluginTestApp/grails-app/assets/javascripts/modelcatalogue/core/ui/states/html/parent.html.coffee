angular.module('modelcatalogue.core.ui.states.html.parent.html', []).run(['$templateCache', ($templateCache) ->

    $templateCache.put 'modelcatalogue/core/ui/state/parent.html', '''
        <ui-view></ui-view>
    '''

])
