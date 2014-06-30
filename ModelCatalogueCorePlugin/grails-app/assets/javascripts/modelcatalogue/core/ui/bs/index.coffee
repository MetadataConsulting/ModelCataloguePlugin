#= require_self
#= require angular-bootstrap/ui-bootstrap-tpls
#= require angular-cookies/angular-cookies
#= require modelcatalogue/core/index
#= require modelcatalogue/util/ui/index
#= require catalogueElementView
#= require importView
#= require catalogueElementTreeview
#= require catalogueElementTreeviewItem
#= require catalogueElementProperties
#= require decoratedListTable
#= require propertiesPane
#= require actions
#= require columns
#= require columnsConfiguration
#= require messagesPanel
#= require modalConfirm
#= require modalPrompt
#= require modalPromptNewRelationship
#= require modalPromptNewImport
#= require simpleObjectEditor
#= require modalPromptAssetEdit
#= require modalPromptBasicEdit
#= require modalPromptLogin
#= require modalPromptMeasurementUnitEdit
#= require modalPromptValueDomainEdit
#= require modalPromptEnumeratedTypeEdit
#= require modalPromptPublishedElement


angular.module('mc.core.ui.bs', [
  # depends on
  'mc.core.ui'
  'mc.util.ui'
  'mc.util.ui.bs'
  'ui.bootstrap'
  'ngCookies'
  # list of modules
  'mc.core.ui.bs.decoratedListTable'
  'mc.core.ui.bs.catalogueElementView'
  'mc.core.ui.bs.importView'
  'mc.core.ui.bs.catalogueElementTreeview'
  'mc.core.ui.bs.catalogueElementTreeviewItem'
  'mc.core.ui.bs.catalogueElementProperties'
  'mc.core.ui.bs.propertiesPane'
  'mc.core.ui.bs.actions'
  'mc.core.ui.bs.columns'
  'mc.core.ui.bs.columnsConfiguration'
  'mc.core.ui.bs.messagesPanel'
  'mc.core.ui.bs.modalConfirm'
  'mc.core.ui.bs.modalPrompt'
  'mc.core.ui.bs.modalPromptNewRelationship'
  'mc.core.ui.bs.modalPromptNewImport'
  'mc.core.ui.bs.simpleObjectEditor'
  'mc.core.ui.bs.modalPromptAssetEdit'
  'mc.core.ui.bs.modalPromptLogin'
  'mc.core.ui.bs.modalPromptBasicEdit'
  'mc.core.ui.bs.modalPromptMeasurementUnitEdit'
  'mc.core.ui.bs.modalPromptValueDomainEdit'
  'mc.core.ui.bs.modalPromptEnumeratedTypeEdit'
  'mc.core.ui.bs.modalPromptPublishedElement'
])