describe "mc.core.listReferenceDecorator", ->

  rest                  = null
  enhance               = null
  $httpBackend          = null
  $rootScope            = null
  modelCatalogueApiRoot = null

  beforeEach module "mc.core.listReferenceDecorator"

  beforeEach inject (_rest_, _enhance_, _$httpBackend_, _$rootScope_, _modelCatalogueApiRoot_) ->
    rest                  = _rest_
    $httpBackend          = _$httpBackend_
    $rootScope            = _$rootScope_
    modelCatalogueApiRoot = _modelCatalogueApiRoot_
    enhance               = _enhance_

  it "can deep enhance", ->
    enhanced = enhance angular.copy(fixtures.valueDomain.showOne)

    expect(angular.isFunction(enhanced.outgoingRelationships)).toBeTruthy()
    expect(angular.isFunction(enhanced.incomingRelationships)).toBeTruthy()
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


