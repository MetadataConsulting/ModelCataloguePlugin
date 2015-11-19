angular.module('mc.core.ui.states.bs.show.html', []).run(['$templateCache', ($templateCache) ->


  $templateCache.put 'modelcatalogue/core/ui/state/show.html', '''
    <div ng-if="!original.isInstanceOf('dataModel')">
      <div ng-if="element">
        <catalogue-element-view element="element"></catalogue-element-view>
      </div>
    </div>
    <div ng-if="original.isInstanceOf('dataModel')">
      <div>
        <div class="row">
          <div class="col-xs-3 col-sm-3 col-md-3 col-lg-3 split-view-left" resizable="{'handles': 'e', 'mirror': '.split-view-right', 'maxWidthPct': 60, 'minWidthPct': 20, 'windowWidthCorrection': 91, 'parentWidthCorrection': 31, 'breakWidth': 768}">
            <div class="split-view-content">
              <div class="row">
                <span class="contextual-actions-right">
                     <contextual-actions size="sm" icon-only="true" no-colors="true" role="no-such-role"></contextual-actions>
                </span>
                <div class="col-md-12">
                  <h3>
                      Overview
                  </h3>
                  <catalogue-element-treeview list="elementAsList" descend="'content'" on-select="onTreeviewSelected($element)"></catalogue-element-treeview>
                </div>
              </div>
            </div>
          </div>
          <div class="col-xs-9 col-sm-9 col-md-9 col-lg-9 split-view-right" ng-if="element">
            <div class="split-view-content">
              <div ng-if="!element.resource">
                <catalogue-element-view element="element" property="property"></catalogue-element-view>
              </div>
              <div ng-if="element.resource">
                <div class="row">
                  <span class="contextual-actions-right">
                    <contextual-actions size="sm" no-colors="true" role="list"></contextual-actions>
                  </span>
                </div>
                <h3><small ng-class="catalogue.getIcon(element.resource)"></small>&nbsp;{{element.name}}</h3>
                <infinite-table list="list"></infinite-table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  '''
])