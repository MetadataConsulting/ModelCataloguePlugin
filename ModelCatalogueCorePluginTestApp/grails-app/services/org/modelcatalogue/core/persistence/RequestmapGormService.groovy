package org.modelcatalogue.core.persistence

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.modelcatalogue.core.testapp.Requestmap
import org.springframework.http.HttpMethod

class RequestmapGormService {

    @Transactional
    Requestmap createRequestmapIfMissing(String url, String configAttribute, HttpMethod method = null) {
        List<Requestmap> maps = Requestmap.findAllByUrlAndHttpMethod(url, method)
        for (Requestmap map in maps) {
            if (map.configAttribute == configAttribute) {
                return map
            }
            println "Requestmap method: $method, url: $url has different config attribute - expected: $configAttribute, actual: $map.configAttribute"
        }
        Requestmap.findOrSaveByUrlAndConfigAttributeAndHttpMethod(url, configAttribute, method, [failOnError: true])
    }

    DetachedCriteria<Requestmap> queryByIds(List<Long> ids) {
        Requestmap.where { id in ids }
    }

    @Transactional(readOnly = true)
    List<Requestmap> findAllByIds(List<Long> ids) {
        if ( !ids ) {
            return [] as List<Requestmap>
        }
        queryByIds(ids).list()
    }
}
