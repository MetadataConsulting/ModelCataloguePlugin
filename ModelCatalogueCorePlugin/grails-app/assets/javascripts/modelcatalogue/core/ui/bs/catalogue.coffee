angular.module('mc.core.ui.bs.catalogue', ['mc.core.catalogue']).config ['catalogueProvider', (catalogueProvider)->
  catalogueProvider.setIcon 'dataModel',          "fa fa-fw fa-book"
  catalogueProvider.setIcon 'classification',     "fa fa-fw fa-book"
  catalogueProvider.setIcon 'dataClass',          "fa fa-fw fa-cubes"
  catalogueProvider.setIcon 'model',              "fa fa-fw fa-cubes"
  catalogueProvider.setIcon 'dataElement',        "fa fa-fw fa-cube"
  catalogueProvider.setIcon 'primitiveType',      "fa fa-fw fa-cog"
  catalogueProvider.setIcon 'enumeratedType',     "fa fa-fw fa-list-alt"
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




]