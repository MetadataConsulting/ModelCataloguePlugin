naturalNames = {}
propertyNamesFromTypes = {}
propertyNamesFromQualifiers = {}

names = {
  getNaturalName: (propertyName) ->
    return null if not propertyName
    return propertyName if propertyName.indexOf('http') == 0
    naturalNames[propertyName] = naturalNames[propertyName] ? (word[0].toUpperCase() + word[1..-1].toLowerCase() for word in propertyName.split /(?=[A-Z])(?=[A-Z])|\s+/g).join(' ').replace /([A-Z]) (?=([A-Z] ))/g, '$1'


  getPropertyNameFromType: (type) ->
    return null if not type
    return type if type.indexOf('http') == 0
    cached = propertyNamesFromTypes[type]
    return cached if cached
    simpleName = type
    simpleName = simpleName.substring(simpleName.lastIndexOf('.') + 1)  if simpleName.indexOf('.') > -1
    # javassist fix
    simpleName = simpleName.substring(0, simpleName.indexOf('_'))       if simpleName.indexOf('_') > -1
    simpleName = simpleName[0].toLowerCase() + simpleName[1..-1]
    propertyNamesFromTypes[type] = simpleName

  ###
  This function is the identity on urls beginning with http
  Otherwise it returns the substring after the last '.' in type.
  ###
  getPropertyNameFromQualifier: (type) ->
    return null if not type
    return type if type.indexOf('http') == 0
    propertyNamesFromQualifiers[type] = propertyNamesFromQualifiers[type] ? if type.indexOf('.') > -1 then type.substring(type.lastIndexOf('.') + 1) else type

  capitalize: (words) ->
    return words.replace /(?:^|\s)\S/g, (a) -> return a.toUpperCase()
}

angular.module( 'mc.util.names', []).constant 'names', names
