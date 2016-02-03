
angular.module('mc.core.ui.states.bs.dataModels.html', []).run(['$templateCache', ($templateCache) ->

  #language=HTML
  $templateCache.put 'modelcatalogue/core/ui/state/dataModels.html', '''
    <div class="row leave-10-before">
      <div class="col-md-12">

       <div class="row">
        <div class="col-lg-12 col-sm-12 col-md-12">
          <div class="panel panel-default">
            <div class="panel-heading">
              <div class="input-group input-group-lg">
                <span class="input-group-addon"><span class="fa fa-search"></span> </span>
                <input type="text" placeholder="{{'Search ' + getCurrentType() + ' Models'}}" class="form-control" ng-model="q" ng-model-options="{debounce: 500}">
                <div class="input-group-btn">
                  <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    {{getCurrentStatus()}} <span class="caret"></span>
                  </button>
                  <ul class="dropdown-menu dropdown-menu-right">
                    <li ng-class="{'active' : getCurrentStatus() == 'All'}"><a ng-click="changeStatus()">All</a></li>
                    <li ng-class="{'active' : getCurrentStatus() == 'Draft'}"><a ng-click="changeStatus('draft')">Draft</a></li>
                    <li ng-class="{'active' : getCurrentStatus() == 'Finalized'}"><a ng-click="changeStatus('finalized')">Finalized</a></li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>


      <div class="row" ng-init="columnClasses = 'col-lg-12 col-sm-12 col-md-12' ; detailClass = 'col-md-12'">
        <div ng-class="detailClass">
          <div class="pull-right data-model-create-buttons">
            <contextual-actions role="data-models" no-colors="true" size="sm"></contextual-actions>
          </div>
          <ul class="nav nav-tabs" role="tablist">
              <li id="my-models" ng-class="{'active' : getCurrentType() == 'My'}" role="presentation"><a href ng-click="showMyModels()">My Models</a></li>
              <li ng-class="{'active' : getCurrentType() == 'Catalogue'}" role="presentation"><a href ng-click="showAllModels()">Catalogue Models</a></li>
          </ul>
          <div class="leave-10-before"></div>
          <infinite-list transform="dataModelOrDestination($element)" column-classes="columnClasses" list="list"></infinite-list>
        </div>
        <div ng-if="element" class="col-md-6">
          <div class="pull-right data-model-create-buttons">
            <button type="button" class="btn btn-sm btn-default"><span class="fa fa-fw fa-link"></span> Go to Detail</button>
          </div>
          <ul class="nav nav-tabs" role="tablist">
              <li class="active" role="presentation"><a>Preview</a></li>
          </ul>
          <catalogue-element-view element="element"></catalogue-element-view>
        </div>
      </div>
      </div>
    </div>
  '''
])