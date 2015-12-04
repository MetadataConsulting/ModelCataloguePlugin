angular.module('mc.core.ui.bs.catalogue', ['mc.core.catalogue']).config ['catalogueProvider', (catalogueProvider)->
  catalogueProvider.setIcon 'dataModel',          "fa fa-fw fa-book"
  catalogueProvider.setIcon 'classification',     "fa fa-fw fa-book"
  catalogueProvider.setIcon 'dataClass',          "fa fa-fw fa-cubes"
  catalogueProvider.setIcon 'model',              "fa fa-fw fa-cubes"
  catalogueProvider.setIcon 'dataElement',        "fa fa-fw fa-cube"
  catalogueProvider.setIcon 'primitiveType',      "fa fa-fw fa-cog"
  catalogueProvider.setIcon 'enumeratedType',     "fa fa-fw fa-list-alt"
  catalogueProvider.setIcon 'enumeratedValue',    "fa fa-fw fa-th-list"
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
  catalogueProvider.setIcon 'mapping',            "fa fa-fw fa-superscript"

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

  catalogueProvider.setInstanceOf 'enumeratedType',     'dataType'
  catalogueProvider.setInstanceOf 'referenceType',      'dataType'
  catalogueProvider.setInstanceOf 'primitiveType',      'dataType'

  catalogueProvider.setDefaultSort 'catalogueElement',  sort: 'name',         order: 'asc'
  catalogueProvider.setDefaultSort 'asset',             sort: 'lastUpdated',  order: 'desc'


  # TODO: deprecation warning for primitive and reference data type
  catalogueProvider.setDeprecationWarning 'dataElement', (dataElement) ->
    if dataElement.dataType
      return 'Data Type Deprecated' if dataElement.dataType.status == 'DEPRECATED'

    return undefined

  catalogueProvider.addContainsCandidateTest ['list', 'element', 'extra', (list, result, extra) ->
    endsWith = (text, suffix) -> text.indexOf(suffix, text.length - suffix.length) != -1
    element = extra.owner ? {}

    return 0 unless result and result.relation and result.element and result.type and result.direction

    direction = if result.direction == 'destinationToSource' then 'incoming' else 'outgoing'
    oppositeDirection = if result.direction == 'destinationToSource' then 'outgoing' else 'incoming'

    currentDescend = list

    return 0.5 if result.element.link == element.link and endsWith(currentDescend.link, "/#{direction}/#{result.type.name}")
    return 0.5 if result.relation.link == element.link and endsWith(currentDescend.link, "/#{oppositeDirection}/#{result.type.name}")
  ]

  catalogueProvider.addContainsCandidateTest ['list', 'element', 'extra', 'modelCatalogueApiRoot', (list, newElement, extra, modelCatalogueApiRoot) ->
    url = extra.url
    return 0 unless list
    return 0 unless list.itemType
    return 0 unless newElement
    return 0 unless newElement.isInstanceOf and newElement.isInstanceOf(list.itemType)
    return 0 unless url
    return 0 unless list.base
    return 0 unless url.indexOf("#{modelCatalogueApiRoot}#{list.base}") >= 0 \
      or "#{modelCatalogueApiRoot}#{list.base}".indexOf(url) >= 0 \
      or url.indexOf("#{modelCatalogueApiRoot}#{list.base.replace('/relationships/', '/outgoing/')}") >= 0 \
      or "#{modelCatalogueApiRoot}#{list.base.replace('/relationships/', '/outgoing/')}".indexOf(url) >= 0 \
      or "#{modelCatalogueApiRoot}#{list.base.replace('/dataType/', '/enumeratedType/')}".indexOf(url) >= 0 \
      or "#{modelCatalogueApiRoot}#{list.base.replace('/dataType/', '/primitiveType/')}".indexOf(url) >= 0 \
      or "#{modelCatalogueApiRoot}#{list.base.replace('/dataType/', '/referenceType/')}".indexOf(url) >= 0

    return 1
  ]
]