angular.module('mc.core.ui.bs', [
  # depends on
  'mc.core.ui'
  'mc.util.ui'
  'mc.util.ui.bs'
  'mc.core.ui.modals'
  'ui.bootstrap'
  'ngCookies'
  'infinite-scroll'
  # list of modules
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
  'mc.core.ui.bs.batchView'
  'mc.core.ui.bs.catalogueElementProperties'
  'mc.core.ui.bs.actions'
  'mc.core.ui.bs.catalogueElementActions'
  'mc.core.ui.bs.navigationActions'
  'mc.core.ui.bs.columns'
  'mc.core.ui.bs.catalogue'
  'mc.core.ui.bs.columnsConfiguration'
  'mc.core.ui.bs.messagesPanel'
  'mc.core.ui.bs.modalConfirm'
  'mc.core.ui.bs.modalPrompt'
  'mc.core.ui.bs.modalFinalize'
  'mc.core.ui.bs.modalExport'
  'mc.core.ui.bs.modalNewVersion'
  'mc.core.ui.bs.modalPromptCsvHeaders'
  'mc.core.ui.bs.modalPromptCsvTransform'
  'mc.core.ui.bs.modalPromptEditRelationship'
  'mc.core.ui.bs.modalPromptWithOptions'
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
  'mc.core.ui.bs.modalPromptEnumeratedTypeEdit'
  'mc.core.ui.bs.modalPromptActionParametersEdit'
  'mc.core.ui.bs.modalPromptModel'
  'mc.core.ui.bs.modalPromptRelationshipTypeEdit'
  'mc.core.ui.bs.modalPromptGenerateSuggestions'
  'mc.core.ui.bs.modalPromptDataElementEdit'
  'mc.core.ui.bs.modalAbout'
  'mc.core.ui.bs.saveOrUpdatePublishedElementCtrl'
  'mc.core.ui.bs.watchAndAskForImportOrCloneCtrl'
  'mc.core.ui.bs.saveAndCreateAnotherCtrlMixin'
  'mc.core.ui.bs.modalPromptConvert'
  'mc.core.ui.bs.modalPromptCurrentActivity'
  'mc.core.ui.bs.modalPromptValidateValue'
  'mc.core.ui.bs.modelWizard'
  'mc.core.ui.bs.dataModelWIzard'
  'mc.core.ui.bs.elementsAsTags'
  'mc.core.ui.bs.diffTable'
  'mc.core.ui.bs.withClassificationCtrlMixin'
  'mc.core.ui.bs.orderedMapEditor'
  'mc.core.ui.bs.metadataEditors'
  'mc.core.ui.bs.metadataEditor'
  'mc.core.ui.bs.detailSections'
  'mc.core.ui.bs.navigationRightActions'
  'mc.core.ui.bs.modalOptions'
]).run ['messages', '$rootScope', '$log', (messages, $rootScope, $log) ->
  $rootScope.$on '$stateChangeError', (event, toState, toParams, fromState, fromParams, error) ->
    $log.error "Error changing state:", event, toState, toParams, fromState, fromParams, error

  if jQuery
    jQuery(document).on 'keypress', (e) ->
      # ctrl + space
      if e.which in [0, 10] and e.ctrlKey
        messages.prompt null, null, type: 'search-action'
        e.preventDefault()
        return
      # shift + space
      if e.shiftKey and e.which is 32
        # mock click to the menu item
        $('#role_navigation-right_search-menu-menu-item-link').click()
#        messages.prompt(null, null, type: 'search-catalogue-element', empty: true).then (element) ->
#          element.show()
        e.preventDefault()
]

window.modelcatalogue.registerModule 'mc.core.ui.bs'
