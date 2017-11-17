describe "mc.core.ui.expectCatalogueElement", ->
  beforeEach module 'mc.core.ui.expectCatalogueElement'
  beforeEach module 'modelcatalogue.core.enhancersConf.catalogueElementEnhancer'

  return unless window.fixtures

  it "doesn't do anything as long as the model is falsy", inject ($compile, $rootScope) ->

    $rootScope.classification = ''

    element = $compile('''
        <div class="form-group">
          <input type="text" ng-model="classification" expect-catalogue-element>
        </div>
      ''')($rootScope)

    $rootScope.$digest()

    expect(element.hasClass('has-feedback')).toBeFalsy()

  it "shows warning if the model is string", inject ($compile, $rootScope) ->

    $rootScope.classification = 'foobar'

    element = $compile('''
        <div class="form-group">
          <span class="input-group">
            <input type="text" ng-model="classification" expect-catalogue-element>
          </span>
        </div>
      ''')($rootScope)

    $rootScope.$digest()

    expect(element.hasClass('has-feedback')).toBeTruthy()
    expect(element.hasClass('has-warning')).toBeTruthy()

  it "shows success if the model is element", inject ($compile, $rootScope, enhance) ->
    $rootScope.classification = enhance angular.copy(fixtures.dataType.showOne)

    element = $compile('''
        <div class="form-group">
          <span class="input-group">
            <input type="text" ng-model="classification" expect-catalogue-element>
          </span>
        </div>
      ''')($rootScope)

    $rootScope.$digest()

    expect(element.hasClass('has-feedback')).toBeTruthy()
    expect(element.hasClass('has-success')).toBeTruthy()
