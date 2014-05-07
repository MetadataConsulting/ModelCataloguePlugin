angular.module("mc.core.modelCatalogueDataArchitect", ['mc.util.rest', 'mc.util.enhance', 'mc.core.modelCatalogueApiRoot']).factory "modelCatalogueDataArchitect", [ 'rest', 'enhance', 'modelCatalogueApiRoot', (rest, enhance, modelCatalogueApiRoot)->
  modelCatalogueDataArchitect = {}

  modelCatalogueDataArchitect.uninstantiatedDataElements = (additionalParams = {}) ->
    params = angular.extend(additionalParams)
    enhance rest method: 'GET', url: "#{modelCatalogueApiRoot}/dataArchitect/uninstantiatedDataElements", params: params

  modelCatalogueDataArchitect.metadataKeyCheck = (query, additionalParams = {}) ->
    params = angular.extend({key: query}, additionalParams)
    enhance rest method: 'GET', url: "#{modelCatalogueApiRoot}/dataArchitect/metadataKeyCheck", params: params

  modelCatalogueDataArchitect
]