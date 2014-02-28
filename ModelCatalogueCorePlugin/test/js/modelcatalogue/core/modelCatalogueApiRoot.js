describe('mc.core.modelCatalogueApiRoot', function() {

    // You need to load modules that you want to test,
    // it loads only the "ng" module by default.
    beforeEach(module('mc.core.modelCatalogueApiRoot'));


    // inject() is used to inject arguments of all given functions
    it('should provide a api root', inject(function(modelCatalogueApiRoot) {
        expect(modelCatalogueApiRoot).toEqual('/api/modelCatalogue/core');
    }));


    // The inject and module method can also be used inside of the it or beforeEach
    it('should override a version and test the new version is injected', function() {
        // module() takes functions or strings (module aliases)
        module(function($provide) {
            $provide.value('modelCatalogueApiRoot', '/context/api/modelCatalogue/core'); // override version here
        });

        inject(function(modelCatalogueApiRoot) {
            expect(modelCatalogueApiRoot).toEqual('/context/api/modelCatalogue/core');
        });
    });
});