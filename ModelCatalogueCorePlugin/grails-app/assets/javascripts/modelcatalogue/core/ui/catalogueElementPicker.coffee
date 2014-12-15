catalogueElementPicker = angular.module('mc.core.ui.catalogueElementPicker', ['mc.core.modelCatalogueSearch', 'mc.core.catalogueElementResource', 'ui.bootstrap'])
catalogueElementPicker.run ['$templateCache', ($templateCache) ->
  $templateCache.put "modelcatalogue/core/ui/catalogueElementPickerTypeahead.html", """
        <a class="cep-item"><span class="omnisearch-text" ng-class="{'text-warning': match.model.status == 'DRAFT', 'text-info': match.model.status == 'PENDING'}"><span class="text-muted" ng-class="match.model.getIcon()"/><span> {{match.model.classifiedName}}</span></a>
  """
]
catalogueElementPicker.directive 'catalogueElementPicker',  ['$compile', 'modelCatalogueSearch', 'catalogueElementResource', ($compile, modelCatalogueSearch, catalogueElementResource)-> {
  restrict: 'A'
  replace: false
  terminal: true
  priority: 10000


  controller: ['$scope', '$q',  ($scope, $q ) ->
    $scope.searchForElement = (query, pickerValue, resourceAttr) ->

      searchFun     = null
      resource      = if resourceAttr then $scope.$eval(resourceAttr) ? $scope.$parent.$eval(resourceAttr) else undefined
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
    icon  = """<span class="input-group-addon"><catalogue-element-icon type="'#{attrs.catalogueElementPicker ? ''}' ? '#{attrs.catalogueElementPicker ? ''}' : #{attrs.resource ? 'null'}"></catalogue-element-icon></span>"""

    label = if attrs.label then attrs.label else 'null'
    element.attr('typeahead', "el as label(el, #{label}) for el in searchForElement($viewValue, '" + (attrs.catalogueElementPicker ? '') + "', '" + (attrs.resource ? '') + "')" )
    element.attr('autocomplete', "off")
    element.attr('typeahead-wait-ms', "500") unless element.attr('typeahead-wait-ms')
    element.attr('typeahead-template-url', 'modelcatalogue/core/ui/catalogueElementPickerTypeahead.html')
    element.removeAttr('catalogue-element-picker')
    element.removeAttr('catalogueElementPicker')
    element.removeAttr('data-catalogue-element-picker')
    element.addClass('form-control')

    unless element.parent().hasClass('input-group')
      group = angular.element("""<span class="input-group"></span>""")
      group.append(icon)
      group.append(element.clone())
      element.replaceWith group

    {
      pre: (scope, element) ->
        if element.parent().hasClass('input-group')
          element.parent().prepend($compile(icon)(scope))
      post: (scope, element) -> $compile(element)(scope)
    }


}]