angular.module('mc.core.ui.bs.watchAndAskForImportOrCloneCtrl', []).controller 'watchAndAskForImportOrCloneCtrl', ['$scope', 'messages', '$parse', ($scope, messages, $parse) ->

  $scope.watchAndAskForImportOrClone = (what) ->
    $scope.$watch what, (element) ->
      assignNewValue = $parse(what).assign
      cloneOrImport = (result) ->
        if result == 'clone'
          return element.execute("clone/#{$scope.currentDataModel.id}", 'POST').then (clone)->
            assignNewValue($scope, clone)
            $scope.copy.dataType = clone
        else if result == 'import'
            return $scope.currentDataModel.imports.add(element.dataModel)

      reject = ->
        assignNewValue($scope, undefined)

      return unless element
      return unless $scope.currentDataModel
      return if angular.isString(element)
      return unless element.id
      if angular.isFunction($scope.currentDataModel.execute)
        $scope.currentDataModel.execute("containsOrImports/#{element.id}").then (result) ->
          unless result.success
            messages.prompt('Import or Clone',
              "The selected element does not belong to current data model nor to any data models imported. Do you want to clone it into the current data model or do you want to import #{element.dataModel?.name}",
              {type: 'options', options: [
                {value: 'clone', label: 'Clone', classes: 'btn btn-primary', icon: 'fa fa-fw fa-clone'}
                {value: 'import', label: 'Import', classes: 'btn btn-primary', icon: 'fa fa-fw fa-upload'}
              ]}).then(cloneOrImport, reject)

]
