package org.modelcatalogue.core

import org.modelcatalogue.core.util.MetadataDomain

class MdxTagLib {
    static namespace = "mdx"

    static returnObjectForTags = ['metadataMessage']

    def metadataMessage = { attrs, body ->
        MetadataDomain domain = attrs.metadataDomain
        if ( domain == MetadataDomain.CATALOGUE_ELEMENT ) {
            return "All elements"
        } else if ( domain == MetadataDomain.DATA_MODEL ) {
            return 'Data Models'
        } else if ( domain == MetadataDomain.DATA_ELEMENT ) {
            return 'Data Elements'
        } else if ( domain == MetadataDomain.DATA_CLASS ) {
            return 'Data Classes'
        } else if ( domain == MetadataDomain.ENUMERATED_TYPE ) {
            return 'Enumerated types'
        } else if ( domain == MetadataDomain.DATA_TYPE ) {
            return 'Data types'
        } else if ( domain == MetadataDomain.MEASUREMENT_UNIT ) {
            return 'Measurement units'
        } else if ( domain == MetadataDomain.BUSINESS_RULE ) {
            return 'Business rules'
        } else if ( domain == MetadataDomain.TAG ) {
            return 'Tags'
        } else {
            return domain.name()
        }
    }
}
