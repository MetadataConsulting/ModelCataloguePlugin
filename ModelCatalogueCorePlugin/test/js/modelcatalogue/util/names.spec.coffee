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
  }


  for type, expectedPropertyName of propertyNamesData
    it "can convert '#{type}' to property name '#{expectedPropertyName}'", inject (names) ->
      expect(names.getPropertyNameFromType).toBeFunction()
      expect(names.getPropertyNameFromType(type)).toBe(expectedPropertyName)