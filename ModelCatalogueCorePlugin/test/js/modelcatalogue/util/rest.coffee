describe "constant promise function factory", ->

  beforeEach module "mc.util.rest"

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

  it "enhances returned data on success when enhancer part of config", ->
    value   = {hello: "World"}
    result  = null
    $httpBackend.when("GET", "/foobar").respond(value)

    promise = rest({ method: "GET", url: "/foobar", enhancer: (_result_) -> _result_.world = "Hello" ; _result_})

    expect(promise).toBeDefined()
    expect(promise.then).toBeDefined()

    promise.then((_result_) ->
      result = _result_
    )

    expect(result).toBeNull()

    $httpBackend.flush()

    expect(result.hello).toBe("World")
    expect(result.world).toBe("Hello")

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







