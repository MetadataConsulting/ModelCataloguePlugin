describe "mc.core.listDecorator", ->

  listDecorator         = null
  rest                  = null
  $httpBackend          = null
  $rootScope            = null
  modelCatalogueApiRoot = null

  beforeEach module "mc.core.listDecorator"

  beforeEach inject (_listDecorator_, _rest_, _$httpBackend_, _$rootScope_, _modelCatalogueApiRoot_) ->
    listDecorator         = _listDecorator_
    rest                  = _rest_
    $httpBackend          = _$httpBackend_
    $rootScope            = _$rootScope_
    modelCatalogueApiRoot = _modelCatalogueApiRoot_

  it "can list resource", ->
    $httpBackend
    .when("GET", '/foo')
    .respond(fixtures.measurementUnit.list1)

    result = null
    error  = null
    rest(method: 'GET', url: '/foo').then( (_result_) ->
      result = _result_
    , (_error_) ->
      error = _error_
    )

    expect(result).toBeNull()

    $httpBackend.flush()

    expect(result).toBeDefined()
    expect(result.total).toBe(12)
    expect(result.page).toBe(10)
    expect(result.size).toBe(10)
    expect(result.offset).toBe(0)
    expect(result.list).toBeDefined()
    expect(result.list.length).toBe(10)
    expect(angular.isFunction(result.next)).toBeTruthy()
    expect(result.next.size).toBe(2)
    expect(angular.isFunction(result.previous)).toBeTruthy()
    expect(result.previous.size).toBe(0)

    emptyResult = null

    result.previous().then((_result_) -> emptyResult = _result_)

    expect(emptyResult).toBeNull()

    $rootScope.$apply();

    expect(emptyResult).toBeDefined()
    expect(emptyResult.total).toBe(12)
    expect(emptyResult.page).toBe(10)
    expect(emptyResult.size).toBe(0)
    expect(emptyResult.offset).toBe(0)
    expect(emptyResult.list).toBeDefined()
    expect(emptyResult.list.length).toBe(0)
    expect(emptyResult.success).toBeFalsy()


    nextList = angular.copy(fixtures.measurementUnit.list1)
    nextList.size = 2
    nextList.list = nextList.list.slice(0, 2)
    nextList.offset = 10
    nextList.next = fixtures.measurementUnit.list1.next.replace("offset=10", "offset=20")
    nextList.previous = "/measurementUnit/?max=10&offset=0"

    $httpBackend
    .when("GET", "#{modelCatalogueApiRoot}/measurementUnit/?max=10&offset=10")
    .respond(nextList)

    nextResult = null
    nextError  = null

    result.next().then( (_result_) ->
      nextResult = _result_
    , (_error_) ->
      nextError = _error_
    )

    expect(nextResult).toBeNull()

    $httpBackend.flush()

    expect(nextResult).toBeDefined()
    expect(nextResult.total).toBe(12)
    expect(nextResult.page).toBe(10)
    expect(nextResult.size).toBe(2)
    expect(nextResult.offset).toBe(10)
    expect(nextResult.list).toBeDefined()
    expect(nextResult.list.length).toBe(2)
    expect(angular.isFunction(nextResult.next)).toBeTruthy()
    expect(angular.isFunction(nextResult.previous)).toBeTruthy()

    $httpBackend
    .when("GET", "#{modelCatalogueApiRoot}/measurementUnit/?max=10&offset=0")
    .respond(fixtures.measurementUnit.list1)

    result = null
    error  = null
    nextResult.previous().then( (_result_) ->
      result = _result_
    , (_error_) ->
      error = _error_
    )

    expect(result).toBeNull()

    $httpBackend.flush()

    expect(result).toBeDefined()
    expect(result.total).toBe(12)
    expect(result.page).toBe(10)
    expect(result.size).toBe(10)
    expect(result.offset).toBe(0)
    expect(result.list).toBeDefined()
    expect(result.list.length).toBe(10)