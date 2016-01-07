angular.module('mc.core.ui.bs.watchAndAskForImportOrCloneCtrl', []).controller 'watchAndAskForImportOrCloneCtrl', ['$scope', 'messages', '$parse', '$q', ($scope, messages, $parse, $q) ->

  $scope.cloneOrImport = (element, currentDataModel) ->
    return $q.when(element) unless element
    return $q.when(element) unless currentDataModel
    return $q.when(element) unless angular.isFunction(currentDataModel.execute)
    return $q.when(element) if angular.isString(element)
    return $q.when(element) unless element.id

    deferred = $q.defer()

    doCloneOrImport = (result) ->
      if result == 'clone'
        element.execute("clone/#{currentDataModel.id}", 'POST').then (clone)->
          deferred.resolve(clone)
      if result == 'import'
        currentDataModel.imports.add(element.dataModel).then ->
          deferred.resolve(element)
      return deferred.promise

    reject = (result) ->
      deferred.reject(result)



    currentDataModel.execute("containsOrImports/#{element.id}").then (result) ->
      if result.success
        return deferred.resolve(element)

      messages.prompt('Import or Clone',
        "The selected element does not belong to current data model nor to any data models imported. Do you want to clone it into the current data model or do you want to import <strong>#{element.dataModel?.name}</strong>. The action is performed immediately.",
        {
          type: 'options',
          options: [
            {value: 'clone', label: 'Clone', classes: 'btn btn-primary', icon: 'fa fa-fw fa-clone'},
            {value: 'import', label: 'Import', classes: 'btn btn-primary', icon: 'fa fa-fw fa-upload'}
          ],
          onSelect: doCloneOrImport,
          onDismiss: reject
        })

    return deferred.promise


  $scope.watchAndAskForImportOrClone = (what) ->
    $scope.$watch what, (element) ->
      $scope.cloneOrImport(element, $scope.currentDataModel).then(
        (element) -> $parse(what).assign($scope, element)
      , -> $parse(what).assign($scope, undefined))


]
