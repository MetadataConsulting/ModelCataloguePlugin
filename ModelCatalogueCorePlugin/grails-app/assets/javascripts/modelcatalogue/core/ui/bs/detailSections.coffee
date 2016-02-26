metadataEditors = angular.module('mc.core.ui.bs.detailSections', ['mc.core.ui.detailSections'])


###
  If you need to put more explanation for certain title put following snippet after the label's strong element and
  update it to your own help text.

  <span class="fa fa-question-circle text-muted" tooltip="These are the authors of the data model"></span>
###

metadataEditors.run ['$templateCache', ($templateCache) ->
  $templateCache.put 'modelcatalogue/core/ui/detailSections/dataModelBasic.html', '''
      <div class="col-md-6">
        <div class="row">
          <div class="col-md-6"><strong class="small">Authors</strong></div>
          <div class="full-width-editable col-md-6"><small editable-text="extAsMap['http://www.modelcatalogue.org/metadata/#authors']">{{element.ext.get('http://www.modelcatalogue.org/metadata/#authors') || 'empty'}}</small></div>
        </div>
        <div class="row">
          <div class="col-md-6"><strong class="small">Reviewers</strong></div>
          <div class="full-width-editable col-md-6"><small editable-text="extAsMap['http://www.modelcatalogue.org/metadata/#reviewers']">{{element.ext.get('http://www.modelcatalogue.org/metadata/#reviewers') || 'empty'}}</small></div>
        </div>
        <div class="row">
          <div class="col-md-6"><strong class="small">Owner</strong></div>
          <div class="full-width-editable col-md-6"><small editable-text="extAsMap['http://www.modelcatalogue.org/metadata/#owner']">{{element.ext.get('http://www.modelcatalogue.org/metadata/#owner') || 'empty'}}</small></div>
        </div>
      </div>
      <div class="col-md-6">
        <div class="row">
          <div class="col-md-6"><strong class="small">Last Updated</strong></div>
          <div class="col-md-6"><small>{{element.lastUpdated | date}}</small></div>
        </div>
        <div class="row">
          <div class="col-md-6"><strong class="small">Version Created</strong></div>
          <div class="col-md-6"><small>{{element.versionCreated | date}}</small></div>
        </div>
        <div class="row">
          <div class="col-md-6"><strong class="small">Status</strong></div>
          <div class="col-md-6"><small>{{element.status}}</small></div>
        </div>
      </div>
  '''
  $templateCache.put 'modelcatalogue/core/ui/detailSections/dataClassBasic.html', '''
      <div class="col-md-6">
        <div class="row">
          <div class="col-md-6"><strong class="small">Last Updated</strong></div>
          <div class="col-md-6"><small>{{element.lastUpdated | date}}</small></div>
        </div>
        <div class="row">
          <div class="col-md-6"><strong class="small">Version Created</strong></div>
          <div class="col-md-6"><small>{{element.versionCreated | date}}</small></div>
        </div>
        <div class="row">
          <div class="col-md-6"><strong class="small">Status</strong></div>
          <div class="col-md-6"><small>{{element.status}}</small></div>
        </div>
      </div>
  '''

  $templateCache.put 'modelcatalogue/core/ui/detailSections/dataElementBasic.html', '''
      <div class="col-md-12">
        <div class="row">
          <div class="col-md-3"><strong class="small">Last Updated</strong></div>
          <div class="col-md-3"><small>{{element.lastUpdated | date}}</small></div>
          <div class="col-md-3"><strong class="small">Status</strong></div>
          <div class="col-md-3"><small>{{element.status}}</small></div>
        </div>
        <div class="row">
          <div class="col-md-3"><strong class="small">Version Created</strong></div>
          <div class="col-md-3"><small>{{element.versionCreated | date}}</small></div>
        </div>
        <div class="row">
        </div>
      </div>
  '''


  $templateCache.put 'modelcatalogue/core/ui/detailSections/assetBasic.html', '''
      <div class="col-md-6">
        <div class="row">
          <div class="col-md-6"><strong class="small">Original File Name</strong></div>
          <div class="col-md-6"><small>{{element.fileName || 'none'}}</small></div>
        </div>
        <div class="row">
          <div class="col-md-6"><strong class="small">Content Type</strong></div>
          <div class="col-md-6"><small>{{element.contentType || 'unknown'}}</small></div>
        </div>
        <div class="row">
          <div class="col-md-6"><strong class="small">Size</strong></div>
          <div class="col-md-6"><small>{{view.toHumanReadableSize(element.size || 0)}}</small></div>
        </div>
      </div>
      <div class="col-md-6">
        <div class="row">
          <div class="col-md-6"><strong class="small">Last Updated</strong></div>
          <div class="col-md-6"><small>{{element.lastUpdated | date}}</small></div>
        </div>
        <div class="row">
          <div class="col-md-6"><strong class="small">Version Created</strong></div>
          <div class="col-md-6"><small>{{element.versionCreated | date}}</small></div>
        </div>
        <div class="row">
          <div class="col-md-6"><strong class="small">Status</strong></div>
          <div class="col-md-6"><small>{{element.status}}</small></div>
        </div>
      </div>
  '''

  $templateCache.put 'modelcatalogue/core/ui/detailSections/measurementUnitBasic.html', '''
      <div class="col-md-6">
        <div class="row">
          <div class="col-md-6"><strong class="small">Symbol</strong></div>
          <div class="full-width-editable col-md-6"><small editable-text="copy.symbol">{{element.symbol || 'empty'}}</small></div>
        </div>
      </div>
      <div class="col-md-6">
        <div class="row">
          <div class="col-md-6"><strong class="small">Last Updated</strong></div>
          <div class="col-md-6"><small>{{element.lastUpdated | date}}</small></div>
        </div>
        <div class="row">
          <div class="col-md-6"><strong class="small">Version Created</strong></div>
          <div class="col-md-6"><small>{{element.versionCreated | date}}</small></div>
        </div>
        <div class="row">
          <div class="col-md-6"><strong class="small">Status</strong></div>
          <div class="col-md-6"><small>{{element.status}}</small></div>
        </div>
      </div>
  '''

  $templateCache.put 'modelcatalogue/core/ui/detailSections/organization.html', '''
      <div class="col-md-3">
          <strong class="small">Organization</strong>
      </div>
      <div class="full-width-editable col-md-9"><small editable-text="extAsMap['http://www.modelcatalogue.org/metadata/#organization']">{{element.ext.get('http://www.modelcatalogue.org/metadata/#organization') || 'empty'}}</small></div>
      <div class="col-md-3">
          <strong class="small">Namespace</strong>
      </div>
      <div class="full-width-editable col-md-9"><small editable-text="extAsMap['http://www.modelcatalogue.org/metadata/#namespace']">{{element.ext.get('http://www.modelcatalogue.org/metadata/#namespace') || 'empty'}}</small></div>
  '''

  $templateCache.put 'modelcatalogue/core/ui/detailSections/modelCatalogueId.html', '''
      <div class="col-md-3">
          <strong class="small">Model Catalogue ID</strong>
      </div>
      <div class="col-md-9"><small>{{element.internalModelCatalogueId}}</small></div>
      <div class="col-md-3" ng-if="element.modelCatalogueId &amp;&amp; element.modelCatalogueId != element.internalModelCatalogueId">
          <strong class="small">External ID (URL)</strong>
      </div>
      <div class="full-width-editable col-md-9" ng-if="element.modelCatalogueId &amp;&amp; element.modelCatalogueId != element.internalModelCatalogueId"><a class="small" ng-href="{{element.modelCatalogueId}}" editable-text="copy.modelCatalogueId">{{element.modelCatalogueId}}</a></div>
  '''

  $templateCache.put 'modelcatalogue/core/ui/detailSections/customMetadata.html', '''
      <div class="col-md-3" ng-repeat-start="value in (editableForm.$visible ? [] : customMetadata.values)">
          <strong class="small">{{value.key}}</strong>
      </div>
      <div class="col-md-9 preserve-new-lines" ng-repeat-end><small>{{value.value}}</small></div>
      <div class="custom-metadata col-md-12" ng-if="editableForm.$visible">
          <ordered-map-editor object="customMetadata"></ordered-map-editor>
      </div>
  '''

  $templateCache.put 'modelcatalogue/core/ui/detailSections/formItem.html', '''
      <div class="col-md-12">
        <div class="row">
          <div class="col-md-3">
            <strong class="small">Protected Health Information</strong>
            <i class="glyphicon glyphicon-question-sign"
               popover="
                Signifies whether this item would be considered Protected Health Information.

                Leaving the field blank or selecting 0 means the item would not be considered Protected Health Information. This flag does not do anything to mask the data or prevent people from seeing it. The field is used as a label only.

                When creating a data set, this label will show in the metadata and the user could choose to include this item in the dataset (create dataset step) or not based on this label.

                This field should not be changed in any subsequent versions of the CRF. If you do change it and you are an owner of the CRF the PHI attribute for this item will be changed for all versions of the CRF.
               "
               popover-title="Protected Health Information"
               popover-trigger="mouseenter"/>
          </div>
          <div class="col-md-9">
            <span editable-checkbox="extAsMap['http://forms.modelcatalogue.org/item#phi']" class="small" e-string-to-boolean>
              {{ element.ext.get('http://forms.modelcatalogue.org/item#phi') ? (element.ext.get('http://forms.modelcatalogue.org/item#phi') == "false" ? "No": "Yes") : "" }}
            </span>
          </div>
        </div>
        <div class="row">
          <div class="col-md-3">
            <strong class="small">Question (Left Item Text)</strong>
            <i class="glyphicon glyphicon-question-sign"
               popover="
                    Descriptive text that appears to the left of the input on the CRF. Often phrased in the form of a question, or descriptive label for the form field input. Defaults to data element's name or local name (Name metadata on containment relationship)

                    HTML elements are allowed; however, only a limited subset of tags is officially supported (bold &lt;b&gt;, italics &lt;i&gt;, underline &lt;u&gt;, superscript &lt;sup&gt;, subscript &lt;sub&gt;, line break &lt;br/&gt;, link &lt;a href=&quot;&quot;&gt;, image &lt;img src=&quot;&quot;&gt;).

                    This field should be used as a way of describing the expected input to users entering or reviewing CRF data. The value of LEFT_ITEM_TEXT is displayed to the left of the form input. The text wraps after the first 20 characters.

                    An example question would be &quot;What is the subject's height?&quot;  Or, a simple one word &quot;Height&quot; suffices as well.

                    If the item is part of a repeating group (GRID), the LEFT_ITEM_TEXT is displayed as a column header above the field and not be displayed to the left of the item."
               popover-title="Question (Left Item Text)"
               popover-trigger="mouseenter"/>
          </div>
          <div class="col-md-9 full-width-editable">
            <span editable-text="extAsMap['http://forms.modelcatalogue.org/item#question']" e-maxlength="2000" class="small">
              {{element.ext.get('http://forms.modelcatalogue.org/item#question')}}
            </span>
          </div>
        </div>
        <div class="row">
          <div class="col-md-3">
            <strong class="small">Description Label</strong>
            <i class="glyphicon glyphicon-question-sign"
                popover="
                  The description or definition of the item. Should give an explanation of the data element and the value(s) it captures. It is not shown on the CRF but is in the data dictionary. Defaults to data element&#39;s description.&#39;

                  For example, if the variable were looking to collect HEIGHT, the DESCRIPTION_LABEL would be &quot;This variable collects the height of the subject.  It captures the value in inches.&quot;

                  This field should not be changed in any subsequent versions of the CRF. If you do change it  and you are the owner of the CRF the DESCRIPTION_LABEL attribute for this item will be changed for all versions of the CRF."
                popover-title="Description Label"
                popover-trigger="mouseenter"/>
          </div>
          <div class="col-md-9 full-width-editable">
            <small editable-textarea="extAsMap['http://forms.modelcatalogue.org/item#description']" e-maxlength="4000" e-rows="5">
              {{element.ext.get('http://forms.modelcatalogue.org/item#description')}}
            </small>
          </div>
        </div>
        <div class="row">
          <div class="col-md-3">
            <strong class="small">Default Value</strong>
            <i class="glyphicon glyphicon-question-sign"
                popover="
                  This field allows the user to specify a default value that will appear in the CRF section the first time the user accesses the form. For single-select default value does not have to be part of the response set and can be instructive text if need be.  It will be interpreted as a blank value if the user does not choose anything.

                  Be careful in using this field because if the default value corresponds to an option in the response set, it will be saved to the database even if the user does not select it."
                popover-title="Default Value"
                popover-trigger="mouseenter"/>
          </div>
          <div class="col-md-9 full-width-editable">
            <small editable-text="extAsMap['http://forms.modelcatalogue.org/item#defaultValue']" e-maxlength="4000">
              {{element.ext.get('http://forms.modelcatalogue.org/item#defaultValue')}}
            </small>
          </div>
        </div>
        <div class="row">
          <div class="col-md-3">
            <strong class="small">Instructions (Right Item Text)</strong>
            <i class="glyphicon glyphicon-question-sign"
                popover="
                  Descriptive text that appears to the right of the form input on the CRF, and to the right of any UNITS that are specified too. Often phrased in the form of a question, or supporting instructions for the form field input.

                  HTML elements are allowed; however, only a limited subset of tags is officially supported (bold &lt;b&gt;, italics &lt;i&gt;, underline &lt;u&gt;, superscript &lt;sup&gt;, subscript &lt;sub&gt;, line break &lt;br/&gt;, link &lt;a href=&quot;&quot;&gt;, image &lt;img src=&quot;&quot;&gt;).

                  This field can be used as a way of describing the expected input to users entering or for field-specific instructions. The value of RIGHT_ITEM_TEXT is displayed to the right of the form input. The text wraps after the first 20 characters.

                  An example of use of right item text is &quot;If other, please specify&quot;.

                  If the item is part of a repeating group (GRID), the RIGHT_ITEM_TEXT will be ignored and never displayed."
                popover-title="Instructions (Right Item Text)"
                popover-trigger="mouseenter"/>
          </div>
          <div class="col-md-9 full-width-editable">
            <small editable-textarea="extAsMap['http://forms.modelcatalogue.org/item#instructions']" e-maxlength="2000" e-rows="5">
              {{element.ext.get('http://forms.modelcatalogue.org/item#instructions')}}
            </small>
          </div>
        </div>
        <div class="row">
          <div class="col-md-3">
            <strong class="small">Required</strong>
            <i class="glyphicon glyphicon-question-sign"
               popover="
                This field determines whether the user must provide a value for it before saving the section the item appears in. Defaults to true if data element's Min Occurs is 1

                Leaving the field blank means the item would be optional so the data entry person does not have to provide a value for it.  If selected, the data entry person must provide a value, or enter a discrepancy note explaining why the field is left blank. This can be used for any RESPONSE_TYPE."
               popover-title="Required"
               popover-trigger="mouseenter"/>
          </div>
          <div class="col-md-9">
            <span editable-checkbox="extAsMap['http://forms.modelcatalogue.org/item#required']" class="small" e-string-to-boolean>
              {{ element.ext.get('http://forms.modelcatalogue.org/item#required') ? (element.ext.get('http://forms.modelcatalogue.org/item#required') == "false" ? "No": "Yes") : "" }}
            </span>
          </div>
        </div>
        <div class="row">
          <div class="col-md-3">
            <strong class="small">Horizontal (for checkbox or radio)</strong>
            <i class="glyphicon glyphicon-question-sign"
                popover="
                  The layout of the options for radio and checkbox fields.

                  The options can be left to right, or top to bottom depending on the value specified in the Items worksheet.

                  Leaving unchecked the items will be displayred in a single column from top to bottom. Choosing Horizontal will put the items in a single row, left to right."
                popover-title="Horizontal (for checkbox or radio)"
                popover-trigger="mouseenter"/>
          </div>
          <div class="col-md-9 full-width-editable">
            <span editable-checkbox="extAsMap['http://forms.modelcatalogue.org/item#layout']" class="small" e-string-to-boolean>
              {{ element.ext.get('http://forms.modelcatalogue.org/item#layout') ? (element.ext.get('http://forms.modelcatalogue.org/item#layout') == "false" ? "No": "Yes") : "" }}
            </span>
          </div>
        </div>
        <div class="row">
          <div class="col-md-3">
            <strong class="small">Column Number</strong>
            <i class="glyphicon glyphicon-question-sign"
                popover="
                  Assigns items to an item group.

                  This is to be used with only non-repeating items and controls display of multiple items on a single row. If you set the column to 3 for an item, the previous two items in the worksheet should have COLUMN_NUMBERS of 1 and 2.  Otherwise, it will just be applied to the first column.

                  Use of COLUMN_NUMBERS greater than 3 is not recommended due to typical screen width limitations."
                popover-title="Column Number"
                popover-trigger="mouseenter"/>
          </div>
          <div class="col-md-9 full-width-editable">
            <small editable-number="extAsMap['http://forms.modelcatalogue.org/item#columnNumber']" e-min="1" e-max="3" e-string-to-number>
              {{element.ext.get('http://forms.modelcatalogue.org/item#columnNumber')}}
            </small>
          </div>
        </div>
        <div class="row">
          <div class="col-md-3">
            <strong class="small">Question Number</strong>
            <i class="glyphicon glyphicon-question-sign"
                popover="
                  This field is used to specify an identifier for each item or question in the Items worksheet.  It appears to the left of the LEFT_ITEM_TEXT field, or if that field was left blank, to the left of the form input.

                  This field allows you to specify questions as 1, 2, 2a etc. in a field."
                popover-title="Question Number"
                popover-trigger="mouseenter"/>
          </div>
          <div class="col-md-9 full-width-editable">
            <small editable-text="extAsMap['http://forms.modelcatalogue.org/item#questionNumber']" e-maxlength="20">
              {{element.ext.get('http://forms.modelcatalogue.org/item#questionNumber')}}
            </small>
          </div>
        </div>
        <div class="row">
          <div class="col-md-3">
            <strong class="small">Data Type</strong>
            <i class="glyphicon glyphicon-question-sign"
                popover="
                  Data type of the item. If you want to use data type file select the File response type.
                  XMLSchema and Java data types are mapped to their proper data types automatically. This includes if the current data type is based on one of the data types from XMLSchema or Java classification. For a partial date, use xs:gMonthYear data type."
                popover-title="Data Type"
                popover-trigger="mouseenter"/>
          </div>
          <div class="col-md-9 full-width-editable">
            <small editable-select="extAsMap['http://forms.modelcatalogue.org/item#dataType']"
                   e-ng-options="key for (key, value) in {'String':'string', 'Integer': 'int', 'Real':'real', 'Date':'date', 'Partial Date': 'pdate'}">
              {{element.ext.get('http://forms.modelcatalogue.org/item#dataType')}}
            </small>
          </div>
        </div>
        <div class="row">
          <div class="col-md-3">
            <strong class="small">Response Type</strong>
            <i class="glyphicon glyphicon-question-sign"
                popover="
                  Response type of the item. If any metadata related to the item doesn't make sense for given context (e.g. default value for file) they are ignored. Reponse type is inherited from data type's base types.

                  If data type name is File the default response type is File. If data type is enumeration the default value is Single Select if data element's Max Occurs is 1. This can be changed to Radio. For other data types the default value is Text but can be customized to Textarea."
                popover-title="Response Type"
                popover-trigger="mouseenter"/>
          </div>
          <div class="col-md-9 full-width-editable">
            <small editable-select="extAsMap['http://forms.modelcatalogue.org/item#responseType']"
                   e-ng-options="key for (key, value) in {'Text':'text', 'Textarea': 'textarea', 'Single Select':'singleselect', 'Radio':'radio', 'Multi Select': 'multiselect', 'Checkbox': 'checkbox', 'File': 'file'}">
              {{element.ext.get('http://forms.modelcatalogue.org/item#responseType')}}
            </small>
          </div>
        </div>
        <div class="row">
          <div class="col-md-3">
            <strong class="small">Units</strong>
            <i class="glyphicon glyphicon-question-sign"
                popover="
                  Used to define the type of values being collected.  It appears to the right of the input field on the CRF. Defaults to the symbol of primitive type's unit of measure.

                  If you are collecting data in Inches, this field can specify your units as Inches, IN, or in. This field should not be changed in any subsequent versions of the CRF. If you do change it and you are the owner of the CRF and no data have been entered for this item, the UNITS attribute for this item will be changed for all versions of the CRF.

                  There are no edit checks associated specifically with units. This will appear as text to right of the input field and will be displayed between parenthesis.

                  If you are exporting to CDISC ODM XML format, this will appear in the metadata as measurement units.

                  Can contain up to 5 characters."
                popover-title="Units"
                popover-trigger="mouseenter"/>
          </div>
          <div class="col-md-9 full-width-editable">
            <small editable-text="extAsMap['http://forms.modelcatalogue.org/item#units']" e-maxlength="64">
              {{element.ext.get('http://forms.modelcatalogue.org/item#units')}}
            </small>
          </div>
        </div>
        <div class="row">
          <div class="col-md-3">
            <strong class="small">Max Length (String) or Number of All Digits (Numbers)</strong>
            <i class="glyphicon glyphicon-question-sign"
                popover="Maximal length of string (up to 2000) or number of digits of the number (up to 26)."
                popover-title="Max Length (String) or Number of All Digits (Numbers)"
                popover-trigger="mouseenter"/>
          </div>
          <div class="col-md-9 full-width-editable">
            <small editable-number="extAsMap['http://forms.modelcatalogue.org/item#length']" e-min="1" e-max="2000" e-string-to-number>
              {{element.ext.get('http://forms.modelcatalogue.org/item#length')}}
            </small>
          </div>
        </div>
        <div class="row">
          <div class="col-md-3">
            <strong class="small">Number of Decimal Digits</strong>
            <i class="glyphicon glyphicon-question-sign"
                popover="Number of digits after decimal point. Max 20."
                popover-title="Number of Decimal Digits"
                popover-trigger="mouseenter"/>
          </div>
          <div class="col-md-9 full-width-editable">
            <small editable-number="extAsMap['http://forms.modelcatalogue.org/item#digits']" e-min="0" e-max="20" e-string-to-number>
              {{element.ext.get('http://forms.modelcatalogue.org/item#digits')}}
            </small>
          </div>
        </div>
        <div class="row">
          <div class="col-md-3">
            <strong class="small">Regular Expression</strong>
            <i class="glyphicon glyphicon-question-sign"
                popover="Regular expresion the value has match. Defaults to regular expression set for the data type if it is in the format x ==~ /\\d+(\\.\\d+)?/"
                popover-title="Regular Expression"
                popover-trigger="mouseenter"/>
          </div>
          <div class="col-md-9 full-width-editable">
            <small editable-text="extAsMap['http://forms.modelcatalogue.org/item#regexp']" e-maxlength="1000">
              {{element.ext.get('http://forms.modelcatalogue.org/item#regexp')}}
            </small>
          </div>
        </div>
        <div class="row">
          <div class="col-md-3">
            <strong class="small">Regular Expression Error Message</strong>
            <i class="glyphicon glyphicon-question-sign"
                popover-html="Message shown if the validation fails. Defaults to &quot;Value must match /regexpDef/&quot;"
                popover-title="Regular Expression Error Message"
                popover-trigger="mouseenter"/>
          </div>
          <div class="col-md-9 full-width-editable">
            <small editable-text="extAsMap['http://forms.modelcatalogue.org/item#regexpErrorMessage']" e-maxlength="1000">
              {{element.ext.get('http://forms.modelcatalogue.org/item#regexpErrorMessage')}}
            </small>
          </div>
        </div>
      </div>
  '''

  $templateCache.put 'modelcatalogue/core/ui/detailSections/enumerations.html', '''
      <div class="col-md-12" ng-if="!editableForm.$visible &amp;&amp; element.enumerations.values"><strong class="small">Enumerations</strong></div>
      <div class="col-md-3" ng-repeat-start="value in (editableForm.$visible ? [] : element.enumerations.values)">
          <strong class="small">{{value.key}}</strong>
      </div>
      <div class="col-md-9 preserve-new-lines" ng-repeat-end><small>{{value.value}}</small></div>
      <div class="custom-metadata col-md-12" ng-if="editableForm.$visible">
          <div ng-if="copy.ext.get('http://www.modelcatalogue.org/metadata/enumerateType#subset')" class="alert alert-warning">Following values are inherited and will be overriden when the base enumeration changes: {{copy.ext.get('http://www.modelcatalogue.org/metadata/enumerateType#subset')}}</div>
          <ordered-map-editor object="copy.enumerations" title="Enumerations" key-placeholder="Value" value-placeholder="Description"></ordered-map-editor>
      </div>
  '''


  $templateCache.put 'modelcatalogue/core/ui/detailSections/revisionNotes.html', '''
      <div class="col-md-3">
          <strong class="small">Revision Notes</strong>
      </div>
      <div class="full-width-editable col-md-9 preserve-new-lines"><small editable-textarea="copy.revisionNotes" e-rows="5" e-cols="1000">{{element.revisionNotes || 'empty'}}</small></div>
  '''


  $templateCache.put 'modelcatalogue/core/ui/detailSections/description.html', '''
      <div class="col-md-3">
          <strong class="small">Description</strong>
      </div>
      <div class="full-width-editable col-md-9 preserve-new-lines"><small editable-textarea="copy.description" e-rows="5" e-cols="1000" class="ce-description">{{element.description || 'empty'}}</small></div>
  '''

  $templateCache.put 'modelcatalogue/core/ui/detailSections/rule.html', '''
      <div class="col-md-3">
          <strong class="small">Rule</strong>
      </div>
      <div class="full-width-editable col-md-9 preserve-new-lines">
        <pre editable-textarea="copy.rule" e-rows="5" e-cols="1000" class="ce-rule small">{{element.rule || 'empty'}}</pre>
        <p ng-if="editableForm.$visible" class="help-block">Enter valid <a href="http://www.groovy-lang.org/" target="_blank">Groovy</a> code. Variable <code>x</code> refers to the value validated value and  <code>dataType</code> to current data type. Last row is the result which should be <code>boolean</code> value. For example you can <a ng-click="view.showRegexExample(copy, messages)"><span class="fa fa-magic"></span> validate using regular expression</a> or <a ng-click="view.showSetExample(copy, messages)"><span class="fa fa-magic"></span> values in set</a></p>
      </div>
  '''

  $templateCache.put 'modelcatalogue/core/ui/detailSections/measurementUnit.html', '''
      <div class="col-md-3">
          <strong class="small">Measurement Unit</strong>
      </div>
      <div class="full-width-editable col-md-9">
          <div ng-if="editableForm.$visible">
            <input type="text" id="measurementUnit" placeholder="Measurement Unit" ng-model="copy.measurementUnit" catalogue-element-picker="measurementUnit" label="el.name">
          </div>
          <span class="editable-empty" ng-if="!editableForm.$visible &amp;&amp; !element.measurementUnit">empty</span>
          <a ng-if="!editableForm.$visible &amp;&amp; element.measurementUnit" class="small with-pointer" ng-href="{{element.measurementUnit.href()}}">
            <span ng-class="element.measurementUnit.getIcon()">&nbsp;</span>
            <span class="unit-name">{{element.measurementUnit.name}}&nbsp;</span>
            <small>
              <a ng-if="!editableForm.$visible" ng-href="{{element.mesurementUnit.dataModel.href()}}" class="label" ng-class="{'label-warning': element.measurementUnit.getDataModelStatus() == 'DRAFT', 'label-info': element.measurementUnit.getDataModelStatus() == 'PENDING', 'label-primary': element.measurementUnit.getDataModelStatus() == 'FINALIZED', 'label-danger': element.measurementUnit.getDataModelStatus() == 'DEPRECATED'}">{{element.measurementUnit.getDataModelWithVersion()}}</a>
            </small>
          </a>

      </div>
  '''

  $templateCache.put 'modelcatalogue/core/ui/detailSections/dataClass.html', '''
      <div class="col-md-3">
          <strong class="small">Data Class</strong>
      </div>
      <div class="full-width-editable col-md-9">
          <div ng-if="editableForm.$visible">
            <input type="text" id="dataClass" placeholder="Data Class" ng-model="copy.dataClass" catalogue-element-picker="dataClass" label="el.name">
          </div>
          <span class="editable-empty" ng-if="!editableForm.$visible &amp;&amp; !element.dataClass">empty</span>
          <a ng-if="!editableForm.$visible &amp;&amp; element.dataClass" class="small with-pointer" ng-href="{{element.dataClass.href()}}">
            <span ng-class="element.dataClass.getIcon()">&nbsp;</span>
            <span class="unit-name">{{element.dataClass.name}}&nbsp;</span>
            <small>
              <a ng-if="!editableForm.$visible" ng-href="{{element.dataClass.dataModel.href()}}" class="label" ng-class="{'label-warning': element.dataClass.getDataModelStatus() == 'DRAFT', 'label-info': element.dataClass.getDataModelStatus() == 'PENDING', 'label-primary': element.dataClass.getDataModelStatus() == 'FINALIZED', 'label-danger': element.dataClass.getDataModelStatus() == 'DEPRECATED'}">{{element.dataClass.getDataModelWithVersion()}}</a>
            </small>
          </a>

      </div>
  '''

  $templateCache.put 'modelcatalogue/core/ui/detailSections/dataType.html', '''
      <div class="col-md-3">
          <strong class="small">Data Type</strong>
      </div>
      <div class="full-width-editable col-md-9">
          <div ng-if="editableForm.$visible">
            <input type="text" id="dataType" placeholder="Data Type" ng-model="copy.dataType" catalogue-element-picker="dataType" label="el.name">
          </div>
          <span class="editable-empty" ng-if="!editableForm.$visible &amp;&amp; !element.dataType">empty</span>
          <a ng-if="!editableForm.$visible &amp;&amp; element.dataType" class="small with-pointer" ng-href="{{element.dataType.href()}}">
            <span ng-class="element.dataType.getIcon()">&nbsp;</span>
            <span class="unit-name">{{element.dataType.name}}&nbsp;</span>
            <small>
              <a ng-if="!editableForm.$visible"  ng-href="{{element.dataType.dataModel.href()}}" class="label" ng-class="{'label-warning': element.dataType.getDataModelStatus() == 'DRAFT', 'label-info': element.dataType.getDataModelStatus() == 'PENDING', 'label-primary': element.dataType.getDataModelStatus() == 'FINALIZED', 'label-danger': element.dataType.getDataModelStatus() == 'DEPRECATED'}">{{element.dataType.getDataModelWithVersion()}}</a>
            </small>
          </a>

      </div>
      <div class="small col-md-9 col-md-offset-3" ng-if="!editableForm.$visible &amp;&amp; element.dataType.measurementUnit">
          uses <a class="small with-pointer" ng-href="{{element.dataType.measurementUnit.href()}}">
                <span ng-class="element.dataType.measurementUnit.getIcon()">&nbsp;</span>
                <span class="unit-name">{{element.dataType.measurementUnit.name}}&nbsp;</span>
                <small>
                  <a ng-href="{{element.dataType.measurementUnit.dataModel.href()}}" class="label" ng-class="{'label-warning': element.dataType.measurementUnit.getDataModelStatus() == 'DRAFT', 'label-info': element.dataType.measurementUnit.getDataModelStatus() == 'PENDING', 'label-primary': element.dataType.measurementUnit.getDataModelStatus() == 'FINALIZED', 'label-danger': element.dataType.measurementUnit.getDataModelStatus() == 'DEPRECATED'}">{{element.dataType.measurementUnit.getDataModelWithVersion()}}</a>
                </small>
          </a>
      </div>
      <div class="small col-md-9 col-md-offset-3" ng-if="!editableForm.$visible &amp;&amp; element.dataType.dataClass">
          refers to <a class="small with-pointer" ng-href="{{element.dataType.dataClass.href()}}">
                <span ng-class="element.dataType.dataClass.getIcon()">&nbsp;</span>
                <span class="unit-name">{{element.dataType.dataClass.name}}&nbsp;</span>
                <small>
                  <a ng-href="{{element.dataType.dataClass.dataModel.href()}}" class="label" ng-class="{'label-warning': element.dataType.dataClass.getDataModelStatus() == 'DRAFT', 'label-info': element.dataType.dataClass.getDataModelStatus() == 'PENDING', 'label-primary': element.dataType.dataClass.getDataModelStatus() == 'FINALIZED', 'label-danger': element.dataType.dataClass.getDataModelStatus() == 'DEPRECATED'}">{{element.dataType.dataClass.getDataModelWithVersion()}}</a>
                </small>
          </a>
      </div>
      <div class="small col-md-9 col-md-offset-3" ng-if="!editableForm.$visible &amp;&amp; element.dataType.enumerations">
          <div class="row">
            <div class="col-md-12" ><strong class="small">Enumerations</strong></div>
            <div class="col-md-3" ng-repeat-start="value in element.dataType.enumerations.values">
              <strong class="small">{{value.key}}</strong>
            </div>
            <div class="col-md-9 preserve-new-lines" ng-repeat-end><small>{{value.value}}</small></div>
          </div>
      </div>
  '''

  $templateCache.put 'modelcatalogue/core/ui/detailSections/assetPreview.html', '''
      <div class="col-md-12">
          <img style="max-width: 100%" ng-src="{{element.downloadUrl}}" ng-if="element.contentType.indexOf('image/') == 0"/>
          <div ng-if="element.htmlPreview" ng-bind-html="element.htmlPreview"></div>
          <pre ng-if="element.contentType.indexOf('image/') != 0 &amp;&amp; !element.htmlPreview" class="text-center">No preview available</pre>
      </div>
  '''
]

metadataEditors.config ['detailSectionsProvider', (detailSectionsProvider)->
  detailSectionsProvider.register {
      title: 'Model Catalogue ID'
      position: -100000
      types: [
        'catalogueElement'
      ]
      keys: []
      template: 'modelcatalogue/core/ui/detailSections/modelCatalogueId.html'
  }

  detailSectionsProvider.register {
     title: 'Basic'
     position: -10000
     types: [
       'dataModel'
     ]
     keys: [
       'http://www.modelcatalogue.org/metadata/#authors'
       'http://www.modelcatalogue.org/metadata/#reviewers'
       'http://www.modelcatalogue.org/metadata/#owner'

     ]
     template: 'modelcatalogue/core/ui/detailSections/dataModelBasic.html'
  }

  detailSectionsProvider.register {
     title: 'Basic'
     position: -10000
     types: [
       'measurementUnit'
     ]
     keys: []
     template: 'modelcatalogue/core/ui/detailSections/measurementUnitBasic.html'
  }


  detailSectionsProvider.register {
     title: 'Basic'
     position: -10000
     types: [
       'asset'
     ]
     keys: []
     template: 'modelcatalogue/core/ui/detailSections/assetBasic.html'
     toHumanReadableSize: (size) ->
        GIGA = 1024 * 1024 * 1024
        MEGA = 1024 * 1024
        KILO = 1024
        return "#{(size / GIGA).toFixed(2)} GB" if size > GIGA
        return "#{(size / MEGA).toFixed(2)} MB" if size > MEGA
        return "#{(size / KILO).toFixed(2)} kB" if size > KILO
        return "#{size} B"

  }


  detailSectionsProvider.register {
     title: 'Basic'
     position: -10000
     types: [
       'dataClass'
     ]
     keys: []
     template: 'modelcatalogue/core/ui/detailSections/dataClassBasic.html'
  }

  detailSectionsProvider.register {
     title: 'Basic'
     position: -10000
     types: [
       'dataElement'
     ]
     keys: []
     template: 'modelcatalogue/core/ui/detailSections/dataElementBasic.html'
  }

  detailSectionsProvider.register {
     title: 'Namespace and Organization'
     position: 2000
     types: [
       'dataModel'
     ]
     keys: [
       'http://www.modelcatalogue.org/metadata/#namespace'
       'http://www.modelcatalogue.org/metadata/#organization'
     ]
     template: 'modelcatalogue/core/ui/detailSections/organization.html'
  }

  detailSectionsProvider.register {
    title: 'Description'
    position: 0
    types: [
      'catalogueElement'
    ]
    keys: []
    template: 'modelcatalogue/core/ui/detailSections/description.html'
  }

  REGEX_EXAMPLE = """// value is decimal number
x ==~ /\\d+(\\.\\d+)?/
"""
  SET_EXAMPLE = """// value is one of predefined values
x in ['apple', 'banana', 'cherry']
"""

  showExample = (copy, messages, example) ->
      if copy.rule and copy.rule != REGEX_EXAMPLE and copy.rule != SET_EXAMPLE
        messages.confirm("Replace current rule with example", "Do already have some rule, do you want to replace it with the example?").then ->
          copy.rule = example
      else
        copy.rule = example


  detailSectionsProvider.register {
    title: 'Rule'
    position: 10000
    types: [
      'dataType'
    ]
    keys: []
    template: 'modelcatalogue/core/ui/detailSections/rule.html'

    showRegexExample: (copy, messages) -> showExample(copy, messages, REGEX_EXAMPLE)
    showSetExample: (copy, messages) -> showExample(copy, messages, SET_EXAMPLE)
  }

  detailSectionsProvider.register {
    title: 'Revision Notes'
    position: 1000
    types: [
      'dataModel'
    ]
    keys: []
    template: 'modelcatalogue/core/ui/detailSections/revisionNotes.html'
  }

  detailSectionsProvider.register {
    title: 'Measurement Unit'
    position: 500
    types: [
      'primitiveType'
    ]
    keys: []
    template: 'modelcatalogue/core/ui/detailSections/measurementUnit.html'
  }


  detailSectionsProvider.register {
    title: 'Data Type'
    position: -110000
    types: [
      'dataElement'
    ]
    keys: []
    template: 'modelcatalogue/core/ui/detailSections/dataType.html'
  }

  detailSectionsProvider.register {
    title: 'Data Class'
    position: 500
    types: [
      'referenceType'
    ]
    keys: []
    template: 'modelcatalogue/core/ui/detailSections/dataClass.html'
  }

  detailSectionsProvider.register {
    title: 'Preview'
    position: 1000
    types: [
      'asset'
    ]
    keys: []
    template: 'modelcatalogue/core/ui/detailSections/assetPreview.html'
  }


  detailSectionsProvider.register {
    title: 'Custom Metadata'
    position: 100000
    types: [
      'dataModel'
      'asset'
      'mesurementUnit'
      'dataElement'
      # data class has various metadata editors which need to be migrated first
      # 'dataClass'
    ]
    keys: []
    template: 'modelcatalogue/core/ui/detailSections/customMetadata.html'
  }

  detailSectionsProvider.register {
    title: 'Form (Item)'
    position: 101000
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
    template: 'modelcatalogue/core/ui/detailSections/formItem.html'
  }

  detailSectionsProvider.register {
    title: 'Enumerations'
    position: 1000
    types: [
      'enumeratedType'
    ]
    keys: ['http://www.modelcatalogue.org/metadata/enumerateType#subset']
    template: 'modelcatalogue/core/ui/detailSections/enumerations.html'
  }


]
