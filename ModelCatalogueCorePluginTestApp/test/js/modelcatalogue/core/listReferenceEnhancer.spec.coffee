describe "modelcatalogue.core.enhancersConf.listReferenceEnhancer", ->

  rest                  = null
  enhance               = null
  $httpBackend          = null
  $rootScope            = null
  modelCatalogueApiRoot = null

  beforeEach module "modelcatalogue.core.enhancersConf.listReferenceEnhancer"

  beforeEach inject (_rest_, _enhance_, _$httpBackend_, _$rootScope_, _modelCatalogueApiRoot_) ->
    rest                  = _rest_
    $httpBackend          = _$httpBackend_
    $rootScope            = _$rootScope_
    modelCatalogueApiRoot = _modelCatalogueApiRoot_
    enhance               = _enhance_

  if window.fixtures
    it "can deep enhance", ->
      enhanced = enhance angular.copy(fixtures.dataType.showOne)

      expect(angular.isFunction(enhanced.mappings)).toBeTruthy()

  it "returns function which will call the link", ->
    expected = {foo:  'bar'}


    enhanced = enhance {count: 1, link: "/foo/bar"}

    expect(angular.isFunction(enhanced)).toBeTruthy()

    $httpBackend.expect("GET", "#{modelCatalogueApiRoot}/foo/bar").respond(expected)

    result = null

    enhanced().then (_result_) -> result = _result_

    expect(result).toBeNull()

    $httpBackend.flush()

    expect(result).toEqual(expected)


  it "you can supply url tail to returned method", ->
    expected = {foo:  'bar'}


    enhanced = enhance {count: 1, link: "/foo/bar"}

    expect(angular.isFunction(enhanced)).toBeTruthy()

    $httpBackend.expect("GET", "#{modelCatalogueApiRoot}/foo/bar/barbar").respond(expected)

    result = null

    enhanced('barbar').then (_result_) -> result = _result_

    expect(result).toBeNull()

    $httpBackend.flush()

    expect(result).toEqual(expected)


  it "you can add to given list", ->
    expected = {foo:  'bar'}
    payload  = {bar: 'foo'}
    expectedResult = { id: 1, source: angular.copy(expected), destination: angular.copy(payload), type: 'barbar' }
    enhanced = enhance {count: 1, link: "/foo/bar"}

    expect(angular.isFunction(enhanced.add)).toBeTruthy()

    $httpBackend.expect("POST", "#{modelCatalogueApiRoot}/foo/bar", payload).respond(expectedResult)

    result = null

    enhanced.add(payload).then (_result_) -> result = _result_

    expect(result).toBeNull()

    $httpBackend.flush()

    expect(result).toEqual(expectedResult)


  it "you can add to given list with specified tail", ->
    expected = {foo:  'bar'}
    payload  = {bar: 'foo'}
    expectedResult = { id: 1, source: angular.copy(expected), destination: angular.copy(payload), type: 'barbar' }
    enhanced = enhance {count: 1, link: "/foo/bar"}

    expect(angular.isFunction(enhanced.add)).toBeTruthy()

    $httpBackend.expect("POST", "#{modelCatalogueApiRoot}/foo/bar/barbar", payload).respond(expectedResult)

    result = null

    enhanced.add('barbar', payload).then (_result_) -> result = _result_

    expect(result).toBeNull()

    $httpBackend.flush()

    expect(result).toEqual(expectedResult)



  it "you can remove to given list", ->
    payload  = {bar: 'foo'}
    enhanced = enhance {count: 1, link: "/foo/bar"}

    expect(angular.isFunction(enhanced.remove)).toBeTruthy()

    $httpBackend.expect("DELETE", "#{modelCatalogueApiRoot}/foo/bar", payload).respond(204)

    result = null

    enhanced.remove(payload).then (_result_) -> result = _result_

    expect(result).toBeNull()

    $httpBackend.flush()

    expect(result).toEqual(204)


  it "you can remove from given list with specified tail", ->
    payload  = {bar: 'foo'}
    enhanced = enhance {count: 1, link: "/foo/bar"}

    expect(angular.isFunction(enhanced.remove)).toBeTruthy()

    $httpBackend.expect("DELETE", "#{modelCatalogueApiRoot}/foo/bar/barbar", payload).respond(204)

    result = null

    enhanced.remove('barbar', payload).then (_result_) -> result = _result_

    expect(result).toBeNull()

    $httpBackend.flush()

    expect(result).toEqual(204)


