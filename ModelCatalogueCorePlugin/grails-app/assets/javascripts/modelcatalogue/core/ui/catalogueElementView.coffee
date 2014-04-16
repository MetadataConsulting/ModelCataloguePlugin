angular.module('mc.core.ui.catalogueElementView', ['mc.core.catalogueElementEnhancer', 'mc.core.listReferenceEnhancer', 'mc.core.listEnhancer', 'mc.util.names', 'mc.util.messages', 'mc.core.ui.columns']).directive 'catalogueElementView',  [-> {
    restrict: 'E'
    replace: true
    scope:
      element: '='
      id: '@'

    templateUrl: 'modelcatalogue/core/ui/catalogueElementView.html'

    controller: ['$scope', '$log', '$filter', '$q', 'enhance', 'names', 'columns', 'messages' , ($scope, $log, $filter, $q, enhance, names, columns, messages) ->
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
            actions:  []
            name:     name

          if fn.itemType == 'org.modelcatalogue.core.Relationship'
            tabDefintion.actions.push {
              title:  'Remove'
              icon:   'remove'
              type:   'danger'
              action: (rel) ->
                deferred = $q.defer()
                messages.confirm('Removing Relationship', "Do you really want to remove relation '#{element.name} #{rel.type[rel.direction]} #{rel.relation.name}'?").then () ->
                    rel.remove().then ->
                      messages.success('Relationship removed!', "#{rel.relation.name} is no longer related to #{element.name}")
                      # reloads the table
                      deferred.resolve(true)
                    , (response) ->
                      if response.status == 404
                        messages.error('Error removing relationship', 'Relationship cannot be removed, it probably does not exist anymore. The table was refreshed to get the most up to date results.')
                        deferred.resolve(true)
                      else
                        messages.error('Error removing relationship', 'Relationship cannot be removed, see application logs for details')

                deferred.promise


            }

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