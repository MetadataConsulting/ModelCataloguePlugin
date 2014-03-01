describe "mc.util.rest", ->

  beforeEach module "mc.util.rest"
  beforeEach module( (restProvider) ->
    condition = (input) ->
      input.world?
    factory   = () ->
      (input) ->
        input.enhanced = true
        input
    restProvider.registerEnhancerFactory('greeter', condition, factory)
    return
  )

  rest          = null
  $httpBackend  = null

  beforeEach inject (_rest_, _$httpBackend_) ->
    rest = _rest_
    $httpBackend = _$httpBackend_

  it "returns data on success", ->
    value   = {hello: "World"}
    result  = null
    $httpBackend.when("GET", "/foobar").respond(value)

    promise = rest (
      method: "GET"
      url:    "/foobar"
    )

    expect(promise).toBeDefined()
    expect(promise.then).toBeDefined()

    promise.then((_result_) ->
      result = _result_
    )

    expect(result).toBeNull()

    $httpBackend.flush()

    expect(angular.equals(result, value)).toBeTruthy()

  it "enhances returned data on success when enhancer condition met", ->
    value   = {hello: "World", world: "Hello"}
    result  = null
    $httpBackend.when("GET", "/foobar").respond(value)

    promise = rest({ method: "GET", url: "/foobar"})

    expect(promise).toBeDefined()
    expect(promise.then).toBeDefined()

    promise.then((_result_) ->
      result = _result_
    )

    expect(result).toBeNull()

    $httpBackend.flush()

    expect(result.hello).toBe("World")
    expect(result.world).toBe("Hello")
    expect(result.enhanced).toBeTruthy()

  it "returns status if no data and status is 2xx", ->
    result  = null
    $httpBackend.when("GET", "/foobar").respond(204)

    promise = rest({ method: "GET", url: "/foobar"})

    expect(promise).toBeDefined()
    expect(promise.then).toBeDefined()

    promise.then((_result_) ->
      result = _result_
    )

    expect(result).toBeNull()

    $httpBackend.flush()

    expect(result).toBe(204)


  it "returns whole response on error", ->
    result  = null
    error   = null
    $httpBackend.when("GET", "/foobar").respond(404, {success: false})

    promise = rest({ method: "GET", url: "/foobar"})

    expect(promise).toBeDefined()
    expect(promise.then).toBeDefined()

    promise.then(
      (_result_) ->
        result = _result_
      , (_error_) ->
        error = _error_
    )

    expect(result).toBeNull()
    expect(error).toBeNull()

    $httpBackend.flush()

    expect(result).toBe(null)
    expect(error).toBeDefined()
    expect(error.status).toBe(404)
    expect(angular.equals(error.data, {success: false})).toBeTruthy()







