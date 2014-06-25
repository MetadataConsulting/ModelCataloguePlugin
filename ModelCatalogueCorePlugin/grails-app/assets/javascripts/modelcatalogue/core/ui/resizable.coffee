#= require jquery/dist/jquery.js
#= require jquery-ui/ui/jquery-ui.js
#= require jquery-ui/ui/jquery.ui.resizable.js
#= require jquery-ui/themes/base/jquery-ui.css
#= require jquery-ui/themes/base/jquery.ui.resizable.css


angular.module('mc.core.ui.resizable', []).directive 'resizable',  [-> {
restrict: 'C'
link: (scope, element, attrs) ->
  $(element).resizable({ handles: "n, e, s, w" })
  return
}
]