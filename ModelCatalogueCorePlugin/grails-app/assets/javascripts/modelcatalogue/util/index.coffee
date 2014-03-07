#= require_self
#= require rest
#= require enhance
#= require names
#= require createConstantPromise

angular.module 'mc.core', [
  # list of modules
  'mc.util.createConstantPromise'
  'mc.util.enhance'
  'mc.util.rest'
  'mc.util.names'
]