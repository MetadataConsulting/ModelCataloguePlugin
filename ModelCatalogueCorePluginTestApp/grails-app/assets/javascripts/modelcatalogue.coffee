#= require ng-file-upload/angular-file-upload
#= require angular-i18n/angular-locale_en-gb
#= require angular-http-auth/src/http-auth-interceptor
#= require angular-loading-bar/build/loading-bar
#= require angular-ui-router/release/angular-ui-router
#= require ngInfiniteScroll/build/ng-infinite-scroll
#= require angular-ui-router/release/angular-ui-router
#= require google-diff-match-patch/javascript/diff_match_patch
#= require angular-diff-match-patch.js

#= require modelcatalogue/util/index
#= require modelcatalogue/util/ui/index
#= require modelcatalogue/core/index
#= require modelcatalogue/core/ui/index
#= require modelcatalogue/core/ui/states/index
#= require modelcatalogue/core/ui/bs/index

@grailsAppName = 'model_catalogue'

metadataCurator = angular.module('metadataCurator', [
  'demo.config'
  'mc.core.ui.bs'
  'mc.core.ui.states'
  'ui.bootstrap'
  'angular-loading-bar'
  'ngAnimate'
])

metadataCurator.config ['$stateProvider', '$urlRouterProvider', ($stateProvider, $urlRouterProvider)->
  # $urlRouterProvider.otherwise("/dashboard")
]