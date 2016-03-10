metadataEditors = angular.module('mc.core.ui.bs.detailSections', ['mc.core.ui.detailSections'])


###
  If you need to put more explanation for certain title put following snippet after the label's strong element and
  update it to your own help text.

  <span class="fa fa-question-circle text-muted" tooltip="These are the authors of the data model"></span>
###

metadataEditors.run ['$templateCache', ($templateCache) ->
  $templateCache.put 'modelcatalogue/core/ui/detailSections/organization.html', '''
      <div class="col-md-3">
          <strong class="small">Organization</strong>
      </div>
      <div class="full-width-editable col-md-9"><small editable-text="extAsMap['http://www.modelcatalogue.org/metadata/#organization']">{{element.ext.get('http://www.modelcatalogue.org/metadata/#organization') || 'empty'}}</small></div>
      <div class="col-md-3">
          <strong class="small">Namespace</strong>
      </div>
      <div class="full-width-editable col-md-9"><small editable-text="extAsMap['http://www.modelcatalogue.org/metadata/#namespace']">{{element.ext.get('http://www.modelcatalogue.org/metadata/#namespace') || 'empty'}}</small></div>
  '''

  $templateCache.put 'modelcatalogue/core/ui/detailSections/modelCatalogueId.html', '''
      <div class="col-md-3">
          <strong class="small">Model Catalogue ID</strong>
      </div>
      <div class="col-md-9"><small>{{element.internalModelCatalogueId}}</small></div>
      <div class="col-md-3" ng-if="element.modelCatalogueId &amp;&amp; element.modelCatalogueId != element.internalModelCatalogueId">
          <strong class="small">External ID (URL)</strong>
      </div>
      <div class="full-width-editable col-md-9" ng-if="element.modelCatalogueId &amp;&amp; element.modelCatalogueId != element.internalModelCatalogueId"><a class="small" ng-href="{{element.modelCatalogueId}}" editable-text="copy.modelCatalogueId">{{element.modelCatalogueId}}</a></div>
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
          <div ng-if="copy.ext.get('http://www.modelcatalogue.org/metadata/enumerateType#subset')" class="alert alert-warning">Following values are inherited and will be overriden when the base enumeration changes: {{copy.ext.get('http://www.modelcatalogue.org/metadata/enumerateType#subset')}}</div>
          <ordered-map-editor object="copy.enumerations" title="Enumerations" key-placeholder="Value" value-placeholder="Description"></ordered-map-editor>
      </div>
  '''


  $templateCache.put 'modelcatalogue/core/ui/detailSections/revisionNotes.html', '''
      <div class="col-md-3">
          <strong class="small">Revision Notes</strong>
      </div>
      <div class="full-width-editable col-md-9 preserve-new-lines"><small editable-textarea="copy.revisionNotes" e-rows="5" e-cols="1000">{{element.revisionNotes || 'empty'}}</small></div>
  '''


  $templateCache.put 'modelcatalogue/core/ui/detailSections/description.html', '''
      <div class="col-md-3">
          <strong class="small">Description</strong>
      </div>
      <div class="full-width-editable col-md-9 preserve-new-lines"><small editable-textarea="copy.description" e-rows="5" e-cols="1000" class="ce-description">{{element.description || 'empty'}}</small></div>
  '''

  $templateCache.put 'modelcatalogue/core/ui/detailSections/rule.html', '''
      <div class="col-md-3">
          <strong class="small">Rule</strong>
      </div>
      <div class="full-width-editable col-md-9 preserve-new-lines">
        <pre editable-textarea="copy.rule" e-rows="5" e-cols="1000" class="ce-rule small">{{element.rule || 'empty'}}</pre>
        <p ng-if="editableForm.$visible" class="help-block">Enter valid <a href="http://www.groovy-lang.org/" target="_blank">Groovy</a> code. Variable <code>x</code> refers to the value validated value and  <code>dataType</code> to current data type. Last row is the result which should be <code>boolean</code> value. For example you can <a ng-click="view.showRegexExample(copy, messages)"><span class="fa fa-magic"></span> validate using regular expression</a> or <a ng-click="view.showSetExample(copy, messages)"><span class="fa fa-magic"></span> values in set</a></p>
      </div>
  '''

  $templateCache.put 'modelcatalogue/core/ui/detailSections/measurementUnit.html', '''
      <div class="col-md-3">
          <strong class="small">Measurement Unit</strong>
      </div>
      <div class="full-width-editable col-md-9">
          <div ng-if="editableForm.$visible">
            <input type="text" id="measurementUnit" placeholder="Measurement Unit" ng-model="copy.measurementUnit" catalogue-element-picker="measurementUnit" label="el.name">
          </div>
          <span class="editable-empty" ng-if="!editableForm.$visible &amp;&amp; !element.measurementUnit">empty</span>
          <a ng-if="!editableForm.$visible &amp;&amp; element.measurementUnit" class="small with-pointer" ng-href="{{element.measurementUnit.href()}}">
            <span ng-class="element.measurementUnit.getIcon()">&nbsp;</span>
            <span class="unit-name">{{element.measurementUnit.name}}&nbsp;</span>
            <small>
              <a ng-if="!editableForm.$visible" ng-href="{{element.mesurementUnit.dataModel.href()}}" class="label" ng-class="{'label-warning': element.measurementUnit.getDataModelStatus() == 'DRAFT', 'label-info': element.measurementUnit.getDataModelStatus() == 'PENDING', 'label-primary': element.measurementUnit.getDataModelStatus() == 'FINALIZED', 'label-danger': element.measurementUnit.getDataModelStatus() == 'DEPRECATED'}">{{element.measurementUnit.getDataModelWithVersion()}}</a>
            </small>
          </a>

      </div>
  '''

  $templateCache.put 'modelcatalogue/core/ui/detailSections/dataClass.html', '''
      <div class="col-md-3">
          <strong class="small">Data Class</strong>
      </div>
      <div class="full-width-editable col-md-9">
          <div ng-if="editableForm.$visible">
            <input type="text" id="dataClass" placeholder="Data Class" ng-model="copy.dataClass" catalogue-element-picker="dataClass" label="el.name">
          </div>
          <span class="editable-empty" ng-if="!editableForm.$visible &amp;&amp; !element.dataClass">empty</span>
          <a ng-if="!editableForm.$visible &amp;&amp; element.dataClass" class="small with-pointer" ng-href="{{element.dataClass.href()}}">
            <span ng-class="element.dataClass.getIcon()">&nbsp;</span>
            <span class="unit-name">{{element.dataClass.name}}&nbsp;</span>
            <small>
              <a ng-if="!editableForm.$visible" ng-href="{{element.dataClass.dataModel.href()}}" class="label" ng-class="{'label-warning': element.dataClass.getDataModelStatus() == 'DRAFT', 'label-info': element.dataClass.getDataModelStatus() == 'PENDING', 'label-primary': element.dataClass.getDataModelStatus() == 'FINALIZED', 'label-danger': element.dataClass.getDataModelStatus() == 'DEPRECATED'}">{{element.dataClass.getDataModelWithVersion()}}</a>
            </small>
          </a>

      </div>
  '''

  $templateCache.put 'modelcatalogue/core/ui/detailSections/dataType.html', '''
      <div class="col-md-3">
          <strong class="small">Data Type</strong>
      </div>
      <div class="full-width-editable col-md-9">
          <div ng-if="editableForm.$visible">
            <input type="text" id="dataType" placeholder="Data Type" ng-model="copy.dataType" catalogue-element-picker="dataType" label="el.name">
          </div>
          <span class="editable-empty" ng-if="!editableForm.$visible &amp;&amp; !element.dataType">empty</span>
          <a ng-if="!editableForm.$visible &amp;&amp; element.dataType" class="small with-pointer" ng-href="{{element.dataType.href()}}">
            <span ng-class="element.dataType.getIcon()">&nbsp;</span>
            <span class="unit-name">{{element.dataType.name}}&nbsp;</span>
            <small>
              <a ng-if="!editableForm.$visible"  ng-href="{{element.dataType.dataModel.href()}}" class="label" ng-class="{'label-warning': element.dataType.getDataModelStatus() == 'DRAFT', 'label-info': element.dataType.getDataModelStatus() == 'PENDING', 'label-primary': element.dataType.getDataModelStatus() == 'FINALIZED', 'label-danger': element.dataType.getDataModelStatus() == 'DEPRECATED'}">{{element.dataType.getDataModelWithVersion()}}</a>
            </small>
          </a>

      </div>
      <div class="small col-md-9 col-md-offset-3" ng-if="!editableForm.$visible &amp;&amp; element.dataType.measurementUnit">
          uses <a class="small with-pointer" ng-href="{{element.dataType.measurementUnit.href()}}">
                <span ng-class="element.dataType.measurementUnit.getIcon()">&nbsp;</span>
                <span class="unit-name">{{element.dataType.measurementUnit.name}}&nbsp;</span>
                <small>
                  <a ng-href="{{element.dataType.measurementUnit.dataModel.href()}}" class="label" ng-class="{'label-warning': element.dataType.measurementUnit.getDataModelStatus() == 'DRAFT', 'label-info': element.dataType.measurementUnit.getDataModelStatus() == 'PENDING', 'label-primary': element.dataType.measurementUnit.getDataModelStatus() == 'FINALIZED', 'label-danger': element.dataType.measurementUnit.getDataModelStatus() == 'DEPRECATED'}">{{element.dataType.measurementUnit.getDataModelWithVersion()}}</a>
                </small>
          </a>
      </div>
      <div class="small col-md-9 col-md-offset-3" ng-if="!editableForm.$visible &amp;&amp; element.dataType.dataClass">
          refers to <a class="small with-pointer" ng-href="{{element.dataType.dataClass.href()}}">
                <span ng-class="element.dataType.dataClass.getIcon()">&nbsp;</span>
                <span class="unit-name">{{element.dataType.dataClass.name}}&nbsp;</span>
                <small>
                  <a ng-href="{{element.dataType.dataClass.dataModel.href()}}" class="label" ng-class="{'label-warning': element.dataType.dataClass.getDataModelStatus() == 'DRAFT', 'label-info': element.dataType.dataClass.getDataModelStatus() == 'PENDING', 'label-primary': element.dataType.dataClass.getDataModelStatus() == 'FINALIZED', 'label-danger': element.dataType.dataClass.getDataModelStatus() == 'DEPRECATED'}">{{element.dataType.dataClass.getDataModelWithVersion()}}</a>
                </small>
          </a>
      </div>
      <div class="small col-md-9 col-md-offset-3" ng-if="!editableForm.$visible &amp;&amp; element.dataType.enumerations">
          <div class="row">
            <div class="col-md-12" ><strong class="small">Enumerations</strong></div>
            <div class="col-md-3" ng-repeat-start="value in element.dataType.enumerations.values">
              <strong class="small">{{value.key}}</strong>
            </div>
            <div class="col-md-9 preserve-new-lines" ng-repeat-end><small>{{value.value}}</small></div>
          </div>
      </div>
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
  REGEX_EXAMPLE = """// value is decimal number
x ==~ /\\d+(\\.\\d+)?/
"""
  SET_EXAMPLE = """// value is one of predefined values
x in ['apple', 'banana', 'cherry']
"""

  showExample = (copy, messages, example) ->
    if copy.rule and copy.rule != REGEX_EXAMPLE and copy.rule != SET_EXAMPLE
      messages.confirm("Replace current rule with example", "Do already have some rule, do you want to replace it with the example?").then ->
        copy.rule = example
    else
      copy.rule = example

  printDataType = (relationship) ->
    result  = ''
    dataType = relationship?.relation?.dataType
    if dataType?.enumerations?.values
      ext     = dataType?.enumerations?.values ? []
      for e, i in ext
        if i == 10
          result += "..."
          break
        result += "#{e.key} \n"
    if dataType?.dataClass
      result = """<a href="#{dataType.dataClass.modelCatalogueId}"><span class="fa fa-fw fa-cubes"></span>#{dataType.dataClass.name}</a>"""
    else if dataType
      result = dataType?.name
    result

  printMetadataOccurrencesOnly = (relationship) ->
    result  = ''
    ext = relationship?.ext ? {values: []}
    otherMetadataPresen = false
    for row in ext.values
      if (row.key == 'Min Occurs' || row.key == 'Max Occurs')
        result += "#{row.key}: #{row.value ? ''}\n"
      else
        otherMetadataPresen = true

    return result

  detailSectionsProvider.register {
    title: 'Description'
    position: -50
    types: [
      'catalogueElement'
    ]
    keys: []
    template: 'modelcatalogue/core/ui/detailSections/description.html'
  }

  detailSectionsProvider.register {
    title: 'Enumerations'
    position: -40
    types: [
      'enumeratedType'
    ]
    keys: ['http://www.modelcatalogue.org/metadata/enumerateType#subset']
    template: 'modelcatalogue/core/ui/detailSections/enumerations.html'
  }

  detailSectionsProvider.register {
    title: 'Rule'
    position: -30
    types: [
      'dataType'
    ]
    keys: []
    template: 'modelcatalogue/core/ui/detailSections/rule.html'

    showRegexExample: (copy, messages) -> showExample(copy, messages, REGEX_EXAMPLE)
    showSetExample: (copy, messages) -> showExample(copy, messages, SET_EXAMPLE)
  }

  detailSectionsProvider.register {
    title: 'Data Type'
    position: -30
    types: [
      'dataElement'
    ]
    keys: []
    template: 'modelcatalogue/core/ui/detailSections/dataType.html'
  }

  detailSectionsProvider.register {
    title: 'Model Catalogue ID'
    position: -10
    types: [
      'catalogueElement'
    ]
    keys: []
    template: 'modelcatalogue/core/ui/detailSections/modelCatalogueId.html'
  }

  detailSectionsProvider.register {
    title: 'Basic'
    position: 0
    types: [
      'dataModel'
    ]
    keys: [
      'http://www.modelcatalogue.org/metadata/#authors'
      'http://www.modelcatalogue.org/metadata/#reviewers'
      'http://www.modelcatalogue.org/metadata/#owner'

    ]
    template: '/mc/core/ui/detail-sections/dataModelBasic.html'
  }

  detailSectionsProvider.register {
    title: 'Basic'
    position: 0
    types: [
      'measurementUnit'
    ]
    keys: []
    template: '/mc/core/ui/detail-sections/measurementUnitBasic.html'
  }

  detailSectionsProvider.register {
    title: 'Basic'
    position: 0
    types: [
      'asset'
    ]
    keys: []
    template: '/mc/core/ui/detail-sections/assetBasic.html'
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
    title: 'Basic'
    position: 0
    types: [
      'dataClass'
      'dataElement'
    ]
    keys: []
    template: '/mc/core/ui/detail-sections/basic.html'
  }

  detailSectionsProvider.register {
    title: 'Measurement Unit'
    position: 10
    types: [
      'primitiveType'
    ]
    keys: []
    template: 'modelcatalogue/core/ui/detailSections/measurementUnit.html'
  }

  detailSectionsProvider.register {
    title: 'Data Class'
    position: 10
    types: [
      'referenceType'
    ]
    keys: []
    template: 'modelcatalogue/core/ui/detailSections/dataClass.html'
  }

  detailSectionsProvider.register {
    title: 'Preview'
    position: 20
    types: [
      'asset'
    ]
    keys: []
    template: 'modelcatalogue/core/ui/detailSections/assetPreview.html'
  }

  detailSectionsProvider.register {
    title: 'Revision Notes'
    position: 20
    types: [
      'dataModel'
    ]
    keys: []
    template: 'modelcatalogue/core/ui/detailSections/revisionNotes.html'
  }

  detailSectionsProvider.register {
     title: 'Namespace and Organization'
     position: 30
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
    title: 'Custom Metadata'
    position: 40
    types: [
      'dataModel'
      'asset'
      'mesurementUnit'
      'dataElement'
      'dataType'
      'dataClass'
    ]
    keys: []
    template: 'modelcatalogue/core/ui/detailSections/customMetadata.html'
  }

  reorderInDetail = (relationName) ->
    (element, messages, $row, $current) ->
      element[relationName].reorder($row.row.element, $current?.row?.element).catch (reason) ->
        messages.error reason

  detailSectionsProvider.register {
    title: 'Data Elements'
    position: 60
    types: [
      'dataClass'
    ]
    keys: []
    template: '/mc/core/ui/detail-sections/tableData.html'
    getList: (element) ->
      return @result if @result

      @result =
          base: element.contains.base
          itemType: element.contains.itemType

      element.contains().then (list) =>
        @result = list
      return @result
    reorder: reorderInDetail('contains')

    data: {
      columns:
        [
          {header: 'Name', value: "relation.name", classes: 'col-md-3', href: 'relation.href()'}
          {header: "Description", value: "relation.description" , classes: "col-md-5"}
          {header: "Data Type", value: printDataType, classes: "col-md-3", href: 'href()'}
          {header: 'Occurs',  value: printMetadataOccurrencesOnly, classes: 'col-md-2'}
        ]
    }
  }

  detailSectionsProvider.register {
    title: 'Children'
    position: 70
    types: [
      'dataClass'
    ]
    keys: []
    template: '/mc/core/ui/detail-sections/tableData.html'
    getList: (element) ->
      return @result if @result

      @result =
        base: element.parentOf.base
        itemType: element.parentOf.itemType

      element.parentOf().then (list) =>
        @result = list
      return @result
    reorder: reorderInDetail('parentOf')
    data: {
      columns:
        [
          {
            header: 'Name',
            value: "ext.get('name') || ext.get('Name') || relation.name ",
            classes: 'col-md-5',
            href: 'relation.href()',
            href: 'relation.href()'
          }
          {
            header: 'Identification',
            value: "relation.getElementTypeName() + ': ' + relation.id",
            classes: 'col-md-5',
            href: 'relation.href()'
          }
          {header: 'Description', value: "relation.description", classes: 'col-md-4'}
          {header: 'Occurs', value: printMetadataOccurrencesOnly, classes: 'col-md-4'}
        ]
    }
  }
]
