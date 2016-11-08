package org.modelcatalogue.core.dataarchitect

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.DataClass
import org.springframework.transaction.TransactionStatus
import spock.lang.Ignore

class OBOServiceISpec extends IntegrationSpec {

    def OBOService

    @Ignore
    void "test import"() {
        boolean ok = false
        DataClass.withTransaction { TransactionStatus status ->
            OBOService.importOntology(new URL('http://compbio.charite.de/hudson/job/hpo/lastStableBuild/artifact/hp/hp.obo').newInputStream(), "Human Phenotype", 'http://purl.obolibrary.org/obo/${id.replace(\':\', \'_\')}')
            ok = true
            status.setRollbackOnly()
        }

        expect: ok

    }
}
