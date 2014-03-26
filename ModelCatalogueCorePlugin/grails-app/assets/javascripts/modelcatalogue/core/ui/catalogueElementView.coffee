angular.module('mc.core.ui.catalogueElementView', ['mc.core.catalogueElementEnhancer', 'mc.core.listReferenceEnhancer', 'mc.core.listEnhancer', 'mc.util.names', 'mc.core.ui.columns']).directive 'catalogueElementView',  [-> {
    restrict: 'E'
    replace: true
    scope:
      element: '='

    templateUrl: 'modelcatalogue/core/ui/catalogueElementView.html'

    controller: ['$scope', '$log', '$filter', 'enhance', 'names', 'columns' , ($scope, $log, $filter, enhance, names, columns) ->
      propExludes     = ['version', 'name', 'description']
      listEnhancer    = enhance.getEnhancer('list')
      getPropertyVal  = (propertyName) ->
        (element) -> element[propertyName]

      getObjectSize   = (object) ->
        size = 0
        angular.forEach object, () ->
          size++
        size

      onElementUpdate = (element) ->
        tabs = []

        for name, fn of element when enhance.isEnhancedBy(fn, 'listReference')
          tabDefintion =
            heading:  names.getNaturalName(name)
            value:    listEnhancer.createEmptyList(fn.itemType)
            disabled: fn.total == 0
            loader:   fn
            type:     'decorated-list'
            columns:   columns(fn.itemType)

          tabs.push tabDefintion


        for name, obj of element
          if name in propExludes
            continue
          unless angular.isObject(obj) and !angular.isArray(obj) and !enhance.isEnhanced(obj)
            continue
          tabDefintion =
            heading:    names.getNaturalName(name)
            value:      obj
            disabled:   obj == undefined or obj == null or getObjectSize(obj) == 0
            properties: []
            type:       'properties-pane'


          for key, value of obj when not angular.isObject(value)
            tabDefintion.properties.push {
              label: key
              value: getPropertyVal(key)
            }

          tabs.push tabDefintion


        tabs = $filter('orderBy')(tabs, 'heading')

        if enhance.isEnhancedBy(element, 'catalogueElement')
          newProperties = []
          for prop in element.getUpdatableProperties()
            obj = element[prop]
            if prop in propExludes
              continue
            if enhance.isEnhancedBy(obj, 'listReference')
              continue
            if (angular.isObject(obj) and !angular.isArray(obj) and !enhance.isEnhanced(obj))
              continue
            newProperties.push(label: names.getNaturalName(prop), value: getPropertyVal(prop))

          tabDefintion =
            heading:    'Properties'
            value:      element
            disabled:   getObjectSize(newProperties) == 0
            properties: newProperties
            type:       'properties-pane'

          tabs.unshift tabDefintion


        showTabs = false
        for tab in tabs
          if not tab.disabled
            tab.active = true
            showTabs = true
            break

        $scope.tabs = tabs
        $scope.showTabs = showTabs

      $scope.tabs   = []
      $scope.doLoad = (tab) ->
        return if not tab.loader?
        if !tab.disabled and tab.value.empty
          tab.loader().then (result) ->
            tab.columns = columns(result.itemType)
            tab.value = result



      # watches
      $scope.$watch 'element', onElementUpdate

      # init
      onElementUpdate($scope.element)
    ]
  }
]