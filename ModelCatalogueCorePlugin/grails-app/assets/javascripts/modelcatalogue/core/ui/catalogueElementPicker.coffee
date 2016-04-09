catalogueElementPicker = angular.module('mc.core.ui.catalogueElementPicker', ['mc.core.modelCatalogueSearch', 'mc.core.catalogueElementResource', 'ui.bootstrap'])
catalogueElementPicker.run ['$templateCache', ($templateCache) ->
  $templateCache.put "modelcatalogue/core/ui/catalogueElementPickerTypeahead.html", """
        <a class="cep-item" ng-class="{'show-more-cep-item': match.model.more}">
        <span class="omnisearch-text" ng-class="{'text-warning': match.model.status == 'DRAFT', 'text-info': match.model.status == 'PENDING'}">
          <span class="text-muted" ng-class="match.model.getIcon()"/><span> {{match.model.classifiedName}}
        </span><br/>
        <small ng-if="match.model.description">{{match.model.description}}</small>
        </a>
  """
]
catalogueElementPicker.directive 'catalogueElementPicker',  ['$compile', 'modelCatalogueSearch', 'catalogueElementResource', 'messages',  ($compile, modelCatalogueSearch, catalogueElementResource, messages)-> {
  restrict: 'A'
  replace: false
  terminal: true
  priority: 10000


  controller: ['$scope', '$q', '$parse', '$state', '$attrs',  ($scope, $q, $parse, $state, $attrs) ->
    $scope.searchForElement = (query, ngModel, pickerValue, resourceAttr, statusAttr, globalAttr, onSelect) ->
      searchFun     = null
      resource      = if resourceAttr then $scope.$eval(resourceAttr) ? $scope.$parent.$eval(resourceAttr) else undefined
      value         = if pickerValue then pickerValue else resource
      params        = {}

      if statusAttr
        params.status = statusAttr

      if $state.params.dataModelId and $state.params.dataModelId != 'catalogue'
        params.dataModel = $state.params.dataModelId

      if (value)
        searchFun = (query) -> catalogueElementResource(value).search(query, params)
      else
        searchFun = (query) -> modelCatalogueSearch(query, params)

      deferred = $q.defer()
      searchFun(query).then (result) ->
        list = result.list ? []
        list.push({
          name: "Search More"
          classifiedName: "Search More"
          getIcon: -> "fa fa-fw fa-search"
          more: true
          description: "View all items, search globally or add more imports. Press 'Esc' to dismiss."
          openSearchMore: ->
            $scope.searchForMore(ngModel, pickerValue, resourceAttr, statusAttr, globalAttr, onSelect)
        })
        deferred.resolve(list)
      deferred.promise

    $scope.label = (el, customLabel) ->
      return ''                  if not el
      return '' + customLabel    if customLabel
      return '' + el.getLabel()  if angular.isFunction(el.getLabel)
      return '' + el
    $scope.searchForMore = (ngModel, pickerValue, resourceAttr, statusAttr, globalAttr, onSelect)->
      unless ngModel
        throw "ng-model for catalogue-element-picker is missing cannot search for more elements"

      resource      = if resourceAttr then $scope.$eval(resourceAttr) ? $scope.$parent.$eval(resourceAttr) else undefined
      value         = if pickerValue then pickerValue else resource

      messages.prompt(null, null, {type: 'search-catalogue-element', resource: value, status: statusAttr, currentDataModel: $scope.currentDataModel, global: globalAttr}).then (element) ->
        $parse(ngModel).assign($scope, element)
        $scope.$eval onSelect, {$item: element, $model: element, $label: element.classifiedName} if onSelect

    $scope.customCepOnSelect = ($item, $model, $label, typeaheadOnSelect) ->
      if $item and $item.more and angular.isFunction($item.openSearchMore)
        $item.openSearchMore()
        $scope.$evalAsync ->
          $parse($attrs.ngModel).assign($scope, undefined)

      if typeaheadOnSelect
        $scope.$eval typeaheadOnSelect, $item: $item, $model: $model, $label: $label

  ]

  compile: (element, attrs) ->
    escape = (string) ->
      stringified = JSON.stringify(string)
      stringified.substring(1,stringified.length - 1).replace(/&/, "&amp;").replace(/"/g, "&quot;")

    icon  = """<span class="input-group-addon search-for-more-icon" ng-click="searchForMore(&quot;""" + escape(attrs.ngModel ? '') + "&quot;, &quot;" + escape(attrs.catalogueElementPicker ? '') + "&quot;, &quot;" + escape(attrs.resource ? '') + "&quot;, &quot;" + escape(attrs.status ? '') + """&quot;, """ + attrs.global + """ ,&quot;""" + escape(attrs.typeaheadOnSelect ? '')  + """&quot;)" title="Search more ..."><catalogue-element-icon type="'#{attrs.catalogueElementPicker ? ''}' ? '#{attrs.catalogueElementPicker ? ''}' : #{attrs.resource ? 'null'}"></catalogue-element-icon></span>"""
    label = if attrs.label then attrs.label else 'null'

    element.attr('typeahead-on-select', 'customCepOnSelect($item, $model, $label, "' + escape(attrs.typeaheadOnSelect ? '') + '")')
    element.attr('typeahead', "el as label(el, #{label}) for el in searchForElement($viewValue, \"" + escape(attrs.ngModel ? '') + "\", \"" + escape(attrs.catalogueElementPicker ? '') + "\",\"" + escape(attrs.resource ? '') + "\", \"" + escape(attrs.status ? '') + "\", " + attrs.global + " ,\"" + escape(attrs.typeaheadOnSelect ? '')  + "\")" )
    element.attr('autocomplete', "off")
    element.attr('typeahead-wait-ms', "50") unless element.attr('typeahead-wait-ms')
    element.attr('typeahead-template-url', 'modelcatalogue/core/ui/catalogueElementPickerTypeahead.html')
    element.attr('placeholder', if attrs.status then "Start typing or click icon on the left for advanced search for #{attrs.status} elements" else 'Start typing or click icon on the left for advanced search')
    element.removeAttr('catalogue-element-picker')
    element.removeAttr('catalogueElementPicker')
    element.removeAttr('data-catalogue-element-picker')
    element.addClass('form-control')
    element.attr('expect-catalogue-element', '')

    element.addClass('cep-' + attrs.status.toLowerCase()) if (attrs.status)




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
