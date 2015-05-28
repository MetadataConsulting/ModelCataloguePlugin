metadataEditors = angular.module('mc.core.ui.bs.metadataEditors', ['mc.core.ui.metadataEditors'])

metadataEditors.config ['metadataEditorsProvider', (metadataEditorsProvider)->
   metadataEditorsProvider.register {
     title: 'Occurrence'
     types: [
       '=[containment]=>'
     ]
     keys: ['Min Occurs', 'Max Occurs']
     template: 'modelcatalogue/core/ui/metadataEditors/occurence.html'
   }
]
metadataEditors.run ['$templateCache', ($templateCache) ->
  $templateCache.put 'modelcatalogue/core/ui/metadataEditors/occurence.html', '''
    <form class="form">
      <div class="form-group">
        <label for="minOccurs" class="control-label">Min Occurs</label>
        <input type="number"  min="0" class="form-control" id="minOccurs" placeholder="Min Occurs" ng-model="object.access('Min Occurs').asInt()" ng-model-options="{ getterSetter: true}">
      </div>
      <div class="form-group">
        <label for="maxOccurs" class="control-label">Max Occurs</label>
        <input type="number" min="0" class="form-control" id="maxOccurs" placeholder="Max Occurs (leave blank for unbounded)" ng-model="object.access('Max Occurs').asInt()" ng-model-options="{ getterSetter: true }">
      </div>
    </form>
  '''
]