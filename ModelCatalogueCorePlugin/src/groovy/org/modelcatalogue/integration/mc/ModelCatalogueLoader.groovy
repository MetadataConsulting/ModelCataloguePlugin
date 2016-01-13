package org.modelcatalogue.integration.mc

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.SecureASTCustomizer
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.builder.util.CatalogueBuilderScript

class ModelCatalogueLoader {

    static class Builder {
        private final CatalogueBuilder catalogueBuilder
        private ClassLoader classLoader
        private List<Class> blackList = []
        private CompilerConfiguration compilerConfiguration

        protected Builder(CatalogueBuilder catalogueBuilder) {
            this.catalogueBuilder = catalogueBuilder
        }

        Builder blackList(Class... classes) {
            blackList.addAll classes
            this
        }

        Builder blackList(Iterable<Class> classes) {
            blackList.addAll(classes)
            this
        }

        Builder configuration(CompilerConfiguration compilerConfiguration) {
            this.compilerConfiguration = compilerConfiguration
            this
        }

        Builder classLoader(ClassLoader classLoader) {
            this.classLoader = classLoader
            this
        }


        ModelCatalogueLoader create() {
            CompilerConfiguration configuration
            if (compilerConfiguration) {
                configuration = compilerConfiguration
            } else if (blackList) {
                configuration = getDefaultCompilerConfiguration(blackList)
            } else {
                configuration = getDefaultCompilerConfiguration()
            }
            new ModelCatalogueLoader(catalogueBuilder, classLoader, configuration)
        }

    }

    static Builder build(CatalogueBuilder builder) {
        return new Builder(builder)
    }

    private final GroovyShell shell

    protected ModelCatalogueLoader(CatalogueBuilder catalogueBuilder, ClassLoader classLoader, CompilerConfiguration compilerConfiguration) {
        shell = prepareGroovyShell(catalogueBuilder, classLoader ?: getDefaultClassLoader(), compilerConfiguration)
    }

    void load(File file) {
        file.withInputStream {
            load(it)
        }
    }

    void load(URL url) {
        url.withInputStream {
            load(it)
        }
    }

    void load(InputStream inputStream) {
        shell.parse(inputStream.newReader()).run()
    }

    private static GroovyShell prepareGroovyShell(CatalogueBuilder builder, ClassLoader classLoader, CompilerConfiguration configuration) {
        new GroovyShell(classLoader, new Binding(builder: builder), configuration)
    }

    protected final static CompilerConfiguration getDefaultCompilerConfiguration(Iterable<Class> blackList) {
        CompilerConfiguration configuration = new CompilerConfiguration()
        configuration.scriptBaseClass = CatalogueBuilderScript.name

        SecureASTCustomizer secureASTCustomizer = new SecureASTCustomizer()
        secureASTCustomizer.with {
            packageAllowed = false
            indirectImportCheckEnabled = true

            importsWhitelist = [Object.name, CatalogueBuilder.name]
            starImportsWhitelist = [Object.name, CatalogueBuilder.name]
            staticImportsWhitelist = [Object.name, CatalogueBuilder.name]
            staticStarImportsWhitelist = [Object.name, CatalogueBuilder.name]

            receiversClassesBlackList = [System] + blackList
        }
        configuration.addCompilationCustomizers secureASTCustomizer
    }

    protected final static CompilerConfiguration getDefaultCompilerConfiguration() {
        getDefaultCompilerConfiguration([])
    }

    protected final ClassLoader getDefaultClassLoader() {
        return getClass().getClassLoader()
    }

}
