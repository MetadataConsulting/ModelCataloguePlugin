window.modelcatalogue.registerModule 'modelcatalogue.core.sections.detailSections.forms'


# Angular modules declared in anticipation of the templates

# core/ui/metadataEditors doesn't even exist, it's called metadata-editor
angular.module('modelcatalogue.core.sections.detailSections.forms')
  .config [
    'metadataEditorsProvider', 'detailSectionsProvider',
    (metadataEditorsProvider, detailSectionsProvider)->
      detailSectionsProvider.register {
        title: 'Form Metadata'
        position: 50
        types: [
          'dataClass' # this detailSection applies to these types
          'dataElement'
          'dataType'
        ]
        keys: [
          "http://forms.modelcatalogue.org/form#form"
          "http://forms.modelcatalogue.org/form#customizer"
          "http://forms.modelcatalogue.org/form#name"
          "http://forms.modelcatalogue.org/form#version"
          "http://forms.modelcatalogue.org/form#versionDescription"
          "http://forms.modelcatalogue.org/form#revisionNotes"
          "http://forms.modelcatalogue.org/form#itemNames"
          "http://forms.modelcatalogue.org/section#exclude"
          "http://forms.modelcatalogue.org/section#label"
          "http://forms.modelcatalogue.org/section#excludeDataElements"
          "http://forms.modelcatalogue.org/section#merge"
          "http://forms.modelcatalogue.org/section#title"
          "http://forms.modelcatalogue.org/section#subtitle"
          "http://forms.modelcatalogue.org/section#instructions"
          "http://forms.modelcatalogue.org/section#pageNumber"
          "http://forms.modelcatalogue.org/group#grid"
          "http://forms.modelcatalogue.org/group#header"
          "http://forms.modelcatalogue.org/group#repeatNum"
          "http://forms.modelcatalogue.org/group#repeatMax"
        ]
        typeKeys: {
          'dataElement': [
            "http://forms.modelcatalogue.org/item#exclude"
            "http://forms.modelcatalogue.org/item#name"
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
          'dataType': [
            "http://forms.modelcatalogue.org/item#responseType"
            "http://forms.modelcatalogue.org/item#units"
            "http://forms.modelcatalogue.org/item#digits"
            "http://forms.modelcatalogue.org/item#length"
            "http://forms.modelcatalogue.org/item#regexp"
            "http://forms.modelcatalogue.org/item#regexpErrorMessage"
            "http://forms.modelcatalogue.org/item#dataType"
          ]
        }
        hideByDefault: true
        template: '/modelcatalogue/core/sections/detailSections/forms/formMetadata.html'
      }

      metadataEditorsProvider.register {
        title: 'Form (Section)'
        position: 50
        types: [
          '=[hierarchy]=>'
        ]
        keys: [
          "http://forms.modelcatalogue.org/section#exclude"
          "http://forms.modelcatalogue.org/section#label"
          "http://forms.modelcatalogue.org/section#excludeDataElements"
          "http://forms.modelcatalogue.org/section#merge"
          "http://forms.modelcatalogue.org/section#title"
          "http://forms.modelcatalogue.org/section#subtitle"
          "http://forms.modelcatalogue.org/section#instructions"
          "http://forms.modelcatalogue.org/section#pageNumber"
        ]
        hideIfNoData: true
        template: '/modelcatalogue/core/sections/detailSections/forms/metadataEditors/formSection.html'
      }

      metadataEditorsProvider.register {
        title: 'Form (Grid)'
        position: 50
        types: [
          '=[hierarchy]=>'
        ]
        keys: [
          "http://forms.modelcatalogue.org/group#grid"
          "http://forms.modelcatalogue.org/group#header"
          "http://forms.modelcatalogue.org/group#repeatNum"
          "http://forms.modelcatalogue.org/group#repeatMax"
        ]
        hideIfNoData: true
        template: '/modelcatalogue/core/sections/detailSections/forms/metadataEditors/formGrid.html'
      }

      metadataEditorsProvider.register {
        title: 'Form (Item)'
        types: [
          '=[containment]=>'
        ]

        keys: [
          "http://forms.modelcatalogue.org/item#exclude"
          "http://forms.modelcatalogue.org/item#name"
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
        template: '/modelcatalogue/core/sections/detailSections/forms/metadataEditors/formItemDataElement.html'
      }
  ]
