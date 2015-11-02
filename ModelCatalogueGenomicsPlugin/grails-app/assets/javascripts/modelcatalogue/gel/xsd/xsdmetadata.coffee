window.modelcatalogue.registerModule 'mc.core.xsd'

xsd = angular.module('mc.core.xsd', ['mc.core.ui.metadataEditors'])

# TODO: inline help
xsd.config ['metadataEditorsProvider', (metadataEditorsProvider)->
  metadataEditorsProvider.register {
    title: 'XSD-XSL(Metadata)'
    types: [
      'dataClass'
      '=[hierarchy]=>'
    ]
    keys: [
      "http://xsd.modelcatalogue.org/metadata#schemaName"
      "http://xsd.modelcatalogue.org/metadata#schemaVersion"
      "http://xsd.modelcatalogue.org/metadata#schemaVersionDescription"
      "http://xsd.modelcatalogue.org/section#type"
      "http://xsl.modelcatalogue.org/tableName"
    ]
    template: 'modelcatalogue/core/ui/metadataEditors/xsdMetadata.html'
  }

  metadataEditorsProvider.register {
    title: 'XSD-XSL( Datatype Restrictions)'
    types: [
      'dataElement'   
      'dataType'
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
           "http://xsl.modelcatalogue.org/tableName"
    ]
    template: 'modelcatalogue/core/ui/metadataEditors/xsdItemDataTypeRestriction.html'
  }
  metadataEditorsProvider.register {
    title: 'Occurences(Metadata)'
    types: [
          'dataElement'   
          'dataType'
          '=[containment]=>'
          'dataClass'
          '=[hierarchy]=>'
        ]

    keys: [
               "Min Occurs"
               "Max Occurs"
    ]
    template: 'modelcatalogue/core/ui/metadataEditors/metadataOccurence.html'
  }
]

xsd.run ['$templateCache', ($templateCache) ->
 $templateCache.put 'modelcatalogue/core/ui/metadataEditors/xsdMetadata.html', '''
  <div class="alert alert-warning">This XSD metadata only applies on the root model of the Xml Schema.Every GEL Schema should have included these mandatory fields</div>
  <form class="form">
      <div class="form-group">
          <label for="xsl-table-name1" class="control-label">Table Name(XSL)</label>
          <input maxlength="63" type="text" class="form-control" id="xsl-table-name1" ng-model="object.access('http://xsl.modelcatalogue.org/tableName')" ng-model-options="{ getterSetter: true }">
          <p class="help-block">
             Optional field used for database.If you don't specify the name here,it will be taken from the name of the current item.  Only if it's a section or occurrence &gt;1. Field used for XSL data model. It will be used only for sections,for items which are repeatable. Table name must be unique across the form.
          </p>
      </div>
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
          <select id="section-type" class="form-control" ng-options="key for (key, value) in {'Sequence':'sequence', 'Choice': 'choice',}" ng-model="object.access('http://xsd.modelcatalogue.org/section#type')" ng-model-options="{ getterSetter: true }"></select>
          <p class="help-block">
             Whether a section should be  sequence or a choice in a ComplexType.Totally optional default will be sequence. 
          </p>
      </div> 
      </form> 
    '''
 $templateCache.put 'modelcatalogue/core/ui/metadataEditors/xsdItemDataTypeRestriction.html', '''
      <form class="form">
        <div class="form-group">
          <label for="xsl-table-name" class="control-label">XSL table Name</label>
          <input maxlength="63" type="text" class="form-control" id="xsl-table-name" ng-model="object.access('http://xsl.modelcatalogue.org/tableName')" ng-model-options="{ getterSetter: true }">
          <p class="help-block">
             Only if it's a section or occurrence &gt;1. Field used for XSL data model. It will be used only for sections,for items which are repeatable. Table name must be unique across the form.
          </p>
        </div>
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
 $templateCache.put 'modelcatalogue/core/ui/metadataEditors/metadataOccurence.html', '''
    <div class="alert alert-warning">Metadata occurences </div>
    <form class="form">
        <div class="form-group">
            <label for="minOccurence" class="control-label">Min Occurs</label>
            <input maxlength="50" type="text" class="form-control" id="minOccurence" ng-model="object.access('Min Occurs')" ng-model-options="{ getterSetter: true }">
                <p class="help-block">
                    Min occurence of the current element within the block. A number between 0 and infinite number. Must be a number otherwise deleted.
                </p>
        </div>
        <div class="form-group">
            <label for="maxOccurence" class="control-label">Max Occurs</label>
            <input maxlength="20" type="text" class="form-control" id="maxOccurence" ng-model="object.access('Max Occurs')" ng-model-options="{ getterSetter: true }">
                <p class="help-block">
                Max occurence of the current element within the block. A number between 0 and infinite number. Must be a number otherwise deleted.
                </p>
        </div>
        </form> 
    '''    
]
