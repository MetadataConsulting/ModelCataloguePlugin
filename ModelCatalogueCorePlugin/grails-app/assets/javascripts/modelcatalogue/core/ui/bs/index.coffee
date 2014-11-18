#= require_self
#= require modelcatalogue/core/index
#= require modelcatalogue/util/ui/index
#= require modelcatalogue/util/ui/bs/index
#= require catalogueElementView
#= require csvTransformationView
#= require importView
#= require importCtrl
#= require modalPromptNewExcelImport
#= require modalPromptNewOboImport
#= require modalPromptNewXsdImport
#= require modalPromptNewMCImport
#= require batchView
#= require catalogueElementTreeview
#= require catalogueElementTreeviewItem
#= require catalogueElementProperties
#= require decoratedListTable
#= require infiniteTable
#= require infiniteList
#= require propertiesPane
#= require actions
#= require importActions
#= require navigationActions
#= require columns
#= require catalogue
#= require columnsConfiguration
#= require messagesPanel
#= require modalConfirm
#= require modalPrompt
#= require modalPromptNewRelationship
#= require modalPromptNewMapping
#= require modalPromptXmlValidate
#= require simpleObjectEditor
#= require modalPromptAssetEdit
#= require modalPromptBasicEdit
#= require modalPromptCsvHeaders
#= require modalPromptCsvTransform
#= require modalPromptLogin
#= require modalPromptForCatalogueElement
#= require modalPromptForCatalogueElements
#= require modalPromptMeasurementUnitEdit
#= require modalPromptActionParametersEdit
#= require modalPromptValueDomainEdit
#= require modalPromptEnumeratedTypeEdit
#= require modalPromptModel
#= require modalPromptRelationshipTypeEdit
#= require modalPromptDataElementEdit
#= require saveOrUpdatePublishedElementCtrl
#= require saveAndCreateAnotherCtrlMixin
#= require modalPromptConvert
#= require modalPromptValidateValue
#= require modelWizard
#= require classificationWizard
#= require elementsAsTags
#= require diffTable


angular.module('mc.core.ui.bs', [
  # depends on
  'mc.core.ui'
  'mc.util.ui'
  'mc.util.ui.bs'
  'ui.bootstrap'
  'ngCookies'
  'infinite-scroll'
  # list of modules
  'mc.core.ui.bs.decoratedListTable'
  'mc.core.ui.bs.infiniteTable'
  'mc.core.ui.bs.infiniteList'
  'mc.core.ui.bs.catalogueElementView'
  'mc.core.ui.bs.csvTransformationView'
  'mc.core.ui.bs.importView'
  'mc.core.ui.bs.importCtrl'
  'mc.core.ui.bs.modalPromptNewExcelImport'
  'mc.core.ui.bs.modalPromptNewOboImport'
  'mc.core.ui.bs.modalPromptNewXsdImport'
  'mc.core.ui.bs.modalPromptNewMCImport'
  'mc.core.ui.bs.batchView'
  'mc.core.ui.bs.catalogueElementTreeview'
  'mc.core.ui.bs.catalogueElementTreeviewItem'
  'mc.core.ui.bs.catalogueElementProperties'
  'mc.core.ui.bs.propertiesPane'
  'mc.core.ui.bs.actions'
  'mc.core.ui.bs.importActions'
  'mc.core.ui.bs.navigationActions'
  'mc.core.ui.bs.columns'
  'mc.core.ui.bs.catalogue'
  'mc.core.ui.bs.columnsConfiguration'
  'mc.core.ui.bs.messagesPanel'
  'mc.core.ui.bs.modalConfirm'
  'mc.core.ui.bs.modalPrompt'
  'mc.core.ui.bs.modalPromptCsvHeaders'
  'mc.core.ui.bs.modalPromptCsvTransform'
  'mc.core.ui.bs.modalPromptNewRelationship'
  'mc.core.ui.bs.modalPromptNewMapping'
  'mc.core.ui.bs.modalPromptXmlValidate'
  'mc.core.ui.bs.modalPromptForCatalogueElement'
  'mc.core.ui.bs.modalPromptForCatalogueElements'
  'mc.core.ui.bs.modalPromptForCatalogueElements'
  'mc.core.ui.bs.simpleObjectEditor'
  'mc.core.ui.bs.modalPromptAssetEdit'
  'mc.core.ui.bs.modalPromptLogin'
  'mc.core.ui.bs.modalPromptBasicEdit'
  'mc.core.ui.bs.modalPromptMeasurementUnitEdit'
  'mc.core.ui.bs.modalPromptValueDomainEdit'
  'mc.core.ui.bs.modalPromptEnumeratedTypeEdit'
  'mc.core.ui.bs.modalPromptActionParametersEdit'
  'mc.core.ui.bs.modalPromptModel'
  'mc.core.ui.bs.modalPromptRelationshipTypeEdit'
  'mc.core.ui.bs.modalPromptDataElementEdit'
  'mc.core.ui.bs.saveOrUpdatePublishedElementCtrl'
  'mc.core.ui.bs.saveAndCreateAnotherCtrlMixin'
  'mc.core.ui.bs.modalPromptConvert'
  'mc.core.ui.bs.modalPromptValidateValue'
  'mc.core.ui.bs.modelWizard'
  'mc.core.ui.bs.classificationWizard'
  'mc.core.ui.bs.elementsAsTags'
  'mc.core.ui.bs.diffTable'
])