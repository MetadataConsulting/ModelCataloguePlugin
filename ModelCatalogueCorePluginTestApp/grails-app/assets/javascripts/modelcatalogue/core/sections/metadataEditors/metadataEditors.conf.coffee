angular.module('modelcatalogue.core.sections.metadataEditors').config (metadataEditorsProvider) ->
  'ngInject'

  metadataEditorsProvider.register {
    title: 'Occurrence'
    types: [
      '=[containment]=>'
      '=[hierarchy]=>'
    ]
    keys: ['Min Occurs', 'Max Occurs']
    template: '/modelcatalogue/core/sections/metadataEditors/occurrence.html'
  }

  metadataEditorsProvider.register {
    title: 'Appearance'
    types: [
      '=[containment]=>'
      '=[hierarchy]=>'
    ]
    keys: ['Name']
    template: '/modelcatalogue/core/sections/metadataEditors/appearance.html'
  }

  metadataEditorsProvider.register {
    title: 'Subset'
    types: [
      'EnumeratedType=[base]=>EnumeratedType'
    ]
    keys: ['http://www.modelcatalogue.org/metadata/enumerateType#subset']
    template: '/modelcatalogue/core/sections/metadataEditors/subset.html'
  }
