package org.modelcatalogue.core.asset

import spock.lang.Specification
import spock.lang.Unroll

class MicrosoftOfficeDocumentSpec extends Specification {

    @Unroll
    void "#doc document type is #type"(MicrosoftOfficeDocument doc, String type) {

        expect:
        type == MicrosoftOfficeDocument.documentType(doc)

        where:
        doc                           | type
        MicrosoftOfficeDocument.DOC   | 'MS Doc'
        MicrosoftOfficeDocument.EXCEL | 'MS Excel'
    }

    @Unroll
    void "#doc suffix is #suffix"(MicrosoftOfficeDocument doc, String suffix) {

        expect:
        suffix == MicrosoftOfficeDocument.suffix(doc)

        where:
        doc                           | suffix
        MicrosoftOfficeDocument.DOC   | 'docx'
        MicrosoftOfficeDocument.EXCEL | 'xlsx'
    }

    @Unroll
    void "#doc content type is #contentType"(MicrosoftOfficeDocument doc, String contentType) {

        expect:
        contentType == MicrosoftOfficeDocument.contentType(doc)

        where:
        doc                           | contentType
        MicrosoftOfficeDocument.DOC   | 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
        MicrosoftOfficeDocument.EXCEL | 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    }
}
