###
  Templates put in this directory tree are minimized and put to the AngularJS's template cache.

  Appropriate AngularJS module must already exist for a path e.g. for `/mc/core/ui/states/templates/*.tpl.html`
  there must be `mc.core.ui.state` module defined elsewhere. The template name strips the `tpl` from the name
  and also the template URLs starts with the slash `/` e.g. `/mc/core/ui/states/templates/dataModel.tpl.html`
  can be referred as `/mc/core/ui/states/templates/dataModel.html` in the JavaScript/CoffeeScript code.
###

#= require_full_tree .
