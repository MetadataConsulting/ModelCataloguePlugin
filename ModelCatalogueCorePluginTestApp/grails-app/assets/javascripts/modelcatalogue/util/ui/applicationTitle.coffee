angular.module('mc.util.ui.applicationTitle', ['ui.router']).provider 'applicationTitle', ->
  applicationTitleProvider  = {defaultTitle: angular.element('title').text()}

  applicationTitleProvider.$get = ['$rootScope', '$state', ($rootScope, $state) ->
    angular.element('title').text($state.current?.data?.applicationTitle ? applicationTitleProvider.defaultTitle)

    applicationTitle = (newTitle) ->
      return angular.element('title').text() if arguments.length == 0
      angular.element('title').text(newTitle)
      return newTitle

    $rootScope.$on '$stateChangeSuccess', (ignored, toState) ->
      applicationTitle(toState.data?.applicationTitle) if toState.data?.applicationTitle

    applicationTitle
  ]

  applicationTitleProvider