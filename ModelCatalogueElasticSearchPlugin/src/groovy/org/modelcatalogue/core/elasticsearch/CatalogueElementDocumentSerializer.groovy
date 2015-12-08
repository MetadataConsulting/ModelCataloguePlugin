package org.modelcatalogue.core.elasticsearch

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import grails.util.GrailsNameUtils
import org.hibernate.proxy.HibernateProxyHelper
import org.modelcatalogue.core.CatalogueElement

import static org.modelcatalogue.core.util.HibernateHelper.getEntityClass

class CatalogueElementDocumentSerializer<T extends CatalogueElement> implements DocumentSerializer<T> {


    @Override
    ImmutableMap.Builder<String, Object> buildDocument(IndexingSession session, T element, ImmutableMap.Builder<String, Object> builder) {
        Class<? extends CatalogueElement> clazz = getEntityClass(element)

        safePut(builder, 'name', element.name)
        safePut(builder, 'name_not_analyzed', element.name)
        safePut(builder, 'link', "/${GrailsNameUtils.getPropertyName(clazz)}/${element.getId()}")
        safePut(builder, 'description', element.description)
        safePut(builder, 'fully_qualified_type', clazz.name)
        safePut(builder, 'type_human_readable', GrailsNameUtils.getNaturalName(clazz.simpleName))
        safePut(builder, 'data_model', element.dataModel ? element.dataModel.name : null)
        safePut(builder, 'full_version', element.combinedVersion)
        safePut(builder, 'latest_id', (element.latestVersionId ?: element.getId()).toString())
        safePut(builder, 'version_number', element.versionNumber)
        safePut(builder, 'model_catalogue_id', element.modelCatalogueId ?: element.getDefaultModelCatalogueId(false))
        safePut(builder, 'entity_id', element.getId().toString())
        safePut(builder, 'status', element.status.toString())
        safePut(builder, 'date_create', new Date(element.dateCreated.time))
        safePut(builder, 'last_updated', new Date(element.lastUpdated.time))
        safePut(builder, 'ext', getExtensions(element.ext))


        return builder
    }

    static ImmutableList<ImmutableMap<String, String>> getExtensions(Map<String, String> ext) {
        ImmutableList.Builder<ImmutableMap<String, String>> listBuilder = ImmutableList.builder()

        for (Map.Entry<String, String> entry in ext) {
            ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder()
            safePut(mapBuilder, 'key', entry.key)
            safePut(mapBuilder, 'value', entry.value)
            listBuilder.add(mapBuilder.build())
        }

        listBuilder.build()
    }

    static <K,V> ImmutableMap.Builder<K,V> safePut(ImmutableMap.Builder<K,V> builder, K key,  V value) {
        if (value != null) {
            return builder.put(key, value)
        }
        return builder
    }

}
