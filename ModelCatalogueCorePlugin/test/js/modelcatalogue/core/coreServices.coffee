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



  it "simply works", ->
    expect(true).toBeTruthy()