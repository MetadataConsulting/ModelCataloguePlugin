angular.module('modelcatalogue.core.ui.exports.modalExport', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
  messagesProvider.setPromptFactory 'export', ['$uibModal', ($uibModal) ->
    (title, body, args) ->
      dialog = $uibModal.open {
        windowClass: 'messages-modal-confirm'
        templateUrl: '/modelcatalogue/core/ui/exports/modalExport.html'
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
