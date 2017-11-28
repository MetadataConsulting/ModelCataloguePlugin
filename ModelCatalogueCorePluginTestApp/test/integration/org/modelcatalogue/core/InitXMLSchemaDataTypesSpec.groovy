package org.modelcatalogue.core

import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@Unroll
class InitXMLSchemaDataTypesSpec extends AbstractIntegrationSpec {


    def setupSpec() {
        initCatalogue()
    }

    def "check XMLSchema classification present"() {
        expect:
        DataModel.countByName('XMLSchema') == 1
    }

    def "check #domain validates value #value as #valid"() {
        when:
        DataType dataType       = DataType.findByName(domain)

        then:
        dataType

        when:
        def result = dataType.validateRule(value)

        then:
        if (valid) {
            assert result.is(true) : "Expected boolean 'true' but was ${result?.class?.name} '${result}'"
        } else {
            assert result instanceof Exception || result instanceof String || result != null
        }

        where:
        domain                      | value                                                                 | valid
        'xs:boolean'                | 'true'                                                                | true
        'xs:boolean'                | '1'                                                                   | true
        'xs:boolean'                | '0'                                                                   | true
        'xs:boolean'                | 'false'                                                               | true
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
        'xs:anyURI'                 | 'https://metadata.cfapps.io/#/catalogue/dataType/58/dataTypes'        | true
        'xs:anyURI'                 | 'https://metadata[cfa!ps]i0/#/catalogue/dataType/58/dataTypes'        | false
        'xs:string'                 | 'just a regular string'                                               | true
        'xs:decimal'                | '+100'                                                                | true
        'xs:decimal'                | '-100'                                                                | true
        'xs:decimal'                | '0'                                                                   | true
        'xs:decimal'                | '-0'                                                                  | true
        'xs:decimal'                | '10.0'                                                                | true
        'xs:decimal'                | 'blah'                                                                | false
        'xs:double'                 | 'blah'                                                                | false
        'xs:byte'                   | '+100'                                                                | true
        'xs:byte'                   | '-100'                                                                | true
        'xs:byte'                   | '0'                                                                   | true
        'xs:byte'                   | '-0'                                                                  | true
        'xs:byte'                   | '+128'                                                                | false
        'xs:byte'                   | '-129'                                                                | false
        'xs:byte'                   | 'blah'                                                                | false
        'xs:unsignedByte'           | '100'                                                                 | true
        'xs:unsignedByte'           | '+100'                                                                | false
        'xs:unsignedByte'           | '-100'                                                                | false
        'xs:unsignedByte'           | '0'                                                                   | true
        'xs:unsignedByte'           | '-0'                                                                  | false
        'xs:unsignedByte'           | '127'                                                                 | true
        'xs:unsignedByte'           | '128'                                                                 | true
        'xs:unsignedByte'           | '256'                                                                 | false
        'xs:unsignedByte'           | '-129'                                                                | false
        'xs:unsignedByte'           | 'blah'                                                                | false
        'xs:int'                    | '+128'                                                                | true
        'xs:int'                    | '-129'                                                                | true
        'xs:int'                    | '0'                                                                   | true
        'xs:int'                    | '-0'                                                                  | true
        'xs:int'                    | '+2147483648'                                                         | false
        'xs:int'                    | '-2147483649'                                                         | false
        'xs:int'                    | 'blah'                                                                | false
        'xs:unsignedInt'            | '128'                                                                 | true
        'xs:unsignedInt'            | '+128'                                                                | false
        'xs:unsignedInt'            | '-129'                                                                | false
        'xs:unsignedInt'            | '0'                                                                   | true
        'xs:unsignedInt'            | '-0'                                                                  | false
        'xs:unsignedInt'            | '2147483647'                                                          | true
        'xs:unsignedInt'            | '2147483648'                                                          | true
        'xs:unsignedInt'            | '4294967296'                                                          | false
        'xs:unsignedInt'            | '-2147483649'                                                         | false
        'xs:unsignedInt'            | 'blah'                                                                | false
        'xs:short'                  | '+32767'                                                              | true
        'xs:short'                  | '-32768'                                                              | true
        'xs:short'                  | '0'                                                                   | true
        'xs:short'                  | '-0'                                                                  | true
        'xs:short'                  | '+32768'                                                              | false
        'xs:short'                  | '-32769'                                                              | false
        'xs:short'                  | 'blah'                                                                | false
        'xs:unsignedShort'          | '32767'                                                               | true
        'xs:unsignedShort'          | '+32767'                                                              | false
        'xs:unsignedShort'          | '-32768'                                                              | false
        'xs:unsignedShort'          | '0'                                                                   | true
        'xs:unsignedShort'          | '-0'                                                                  | false
        'xs:unsignedShort'          | '32768'                                                               | true
        'xs:unsignedShort'          | '65535'                                                               | true
        'xs:unsignedShort'          | '65536'                                                               | false
        'xs:unsignedShort'          | '-32769'                                                              | false
        'xs:unsignedShort'          | 'blah'                                                                | false
        'xs:integer'                | '+128'                                                                | true
        'xs:integer'                | '-129'                                                                | true
        'xs:integer'                | '0'                                                                   | true
        'xs:integer'                | '-0'                                                                  | true
        'xs:integer'                | '+9223372036854775808'                                                | true
        'xs:integer'                | '-9223372036854775809'                                                | true
        'xs:nonPositiveInteger'     | '+128'                                                                | false
        'xs:nonPositiveInteger'     | '-129'                                                                | true
        'xs:nonPositiveInteger'     | '0'                                                                   | true
        'xs:nonPositiveInteger'     | '-0'                                                                  | true
        'xs:nonPositiveInteger'     | '+9223372036854775808'                                                | false
        'xs:nonPositiveInteger'     | '-9223372036854775809'                                                | true
        'xs:nonNegativeInteger'     | '+128'                                                                | true
        'xs:nonNegativeInteger'     | '-129'                                                                | false
        'xs:nonNegativeInteger'     | '0'                                                                   | true
        'xs:nonNegativeInteger'     | '-0'                                                                  | true
        'xs:nonNegativeInteger'     | '+9223372036854775808'                                                | true
        'xs:nonNegativeInteger'     | '-9223372036854775809'                                                | false
        'xs:negativeInteger'        | '+128'                                                                | false
        'xs:negativeInteger'        | '-129'                                                                | true
        'xs:negativeInteger'        | '0'                                                                   | false
        'xs:negativeInteger'        | '-0'                                                                  | false
        'xs:negativeInteger'        | '+9223372036854775808'                                                | false
        'xs:negativeInteger'        | '-9223372036854775809'                                                | true
        'xs:positiveInteger'        | '+128'                                                                | true
        'xs:positiveInteger'        | '-129'                                                                | false
        'xs:positiveInteger'        | '0'                                                                   | false
        'xs:positiveInteger'        | '-0'                                                                  | false
        'xs:positiveInteger'        | '+9223372036854775808'                                                | true
        'xs:positiveInteger'        | '-9223372036854775809'                                                | false
        'xs:long'                   | '+2147483648'                                                         | true
        'xs:long'                   | '-2147483649'                                                         | true
        'xs:long'                   | '0'                                                                   | true
        'xs:long'                   | '-0'                                                                  | true
        'xs:long'                   | '+9223372036854775808'                                                | false
        'xs:long'                   | '-9223372036854775809'                                                | false
        'xs:long'                   | 'blah'                                                                | false
        'xs:unsignedLong'           | '2147483648'                                                          | true
        'xs:unsignedLong'           | '+2147483648'                                                         | false
        'xs:unsignedLong'           | '-2147483649'                                                         | false
        'xs:unsignedLong'           | '0'                                                                   | true
        'xs:unsignedLong'           | '-0'                                                                  | false
        'xs:unsignedLong'           | '9223372036854775807'                                                 | true
        'xs:unsignedLong'           | '18446744073709551615'                                                | true
        'xs:unsignedLong'           | '18446744073709551616'                                                | false
        'xs:unsignedLong'           | '-9223372036854775809'                                                | false
        'xs:unsignedLong'           | 'blah'                                                                | false
        'xs:double'                 | 'blah'                                                                | false
        'xs:double'                 | '10'                                                                  | true
        'xs:double'                 | 'NaN'                                                                 | true
        'xs:double'                 | 'INF'                                                                 | true
        'xs:double'                 | '-INF'                                                                | true
        'xs:double'                 | '12.78e-2'                                                            | true
        'xs:double'                 | '-1E4'                                                                | true
        'xs:double'                 | '0'                                                                   | true
        'xs:float'                  | 'blah'                                                                | false
        'xs:float'                  | '10'                                                                  | true
        'xs:float'                  | '0'                                                                   | true
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
