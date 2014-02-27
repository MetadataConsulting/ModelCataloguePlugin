describe "Model Catalogue Provider", ->

  $httpBackend    = null
  $rootScope      = null
  modelCatalogue  = null

  beforeEach module "mc.modelCatalogue"

  beforeEach inject (_modelCatalogue_, _$httpBackend_, _$rootScope_) ->
    modelCatalogue = _modelCatalogue_
    $httpBackend   = _$httpBackend_
    $rootScope     = _$rootScope_

  afterEach ->
    $httpBackend.verifyNoOutstandingExpectation()
    $httpBackend.verifyNoOutstandingRequest()

  it "model catalogue has api root assigned", ->
    expect(modelCatalogue.getApiRoot).toBeDefined()
    expect(modelCatalogue.getApiRoot()).toBe("/api/modelCatalogue/core")


  it "creates catalogue element resource", ->
    expect(modelCatalogue.elements).toBeDefined()

    measurementUnits = modelCatalogue.elements('measurementUnit')
    measurementUnitsRootPath = "/api/modelCatalogue/core/measurementUnit"

    expect(measurementUnits.getIndexPath()).toBe(measurementUnitsRootPath)

    describe "with methods for CRUD operations", ->

      it "can list resource", ->
        $httpBackend
         .when("GET", measurementUnitsRootPath)
         .respond(fixtures.measurementUnit.list1)

        result = null
        error  = null
        measurementUnits.list().then( (_result_) ->
          result = _result_
        , (_error_) ->
          error = _error_
        )

        expect(result).toBeNull()

        $httpBackend.flush()

        expect(result).toBeDefined()
        expect(result.total).toBe(12)
        expect(result.page).toBe(10)
        expect(result.size).toBe(10)
        expect(result.offset).toBe(0)
        expect(result.list).toBeDefined()
        expect(result.list.length).toBe(10)
        expect(angular.isFunction(result.list[0].delete)).toBeTruthy() # elements are enhanced
        expect(angular.isFunction(result.list[0].update)).toBeTruthy() # elements are enhanced
        expect(angular.isFunction(result.next)).toBeTruthy()
        expect(result.next.size).toBe(2)
        expect(angular.isFunction(result.previous)).toBeTruthy()
        expect(result.previous.size).toBe(0)

        emptyResult = null

        result.previous().then((_result_) -> emptyResult = _result_)

        expect(emptyResult).toBeNull()

        $rootScope.$apply();

        expect(emptyResult).toBeDefined()
        expect(emptyResult.total).toBe(12)
        expect(emptyResult.page).toBe(10)
        expect(emptyResult.size).toBe(0)
        expect(emptyResult.offset).toBe(0)
        expect(emptyResult.list).toBeDefined()
        expect(emptyResult.list.length).toBe(0)
        expect(emptyResult.success).toBeFalsy()


        nextList = angular.copy(fixtures.measurementUnit.list1)
        nextList.size = 2
        nextList.list = nextList.list.slice(0, 2)
        nextList.offset = 10
        nextList.next = fixtures.measurementUnit.list1.next.replace("offset=10", "offset=20")
        nextList.previous = "/measurementUnit/?max=10&offset=0"

        $httpBackend
          .when("GET", "#{modelCatalogue.getApiRoot()}/measurementUnit/?offset=10")
          .respond(nextList)

        nextResult = null
        nextError  = null
        result.next().then( (_result_) ->
          nextResult = _result_
        , (_error_) ->
          nextError = _error_
        )

        expect(nextResult).toBeNull()

        $httpBackend.flush()

        expect(nextResult).toBeDefined()
        expect(nextResult.total).toBe(12)
        expect(nextResult.page).toBe(10)
        expect(nextResult.size).toBe(2)
        expect(nextResult.offset).toBe(10)
        expect(nextResult.list).toBeDefined()
        expect(nextResult.list.length).toBe(2)
        expect(angular.isFunction(nextResult.next)).toBeTruthy()
        expect(angular.isFunction(nextResult.previous)).toBeTruthy()

        $httpBackend
        .when("GET", "#{modelCatalogue.getApiRoot()}/measurementUnit/?max=10&offset=0")
        .respond(fixtures.measurementUnit.list1)

        result = null
        error  = null
        nextResult.previous().then( (_result_) ->
          result = _result_
        , (_error_) ->
          error = _error_
        )

        expect(result).toBeNull()

        $httpBackend.flush()

        expect(result).toBeDefined()
        expect(result.total).toBe(12)
        expect(result.page).toBe(10)
        expect(result.size).toBe(10)
        expect(result.offset).toBe(0)
        expect(result.list).toBeDefined()
        expect(result.list.length).toBe(10)

      describe "can get single resource", ->
        it "will respond with expected resource if exists", ->
          $httpBackend
          .when("GET", "#{modelCatalogue.getApiRoot()}/measurementUnit/1")
          .respond(fixtures.measurementUnit.showOne)

          result = null
          error  = null
          measurementUnits.get(1).then( (_result_) ->
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
          expect(result.name).toBe("Degrees of Celsius")
          expect(result.description).toBeDefined()
          expect(result.version).toBe(0)
          expect(result.symbol).toBe("°C")


          describe "fetched instance if enhanced", ->
            it "outgoing and incoming relationships are functions", ->
              $httpBackend
              .when("GET", "#{modelCatalogue.getApiRoot()}/measurementUnit/1/outgoing")
              .respond(fixtures.measurementUnit.outgoing1)
              $httpBackend
              .when("GET", "#{modelCatalogue.getApiRoot()}/measurementUnit/1/incoming")
              .respond(fixtures.measurementUnit.incoming1)

              expect(angular.isFunction(result.incomingRelationships)).toBeTruthy()
              expect(angular.isFunction(result.outgoingRelationships)).toBeTruthy()

              incoming = null
              outgoing = null

              result.incomingRelationships().then((result) -> incoming = result)
              result.outgoingRelationships().then((result) -> outgoing = result)

              expect(incoming).toBeNull()
              expect(outgoing).toBeNull()

              $httpBackend.flush()

              expect(incoming).toBeDefined()
              expect(outgoing).toBeDefined()

            it "has delete method", ->
              $httpBackend
              .when("DELETE", "#{modelCatalogue.getApiRoot()}/measurementUnit/1")
              .respond(204)

              expect(angular.isFunction(result.delete)).toBeTruthy()

              status = null

              result.delete().then((result) -> status = result)

              expect(status).toBeNull()

              $httpBackend.flush()

              expect(status).toBe(204)



        it "will respond with 404 if the resource does not exist", ->
          $httpBackend
          .when("GET", "#{modelCatalogue.getApiRoot()}/measurementUnit/10000000")
          .respond(404)

          result = null
          error  = null

          measurementUnits.get(10000000).then( (_result_) ->
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
          .when("DELETE", "#{modelCatalogue.getApiRoot()}/measurementUnit/1")
          .respond(204)

          result = null
          error  = null
          ok     = null

          measurementUnits.delete(1).then( (_result_) ->
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
          .when("DELETE", "#{modelCatalogue.getApiRoot()}/measurementUnit/1000000")
          .respond(404)

          result = null
          error  = null
          ok     = null

          measurementUnits.delete(1000000).then( (_result_) ->
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
          .when("DELETE", "#{modelCatalogue.getApiRoot()}/measurementUnit/2")
          .respond(409)

          result = null
          error  = null
          ok     = null

          measurementUnits.delete(2).then( (_result_) ->
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
          payloadWithId = angular.extend({}, fixtures.measurementUnit.updateInput)
          payloadWithId.id = 1

          $httpBackend
          .when("PUT", "#{modelCatalogue.getApiRoot()}/measurementUnit/1", fixtures.measurementUnit.updateInput)
          .respond(fixtures.measurementUnit.updateOk)

          result = null
          error  = null

          measurementUnits.update(payloadWithId).then( (_result_) ->
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
          expect(result.name).toBe(fixtures.measurementUnit.updateOk.name)

