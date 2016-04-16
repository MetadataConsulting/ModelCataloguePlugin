#= require vk-beautify/dist/vkbeautify.0.99.00.beta
#= require blob-polyfill/Blob
#= require file-saver.js/FileSaver
#= require ng-file-upload/ng-file-upload
#= require angular-i18n/angular-locale_en-gb
#= require angular-http-auth/src/http-auth-interceptor
#= require angular-loading-bar/build/loading-bar
#= require angular-ui-router/release/angular-ui-router
#= require ngInfiniteScroll/build/ng-infinite-scroll
#= require angular-ui-router/release/angular-ui-router
#= require google-diff-match-patch/javascript/diff_match_patch
#= require angular-diff-match-patch.js
#= require angular-xeditable/dist/js/xeditable
#= require angular-ui-ace/ui-ace
#= require angular-file-saver/dist/angular-file-saver
#= require_self
#= require_full_tree .
#= require mc/index

if !String.prototype.startsWith
  String.prototype.startsWith = (searchString, position) ->
    position = position || 0;
    return this.indexOf(searchString, position) == position

modules = [
  'ui.bootstrap'
  'ui.router'
  'angular-loading-bar'
  'ngAnimate'
  'xeditable'
  'ui.ace'
  'ngFileSaver'
]

modelcatalogue =
  # registers module which should be loaded with the application
  registerModule: (extension) -> modules.push extension
  # copy of the registered modules
  getModules: -> angular.copy modules


window.modelcatalogue = modelcatalogue
