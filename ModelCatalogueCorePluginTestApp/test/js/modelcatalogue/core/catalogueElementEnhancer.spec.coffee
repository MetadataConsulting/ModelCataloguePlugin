describe "modelcatalogue.core.enhancersConf.catalogueElementEnhancer", ->

  beforeEach module 'mc.core.ui.states'
  beforeEach module 'modelcatalogue.core.enhancersConf.catalogueElementEnhancer'
  beforeEach module 'mc.core.ui.bs.catalogue'
  beforeEach module 'mc.util'

  xit "changes the state on show() method", inject (enhance, $rootScope, $httpBackend, modelCatalogueApiRoot) ->
    $httpBackend
    .when('GET', "#{modelCatalogueApiRoot}#{fixtures.dataType.showOne.link}")
    .respond(angular.copy(fixtures.dataType.showOne))

    enhanced = enhance angular.copy(fixtures.dataType.showOne)

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
    expect(angular.equals(toParams, {resource: 'dataType', id: "#{enhanced.id}"})).toBeTruthy()
    expect(self).toEqual(enhanced)

  if window.fixtures
    it "returns expected results for getLabel()", inject (enhance) ->
      dataClass   = enhance angular.copy(fixtures.dataClass.showOne)
      element = enhance angular.copy(fixtures.dataType.showOne)

      expect(dataClass.getLabel()).toBe("mTest3")
      expect(element.getLabel()).toBe("boolean")

    it "returns expected results for instance of", inject (enhance) ->
      dataType  = enhance angular.copy(fixtures.dataType.showOne)
      dataClass   = enhance angular.copy(fixtures.dataClass.showOne)

      expect(dataType.isInstanceOf).toBeFunction()
      expect(dataClass.isInstanceOf).toBeFunction()

      describe "test data type and model", ->
        it "test data type is DataType", ->
          expect(dataType.isInstanceOf('org.modelcatalogue.core.DataType')).toBeTruthy()
        it "test data type is CatalogueElement", ->
          expect(dataType.isInstanceOf('org.modelcatalogue.core.CatalogueElement')).toBeTruthy()
        it "test data type isn't Model", ->
          expect(dataType.isInstanceOf('org.modelcatalogue.core.Model')).toBeFalsy()

        it "test model isn't DataType", ->
          expect(dataClass.isInstanceOf('org.modelcatalogue.core.DataType')).toBeFalsy()
        it "test model isn't MeasurementUnit", ->
          expect(dataClass.isInstanceOf('org.modelcatalogue.core.MeasurementUnit')).toBeFalsy()
        it "test model is CatalogueElement", ->
          expect(dataClass.isInstanceOf('org.modelcatalogue.core.CatalogueElement')).toBeTruthy()
        it "test model is Model", ->
          expect(dataClass.isInstanceOf('org.modelcatalogue.core.DataClass')).toBeTruthy()


