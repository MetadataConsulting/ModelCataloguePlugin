angular.module('mc.core.ui.bs.catalogue', ['mc.core.catalogue']).config ['catalogueProvider', (catalogueProvider)->
  catalogueProvider.setIcon 'classification',     "fa fa-fw fa-tags"
  catalogueProvider.setIcon 'model',              "fa fa-fw fa-cubes"
  catalogueProvider.setIcon 'dataElement',        "fa fa-fw fa-cube"
  catalogueProvider.setIcon 'valueDomain',        "fa fa-fw fa-cog"
  catalogueProvider.setIcon 'enumeratedType',     "fa fa-fw fa-list-alt"
  catalogueProvider.setIcon 'dataType',           "fa fa-fw fa-th-large"
  catalogueProvider.setIcon 'measurementUnit',    "fa fa-fw fa-tachometer"
  catalogueProvider.setIcon 'publishedElement',   "fa fa-fw fa-file-powerpoint-o"
  catalogueProvider.setIcon 'asset',              "fa fa-fw fa-file-code-o"
  catalogueProvider.setIcon 'catalogueElement',   "fa fa-fw fa-file-o"
  catalogueProvider.setIcon 'relationshipType',   "fa fa-fw fa-link"
  catalogueProvider.setIcon 'action',             "fa fa-fw fa-flash"
  catalogueProvider.setIcon 'batch',              "fa fa-fw fa-flash"
  catalogueProvider.setIcon 'user',               "fa fa-fw fa-user"
  catalogueProvider.setIcon 'csvTransformation',  "fa fa-fw fa-long-arrow-right"

  # this should be generated automatically in the future


  catalogueProvider.setInstanceOf 'publishedElement',   'catalogueElement'
  catalogueProvider.setInstanceOf 'user',               'catalogueElement'
  catalogueProvider.setInstanceOf 'valueDomain',        'catalogueElement'

  catalogueProvider.setInstanceOf 'enumeratedType',     'dataType'

  catalogueProvider.setInstanceOf 'dataType', 'publishedElement'
  catalogueProvider.setInstanceOf 'classification', 'publishedElement'
  catalogueProvider.setInstanceOf 'asset',              'publishedElement'
  catalogueProvider.setInstanceOf 'measurementUnit',    'publishedElement'
  catalogueProvider.setInstanceOf 'model',              'publishedElement'
  catalogueProvider.setInstanceOf 'dataElement',        'publishedElement'

]