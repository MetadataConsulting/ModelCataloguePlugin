metadataEditors = angular.module('mc.core.ui.bs.metadataEditors', ['mc.core.ui.metadataEditors'])

metadataEditors.config ['metadataEditorsProvider', (metadataEditorsProvider)->
   metadataEditorsProvider.register {
     title: 'Occurrence'
     types: [
       '=[containment]=>'
       '=[hierarchy]=>'
     ]
     keys: ['Min Occurs', 'Max Occurs']
     template: 'modelcatalogue/core/ui/metadataEditors/occurence.html'
   }
   metadataEditorsProvider.register {
     title: 'Appearance'
     types: [
       '=[containment]=>'
       '=[hierarchy]=>'
     ]
     keys: ['Name']
     template: 'modelcatalogue/core/ui/metadataEditors/appearance.html'
   }

   metadataEditorsProvider.register {
     title: 'Subset'
     types: [
       'EnumeratedType=[base]=>EnumeratedType'
     ]
     keys: ['http://www.modelcatalogue.org/metadata/enumerateType#subset']
     template: 'modelcatalogue/core/ui/metadataEditors/subset.html'
   }
]
metadataEditors.run ['$templateCache', ($templateCache) ->
  $templateCache.put 'modelcatalogue/core/ui/metadataEditors/occurence.html', '''
    <form class="form">
      <div class="form-group">
        <label for="minOccurs" class="control-label">Min Occurs</label>
        <input type="number"  min="0" class="form-control" id="minOccurs" placeholder="Min Occurs" ng-model="object.access('Min Occurs').asInt" ng-model-options="{ getterSetter: true}">
      </div>
      <div class="form-group">
        <label for="maxOccurs" class="control-label">Max Occurs</label>
        <input type="number" min="0" class="form-control" id="maxOccurs" placeholder="Max Occurs (leave blank for unbounded)" ng-model="object.access('Max Occurs').asInt" ng-model-options="{ getterSetter: true }">
      </div>
    </form>
  '''
  $templateCache.put 'modelcatalogue/core/ui/metadataEditors/appearance.html', '''
    <form class="form">
      <div class="form-group">
        <label for="local-name" class="control-label">Name</label>
        <input type="text" class="form-control" id="local-name"  ng-model="object.access('Name')" ng-model-options="{ getterSetter: true}">
      </div>
    </form>
  '''

  $templateCache.put 'modelcatalogue/core/ui/metadataEditors/subset.html', '''
    <form class="form">
      <div class="form-group">
        <label for="subset" class="control-label">Subset</label>
        <input type="text" class="form-control" id="subset"  ng-model="object.access('http://www.modelcatalogue.org/metadata/enumerateType#subset')" ng-model-options="{ getterSetter: true}">
      </div>
    </form>
  '''
]
