angular.module('mc.core.ui.bs.messagesPanel', ['mc.core.ui.messagesPanel', 'ui.bootstrap']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/messagesPanel.html', '''
      <div class="messages-panel" ng-class="{'growl': growl}">
        <alert ng-repeat="message in getMessages()" type="message.type" close="message.remove()">
          <strong ng-if="message.title">{{message.title}} </strong>{{message.body}}
        </alert>
      </div>
    '''
  ]