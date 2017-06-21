metadataEditors = angular.module('mc.core.ui.bs.metadataEditors', ['mc.core.ui.metadataEditors'])

metadataEditors.config (metadataEditorsProvider) ->
  'ngInject'

  metadataEditorsProvider.register {
    title: 'Occurrence'
    types: [
      '=[containment]=>'
      '=[hierarchy]=>'
    ]
    keys: ['Min Occurs', 'Max Occurs']
    template: '/mc/core/ui/metadata-editor/occurrence.html'
  }

  metadataEditorsProvider.register {
    title: 'Appearance'
    types: [
      '=[containment]=>'
      '=[hierarchy]=>'
    ]
    keys: ['Name']
    template: '/mc/core/ui/metadata-editor/appearance.html'
  }

  metadataEditorsProvider.register {
    title: 'Subset'
    types: [
      'EnumeratedType=[base]=>EnumeratedType'
    ]
    keys: ['http://www.modelcatalogue.org/metadata/enumerateType#subset']
    template: '/mc/core/ui/metadata-editor/subset.html'
  }
