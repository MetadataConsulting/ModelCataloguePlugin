metadataEditors = angular.module('mc.core.ui.bs.metadataEditors', ['mc.core.ui.metadataEditors'])

metadataEditors.config ['metadataEditorsProvider', (metadataEditorsProvider)->
   metadataEditorsProvider.register {
     title: 'Occurrence'
     types: [
       '=[containment]=>'
       'dataElement' # testing only
     ]
     keys: ['Min Occurs', 'Max Occurs']
     template: 'modelcatalogue/core/ui/metadataEditors/occurence.html'
   }
]
metadataEditors.run ['$templateCache', ($templateCache) ->
  $templateCache.put 'modelcatalogue/core/ui/metadataEditors/occurence.html', '''
    <div class="row">
      <div class="form-group col-md-6">
        <label for="minOccurs" class="col-sm-4 control-label">Min Occurs</label>
        <div class="col-sm-8">
          <input type="number"  min="0" class="form-control" id="minOccurs" placeholder="Min Occurs" ng-model="object.access('Min Occurs')" ng-model-options="{ getterSetter: true }">
        </div>
      </div>
      <div class="form-group col-md-6">
        <label for="maxOccurs" class="col-sm-4 control-label">Max Occurs</label>
        <div class="col-sm-8">
          <input type="number" min="0" class="form-control" id="maxOccurs" placeholder="Max Occurs (leave blank for unbounded)" ng-model="object.access('Max Occurs')" ng-model-options="{ getterSetter: true }">
        </div>
      </div>
    </div>
  '''
]