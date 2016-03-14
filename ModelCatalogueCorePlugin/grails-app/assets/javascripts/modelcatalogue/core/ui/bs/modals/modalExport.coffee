angular.module('mc.core.ui.bs.modalExport', ['mc.util.messages']).config ['messagesProvider', (messagesProvider)->
  messagesProvider.setPromptFactory 'export', ['$modal', ($modal) ->
    (title, body, args) ->
      dialog = $modal.open {
        windowClass: 'messages-modal-confirm'
        templateUrl: '/mc/core/ui/modals/modalExport.html'
        controller: ['$scope', ($scope) ->
            $scope.title = title
            $scope.body = args.body
            $scope.type = args.type ? 'text'
            $scope.hasExportDepth = args.hasExportDepth
            $scope.result = {
              value: args.value,
              exportDepth: if args.hasExportDepth then 3 else null
            }
        ]
      }

      dialog.result
  ]
]
