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


    commaSeparatedList = (things)->
      out = ''
      angular.forEach(things, (thing)->
        out = out + "#{thing.name} "
        return
      )
      out


    $scope.label = (element)->
      if(element)
        if (element.classifications? && element.classifications.length>0)
          classificationNames = commaSeparatedList(element.classifications)
          return "#{element.name} ( #{element.elementTypeName} in #{classificationNames})"
        else if(element.conceptualDomains? && element.conceptualDomains.length>0)
          conceptualDomains = commaSeparatedList(element.conceptualDomains)
          return "#{element.name} ( #{element.elementTypeName} in #{conceptualDomains})"
        else
          return "#{element.name} ( #{element.elementTypeName} )"

  ]

  compile: (element, attrs) ->
    element.attr('typeahead', "el as label(el) for el in searchForElement($viewValue, '" + (attrs.catalogueElementPicker ? '') + "', '" + (attrs.resource ? '') + "')" )
    element.removeAttr('catalogue-element-picker')
    element.removeAttr('catalogueElementPicker')
    element.removeAttr('data-catalogue-element-picker')
    element.addClass('form-control')

    {
      pre: ()->
      post: (scope, element) -> $compile(element)(scope)
    }


}]