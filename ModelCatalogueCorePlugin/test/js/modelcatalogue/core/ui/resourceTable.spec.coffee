describe "mc.core.ui.resourceTable", ->

  beforeEach module 'mc.core.catalogueElementResource'
  beforeEach module 'mc.core.modelCatalogueApiRoot'
  beforeEach module 'mc.core.ui.resourceTable'

  it "element get compiled",  inject ($compile, $rootScope, $httpBackend, modelCatalogueApiRoot) ->
    $httpBackend.when("GET", "#{modelCatalogueApiRoot}/measurementUnit").respond(fixtures.measurementUnit.list2)

    element = $compile('<table resource-table="measurementUnit"><thead><tr><th property="name"></th></tr></thead></table>')($rootScope)

    $rootScope.$digest()
    $httpBackend.flush()

    expect(element.find('tr').length).toBe(5)

