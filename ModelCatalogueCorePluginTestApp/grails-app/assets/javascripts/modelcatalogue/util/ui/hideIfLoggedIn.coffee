angular.module('mc.util.ui.hideIfLoggedIn', ['mc.util.security']).directive 'hideIfLoggedIn', ['$animate', 'security',
  ($animate, security)->
    {
    restrict: 'A'
    link: (scope, element) ->
      updateElement = ->
        if security.isUserLoggedIn()
          $animate.addClass(element, 'security-hide')
        else
          $animate.removeClass(element, 'security-hide')

      updateElement()

      scope.$on 'userLoggedIn', ->
        updateElement()

      scope.$on 'userLoggedOut', ->
        updateElement()


    }
]