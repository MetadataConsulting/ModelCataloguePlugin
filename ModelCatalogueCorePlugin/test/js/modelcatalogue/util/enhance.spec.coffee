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

  beforeEach inject (_enhance_) ->
    enhance = _enhance_

  it "does not enhance if condition is not met", ->
    result = enhance({hello: "world"})

    expect(result).toBeDefined()
    expect(result.hello).toBeDefined()
    expect(result.hello).toBe("world")
    expect(result.enhanced).toBeUndefined()



  it "enhances returned data on success when enhancer condition met", ->
    result  = enhance hello: "World", world: "Hello"

    expect(result).toBeDefined()
    expect(result.hello).toBe("World")
    expect(result.world).toBe("Hello")
    expect(result.enhanced).toBeTruthy()

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








