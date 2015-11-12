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
                model_catalogue_id: element.modelCatalogueId ?: element.getDefaultModelCatalogueId(false),
                id: element.getId(),
                status: element.status.toString(),
                date_create: element.dateCreated,
                last_updated: element.lastUpdated,
                ext: element.ext.collect { key, value -> [key: key, value: value] }

        ]

    }

}
