angular.module('mc.util.ui.hideForRole', ['mc.util.security']).directive 'hideForRole',  ['$animate', 'security', ($animate, security)-> {
  restrict: 'A'
  link: (scope, element, attrs) ->
    updateElement = ->
      if security.hasRole(attrs.hideForRole)
        $animate.addClass(element, 'ng-hide')
      else
        $animate.removeClass(element, 'ng-hide')

    updateElement()

    scope.$on 'userLoggedIn', ->
      updateElement()

    scope.$on 'userLoggedOut', ->
      updateElement()


}]