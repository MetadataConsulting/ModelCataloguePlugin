actionRoleRegister =
  ROLE_NAVIGATION_ACTION:               'navigation'
  ROLE_NAVIGATION_RIGHT_ACTION:         'navigation-right'

  ROLE_LIST_ACTION:                     'list'

  ROLE_ITEM_ACTION:                     'item'
  ROLE_ITEM_DETAIL_ACTION:              'item-detail'
  ROLE_ITEM_INFINITE_LIST:              'item-infinite-list'

  ROLE_MODAL_ACTION:                    'modal'
  ROLE_LIST_HEADER_ACTION:              'header'
  ROLE_LIST_FOOTER_ACTION:              'footer'
  ROLE_GLOBAL_ACTION:                   'global-action'

  ROLE_ACTION_ACTION:                   'action'

  ROLE_FEEDBACK_ACTION:                 'feedback' # one action
  ROLE_DATA_MODELS_ACTION:              'data-models' # two actions (one parent with seven children)
  ROLE_XMLEDITOR_XSLT_ACTION:           'xmleditor-xslt' # two actions
  ROLE_XMLEDITOR_XSD_ACTION:            'xmleditor-xsd' # two actions

actionRoleAccess = actionRoleRegister # used for when actions are being accessed
angular.module('mc.util.ui.actions').constant('actionRoleRegister', actionRoleRegister)
angular.module('mc.util.ui.actions').constant('actionRoleAccess', actionRoleAccess)
