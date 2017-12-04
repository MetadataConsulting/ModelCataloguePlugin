package org.modelcatalogue

import spock.lang.Specification

/**
 * Created by james on 04/12/2017.
 */
class ClientSpec extends Specification{
    def "test toString"() {
        expect:
        Client.NORTH_THAMES.toString() == "NORTH_THAMES"
    }
}
