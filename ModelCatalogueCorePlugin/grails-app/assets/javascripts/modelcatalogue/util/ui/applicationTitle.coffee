angular.module('mc.util.ui.applicationTitle', []).provider 'applicationTitle', ->
  applicationTitleProvider  = {defaultTitle: angular.element('title').text()}

  applicationTitleProvider.$get = ->
    angular.element('title').text(applicationTitleProvider.defaultTitle)

    applicationTitle = (newTitle) ->
      return angular.element('title').text() if arguments.length == 0
      angular.element('title').text(newTitle)
      return newTitle
    applicationTitle

  applicationTitleProvider