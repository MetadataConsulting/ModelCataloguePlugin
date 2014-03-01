package uk.co.mc.core

import grails.test.spock.IntegrationSpec
import spock.lang.Shared

/**
 * Created by adammilward on 27/02/2014.
 */
abstract class AbstractIntegrationSpec extends IntegrationSpec {

    @Shared
    def fixtureLoader, fixtures

    def loadFixtures(){
        if(CatalogueElement.count()==0){
            fixtures = fixtureLoader.load("dataTypes/*", "enumeratedTypes/*", "measurementUnits/*", "dataElements/*", "conceptualDomains/*", "models/*", "relationshipTypes/*").load("valueDomains/*", "extensions/*")
        }
    }

}
