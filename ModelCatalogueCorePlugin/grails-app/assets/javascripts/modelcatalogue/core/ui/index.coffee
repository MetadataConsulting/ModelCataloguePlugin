#= require_self
#= require angular-ui-router/release/angular-ui-router
#= require ng-file-upload/angular-file-upload
#= require google-diff-match-patch/javascript/diff_match_patch
#= require angular-diff-match-patch-musketyr/angular-diff-match-patch.js
#= require modelcatalogue/core/index
#= require decoratedList
#= require infiniteList
#= require infiniteTable
#= require infiniteListCtrl
#= require columnsSupportCtrl
#= require catalogueElementView
#= require csvTransformationView
#= require batchView
#= require importView
#= require catalogueElementTreeviewItem
#= require catalogueElementTreeview
#= require catalogueElementPicker
#= require catalogueElementIcon
#= require catalogueElementProperties
#= require propertiesPane
#= require messagesPanel
#= require columns
#= require columnsConfiguration
#= require simpleObjectEditor
#= require elementsAsTags
#= require shoppingCart
#= require diffTable

angular.module('mc.core.ui', [
  # depends on
  'ui.router',
  'mc.core'
  # list of modules
  'mc.core.ui.decoratedList'
  'mc.core.ui.infiniteList'
  'mc.core.ui.infiniteTable'
  'mc.core.ui.infiniteListCtrl'
  'mc.core.ui.columnsSupportCtrl'
  'mc.core.ui.catalogueElementView'
  'mc.core.ui.csvTransformationView'
  'mc.core.ui.importView'
  'mc.core.ui.batchView'
  'mc.core.ui.catalogueElementTreeviewItem'
  'mc.core.ui.catalogueElementTreeview'
  'mc.core.ui.catalogueElementIcon'
  'mc.core.ui.catalogueElementPicker'
  'mc.core.ui.catalogueElementProperties'
  'mc.core.ui.propertiesPane'
  'mc.core.ui.messagesPanel'
  'mc.core.ui.columns'
  'mc.core.ui.columnsConfiguration'
  'mc.core.ui.simpleObjectEditor'
  'mc.core.ui.elementsAsTags'
  'mc.core.ui.shoppingCart'
  'mc.core.ui.diffTable'
])