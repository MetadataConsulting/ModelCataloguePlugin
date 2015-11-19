angular.module('mc.core.ui.states.bs.layout.html', [])
.constant('mcLayoutTemplateHtml', '''
    <div class="navbar navbar-default navbar-fixed-top" role="navigation">
        <div class="container-fluid">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>

                </button>
                <a class="navbar-brand" href="#" hide-if-logged-in><span class="fa fa-fw fa-book"></span><span class="hidden-sm">&nbsp; Model Catalogue</span></a>
            </div>

            <div class="navbar-collapse collapse">
                <contextual-menu></contextual-menu>
                <ul class="nav navbar-nav">
                    <li class="hidden-sm hidden-md hidden-lg" ng-controller="mc.core.ui.states.controllers.UserCtrl">
                        <a show-if-logged-in ng-click="logout()" type="submit">Log out</a>
                    </li>

                </ul>

                <form class="navbar-form navbar-right hidden-xs" ng-controller="mc.core.ui.states.controllers.UserCtrl">
                    <button show-if-logged-in ng-click="logout()" class="btn btn-danger"  type="submit"><i class="glyphicon glyphicon-log-out"></i></button>
                    <button hide-if-logged-in ng-click="login()"  class="btn btn-primary" type="submit"><i class="glyphicon glyphicon-log-in"></i></button>
                </form>

                <ng-include src="'modelcatalogue/core/ui/omnisearch.html'"></ng-include>

            </div><!--/.nav-collapse -->
        </div>
        <div class="fast-actions" ng-controller="mc.core.ui.states.controllers.FastActionsCtrl" ng-click="showFastActions()" show-if-logged-in>
            <span class="fa-stack fa-3x">
              <i class="fa fa-fw fa-circle fa-stack-2x"></i>
              <i class="fa fa-fw fa-flash fa-inverse fa-stack-1x"></i>
            </span>
        </div>
    </div>

    <div class="container-fluid container-main">
        <sidenav></sidenav>
        <div class="row content-row">
            <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                <div id="jserrors"></div>
                <ui-view></ui-view>
            </div>
        </div>
    </div>
''')

.run([
    '$templateCache', 'mcLayoutTemplateHtml',
    ($templateCache ,  mcLayoutTemplateHtml) ->

      $templateCache.put 'modelcatalogue/core/ui/layout.html', mcLayoutTemplateHtml

])