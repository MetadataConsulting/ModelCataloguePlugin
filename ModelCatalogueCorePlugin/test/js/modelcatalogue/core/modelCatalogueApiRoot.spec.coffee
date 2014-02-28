describe 'mc.core.modelCatalogueApiRoot', ->

  beforeEach(module('mc.core.modelCatalogueApiRoot'))


  it 'should provide a api root', inject (modelCatalogueApiRoot) ->
    expect(modelCatalogueApiRoot).toEqual('/api/modelCatalogue/core')

  it 'should override a version and test the new version is injected', ->
    module ($provide) ->
      $provide.value('modelCatalogueApiRoot', '/context/api/modelCatalogue/core')
      # sometimes are CoffeeScript automatic returns pretty nasty :-(
      return

    inject (modelCatalogueApiRoot) ->
      expect(modelCatalogueApiRoot).toEqual('/context/api/modelCatalogue/core')