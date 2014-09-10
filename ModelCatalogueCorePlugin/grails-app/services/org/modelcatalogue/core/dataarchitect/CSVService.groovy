package org.modelcatalogue.core.dataarchitect

import au.com.bytecode.opencsv.CSVReader

class CSVService {

    static transactional = false

    String[] readHeaders(Reader input, String separator = ';') {
        CSVReader reader = new CSVReader(input, separator?.charAt(0))
        try {
            return reader.readNext()
        } catch (Exception e) {
            log.warn("Exception reading csv", e)
            return []
        }
    }
}
