angular.module('mc.util.ui.showForRole', ['mc.util.security']).directive 'showForRole', ['$animate', 'security',
  ($animate, security)->
    {
    restrict: 'A'
    link: (scope, element, attrs) ->
      updateElement = ->
        if security.hasRole(attrs.showForRole)
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