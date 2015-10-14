angular.module("mc.core.modelCatalogueDataArchitect", ['mc.util.rest', 'mc.util.enhance', 'mc.core.modelCatalogueApiRoot']).factory "modelCatalogueDataArchitect", [ 'rest', 'enhance', 'modelCatalogueApiRoot', (rest, enhance, modelCatalogueApiRoot)->
  modelCatalogueDataArchitect = {}

  modelCatalogueDataArchitect.metadataKeyCheck = (query, additionalParams = {}) ->
    params = angular.extend({key: query}, additionalParams)
    enhance rest method: 'GET', url: "#{modelCatalogueApiRoot}/dataArchitect/metadataKeyCheck", params: params

  modelCatalogueDataArchitect.findRelationsByMetadataKeys = (query, query2, additionalParams = {}) ->
    params = angular.extend({keyOne: query}, {keyTwo: query2}, additionalParams)
    enhance rest method: 'GET', url: "#{modelCatalogueApiRoot}/dataArchitect/findRelationsByMetadataKeys", params: params

  modelCatalogueDataArchitect.imports = (additionalParams = {}) ->
    params = angular.extend(additionalParams)
    enhance rest method: 'GET', url: "#{modelCatalogueApiRoot}/dataArchitect/imports", params: params

  modelCatalogueDataArchitect.getImport = (id, additionalParams = {}) ->
    params = angular.extend({id: id}, additionalParams)
    enhance rest method: 'GET', url: "#{modelCatalogueApiRoot}/dataArchitect/imports/#{id}", params: params

  modelCatalogueDataArchitect.resolveAll = (id, additionalParams = {}) ->
    params = angular.extend({id: id}, additionalParams)
    enhance rest method: 'POST', url: "#{modelCatalogueApiRoot}/dataArchitect/imports/#{id}/resolveAll", params: params

  modelCatalogueDataArchitect.ingestQueue = (id, additionalParams = {}) ->
    params = angular.extend({id: id}, additionalParams)
    enhance rest method: 'POST', url: "#{modelCatalogueApiRoot}/dataArchitect/imports/#{id}/ingestQueue", params: params

  modelCatalogueDataArchitect
]