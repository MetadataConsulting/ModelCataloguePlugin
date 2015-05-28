angular.module('mc.core.ui.bs.orderedMapEditor', ['mc.core.ui.orderedMapEditor']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/orderedMapEditor.html', '''
      <div>
        <div ng-repeat="editor in availableEditors">
          <hr/>
          <h4 class="text-center">{{editor.getTitle()}}</h4>
          <hr/>
          <div ng-include="editor.getTemplate()"></div>
          <hr/>
        </div>
        <h4 class="text-center" ng-if="availableEditors.length">General</h4>
        <hr ng-if="availableEditors.length"/>
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
                <input type="text" ng-model="property.key" class="form-control" placeholder="{{valuePlaceholder ? keyPlaceholder : 'Key'}}" autofocus="autofocus" focus-me="lastAddedRow == $index &amp;&amp; $index != 0">
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
      </div>
    '''
  ]