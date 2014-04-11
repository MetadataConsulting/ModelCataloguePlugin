angular.module('mc.core.ui.bs.catalogueElementTreeview', ['mc.core.ui.catalogueElementTreeview', 'mc.core.ui.decoratedList', 'ui.bootstrap']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/catalogueElementTreeview.html', '''
    <ul class="catalogue-element-treeview-list-root catalogue-element-treeview-list">
        <catalogue-element-treeview-item element="element" descend="descend"></catalogue-element-treeview-item>
    </ul>
    '''
  ]