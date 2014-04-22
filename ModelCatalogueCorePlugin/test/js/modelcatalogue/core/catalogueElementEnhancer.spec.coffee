describe "mc.core.catalogueElementEnhancer", ->

  beforeEach module 'mc.core.catalogueElementEnhancer'
  beforeEach module 'mc.core.catalogueElementEnhancer'

  it "broadcasts the showCatalogueElement event on show() method", inject (enhance, $rootScope) ->
    enhanced = enhance angular.copy(fixtures.valueDomain.showOne)

    event    = null
    element  = null

    $rootScope.$on 'showCatalogueElement', (_event_, _element_) ->
      event   = _event_
      element = _element_

    $rootScope.$digest()

    expect(enhance.isEnhancedBy(enhanced, 'catalogueElement')).toBeTruthy()
    expect(event).toBeNull()
    expect(element).toBeNull()

    expect(enhanced.show).toBeFunction()

    self = enhanced.show()

    $rootScope.$digest()

    expect(event).not.toBeNull()
    expect(element).toEqual(enhanced)
    expect(self).toEqual(enhanced)

  it "returns expected results for instance of", inject (enhance) ->
    domain  = enhance angular.copy(fixtures.valueDomain.showOne)
    model   = enhance angular.copy(fixtures.model.showOne)

    expect(domain.isInstanceOf).toBeFunction()
    expect(model.isInstanceOf).toBeFunction()

    expect(domain.isInstanceOf('org.modelcatalogue.core.ValueDomain')).toBeTruthy()
    expect(domain.isInstanceOf('org.modelcatalogue.core.CatalogueElement')).toBeTruthy()
    expect(domain.isInstanceOf('org.modelcatalogue.core.Model')).toBeFalsy()

    expect(model.isInstanceOf('org.modelcatalogue.core.ValueDomain')).toBeFalsy()
    expect(model.isInstanceOf('org.modelcatalogue.core.MeasurementUnit')).toBeFalsy()
    expect(model.isInstanceOf('org.modelcatalogue.core.CatalogueElement')).toBeTruthy()
    expect(model.isInstanceOf('org.modelcatalogue.core.Model')).toBeTruthy()
    expect(model.isInstanceOf('org.modelcatalogue.core.ExtendibleElement')).toBeTruthy()
    expect(model.isInstanceOf('org.modelcatalogue.core.PublishedElement')).toBeTruthy()


