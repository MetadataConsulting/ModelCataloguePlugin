angular.module('mc.core.ui.bs.elementsAsTags', ['mc.core.ui.elementsAsTags']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/elementsAsTags.html', '''
      <div class="tags">
        <span ng-repeat="element in elements">
            <span ng-if="  isString(element) || element.create"  class="label label-success" title="This item will be created">{{element.create ? element.name : element}} <a id="remove-tag-{{$index}}" ng-click="removeItem($index)" class="remove-tag"><span class="glyphicon glyphicon-remove"></span></a></span>
            <span ng-if="!(isString(element) || element.create)" class="label" ng-class="{'label-warning': getStatus(element) == 'DRAFT', 'label-info': getStatus(element) == 'PENDING', 'label-primary': getStatus(element) == 'FINALIZED', 'label-danger': getStatus(element) == 'DEPRECATED'}" title="This is an existing item"><span class="with-pointer" ng-click="openElementInNewWindow(element)">{{element.name}}</span> <a ng-click="removeItem($index)" id="remove-tag-{{$index}}" class="remove-tag"><span class="glyphicon glyphicon-remove"></span></a></span>
        </span>
      </div>
    '''
  ]
