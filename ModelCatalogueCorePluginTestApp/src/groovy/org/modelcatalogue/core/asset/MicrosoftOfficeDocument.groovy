package org.modelcatalogue.core.asset

enum MicrosoftOfficeDocument {
    DOC, EXCEL

    static String documentType(MicrosoftOfficeDocument officeDocument) {
        switch (officeDocument) {
            case DOC:
                return 'MS Doc'
            case EXCEL:
                return 'MS Excel'
        }
    }

    static String suffix(MicrosoftOfficeDocument officeDocument) {
        switch (officeDocument) {
            case DOC:
                return 'docx'
            case EXCEL:
                return 'xlsx'
        }
    }

    static String contentType(MicrosoftOfficeDocument officeDocument) {
        switch (officeDocument) {
            case DOC:
                return 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
            case EXCEL:
                return 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        }
    }

}