angular.module('mc.core.ui.catalogueElementTreeview', ['mc.core.ui.catalogueElementTreeviewItem']).directive 'catalogueElementTreeview',  [-> {
    restrict: 'E'
    replace: true
    scope:
      element: '='
      descend: '='

    templateUrl: 'modelcatalogue/core/ui/catalogueElementTreeview.html'
  }
]