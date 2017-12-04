package org.modelcatalogue.core.dataarchitect

import grails.transaction.Transactional
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.modelcatalogue.Client

@Transactional
class ExcelImportService {
    static List<String> excelImportTypesHumanReadable() {
        return Client.fromString(System.properties['mc.client']).excelImportTypes.collect {
            it.humanReadableName
        }

    }
}
