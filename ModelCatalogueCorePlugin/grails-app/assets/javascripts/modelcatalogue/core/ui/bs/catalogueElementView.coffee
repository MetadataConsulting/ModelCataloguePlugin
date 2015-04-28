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

    $templateCache.put 'modelcatalogue/core/ui/catalogueElementView.html', '''
    <div>
      <span class="contextual-actions-right">
        <contextual-actions size="sm" no-colors="true" role="item"></contextual-actions>
      </span>
      <h3 class="ce-name"><small ng-class="element.getIcon()" title="{{element.getElementTypeName()}}"></small> <span class="text-danger fa fa-fw fa-warning" ng-if="getDeprecationWarning()" title="{{getDeprecationWarning()}}"></span> {{element.name}} <small><span class="label" ng-show="element.status" ng-class="{'label-warning': element.status == 'DRAFT', 'label-info': element.status == 'PENDING', 'label-primary': element.status == 'FINALIZED', 'label-danger': element.status == 'DEPRECATED'}">{{element.status}}</span></small></h3>
      <blockquote class="ce-description" ng-show="element.description" ng-bind-html="'' + element.description | linky:'_blank'"></blockquote>

      <ul class="nav nav-tabs" role="tablist">
        <li role="presentation" ng-repeat="tab in tabs" ng-class="{active: tab.active}" data-tab-name="{{tab.name}}"><a ng-click="select(tab)"><span  ng-class="{'text-muted': tab.type == 'decorated-list' &amp;&amp; tab.value.total == 0}">{{tab.heading}}</span><span ng-show="tab.value.total"> <span class="badge tab-value-total">{{tab.value.total}}</span></span></a></li>
      </ul>

      <div ng-repeat="tab in tabs" class="tab-pane">
          <div  id="{{tab.name}}-tab" class="cev-tab-content" ng-if="tab.active">
            <ng-include src="'modelcatalogue/core/ui/catalogueElementView/' + tab.type + '.html'"></ng-include>
          </div>
      </div>
    </div>
    '''
  ]