package org.modelcatalogue.core.elasticsearch

import grails.util.GrailsNameUtils
import org.hibernate.proxy.HibernateProxyHelper
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship

class CatalogueElementDocumentSerializer implements DocumentSerializer<CatalogueElement> {

    Map getDocument(CatalogueElement element) {
        Class<? extends CatalogueElement> clazz = HibernateProxyHelper.getClassWithoutInitializingProxy(element)
        [
                name: element.name,
                name_not_analyzed: element.name,
                link: "/${GrailsNameUtils.getPropertyName(clazz)}/${element.getId()}",
                description: element.description,
                fully_qualified_type: clazz.name,
                type_human_readable: GrailsNameUtils.getNaturalName(clazz.simpleName),
                data_model: element.dataModels ? element.dataModels.first().name : null,
                full_version: element.combinedVersion,
                latest_id: (element.latestVersionId ?: element.getId()).toString(),
                version_number: element.versionNumber,
                model_catalogue_id: element.modelCatalogueId ?: element.getDefaultModelCatalogueId(false),
                entity_id: element.getId().toString(),
                status: element.status.toString(),
                date_create: element.dateCreated,
                last_updated: element.lastUpdated,
                ext: element.ext.collect { key, value -> [key: key, value: value] }

        ]

    }

}
