
angular.module('modelcatalogue.core.sections.detailSections.stewardship').config ['detailSectionsProvider', (detailSectionsProvider)->
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
        template: '/modelcatalogue/core/sections/detailSections/stewardship/stewardshipMetadata.html'
    }
  ]
