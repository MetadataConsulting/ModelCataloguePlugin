package uk.co.mc.core.fixtures

/**
 * Created by ladin on 17.02.14.
 */
abstract class MockFixturesLoaderScript extends Script {

    Map<String, Object> fixtures = [:]
    MockFixturesLoader loader

    void fixture(Closure closure) {
        closure()
    }

    void load(String... args) {
        fixtures.putAll loader.load(args)
    }

    def methodMissing(String name, args) {
        assert args.size() in 1..2
        Map<String, Object> objArgs = args.size() == 2 ? args[0] : [:]
        Class cls = args.size() == 2 ? args[1] : args[0]
        fixtures[name] = cls.newInstance(objArgs)
    }

    def propertyMissing(String name) {
        fixtures[name]
    }

    def propertyMissing(String name, def arg) {
        fixtures[name] = arg
    }
}
