package org.modelcatalogue.core.persistence

import groovy.transform.CompileStatic
import org.modelcatalogue.core.DataModel

@CompileStatic
class HqlOperationUtils {

    static HqlOperation ofDataModelAndKeywordList(DataModel dataModel, List<String> keywords) {
        HqlOperation keywordsHqlOperation = ofKeywords(keywords)
        String hql = "from DataElement as de where de.dataModel = :dataModelParam and (${keywordsHqlOperation.hql})".toString()
        Map params =  [dataModelParam: dataModel] + keywordsHqlOperation.params
        new HqlOperation(hql: hql, params: params)
    }

    static HqlOperation ofKeywords(List<String> keywords) {
        Map params = [:]
        keywords.eachWithIndex { String keyword, int index ->
            params[("keyword${index}".toString())] = "%${keyword}%".toString()
        }
        String hql = keywords.withIndex().collect { element, index -> "name like :keyword${index}" }.join(' OR ').toString()
        new HqlOperation(hql: hql, params: params)
    }
}
