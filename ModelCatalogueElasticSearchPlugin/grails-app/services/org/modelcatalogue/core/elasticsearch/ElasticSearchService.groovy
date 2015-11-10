package org.modelcatalogue.core.elasticsearch

import grails.util.GrailsNameUtils
import groovy.json.JsonSlurper
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.SearchCatalogue
import org.modelcatalogue.core.util.ListWithTotalAndType
import org.modelcatalogue.core.util.RelationshipDirection

class ElasticSearchService implements SearchCatalogue {

    @Override
    ListWithTotalAndType<Relationship> search(CatalogueElement element, RelationshipType type, RelationshipDirection direction, Map params) {
        return null
    }

    @Override
    def <T> ListWithTotalAndType<T> search(Class<T> resource, Map params) {
        return null
    }

    @Override
    ListWithTotalAndType<CatalogueElement> search(Map params) {
        return null
    }

    @Override
    void index(Object element) {

    }

    @Override
    void index(Iterable<Object> resource) {

    }

    @Override
    void unindex(Object object) {

    }

    @Override
    void unindex(Collection<Object> object) {

    }

    @Override
    void refresh() {

    }

    Map<String, Map> getMapping(Class clazz) {
        if (!clazz) {
            return [:]
        }

        Map<String, Map> mapping = [(getTypeName(clazz)): [:]]

        if (clazz.superclass && !clazz.superclass != Object) {
            mapping[getTypeName(clazz)].putAll(getMapping(clazz.superclass)[getTypeName(clazz.superclass)])
        }

        File mappingFile = new File("${clazz.simpleName}.mapping.json")

        if (mappingFile.exists()) {
            Map parsed = new JsonSlurper().parse(mappingFile) as Map
            mapping[getTypeName(clazz)].putAll(parsed[getTypeName(clazz)] as Map)
        }

        return mapping
    }


    static String getTypeName(Class clazz) {
        GrailsNameUtils.getNaturalName(clazz.simpleName).replaceAll(/\s/, '_')
    }
}
