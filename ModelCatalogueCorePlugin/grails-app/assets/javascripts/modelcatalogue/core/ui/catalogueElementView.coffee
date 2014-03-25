angular.module('mc.core.ui.catalogueElementView', ['mc.core.catalogueElementEnhancer', 'mc.core.listReferenceEnhancer', 'mc.core.listEnhancer', 'mc.util.names']).directive 'catalogueElementView',  [-> {
    restrict: 'E'
    replace: true
    scope:
      element: '='

    templateUrl: 'modelcatalogue/core/ui/catalogueElementView.html'

    controller: ['$scope', '$log', 'enhance', 'names' , ($scope, $log, enhance, names) ->
      propExludes     = ['version', 'name', 'description']
      listEnhancer    = enhance.getEnhancer('list')
      getPropertyVal  = (propertyName) ->
        (element) -> element[propertyName]

      getObjectSize   = (object) ->
        size = 0
        angular.forEach object, () ->
          size++
        size

      relationshipsColumns = [
            {header: 'Relation',        value: 'type[direction]',                               classes: 'col-md-3'}
            {header: 'Destination',     value: "relation.name",                                 classes: 'col-md-6', show: "relation.show()"}
            {header: 'Identification',  value: "relation.elementTypeName + ': ' + relation.id", classes: 'col-md-3', show: "relation.show()"}
          ]

      mappingColumns = [
        {header: 'Destination',     value: "destination.name",                                    classes: 'col-md-4', show: 'destination.show()'}
        {header: 'Mapping',         value: 'mapping',                                             classes: 'col-md-5'}
        {header: 'Identification',  value: "destination.elementTypeName + ': ' + destination.id", classes: 'col-md-3', show: 'destination.show()'}
      ]

      onElementUpdate = (element) ->
        newRelationshipTabs = {}
        newObjectsTabs      = {}
        newActiveTabs = [true]

        for name, fn of element when enhance.isEnhancedBy(fn, 'listReference')
          tabDefintion =
            heading:  names.getNaturalName(name)
            value:    listEnhancer.createEmptyList()
            disabled: fn.total == 0
            loader:   fn

          if name == 'mappings'
            tabDefintion.columns = mappingColumns
          else
            tabDefintion.columns = relationshipsColumns

          newRelationshipTabs[name] = tabDefintion
          newActiveTabs.push(false)

        newObjectTabsCount = 0

        for name, obj of element
          if name in propExludes
            continue
          unless angular.isObject(obj) and !angular.isArray(obj) and !enhance.isEnhanced(obj)
            continue
          newObjectTabsCount++
          tabDefintion =
            heading:    names.getNaturalName(name)
            value:      obj
            disabled:   obj == undefined or obj == null or getObjectSize(obj) == 0
            properties: []


          for name, value of obj when not angular.isObject(value)
            tabDefintion.properties.push {
              label: name
              value: getPropertyVal(name)
            }

          newObjectsTabs[name] = tabDefintion
          newActiveTabs.push(false)


        newProperties = []

        if (enhance.isEnhancedBy(element, 'catalogueElement'))
          for prop in element.getUpdatableProperties()
            obj = element[prop]
            if prop in propExludes
              continue
            if enhance.isEnhancedBy(obj, 'listReference')
              continue
            if (angular.isObject(obj) and !angular.isArray(obj) and !enhance.isEnhanced(obj))
              continue
            newProperties.push(label: names.getNaturalName(prop), value: getPropertyVal(prop))



        $scope.listRelationsTabs = newRelationshipTabs
        $scope.objectTabs        = newObjectsTabs
        $scope.propertiesToShow  = newProperties
        $scope.activeTabs        = newActiveTabs
        $scope.objectTabsCount   = newObjectTabsCount



      $scope.listRelationsTabs      = {}
      $scope.objectTabs             = {}
      $scope.objectTabsCount        = 0
      $scope.loadRelations          = (tab) ->
        if !tab.disabled and tab.value.empty
          tab.loader().then (result) ->
            tab.value = result

      $scope.propertiesToShow       = []
      $scope.activeTabs             = [true]


      # watches
      $scope.$watch 'element', onElementUpdate

      # init
      onElementUpdate($scope.element)
    ]
  }
]