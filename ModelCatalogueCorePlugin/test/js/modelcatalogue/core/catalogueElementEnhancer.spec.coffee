describe "mc.core.catalogueElementEnhancer", ->

  beforeEach module 'mc.core.ui.states'
  beforeEach module 'mc.core.catalogueElementEnhancer'

  it "changes the state on show() method", inject (enhance, $rootScope, $httpBackend, modelCatalogueApiRoot) ->
    $httpBackend
    .when('GET', "#{modelCatalogueApiRoot}#{fixtures.valueDomain.showOne.link}")
    .respond(angular.copy(fixtures.valueDomain.showOne))

    enhanced = enhance angular.copy(fixtures.valueDomain.showOne)

    event    = null
    toState  = null
    toParams = null

    $rootScope.$on '$stateChangeSuccess', (_event_, _toState_, _toParams_) ->
      event     = _event_
      toState   = _toState_
      toParams  = _toParams_

    $rootScope.$digest()

    expect(enhance.isEnhancedBy(enhanced, 'catalogueElement')).toBeTruthy()
    expect(event).toBeNull()
    expect(toState).toBeNull()
    expect(toParams).toBeNull()

    expect(enhanced.show).toBeFunction()

    self = enhanced.show()

    $rootScope.$digest()
    $httpBackend.flush()

    expect(event).not.toBeNull()
    expect(toState).not.toBeNull()
    expect(toState.name).toEqual('mc.resource.show')
    expect(toParams).toEqual({resource: 'valueDomain', id: "#{enhanced.id}"})
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


