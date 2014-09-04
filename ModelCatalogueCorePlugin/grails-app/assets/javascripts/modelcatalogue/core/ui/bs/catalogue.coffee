angular.module('mc.core.ui.bs.catalogue', ['mc.core.catalogue']).config ['catalogueProvider', (catalogueProvider)->
  catalogueProvider.setIcon 'org.modelcatalogue.core.Classification',   "fa fa-fw fa-tags"
  catalogueProvider.setIcon 'org.modelcatalogue.core.Model',            "fa fa-fw fa-cubes"
  catalogueProvider.setIcon 'org.modelcatalogue.core.DataElement',      "fa fa-fw fa-cube"
  catalogueProvider.setIcon 'org.modelcatalogue.core.ConceptualDomain', "fa fa-fw fa-cogs"
  catalogueProvider.setIcon 'org.modelcatalogue.core.ValueDomain',      "fa fa-fw fa-cog"
  catalogueProvider.setIcon 'org.modelcatalogue.core.EnumeratedType',   "fa fa-fw fa-list-alt"
  catalogueProvider.setIcon 'org.modelcatalogue.core.DataType',         "fa fa-fw fa-list-alt"
  catalogueProvider.setIcon 'org.modelcatalogue.core.MeasurementUnit',  "fa fa-fw fa-tachometer"
  catalogueProvider.setIcon 'org.modelcatalogue.core.Asset',            "fa fa-fw fa-file-o"
  catalogueProvider.setIcon 'org.modelcatalogue.core.RelationshipType', "fa fa-fw fa-link"
  catalogueProvider.setIcon 'org.modelcatalogue.core.action.Action',    "fa fa-fw fa-flash"
]