package org.modelcatalogue.core.diff

import com.google.common.collect.Multimap
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.integration.xml.CatalogueXmlLoader

class CatalogueElementDiffsIntegrationSpec extends AbstractIntegrationSpec {

    CatalogueBuilder catalogueBuilder
    GrailsApplication grailsApplication

    CatalogueElementDiffs catalogueElementDiffs
    CatalogueXmlLoader loader

    def setup() {
        initRelationshipTypes()
        loader = new CatalogueXmlLoader(catalogueBuilder)
        catalogueElementDiffs = new CatalogueElementDiffs(grailsApplication)
    }

    def "some diff" () {
        when:
            loader.load(CatalogueElementDiffsIntegrationSpec.getResourceAsStream('TestDataModelV1.xml'))
            loader.load(CatalogueElementDiffsIntegrationSpec.getResourceAsStream('TestDataModelV2.xml'))

            DataModel testV1 = DataModel.findByNameAndSemanticVersion('TestDataModel', '1')
            DataModel testV2 = DataModel.findByNameAndSemanticVersion('TestDataModel', '2')
        then:
            testV1
            testV2

        when:
            Multimap<String, Diff> diffs = catalogueElementDiffs.differentiate(testV1, testV2)
            for (Diff diff in diffs.values()) {
                println diff
            }
        then:
            diffs
            diffs.containsKey(Diff.keyForProperty('description'))
            !diffs.containsKey(Diff.keyForProperty('status'))
            !diffs.containsKey(Diff.keyForProperty('lastUpdated'))
            !diffs.containsKey(Diff.keyForProperty('versionCreated'))
            !diffs.containsKey(Diff.keyForProperty('version'))
            diffs.containsKey(Diff.keyForExtension('http://www.modelcatalogue.org/metadataStep/#reviewed'))
            diffs.containsKey(Diff.keyForExtension('http://www.modelcatalogue.org/metadataStep/#approved'))
        when:
            DataClass detailsV1 = DataClass.findByNameAndDataModel('PATIENT IDENTITY DETAILS', testV1)
            DataClass detailsV2 = DataClass.findByNameAndDataModel('PATIENT IDENTITY DETAILS', testV2)
        then:
            detailsV1
            detailsV2
        when:
            Multimap<String, Diff> detailsDiff = catalogueElementDiffs.differentiate(detailsV1, detailsV2)
            for (Diff diff in detailsDiff.values()) {
                println diff
            }
        then:
            detailsDiff
            detailsDiff.asMap().any { k, v -> k.startsWith("rex:${detailsV2.latestVersionId}=[containment/Min Occurs") }
            detailsDiff.asMap().any { k, v -> k.startsWith("rex:${detailsV2.latestVersionId}=[containment/Max Occurs") }
            detailsDiff.asMap().any { k, v -> k.startsWith("rex:${detailsV2.latestVersionId}=[containment/foo") }
            detailsDiff.asMap().any { k, v -> k.startsWith("rex:${detailsV2.latestVersionId}=[containment/bar") }
            detailsDiff.asMap().any { k, v -> k.startsWith("rel:${detailsV2.latestVersionId}=[containment]=") }

        when:
            EnumeratedType enumV1 = EnumeratedType.findByNameAndDataModel('testcerOrSymptomaticBreastReferralPatientStatus', testV1)
            EnumeratedType enumV2 = EnumeratedType.findByNameAndDataModel('testcerOrSymptomaticBreastReferralPatientStatus', testV2)
        then:
            enumV1
            enumV2
        when:
            Multimap<String, Diff> enumDiffs = catalogueElementDiffs.differentiate(enumV1, enumV2)
            for (Diff diff in enumDiffs.values()) {
                println diff
            }
        then:
            enumDiffs
            !enumDiffs.containsKey('enumAsString')
            enumDiffs.asMap().any { k, v -> k.startsWith("enum:") }

    }

}
