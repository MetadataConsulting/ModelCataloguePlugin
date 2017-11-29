angular.module('mc.core.ui.bs', [
  # depends on
  'mc.util.ui'
  'modelcatalogue.core.components.metadataEditor'
  'ui.bootstrap'
  'ngCookies'
  'infinite-scroll'
  # list of modules
  'mc.core.ui.bs.modalAlert'
  'mc.core.ui.bs.modalPromptCloneIntoDataModel'
  'mc.core.ui.bs.catalogueElementProperties'
  'modelcatalogue.core.ui.bs.actionsConf'
  'mc.core.ui.bs.columns'
  'mc.core.ui.bs.catalogue'
  'mc.core.ui.bs.columnsConfiguration'
  'mc.core.ui.bs.messagesPanel'
  'mc.core.ui.bs.modalConfirm'
  'mc.core.ui.bs.modalPrompt'
  'mc.core.ui.bs.modalFinalize'
  'mc.core.ui.bs.modalFeedback'
  'mc.core.ui.bs.modalNewVersion'
  'mc.core.ui.bs.modalPromptCsvHeaders'
  'mc.core.ui.bs.modalPromptCsvTransform'
  'mc.core.ui.bs.modalPromptEditRelationship'
  'mc.core.ui.bs.modalPromptWithOptions'
  'mc.core.ui.bs.modalPromptWithMultipleOptions'
  'mc.core.ui.bs.modalPromptNewRelationship'
  'mc.core.ui.bs.modalPromptNewMapping'
  'mc.core.ui.bs.modalPromptXmlValidate'
  'mc.core.ui.bs.modalPromptForCatalogueElement'
  'mc.core.ui.bs.modalSearchForCatalogueElement'
  'mc.core.ui.bs.modalPromptForCatalogueElements'
  'mc.core.ui.bs.modalPromptLogin'
  'mc.core.ui.bs.modalPromptBasicEdit'
  'mc.core.ui.bs.modalPromptActionParametersEdit'
  'mc.core.ui.bs.modalPromptRelationshipTypeEdit'
  'mc.core.ui.bs.modalPromptDatModelPolicyEdit'
  'mc.core.ui.bs.modalAbout'
  'mc.core.ui.bs.saveOrUpdatePublishedElementCtrl'
  'mc.core.ui.bs.watchAndAskForImportOrCloneCtrl'
  'mc.core.ui.bs.saveAndCreateAnotherCtrlMixin'
  'mc.core.ui.bs.modalPromptConvert'
  'mc.core.ui.bs.modalPromptCurrentActivity'
  'mc.core.ui.bs.modalPromptValidateValue'
  'mc.core.ui.bs.withClassificationCtrlMixin'
  'modelcatalogue.core.sections.metadataEditors'
  'mc.core.ui.bs.modalOptions'
  # This modal is actually used right here in this module:
  'modelcatalogue.core.ui.navigationRight.global.modalSearchForActions'
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
      if e.shiftKey and (e.ctrlKey or e.metaKey) and e.which in [1, 65]
        # mock click to the menu item
        $('#ROLE_NAVIGATION_ACTION-right_search-menu-menu-item-link').click()
        e.preventDefault()
]
