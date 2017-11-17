describe "mc.core.removableItemEnancer", ->

  enhance               = null
  $httpBackend          = null
  modelCatalogueApiRoot = null

  beforeEach module "modelcatalogue.core.enhancersConf.removableItemEnhancer"

  beforeEach inject ( _$httpBackend_,  _modelCatalogueApiRoot_, _enhance_) ->
    $httpBackend          = _$httpBackend_
    modelCatalogueApiRoot = _modelCatalogueApiRoot_
    enhance               = _enhance_


  if window.fixtures
    it "remove method will be added if removeLink is present", ->
      list = angular.copy(fixtures.dataType.outgoing1)
      enhanced = enhance list

      item = enhanced.list[0]

      expect(angular.isFunction(item.remove)).toBeTruthy()

      $httpBackend.expect("DELETE", "#{modelCatalogueApiRoot}#{item.removeLink}").respond(204)

      result = null

      item.remove().then (_result_) ->
        result = _result_

      expect(result).toBeNull()

      $httpBackend.flush()

      expect(result).toBe(204)

