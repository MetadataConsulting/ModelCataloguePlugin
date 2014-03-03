describe "mc.util.createConstantPromise", ->
  beforeEach module "mc.util.createConstantPromise"

  factory    = null
  $rootScope = null

  beforeEach inject (createConstantPromise, _$rootScope_) ->
    $rootScope     = _$rootScope_
    factory = createConstantPromise

  it "creates function which creates other function which always resolve to given value", ->
    value     = "Hello World"
    result    = null

    expect(angular.isFunction(factory)).toBeTruthy()

    promised = factory(value)

    expect(angular.isFunction(promised)).toBeTruthy()

    promise = promised()

    expect(promise.then).toBeDefined()

    promise.then (_result_) ->
      result = _result_

    expect(result).toBeNull()

    $rootScope.$apply()

    expect(result).toBe(value)

