angular.module( 'mc.util.names', []).service 'names', ->
  names =
    getNaturalName: (propertyName) ->
      (word[0].toUpperCase() + word[1..-1].toLowerCase() for word in propertyName.split /(?=[A-Z])(?=[A-Z])|\s+/g).join(' ').replace /([A-Z]) (?=([A-Z] ))/g, '$1'


    getPropertyNameFromType: (type) ->
      simpleName = if type.indexOf('.') > -1 then type.substring(type.lastIndexOf('.') + 1) else type
      simpleName[0].toLowerCase() + simpleName[1..-1]

  names