angular.module('mc.util.ui.showIfLoggedIn', ['mc.util.security']).directive 'showIfLoggedIn', ['$animate', 'security',
  ($animate, security)->
    {
    restrict: 'A'
    link: (scope, element) ->
      updateElement = ->
        if security.isUserLoggedIn()
          $animate.removeClass(element, 'security-hide')
        else
          $animate.addClass(element, 'security-hide')

      updateElement()

      scope.$on 'userLoggedIn', ->
        updateElement()

      scope.$on 'userLoggedOut', ->
        updateElement()


    }
]