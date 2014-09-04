angular.module('mc.core.ui.bs.elementsAsTags', ['mc.core.ui.elementsAsTags']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/elementsAsTags.html', '''
      <div class="tags">
        <span ng-repeat="element in elements">
            <span ng-show="isString(element) || element.create" class="label label-success" title="This item will be created">{{element.create ? element.name : element}} <a ng-click="removeItem($index)" class="remove-tag"><span class="glyphicon glyphicon-remove"></span></a></span>
            <span ng-hide="isString(element) || element.create" class="label label-primary" title="This is an existing item">{{element.name}} <a ng-click="removeItem($index)" class="remove-tag"><span class="glyphicon glyphicon-remove"></span></a></span>
        </span>
      </div>
    '''
  ]