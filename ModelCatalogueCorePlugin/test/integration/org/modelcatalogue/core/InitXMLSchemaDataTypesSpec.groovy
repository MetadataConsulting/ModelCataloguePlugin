package org.modelcatalogue.core

import grails.test.spock.IntegrationSpec
import spock.lang.Shared
import spock.lang.Unroll
import spock.util.mop.Use

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@Unroll
class InitXMLSchemaDataTypesSpec extends IntegrationSpec {

    @Shared
    def initCatalogueService

    def setupSpec() {
        initCatalogueService.initDefaultRelationshipTypes()
        initCatalogueService.initDefaultDataTypes()
    }

    def "check XMLSchema classification present"() {
        expect:
        Classification.countByName('XMLSchema') == 1
    }

    @Use(FalseEx)
    def "check #domain validates value #value as #valid"(){
        when:
        ValueDomain valueDomain = ValueDomain.findByName(domain)
        DataType dataType       = DataType.findByName(domain)

        then:
        dataType
        valueDomain
        (!!valueDomain.validateRule(value)) == valid

        where:
        domain                      | value                                                                 | valid
        'xs:boolean'                | 'true'                                                                | true
        'xs:boolean'                | '1'                                                                   | true
        'xs:boolean'                | 'blah'                                                                | false
        'xs:base64Binary'           | ''                                                                    | true
        'xs:base64Binary'           | 'Ah0y'                                                                | true
        'xs:base64Binary'           | '#!#!'                                                                | false
        'xs:hexBinary'              | ''                                                                    | true
        'xs:hexBinary'              | 'cafe'                                                                | true
        'xs:hexBinary'              | 'babe'                                                                | true
        'xs:hexBinary'              | 'game'                                                                | false
        'xs:anyURI'                 | 'game'                                                                | true
        'xs:anyURI'                 | 'game#over'                                                           | true
        'xs:anyURI'                 | 'https://metadata.cfapps.io/#/catalogue/dataType/58/valueDomains'     | true
        'xs:anyURI'                 | 'https://metadata[cfa!ps]i0/#/catalogue/dataType/58/valueDomains'     | false
        'xs:string'                 | 'https://metadata[cfa!ps]i0/#/catalogue/dataType/58/valueDomains'     | true
        'xs:decimal'                | '+100'                                                                | true
        'xs:decimal'                | '-100'                                                                | true
        'xs:decimal'                | '10.0'                                                                | true
        'xs:decimal'                | 'blah'                                                                | false
        'xs:double'                 | 'blah'                                                                | false
        'xs:double'                 | '10'                                                                  | true
        'xs:double'                 | 'NaN'                                                                 | true
        'xs:double'                 | 'INF'                                                                 | true
        'xs:double'                 | '-INF'                                                                | true
        'xs:double'                 | '12.78e-2'                                                            | true
        'xs:double'                 | '-1E4'                                                                | true
        'xs:float'                  | 'blah'                                                                | false
        'xs:float'                  | '10'                                                                  | true
        'xs:float'                  | 'NaN'                                                                 | true
        'xs:float'                  | 'INF'                                                                 | true
        'xs:float'                  | '-INF'                                                                | true
        'xs:float'                  | '12.78e-2'                                                            | true
        'xs:float'                  | '-1E4'                                                                | true
        'xs:date'                   | '2001-09-12'                                                          | true
        'xs:date'                   | '2001-13-12'                                                          | false
        'xs:dateTime'               | '1999-05-31T13:20:00-05:00'                                           | true
        'xs:dateTime'               | '1999-15-31T13:20:00-56:00'                                           | false
        'xs:time'                   | '13:20:00-05:00'                                                      | true
        'xs:time'                   | '13:20:00-67:00'                                                      | false
        'xs:duration'               | 'P1Y2M3DT10H30M30S'                                                   | true
        'xs:duration'               | '-P1Y2M3DT10H30M30S'                                                  | true
        'xs:duration'               | 'P1Y2M3DT10H30M'                                                      | true
        'xs:duration'               | '-P1Y2M3DT10H30M'                                                     | true
        'xs:duration'               | 'P1Y2M3DT10H'                                                         | true
        'xs:duration'               | '-P1Y2M3DT10H'                                                        | true
        'xs:duration'               | 'P1Y2M3D'                                                             | true
        'xs:duration'               | '-P1Y2M3D'                                                            | true
        'xs:duration'               | 'P1Y2M'                                                               | true
        'xs:duration'               | '-P1Y2M'                                                              | true
        'xs:gDay'                   | '01'                                                                  | true
        'xs:gDay'                   | '50'                                                                  | false
        'xs:gMonth'                 | '01'                                                                  | true
        'xs:gMonth'                 | '20'                                                                  | false
        'xs:gMonthDay'              | '01-02'                                                               | true
        'xs:gMonthDay'              | '01-40'                                                               | false
        'xs:gMonthDay'              | '13-02'                                                               | false
        'xs:gYear'                  | '1234'                                                                | true
        'xs:gYear'                  | 'blah'                                                                | false
        'xs:gYearMonth'             | '1234-01'                                                             | true
        'xs:gYearMonth'             | '1234-15'                                                             | false
        'xs:language'               | 'cs'                                                                  | true
        'xs:language'               | 'abcd'                                                                | false

        // check for value domains present
        'xs:QName'                  | 'blah'                                                                | true
        'xs:NOTATION'               | 'blah'                                                                | true
        'xs:string'                 | 'blah'                                                                | true
        'xs:normalizedString'       | 'blah'                                                                | true

    }

}

class FalseEx {
    static asBoolean(Exception self) {
        return false
    }
}
