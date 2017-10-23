window.modelcatalogue.registerModule('mc.core.stewardship')

angular.module('mc.core.stewardship.metadata', [])

stewardship = angular.module('mc.core.stewardship',
  ['mc.core.ui.detailSections',
    'mc.core.stewardship.metadata' # Need to include this to use the templates under stewardship/metadata
    # bootstrap for date picker?
    #,'ui.bootstrap'
  ])


stewardship.controller 'SelectStewardshipInfo', ($scope) ->
  $scope.sensitivityLevels = [
    "Anonymous"
    "Personal"
    "Potentially Sensitive"
    "Pseudonymous"
    "Sensitive"
  ]


stewardship.config ['detailSectionsProvider', (detailSectionsProvider)->
  detailSectionsProvider.register {
    title: 'Stewardship Metadata'
    position: 50
    types: [
      'dataModel'
      # doesn't apply to classes or data elements - steward is concerned with the whole dataset
    ]
    keys: [
      "http://stewardship.modelcatalogue.org/stewardship#registrationAuthority" # name
      "http://stewardship.modelcatalogue.org/stewardship#dataSteward" # name
      "http://stewardship.modelcatalogue.org/stewardship#dataQualityAssessment" # name
      "http://stewardship.modelcatalogue.org/stewardship#stewardshipEffectiveFrom" # date
      "http://stewardship.modelcatalogue.org/stewardship#stewardshipEffectiveTo" # date
      "http://stewardship.modelcatalogue.org/stewardship#lastAudit" # date
      "http://stewardship.modelcatalogue.org/stewardship#sensitivity" # sensitivity type
      "http://stewardship.modelcatalogue.org/stewardship#databaseLocation" # url
      "http://stewardship.modelcatalogue.org/stewardship#databaseAccessDetails" # string
      "http://stewardship.modelcatalogue.org/stewardship#databaseAdministrator" # name

    ]
    # should be displayed by default
    hideByDefault: false
    template: '/mc/core/stewardship/stewardshipMetadata.html'
  }
]
