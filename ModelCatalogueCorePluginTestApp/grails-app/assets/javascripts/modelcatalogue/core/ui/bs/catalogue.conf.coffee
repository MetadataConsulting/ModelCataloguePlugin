angular.module('mc.core.ui.bs.catalogue', ['mc.core.catalogue']).config ['catalogueProvider', (catalogueProvider)->
  catalogueProvider.setPlural 'dataClass', 'Data Classes'
  catalogueProvider.setPlural 'batch', 'Batches'
  catalogueProvider.setPlural 'dataModelPolicy', 'Data Model Policies'

  catalogueProvider.setIcon 'dataModel',          "fa fa-fw fa-book"
  catalogueProvider.setIcon 'classification',     "fa fa-fw fa-book"
  catalogueProvider.setIcon 'dataClass',          "fa fa-fw fa-cubes"
  catalogueProvider.setIcon 'model',              "fa fa-fw fa-cubes"
  catalogueProvider.setIcon 'dataElement',        "fa fa-fw fa-cube"
  catalogueProvider.setIcon 'primitiveType',      "fa fa-fw fa-cog"
  catalogueProvider.setIcon 'enumeratedType',     "fa fa-fw fa-list-alt"
  catalogueProvider.setIcon 'enumeratedValue',    "fa fa-fw fa-th-list"
  catalogueProvider.setIcon 'versions',           "fa fa-fw fa-history"
  catalogueProvider.setIcon 'referenceType',      "fa fa-fw fa-external-link-square"
  catalogueProvider.setIcon 'dataType',           "fa fa-fw fa-th-large"
  catalogueProvider.setIcon 'measurementUnit',    "fa fa-fw fa-tachometer"
  catalogueProvider.setIcon 'asset',              "fa fa-fw fa-file-code-o"
  catalogueProvider.setIcon 'catalogueElement',   "fa fa-fw fa-file-o"
  catalogueProvider.setIcon 'relationshipType',   "fa fa-fw fa-link"
  catalogueProvider.setIcon 'action',             "fa fa-fw fa-flash"
  catalogueProvider.setIcon 'batch',              "fa fa-fw fa-flash"
  catalogueProvider.setIcon 'user',               "fa fa-fw fa-user"
  catalogueProvider.setIcon 'csvTransformation',  "fa fa-fw fa-long-arrow-right"
  catalogueProvider.setIcon 'relationship',       "fa fa-fw fa-link"
  catalogueProvider.setIcon 'relationships',      "fa fa-fw fa-link"
  catalogueProvider.setIcon 'mapping',            "fa fa-fw fa-superscript"
  catalogueProvider.setIcon 'validationRule',     "fa fa-fw fa-university"
  catalogueProvider.setIcon 'dataModelPolicy',    "fa fa-fw fa-check-square-o"
  catalogueProvider.setIcon 'tag',                "fa fa-fw fa-tag"

  # this should be generated automatically in the future


  catalogueProvider.setInstanceOf 'dataModel',          'classification'
  catalogueProvider.setInstanceOf 'dataModel',          'catalogueElement'
  catalogueProvider.setInstanceOf 'dataClass',          'model'
  catalogueProvider.setInstanceOf 'dataClass',          'catalogueElement'
  catalogueProvider.setInstanceOf 'publishedElement',   'catalogueElement'
  catalogueProvider.setInstanceOf 'user',               'catalogueElement'
  catalogueProvider.setInstanceOf 'dataType',           'catalogueElement'
  catalogueProvider.setInstanceOf 'classification',     'catalogueElement'
  catalogueProvider.setInstanceOf 'asset',              'catalogueElement'
  catalogueProvider.setInstanceOf 'measurementUnit',    'catalogueElement'
  catalogueProvider.setInstanceOf 'model',              'catalogueElement'
  catalogueProvider.setInstanceOf 'dataElement',        'catalogueElement'
  catalogueProvider.setInstanceOf 'validationRule',     'catalogueElement'
  catalogueProvider.setInstanceOf 'tag',                'catalogueElement'

  catalogueProvider.setInstanceOf 'enumeratedType',     'dataType'
  catalogueProvider.setInstanceOf 'referenceType',      'dataType'
  catalogueProvider.setInstanceOf 'primitiveType',      'dataType'

  catalogueProvider.setDefaultSort 'catalogueElement',  sort: 'name',         order: 'asc'
  catalogueProvider.setDefaultSort 'asset',             sort: 'lastUpdated',  order: 'desc'

  catalogueProvider.setDefaultXslt 'catalogueElement',  '/assets/xsl/transform2CatalogueSchema.xsl'

  # TODO: deprecation warning for primitive and reference data type
  catalogueProvider.setDeprecationWarning 'dataElement', (dataElement) ->
    if dataElement.dataType
      return 'Data Type Deprecated' if dataElement.dataType.status == 'DEPRECATED'

    return undefined

#  catalogueProvider.addContainsCandidateTest ['list', 'element', 'extra', '$log', (list, result, extra, $log) ->
#    $log.info 'testing candidates:',list, result, extra
#  ]

  catalogueProvider.addContainsCandidateTest ['list', 'element', 'extra', (list, result, extra) ->
    endsWith = (text, suffix) -> text.indexOf(suffix, text.length - suffix.length) != -1
    element = extra.owner ? {}

    return 0 unless result and result.relation and result.element and result.type and result.direction

    direction = if result.direction == 'destinationToSource' then 'incoming' else 'outgoing'
    oppositeDirection = if result.direction == 'destinationToSource' then 'outgoing' else 'incoming'

    return 0 unless list

    currentDescend = list

    return 0.5 if result.element.link == element.link and endsWith(currentDescend.link, "/#{direction}/#{result.type.name}")
    return 0.5 if result.relation.link == element.link and endsWith(currentDescend.link, "/#{oppositeDirection}/#{result.type.name}")
  ]

  catalogueProvider.addContainsCandidateTest ['list', 'element', 'extra', 'modelCatalogueApiRoot', (list, newElement, extra, modelCatalogueApiRoot) ->
    url = extra.url
    return 0 unless list
    return 0 unless list.itemType
    return 0 unless newElement
    return 0 if angular.isFunction(newElement.isInstanceOf) and not newElement.isInstanceOf(list.itemType)
    return 0 unless url
    return 0 unless list.base

    return 1 if url.indexOf("#{modelCatalogueApiRoot}#{list.base}") >= 0
    return 1 if "#{modelCatalogueApiRoot}#{list.base}".indexOf(url) >= 0
    return 1 if url.indexOf("#{modelCatalogueApiRoot}#{list.base.replace('/relationships/', '/outgoing/')}") >= 0
    return 1 if "#{modelCatalogueApiRoot}#{list.base.replace('/relationships/', '/outgoing/')}".indexOf(url) >= 0
    return 1 if "#{modelCatalogueApiRoot}#{list.base.replace('/dataType/', '/enumeratedType/')}".indexOf(url) >= 0
    return 1 if "#{modelCatalogueApiRoot}#{list.base.replace('/dataType/', '/primitiveType/')}".indexOf(url) >= 0
    return 1 if "#{modelCatalogueApiRoot}#{list.base.replace('/dataType/', '/referenceType/')}".indexOf(url) >= 0
    return 1 if "#{modelCatalogueApiRoot}#{list.base.replace('/outgoing/favourite', '/favourite')}".indexOf(url) >= 0

    return 0.5 if "#{modelCatalogueApiRoot}#{list.base.replace('/content', '/outgoing/hierarchy')}".indexOf(url) >= 0
    return 0.5 if "#{modelCatalogueApiRoot}#{list.base.replace('/content', '/outgoing/containment')}".indexOf(url) >= 0
    return 0.5 if "#{modelCatalogueApiRoot}#{list.base.replace('/content', '/incoming/involvedness')}".indexOf(url) >= 0
    return 0.5 if "#{modelCatalogueApiRoot}#{list.base.replace('/content', '/incoming/ruleContext')}".indexOf(url) >= 0

    return 0.3 if url.indexOf("#{modelCatalogueApiRoot}#{list.base.replace('/relationships/', '/incoming/')}") >= 0
    return 0.3 if "#{modelCatalogueApiRoot}#{list.base.replace('/relationships/', '/incoming/')}".indexOf(url) >= 0

    return 0.2 if url.indexOf("#{modelCatalogueApiRoot}#{list.base.replace('/outgoing/', '/incoming/')}") >= 0
    return 0.2 if "#{modelCatalogueApiRoot}#{list.base.replace('/outgoing/', '/incoming/')}".indexOf(url) >= 0
    return 0.2 if url.indexOf("#{modelCatalogueApiRoot}#{list.base.replace('/incoming/', '/outgoing/')}") >= 0
    return 0.2 if "#{modelCatalogueApiRoot}#{list.base.replace('/incoming/', '/outgoing/')}".indexOf(url) >= 0

    return 0.1 if list.base.indexOf('/history') >= 0

    return 0
  ]
  catalogueProvider.addContainsCandidateTest ['list', 'element', 'extra', 'modelCatalogueApiRoot', (list, newElement, extra, modelCatalogueApiRoot) ->
    url = extra.url
    return 0 unless url
    return 0 unless list
    return 0 unless list.base

    return 1 if "#{modelCatalogueApiRoot}#{list.base.replace('/outgoing/favourite', '/favourite')}".indexOf(url) >= 0

    return 0
  ]

  catalogueProvider.addContainsCandidateTest ['list', 'element', 'extra', (list, newElement, extra) ->
    return 0 unless newElement.id
    return 0 unless extra.owner
    return 0 unless extra.owner.id

    return 0.5 if newElement.id == extra.owner.id
  ]

  catalogueProvider.addContainsCandidateTest ['list', 'element', 'extra', (list, newElement, extra) ->
    return 0 unless extra.url
    return 0 unless extra.url.indexOf("/asset/upload") >= 0

    return 0 unless list
    return 0 unless list.base

    return 0.5 if list.base.indexOf("/asset") >= 0

    return 0
  ]
  catalogueProvider.addContainsCandidateTest ['list', 'element', 'extra', (list, newElement, extra) ->
    return 0 unless list
    return 0 unless list.base

    return 0.5 if list.base.indexOf("/tag/forDataModel") >= 0 and newElement.isInstanceOf('dataElement')

    return 0
  ]
]
