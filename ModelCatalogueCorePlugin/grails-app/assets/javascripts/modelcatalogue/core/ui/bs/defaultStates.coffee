angular.module('mc.core.ui.bs.defaultStates', ['ui.router']).config ['$stateProvider', ($stateProvider)->

  $stateProvider.state 'mc', {abstract: true, url: '/catalogue'}
  $stateProvider.state 'mc.resource', {abstract: true, url: '/:resource'}
  $stateProvider.state 'mc.resource.show', {url: '/{id:\\d+}'}

]