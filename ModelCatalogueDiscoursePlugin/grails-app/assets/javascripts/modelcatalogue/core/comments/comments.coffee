window.modelcatalogue.registerModule 'mc.core.comments'

changes = angular.module('mc.core.comments', ['mc.util.ui.actions', 'mc.core.ui.catalogueElementProperties', 'mc.core.modelCatalogueApiRoot', 'mc.util.enhance'])

changes.value 'discourseUrl', undefined

changes.run ['$templateCache', ($templateCache) ->
  $templateCache.put 'modelcatalogue/core/ui/catalogueElementView/comments-tab.html', '''
     <span class="contextual-actions-right">
        <contextual-actions size="sm" no-colors="true" role="comments"></contextual-actions>
     </span>
     <div class="row">
        <div class="col-md-12">
          <a class="btn btn-default btn-block text-center" ng-if="!tab.topic"><span class="fa fa-fw fa-spin fa-refresh"></span></a>
          <div class="row">
             <div class="col-md-12">
                <ul class="media-list"ng-repeat="post in tab.topic.post_stream.posts">
                  <!-- first item is the summary which is same as description -->
                  <li class="media" ng-if="!$first">
                    <div class="media-left pull-left">
                      <a href="#">
                        <img class="media-object img-circle" width="54px" height="54px" ng-src="{{tab.getUserAvatar(post.avatar_template)}}">
                      </a>
                    </div>
                    <div class="media-body">
                        <h4 class="media-heading">{{post.display_username}}</h4>
                        <div ng-bind-html="post.cooked"></div>
                    </div>
                  </li>
                </ul>
              </div>
             <div class="col-md-12" ng-if="tab.topic" >
                <ul class="media-list">
                  <!-- first item is the summary which is same as description -->
                  <li class="media">
                    <div class="media-left pull-left">
                      <a href="#">
                        <img class="media-object img-circle" width="54px" height="54px" ng-src="{{tab.getUserAvatar(tab.user.avatar_template)}}">
                      </a>
                    </div>
                    <div class="media-body">
                        <h4 class="media-heading">{{tab.user.name}}</h4>
                        <form class="form" ng-submit="tab.submitPost()">
                          <div class="form-group">
                            <textarea ng-model="tab.raw" class="form-control" placeholder="Write new comment (you can use MarkDown)"></textarea>
                          </div>
                          <div class="form-group">
                            <button type="submit" class="btn btn-success btn-block" ng-disabled="!tab.raw" ng-submit="tab.submitPost()"><span class="fa fa-fw fa-comment"></span> Submit New Comment</a>
                          </div>
                        </form>
                    </div>
                  </li>
                </ul>
              </div>
          </div>
        </div>
     </div>
    '''
]

changes.config ['enhanceProvider', (enhanceProvider)->
  condition = (item) -> item.hasOwnProperty('cooked') and item.cooked
  factory   = ['discourseUrl', (discourseUrl) ->
    (element) ->
      element.cooked = element.cooked.replace(/href="\//g, "target=\"_blank\" href=\"#{discourseUrl}").replace(/src="\//g, "src=\"#{discourseUrl}")
      element
  ]

  enhanceProvider.registerEnhancerFactory('change', condition, factory)

]

changes.config ['catalogueElementPropertiesProvider', (catalogueElementPropertiesProvider)->

  catalogueElementPropertiesProvider.configureProperty 'comments', tabDefinition: [ '$element', '$value', 'security', 'modelCatalogueApiRoot', '$http', 'discourseUrl', 'messages', ($element, $value, security, modelCatalogueApiRoot, $http, discourseUrl, messages) ->
    return undefined unless discourseUrl
    tab = {
      discourseUrl:   discourseUrl
      heading:        'Comments'
      name:           'comments'
      value:          $value
      type:           'comments-tab'
      element:        $element
      getUserAvatar:  (template)  ->
        return "" if not template
        "#{discourseUrl}#{template.replace('{size}', '54')}"
      load: ->
        $value().then (topic) ->
          tab.topic = topic
      getCurrentUser: -> security.getCurrentUser()
      submitPost: ->
        $http.post($value.link, raw: tab.raw).then (response) ->
          if response.data.errors
            for error in response.data.errors
              messages.error error
            return
          tab.raw = ''
          tab.topic.post_stream.posts.push response.data
          tab.load()
    }

    tab.load()

    tab.user = undefined

    $http.get("#{modelCatalogueApiRoot}/user/discourse").then (response) ->
      tab.user = response.data.user

    tab
  ]

]

changes.config ['actionsProvider', (actionsProvider)->
  ROLE_COMMENTS = "comments"

  actionsProvider.registerActionInRole 'open-in-discourse', ROLE_COMMENTS, ['$scope', 'discourseUrl', '$window', ($scope, discourseUrl, $window) ->
    return undefined unless discourseUrl
    action = {
      position:   1000
      label:      'Open in Forum'
      icon:       'fa fa-comments-o'
      disabled:   not $scope.topic
      action: ->
        $window.open("#{discourseUrl}t/#{$scope.tab.topic.id}", '_blank');
    }

    $scope.$watch 'tab.topic', (topic) ->
      action.disabled = not topic

    action
  ]

  actionsProvider.registerActionInRole 'refresh-comments', ROLE_COMMENTS, ['$scope', 'discourseUrl', ($scope, discourseUrl) ->
    return undefined unless discourseUrl
    action = {
      position:   1500
      label:      'Refresh'
      icon:       'fa fa-refresh'
      disabled:   not $scope.topic
      action: ->
        $scope.tab.load()
    }

    $scope.$watch 'tab.topic', (topic) ->
      action.disabled = not topic

    action
  ]


  actionsProvider.registerActionInRoles 'open-discourse', [actionsProvider.ROLE_NAVIGATION, actionsProvider.ROLE_GLOBAL_ACTIONS], ['discourseUrl', '$window', 'security', (discourseUrl, $window, security) ->
    return undefined unless security.isUserLoggedIn()
    return undefined unless discourseUrl
    {
      position:   3000
      label:      'Forum'
      icon:       'fa fa-comments'
      action: ->
        $window.open(discourseUrl, '_blank');
    }
  ]
]