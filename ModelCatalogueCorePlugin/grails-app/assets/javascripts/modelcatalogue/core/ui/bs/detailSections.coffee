metadataEditors = angular.module('mc.core.ui.bs.detailSections', ['mc.core.ui.detailSections'])


###
  If you need to put more explanation for certain title put following snippet after the label's strong element and
  update it to your own help text.

  <span class="fa fa-question-circle text-muted" tooltip="These are the authors of the data model"></span>
###

metadataEditors.run ['$templateCache', ($templateCache) ->
  $templateCache.put 'modelcatalogue/core/ui/detailSections/dataModelBasic.html', '''
      <div class="col-md-6">
        <div class="row">
          <div class="col-md-6"><strong class="small">Authors</strong></div>
          <div class="col-md-6"><small editable-text="extAsMap['http://www.modelcatalogue.org/metadata/#authors']">{{element.ext.get('http://www.modelcatalogue.org/metadata/#authors') || 'empty'}}</small></div>
        </div>
        <div class="row">
          <div class="col-md-6"><strong class="small">Reviewers</strong></div>
          <div class="col-md-6"><small editable-text="extAsMap['http://www.modelcatalogue.org/metadata/#reviewers']">{{element.ext.get('http://www.modelcatalogue.org/metadata/#reviewers') || 'empty'}}</small></div>
        </div>
        <div class="row">
          <div class="col-md-6"><strong class="small">Owner</strong></div>
          <div class="col-md-6"><small editable-text="extAsMap['http://www.modelcatalogue.org/metadata/#owner']">{{element.ext.get('http://www.modelcatalogue.org/metadata/#owner') || 'empty'}}</small></div>
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

  $templateCache.put 'modelcatalogue/core/ui/detailSections/assetBasic.html', '''
      <div class="col-md-6">
        <div class="row">
          <div class="col-md-6"><strong class="small">Original File Name</strong></div>
          <div class="col-md-6"><small>{{element.fileName || 'none'}}</small></div>
        </div>
        <div class="row">
          <div class="col-md-6"><strong class="small">Content Type</strong></div>
          <div class="col-md-6"><small>{{element.contentType || 'unknown'}}</small></div>
        </div>
        <div class="row">
          <div class="col-md-6"><strong class="small">Size</strong></div>
          <div class="col-md-6"><small>{{view.toHumanReadableSize(element.size || 0)}}</small></div>
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

  $templateCache.put 'modelcatalogue/core/ui/detailSections/organization.html', '''
      <div class="col-md-3">
          <strong class="small">Organization</strong>
      </div>
      <div class="col-md-9"><small editable-text="extAsMap['http://www.modelcatalogue.org/metadata/#organization']">{{element.ext.get('http://www.modelcatalogue.org/metadata/#organization') || 'empty'}}</small></div>
      <div class="col-md-3">
          <strong class="small">Namespace</strong>
      </div>
      <div class="col-md-9"><small editable-text="extAsMap['http://www.modelcatalogue.org/metadata/#namespace']">{{element.ext.get('http://www.modelcatalogue.org/metadata/#namespace') || 'empty'}}</small></div>
  '''

  $templateCache.put 'modelcatalogue/core/ui/detailSections/internalId.html', '''
      <div class="col-md-3">
          <strong class="small">Model Catalogue ID</strong>
      </div>
      <div class="col-md-9"><small>{{element.internalModelCatalogueId}}</small></div>
  '''

  $templateCache.put 'modelcatalogue/core/ui/detailSections/customMetadata.html', '''
      <div class="col-md-3" ng-repeat-start="value in (editableForm.$visible ? [] : customMetadata.values)">
          <strong class="small">{{value.key}}</strong>
      </div>
      <div class="col-md-9 preserve-new-lines" ng-repeat-end><small>{{value.value}}</small></div>
      <div class="custom-metadata col-md-12" ng-if="editableForm.$visible">
          <ordered-map-editor object="customMetadata"></ordered-map-editor>
      </div>
  '''

  $templateCache.put 'modelcatalogue/core/ui/detailSections/enumerations.html', '''
      <div class="col-md-12" ng-if="!editableForm.$visible &amp;&amp; element.enumerations.values"><strong class="small">Enumerations</strong></div>
      <div class="col-md-3" ng-repeat-start="value in (editableForm.$visible ? [] : element.enumerations.values)">
          <strong class="small">{{value.key}}</strong>
      </div>
      <div class="col-md-9 preserve-new-lines" ng-repeat-end><small>{{value.value}}</small></div>
      <div class="custom-metadata col-md-12" ng-if="editableForm.$visible">
          <ordered-map-editor object="copy.enumerations" title="Enumerations" key-placeholder="Value" value-placeholder="Description"></ordered-map-editor>
      </div>
  '''


  $templateCache.put 'modelcatalogue/core/ui/detailSections/revisionNotes.html', '''
      <div class="col-md-3">
          <strong class="small">Revision Notes</strong>
      </div>
      <div class="col-md-9 preserve-new-lines"><small editable-textarea="copy.revisionNotes" e-rows="5" e-cols="1000">{{element.revisionNotes || 'empty'}}</small></div>
  '''


  $templateCache.put 'modelcatalogue/core/ui/detailSections/description.html', '''
      <div class="col-md-3">
          <strong class="small">Description</strong>
      </div>
      <div class="col-md-9 preserve-new-lines"><small editable-textarea="copy.description" e-rows="5" e-cols="1000" class="ce-description">{{element.description || 'empty'}}</small></div>
  '''

  $templateCache.put 'modelcatalogue/core/ui/detailSections/assetPreview.html', '''
      <div class="col-md-12">
          <img style="max-width: 100%" ng-src="{{element.downloadUrl}}" ng-if="element.contentType.indexOf('image/') == 0"/>
          <div ng-if="element.htmlPreview" ng-bind-html="element.htmlPreview"></div>
          <pre ng-if="element.contentType.indexOf('image/') != 0 &amp;&amp; !element.htmlPreview" class="text-center">No preview available</pre>
      </div>
  '''
]

metadataEditors.config ['detailSectionsProvider', (detailSectionsProvider)->
  detailSectionsProvider.register {
      title: 'Model Catalogue ID'
      position: -100000
      types: [
        'catalogueElement'
      ]
      keys: []
      template: 'modelcatalogue/core/ui/detailSections/internalId.html'
  }

  detailSectionsProvider.register {
     title: 'Basic'
     position: -10000
     types: [
       'dataModel'
     ]
     keys: [
       'http://www.modelcatalogue.org/metadata/#authors'
       'http://www.modelcatalogue.org/metadata/#reviewers'
       'http://www.modelcatalogue.org/metadata/#owner'

     ]
     template: 'modelcatalogue/core/ui/detailSections/dataModelBasic.html'
  }


  detailSectionsProvider.register {
     title: 'Basic'
     position: -10000
     types: [
       'asset'
     ]
     keys: []
     template: 'modelcatalogue/core/ui/detailSections/assetBasic.html'
     toHumanReadableSize: (size) ->
        GIGA = 1024 * 1024 * 1024
        MEGA = 1024 * 1024
        KILO = 1024
        return "#{(size / GIGA).toFixed(2)} GB" if size > GIGA
        return "#{(size / MEGA).toFixed(2)} MB" if size > MEGA
        return "#{(size / KILO).toFixed(2)} kB" if size > KILO
        return "#{size} B"

  }



  detailSectionsProvider.register {
     title: 'Namespace and Organization'
     position: 2000
     types: [
       'dataModel'
     ]
     keys: [
       'http://www.modelcatalogue.org/metadata/#namespace'
       'http://www.modelcatalogue.org/metadata/#organization'
     ]
     template: 'modelcatalogue/core/ui/detailSections/organization.html'
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

  detailSectionsProvider.register {
    title: 'Revision Notes'
    position: 1000
    types: [
      'dataModel'
    ]
    keys: []
    template: 'modelcatalogue/core/ui/detailSections/revisionNotes.html'
  }

  detailSectionsProvider.register {
    title: 'Preview'
    position: 1000
    types: [
      'asset'
    ]
    keys: []
    template: 'modelcatalogue/core/ui/detailSections/assetPreview.html'
  }


  detailSectionsProvider.register {
    title: 'Custom Metadata'
    position: 100000
    types: [
      'dataModel'
      'asset'
    ]
    keys: []
    template: 'modelcatalogue/core/ui/detailSections/customMetadata.html'
  }


  detailSectionsProvider.register {
    title: 'Enumerations'
    position: 1000
    types: [
      'enumeratedType'
    ]
    keys: []
    template: 'modelcatalogue/core/ui/detailSections/enumerations.html'
  }

]