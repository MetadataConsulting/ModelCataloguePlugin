angular.module('mc.core.ui.states.bs.mc.html', []).run(['$templateCache', ($templateCache) ->


  $templateCache.put 'modelcatalogue/core/ui/state/mc.html', '''
      <div class="row">
        <div class="split-view-left data-model-treeview-pane" resizable="{'handles': 'e', 'mirror': '.split-view-right', 'maxWidthPct': 60, 'minWidthPct': 20, 'windowWidthCorrection': 91, 'parentWidthCorrection': 31, 'breakWidth': 768}">
          <div class="split-view-content">
              <catalogue-element-treeview list="elementAsList" descend="'content'" on-select="onTreeviewSelected($element)" prefetch="true"></catalogue-element-treeview>
          </div>
        </div>
        <div class="split-view-right data-model-detail-pane">
          <ui-view></ui-view>
        </div>
      </div>
  '''
])
