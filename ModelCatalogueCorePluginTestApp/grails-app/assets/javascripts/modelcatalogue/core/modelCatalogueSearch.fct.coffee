angular.module("mc.core.modelCatalogueSearch", ['mc.util.rest', 'mc.util.enhance', 'mc.core.modelCatalogueApiRoot']).factory "modelCatalogueSearch", [ 'rest', 'enhance', 'modelCatalogueApiRoot', (rest, enhance, modelCatalogueApiRoot)->
  (query, additionalParams = {}) ->
    params = angular.extend({search: query}, additionalParams)
    enhance rest method: 'GET', url: "#{modelCatalogueApiRoot}/search", params: params
]