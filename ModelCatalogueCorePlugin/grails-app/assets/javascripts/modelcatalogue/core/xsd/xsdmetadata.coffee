window.modelcatalogue.registerModule 'mc.core.xsd'

xsd = angular.module('mc.core.xsd', ['mc.core.ui.metadataEditors'])

# TODO: inline help
xsd.config ['metadataEditorsProvider', (metadataEditorsProvider)->
  metadataEditorsProvider.register {
    title: 'XSD (Metadata)'
    types: [
      'model'
      '=[hierarchy]=>'
    ]
    keys: [
      "http://xsd.modelcatalogue.org/metadata#schemaName"
      "http://xsd.modelcatalogue.org/metadata#schemaVersion"
      "http://xsd.modelcatalogue.org/metadata#schemaVersionDescription"
      "http://xsd.modelcatalogue.org/section#type"
    ]
    template: 'modelcatalogue/core/ui/metadataEditors/xsdMetadata.html'
  }
]
xsd.run ['$templateCache', ($templateCache) ->
 $templateCache.put 'modelcatalogue/core/ui/metadataEditors/xsdMetadata.html', '''
  <div class="alert alert-warning">This XSD metadata only applies on the root model of the Xml Schema.Every GEL Schema should have included these mandatory fields</div>
  <form class="form">
      <div class="form-group">
          <label for="form-name" class="control-label">XML Schema Name</label>
          <input maxlength="50" type="text" class="form-control" id="form-name" ng-model="object.access('http://xsd.modelcatalogue.org/metadata#schemaName')" ng-model-options="{ getterSetter: true }">
              <p class="help-block">
                  XML Schema Name. Limit to maximum 50 chars. Attention! use lower case without without spaces or unaccepted chars for an xsd element name 
              </p>
      </div>
      <div class="form-group">
          <label for="form-version" class="control-label">Version</label>
          <input maxlength="20" type="text" class="form-control" id="form-version" ng-model="object.access('http://xsd.modelcatalogue.org/metadata#schemaVersion')" ng-model-options="{ getterSetter: true }">
              <p class="help-block">
                  This define the schema version.Must be a number in format X.X.X 
              </p>
      </div>
      <div class="form-group">
          <label for="form-version-description" class="control-label">Version Description</label>
          <textarea maxlength="4000" rows="5" class="form-control" id="form-version-description" ng-model="object.access('http://xsd.modelcatalogue.org/metadata#schemaVersionDescription')" ng-model-options="{ getterSetter: true }"></textarea>
          <p class="help-block">
             A specific xsd version description with most important changes. max 4000 chars. 
          </p>
      </div> 
      <div class="form-group">
          <label for="form-version-description" class="control-label">Section type</label>
          <input maxlength="4000" rows="5" class="form-control" id="form-version-description" ng-model="object.access('http://xsd.modelcatalogue.org/section#type')" ng-model-options="{ getterSetter: true }">
          </choice>
          <p class="help-block">
             Accepted values:'choice' Whether a section should be  sequence or a choice in a ComplexType. For the moment only word 'choice' it's recognized. Totally optional default will be sequence. 
          </p>
      </div> 
      </form> 
    '''
]
