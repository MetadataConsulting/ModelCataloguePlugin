package org.modelcatalogue.core

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.integration.obo.OboLoader
import spock.lang.Shared
import spock.lang.Unroll

class HPOUpdatesSpec extends AbstractIntegrationSpec  {

    @Shared GrailsApplication grailsApplication

    CatalogueBuilder catalogueBuilder
    ElementService elementService


    def setup() {
        initRelationshipTypes()
    }

    @Unroll
    def "update HPO with id pattern #idPattern"() {
        OboLoader loader = new OboLoader(catalogueBuilder)

        when:
        InputStream test1 = getClass().getResourceAsStream('test1.obo')
        loader.load(test1, 'HPO', idPattern)

        then:
        noExceptionThrown()

        when:
        DataModel hpo = DataModel.findByName('HPO')

        then:
        hpo
        hpo.countDeclares() == 12

        when:
        hpo = elementService.finalizeDataModel(hpo, hpo.semanticVersion, "finalized", true)

        then:
        noExceptionThrown()
        hpo
        hpo.errors.errorCount == 0
        hpo.status == ElementStatus.FINALIZED
        hpo.countDeclares() == 12

        when:
        InputStream test2 = getClass().getResourceAsStream('test2.obo')
        loader.load(test2, 'HPO', idPattern)
        DataModel hpoDraft = DataModel.findByNameAndStatus('HPO', ElementStatus.DRAFT)

        then:
        noExceptionThrown()
        hpoDraft
        hpoDraft.countDeclares() == 14

        when:
        DataClass renalCyst = DataClass.findByNameAndDataModel('Renal cyst', hpoDraft)
        DataClass something = DataClass.findByNameAndDataModel('Something Different', hpoDraft)
        DataClass somethingObsolete = DataClass.findByNameAndDataModel('Something Different Obsolete', hpoDraft)

        then:
        renalCyst
        something
        something in renalCyst.parentOf
        somethingObsolete
        somethingObsolete.status == ElementStatus.DEPRECATED

        when:
        hpo = elementService.finalizeDataModel(hpoDraft, hpoDraft.semanticVersion, "finalized", true)

        then:
        noExceptionThrown()
        hpo
        hpo.errors.errorCount == 0
        hpo.status == ElementStatus.FINALIZED
        hpo.countDeclares() == 14

        when:
        InputStream test3 = getClass().getResourceAsStream('test3.obo')
        loader.load(test3, 'HPO', idPattern)
        hpoDraft = DataModel.findByNameAndStatus('HPO', ElementStatus.DRAFT)

        then:
        noExceptionThrown()
        hpoDraft
        hpoDraft.countDeclares() == 15

        where:
        idPattern << [
            "${grailsApplication.config.grails.serverURL}/catalogue/ext/${URLEncoder.encode(OboLoader.OBO_ID, 'UTF-8')}/:id".toString().replace(':id', '$id'),
            'http://purl.obolibrary.org/obo/${id.replace(\'%3A\', \'_\')}'
        ]

    }


}
