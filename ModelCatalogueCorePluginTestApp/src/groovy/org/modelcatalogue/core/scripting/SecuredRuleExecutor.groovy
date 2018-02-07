package org.modelcatalogue.core.scripting

import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.stmt.ThrowStatement
import org.codehaus.groovy.control.CompilationFailedException
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.codehaus.groovy.control.customizers.SecureASTCustomizer
import org.codehaus.groovy.syntax.Types
import org.grails.datastore.gorm.GormStaticApi

import javax.xml.bind.DatatypeConverter

class SecuredRuleExecutor<S extends Script> {

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

    static class ReusableScript<RS extends Script> {
        final RS script

        ReusableScript(RS script) {
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
    private final Class<S> baseScriptClass

    SecuredRuleExecutor(Binding binding) {
        this.binding            = binding
        this.baseScriptClass    = Script.class
        this.shell              = createShell(binding)
    }

    SecuredRuleExecutor(Map binding, Class<S> baseScriptClass) {
        this.binding            = new Binding(binding)
        this.baseScriptClass    = baseScriptClass
        this.shell              = createShell(this.binding)
    }

    SecuredRuleExecutor(Class<S> baseScriptClass, Binding binding) {
        this.binding            = binding
        this.baseScriptClass    = baseScriptClass
        this.shell              = createShell(binding)
    }

    private createShell(Binding binding) {
        CompilerConfiguration configuration = new CompilerConfiguration()

        ImportCustomizer importCustomizer = new ImportCustomizer().addStaticStars(Math.name)

        SecureASTCustomizer secureASTCustomizer = new SecureASTCustomizer()
        secureASTCustomizer.with {
            packageAllowed = false
            importsWhitelist = withBaseScript()
            starImportsWhitelist = withBaseScript()
            staticImportsWhitelist = withBaseScript()
            staticStarImportsWhitelist = withBaseScript(Math, DatatypeConverter)

            receiversClassesBlackList = [System, GormStaticApi]
        }

        secureASTCustomizer.addStatementCheckers(new SecureASTCustomizer.StatementChecker() {
            @Override
            boolean isAuthorized(Statement expression) {
                return !(expression instanceof ThrowStatement)
            }
        })

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
                    if (expression.name == 'this') return true
                    if (baseScriptClass && expression.name in baseScriptClass.metaClass.properties*.name) return true
                    return expression.name in names
                }

                if (expression instanceof MethodCallExpression) {
                    return !(expression.methodAsString in ['delete'])
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

    List<String> withBaseScript(Class... classes) {
        List<Class> ret = []
        ret.addAll(classes)
        if (baseScriptClass) {
            ret << baseScriptClass
        }
        ret*.name
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

    ReusableScript<S> reuse(String scriptText) {
        try {
            return new ReusableScript<S>((S) shell.parse(scriptText))
        } catch (CompilationFailedException e) {
            throw new IllegalArgumentException("Invalid script:\n\n$scriptText\n", e)
        } catch (e) {
            throw e
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
