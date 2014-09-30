describe "mc.core.catalogueElementEnhancer", ->

  beforeEach module 'mc.core.ui.states'
  beforeEach module 'mc.core.catalogueElementEnhancer'
  beforeEach module 'mc.core.ui.bs.catalogue'

  xit "changes the state on show() method", inject (enhance, $rootScope, $httpBackend, modelCatalogueApiRoot) ->
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
    expect(angular.equals(toParams, {resource: 'valueDomain', id: "#{enhanced.id}"})).toBeTruthy()
    expect(self).toEqual(enhanced)

  it "returns expected results for getLabel()", inject (enhance) ->
    domain  = enhance angular.copy(fixtures.valueDomain.showOne)
    model   = enhance angular.copy(fixtures.model.showOne)
    element = enhance angular.copy(fixtures.dataElement.showOne)

    expect(domain.getLabel()) .toBe("school subject (cdtest1)")
    expect(model.getLabel())  .toBe("mTest3")
    expect(element.getLabel()).toBe("DE_author")

  it "returns expected results for instance of", inject (enhance) ->
    domain  = enhance angular.copy(fixtures.valueDomain.showOne)
    model   = enhance angular.copy(fixtures.model.showOne)

    expect(domain.isInstanceOf).toBeFunction()
    expect(model.isInstanceOf).toBeFunction()

    describe "test value domain and model", ->
      it "test value domain is ValueDomain", ->
        expect(domain.isInstanceOf('org.modelcatalogue.core.ValueDomain')).toBeTruthy()
      it "test value domain is CatalogueElement", ->
        expect(domain.isInstanceOf('org.modelcatalogue.core.CatalogueElement')).toBeTruthy()
      it "test value domain isn't Model", ->
        expect(domain.isInstanceOf('org.modelcatalogue.core.Model')).toBeFalsy()

      it "test model isn't ValueDomain", ->
        expect(model.isInstanceOf('org.modelcatalogue.core.ValueDomain')).toBeFalsy()
      it "test model isn't MeasurementUnit", ->
        expect(model.isInstanceOf('org.modelcatalogue.core.MeasurementUnit')).toBeFalsy()
      it "test model is CatalogueElement", ->
        expect(model.isInstanceOf('org.modelcatalogue.core.CatalogueElement')).toBeTruthy()
      it "test model is Model", ->
        expect(model.isInstanceOf('org.modelcatalogue.core.Model')).toBeTruthy()
      it "test model is ExtendibleElement", ->
        expect(model.isInstanceOf('org.modelcatalogue.core.ExtendibleElement')).toBeTruthy()
      it "test model is PublishedElement", ->
        expect(model.isInstanceOf('org.modelcatalogue.core.PublishedElement')).toBeTruthy()


