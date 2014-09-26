package org.modelcatalogue.core.util

import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.control.CompilationFailedException
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.codehaus.groovy.control.customizers.SecureASTCustomizer
import org.codehaus.groovy.syntax.Types

class SecuredRuleExecutor {

    static class ValidationResult {

        static final ValidationResult OK = new ValidationResult("")

        final String compilationFailedMessage

        ValidationResult(String compilationFailedMessage) {
            this.compilationFailedMessage = cleanUpMessage compilationFailedMessage
        }

        boolean asBoolean() {
            !compilationFailedMessage
        }

        String toString() {
            compilationFailedMessage ? "FAILED with $compilationFailedMessage" : "PASSED"
        }
    }

    static class ReusableScript {
        final Script script

        ReusableScript(Script script) {
            this.script = script
        }

        Object execute(Map<String, Object> binding) {
            script.binding = new Binding(binding)
            try {
                return script.run()
            } catch (e) {
                return e
            }
        }
    }

    private final Binding binding
    private final GroovyShell shell
    private final Class<? extends Script> baseScriptClass

    SecuredRuleExecutor(Binding binding) {
        this(Script.class, binding)
    }

    SecuredRuleExecutor(Class<? extends Script> baseScriptClass, Binding binding) {
        this.binding            = binding
        this.shell              = createShell(binding)
        this.baseScriptClass    = baseScriptClass
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

            Set<String> names = new HashSet(binding.variables.keySet())

            @Override boolean isAuthorized(Expression expression) {
                if (expression instanceof BinaryExpression && expression.operation.meaning == Types.ASSIGN) {
                    if (expression.leftExpression instanceof VariableExpression) {
                        names << expression.leftExpression.name
                        return true
                    } else {
                        return false
                    }
                }
                if (expression instanceof VariableExpression) {
                    return expression.name in names
                }
                true
            }
        })

        configuration.addCompilationCustomizers(importCustomizer)
        configuration.addCompilationCustomizers(secureASTCustomizer)

        if (baseScriptClass && baseScriptClass != Script.class) {
            configuration.scriptBaseClass = baseScriptClass.name
        }

        new GroovyShell(getClass().getClassLoader(), binding, configuration)
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

    ReusableScript reuse(String scriptText) {
        try {
            return new ReusableScript(shell.parse(scriptText))
        } catch (CompilationFailedException ignored) {
            return null
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

    static String cleanUpMessage(String full) {
        String ret = full.replace('startup failed:\nGeneral error during canonicalization: ', '')
        ret = ret.replace('Expression [VariableExpression]', 'variable')
        int indexOfSecurityException = ret.indexOf('java.lang.SecurityException:')
        if (indexOfSecurityException > -1) {
            ret = ret.substring(0, indexOfSecurityException)
        }
        ret.trim()
    }
}
