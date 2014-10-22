package org.modelcatalogue.core.dataarchitect

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.Model
import org.springframework.transaction.TransactionStatus

class OBOServiceISpec extends IntegrationSpec {

    def OBOService

    void "test import"() {
        boolean ok = false
        Model.withTransaction { TransactionStatus status ->
            OBOService.importOntology(new URL('http://compbio.charite.de/hudson/job/hpo/lastStableBuild/artifact/hp/hp.obo').newInputStream(), "Human Phenotype")
            ok = true
            status.setRollbackOnly()
        }

        expect: ok

    }
}
