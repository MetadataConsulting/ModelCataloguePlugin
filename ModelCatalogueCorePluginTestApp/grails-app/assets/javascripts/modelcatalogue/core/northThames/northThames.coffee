window.modelcatalogue.registerModule('mc.core.northThames')

angular.module('mc.core.northThames.metadata', [])

stewardship = angular.module('mc.core.northThames',
  ['mc.core.ui.detailSections',
    'mc.core.northThames.metadata' # Need to include this to use the templates under stewardship/metadata
    # bootstrap for date picker?
    #,'ui.bootstrap'
  ])

# List of levels to be selected for sensitivity
stewardship.controller 'SelectYesNoUnk', ($scope) ->
  $scope.yesNoUnknown = [
    "Yes"
    "No"
    "Unknown"
  ]

stewardship.config ['detailSectionsProvider', (detailSectionsProvider)->
  detailSectionsProvider.register {
    title: 'North Thames Metadata'
    position: 50
    types: [
      'dataModel'
      # may later extend to Data Types
    ]
    keys: [
      "Semantic Matching" # yesNoType
      "Known Issue" # name
      "Immediate Solution" # name
      "Immediate Solution Owner" # name
      "Long Term Solution" # name
      "Long Term Solution Owner" # name
      "Data Item Unique Code" # name
      "Related To" # name
      "Part Of Standard Data Set" # name
      "Data Completeness" # name
      "Estimated Quality" # name
      "Timely" # name
      "Comments" # name

    ]
    hideByDefault: true
    template: '/mc/core/northThames/northThamesMetadata.html'
  }
]
