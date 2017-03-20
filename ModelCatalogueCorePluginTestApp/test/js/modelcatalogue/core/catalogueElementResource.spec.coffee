describe "mc.core.catalogueElementResource", ->

  $httpBackend              = null
  $rootScope                = null
  catalogueElementResource  = null
  modelCatalogueApiRoot     = null


  beforeEach module "mc.core.catalogueElementResource"
  beforeEach module "mc.core.catalogueElementEnhancer"
  beforeEach module "mc.core.listReferenceEnhancer"
  beforeEach module "mc.core.promiseEnhancer"

  beforeEach module ($provide) ->
    $provide.value('objectVisitor', {visit: (value) -> value})
    return

  beforeEach inject (_catalogueElementResource_, _modelCatalogueApiRoot_, _$httpBackend_, _$rootScope_) ->
    catalogueElementResource  = _catalogueElementResource_
    modelCatalogueApiRoot     = _modelCatalogueApiRoot_
    $httpBackend              = _$httpBackend_
    $rootScope                = _$rootScope_


  afterEach ->
    $httpBackend.verifyNoOutstandingExpectation()
    $httpBackend.verifyNoOutstandingRequest()


  it "creates catalogue element resource", ->
    dataTypes = catalogueElementResource('com.modelcatalogue.core.DataType')
    dataTypesRootPath = "/api/modelCatalogue/core/dataType"

    expect(dataTypes.getIndexPath()).toBe(dataTypesRootPath)

    return unless window.fixtures

    describe "with methods for CRUD operations", ->

      describe "can get single resource", ->

        testElementId = fixtures.dataType.showOne.id

        it "will respond with expected resource if exists", ->
          $httpBackend
          .when("GET", "#{modelCatalogueApiRoot}/dataType/#{testElementId}")
          .respond(fixtures.dataType.showOne)


          result = null
          error  = null
          dataTypes.get(testElementId).then( (_result_) ->
            result = _result_
          , (_error_) ->
            error = _error_
          )

          expect(result).toBeNull()
          expect(error).toBeNull()

          $httpBackend.flush()


          expect(result).toBeDefined()
          expect(error).toBeNull()
          expect(result.id).toBe(testElementId)
          expect(result.name).toBe("boolean")
          expect(result.description).toBeDefined()


          describe "fetched instance is enhanced", ->
            it "outgoing and incoming relationships are functions", ->
              $httpBackend
              .when("GET", "#{modelCatalogueApiRoot}/dataType/#{testElementId}/outgoing/relatedTo")
              .respond(fixtures.dataType.incoming1)

              expect(angular.isFunction(result.isBaseFor)).toBeTruthy()
              expect(result.isBaseFor.total).toBe(0)

              incoming = null

              result.relatedTo().then((result) -> incoming = result)

              expect(incoming).toBeNull()

              $httpBackend.flush()

              expect(incoming).toBeDefined()


              $httpBackend
              .when("GET", "#{modelCatalogueApiRoot}/dataType/#{testElementId}/mapping")
              .respond(fixtures.dataType.mapping1)

              expect(angular.isFunction(result.mappings)).toBeTruthy()

              expect(result.mappings.total).toBe(0)

              mappings = null

              result.mappings().then((result) -> mappings = result)

              expect(mappings).toBeNull()

              $httpBackend.flush()

              expect(mappings).toBeDefined()

            it "has delete method", ->
              $httpBackend
              .when("DELETE", "#{modelCatalogueApiRoot}/dataType/#{testElementId}")
              .respond(204)

              expect(angular.isFunction(result.delete)).toBeTruthy()

              status = null

              result.delete().then((result) -> status = result)

              expect(status).toBeNull()

              $httpBackend.flush()

              expect(status).toBe(204)

            it "has update method", ->
              result.version = 1

              $httpBackend
              .when("PUT", "#{modelCatalogueApiRoot}/dataType/#{testElementId}" )
              .respond(fixtures.dataType.updateOk)

              expect(angular.isFunction(result.update)).toBeTruthy()

              updated = null

              result.update().then((result) -> updated = result)

              expect(updated).toBeNull()

              $httpBackend.flush()

              expect(updated).toBeDefined()

            it "has refresh method", ->

              $httpBackend
              .when("GET", "#{modelCatalogueApiRoot}/dataType/#{testElementId}" )
              .respond(fixtures.dataType.showOne)

              expect(angular.isFunction(result.refresh)).toBeTruthy()

              refreshed = null

              result.update().then((result) -> refreshed = result)

              expect(refreshed).toBeNull()

              $httpBackend.flush()

              expect(refreshed).toBeDefined()

            it "has validate method", ->
              result.version = 1

              $httpBackend
              .when("POST", "#{modelCatalogueApiRoot}/dataType/#{testElementId}/validate")
              .respond(fixtures.dataType.updateOk)

              expect(angular.isFunction(result.validate)).toBeTruthy()

              validationResult = null

              result.validate().then((result) -> validationResult = result)

              expect(validationResult).toBeNull()

              $httpBackend.flush()

              expect(validationResult).toBeDefined()

        xit "will respond with 404 if the resource does not exist", ->
          $httpBackend
          .when("GET", "#{modelCatalogueApiRoot}/dataType/10000000")
          .respond(404)

          result = null
          error  = null

          dataTypes.get(10000000).then( (_result_) ->
            result = _result_
          , (_error_) ->
            error = _error_
          )

          expect(result).toBeNull()
          expect(error).toBeNull()

          $httpBackend.flush()
#          $rootScope.$digest()


          expect(result).toBeNull()
          expect(error).toBeDefined()
          expect(error.status).toBe(404)


      describe "can delete resource", ->
        it "will delete existing resource and return 204", ->
          $httpBackend
          .when("DELETE", "#{modelCatalogueApiRoot}/dataType/1")
          .respond(204)

          result = null
          error  = null
          ok     = null

          fromEvent = null

          $rootScope.$on "catalogueElementDeleted", (event, element)->
            fromEvent = element

          dataTypes.delete(1).then( (_result_) ->
            result = _result_
            ok     = true
          , (_error_) ->
            ok     = false
            error  = _error_
          )

          expect(result).toBeNull()
          expect(error).toBeNull()
          expect(fromEvent).toBeNull()

          $httpBackend.flush()
          $rootScope.$digest()

          expect(ok).toBeTruthy()
          expect(error).toBeNull()
          expect(result).toBe(204)
          expect(fromEvent).not.toBeNull()
          expect(fromEvent.link).toBe('/dataType/1')

        xit "will return 404 if the resource does not exist", ->
          $httpBackend
          .when("DELETE", "#{modelCatalogueApiRoot}/dataType/1000000")
          .respond(404)

          result = null
          error  = null
          ok     = null

          dataTypes.delete(1000000).then( (_result_) ->
            result = _result_
            ok     = true
          , (_error_) ->
            ok     = false
            error  = _error_
          )

          expect(result).toBeNull()
          expect(error).toBeNull()

          $httpBackend.flush()

          expect(ok).toBeFalsy()
          expect(error).toBeDefined()
          expect(error.status).toBe(404)
          expect(result).toBeNull()

        it "will return 409 if constraints are violated", ->
          $httpBackend
          .when("DELETE", "#{modelCatalogueApiRoot}/dataType/2")
          .respond(409)

          result = null
          error  = null
          ok     = null

          dataTypes.delete(2).then( (_result_) ->
            result = _result_
            ok     = true
          , (_error_) ->
            ok     = false
            error  = _error_
          )

          expect(result).toBeNull()
          expect(error).toBeNull()

          $httpBackend.flush()

          expect(ok).toBeFalsy()
          expect(error).toBeDefined()
          expect(error.status).toBe(409)
          expect(result).toBeNull()

      describe "can update entity", ->
        it "updates ok if entity exists", ->
          payloadWithId = angular.extend({}, fixtures.dataType.updateInput)
          payloadWithId.id = fixtures.dataType.showOne.id

          $httpBackend
          .when("PUT", "#{modelCatalogueApiRoot}/dataType/#{payloadWithId.id}?format=json", fixtures.dataType.updateInput)
          .respond(fixtures.dataType.updateOk)

          result = null
          error  = null

          dataTypes.update(payloadWithId).then( (_result_) ->
            result = _result_
          , (_error_) ->
            error  = _error_
          )

          expect(result).toBeNull()
          expect(error).toBeNull()

          $httpBackend.flush()

          expect(error).toBeNull()
          expect(result).toBeDefined()
          expect(result.id).toBe(payloadWithId.id)
          expect(result.name).toBe(fixtures.dataType.updateOk.name)
          expect(result.update).toBeDefined()

        it "validates ok if entity exists", ->
          $httpBackend
          .when("POST", "#{modelCatalogueApiRoot}/dataType/validate", fixtures.dataType.updateInput)
          .respond(fixtures.dataType.updateOk)

          result = null
          error  = null

          dataTypes.validate(fixtures.dataType.updateInput).then( (_result_) ->
            result = _result_
          , (_error_) ->
            error  = _error_
          )

          expect(result).toBeNull()
          expect(error).toBeNull()

          $httpBackend.flush()

          expect(error).toBeNull()
          expect(result).toBeDefined()
          expect(result.name).toBe(fixtures.dataType.updateOk.name)
          expect(result.update).toBeDefined()

        xit "returns 404 if the resource does not exist", ->
          payloadWithId = angular.extend({}, fixtures.dataType.updateInput)
          payloadWithId.id = 1000000

          $httpBackend
          .when("PUT", "#{modelCatalogueApiRoot}/dataType/1000000?format=json", fixtures.dataType.updateInput)
          .respond(404)

          result = null
          error  = null

          dataTypes.update(payloadWithId).then( (_result_) ->
            result = _result_
          , (_error_) ->
            error  = _error_
          )

          expect(result).toBeNull()
          expect(error).toBeNull()

          $httpBackend.flush()

          expect(error).toBeDefined()
          expect(error.status).toBe(404)
          expect(result).toBeNull()

        it "updates failed if wrong input", ->
          payloadWithId = angular.extend({}, fixtures.dataType.updateInput)
          payloadWithId.id = 1
          payloadWithId.name = fixtures.dataType.updateErrors.errors[0]["rejected-value"]

          payloadWithoutId = angular.extend({}, payloadWithId)
          delete payloadWithoutId.id

          $httpBackend
          .when("PUT", "#{modelCatalogueApiRoot}/dataType/1?format=json", payloadWithoutId)
          .respond(fixtures.dataType.updateErrors)

          result = null
          error  = null

          dataTypes.update(payloadWithId).then( (_result_) ->
            result = _result_
          , (_error_) ->
            error  = _error_
          )

          expect(result).toBeNull()
          expect(error).toBeNull()

          $httpBackend.flush()

          expect(result).toBeNull()
          expect(error).toBeDefined()
          expect(error.data).toBeDefined()
          expect(error.data.errors).toBeDefined()
          expect(error.data.errors[0].field).toBe("name")
          expect(error.data.errors[0]["rejected-value"]).toBe(fixtures.dataType.updateErrors.errors[0]["rejected-value"])
          expect(error.data.errors[0].message).toBe(fixtures.dataType.updateErrors.errors[0].message)

      describe "can save new entity", ->
        it "saves if input data ok", ->
          payload = {
            name:    "Miles per hour"
            symbol:  "MPH"
          }

          resultFromEvent = null

          $rootScope.$on "catalogueElementCreated", (event, element) ->
            resultFromEvent = element


          $httpBackend
          .when("POST", "#{modelCatalogueApiRoot}/dataType", payload)
          .respond(fixtures.dataType.saveOk)

          result = null
          error  = null

          dataTypes.save(payload).then( (_result_) ->
            result = _result_
          , (_error_) ->
            error  = _error_
          )

          expect(result).toBeNull()
          expect(error).toBeNull()
          expect(resultFromEvent).toBeNull()

          $httpBackend.flush()
          $rootScope.$digest()

          expect(error).toBeNull()
          expect(result).toBeDefined()
          expect(result.id).toBe(fixtures.dataType.saveOk.id)
          expect(result.version).toBe(fixtures.dataType.saveOk.version)
          expect(result.name).toBe(fixtures.dataType.saveOk.name)
          expect(result.update).toBeDefined()

          expect(resultFromEvent).toEqual(result)

        it "save failed if wrong input", ->
          payload = {symbol: "MPH"}

          $httpBackend
          .when("POST", "#{modelCatalogueApiRoot}/dataType", payload)
          .respond(fixtures.dataType.saveErrors)

          result = null
          error  = null

          dataTypes.save(payload).then( (_result_) ->
            result = _result_
          , (_error_) ->
            error  = _error_
          )

          expect(result).toBeNull()
          expect(error).toBeNull()

          $httpBackend.flush()

          expect(result).toBeNull()
          expect(error).toBeDefined()
          expect(error.data).toBeDefined()
          expect(error.data.errors).toBeDefined()
          expect(error.data.errors[0].field).toBe("name")
          expect(error.data.errors[0]["rejected-value"]).toBe(fixtures.dataType.saveErrors.errors[0]["rejected-value"])
          expect(error.data.errors[0].message).toBe(fixtures.dataType.saveErrors.errors[0].message)

