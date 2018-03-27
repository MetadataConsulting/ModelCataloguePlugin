package org.modelcatalogue.core

import grails.validation.Validateable

@Validateable
class DataModelCreateCommand {
    String name
    String semanticVersion = '0.0.1'
    String modelCatalogueId
    String description
    List<Long> dataModelPolicies
    List<Long> dataModels

    static constraints = {
        name nullable: false
        semanticVersion nullable: true
        modelCatalogueId nullable: true
        dataModels nullable: true
        dataModelPolicies nullable: true
        description nullable: true
    }

    DataModel toDataModel() {
        new DataModel(name: name, semanticVersion: semanticVersion, modelCatalogueId: modelCatalogueId, description: description)
    }

    Map<String, Object> toMap() {
        [
                name: name,
                semanticVersion: semanticVersion,
                modelCatalogueId: modelCatalogueId,
                description: description,
                dataModelPolicies: dataModelPolicies,
                dataModels: dataModels
        ]
    }
}
