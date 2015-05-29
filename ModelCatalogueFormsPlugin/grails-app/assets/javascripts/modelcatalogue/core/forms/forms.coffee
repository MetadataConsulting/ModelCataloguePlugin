window.modelcatalogue.registerModule 'mc.core.forms'

forms = angular.module('mc.core.forms', ['mc.core.ui.metadataEditors'])

forms.config ['metadataEditorsProvider', (metadataEditorsProvider)->
  metadataEditorsProvider.register {
    title: 'Form (Metadata)'
    types: [
      'model'
    ]
    keys: [
      "http://forms.modelcatalogue.org/form#name"
      "http://forms.modelcatalogue.org/form#version"
      "http://forms.modelcatalogue.org/form#versionDescription"
      "http://forms.modelcatalogue.org/form#revisionNotes"
    ]
    template: 'modelcatalogue/core/ui/metadataEditors/formMetadata.html'
  }

  metadataEditorsProvider.register {
    title: 'Form (Section)'
    types: [
      'model'
      '=[hierarchy]=>'
    ]
    keys: [
      "http://forms.modelcatalogue.org/section#title"
      "http://forms.modelcatalogue.org/section#subtitle"
      "http://forms.modelcatalogue.org/section#instructions"
      "http://forms.modelcatalogue.org/section#pageNumber"
    ]
    template: 'modelcatalogue/core/ui/metadataEditors/formSection.html'
  }

  metadataEditorsProvider.register {
    title: 'Form (Grid)'
    types: [
      'model'
      '=[hierarchy]=>'
    ]
    keys: [
      "http://forms.modelcatalogue.org/group#grid"
      "http://forms.modelcatalogue.org/group#repeatNum"
      "http://forms.modelcatalogue.org/group#repeatMax"
    ]
    template: 'modelcatalogue/core/ui/metadataEditors/formGrid.html'
  }

  metadataEditorsProvider.register {
    title: 'Form (Item)'
    types: [
      'dataType'
    ]
    keys: [
      "http://forms.modelcatalogue.org/item#dataType"
    ]
    template: 'modelcatalogue/core/ui/metadataEditors/formItemDataType.html'
  }

  metadataEditorsProvider.register {
    title: 'Form (Item)'
    types: [
      'valueDomain'
    ]

    keys: [
#      "http://forms.modelcatalogue.org/item#layout"
#      "http://forms.modelcatalogue.org/item#columnNumber"
#      "http://forms.modelcatalogue.org/item#required"
#      "http://forms.modelcatalogue.org/item#questionNumber"
#
#      "http://forms.modelcatalogue.org/item#phi"
#      "http://forms.modelcatalogue.org/item#description"
#      "http://forms.modelcatalogue.org/item#question"
#      "http://forms.modelcatalogue.org/item#instructions"
#      "http://forms.modelcatalogue.org/item#defaultValue"
#

      "http://forms.modelcatalogue.org/item#responseType"
      "http://forms.modelcatalogue.org/item#units"
      "http://forms.modelcatalogue.org/item#digits"
      "http://forms.modelcatalogue.org/item#length"
      "http://forms.modelcatalogue.org/item#regexp"
      "http://forms.modelcatalogue.org/item#regexpErrorMessage"
    ]
    template: 'modelcatalogue/core/ui/metadataEditors/formItemValueDomain.html'
  }

  metadataEditorsProvider.register {
    title: 'Form (Item)'
    types: [
      'dataElement'
    ]

    keys: [
      "http://forms.modelcatalogue.org/item#question"
      "http://forms.modelcatalogue.org/item#defaultValue"
      "http://forms.modelcatalogue.org/item#phi"
      "http://forms.modelcatalogue.org/item#instructions"
      "http://forms.modelcatalogue.org/item#description"
    ]
    template: 'modelcatalogue/core/ui/metadataEditors/formItemDataElement.html'
  }

  metadataEditorsProvider.register {
    title: 'Form (Item)'
    types: [
      '=[containment]=>'
    ]

    keys: [
      "http://forms.modelcatalogue.org/item#layout"
      "http://forms.modelcatalogue.org/item#columnNumber"
      "http://forms.modelcatalogue.org/item#required"
      "http://forms.modelcatalogue.org/item#questionNumber"
    ]
    template: 'modelcatalogue/core/ui/metadataEditors/formItemContainment.html'
  }

]

forms.run ['$templateCache', ($templateCache) ->
  $templateCache.put 'modelcatalogue/core/ui/metadataEditors/formMetadata.html', '''
    <form class="form">
      <div class="form-group">
        <label for="form-name" class="control-label">Name</label>
        <input maxlength="255" type="text" class="form-control" id="form-name" ng-model="object.access('http://forms.modelcatalogue.org/form#name')" ng-model-options="{ getterSetter: true }">
      </div>
      <div class="form-group">
        <label for="form-version" class="control-label">Version</label>
        <input maxlength="255" type="text" class="form-control" id="form-version" ng-model="object.access('http://forms.modelcatalogue.org/form#version')" ng-model-options="{ getterSetter: true }">
      </div>
      <div class="form-group">
        <label for="form-version-description" class="control-label">Version Description</label>
        <textarea maxlength="4000" rows="5" class="form-control" id="form-version-description" ng-model="object.access('http://forms.modelcatalogue.org/form#versionDescription')" ng-model-options="{ getterSetter: true }"></textarea>
      </div>
      <div class="form-group">
        <label for="form-revision-notes" class="control-label">Revision Notes</label>
        <textarea maxlength="255" rows="5" class="form-control" id="form-revision-notes" ng-model="object.access('http://forms.modelcatalogue.org/form#revisionNotes')" ng-model-options="{ getterSetter: true }"></textarea>
      </div>

    </form>
  '''

  $templateCache.put 'modelcatalogue/core/ui/metadataEditors/formSection.html', '''
    <form class="form">
      <div class="form-group">
        <label for="section-title" class="control-label">Title</label>
        <textarea maxlength="2000" rows="5" class="form-control" id="section-title" ng-model="object.access('http://forms.modelcatalogue.org/section#title')" ng-model-options="{ getterSetter: true }"></textarea>
      </div>
      <div class="form-group">
        <label for="section-subtitle" class="control-label">Subtitle</label>
        <textarea maxlength="2000" rows="5" class="form-control" id="section-subtitle" ng-model="object.access('http://forms.modelcatalogue.org/section#subtitle')" ng-model-options="{ getterSetter: true }"></textarea>
      </div>
      <div class="form-group">
        <label for="section-instructions" class="control-label">Instructions</label>
        <textarea maxlength="2000" rows="5" class="form-control" id="section-instructions" ng-model="object.access('http://forms.modelcatalogue.org/section#instructions')" ng-model-options="{ getterSetter: true }"></textarea>
      </div>
      <div class="form-group">
        <label for="form-page-number" class="control-label">Page Number</label>
        <input maxlength="5" type="text" class="form-control" id="form-page-number" ng-model="object.access('http://forms.modelcatalogue.org/section#pageNumber')" ng-model-options="{ getterSetter: true }">
      </div>
    </form>
  '''

  $templateCache.put 'modelcatalogue/core/ui/metadataEditors/formGrid.html', '''
    <form class="form">
      <div class="checkbox">
        <label>
          <input type="checkbox" ng-model="object.access('http://forms.modelcatalogue.org/group#grid')" ng-model-options="{ getterSetter: true }">
          Grid
        </label>
      </div>
      <div class="form-group">
        <label for="repeat-num" class="control-label">Initial Number of Rows (default 1)</label>
        <input type="number" min="1" class="form-control" id="repeat-num" ng-model="object.access('http://forms.modelcatalogue.org/group#repeatNum').asInt" ng-model-options="{ getterSetter: true}" ng-disabled="!object.get('http://forms.modelcatalogue.org/group#grid')">
      </div>
      <div class="form-group">
        <label for="repeat-max" class="control-label">Max Number of Rows (default 40)</label>
        <input type="number" min="1" class="form-control" id="repeat-max" ng-model="object.access('http://forms.modelcatalogue.org/group#repeatMax').asInt" ng-model-options="{ getterSetter: true }" ng-disabled="!object.get('http://forms.modelcatalogue.org/group#grid')">
      </div>
    </form>
  '''

  $templateCache.put 'modelcatalogue/core/ui/metadataEditors/formItemDataType.html', '''
    <form class="form">
      <div class="form-group">
        <label for="item-data-type" class="control-label">Data Type</label>
        <select id="item-data-type" class="form-control" ng-options="key for (key, value) in {'String':'string', 'Integer': 'int', 'Real':'real', 'Date':'date', 'Partial Date': 'pdate'}" ng-model="object.access('http://forms.modelcatalogue.org/item#dataType')" ng-model-options="{ getterSetter: true }"></select>
      </div>
    </form>
  '''

  $templateCache.put 'modelcatalogue/core/ui/metadataEditors/formItemValueDomain.html', '''
    <form class="form">
      <div class="form-group">
        <label for="item-response-type" class="control-label">Response Type</label>
        <select id="item-response-type" class="form-control" ng-options="key for (key, value) in {'Text':'text', 'Textarea': 'textarea', 'Single Select':'singleselect', 'Radio':'radio', 'Multi Select': 'multiselect', 'Checkbox': 'checkbox', 'File': 'file'}" ng-model="object.access('http://forms.modelcatalogue.org/item#responseType')" ng-model-options="{ getterSetter: true }"></select>
      </div>
      <div class="form-group">
        <label for="item-units" class="control-label">Units</label>
        <input maxlength="64" type="text" class="form-control" id="item-units" ng-model="object.access('http://forms.modelcatalogue.org/item#units')" ng-model-options="{ getterSetter: true }">
      </div>
      <div class="form-group">
        <label for="item-length" class="control-label">Max Length (String) or Number of All Digits (Numbers)</label>
        <input type="number" min="1" max="2000" class="form-control" id="item-length" ng-model="object.access('http://forms.modelcatalogue.org/item#length').asInt" ng-model-options="{ getterSetter: true }">
      </div>
      <div class="form-group">
        <label for="item-digits" class="control-label">Number of Decimal Digits</label>
        <input type="number" min="0" max="20" class="form-control" id="item-digits" ng-model="object.access('http://forms.modelcatalogue.org/item#digits').asInt" ng-model-options="{ getterSetter: true }">
      </div>
      <div class="form-group">
        <label for="item-regexp" class="control-label">Regular Expression</label>
        <input maxlength="1000" type="text" class="form-control" id="item-regexp" ng-model="object.access('http://forms.modelcatalogue.org/item#regexp')" ng-model-options="{ getterSetter: true }">
      </div>
      <div class="form-group">
        <label for="item-regexp-message" class="control-label">Regular Expression Error Message</label>
        <input maxlength="1000" type="text" class="form-control" id="item-regexp-message" ng-model="object.access('http://forms.modelcatalogue.org/item#regexpErrorMessage')" ng-model-options="{ getterSetter: true }">
      </div>
    </form>
  '''
  $templateCache.put 'modelcatalogue/core/ui/metadataEditors/formItemDataElement.html', '''
    <form class="form">
      <div class="checkbox">
        <label>
          <input type="checkbox" ng-model="object.access('http://forms.modelcatalogue.org/item#phi')" ng-model-options="{ getterSetter: true }">
          Protected Health Information
        </label>
      </div>
      <div class="form-group">
        <label for="item-question" class="control-label">Question (Left Item Text)</label>
        <input maxlength="2000" type="text" class="form-control" id="item-question" ng-model="object.access('http://forms.modelcatalogue.org/item#question')" ng-model-options="{ getterSetter: true }">
      </div>
      <div class="form-group">
        <label for="item-description" class="control-label">Description Label</label>
        <textarea maxlength="4000" rows="5" class="form-control" id="item-description" ng-model="object.access('http://forms.modelcatalogue.org/item#description')" ng-model-options="{ getterSetter: true }"></textarea>
      </div>
      <div class="form-group">
        <label for="item-default-value" class="control-label">Default Value</label>
        <input maxlength="4000" type="text" class="form-control" id="item-default-value" ng-model="object.access('http://forms.modelcatalogue.org/item#defaultValue')" ng-model-options="{ getterSetter: true }">
      </div>
      <div class="form-group">
        <label for="item-instructions" class="control-label">Instructions (Right Item Text)</label>
        <textarea maxlength="2000" rows="5" class="form-control" id="item-instructions" ng-model="object.access('http://forms.modelcatalogue.org/item#instructions')" ng-model-options="{ getterSetter: true }"></textarea>
      </div>
    </form>
  '''


  $templateCache.put 'modelcatalogue/core/ui/metadataEditors/formItemContainment.html', '''
    <form class="form">
      <div class="checkbox">
        <label>
          <input type="checkbox" ng-model="object.access('http://forms.modelcatalogue.org/item#required')" ng-model-options="{ getterSetter: true }">
          Required
        </label>
      </div>
      <div class="checkbox">
        <label>
          <input type="checkbox" ng-model="object.access('http://forms.modelcatalogue.org/item#layout')" ng-model-options="{ getterSetter: true }" ng-true-value="'horizontal'" ng-false-value="''">
          Horizontal (for checkbox or radio)
        </label>
      </div>
      <div class="form-group">
        <label for="item-column-number" class="control-label">Column Number</label>
        <input type="number" min="1" max="3" class="form-control" id="item-column-number" ng-model="object.access('http://forms.modelcatalogue.org/item#columnNumber').asInt" ng-model-options="{ getterSetter: true }">
      </div>
      <div class="form-group">
        <label for="item-question-number" class="control-label">Question Number</label>
        <input maxlength="20" type="text" class="form-control" id="item-question-number" ng-model="object.access('http://forms.modelcatalogue.org/item#questionNumber')" ng-model-options="{ getterSetter: true }">
      </div>
    </form>
  '''
]