angular.module('mc.core.ui.expectCatalogueElement', []).directive 'expectCatalogueElement',  ['$compile', 'enhance',  ($compile, enhance)-> {
  restrict: 'A'
  replace: false
  require: 'ngModel'

  link: (originalScope, element, attrs, modelCtrl) ->
    updateState = (newValue)->
      formGroup = element.parents('.form-group')

      return if formGroup.length == 0

      formGroup.removeClass('has-success has-warning has-feedback')

      unless newValue
        return

      if enhance.isEnhancedBy(newValue, 'catalogueElement')
        formGroup.addClass('has-success has-feedback')
        return

      formGroup.addClass('has-warning has-feedback')

    element.on 'blur', ->
      updateState modelCtrl.$modelValue
    element.on 'keydown', ($e)->
      if $e.keyCode is 13
        updateState modelCtrl.$modelValue

    originalScope.$watch (-> modelCtrl.$modelValue), updateState
}]