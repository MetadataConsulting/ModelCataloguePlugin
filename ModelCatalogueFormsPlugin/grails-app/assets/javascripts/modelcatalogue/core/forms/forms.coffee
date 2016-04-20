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
    template: 'modelcatalogue/core/ui/metadataEditors/formItemDataElement.html'
  }

  detailSectionsProvider.register {
    title: 'Form (Item)'
    position: 50
    types: [
      'dataElement'
    ]
    keys: [
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

forms.run ['$templateCache', ($templateCache) ->
  $templateCache.put 'modelcatalogue/core/ui/metadataEditors/formItemDataElement.html', '''
    <form class="form">
      <div class="checkbox">
        <label>
          <input type="checkbox" ng-model="object.access('http://forms.modelcatalogue.org/item#phi')" ng-model-options="{ getterSetter: true }">
          Protected Health Information
        </label>
        <p class="help-block">
            Signifies whether this item would be considered Protected Health Information.<br/>

            Leaving the field blank or selecting 0 means the item would not be considered Protected Health Information.
            This flag does not do anything to mask the data or prevent people from seeing it.
            The field is used as a label only.<br/>

            When creating a data set, this label will show in the metadata and the user could choose to include this item
            in the dataset (create dataset step) or not based on this label.<br/>

            This field should not be changed in any subsequent versions of the CRF. If you do change it and you are
            an owner of the CRF the PHI attribute for this item will be changed for all versions of the CRF.<br/>
        </p>
      </div>
      <div class="form-group">
        <label for="item-question" class="control-label">Question (Left Item Text)</label>
        <input maxlength="2000" type="text" class="form-control" id="item-question" ng-model="object.access('http://forms.modelcatalogue.org/item#question')" ng-model-options="{ getterSetter: true }">
        <p class="help-block">
            Descriptive text that appears to the left of the input on the CRF. Often phrased in the form of a question, or
            descriptive label for the form field input. Defaults to data element's name or local name (<code>Name</code> metadata on containment relationship)<br/>

            HTML elements are allowed; however, only a limited subset of tags is officially supported (bold &lt;b&gt;,
            italics &lt;i&gt;, underline &lt;u&gt;, superscript &lt;sup&gt;, subscript &lt;sub&gt;, line break &lt;br/&gt;,
            link &lt;a href=""&gt;, image &lt;img src=""&gt;).<br/>

            This field should be used as a way of describing the expected input to users entering or reviewing CRF data.
            The value of LEFT_ITEM_TEXT is displayed to the left of the form input.
            The text wraps after the first 20 characters.<br/>

            An example question would be "What is the subject's height?"  Or, a simple one word "Height" suffices as well.<br/>

            If the item is part of a repeating group (GRID), the LEFT_ITEM_TEXT is displayed as a column header above
            the field and not be displayed to the left of the item.<br/>
        </p>
      </div>
      <div class="form-group">
        <label for="item-description" class="control-label">Description Label</label>
        <textarea maxlength="4000" rows="5" class="form-control" id="item-description" ng-model="object.access('http://forms.modelcatalogue.org/item#description')" ng-model-options="{ getterSetter: true }"></textarea>
        <p class="help-block">
          The description or definition of the item. Should give an explanation of the data element and the value(s) it
          captures. It is not shown on the CRF but is in the data dictionary. Defaults to data element's description.'<br/>

          For example, if the variable were looking to collect HEIGHT, the DESCRIPTION_LABEL would be "This variable
          collects the height of the subject.  It captures the value in inches."<br/>

          This field should not be changed in any subsequent versions of the CRF. If you do change it  and you are the
          owner of the CRF the DESCRIPTION_LABEL attribute for this item will be changed for all versions of the CRF.<br/>
        </p>
      </div>
      <div class="form-group">
        <label for="item-default-value" class="control-label">Default Value</label>
        <input maxlength="4000" type="text" class="form-control" id="item-default-value" ng-model="object.access('http://forms.modelcatalogue.org/item#defaultValue')" ng-model-options="{ getterSetter: true }">
        <p class="help-block">
            This field allows the user to specify a default value that will appear in the CRF section the first time the user
            accesses the form.  For single-select default value does not have to be part of the response set and can be
            instructive text if need be.  It will be interpreted as a blank value if the user does not choose anything.<br/>

            Be careful in using this field because if the default value corresponds to an option in the response set, it will
            be saved to the database even if the user does not select it.
        </p>
      </div>
      <div class="form-group">
        <label for="item-instructions" class="control-label">Instructions (Right Item Text)</label>
        <textarea maxlength="2000" rows="5" class="form-control" id="item-instructions" ng-model="object.access('http://forms.modelcatalogue.org/item#instructions')" ng-model-options="{ getterSetter: true }"></textarea>
        <p class="help-block">
            Descriptive text that appears to the right of the form input on the CRF, and to the right of any UNITS that are
            specified too. Often phrased in the form of a question, or supporting instructions for the form field input.<br/>

            HTML elements are allowed; however, only a limited subset of tags is officially supported (bold &lt;b&gt;,
            italics &lt;i&gt;, underline &lt;u&gt;, superscript &lt;sup&gt;, subscript &lt;sub&gt;, line break &lt;br/&gt;,
            link &lt;a href=""&gt;, image &lt;img src=""&gt;).<br/>

            This field can be used as a way of describing the expected input to users entering or for field-specific
            instructions. The value of RIGHT_ITEM_TEXT is displayed to the right of the form input. The text wraps after
            the first 20 characters.<br/>

            An example of use of right item text is "If other, please specify".<br/>

            If the item is part of a repeating group (GRID), the RIGHT_ITEM_TEXT will be ignored and never displayed.<br/>
        </p>
      </div>
      <div class="checkbox">
      <label>
        <input type="checkbox" ng-model="object.access('http://forms.modelcatalogue.org/item#required')" ng-model-options="{ getterSetter: true }">
        Required
      </label>
      <p class="help-block">
        This field determines whether the user must provide a value for it before saving the section the item appears in. Defaults to <code>true</code> if data element's <code>Min Occurs</code> is <code>1</code><br/>

        Leaving the field blank means the item would be optional so the data entry person does not have to
        provide a value for it.  If selected, the data entry person must provide a value, or enter a discrepancy
        note explaining why the field is left blank. This can be used for any RESPONSE_TYPE.
      </p>
    </div>
    <div class="checkbox">
      <label>
        <input type="checkbox" ng-model="object.access('http://forms.modelcatalogue.org/item#layout')" ng-model-options="{ getterSetter: true }" ng-true-value="'horizontal'" ng-false-value="''">
        Horizontal (for checkbox or radio)
      </label>
      <p class="help-block">
        The layout of the options for radio and checkbox fields.

        The options can be left to right, or top to bottom depending on the value specified in the Items worksheet.

        Leaving unchecked the items will be displayred in a single column from top to bottom.
        Choosing Horizontal will put the items in a single row, left to right.
      </p>
    </div>
    <div class="form-group">
      <label for="item-column-number" class="control-label">Column Number</label>
      <input type="number" min="1" max="3" class="form-control" id="item-column-number" ng-model="object.access('http://forms.modelcatalogue.org/item#columnNumber').asInt" ng-model-options="{ getterSetter: true }">
      <p class="help-block">
        Assigns items to an item group.<br/>

        This is to be used with only non-repeating items and controls display of multiple items on a single row.
        If you set the column to 3 for an item, the previous two items in the worksheet should have COLUMN_NUMBERS
        of 1 and 2.  Otherwise, it will just be applied to the first column.<br/>

        Use of COLUMN_NUMBERS greater than 3 is not recommended due to typical screen width limitations.<br/>
      </p>
    </div>
    <div class="form-group">
      <label for="item-question-number" class="control-label">Question Number</label>
      <input maxlength="20" type="text" class="form-control" id="item-question-number" ng-model="object.access('http://forms.modelcatalogue.org/item#questionNumber')" ng-model-options="{ getterSetter: true }">
      <p class="help-block">
          This field is used to specify an identifier for each item or question in the Items worksheet.  It appears to the
          left of the LEFT_ITEM_TEXT field, or if that field was left blank, to the left of the form input.

          This field allows you to specify questions as 1, 2, 2a etc. in a field.
      </p>
    </div>
    <div class="form-group">
	    <label for="item-data-type" class="control-label">Data Type</label>
	    <select id="item-data-type" class="form-control" ng-options="key for (key, value) in {'String':'string', 'Integer': 'int', 'Real':'real', 'Date':'date', 'Partial Date': 'pdate'}" ng-model="object.access('http://forms.modelcatalogue.org/item#dataType')" ng-model-options="{ getterSetter: true }"></select>
	    <p class="help-block">
	      Data type of the item. If you want to use data type <code>file</code> select the <code>File</code> response type.<br/>
	      XMLSchema and Java data types are mapped to their proper data types automatically. This includes if the current data type is based on one of the data types from XMLSchema or Java classification. For a partial date, use <code>xs:gMonthYear</code> data type.
	    </p>
	</div>
    <div class="form-group">
	    <label for="item-response-type" class="control-label">Response Type</label>
	    <select id="item-response-type" class="form-control" ng-options="key for (key, value) in {'Text':'text', 'Textarea': 'textarea', 'Single Select':'singleselect', 'Radio':'radio', 'Multi Select': 'multiselect', 'Checkbox': 'checkbox', 'File': 'file'}" ng-model="object.access('http://forms.modelcatalogue.org/item#responseType')" ng-model-options="{ getterSetter: true }"></select>
	    <p class="help-block">
	      Response type of the item. If any metadata related to the item doesn't make sense for given context (e.g. default value for file) they are ignored. Reponse type is inherited from data type's base types.<br/>

	      If data type name is <code>File</code> the default response type is <code>File</code>. If data type is enumeration the default value is <code>Single Select</code>
	      if data element's <code>Max Occurs</code> is <code>1</code>. This can be changed to <code>Radio</code>.
	      For other data types the default value is <code>Text</code> but can be customized to <code>Textarea</code>.
	    </p>
	  </div>
	  <div class="form-group">
	    <label for="item-units" class="control-label">Units</label>
	    <input maxlength="64" type="text" class="form-control" id="item-units" ng-model="object.access('http://forms.modelcatalogue.org/item#units')" ng-model-options="{ getterSetter: true }">
	    <p class="help-block">
	        Used to define the type of values being collected.  It appears to the right of the input field on the CRF. Defaults to the symbol of primitive type's unit of measure.<br/>

	        If you are collecting data in Inches, this field can specify your units as Inches, IN, or in.
	        This field should not be changed in any subsequent versions of the CRF. If you do change it and you are the owner
	        of the CRF and no data have been entered for this item, the UNITS attribute for this item will be changed for all
	        versions of the CRF.<br/>

	        There are no edit checks associated specifically with units. This will appear as text to right of the input field
	        and will be displayed between parenthesis.<br/>

	        If you are exporting to CDISC ODM XML format, this will appear in the metadata as measurement units.<br/>

	        Can contain up to 5 characters.
	    </p>
	  </div>
	  <div class="form-group">
	    <label for="item-length" class="control-label">Max Length (String) or Number of All Digits (Numbers)</label>
	    <input type="number" min="1" max="2000" class="form-control" id="item-length" ng-model="object.access('http://forms.modelcatalogue.org/item#length').asInt" ng-model-options="{ getterSetter: true }">
	    <p class="help-block">
	      Maximal length of string (up to 2000) or number of digits of the number (up to 26).
	    </p>
	  </div>
	  <div class="form-group">
	    <label for="item-digits" class="control-label">Number of Decimal Digits</label>
	    <input type="number" min="0" max="20" class="form-control" id="item-digits" ng-model="object.access('http://forms.modelcatalogue.org/item#digits').asInt" ng-model-options="{ getterSetter: true }">
	    <p class="help-block">
	      Number of digits after decimal point. Max 20.
	    </p>
	  </div>
	  <div class="form-group">
	    <label for="item-regexp" class="control-label">Regular Expression</label>
	    <input maxlength="1000" type="text" class="form-control" id="item-regexp" ng-model="object.access('http://forms.modelcatalogue.org/item#regexp')" ng-model-options="{ getterSetter: true }">
	    <p class="help-block">
	      Regular expresion the value has match. Defaults to regular expression set for the data type if it is in the format <code>x ==~ /\\d+(\\.\\d+)?/</code>
	    </p>
	  </div>
	  <div class="form-group">
	    <label for="item-regexp-message" class="control-label">Regular Expression Error Message</label>
	    <input maxlength="1000" type="text" class="form-control" id="item-regexp-message" ng-model="object.access('http://forms.modelcatalogue.org/item#regexpErrorMessage')" ng-model-options="{ getterSetter: true }">
	    <p class="help-block">
	      Message shown if the validation fails. Defaults to "Value must match /regexpDef/"
	    </p>
	  </div>

    </form>
  '''
]
