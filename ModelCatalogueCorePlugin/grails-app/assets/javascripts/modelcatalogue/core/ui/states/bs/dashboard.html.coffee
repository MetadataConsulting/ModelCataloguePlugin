angular.module('mc.core.ui.states.bs.dashboard.html', []).run([
  '$templateCache',
  ($templateCache) ->

    #language=HTML
    $templateCache.put 'modelcatalogue/core/ui/state/dashboard.html', dashboard = '''
    <!-- Jumbotron -->
    <div hide-if-logged-in>
      <div class="jumbotron">
        <!-- from config mc.welcome.jumbo -->
        <div ng-bind-html="welcome.jumbo"></div>
        <form ng-controller="mc.core.ui.states.controllers.UserCtrl">
           <button ng-click="login()" class="btn btn-large btn-primary" type="submit">Login <i class="glyphicon glyphicon-log-in"></i></button>
           <a ng-href="{{registrationUrl}}" ng-if="registrationUrl" class="btn btn-large btn-primary">Sign Up <span class="fa fa-user"></span></a>
           <!--a href="" class="btn btn-large btn-primary" >Sign Up <i class="glyphicon glyphicon-pencil"></i></a-->
        </form>
      </div>

      <!-- from config mc.welcome.info -->
      <div id="info" class="row" ng-bind-html="welcome.info"></div>
    </div>

    <div class="row">
        <div class="col-lg-12 col-sm-12 col-md-12">
            <div class="panel panel-default">
                <div class="panel-body">
                    <div class="row">
                      <div class=" col-xs-12 col-sm-12 col-md-4 col-lg-4"><p>Model catalogue development supported by</p></div>
                      <div class=" col-xs-3 col-sm-3 col-md-2 col-lg-2">
                        <p>
                          <a href="http://www.genomicsengland.co.uk/">
                            <img ng-src="{{image('/modelcatalogue/GEL.jpg')}}" class="img-thumbnail sponsor-logo-small" alt="Genomics England">
                          </a>
                        </p>
                        <p class="hidden-xs"><a href="http://www.genomicsengland.co.uk/" class="text-muted">Genomics England</a></p>
                      </div>
                      <div class=" col-xs-3 col-sm-3 col-md-2 col-lg-2">
                        <p><a href="http://www.mrc.ac.uk"><img ng-src="{{image('/modelcatalogue/MRC.png')}}" class="img-thumbnail sponsor-logo-small" alt="Medical Research Council"></a></p>
                        <p class="hidden-xs"><a href="http://www.mrc.ac.uk" class="text-muted">Medical Research Council</a></p>
                      </div>
                      <div class=" col-xs-3 col-sm-3 col-md-2 col-lg-2">
                        <p><a href="http://www.nihr.ac.uk/"><img ng-src="{{image('/modelcatalogue/NIHR.png')}}" class="img-thumbnail sponsor-logo-small" alt="NIHR"></a></p>
                        <p class="hidden-xs"><a href="http://www.nihr.ac.uk/" class="text-muted">National Institute for Health Research</a></p>
                      </div>
                      <div class=" col-xs-3 col-sm-3 col-md-2 col-lg-2">
                        <p><a href="http://www.metadataconsulting.co.uk"><img ng-src="{{image('/modelcatalogue/MDC.png')}}" class="img-thumbnail sponsor-logo-small" alt="Metadata Consulting Ltd"></a></p>
                        <p class="hidden-xs"><a href="http://www.metadataconsulting.co.uk" class="text-muted">Metadata Consulting</a></p>
                      </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    '''

    $templateCache.put 'modelcatalogue/core/ui/state/dashboardWithNav.html', dashboard

])