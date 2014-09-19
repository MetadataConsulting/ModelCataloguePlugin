angular.module('mc.core.ui.bs.infiniteList', ['mc.core.ui.infiniteList', 'ngSanitize', 'infinite-scroll']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/infinitePanels.html', '''
    <div>
      <div class="row" infinite-scroll="loadMore()" infinite-scroll-disabled="loading" infinite-scroll-distance="2">
        <div ng-repeat="element in elements" class="col-lg-4 col-sm-6 col-md-4 expandable infinite-scroll-item">
          <div class="panel panel-fixed-height-300" ng-class="{'panel-warning': element.status == 'DRAFT', 'panel-info': element.status == 'PENDING', 'panel-default': element.status == 'FINALIZED' || !element.status, 'panel-danger': element.status == 'ARCHIVED'}">
            <div class="panel-heading">
              <a><span class="fa close" ng-click="extendOrCollapse($event)"></span></a>
              <h3 class="panel-title" title="{{element.name}}" ng-click="element.show()"><span ng-class="element.getIcon()"></span> {{element.name}}</h3>
            </div>
            <div class="panel-body preserve-new-lines">
              {{element.description}}
            </div>
          </div>
          <div class="panel-actions">
            <contextual-actions size="sm" no-colors="true" icon-only="true" group="true"></contextual-actions>
          </div>
        </div>
      </div>
    </div>
    '''
  ]