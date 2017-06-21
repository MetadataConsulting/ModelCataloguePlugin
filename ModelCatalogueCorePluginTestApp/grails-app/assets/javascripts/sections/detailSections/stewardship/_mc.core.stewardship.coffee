window.modelcatalogue.registerModule('mc.core.stewardship')
stewardship = angular.module('mc.core.stewardship',
  ['mc.core.ui.detailSections',
    'mc.core.stewardship.metadata' # Need to include this to use the templates under stewardship/metadata
    # bootstrap for date picker?
    #,'ui.bootstrap'
  ])
