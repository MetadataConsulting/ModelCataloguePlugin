describe "modelcatalogue.core.enhancersConf.orderedMapEnhancer", ->

  beforeEach module "modelcatalogue.core.enhancersConf.orderedMapEnhancer"

  it "can easy get the values by key", inject (enhance) ->
    map =
      type: 'orderedMap'
      values: [
        {key: 'name', value: 'Frodo'}
        {key: 'race', value: 'Hobit'}
      ]


    enhanced = enhance map

    expect(enhance.isEnhancedBy(enhanced, 'orderedMap')).toBeTruthy()
    expect(enhanced.get).toBeFunction()
    expect(enhanced.get('name')).toEqual('Frodo')
    expect(enhanced.map).toBeUndefined()

  it "can create empty ordered map", inject (enhance) ->
    emptyMap = enhance.getEnhancer('orderedMap').emptyOrderedMap()

    expect(emptyMap).toBeObject()
    expect(emptyMap.type).toEqual('orderedMap')
    expect(emptyMap.values).toBeArray()
    expect(emptyMap.values.length).toBe(1)
    expect(emptyMap.values[0].key).toEqual('')
    expect(emptyMap.values[0].value).toBeUndefined()

