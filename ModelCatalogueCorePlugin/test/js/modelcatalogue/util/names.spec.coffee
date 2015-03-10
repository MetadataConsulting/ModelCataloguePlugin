describe 'mc.util.names', ->
  beforeEach module 'mc.util.names'

  naturalNamesData = {
    myFancyProperty:  'My Fancy Property'
    theURLLocation:   'The URL Location'
    'my fancy prop':  'My Fancy Prop'
  }

  for propName, expected of naturalNamesData
    it "can convert '#{propName}' to natural name '#{expected}'", inject (names) ->
      expect(names.getNaturalName).toBeFunction()
      expect(names.getNaturalName(propName)).toBe(expected)


  propertyNamesData = {
    'com.modelacatalogue.core.CatalogueElement' : 'catalogueElement'
    'catalogueElement'                          : 'catalogueElement'
    'catalogueElement_bla_bla_bla'              : 'catalogueElement'
  }


  for type, expectedPropertyName of propertyNamesData
    it "can convert '#{type}' to property name '#{expectedPropertyName}'", inject (names) ->
      expect(names.getPropertyNameFromType).toBeFunction()
      expect(names.getPropertyNameFromType(type)).toBe(expectedPropertyName)

  propertyQualifierData = {
      'com.modelacatalogue.core.CatalogueElement.history' : 'history'
      'history'                                           : 'history'
    }

  for qualifier, expectedProperty of propertyQualifierData
    it "can convert qualifier '#{qualifier}' to property '#{expectedProperty}'", inject (names) ->
      expect(names.getPropertyNameFromQualifier).toBeFunction()
      expect(names.getPropertyNameFromQualifier(qualifier)).toBe(expectedProperty)