describe 'mc.util.names', ->
  beforeEach module 'mc.util.names'

  it "can convert property name to natural name", inject (names) ->
    expect(names.getNaturalName).toBeFunction()

    data = {
      myFancyProperty:  'My Fancy Property'
      theURLLocation:   'The URL Location'
      'my fancy prop':  'My Fancy Prop'
    }

    for propName, expected of data
      expect(names.getNaturalName(propName)).toBe(expected)
