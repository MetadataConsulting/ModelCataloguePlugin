angular.module('mc.core.ui.bs.catalogueElementView', ['mc.core.ui.catalogueElementView', 'mc.core.ui.propertiesPane', 'mc.core.ui.simpleObjectEditor', 'ui.bootstrap', 'ngSanitize']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/catalogueElementView/simple-object-editor.html', '''
        <div>
          <simple-object-editor object="tab.value" title="Key" value-title="Value"></simple-object-editor>
          <div class="row">
            <div class="col-md-12">
              <div class=" text-center">
                <button class="btn btn-primary update-object" ng-disabled="!tab.isDirty()" ng-click="tab.update()"><span class="glyphicon glyphicon-ok"></span> Update</button>
                <button class="btn btn-default reset-object-changes" ng-disabled="!tab.isDirty()" ng-click="tab.reset()"><span class="glyphicon glyphicon-remove"></span> Reset</button>
                <br/>
                <hr/>
              </div>
            </div>
          </div>
        </div>
    '''

    $templateCache.put 'modelcatalogue/core/ui/catalogueElementView/simple-object-editor-for-enumerations.html', '''
        <div>
          <simple-object-editor object="tab.value" title="Key" value-title="Value"></simple-object-editor>
          <div class="row">
            <div class="col-md-12">
              <div class=" text-center">
                <button class="btn btn-primary update-object" ng-disabled="!tab.isDirty()" ng-click="tab.update()"><span class="glyphicon glyphicon-ok"></span> Update</button>
                <button class="btn btn-default reset-object-changes" ng-disabled="!tab.isDirty()" ng-click="tab.reset()"><span class="glyphicon glyphicon-remove"></span> Reset</button>
                <br/>
                <hr/>
              </div>
            </div>
          </div>
        </div>
    '''
    $templateCache.put 'modelcatalogue/core/ui/catalogueElementView/ordered-map-editor.html', '''
        <div>
          <ordered-map-editor object="tab.value" title="Key" value-title="Value"></ordered-map-editor>
          <div class="row">
            <div class="col-md-12">
              <div class=" text-center">
                <button class="btn btn-primary update-object" ng-disabled="!tab.isDirty()" ng-click="tab.update()"><span class="glyphicon glyphicon-ok"></span> Update</button>
                <button class="btn btn-default reset-object-changes" ng-disabled="!tab.isDirty()" ng-click="tab.reset()"><span class="glyphicon glyphicon-remove"></span> Reset</button>
                <br/>
                <hr/>
              </div>
            </div>
          </div>
        </div>
    '''

    $templateCache.put 'modelcatalogue/core/ui/catalogueElementView/metadata-editor.html', '''
        <div>
          <metadata-editor object="tab.value" title="Key" value-title="Value" owner="element"></metadata-editor>
          <div class="row">
            <div class="col-md-12">
              <div class=" text-center">
                <button class="btn btn-primary update-object" ng-disabled="!tab.isDirty()" ng-click="tab.update()"><span class="glyphicon glyphicon-ok"></span> Update</button>
                <button class="btn btn-default reset-object-changes" ng-disabled="!tab.isDirty()" ng-click="tab.reset()"><span class="glyphicon glyphicon-remove"></span> Reset</button>
                <br/>
                <hr/>
              </div>
            </div>
          </div>
        </div>
    '''

    $templateCache.put 'modelcatalogue/core/ui/catalogueElementView/ordered-map-editor-for-enumerations.html', '''
        <div>
          <ordered-map-editor object="tab.value" key-placeholder="Value or copy & paste from excel" value-placeholder="Description"></ordered-map-editor>
          <div class="row">
            <div class="col-md-12">
              <div class=" text-center">
                <button class="btn btn-primary update-object" ng-disabled="!tab.isDirty()" ng-click="tab.update()"><span class="glyphicon glyphicon-ok"></span> Update</button>
                <button class="btn btn-default reset-object-changes" ng-disabled="!tab.isDirty()" ng-click="tab.reset()"><span class="glyphicon glyphicon-remove"></span> Reset</button>
                <br/>
                <hr/>
              </div>
            </div>
          </div>
        </div>
    '''

    $templateCache.put 'modelcatalogue/core/ui/catalogueElementView/properties-pane-for-enumerations.html', '''
        <properties-pane id="{{tab.name}}-enums" item="tab.value" properties="tab.properties" title="Value" value-title="Description"></properties-pane>
    '''

    $templateCache.put 'modelcatalogue/core/ui/catalogueElementView/properties-pane-for-properties.html', '''
        <properties-pane id="{{tab.name}}-props" item="tab.value" properties="tab.properties"></properties-pane>
    '''

    $templateCache.put 'modelcatalogue/core/ui/catalogueElementView/properties-pane.html', '''
      <properties-pane id="{{tab.name}}-objects" item="tab.value" properties="tab.properties" title="Key" value-title="Value"></properties-pane>
    '''

    $templateCache.put 'modelcatalogue/core/ui/catalogueElementView/decorated-list.html', '''
     <infinite-table  id="{{tab.name}}-changes"  list="tab.value" is-sortable="isTableSortable(tab)" reorder="reorder(tab, $row, $current)" columns="tab.columns" actions="tab.actions"></infinite-table>
    '''

    $templateCache.put 'modelcatalogue/core/ui/catalogueElementView/relationship-details.html', '''
     <div ng-init="element = tab.value">
          <span class="contextual-actions-right">
             <contextual-actions size="sm" no-colors="true" role="item"></contextual-actions>
          </span>
          <h3>Metadata</h3>
          <properties-pane id="{{tab.name}}-relationship-metadata" item="element.ext" title="Key" value-title="Value"></properties-pane>
     </div>
    '''
  ]
