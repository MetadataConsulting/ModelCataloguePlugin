###
  Usages of simple-object-editor
catalogueElementView.tpl.coffee defines some templates using simple-object-editor:
  simple-object-editor.html and simple-object-editor-for-enumerations.html. These don't
  appear to be referenced anywhere.

catalogueElementProperties.conf.coffee
  has a line
  catalogueElementPropertiesProvider.configureProperty 'type:object', tabDefinition: objectTabDefinition('simple-object-editor')
  I'm not sure what it does.

modalPromptActionParametersEdit.coffee
  This is used for editing actions, e.g. if you go to Batches and Actions, look at a test batch, and edit an action there.
###
angular.module('mc.core.ui.simpleObjectEditor', [])
