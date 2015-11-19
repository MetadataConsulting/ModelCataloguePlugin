angular.module('mc.core.ui.states.bs', [
  # depends on
  'mc.core.ui.states'
  # list of modules
  'mc.core.ui.states.bs.layout.html'
  'mc.core.ui.states.bs.omnisearch.html'
  'mc.core.ui.states.bs.parent.html'
  'mc.core.ui.states.bs.diff.html'
  'mc.core.ui.states.bs.list.html'
  'mc.core.ui.states.bs.favorites.html'
  'mc.core.ui.states.bs.show.html'
  'mc.core.ui.states.bs.dataImport.html'
  'mc.core.ui.states.bs.batch.html'
  'mc.core.ui.states.bs.csvTransformation.html'
])

window.modelcatalogue.registerModule 'mc.core.ui.states.bs'