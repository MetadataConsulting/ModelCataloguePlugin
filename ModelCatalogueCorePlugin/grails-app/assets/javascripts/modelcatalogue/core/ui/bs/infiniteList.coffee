angular.module('mc.core.ui.bs.infiniteList', ['mc.core.ui.infiniteList', 'ngSanitize', 'infinite-scroll']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/infinitePanels.html', '''
    <div>
      <div class="row">
        <div class="col-lg-12 col-sm-12 col-md-12">
          <div class="panel panel-default">
            <div class="panel-heading">
              <div class="input-group input-group-lg">
                <span class="input-group-addon"><span class="fa fa-search"></span> </span>
                <input type="text" placeholder="Search by Name" class="form-control" ng-model="nameFilter" ng-model-options="{debounce: 500}">
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="row" infinite-scroll="loadMore()" infinite-scroll-disabled="loading || !isVisible()" infinite-scroll-distance="2" infinite-scroll-immediate-check="isVisible()" infinite-scroll-listen-for-event="infiniteList:filtered">
        <div ng-repeat="element in elements" class="col-lg-3 col-sm-4 col-md-3 infinite-scroll-item" ng-if="isNotFiltered(element)">
          <div class="panel panel-fixed-height-300" ng-class="{'panel-warning': element.status == 'DRAFT', 'panel-info': element.status == 'PENDING', 'panel-default': element.status == 'FINALIZED' || !element.status, 'panel-danger': element.status == 'DEPRECATED'}">
            <div class="panel-heading">
              <h3 class="panel-title" title="{{element.name}}" ng-click="select(element)"><span ng-class="element.getIcon()"></span> {{element.name}}</h3>
            </div>
            <div class="panel-body preserve-new-lines">
              {{element.description}}
            </div>
          </div>
          <div class="panel-actions" ng-if="!noActions">
            <contextual-actions size="sm" no-colors="true" icon-only="true" group="true" role="item"></contextual-actions>
          </div>
        </div>
        <div ng-if="elements.length < total" class="col-lg-3 col-sm-4 col-md-3 infinite-scroll-item">
          <div class="panel panel-fixed-height-300 panel-default">
            <div class="panel-heading">
              <h3 class="panel-title"> Loading</h3>
            </div>
            <div class="panel-body">
                Loaded {{elements.length}} / {{total}} items
            </div>
          </div>
        </div>
      </div>
    </div>
    '''
  ]