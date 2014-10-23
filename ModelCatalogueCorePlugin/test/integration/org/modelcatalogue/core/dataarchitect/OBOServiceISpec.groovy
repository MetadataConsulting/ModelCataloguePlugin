package org.modelcatalogue.core.dataarchitect

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.Model
import org.springframework.transaction.TransactionStatus
import spock.lang.Ignore

class OBOServiceISpec extends IntegrationSpec {

    def OBOService

    @Ignore
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
