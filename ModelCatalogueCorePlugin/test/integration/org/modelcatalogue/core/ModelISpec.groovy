package org.modelcatalogue.core

import spock.lang.Shared

/**
 * Created by adammilward on 05/02/2014.
 */

class ModelISpec extends AbstractIntegrationSpec{

    @Shared
    def book

    def setupSpec(){
        loadFixtures()
        book = Model.findByName("book")
    }
/*
    def cleanupSpec(){
        book.delete()
    }
*/


}
