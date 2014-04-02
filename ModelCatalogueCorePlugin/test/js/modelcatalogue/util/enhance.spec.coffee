describe "mc.util.enhance", ->

  beforeEach module "mc.util.enhance"
  beforeEach module( (enhanceProvider) ->
    condition = (input) ->
      input.world?
    factory   = () ->
      (input) ->
        input.enhanced = true
        input
    enhanceProvider.registerEnhancerFactory('greeter', condition, factory)
    return
  )

  enhance = null
  $rootScope = null
  $q = null

  beforeEach inject (_enhance_, _$rootScope_, _$q_) ->
    enhance = _enhance_
    $rootScope = _$rootScope_
    $q = _$q_

  it "does not enhance if condition is not met", ->
    result = enhance({hello: "world"})

    expect(result).toBeDefined()
    expect(result.hello).toBeDefined()
    expect(result.hello).toBe("world")
    expect(result.enhanced).toBeUndefined()
    expect(enhance.isEnhanced(result)).toBeFalsy()



  it "enhances returned data on success when enhancer condition met", ->
    result  = enhance hello: "World", world: "Hello"

    expect(result).toBeDefined()
    expect(result.hello).toBe("World")
    expect(result.world).toBe("Hello")
    expect(result.enhanced).toBeTruthy()
    expect(result.__enhancedBy).toEqual(['greeter'])
    expect(enhance.isEnhancedBy(result, 'greeter')).toBeTruthy()
    expect(enhance.isEnhancedBy(result, 'meeter')).toBeFalsy()

  it "deep enhances returned lists on success when enhancer condition met", ->
    result  = enhance [{hello: "World", world: "Hello"}]

    expect(result).toBeDefined()
    expect(angular.isArray(result)).toBeTruthy()
    expect(result.length).toBe(1)
    expect(result[0].hello).toBe("World")
    expect(result[0].world).toBe("Hello")
    expect(result[0].enhanced).toBeTruthy()

  it "deep enhances returned objects on success when enhancer condition met", ->
    result  = enhance foo: {hello: "World", world: "Hello"}
    expect(result).toBeDefined()
    expect(angular.isObject(result)).toBeTruthy()
    expect(result.foo).toBeDefined()
    expect(result.foo.hello).toBe("World")
    expect(result.foo.world).toBe("Hello")
    expect(result.foo.enhanced).toBeTruthy()

  it "in case of promise enhances the final result", ->
    promise = $q.when(hello: "World", world: "Hello")
    result = null

    enhance(promise).then (_result_) ->
      result = _result_

    $rootScope.$apply()

    expect(result).toBeDefined()
    expect(result.hello).toBe("World")
    expect(result.world).toBe("Hello")
    expect(result.enhanced).toBeTruthy()


  it "can list available enhancer names", ->
    expect(enhance.getAvailableEnhancers).toBeFunction()

    enhancers = enhance.getAvailableEnhancers()

    expect(enhancers).toEqual(['greeter'])

    expect(enhance.getEnhancer).toBeFunction()

    greeter = enhance.getEnhancer('greeter')

    expect(greeter).toBeFunction()
    expect(greeter)


    result  = greeter hello: "World", world: "Hello"

    expect(result).toBeDefined()
    expect(result.hello).toBe("World")
    expect(result.world).toBe("Hello")
    expect(result.enhanced).toBeTruthy()

    nothing = enhance.getEnhancer('nothing')

    expect(nothing).toBeUndefined()







