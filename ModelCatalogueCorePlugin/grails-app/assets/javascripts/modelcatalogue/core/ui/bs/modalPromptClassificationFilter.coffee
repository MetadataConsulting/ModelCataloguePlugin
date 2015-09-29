angular.module('mc.core.ui.bs.modalPromptClassificationFilter', ['mc.util.messages', 'mc.util.ui.focusMe']).config ['messagesProvider', (messagesProvider)->
  messagesProvider.setPromptFactory 'classification-filter',  [ '$modal', ($modal) ->
    (title, body, args) ->
      dialog = $modal.open {
        windowClass: 'messages-modal-prompt'
        template: '''
         <div class="modal-header">
            <h4>Classifications Filter</h4>
        </div>
        <div class="modal-body">
            <form role="form">
              <div class="checkbox">
                <label>
                  <input type="checkbox" ng-model="filter.unclassifiedOnly"> <strong>Show Unclassified Elements Only</strong>
                </label>
              </div>
              <div class="form-group" ng-hide="filter.unclassifiedOnly">
                <label for="elements">Included Classifications</label>
                <elements-as-tags elements="filter.includes"></elements-as-tags>
                <input id="includes" placeholder="Name or Catalogue ID" ng-model="pending.include" catalogue-element-picker="classification" label="el.name" typeahead-on-select="push(pending.include, filter.includes, filter.excludes); pending.include = null" focus-me="true" >
              </div>
              <div class="form-group" ng-hide="filter.unclassifiedOnly">
                <label for="elements">Excluded Classifications</label>
                <elements-as-tags elements="filter.excludes"></elements-as-tags>
                <input id="excludes" placeholder="Name or Catalogue ID" ng-model="pending.exclude" catalogue-element-picker="classification" label="el.name" typeahead-on-select="push(pending.exclude, filter.excludes, filter.includes); pending.exclude = null">
              </div>
            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" ng-click="$close(filter)">OK</button>
            <button class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
        </div>
        '''

        controller: ['$scope', ($scope) ->
          emptyFilter = -> {includes: [], excludes: [], unclassifiedOnly: false}
          $scope.filter = if args.filter then angular.extend(emptyFilter(), args.filter) else emptyFilter()
          $scope.push = (item, container, otherContainer) ->
            otherItemExistingIndex = -1
            for otherItem, i in otherContainer
              if otherItem?.link == item.link
                otherItemExistingIndex = i
                break

            if otherItemExistingIndex > -1
              otherContainer.splice otherItemExistingIndex, 1

            container.push item

        ]
      }

      dialog.result
  ]
]