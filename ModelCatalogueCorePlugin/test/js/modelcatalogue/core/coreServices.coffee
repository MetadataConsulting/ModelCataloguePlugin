describe "Test core services", ->

  $httpBackend = null
  mcDataElementsService = null

  beforeEach module "mc.coreServices"

  beforeEach inject (_$httpBackend_, _mcDataElementsService_) ->
    $httpBackend = _$httpBackend_
    mcDataElementsService = _mcDataElementsService_

  it "calling the list action on data element controller", () ->
    $httpBackend
    .when("GET", "/dataElement/")
    .respond(fixtures.dataElementList)

    response = null

    mcDataElementsService.list().then (_response_) ->
      response = _response_

    expect(response).toBeNull()

    $httpBackend.flush()

    expect(angular.equals(response, fixtures.dataElementList)).toBeTruthy()

  it "calling the get action on data element controller", () ->
    $httpBackend
    .when("GET", "/dataElement/1")
    .respond(fixtures.dataElementGet)

    response = null

    mcDataElementsService.get(1).then (_response_) ->
      response = _response_

    expect(response).toBeNull()

    $httpBackend.flush()

    expect(angular.equals(response, fixtures.dataElementGet)).toBeTruthy()



  it "simply works", ->
    expect(true).toBeTruthy()