describe "mc.core.modelCatalogueSearch", ->

  beforeEach module "mc.core.modelCatalogueSearch"

  it "can search globally", inject ($httpBackend, modelCatalogueSearch, modelCatalogueApiRoot) ->
    $httpBackend
    .when("GET", "#{modelCatalogueApiRoot}/search?search=foo")
    .respond(fixtures.search.list1)

    result = null
    error  = null

    modelCatalogueSearch('foo').then( (_result_) ->
      result = _result_
    , (_error_) ->
      error = _error_
    )

    expect(result).toBeNull()

    $httpBackend.flush()

    expect(result).toBeDefined()
    expect(result.total).toBeGreaterThan(12)
    expect(result.page).toBe(10)
    expect(result.size).toBe(10)
    expect(result.offset).toBe(0)
    expect(result.list).toBeDefined()
    expect(result.list.length).toBe(10)

  it "search with params", inject ($httpBackend, modelCatalogueSearch, modelCatalogueApiRoot) ->
    $httpBackend
    .when("GET", "#{modelCatalogueApiRoot}/search?offset=10&search=foo")
    .respond(fixtures.search.list1)

    result = null
    error  = null

    modelCatalogueSearch('foo', {offset: 10}).then( (_result_) ->
      result = _result_
    , (_error_) ->
      error = _error_
    )

    expect(result).toBeNull()

    $httpBackend.flush()

    expect(result).toBeDefined()
    expect(result.total).toBeGreaterThan(12)
    expect(result.page).toBe(10)
    expect(result.size).toBe(10)
    expect(result.offset).toBe(0)
    expect(result.list).toBeDefined()
    expect(result.list.length).toBe(10)
