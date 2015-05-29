angular.module('mc.core.ui.bs.metadataEditor', ['mc.core.ui.metadataEditor']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/metadataEditor.html', '''
      <div class="ordered-map-container panel-body">
        <div class="col-sm-3 col-md-3 col-lg-3 pill">
          <ul class="nav nav-pills nav-stacked" role="tablist">
            <li role="presentation" ng-class="{'active': selectedEditor == '__ALL__'}"><a ng-click="selectEditor('__ALL__')">Raw</a></li>
            <li role="presentation" ng-class="{'active': selectedEditor == editor.getTitle()}" ng-repeat="editor in availableEditors"><a ng-click="selectEditor(editor.getTitle())">{{editor.getTitle()}}</a></li>
          </ul>
        </div>

        <div class="col-sm-9 col-md-9 col-lg-9 editor">
          <div ng-repeat="editor in availableEditors">
            <div ng-include="editor.getTemplate()" ng-if="selectedEditor == editor.getTitle()"></div>
          </div>
          <div ng-if="selectedEditor == '__ALL__'">
            <table class="soe-table table">
              <thead ng-if="title || valueTitle">
                <th></th>
                <th class="col-md-6 col-sm-5 soe-table-property-key">{{title}}</th>
                <th class="col-md-5 col-sm-5 soe-table-property-value ">{{valueTitle}}</th>
                <th class="col-md-1 col-sm-2 soe-table-property-actions">
                  <!-- to allow submitting forms with only this editor within -->
                  <input type="submit" class="hide">
                </th>
              </thead>
              <tbody sortable="sortableOptions">
                <tr class="soe-table-property-row" ng-repeat="property in object.values" ng-class="{'has-error': !isKeyUnique(property.key)}">
                  <td><span class="handle fa fa-ellipsis-v fa-fw text-muted with-move"></span></td>
                  <th class="soe-table-property-key col-md-5 col-sm-5">
                    <input type="text" ng-model="property.key" class="form-control" placeholder="{{valuePlaceholder ? keyPlaceholder : 'Key'}}" ng-disabled="handledKeys.indexOf(property.key) > -1">
                  </th>
                  <td class="soe-table-property-value col-md-5 col-sm-5"><input type="text" ng-model="property.value" class="form-control" data-for-property="{{property.key}}" placeholder="{{valuePlaceholder ? valuePlaceholder : 'Value (leave blank for null)'}}" ng-keydown="addNewRowOnTab($event, $index, $last)" ng-disabled="handledKeys.indexOf(property.key) > -1"></td>
                  <td class="soe-table-property-actions col-md-2 col-sm-2">
                      <p><a class="btn btn-link btn-sm soe-add-row" ng-click="addProperty($index)"><span
                              class="glyphicon glyphicon-plus"></span></a><a class="btn btn-link btn-sm soe-remove-row"
                              ng-click="removeProperty($index)"><span class="glyphicon glyphicon-minus"></span>
                      </a></p>
                  </td>
                </tr>
              </tbody>
            </table>
            <a ng-if="object.values.length == 0" class="btn btn-success btn-block add-metadata" ng-click="addProperty(0)"><span class="glyphicon glyphicon-plus"></span> Add Metadata</a>
          </div>
        </div>
      </div>
    '''
  ]