#= require ng-file-upload/angular-file-upload
#= require angular-i18n/angular-locale_en-gb
#= require angular-http-auth/src/http-auth-interceptor
#= require angular-loading-bar/build/loading-bar
#= require angular-ui-router/release/angular-ui-router
#= require ngInfiniteScroll/build/ng-infinite-scroll
#= require angular-ui-router/release/angular-ui-router
#= require google-diff-match-patch/javascript/diff_match_patch
#= require angular-diff-match-patch.js

#= require_self
#= require_full_tree .

modules = [
  'ui.bootstrap'
  'angular-loading-bar'
  'ngAnimate'
]

modelcatalogue =
  # registers module which should be loaded with the application
  registerModule: (extension) -> modules.push extension
  # copy of the registered modules
  getModules: -> angular.copy modules


window.modelcatalogue = modelcatalogue