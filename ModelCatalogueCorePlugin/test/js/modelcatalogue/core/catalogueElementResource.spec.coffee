describe "mc.core.catalogueElementResource", ->

  $httpBackend              = null
  $rootScope                = null
  catalogueElementResource  = null
  modelCatalogueApiRoot     = null

  beforeEach module "mc.core.catalogueElementResource"
  beforeEach module "mc.core.catalogueElementEnhancer"
  beforeEach module "mc.core.listReferenceEnhancer"

  beforeEach inject (_catalogueElementResource_, _modelCatalogueApiRoot_, _$httpBackend_, _$rootScope_) ->
    catalogueElementResource  = _catalogueElementResource_
    modelCatalogueApiRoot     = _modelCatalogueApiRoot_
    $httpBackend              = _$httpBackend_
    $rootScope                = _$rootScope_


  afterEach ->
    $httpBackend.verifyNoOutstandingExpectation()
    $httpBackend.verifyNoOutstandingRequest()


  it "creates catalogue element resource", ->
    valueDomains = catalogueElementResource('valueDomain')
    valueDomainsRootPath = "/api/modelCatalogue/core/valueDomain"

    expect(valueDomains.getIndexPath()).toBe(valueDomainsRootPath)

    describe "with methods for CRUD operations", ->

      describe "can get single resource", ->
        it "will respond with expected resource if exists", ->
          $httpBackend
          .when("GET", "#{modelCatalogueApiRoot}/valueDomain/1")
          .respond(fixtures.valueDomain.showOne)

          result = null
          error  = null
          valueDomains.get(1).then( (_result_) ->
            result = _result_
          , (_error_) ->
            error = _error_
          )

          expect(result).toBeNull()
          expect(error).toBeNull()

          $httpBackend.flush()


          expect(result).toBeDefined()
          expect(error).toBeNull()
          expect(result.id).toBe(1)
          expect(result.name).toBe("ground_speed")
          expect(result.description).toBeDefined()
          expect(result.version).toBe(0)


          describe "fetched instance is enhanced", ->
            it "outgoing and incoming relationships are functions", ->
              $httpBackend
              .when("GET", "#{modelCatalogueApiRoot}/valueDomain/1/incoming")
              .respond(fixtures.valueDomain.incoming1)

              expect(angular.isFunction(result.incomingRelationships)).toBeTruthy()
              expect(result.incomingRelationships.total).toBe(0)

              incoming = null

              result.incomingRelationships().then((result) -> incoming = result)

              expect(incoming).toBeNull()

              $httpBackend.flush()

              expect(incoming).toBeDefined()


              $httpBackend
              .when("GET", "#{modelCatalogueApiRoot}/valueDomain/1/outgoing")
              .respond(fixtures.valueDomain.outgoing1)

              expect(angular.isFunction(result.outgoingRelationships)).toBeTruthy()

              expect(result.outgoingRelationships.total).toBe(1)

              outgoing = null

              result.outgoingRelationships().then((result) -> outgoing = result)

              expect(outgoing).toBeNull()

              $httpBackend.flush()

              expect(outgoing).toBeDefined()


              $httpBackend
              .when("GET", "#{modelCatalogueApiRoot}/valueDomain/1/mapping")
              .respond(fixtures.valueDomain.mapping1)

              expect(angular.isFunction(result.mappings)).toBeTruthy()

              expect(result.mappings.total).toBe(0)

              mappings = null

              result.mappings().then((result) -> mappings = result)

              expect(mappings).toBeNull()

              $httpBackend.flush()

              expect(mappings).toBeDefined()

            it "has delete method", ->
              $httpBackend
              .when("DELETE", "#{modelCatalogueApiRoot}/valueDomain/1")
              .respond(204)

              expect(angular.isFunction(result.delete)).toBeTruthy()

              status = null

              result.delete().then((result) -> status = result)

              expect(status).toBeNull()

              $httpBackend.flush()

              expect(status).toBe(204)

            it "has update method", ->
              expectedPayload = {"dataType":{"id":1},"description":"the ground speed of the moving vehicle","name":"ground_speed","regexDef":"[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?","unitOfMeasure":{"id":1},"version":1}

              result.version = 1

              $httpBackend
              .when("PUT", "#{modelCatalogueApiRoot}/valueDomain/1", expectedPayload)
              .respond(fixtures.valueDomain.updateOk)

              expect(angular.isFunction(result.update)).toBeTruthy()

              updated = null

              result.update().then((result) -> updated = result)

              expect(updated).toBeNull()

              $httpBackend.flush()

              expect(updated).toBeDefined()

            it "has validate method", ->
              expectedPayload = {"dataType":{"id":1},"description":"the ground speed of the moving vehicle","name":"ground_speed","regexDef":"[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?","unitOfMeasure":{"id":1},"version":1}

              result.version = 1

              $httpBackend
              .when("POST", "#{modelCatalogueApiRoot}/valueDomain/1/validate", expectedPayload)
              .respond(fixtures.valueDomain.updateOk)

              expect(angular.isFunction(result.validate)).toBeTruthy()

              validationResult = null

              result.validate().then((result) -> validationResult = result)

              expect(validationResult).toBeNull()

              $httpBackend.flush()

              expect(validationResult).toBeDefined()

        it "will respond with 404 if the resource does not exist", ->
          $httpBackend
          .when("GET", "#{modelCatalogueApiRoot}/valueDomain/10000000")
          .respond(404)

          result = null
          error  = null

          valueDomains.get(10000000).then( (_result_) ->
            result = _result_
          , (_error_) ->
            error = _error_
          )

          expect(result).toBeNull()
          expect(error).toBeNull()

          $httpBackend.flush()


          expect(result).toBeNull()
          expect(error).toBeDefined()
          expect(error.status).toBe(404)


      describe "can delete resource", ->
        it "will delete existing resource and return 204", ->
          $httpBackend
          .when("DELETE", "#{modelCatalogueApiRoot}/valueDomain/1")
          .respond(204)

          result = null
          error  = null
          ok     = null

          valueDomains.delete(1).then( (_result_) ->
            result = _result_
            ok     = true
          , (_error_) ->
            ok     = false
            error  = _error_
          )

          expect(result).toBeNull()
          expect(error).toBeNull()

          $httpBackend.flush()

          expect(ok).toBeTruthy()
          expect(error).toBeNull()
          expect(result).toBe(204)

        it "will return 404 if the resource does not exist", ->
          $httpBackend
          .when("DELETE", "#{modelCatalogueApiRoot}/valueDomain/1000000")
          .respond(404)

          result = null
          error  = null
          ok     = null

          valueDomains.delete(1000000).then( (_result_) ->
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
          .when("DELETE", "#{modelCatalogueApiRoot}/valueDomain/2")
          .respond(409)

          result = null
          error  = null
          ok     = null

          valueDomains.delete(2).then( (_result_) ->
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
          payloadWithId = angular.extend({}, fixtures.valueDomain.updateInput)
          payloadWithId.id = 1

          $httpBackend
          .when("PUT", "#{modelCatalogueApiRoot}/valueDomain/1", fixtures.valueDomain.updateInput)
          .respond(fixtures.valueDomain.updateOk)

          result = null
          error  = null

          valueDomains.update(payloadWithId).then( (_result_) ->
            result = _result_
          , (_error_) ->
            error  = _error_
          )

          expect(result).toBeNull()
          expect(error).toBeNull()

          $httpBackend.flush()

          expect(error).toBeNull()
          expect(result).toBeDefined()
          expect(result.id).toBe(1)
          expect(result.version).toBe(1)
          expect(result.name).toBe(fixtures.valueDomain.updateOk.name)
          expect(result.update).toBeDefined()

        it "validates ok if entity exists", ->
          $httpBackend
          .when("POST", "#{modelCatalogueApiRoot}/valueDomain/validate", fixtures.valueDomain.updateInput)
          .respond(fixtures.valueDomain.updateOk)

          result = null
          error  = null

          valueDomains.validate(fixtures.valueDomain.updateInput).then( (_result_) ->
            result = _result_
          , (_error_) ->
            error  = _error_
          )

          expect(result).toBeNull()
          expect(error).toBeNull()

          $httpBackend.flush()

          expect(error).toBeNull()
          expect(result).toBeDefined()
          expect(result.id).toBe(1)
          expect(result.version).toBe(1)
          expect(result.name).toBe(fixtures.valueDomain.updateOk.name)
          expect(result.update).toBeDefined()

        it "returns 404 if the resource does not exist", ->
          payloadWithId = angular.extend({}, fixtures.valueDomain.updateInput)
          payloadWithId.id = 1000000

          $httpBackend
          .when("PUT", "#{modelCatalogueApiRoot}/valueDomain/1000000", fixtures.valueDomain.updateInput)
          .respond(404)

          result = null
          error  = null

          valueDomains.update(payloadWithId).then( (_result_) ->
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
          payloadWithId = angular.extend({}, fixtures.valueDomain.updateInput)
          payloadWithId.id = 1
          payloadWithId.name = fixtures.valueDomain.updateErrors.errors[0]["rejected-value"]

          payloadWithoutId = angular.extend({}, payloadWithId)
          delete payloadWithoutId.id

          $httpBackend
          .when("PUT", "#{modelCatalogueApiRoot}/valueDomain/1", payloadWithoutId)
          .respond(fixtures.valueDomain.updateErrors)

          result = null
          error  = null

          valueDomains.update(payloadWithId).then( (_result_) ->
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
          expect(error.data.errors[0]["rejected-value"]).toBe(fixtures.valueDomain.updateErrors.errors[0]["rejected-value"])
          expect(error.data.errors[0].message).toBe(fixtures.valueDomain.updateErrors.errors[0].message)

      describe "can save new entity", ->
        it "saves if input data ok", ->
          payload = {
            name:    "Miles per hour"
            symbol:  "MPH"
          }

          $httpBackend
          .when("POST", "#{modelCatalogueApiRoot}/valueDomain", payload)
          .respond(fixtures.valueDomain.saveOk)

          result = null
          error  = null

          valueDomains.save(payload).then( (_result_) ->
            result = _result_
          , (_error_) ->
            error  = _error_
          )

          expect(result).toBeNull()
          expect(error).toBeNull()

          $httpBackend.flush()

          expect(error).toBeNull()
          expect(result).toBeDefined()
          expect(result.id).toBe(fixtures.valueDomain.saveOk.id)
          expect(result.version).toBe(fixtures.valueDomain.saveOk.version)
          expect(result.name).toBe(fixtures.valueDomain.saveOk.name)
          expect(result.update).toBeDefined()

        it "save failed if wrong input", ->
          payload = {symbol: "MPH"}

          $httpBackend
          .when("POST", "#{modelCatalogueApiRoot}/valueDomain", payload)
          .respond(fixtures.valueDomain.saveErrors)

          result = null
          error  = null

          valueDomains.save(payload).then( (_result_) ->
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
          expect(error.data.errors[0]["rejected-value"]).toBe(fixtures.valueDomain.saveErrors.errors[0]["rejected-value"])
          expect(error.data.errors[0].message).toBe(fixtures.valueDomain.saveErrors.errors[0].message)

    describe "can search resource", ->
      it "works", ->
        $httpBackend
        .when("GET", "#{modelCatalogueApiRoot}/valueDomain/search?search=foo")
        .respond(fixtures.valueDomain.searchElement15)

        result = null
        error  = null

        valueDomains.search('foo').then( (_result_) ->
          result = _result_
        , (_error_) ->
          error = _error_
        )

        expect(result).toBeNull()

        $httpBackend.flush()

        expect(result).toBeDefined()
        expect(result.total).toBe(4)
        expect(result.page).toBe(10)
        expect(result.size).toBe(4)
        expect(result.offset).toBe(0)
        expect(result.list).toBeDefined()
        expect(result.list.length).toBe(4)

