angular.module('mc.core.ui.catalogueElementPicker', ['mc.core.modelCatalogueSearch', 'mc.core.catalogueElementResource', 'ui.bootstrap']).directive 'catalogueElementPicker',  ['$compile', 'modelCatalogueSearch', 'catalogueElementResource', ($compile, modelCatalogueSearch, catalogueElementResource)-> {
  restrict: 'A'
  replace: false
  terminal: true
  priority: 10000

  controller: ['$scope', '$q', '$attrs', ($scope, $q, $attrs) ->
    value = $attrs.catalogueElementPicker
    searchFun = () -> $.when({list: []})

    if (value)
      searchFun = (query) -> catalogueElementResource(value).search(query)
    else
      searchFun = (query) -> modelCatalogueSearch(query)

    $scope.searchForElement = (query) ->
      deferred = $q.defer()
      searchFun(query).then (result) ->
        deferred.resolve(result.list)
      deferred.promise
  ]

  compile: (element, attrs) ->
    element.attr('typeahead', "el as (el.name + ' (' + el.elementTypeName + ': ' + el.id + ')') for el in searchForElement($viewValue)" )
    element.removeAttr('catalogue-element-picker')
    element.removeAttr('catalogueElementPicker')
    element.removeAttr('data-catalogue-element-picker')
    element.addClass('form-control')

    {
      pre: ()->
      post: (scope, element) -> $compile(element)(scope)
    }


}]