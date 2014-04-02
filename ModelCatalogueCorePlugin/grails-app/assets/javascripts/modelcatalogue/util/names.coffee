angular.module( 'mc.util.names', []).service 'names', ->
  {
    getNaturalName: (propertyName) ->
      (word[0].toUpperCase() + word[1..-1].toLowerCase() for word in propertyName.split /(?=[A-Z])(?=[A-Z])|\s+/g).join(' ').replace /([A-Z]) (?=([A-Z] ))/g, '$1'


  }