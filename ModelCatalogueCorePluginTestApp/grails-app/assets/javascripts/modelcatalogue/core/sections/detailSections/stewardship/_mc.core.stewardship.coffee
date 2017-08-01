window.modelcatalogue.registerModule('modelcatalogue.core.sections.detailSections.stewardship')
stewardship = angular.module('modelcatalogue.core.sections.detailSections.stewardship',
  ['modelcatalogue.core.sections.detailSections',
    'modelcatalogue.core.sections.detailSections.stewardship.metadata' # Need to include this to use the templates under stewardship/metadata
    # bootstrap for date picker?
    #,'ui.bootstrap'
  ])
