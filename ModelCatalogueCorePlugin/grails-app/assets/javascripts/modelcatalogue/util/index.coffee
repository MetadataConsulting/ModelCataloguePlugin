#= require_self
#= require rest
#= require enhance
#= require recursiveCompile
#= require names

angular.module 'mc.util', [
  # list of modules
  'mc.util.enhance'
  'mc.util.rest'
  'mc.util.names'
  'mc.util.recursiveCompile'
]