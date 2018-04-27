package org.modelcatalogue.core

import grails.test.mixin.TestFor
import org.modelcatalogue.core.util.MetadataDomain
import spock.lang.Specification

@TestFor(MdxTagLib)
class MdxTagLibSpec extends Specification {

    void "test metadataMessage tag"() {
        expect:
        applyTemplate('<mdx:metadataMessage metadataDomain="'+ MetadataDomain.DATA_MODEL+'" />') == 'Data Models'
        applyTemplate('<mdx:metadataMessage metadataDomain="'+ MetadataDomain.CATALOGUE_ELEMENT+'" />') == 'All elements'
        applyTemplate('<mdx:metadataMessage metadataDomain="'+ MetadataDomain.DATA_ELEMENT+'" />') == 'Data Elements'
        applyTemplate('<mdx:metadataMessage metadataDomain="'+ MetadataDomain.DATA_CLASS+'" />') == 'Data Classes'
        applyTemplate('<mdx:metadataMessage metadataDomain="'+ MetadataDomain.ENUMERATED_TYPE +'" />') == 'Enumerated types'
        applyTemplate('<mdx:metadataMessage metadataDomain="'+ MetadataDomain.DATA_TYPE +'" />') == 'Data types'
        applyTemplate('<mdx:metadataMessage metadataDomain="'+ MetadataDomain.MEASUREMENT_UNIT +'" />') == 'Measurement units'
        applyTemplate('<mdx:metadataMessage metadataDomain="'+ MetadataDomain.BUSINESS_RULE +'" />') == 'Business rules'
        applyTemplate('<mdx:metadataMessage metadataDomain="'+ MetadataDomain.TAG +'" />') == 'Tags'
    }
}
