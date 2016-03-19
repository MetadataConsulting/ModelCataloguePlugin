angular.module('mc.core.ui.bs.modalExport', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
  messagesProvider.setPromptFactory 'export', ['$modal', ($modal) ->
    (title, body, args) ->
      dialog = $modal.open {
        windowClass: 'messages-modal-confirm'
        templateUrl: '/mc/core/ui/modals/modalExport.html'
        controller: ['$scope', ($scope) ->
            $scope.title = title
            $scope.type = args.type ? 'text'
            $scope.depth = args.depth
            $scope.includeMetadata = args.includeMetadata
            $scope.result = {
              assetName: args.assetName,
              depth: args.depth,
              includeMetadata: args.includeMetadata
            }
        ]
      }

      dialog.result
  ]
]
