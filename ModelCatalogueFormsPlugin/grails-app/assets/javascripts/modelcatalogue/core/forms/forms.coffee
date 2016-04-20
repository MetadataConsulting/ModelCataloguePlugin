window.modelcatalogue.registerModule 'mc.core.forms'

forms = angular.module('mc.core.forms', ['mc.core.ui.metadataEditors', 'mc.core.ui.detailSections'])

# TODO: inline help

forms.config ['metadataEditorsProvider', 'detailSectionsProvider', (metadataEditorsProvider, detailSectionsProvider)->
  detailSectionsProvider.register {
    title: 'Form (Metadata)'
    position: 50
    types: [
      'dataClass'
    ]
    keys: [
      "http://forms.modelcatalogue.org/form#name"
      "http://forms.modelcatalogue.org/form#version"
      "http://forms.modelcatalogue.org/form#versionDescription"
      "http://forms.modelcatalogue.org/form#revisionNotes"
    ]
    hideIfNoData: true
    template: '/mc/core/forms/formMetadata.html'
  }

  detailSectionsProvider.register {
    title: 'Form (Section)'
    position: 50
    types: [
      'dataClass'
      '=[hierarchy]=>'
    ]
    keys: [
      "http://forms.modelcatalogue.org/section#exclude"
      "http://forms.modelcatalogue.org/section#excludeDataElements"
      "http://forms.modelcatalogue.org/section#merge"
      "http://forms.modelcatalogue.org/section#title"
      "http://forms.modelcatalogue.org/section#subtitle"
      "http://forms.modelcatalogue.org/section#instructions"
      "http://forms.modelcatalogue.org/section#pageNumber"
    ]
    hideIfNoData: true
    template: '/mc/core/forms/formSection.html'
  }

  detailSectionsProvider.register {
    title: 'Form (Grid)'
    position: 50
    types: [
      'dataClass'
      '=[hierarchy]=>'
    ]
    keys: [
      "http://forms.modelcatalogue.org/group#grid"
      "http://forms.modelcatalogue.org/group#header"
      "http://forms.modelcatalogue.org/group#repeatNum"
      "http://forms.modelcatalogue.org/group#repeatMax"
    ]
    hideIfNoData: true
    template: '/mc/core/forms/formGrid.html'
  }

  metadataEditorsProvider.register {
    title: 'Form (Item)'
    types: [
      '=[containment]=>'
    ]

    keys: [
      "http://forms.modelcatalogue.org/item#exclude"
      "http://forms.modelcatalogue.org/item#question"
      "http://forms.modelcatalogue.org/item#defaultValue"
      "http://forms.modelcatalogue.org/item#phi"
      "http://forms.modelcatalogue.org/item#instructions"
      "http://forms.modelcatalogue.org/item#description"
      "http://forms.modelcatalogue.org/item#layout"
      "http://forms.modelcatalogue.org/item#columnNumber"
      "http://forms.modelcatalogue.org/item#required"
      "http://forms.modelcatalogue.org/item#questionNumber"
      "http://forms.modelcatalogue.org/item#responseType"
      "http://forms.modelcatalogue.org/item#units"
      "http://forms.modelcatalogue.org/item#digits"
      "http://forms.modelcatalogue.org/item#length"
      "http://forms.modelcatalogue.org/item#regexp"
      "http://forms.modelcatalogue.org/item#regexpErrorMessage"
      "http://forms.modelcatalogue.org/item#dataType"
    ]
    template: '/mc/core/forms/formItemDataElement.html'
  }

  detailSectionsProvider.register {
    title: 'Form (Item)'
    position: 50
    types: [
      'dataElement'
    ]
    keys: [
      "http://forms.modelcatalogue.org/item#exclude"
      "http://forms.modelcatalogue.org/item#question"
      "http://forms.modelcatalogue.org/item#defaultValue"
      "http://forms.modelcatalogue.org/item#phi"
      "http://forms.modelcatalogue.org/item#instructions"
      "http://forms.modelcatalogue.org/item#description"
      "http://forms.modelcatalogue.org/item#layout"
      "http://forms.modelcatalogue.org/item#columnNumber"
      "http://forms.modelcatalogue.org/item#required"
      "http://forms.modelcatalogue.org/item#questionNumber"
      "http://forms.modelcatalogue.org/item#responseType"
      "http://forms.modelcatalogue.org/item#units"
      "http://forms.modelcatalogue.org/item#digits"
      "http://forms.modelcatalogue.org/item#length"
      "http://forms.modelcatalogue.org/item#regexp"
      "http://forms.modelcatalogue.org/item#regexpErrorMessage"
      "http://forms.modelcatalogue.org/item#dataType"
    ]
    hideIfNoData: true
    template: '/mc/core/forms/formItem.html'
  }

  detailSectionsProvider.register {
    title: 'Form (Item)'
    position: 50
    types: [
      'dataType'
    ]
    keys: [
      "http://forms.modelcatalogue.org/item#responseType"
      "http://forms.modelcatalogue.org/item#units"
      "http://forms.modelcatalogue.org/item#digits"
      "http://forms.modelcatalogue.org/item#length"
      "http://forms.modelcatalogue.org/item#regexp"
      "http://forms.modelcatalogue.org/item#regexpErrorMessage"
      "http://forms.modelcatalogue.org/item#dataType"
    ]
    hideIfNoData: true
    template: '/mc/core/forms/formItem.html'
  }
]
