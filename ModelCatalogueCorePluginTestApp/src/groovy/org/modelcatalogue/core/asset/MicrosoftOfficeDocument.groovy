package org.modelcatalogue.core.asset

import org.codehaus.groovy.grails.web.mime.MimeType

enum MicrosoftOfficeDocument {
    DOC, XLSX, EXCEL

    static String documentType(MicrosoftOfficeDocument officeDocument) {
        switch (officeDocument) {
            case DOC:
                return 'MS Doc'
            case XLSX:
            case EXCEL:
                return 'MS Excel'
        }
    }

    static String suffix(MicrosoftOfficeDocument officeDocument) {
        switch (officeDocument) {
            case DOC:
                return 'docx'
            case EXCEL:
            case XLSX:
                return 'xlsx'
        }
    }

    static String contentType(MicrosoftOfficeDocument officeDocument) {
        switch (officeDocument) {
            case DOC:
                return 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
            case XLSX:
                return 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
            case EXCEL:
                return 'application/vnd.ms-excel'
        }
    }

    static MimeType mimeType(MicrosoftOfficeDocument doc) {
        return new MimeType(contentType(doc), suffix(doc))
    }

}