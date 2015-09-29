angular.module('mc.core.ui.bs.catalogue', ['mc.core.catalogue']).config ['catalogueProvider', (catalogueProvider)->
  catalogueProvider.setIcon 'classification',     "fa fa-fw fa-tags"
  catalogueProvider.setIcon 'model',              "fa fa-fw fa-cubes"
  catalogueProvider.setIcon 'dataElement',        "fa fa-fw fa-cube"
  catalogueProvider.setIcon 'valueDomain',        "fa fa-fw fa-cog"
  catalogueProvider.setIcon 'enumeratedType',     "fa fa-fw fa-list-alt"
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


  catalogueProvider.setInstanceOf 'publishedElement',   'catalogueElement'
  catalogueProvider.setInstanceOf 'user',               'catalogueElement'
  catalogueProvider.setInstanceOf 'valueDomain',        'catalogueElement'
  catalogueProvider.setInstanceOf 'dataType',           'catalogueElement'
  catalogueProvider.setInstanceOf 'classification',     'catalogueElement'
  catalogueProvider.setInstanceOf 'asset',              'catalogueElement'
  catalogueProvider.setInstanceOf 'measurementUnit',    'catalogueElement'
  catalogueProvider.setInstanceOf 'model',              'catalogueElement'
  catalogueProvider.setInstanceOf 'dataElement',        'catalogueElement'

  catalogueProvider.setInstanceOf 'enumeratedType',     'dataType'

  catalogueProvider.setDefaultSort 'catalogueElement',  sort: 'name',         order: 'asc'
  catalogueProvider.setDefaultSort 'asset',             sort: 'lastUpdated',  order: 'desc'


  catalogueProvider.setDeprecationWarning 'valueDomain', (domain) ->
    ret = []
    ret.push 'Data Type'        if domain.dataType?.status == 'DEPRECATED'
    ret.push 'Measurement Unit' if domain.unitOfMeasure?.status == 'DEPRECATED'

    return undefined if ret.length == 0
    return ret.join(' and ') + " Deprecated"

  catalogueProvider.setDeprecationWarning 'dataElement', (dataElement) ->
    if dataElement.valueDomain
      return 'Value Domain Deprecated' if dataElement.valueDomain.status == 'DEPRECATED'
      valueDomainDeprecation = catalogueProvider.getDeprecationWarning('valueDomain')(dataElement.valueDomain)
      return "Value Domain uses deprecated #{valueDomainDeprecation}" if valueDomainDeprecation

    return undefined




]