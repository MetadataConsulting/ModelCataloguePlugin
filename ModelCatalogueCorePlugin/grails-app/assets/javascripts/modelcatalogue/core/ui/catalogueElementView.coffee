angular.module('mc.core.ui.catalogueElementView', ['mc.core.catalogueElementEnhancer', 'mc.core.listReferenceEnhancer', 'mc.core.listEnhancer']).directive 'catalogueElementView',  [-> {
    restrict: 'E'
    replace: true
    scope:
      element: '='

    templateUrl: 'modelcatalogue/core/ui/catalogueElementView.html'

    controller: ['$scope', '$log', 'enhance', 'names' , ($scope, $log, enhance, names) ->
      propExludes     = ['version', 'name', 'description', 'ext']
      listEnhancer    = enhance.getEnhancer('list')
      getPropertyVal  = (propertyName) ->
        (element) -> element[propertyName]

      getExtensionVal  = (extensionName) ->
        (element) ->
          element.ext[extensionName]

      relationshipsColumns = [
            {header: 'Relation',        value: 'type[direction]',                               classes: 'col-md-3'}
            {header: 'Destination',     value: "relation.name",                                 classes: 'col-md-6'}
            {header: 'Identification',  value: "relation.elementTypeName + ': ' + relation.id", classes: 'col-md-3'}
          ]

      mappingColumns = [
        {header: 'Destination',     value: "destination.name",                                    classes: 'col-md-4'}
        {header: 'Mapping',         value: 'mapping',                                             classes: 'col-md-5'}
        {header: 'Identification',  value: "destination.elementTypeName + ': ' + destination.id", classes: 'col-md-3'}
      ]

      relationshipItemSelect  = (relationship) -> relationship.relation.show()
      mappingItemSelect       = (mapping) -> mapping.destination.show()

      onElementUpdate = (element) ->
        newTabs       = {}
        newActiveTabs = [true]
        for name, fn of element when enhance.isEnhancedBy(fn, 'listReference')
          tabDefintion =
            heading:  names.getNaturalName(name)
            value:    listEnhancer.createEmptyList()
            disabled: fn.total == 0
            loader:   fn

          if name.indexOf('Relationships') >= 0
            tabDefintion.columns    = relationshipsColumns
            tabDefintion.itemSelect = relationshipItemSelect
          else if name == 'mappings'
            tabDefintion.columns    = mappingColumns
            tabDefintion.itemSelect = mappingItemSelect

          newTabs[name] = tabDefintion
          newActiveTabs.push(false)


        newProperties = []

        if (enhance.isEnhancedBy(element, 'catalogueElement'))
          for prop in element.getUpdatableProperties() when !(prop in propExludes)
            newProperties.push(label: names.getNaturalName(prop), value: getPropertyVal(prop))
          if element.ext?
            newProperties.push {}
            newProperties.push label: 'Extensions'
            for name, ignored of element.ext
              newProperties.push(label: name, value: getExtensionVal(name))


        $scope.listRelationsTabs = newTabs
        $scope.propertiesToShow  = newProperties
        $scope.activeTabs        = newActiveTabs



      $scope.listRelationsTabs      = {}
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