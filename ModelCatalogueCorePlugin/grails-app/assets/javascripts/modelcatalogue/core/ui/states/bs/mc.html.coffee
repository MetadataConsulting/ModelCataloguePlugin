angular.module('mc.core.ui.states.bs.mc.html', []).run(['$templateCache', ($templateCache) ->


  $templateCache.put 'modelcatalogue/core/ui/state/mc.html', '''
      <div class="row">
        <div class="col-xs-3 col-sm-3 col-md-3 col-lg-3 split-view-left" resizable="{'handles': 'e', 'mirror': '.split-view-right', 'maxWidthPct': 60, 'minWidthPct': 20, 'windowWidthCorrection': 91, 'parentWidthCorrection': 31, 'breakWidth': 768}">
          <div class="split-view-content">
            <div class="row">
              <div class="col-md-12">
                <h3 class="trucate"><span class="text-muted"><catalogue-element-icon type="'dataModel'"></catalogue-element-icon></span> {{currentDataModel.name}}</h3>
              </div>
              <div class="col-md-12">
                <catalogue-element-treeview list="elementAsList" descend="'content'" on-select="onTreeviewSelected($element)"></catalogue-element-treeview>
              </div>
            </div>
          </div>
        </div>
        <div class="col-xs-9 col-sm-9 col-md-9 col-lg-9 split-view-right">
          <ui-view></ui-view>
        </div>
      </div>
  '''
])