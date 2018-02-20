package org.modelcatalogue.core.util

import org.modelcatalogue.core.DataType
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class SecuredRuleExecutorSpec extends Specification {

    DataType x = new DataType()
    SecuredRuleExecutor executor = [x: x]

    static List<String> VALID_EXPRESSIONS = [
        "x",
         "'text'",
         "true",
         "x.unitOfMeasure",
         "y = x",
         "int y = x"
    ]

    static List<String> INVALID_EXPRESSIONS = [
        "System.exit(0)",
        "throw new RuntimeException()",
        "x.delete()",
        "package org.modelcatalogue.core.util",
        "import org.modelcatalogue.core.scripting.SecuredRuleExecutor",
        "y",
        "x.unitOfMeasure = null"

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

    def "cleaned up message"() {
        String full = '''startup failed:
General error during canonicalization: Expression [VariableExpression] is not allowed: y

java.lang.SecurityException: Expression [VariableExpression] is not allowed: y
\tat org.codehaus.groovy.control.customizers.SecureASTCustomizer$SecuringCodeVisitor.assertExpressionAuthorized(SecureASTCustomizer.java:690)'''

        String brief = SecuredRuleExecutor.cleanUpMessage(full)

        expect:
        brief == 'variable is not allowed: y'



    }


}
