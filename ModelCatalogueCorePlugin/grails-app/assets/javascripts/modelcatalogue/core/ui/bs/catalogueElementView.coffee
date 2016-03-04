angular.module('mc.core.ui.bs.catalogueElementView', ['mc.core.ui.catalogueElementView', 'mc.core.ui.propertiesPane', 'mc.core.ui.simpleObjectEditor', 'mc.util.ui.bs.contextualActions' , 'ui.bootstrap', 'ngSanitize']).run [ '$templateCache', ($templateCache) ->
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

    $templateCache.put 'modelcatalogue/core/ui/catalogueElementView.html', '''
    <div class="row catalogue-element-view">
      <div ng-if="!displayOnly">
        <div class="col-md-12">
          <form editable-form name="editableForm" onaftersave="inlineUpdateElement()">
            <div class="catalogue-element-detail-actions">
              <contextual-actions icon-only="true" role="item-detail" size="xs" no-colors="true"></contextual-actions>
            </div>
            <h3 class="ce-name"><small ng-class="element.getIcon()" title="{{element.getElementTypeName()}}"></small> <span class="text-danger fa fa-fw fa-warning" ng-if="getDeprecationWarning()" title="{{getDeprecationWarning()}}"></span> <span editable-text="copy.name" e-name="name">{{element.name}}</span> <small><a ng-href="{{element.dataModel.href()}}" class="label" ng-class="{'label-warning': element.getDataModelStatus() == 'DRAFT', 'label-info': element.getDataModelStatus() == 'PENDING', 'label-primary': element.getDataModelStatus() == 'FINALIZED', 'label-danger': element.getDataModelStatus() == 'DEPRECATED'}">{{element.getDataModelWithVersion()}}</a></small></h3>
            <messages-panel messages="messages"></messages-panel>
            <div class="row detail-section" ng-repeat="view in detailSections">
              <p ng-if="view.getTitle()"
                 class="text-center detail-section-title small"
                 data-view-name="{{view.getTitle()}}"
                 ng-init="view.templateHidden = view.hideIfNoData() && !view.hasData(element)">
                <span class="title btn btn-link btn-sm"
                      ng-click="view.templateHidden = !view.templateHidden"
                      ng-show="view.hideIfNoData() && !view.hasData(element)">
                  {{view.getTitle()}}
                </span>
                <span class="title" ng-hide="view.hideIfNoData() && !view.hasData(element)">
                  {{view.getTitle()}}
                </span>
              </p>
              <div ng-hide="view.templateHidden" data-view-content-name="{{view.getTitle()}}">
                <ng-include src="view.getTemplate()"></ng-include>
              </div>
            </div>
          </form>
          <ul class="nav nav-tabs" role="tablist">
            <li role="presentation" ng-repeat="tab in tabs" ng-if="!tab.hidden" ng-class="{active: tab.active}" data-tab-name="{{tab.name}}"><a ng-click="select(tab)"><span  ng-class="{'text-muted': tab.type == 'decorated-list' &amp;&amp; tab.value.total == 0}">{{tab.heading}}</span><span ng-show="tab.value.total"> <span class="badge tab-value-total" ng-if="tab.value.total != 2147483647">{{tab.value.total}}</span><span class="badge tab-value-total" ng-if="tab.value.total == 2147483647"><span class="fa fa-question fa-inverse"</span></span></a></li>
          </ul>

          <div ng-repeat="tab in tabs" class="tab-pane">
            <div  id="{{tab.name}}-tab" class="cev-tab-content" ng-if="tab.active">
              <ng-include src="'modelcatalogue/core/ui/catalogueElementView/' + tab.type + '.html'"></ng-include>
            </div>
          </div>
        </div>
      </div>
      <div ng-if="displayOnly">
        <div class="col-md-12">
          <div ng-repeat="tab in tabs" class="tab-pane">
            <div id="{{tab.name}}-tab" class="cev-tab-content" ng-if="tab.active">
              <h3 class="ce-name"><small ng-class="element.getIcon()" title="{{element.getElementTypeName()}}"></small> <span class="text-danger fa fa-fw fa-warning" ng-if="getDeprecationWarning()" title="{{getDeprecationWarning()}}"></span> {{element.name}} {{tab.heading}} <small><span class="label" ng-class="{'label-warning': element.getDataModelStatus() == 'DRAFT', 'label-info': element.getDataModelStatus() == 'PENDING', 'label-primary': element.getDataModelStatus() == 'FINALIZED', 'label-danger': element.getDataModelStatus() == 'DEPRECATED'}">{{element.getDataModelWithVersion()}}</span></small></h3>
              <ng-include src="'modelcatalogue/core/ui/catalogueElementView/' + tab.type + '.html'"></ng-include>
            </div>
           </div>
        </div>
      </div>
    </div>
    '''
  ]
