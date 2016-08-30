metadataEditors = angular.module('mc.core.ui.bs.detailSections', ['mc.core.ui.detailSections'])


###
  If you need to put more explanation for certain title put following snippet after the label's strong element and
  update it to your own help text.

  <span class="fa fa-question-circle text-muted" tooltip="These are the authors of the data model"></span>
###

metadataEditors.config ['detailSectionsProvider', (detailSectionsProvider)->
  REGEX_EXAMPLE = """// value is decimal number
x ==~ /\\d+(\\.\\d+)?/
"""
  SET_EXAMPLE = """// value is one of predefined values
x in ['apple', 'banana', 'cherry']
"""

  LIST_MAX = 10

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
    template: '/mc/core/ui/detail-sections/description.html'
  }

  detailSectionsProvider.register {
    title: 'Enumerations'
    position: -40
    types: [
      'enumeratedType'
    ]
    keys: ['http://www.modelcatalogue.org/metadata/enumerateType#subset']
    template: '/mc/core/ui/detail-sections/enumerations.html'
    setDeprecated: (enumeration, deprecated, globalMessages, element, rest, modelCatalogueApiRoot) ->
      title = "Do you want to mark enumeration #{enumeration.key} as deprecated?"
      message = "The enumeration #{enumeration.key} will be marked as deprecated."
      if enumeration.deprecated
        title = "Do you want to restore enumeration #{enumeration.key}?"
        message = "The enumeration #{enumeration.key} will no longer be deprecated."
      globalMessages.confirm(title, message).then ->
        rest(url: "#{modelCatalogueApiRoot}#{element.link}/setDeprecated", method: 'POST', data: {enumerationId: enumeration.id, deprecated: deprecated}).then () ->
          enumeration.deprecated = !enumeration.deprecated
          globalMessages.success "Enumeration #{enumeration.key} has been #{if deprecated then 'deprecated' else 'restored'}."
        , (result) ->
          globalMessages.error "Error during setting deprecated."
  }

  detailSectionsProvider.register {
    title: 'Rule'
    position: -30
    types: [
      'dataType'
    ]
    keys: []
    template: '/mc/core/ui/detail-sections/rule.html'

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
    template: '/mc/core/ui/detail-sections/dataType.html'
    autoSave:
      dataType: 'dataType'
  }

  detailSectionsProvider.register {
    title: 'Model Catalogue ID'
    position: -10
    types: [
      'catalogueElement'
    ]
    keys: []
    template: '/mc/core/ui/detail-sections/modelCatalogueId.html'
  }

  detailSectionsProvider.register {
    title: 'Policies'
    position: -10000
    types: [
      'dataModel'
    ]
    keys: []
    template: '/mc/core/ui/detail-sections/dataModelPolicies.html'
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
      'http://www.modelcatalogue.org/metadata/#reviewed'
      'http://www.modelcatalogue.org/metadata/#approved'
      'http://www.modelcatalogue.org/metadata/#released'
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
      'validationRule'
    ]
    keys: []
    template: '/mc/core/ui/detail-sections/validationRuleBasic.html'
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
    template: '/mc/core/ui/detail-sections/measurementUnit.html'
    autoSave:
      measurementUnit: 'measurementUnit'
  }

  detailSectionsProvider.register {
    title: 'Data Class'
    position: 10
    types: [
      'referenceType'
    ]
    keys: []
    template: '/mc/core/ui/detail-sections/dataClass.html'
    autoSave:
      dataClass: 'dataClass'
  }

  detailSectionsProvider.register {
    title: 'Preview'
    position: 20
    types: [
      'asset'
    ]
    keys: []
    template: '/mc/core/ui/detail-sections/assetPreview.html'
  }

  detailSectionsProvider.register {
    title: 'Revision Notes'
    position: 20
    types: [
      'dataModel'
    ]
    keys: []
    template: '/mc/core/ui/detail-sections/revisionNotes.html'
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
     template: '/mc/core/ui/detail-sections/organization.html'
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
      'validationRule'
    ]
    keys: []
    template: '/mc/core/ui/detail-sections/customMetadata.html'
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

      element.contains(null, max: LIST_MAX).then (list) =>
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

      element.parentOf(null, max: LIST_MAX).then (list) =>
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

  detailSectionsProvider.register {
    title: 'Inherited Rules'
    position: -29
    types: [
      'dataType'
    ]
    keys: []
    template: '/mc/core/ui/detail-sections/tableData.html'
    getList: (element) ->
      return @result if @result

      @result =
        base: element.typeHierarchy.base
        itemType: element.typeHierarchy.itemType

      element.typeHierarchy(null, max: LIST_MAX).then (list) =>
        @result = list
      return @result
    data: {
      columns:
        [
          {
            header: 'Name',
            value: "name",
            classes: 'col-md-3',
            href: 'href()'
          }
          {
            header: 'Rule',
            value: "rule",
            classes: 'col-md-9 code',
          }

        ]
    }
  }

  detailSectionsProvider.register {
    title: 'Context Data Classes'
    position: 60
    types: [
      'validationRule'
    ]
    keys: []
    template: '/mc/core/ui/detail-sections/tableData.html'
    getList: (element) ->
      return @result if @result

      @result =
        base: element.appliedWithin.base
        itemType: element.appliedWithin.itemType

      element.appliedWithin(null, max: LIST_MAX).then (list) =>
        @result = list
      return @result
    reorder: reorderInDetail('appliedWithin')
    data: {
      columns:
        [
          {
            header: 'Name',
            value: "ext.get('name') || ext.get('Name') || relation.name ",
            classes: 'col-md-6',
            href: 'relation.href()'
          }
          {
            header: 'Description',
            value: "relation.description",
            classes: 'col-md-6',
          }

        ]
    }
  }

  detailSectionsProvider.register {
    title: 'Involved Data Elements'
    position: 70
    types: [
      'validationRule'
    ]
    keys: []
    template: '/mc/core/ui/detail-sections/tableData.html'
    getList: (element) ->
      return @result if @result

      @result =
        base: element.involves.base
        itemType: element.involves.itemType

      element.involves(null, max: LIST_MAX).then (list) =>
        @result = list
      return @result
    reorder: reorderInDetail('involves')
    data: {
      columns:
        [
          {
            header: 'Name',
            value: "ext.get('name') || ext.get('Name') || relation.name ",
            classes: 'col-md-6',
            href: 'relation.href()'
          }
          {
            header: 'Description',
            value: "relation.description",
            classes: 'col-md-6',
          }

        ]
    }
  }
]
