package uk.co.mc.core.fixtures

import org.codehaus.groovy.control.CompilerConfiguration

/**
 * This mock fixture loader apart from the plugin one works in the unit test.
 * At the moment it doesn't handle wildcards in the fixture paths.
 */
class MockFixturesLoader {

    Map<String, Object> fixtures = [:]


    Map<String, Object> load(String... fixturesPaths) {
        CompilerConfiguration compilerConfiguration = new CompilerConfiguration()
        compilerConfiguration.scriptBaseClass = MockFixturesLoaderScript.name

        GroovyShell shell = new GroovyShell(getClass().getClassLoader(), new Binding(), compilerConfiguration)

        for (String fixture in fixturesPaths) {
            File fixtureFile = new File("../ModelCatalogueCorePlugin/fixtures/${fixture}.groovy")
            if (!fixtureFile.exists()) {
                throw new IllegalArgumentException("Fixture file $fixtureFile.canonicalPath does not exist!")
            }
            MockFixturesLoaderScript script = shell.parse(fixtureFile)
            script.run()
            fixtures.putAll(script.fixtures)
        }
        fixtures
    }

    def propertyMissing(String name) { fixtures[name] }

}
