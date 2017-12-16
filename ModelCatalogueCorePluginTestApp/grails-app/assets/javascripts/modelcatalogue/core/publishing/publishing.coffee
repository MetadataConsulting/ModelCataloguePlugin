window.modelcatalogue.registerModule('mc.core.publishing')

angular.module('mc.core.publishing.metadata', [])

publishing = angular.module('mc.core.publishing',
  ['mc.core.ui.detailSections',
    'mc.core.publishing.metadata' # Need to include this to use the templates under publishing/metadata
    # bootstrap for date picker?
    #,'ui.bootstrap'
  ])


publishing.config ['detailSectionsProvider', (detailSectionsProvider)->
  detailSectionsProvider.register {
    title: 'Publishing Metadata'
    position: 50
    types: [
      'dataModel'
      # doesn't apply to classes or data elements - steward is concerned with the whole dataset
    ]
    keys: [
      "http://www.modelcatalogue.org/metadata/#authors" # name
      "http://www.modelcatalogue.org/metadata/#reviewers" # name
      "http://www.modelcatalogue.org/metadata/#owner" # name
      "http://www.modelcatalogue.org/metadata/#reviewed" # date
      "http://www.modelcatalogue.org/metadata/#approved" # date
      "http://www.modelcatalogue.org/metadata/#released" # date
      "http://www.modelcatalogue.org/metadata/#effectiveFrom" # date
      "http://www.modelcatalogue.org/metadata/#effectiveTo" # date
    ]
    # should be displayed by default
    hideByDefault: false
    template: '/mc/core/publishing/publishingMetadata.html'
  }
]
