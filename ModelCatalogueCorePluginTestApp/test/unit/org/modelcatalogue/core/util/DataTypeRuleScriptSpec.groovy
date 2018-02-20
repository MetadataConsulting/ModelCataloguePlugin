package org.modelcatalogue.core.util

import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.scripting.DataTypeRuleScript
import spock.lang.Specification
import spock.lang.Unroll
import spock.util.mop.ConfineMetaClassChanges

import java.text.DateFormat
import java.text.SimpleDateFormat

class DataTypeRuleScriptSpec extends Specification {

    @Unroll @ConfineMetaClassChanges(DataType)
    def "for x='#value' the expression '#expression' is #expected"() {
        when:
        DataType base = new DataType(name: "Test Base Domain", rule: "x != 'abrakadabra'")
        DataType dataType = new DataType(name: "Test Domain")

        dataType.metaClass.getBasedOn = {->
            [base]
        }

        def binding = new Binding(x: value, dataType: dataType)
        def executor = new SecuredRuleExecutor<DataTypeRuleScript>(DataTypeRuleScript, binding)
        executor.execute(expression)
        then:
        noExceptionThrown()


        when:
        def reusable = executor.reuse(expression)
        def result = reusable.execute(x: value)

        then:
        if (expected) {
            !reusable.script.exception
        }
        result == expected

        where:
        expected                | value                                     | expression
        true                    | "1000"                                    | "is Integer"
        true                    | "1000"                                    | "is number"
        true                    | "1000"                                    | "is decimal"
        true                    | "10.5"                                    | "is decimal"
        true                    | "10.5"                                    | "is number"
        false                   | "blah"                                    | "is number"
        true                    | "https://www.google.co.uk"                | "is URL"
        false                   | "1235"                                    | "is URL"
        true                    | "https://www.google.co.uk"                | "is URI"
        true                    | "1235"                                    | "is URI"
        false                   | "http://datypic.com#frag1#frag2"          | "is URI"
        false                   | "http://datypic.com#f% rag"               | "is URI"
        "1001"                  | "x"                                       | "string(1001)"
        true                    | "abcdef"                                  | "minLength(5)"
        true                    | "abcdef"                                  | "minLength(6)"
        false                   | "abcdef"                                  | "minLength(7)"
        false                   | "abcdef"                                  | "maxLength(5)"
        true                    | "abcdef"                                  | "maxLength(6)"
        true                    | "abcdef"                                  | "maxLength(7)"
        false                   | "abcdef"                                  | "length(5)"
        true                    | "abcdef"                                  | "length(6)"
        false                   | "abcdef"                                  | "length(7)"
        true                    | "6"                                       | "minInclusive(5)"
        true                    | "6"                                       | "minInclusive(6)"
        false                   | "6"                                       | "minInclusive(7)"
        true                    | "6"                                       | "minExclusive(5)"
        false                   | "6"                                       | "minExclusive(6)"
        false                   | "6"                                       | "minExclusive(7)"
        false                   | "6"                                       | "maxInclusive(5)"
        true                    | "6"                                       | "maxInclusive(6)"
        true                    | "6"                                       | "maxInclusive(7)"
        false                   | "6"                                       | "maxExclusive(5)"
        false                   | "6"                                       | "maxExclusive(6)"
        true                    | "6"                                       | "maxExclusive(7)"
        true                    | "1234"                                    | "fixed(1234)"
        true                    | "1234"                                    | "fixed('1234')"
        false                   | "0000"                                    | "fixed('1234')"
        d('2012-10-31 00:00')   | "2012-10-31"                              | "date('yyyy-MM-dd')"


    }

    private static DateFormat dateFormat = new SimpleDateFormat('yyyy-MM-dd HH:mm')
    private static d(String dateString) {
        dateFormat.parse(dateString)
    }

}
