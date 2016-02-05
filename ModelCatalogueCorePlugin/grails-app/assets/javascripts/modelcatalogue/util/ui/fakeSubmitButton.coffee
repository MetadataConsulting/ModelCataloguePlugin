angular.module('mc.util.ui.fakeSubmitButton', []).directive 'fakeSubmitButton',  [-> {
  restrict: 'E'
  replace: true

  template: '''<input type="submit" class="fsb" style="position: absolute; left: -9999px; width: 1px; height: 1px;"/>'''
}]