angular.module('mc.core.ui.states.bs', [
  # depends on
  'mc.core.ui.states'
  # list of modules
  'mc.core.ui.states.bs.parent.html'
  'mc.core.ui.states.bs.diff.html'
  'mc.core.ui.states.bs.list.html'
  'mc.core.ui.states.bs.favorites.html'
  'mc.core.ui.states.bs.dashboard.html'
  'mc.core.ui.states.bs.show.html'
  'mc.core.ui.states.bs.batch.html'
  'mc.core.ui.states.bs.csvTransformation.html'
  'mc.core.ui.states.bs.mc.html'
  'mc.core.ui.states.bs.panels.html'
  'mc.core.ui.states.bs.dataModels.html'
])

window.modelcatalogue.registerModule 'mc.core.ui.states.bs'