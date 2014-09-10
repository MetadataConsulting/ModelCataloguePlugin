angular.module('mc.core.ui.catalogueElementPicker', ['mc.core.modelCatalogueSearch', 'mc.core.catalogueElementResource', 'ui.bootstrap']).directive 'catalogueElementPicker',  ['$compile', 'modelCatalogueSearch', 'catalogueElementResource', ($compile, modelCatalogueSearch, catalogueElementResource)-> {
  restrict: 'A'
  replace: false
  terminal: true
  priority: 10000


  controller: ['$scope', '$q', '$attrs', ($scope, $q, $attrs) ->
    $scope.searchForElement = (query, pickerValue, resourceAttr) ->
      searchFun     = null
      resource      = if resourceAttr then $scope.$eval($attrs.resource) else undefined
      value         = if pickerValue then pickerValue else resource

      if (value)
        searchFun = (query) -> catalogueElementResource(value).search(query)
      else
        searchFun = (query) -> modelCatalogueSearch(query)

      deferred = $q.defer()
      searchFun(query).then (result) ->
        deferred.resolve(result.list)
      deferred.promise

    $scope.label = (el, customLabel) ->
      return ''                  if not el
      return '' + customLabel    if customLabel
      return '' + el.getLabel()  if angular.isFunction(el.getLabel)
      return '' + el
  ]

  compile: (element, attrs) ->
    label = if attrs.label then attrs.label else 'null'
    element.attr('typeahead', "el as label(el, #{label}) for el in searchForElement($viewValue, '" + (attrs.catalogueElementPicker ? '') + "', '" + (attrs.resource ? '') + "')" )
    element.attr('autocomplete', "off")
    element.attr('typeahead-wait-ms', "500") unless element.attr('typeahead-wait-ms')
    element.removeAttr('catalogue-element-picker')
    element.removeAttr('catalogueElementPicker')
    element.removeAttr('data-catalogue-element-picker')
    element.addClass('form-control')

    {
      pre: ()->
      post: (scope, element) -> $compile(element)(scope)
    }


}]