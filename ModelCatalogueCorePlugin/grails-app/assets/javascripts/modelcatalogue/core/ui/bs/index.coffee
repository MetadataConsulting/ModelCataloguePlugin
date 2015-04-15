angular.module('mc.core.ui.bs', [
  # depends on
  'mc.core.ui'
  'mc.util.ui'
  'mc.util.ui.bs'
  'ui.bootstrap'
  'ngCookies'
  'infinite-scroll'
  # list of modules
  'mc.core.ui.bs.infiniteTable'
  'mc.core.ui.bs.infiniteList'
  'mc.core.ui.bs.catalogueElementView'
  'mc.core.ui.bs.csvTransformationView'
  'mc.core.ui.bs.importCtrl'
  'mc.core.ui.bs.modalPromptNewExcelImport'
  'mc.core.ui.bs.modalPromptNewLoincImport'
  'mc.core.ui.bs.modalPromptNewOboImport'
  'mc.core.ui.bs.modalPromptNewUmljImport'
  'mc.core.ui.bs.modalPromptNewXsdImport'
  'mc.core.ui.bs.modalPromptNewMCImport'
  'mc.core.ui.bs.modalPromptNewCatalogueXmlImport'
  'mc.core.ui.bs.modalPromptClassificationFilter'
  'mc.core.ui.bs.batchView'
  'mc.core.ui.bs.catalogueElementTreeview'
  'mc.core.ui.bs.catalogueElementTreeviewItem'
  'mc.core.ui.bs.catalogueElementProperties'
  'mc.core.ui.bs.propertiesPane'
  'mc.core.ui.bs.actions'
  'mc.core.ui.bs.catalogueElementActions'
  'mc.core.ui.bs.statesActions'
  'mc.core.ui.bs.navigationActions'
  'mc.core.ui.bs.columns'
  'mc.core.ui.bs.catalogue'
  'mc.core.ui.bs.columnsConfiguration'
  'mc.core.ui.bs.messagesPanel'
  'mc.core.ui.bs.modalConfirm'
  'mc.core.ui.bs.modalPrompt'
  'mc.core.ui.bs.modalPromptCsvHeaders'
  'mc.core.ui.bs.modalPromptCsvTransform'
  'mc.core.ui.bs.modalPromptEditRelationship'
  'mc.core.ui.bs.modalPromptNewRelationship'
  'mc.core.ui.bs.modalPromptNewMapping'
  'mc.core.ui.bs.modalPromptXmlValidate'
  'mc.core.ui.bs.modalPromptForCatalogueElement'
  'mc.core.ui.bs.modalSearchForCatalogueElement'
  'mc.core.ui.bs.modalSearchForActions'
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
  'mc.core.ui.bs.withClassificationCtrlMixin'
]).run ['messages', (messages) ->
  if jQuery
    jQuery(document).on 'keypress', (e) ->
      # ctrl + space
      if e.keyCode is 0 and e.ctrlKey
        messages.prompt null, null, type: 'search-action'
        e.preventDefault()
        return
      # shift + space
      if e.shiftKey and e.which is 32
        messages.prompt(null, null, type: 'search-catalogue-element').then (element) ->
          element.show()
        e.preventDefault()
]

window.modelcatalogue.registerModule 'mc.core.ui.bs'