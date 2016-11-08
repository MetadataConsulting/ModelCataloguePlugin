describe("metadata.exchange.index", function() {

    var $rootScope, $state, $location;

    beforeEach(angular.mock.module('metadata.exchange.index', function() {

    }));

    beforeEach(angular.mock.inject(function (_$rootScope_, $templateCache, _$state_, _$location_) {
        $rootScope = _$rootScope_;
        $state = _$state_;
        $location = _$location_;
        $templateCache.put('/metadata/exchange/index/index.html', '');
    }));

    it('should respond to URL', function() {
        expect($state.href("index")).toEqual('#/');
    });

    it('should go to the index page', function() {
        $state.go('index');
        $rootScope.$apply();
        expect($state.current.name).toEqual('index');
    });

    it('should default to the index page', function() {
        $location.path('/#/foo');
        $rootScope.$apply();
        expect($state.current.name).toEqual('index');
    });

});
