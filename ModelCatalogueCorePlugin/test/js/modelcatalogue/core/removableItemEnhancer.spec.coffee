describe "mc.core.removableItemEnancer", ->

  enhance               = null
  $httpBackend          = null
  modelCatalogueApiRoot = null

  beforeEach module "mc.core.removableItemEnhancer"

  beforeEach inject ( _$httpBackend_,  _modelCatalogueApiRoot_, _enhance_) ->
    $httpBackend          = _$httpBackend_
    modelCatalogueApiRoot = _modelCatalogueApiRoot_
    enhance               = _enhance_

  it "remove method will be added if removeLink is present", ->
    list = angular.copy(fixtures.valueDomain.outgoing1)
    enhanced = enhance list

    item = enhanced.list[0]

    console.log item

    expect(angular.isFunction(item.remove)).toBeTruthy()

    $httpBackend.expect("DELETE", "#{modelCatalogueApiRoot}#{item.removeLink}").respond(204)

    result = null

    item.remove().then (_result_) ->
      result = _result_

    expect(result).toBeNull()

    $httpBackend.flush()

    expect(result).toBe(204)

