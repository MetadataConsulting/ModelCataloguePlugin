package org.modelcatalogue.core

import org.modelcatalogue.core.dataarchitect.CSVService
import org.springframework.web.multipart.MultipartFile

class CSVController {

    CSVService csvService

    def headers() {
        MultipartFile file = request.getFile('csv')

        file.inputStream.withReader {
            respond csvService.readHeaders(it, params.separator ?: ';')
        }
    }
}
