package org.modelcatalogue.core.util

import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.VariableExpression

import static org.codehaus.groovy.syntax.Types.*

import org.codehaus.groovy.control.CompilationFailedException
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.codehaus.groovy.control.customizers.SecureASTCustomizer


class SecuredRuleExecutor {

    static class ValidationResult {

        static final ValidationResult OK = new ValidationResult("")

        final String compilationFailedMessage

        ValidationResult(String compilationFailedMessage) {
            this.compilationFailedMessage = compilationFailedMessage
        }

        boolean asBoolean() {
            !compilationFailedMessage
        }
    }

    private final Binding binding
    private final GroovyShell shell

    SecuredRuleExecutor(Binding binding) {
        this.binding = binding
        this.shell   = createShell(binding)
    }

    private createShell(Binding binding) {
        CompilerConfiguration configuration = new CompilerConfiguration()

        ImportCustomizer importCustomizer = new ImportCustomizer().addStaticStars('java.lang.Math')

        SecureASTCustomizer secureASTCustomizer = new SecureASTCustomizer()
        secureASTCustomizer.with {
            packageAllowed = false

            importsWhitelist = []
            staticImportsWhitelist = []
            staticStarImportsWhitelist = ['java.lang.Math']
            indirectImportCheckEnabled = true

        }

        secureASTCustomizer.addExpressionCheckers(new SecureASTCustomizer.ExpressionChecker() {
            @Override boolean isAuthorized(Expression expression) {
                if (!(expression instanceof VariableExpression)) return true
                VariableExpression variableExpression = expression
                return variableExpression.name in binding.variables.keySet()
            }
        })

        configuration.addCompilationCustomizers(importCustomizer)
        configuration.addCompilationCustomizers(secureASTCustomizer)

        new GroovyShell(binding, configuration)
    }

    SecuredRuleExecutor(Map binding) {
        this(new Binding(binding))
    }

    ValidationResult validate(String scriptText) {
        try {
            shell.parse(scriptText)
            return ValidationResult.OK
        } catch (CompilationFailedException e) {
            return new ValidationResult(e.message)
        }
    }

    Object execute(String scriptText) {
        try {
            return shell.evaluate(scriptText)
        } catch (CompilationFailedException e) {
            throw new IllegalArgumentException("Invalid script:\n\n$scriptText\n", e)
        } catch (e) {
            return e
        }
    }
}
