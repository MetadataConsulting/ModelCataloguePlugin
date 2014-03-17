angular.module("mc.core.modelCatalogueSearch", ['mc.util.rest', 'mc.util.enhance', 'mc.core.modelCatalogueApiRoot']).factory "modelCatalogueSearch", [ 'rest', 'enhance', 'modelCatalogueApiRoot', (rest, enhance, modelCatalogueApiRoot)->
  (query) -> enhance rest method: 'GET', url: "#{modelCatalogueApiRoot}/search", params: { search: query }
]