angular.module('mc.core.ui.bs.catalogue', ['mc.core.catalogue']).config ['catalogueProvider', (catalogueProvider)->
  catalogueProvider.setIcon 'classification',     "fa fa-fw fa-tags"
  catalogueProvider.setIcon 'model',              "fa fa-fw fa-cubes"
  catalogueProvider.setIcon 'dataElement',        "fa fa-fw fa-cube"
  catalogueProvider.setIcon 'conceptualDomain',   "fa fa-fw fa-cogs"
  catalogueProvider.setIcon 'valueDomain',        "fa fa-fw fa-cog"
  catalogueProvider.setIcon 'enumeratedType',     "fa fa-fw fa-list-alt"
  catalogueProvider.setIcon 'dataType',           "fa fa-fw fa-th-large"
  catalogueProvider.setIcon 'measurementUnit',    "fa fa-fw fa-tachometer"
  catalogueProvider.setIcon 'asset',              "fa fa-fw fa-file-o"
  catalogueProvider.setIcon 'relationshipType',   "fa fa-fw fa-link"
  catalogueProvider.setIcon 'action',             "fa fa-fw fa-flash"
  catalogueProvider.setIcon 'batch',              "fa fa-fw fa-flash"
  catalogueProvider.setIcon 'csvTransformation',  "fa fa-fw fa-long-arrow-right"

  # this should be generated automatically in the future
  catalogueProvider.setInstanceOf 'extendibleElement',  'catalogueElement'
  catalogueProvider.setInstanceOf 'conceptualDomain',   'catalogueElement'
  catalogueProvider.setInstanceOf 'dataType',           'catalogueElement'
  catalogueProvider.setInstanceOf 'enumeratedType',     'dataType'
  catalogueProvider.setInstanceOf 'measurementUnit',    'catalogueElement'
  catalogueProvider.setInstanceOf 'valueDomain',        'catalogueElement'
  catalogueProvider.setInstanceOf 'classification',     'catalogueElement'
  catalogueProvider.setInstanceOf 'publishedElement',   'extendibleElement'
  catalogueProvider.setInstanceOf 'asset',              'publishedElement'
  catalogueProvider.setInstanceOf 'model',              'publishedElement'
  catalogueProvider.setInstanceOf 'dataElement',        'publishedElement'

]