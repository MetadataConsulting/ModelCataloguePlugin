angular.module('mc.core.ui.bs.elementsAsTags', ['mc.core.ui.elementsAsTags']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/elementsAsTags.html', '''
      <div class="tags">
        <span ng-repeat="element in elements">
            <span class="label label-default">{{element.name}} <a ng-click="removeItem($index)" class="remove-tag"><span class="glyphicon glyphicon-remove"></span></a></span>
        </span>
      </div>
    '''
  ]