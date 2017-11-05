actionRole =
  ROLE_NAVIGATION:              'navigation'
  ROLE_NAVIGATION_RIGHT:        'navigation-right'

  ROLE_LIST_ACTION:             'list'

  ROLE_ITEM_ACTION:             'item'
  ROLE_ITEM_DETAIL_ACTION:      'item-detail'
  ROLE_ITEM_INFINITE_LIST:     'item-infinite-list'

  ROLE_MODAL_ACTION:            'modal'
  ROLE_LIST_HEADER_ACTION:      'header'
  ROLE_LIST_FOOTER_ACTION:      'footer'
  ROLE_GLOBAL_ACTION:           'global-action'

  ROLE_ACTION_ACTION:           'action'

  ROLE_FEEDBACK:                'feedback' # one action
  ROLE_DATA_MODELS :            'data-models' # two actions (one parent with seven children)
  ROLE_XMLEDITOR_XSLT:          'xmleditor-xslt' # two actions
  ROLE_XMLEDITOR_XSD:           'xmleditor-xsd' # two actions

angular.module('mc.util.ui.actions').constant('actionRole', actionRole)
