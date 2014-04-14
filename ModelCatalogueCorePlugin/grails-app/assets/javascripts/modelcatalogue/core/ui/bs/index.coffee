#= require_self
#= require angular-bootstrap/ui-bootstrap-tpls
#= require modelcatalogue/core/index
#= require catalogueElementView
#= require catalogueElementTreeview
#= require catalogueElementTreeviewItem
#= require decoratedListTable
#= require propertiesPane
#= require columns
#= require messagesPanel
#= require modalConfirm
#= require modalPrompt


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
  'mc.core.ui.bs.messagesPanel'
  'mc.core.ui.bs.modalConfirm'
  'mc.core.ui.bs.modalPrompt'
])