#= require_self
#= require angular-bootstrap/ui-bootstrap-tpls
#= require modelcatalogue/core/index
#= require catalogueElementView
#= require catalogueElementTreeview
#= require catalogueElementTreeviewItem
#= require decoratedListTable
#= require propertiesPane
#= require columns
#= require columnsConfiguration
#= require messagesPanel
#= require modalConfirm
#= require modalPrompt
#= require modalPromptNewRelationship
#= require simpleObjectEditor
#= require modalPromptAssetEdit
#= require modalPromptBasicEdit
#= require modalPromptMeasurementUnitEdit
#= require modalPromptValueDomainEdit
#= require modalPromptEnumeratedTypeEdit


angular.module('mc.core.ui.bs', [
  # depends on
  'mc.core.ui'
  'ui.bootstrap'
  # list of modules
  'mc.core.ui.bs.decoratedListTable'
  'mc.core.ui.bs.catalogueElementView'
  'mc.core.ui.bs.catalogueElementTreeview'
  'mc.core.ui.bs.catalogueElementTreeviewItem'
  'mc.core.ui.bs.propertiesPane'
  'mc.core.ui.bs.columns'
  'mc.core.ui.bs.columnsConfiguration'
  'mc.core.ui.bs.messagesPanel'
  'mc.core.ui.bs.modalConfirm'
  'mc.core.ui.bs.modalPrompt'
  'mc.core.ui.bs.modalPromptNewRelationship'
  'mc.core.ui.bs.simpleObjectEditor'
  'mc.core.ui.bs.modalPromptAssetEdit'
  'mc.core.ui.bs.modalPromptBasicEdit'
  'mc.core.ui.bs.modalPromptMeasurementUnitEdit'
  'mc.core.ui.bs.modalPromptValueDomainEdit'
  'mc.core.ui.bs.modalPromptEnumeratedTypeEdit'
])