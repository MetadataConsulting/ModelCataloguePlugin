package org.modelcatalogue.core.dataarchitect

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(OBOService)
@Mock([RelationshipType, Model, Classification, Relationship, ExtensionValue])
class OBOServiceSpec extends Specification {


    void "test something"() {
        service.importOntology(new URL('http://compbio.charite.de/hudson/job/hpo/lastStableBuild/artifact/hp/hp.obo').newInputStream(), "Human Phenotype", "")

        expect: true

    }
}
