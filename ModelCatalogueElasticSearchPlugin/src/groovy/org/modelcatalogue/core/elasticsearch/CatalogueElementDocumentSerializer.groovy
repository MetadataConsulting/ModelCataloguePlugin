package org.modelcatalogue.core.elasticsearch

import grails.util.GrailsNameUtils
import org.hibernate.proxy.HibernateProxyHelper
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship

class CatalogueElementDocumentSerializer implements DocumentSerializer<CatalogueElement> {

    Map getDocument(CatalogueElement element) {
        [
                name: element.name,
                name_not_analyzed: element.name,
                description: element.description,
                type_human_readable: GrailsNameUtils.getNaturalName(HibernateProxyHelper.getClassWithoutInitializingProxy(element).simpleName),
                data_model: element.dataModels ? element.dataModels.first().name : null,
                full_version: element.combinedVersion,
                latest_version: element.latestVersionId ?: element.getId(),
                version_number: element.versionNumber,
                model_catalogue_id: element.modelCatalogueId,
                id: element.getId(),
                status: element.status.toString(),
                date_create: element.dateCreated,
                last_updated: element.lastUpdated,
                relationships: collectRelationships(element),
                ext: element.ext.collect { key, value -> [key: key, value: value] }

        ]

    }


    private static List<Map> collectRelationships(CatalogueElement element) {
        List<Map> ret = []
        for (Relationship relationship in element.outgoingRelationships) {
            ret << [
                    id: relationship.getId(),
                    incoming_index: relationship.incomingIndex,
                    outgoing_index: relationship.outgoingIndex,
                    relationship_type: relationship.relationshipType.name,
                    relationship_type_id: relationship.relationshipType.getId(),
                    name: relationship.ext['name'] ?: relationship.ext['Name'] ?: relationship.destination.name,
                    relation_name: relationship.destination.name,
                    relation_id: relationship.destination.getId(),
                    archived: relationship.archived,
                    inherited: relationship.inherited,
                    ext: relationship.ext.collect { key, value -> [key: key, value: value] }
            ]
        }
        for (Relationship relationship in element.incomingRelationships) {
            ret << [
                    id: relationship.getId(),
                    incoming_index: relationship.incomingIndex,
                    outgoing_index: relationship.outgoingIndex,
                    relationship_type: relationship.relationshipType.name,
                    relationship_type_id: relationship.relationshipType.getId(),
                    name: relationship.ext['name'] ?: relationship.ext['Name'] ?: relationship.source.name,
                    relation_name: relationship.source.name,
                    relation_id: relationship.source.getId(),
                    archived: relationship.archived,
                    inherited: relationship.inherited,
                    ext: relationship.ext.collect { key, value -> [key: key, value: value] }
            ]
        }
        ret
    }


}
