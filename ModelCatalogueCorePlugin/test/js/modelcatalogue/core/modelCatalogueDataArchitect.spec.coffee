describe "mc.core.modelCatalogueDataArchitect", ->

  beforeEach module "mc.core.modelCatalogueDataArchitect"

  it "can find the uninstantiated data elements", inject ($httpBackend, modelCatalogueDataArchitect, modelCatalogueApiRoot) ->
    $httpBackend
    .when("GET", "#{modelCatalogueApiRoot}/dataArchitect/uninstantiatedDataElements")
    .respond(fixtures.dataArchitect.metadata_uninstantiated)

    result = null
    error  = null

    modelCatalogueDataArchitect.uninstantiatedDataElements().then( (_result_) ->
      result = _result_
    , (_error_) ->
      error = _error_
    )

    expect(result).toBeNull()

    $httpBackend.flush()

    expect(result).toBeDefined()
    expect(result.total).toBe(8)
    expect(result.page).toBe(10)
    expect(result.size).toBe(8)
    expect(result.offset).toBe(0)
    expect(result.list).toBeDefined()
    expect(result.list.length).toBe(8)

  it "metadataKeyCheck with params", inject ($httpBackend, modelCatalogueDataArchitect, modelCatalogueApiRoot) ->
    $httpBackend
    .when("GET", "#{modelCatalogueApiRoot}/dataArchitect/metadataKeyCheck?key=metadata")
    .respond(fixtures.dataArchitect.metadataKey_missing_key_metadata)

    result = null
    error  = null

    modelCatalogueDataArchitect.metadataKeyCheck('metadata', {}).then( (_result_) ->
      result = _result_
    , (_error_) ->
      error = _error_
    )

    expect(result).toBeNull()

    $httpBackend.flush()

    expect(result).toBeDefined()
#    expect(result.total).toBe(11)
    expect(result.page).toBe(10)
    expect(result.size).toBe(10)
    expect(result.offset).toBe(0)
    expect(result.list).toBeDefined()
    expect(result.list.length).toBe(10)
