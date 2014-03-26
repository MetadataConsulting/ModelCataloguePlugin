package org.modelcatalogue.core.util

import org.modelcatalogue.core.ValueDomain
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class SecuredRuleExecutorSpec extends Specification {

    ValueDomain x = new ValueDomain()
    SecuredRuleExecutor executor = [x: x]

    static List<String> VALID_EXPRESSIONS = [
        "x",
         "'text'",
         "true",
         "x.unitOfMeasure",
    ]

    static List<String> INVALID_EXPRESSIONS = [
        "System.exit(0)",
        "throw new RuntimeException()",
        "x.delete()",
        "package org.modelcatalogue.core.util",
        "import org.modelcatalogue.core.util.SecuredRuleExecutor",
        "y"
    ]

    def "Evaluation expression throws no error: #exp"() {


        when:
        executor.execute(exp)

        then:
        noExceptionThrown()

        where:
        exp << VALID_EXPRESSIONS
    }

    def "Validates valid expression: #exp"() {
        SecuredRuleExecutor executor = [x: new Object()]

        expect:
        executor.validate(exp)

        where:
        exp << VALID_EXPRESSIONS
    }

    def "Validates invalid expression: #exp"() {
        SecuredRuleExecutor executor = [x: new Object()]

        expect:
        !executor.validate(exp)

        where:
        exp << INVALID_EXPRESSIONS
    }


}
