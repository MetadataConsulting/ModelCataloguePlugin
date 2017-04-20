window.modelcatalogue.registerModule('mc.core.stewardship')

angular.module('mc.core.stewardship.metadata', [])

stewardship = angular.module('mc.core.stewardship',
  ['mc.core.ui.detailSections',
    'mc.core.stewardship.metadata' # Need to include this to use the templates under stewardship/metadata
    # bootstrap for date picker?
    #,'ui.bootstrap'
  ])

# List of levels to be selected for sensitivity
stewardship.controller 'SelectSensitivity', ($scope) ->
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
      'dataClass' # this detailSection applies to these types
      'dataElement'
      'dataModel'
      # may later extend to Data Types
    ]
    keys: [
      "http://stewardship.modelcatalogue.org/stewardship#registrationAuthority" # name
      "http://stewardship.modelcatalogue.org/stewardship#dataSteward" # name
      "http://stewardship.modelcatalogue.org/stewardship#dataQualityAssessment" # name
      "http://stewardship.modelcatalogue.org/stewardship#effectiveFrom" # date
      "http://stewardship.modelcatalogue.org/stewardship#effectiveTo" # date
      "http://stewardship.modelcatalogue.org/stewardship#lastAudit" # date
      "http://stewardship.modelcatalogue.org/stewardship#sensitivity" # sensitivity type
      "http://stewardship.modelcatalogue.org/stewardship#databaseLocation" # url
      "http://stewardship.modelcatalogue.org/stewardship#databaseAccessDetails" # string
      "http://stewardship.modelcatalogue.org/stewardship#databaseAdministrator" # name

    ]
    hideByDefault: true
    template: '/mc/core/stewardship/stewardshipMetadata.html'
  }
]
