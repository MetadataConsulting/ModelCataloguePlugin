package org.modelcatalogue.core.persistence

import grails.transaction.Transactional
import org.modelcatalogue.core.testapp.Requestmap
import org.springframework.http.HttpMethod

class RequestmapGormService {

    @Transactional
    Requestmap createRequestmapIfMissing(String url, String configAttribute, HttpMethod method = null) {
        List<Requestmap> maps = Requestmap.findAllByUrlAndHttpMethod(url, method)
        for(Requestmap map in maps) {
            if (map.configAttribute == configAttribute) {
                return map
            }
            println "Requestmap method: $method, url: $url has different config attribute - expected: $configAttribute, actual: $map.configAttribute"
        }
        Requestmap.findOrSaveByUrlAndConfigAttributeAndHttpMethod(url, configAttribute, method, [failOnError: true])
    }
}
