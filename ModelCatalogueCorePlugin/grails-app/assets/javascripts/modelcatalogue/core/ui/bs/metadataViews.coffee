metadataEditors = angular.module('mc.core.ui.bs.detailSections', ['mc.core.ui.detailSections'])

metadataEditors.run ['$templateCache', ($templateCache) ->
  $templateCache.put 'modelcatalogue/core/ui/detailSections/basic.html', '''
      <div class="col-md-6">
        <div class="row">
          <div class="col-md-6"><strong class="small">Authors</strong></div>
          <div class="col-md-6"><small>John Smith</small></div>
        </div>
        <div class="row">
          <div class="col-md-6"><strong class="small">Reviewers</strong></div>
          <div class="col-md-6"><small>Ted Summer</small></div>
        </div>
        <div class="row">
          <div class="col-md-6"><strong class="small">Owner</strong></div>
          <div class="col-md-6"><small>Bob Hope</small></div>
        </div>
      </div>
      <div class="col-md-6">
        <div class="row">
          <div class="col-md-6"><strong class="small">Last Updated</strong></div>
          <div class="col-md-6"><small>{{element.lastUpdated | date}}</small></div>
        </div>
        <div class="row">
          <div class="col-md-6"><strong class="small">Version Created</strong></div>
          <div class="col-md-6"><small>{{element.versionCreated | date}}</small></div>
        </div>
        <div class="row">
          <div class="col-md-6"><strong class="small">Status</strong></div>
          <div class="col-md-6"><small>{{element.status}}</small></div>
        </div>
      </div>
  '''

  $templateCache.put 'modelcatalogue/core/ui/detailSections/internalId.html', '''
      <div class="col-md-3">
          <strong class="small">Model Catalogue ID</strong>
      </div>
      <div class="col-md-9"><small>{{element.internalModelCatalogueId}}</small></div>
  '''

  $templateCache.put 'modelcatalogue/core/ui/detailSections/description.html', '''
      <div class="col-md-3">
          <strong class="small">Description</strong>
      </div>
      <div class="col-md-9 preserve-new-lines"><small>{{element.description}}</small></div>
  '''

  $templateCache.put 'modelcatalogue/core/ui/detailSections/test2.html', '''
    <h1>BAR FOO!!!</h1>
  '''
]

metadataEditors.config ['detailSectionsProvider', (detailSectionsProvider)->
  detailSectionsProvider.register {
      title: 'Model Catalogue Internal ID'
      position: -100000
      types: [
        'catalogueElement'
      ]
      keys: []
      template: 'modelcatalogue/core/ui/detailSections/internalId.html'
  }

  detailSectionsProvider.register {
     title: 'Status and Autors'
     position: -10000
     types: [
       'dataModel'
     ]
     keys: ['Foo', 'Bar']
     template: 'modelcatalogue/core/ui/detailSections/basic.html'
  }

  detailSectionsProvider.register {
    title: 'Description'
    position: 0
    types: [
      'catalogueElement'
    ]
    keys: []
    template: 'modelcatalogue/core/ui/detailSections/description.html'
  }

]