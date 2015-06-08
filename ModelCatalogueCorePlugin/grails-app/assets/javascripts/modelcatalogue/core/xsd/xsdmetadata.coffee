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

  metadataEditorsProvider.register {
    title: 'XSD( Datatype Restrictions)'
    types: [
      'dataElement'   
      'valueDomain'
      '=[containment]=>'
    ]

    keys: [
           "http://xsd.modelcatalogue.org/restrictions#length"
           "http://xsd.modelcatalogue.org/restrictions#minLength"
           "http://xsd.modelcatalogue.org/restrictions#maxLength"
           "http://xsd.modelcatalogue.org/restrictions#maxInclusive"
           "http://xsd.modelcatalogue.org/restrictions#minInclusive"
           "http://xsd.modelcatalogue.org/restrictions#maxExclusive"
           "http://xsd.modelcatalogue.org/restrictions#minExclusive"
           "http://xsd.modelcatalogue.org/restrictions#totalDigits"
           "http://xsd.modelcatalogue.org/restrictions#fractionDigits"
           "http://xsd.modelcatalogue.org/restrictions#pattern"
    ]
    template: 'modelcatalogue/core/ui/metadataEditors/xsdItemValueDomainRestriction.html'
  }
]

xsd.run ['$templateCache', ($templateCache) ->
 $templateCache.put 'modelcatalogue/core/ui/metadataEditors/xsdMetadata.html', '''
  <div class="alert alert-warning">This XSD metadata only applies on the root model of the Xml Schema.Every GEL Schema should have included these mandatory fields</div>
  <form class="form">
      <div class="form-group">
          <label for="schema-name" class="control-label">XML Schema Name</label>
          <input maxlength="50" type="text" class="form-control" id="schema-name" ng-model="object.access('http://xsd.modelcatalogue.org/metadata#schemaName')" ng-model-options="{ getterSetter: true }">
              <p class="help-block">
                  XML Schema Name. Limit to maximum 50 chars. Attention! use lower case without without spaces or unaccepted chars for an xsd element name 
              </p>
      </div>
      <div class="form-group">
          <label for="schema-version" class="control-label">Version</label>
          <input maxlength="20" type="text" class="form-control" id="schema-version" ng-model="object.access('http://xsd.modelcatalogue.org/metadata#schemaVersion')" ng-model-options="{ getterSetter: true }">
              <p class="help-block">
                  This define the schema version.Must be a number in format X.X.X 
              </p>
      </div>
      <div class="form-group">
          <label for="schema-version-description" class="control-label">Version Description</label>
          <textarea maxlength="4000" rows="5" class="form-control" id="schema-version-description" ng-model="object.access('http://xsd.modelcatalogue.org/metadata#schemaVersionDescription')" ng-model-options="{ getterSetter: true }"></textarea>
          <p class="help-block">
             A specific xsd version description with most important changes. max 4000 chars.
          </p>
      </div> 
      <div class="form-group">
          <label for="section-type" class="control-label">Section type</label>
          <input type="text" maxlength="10" rows="5" class="form-control" id="section-type" ng-model="object.access('http://xsd.modelcatalogue.org/section#type')" ng-model-options="{ getterSetter: true }">
          </choice>
          <p class="help-block">
             Accepted values:'choice' Whether a section should be  sequence or a choice in a ComplexType. For the moment only word 'choice' it's recognized. Totally optional default will be sequence. 
          </p>
      </div> 
      </form> 
    '''
 $templateCache.put 'modelcatalogue/core/ui/metadataEditors/xsdItemValueDomainRestriction.html', '''
      <form class="form">
        <div class="form-group">
          <label for="xsd-restriction-pattern" class="control-label">Pattern (Valid XML Regex)</label>
          <input maxlength="500" type="text" class="form-control" id="xsd-restriction-pattern" ng-model="object.access('http://xsd.modelcatalogue.org/restrictions#pattern')" ng-model-options="{ getterSetter: true }">
          <p class="help-block">
             Defines the exact sequence of characters that are acceptable
          </p>
        </div>
        <div class="form-group">
        <label for="xsd-restriction-length" class="control-label">Length</label>
        <input maxlength="100" type="text" class="form-control" id="xsd-restriction-length" ng-model="object.access('http://xsd.modelcatalogue.org/restrictions#length')" ng-model-options="{ getterSetter: true }">
        <p class="help-block">
            Specifies the exact number of characters or list items allowed. Must be equal to or greater than zero
        </p>
      </div> 
      <div class="form-group">
      <label for="xsd-restriction-maxlength" class="control-label">Max Length</label>
      <input maxlength="1000" type="text" class="form-control" id="xsd-restriction-maxlength" ng-model="object.access('http://xsd.modelcatalogue.org/restrictions#maxLength')" ng-model-options="{ getterSetter: true }">
      <p class="help-block">
         Specifies the maximum number of characters or list items allowed. Must be equal to or greater than zero
      </p>
     </div>
     <div class="form-group">
     <label for="xsd-restriction-minlength" class="control-label">Min Length</label>
     <input maxlength="1000" type="text" class="form-control" id="xsd-restriction-minlength" ng-model="object.access('http://xsd.modelcatalogue.org/restrictions#minLength')" ng-model-options="{ getterSetter: true }">
     <p class="help-block">
        Specifies the minimum number of characters or list items allowed. Must be equal to or greater than zero
     </p>
    </div>
    <div class="form-group">
    <label for="xsd-restriction-maxInclusive" class="control-label">Max Inclusive</label>
    <input maxlength="40" type="text" class="form-control" id="xsd-restriction-maxInclusive" ng-model="object.access('http://xsd.modelcatalogue.org/restrictions#maxInclusive')" ng-model-options="{ getterSetter: true }">
    <p class="help-block">
        Specifies the upper bounds for numeric values (the value must be less than or equal to this value)
    </p>
   </div>
   <div class="form-group">
   <label for="xsd-restriction-minInclusive" class="control-label">Min Inclusive</label>
   <input maxlength="40" type="text" class="form-control" id="xsd-restriction-minInclusive" ng-model="object.access('http://xsd.modelcatalogue.org/restrictions#minInclusive')" ng-model-options="{ getterSetter: true }">
   <p class="help-block">
       Specifies the lower bounds for numeric values (the value must be greater than or equal to this value)
   </p>
   </div>
   <div class="form-group">
       <label for="xsd-restriction-maxExclusive" class="control-label">Max Exclusive</label>
       <input maxlength="40" type="text" class="form-control" id="xsd-restriction-maxExclusive" ng-model="object.access('http://xsd.modelcatalogue.org/restrictions#maxExclusive')" ng-model-options="{ getterSetter: true }">
       <p class="help-block">
          Specifies the upper bounds for numeric values (the value must be less than this value)
       <p>
    </div>
    <div class="form-group">
    <label for="xsd-restriction-minExclusive" class="control-label">Min Exclusive</label>
    <input maxlength="40" type="text" class="form-control" id="xsd-restriction-minExclusive" ng-model="object.access('http://xsd.modelcatalogue.org/restrictions#minExclusive')" ng-model-options="{ getterSetter: true }">
    <p class="help-block">
        Specifies the lower bounds for numeric values (the value must be greater than this value)
    <p>
    </div>
    <div class="form-group">
    <label for="xsd-restriction-totalDigits" class="control-label">Total digits</label>
    <input maxlength="40" type="text" class="form-control" id="xsd-restriction-totalDigits" ng-model="object.access('http://xsd.modelcatalogue.org/restrictions#totalDigits')" ng-model-options="{ getterSetter: true }">
    <p class="help-block">
        Specifies the exact number of digits allowed. Must be greater than zero
    <p>
    </div>
    <div class="form-group">
    <label for="xsd-restriction-fractionDigits" class="control-label">Min Exclusive</label>
    <input maxlength="40" type="text" class="form-control" id="xsd-restriction-fractionDigits" ng-model="object.access('http://xsd.modelcatalogue.org/restrictions#fractionDigits')" ng-model-options="{ getterSetter: true }">
    <p class="help-block">
          Specifies the maximum number of decimal places allowed. Must be equal to or greater than zero
    <p>
    </div>
    
    </form>
    '''
]
