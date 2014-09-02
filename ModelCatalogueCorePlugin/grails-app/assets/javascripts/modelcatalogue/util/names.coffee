naturalNames = {}
propertyNamesFromTypes = {}
propertyNamesFromQualifiers = {}

names = {
  getNaturalName: (propertyName) ->
    return null if not propertyName
    naturalNames[propertyName] = naturalNames[propertyName] ? (word[0].toUpperCase() + word[1..-1].toLowerCase() for word in propertyName.split /(?=[A-Z])(?=[A-Z])|\s+/g).join(' ').replace /([A-Z]) (?=([A-Z] ))/g, '$1'


  getPropertyNameFromType: (type) ->
    return null if not type
    simpleName = if type.indexOf('.') > -1 then type.substring(type.lastIndexOf('.') + 1) else type
    propertyNamesFromTypes[type] = propertyNamesFromTypes[type] ? simpleName[0].toLowerCase() + simpleName[1..-1]

  getPropertyNameFromQualifier: (type) ->
    return null if not type
    propertyNamesFromQualifiers[type] = propertyNamesFromQualifiers[type] ? if type.indexOf('.') > -1 then type.substring(type.lastIndexOf('.') + 1) else type
}

angular.module( 'mc.util.names', []).constant 'names', names