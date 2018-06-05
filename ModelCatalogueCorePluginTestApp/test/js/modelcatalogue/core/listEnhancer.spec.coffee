if window.fixtures
  describe "mc.core.listEnhancer", ->

    rest                  = null
    enhance               = null
    $httpBackend          = null
    $rootScope            = null
    modelCatalogueApiRoot = null

    beforeEach module "mc.core.listEnhancer"
    beforeEach module "mc.core.promiseEnhancer"

    beforeEach inject (_rest_, _enhance_, _$httpBackend_, _$rootScope_, _modelCatalogueApiRoot_) ->
      rest                  = _rest_
      $httpBackend          = _$httpBackend_
      $rootScope            = _$rootScope_
      modelCatalogueApiRoot = _modelCatalogueApiRoot_
      enhance               = _enhance_

    it "can list resource", ->
      $httpBackend
      .when("GET", '/foo')
      .respond(fixtures.dataType.list1)

      result = null
      error  = null
      rest.cleanCache()
      enhance(rest(method: 'GET', url: '/foo')).then( (_result_) ->
        result = _result_
      , (_error_) ->
        error = _error_
      )

      expect(result).toBeNull()

      $httpBackend.flush()

      expect(result).toBeDefined()
      expect(result.total).toBe(48)
      expect(result.page).toBe(1)
      expect(result.size).toBe(1)
      expect(result.offset).toBe(0)
      expect(result.currentPage).toBe(1)
      expect(result.list).toBeDefined()
      expect(result.list.length).toBe(1)
      expect(angular.isFunction(result.next)).toBeTruthy()
      expect(result.next.size).toBe(1)
      expect(angular.isFunction(result.previous)).toBeTruthy()
      expect(result.previous.size).toBe(0)
      expect(angular.isFunction(result.goto)).toBeTruthy()

      emptyResult = null

      result.previous().then((_result_) -> emptyResult = _result_)

      expect(emptyResult).toBeNull()

      $rootScope.$apply();

      expect(emptyResult).toBeDefined()
      expect(emptyResult.total).toBe(48)
      expect(emptyResult.page).toBe(1)
      expect(emptyResult.size).toBe(0)
      expect(emptyResult.offset).toBe(0)
      expect(emptyResult.currentPage).toBe(1)
      expect(emptyResult.list).toBeDefined()
      expect(emptyResult.list.length).toBe(0)
      expect(emptyResult.success).toBeFalsy()


      nextList = angular.copy(fixtures.dataType.list1)
      nextList.size = 2
      nextList.list = nextList.list.slice(0, 2)
      nextList.offset = 10
      nextList.next = fixtures.dataType.list1.next.replace("offset=10", "offset=20")
      nextList.previous = "/dataType/?max=10&offset=0"

      $httpBackend
      .when("GET", "#{modelCatalogueApiRoot}/dataType/?max=1&total=48&offset=1")
      .respond(nextList)

      rest.cleanCache()

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
      expect(nextResult.total).toBe(48)
      expect(nextResult.page).toBe(1)
      expect(nextResult.size).toBe(2)
      expect(nextResult.offset).toBe(10)
      expect(nextResult.currentPage).toBe(11)
      expect(nextResult.list).toBeDefined()
      expect(nextResult.list.length).toBe(1)
      expect(angular.isFunction(nextResult.next)).toBeTruthy()
      expect(angular.isFunction(nextResult.previous)).toBeTruthy()

      $httpBackend
      .when("GET", "#{modelCatalogueApiRoot}/dataType/?max=10&offset=0")
      .respond(fixtures.dataType.list1)

      rest.cleanCache()

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
      expect(result.total).toBe(48)
      expect(result.page).toBe(1)
      expect(result.size).toBe(1)
      expect(result.offset).toBe(0)
      expect(result.currentPage).toBe(1)
      expect(result.list).toBeDefined()
      expect(result.list.length).toBe(1)

      listEnhancer = enhance.getEnhancer('list')

      emptyList = listEnhancer.createEmptyList(itemType: 'relationships')
      expect(emptyList.total).toBe(0)
      expect(emptyList.size).toBe(0)
      expect(emptyList.list).toEqual([])
      expect(emptyList.next.size).toBe(0)
      expect(emptyList.previous.size).toBe(0)
      expect(emptyList.empty).toBeTruthy()
      expect(emptyList.itemType).toBe('relationships')

      singleItem    = {foo: 'bar', elementType: 'the.type'}
      singletonList = listEnhancer.createSingletonList(singleItem)
      expect(singletonList.total).toBe(1)
      expect(singletonList.size).toBe(1)
      expect(singletonList.list).toEqual([singleItem])
      expect(singletonList.next.size).toBe(0)
      expect(singletonList.previous.size).toBe(0)
      expect(singletonList.itemType).toBe('the.type')

      theList     = [{one: 'one'}, {two: 2}, {3: 'three'}]
      wrappedList = listEnhancer.createArrayList(theList, itemType: 'the.list.type')
      expect(wrappedList.total).toBe(3)
      expect(wrappedList.size).toBe(3)
      expect(wrappedList.list).toEqual(theList)
      expect(wrappedList.next.size).toBe(0)
      expect(wrappedList.previous.size).toBe(0)
      expect(wrappedList.itemType).toBe('the.list.type')


