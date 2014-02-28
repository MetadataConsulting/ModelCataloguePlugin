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
          .when("GET", "#{modelCatalogue.getModelCatalogueApiRoot()}/measurementUnit/?max=10&offset=10")
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
        .when("GET", "#{modelCatalogue.getModelCatalogueApiRoot()}/measurementUnit/?max=10&offset=0")
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
          .when("GET", "#{modelCatalogue.getModelCatalogueApiRoot()}/measurementUnit/1")
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


          describe "fetched instance is enhanced", ->
            it "outgoing and incoming relationships are functions", ->
              $httpBackend
              .when("GET", "#{modelCatalogue.getModelCatalogueApiRoot()}/measurementUnit/1/outgoing")
              .respond(fixtures.measurementUnit.outgoing1)
              $httpBackend
              .when("GET", "#{modelCatalogue.getModelCatalogueApiRoot()}/measurementUnit/1/incoming")
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
              .when("DELETE", "#{modelCatalogue.getModelCatalogueApiRoot()}/measurementUnit/1")
              .respond(204)

              expect(angular.isFunction(result.delete)).toBeTruthy()

              status = null

              result.delete().then((result) -> status = result)

              expect(status).toBeNull()

              $httpBackend.flush()

              expect(status).toBe(204)

            it "has update method", ->
              expectedPayload = {
                symbol:       "°C"
                description:  "Celsius, also known as centigrade,[1] is a scale and unit of measurement for temperature. It is named after the Swedish astronomer Anders Celsius (1701–1744), who developed a similar temperature scale. The degree Celsius (°C) can refer to a specific temperature on the Celsius scale as well as a unit to indicate a temperature interval, a difference between two temperatures or an uncertainty. The unit was known until 1948 as \"centigrade\" from the Latin centum translated as 100 and gradus translated as \"steps\"."
                name:         "Degrees of Celsius"
                version:      1
              }

              result.version = 1

              $httpBackend
              .when("PUT", "#{modelCatalogue.getModelCatalogueApiRoot()}/measurementUnit/1", expectedPayload)
              .respond(fixtures.measurementUnit.updateOk)

              expect(angular.isFunction(result.update)).toBeTruthy()

              updated = null

              result.update().then((result) -> updated = result)

              expect(updated).toBeNull()

              $httpBackend.flush()

              expect(updated).toBeDefined()



        it "will respond with 404 if the resource does not exist", ->
          $httpBackend
          .when("GET", "#{modelCatalogue.getModelCatalogueApiRoot()}/measurementUnit/10000000")
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
          .when("DELETE", "#{modelCatalogue.getModelCatalogueApiRoot()}/measurementUnit/1")
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
          .when("DELETE", "#{modelCatalogue.getModelCatalogueApiRoot()}/measurementUnit/1000000")
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
          .when("DELETE", "#{modelCatalogue.getModelCatalogueApiRoot()}/measurementUnit/2")
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
          .when("PUT", "#{modelCatalogue.getModelCatalogueApiRoot()}/measurementUnit/1", fixtures.measurementUnit.updateInput)
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
          expect(result.update).toBeDefined()

        it "returns 404 if the resource does not exist", ->
          payloadWithId = angular.extend({}, fixtures.measurementUnit.updateInput)
          payloadWithId.id = 1000000

          $httpBackend
          .when("PUT", "#{modelCatalogue.getModelCatalogueApiRoot()}/measurementUnit/1000000", fixtures.measurementUnit.updateInput)
          .respond(404)

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

          expect(error).toBeDefined()
          expect(error.status).toBe(404)
          expect(result).toBeNull()

        it "updates failed if wrong input", ->
          payloadWithId = angular.extend({}, fixtures.measurementUnit.updateInput)
          payloadWithId.id = 1
          payloadWithId.name = fixtures.measurementUnit.updateErrors.errors[0]["rejected-value"]

          payloadWithoutId = angular.extend({}, payloadWithId)
          delete payloadWithoutId.id

          $httpBackend
          .when("PUT", "#{modelCatalogue.getModelCatalogueApiRoot()}/measurementUnit/1", payloadWithoutId)
          .respond(fixtures.measurementUnit.updateErrors)

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

          expect(result).toBeNull()
          expect(error).toBeDefined()
          expect(error.data).toBeDefined()
          expect(error.data.errors).toBeDefined()
          expect(error.data.errors[0].field).toBe("name")
          expect(error.data.errors[0]["rejected-value"]).toBe(fixtures.measurementUnit.updateErrors.errors[0]["rejected-value"])
          expect(error.data.errors[0].message).toBe(fixtures.measurementUnit.updateErrors.errors[0].message)

      describe "can save new entity", ->
        it "saves if input data ok", ->
          payload = {
            name:    "Miles per hour"
            symbol:  "MPH"
          }

          $httpBackend
          .when("POST", "#{modelCatalogue.getModelCatalogueApiRoot()}/measurementUnit", payload)
          .respond(fixtures.measurementUnit.saveOk)

          result = null
          error  = null

          measurementUnits.save(payload).then( (_result_) ->
            result = _result_
          , (_error_) ->
            error  = _error_
          )

          expect(result).toBeNull()
          expect(error).toBeNull()

          $httpBackend.flush()

          expect(error).toBeNull()
          expect(result).toBeDefined()
          expect(result.id).toBe(fixtures.measurementUnit.saveOk.id)
          expect(result.version).toBe(fixtures.measurementUnit.saveOk.version)
          expect(result.name).toBe(fixtures.measurementUnit.saveOk.name)
          expect(result.update).toBeDefined()

        it "save failed if wrong input", ->
          payload = {symbol: "MPH"}

          $httpBackend
          .when("POST", "#{modelCatalogue.getModelCatalogueApiRoot()}/measurementUnit", payload)
          .respond(fixtures.measurementUnit.saveErrors)

          result = null
          error  = null

          measurementUnits.save(payload).then( (_result_) ->
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
          expect(error.data.errors[0]["rejected-value"]).toBe(fixtures.measurementUnit.saveErrors.errors[0]["rejected-value"])
          expect(error.data.errors[0].message).toBe(fixtures.measurementUnit.saveErrors.errors[0].message)
