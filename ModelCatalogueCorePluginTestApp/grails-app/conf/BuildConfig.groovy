grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.fork = [
        // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
        //  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

        // configure settings for the test-app JVM, uses the daemon by default
        test   : false,
        // configure settings for the run-app JVM
        run    : false, //[maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
        // configure settings for the run-war JVM
        war    : [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve: false],
        // configure settings for the Console UI JVM
        console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
]


grails.plugin.location.'ModelCatalogueCorePlugin' = "../ModelCatalogueCorePlugin"
grails.plugin.location.'ModelCatalogueSpringSecurity2xPlugin' = "../ModelCatalogueSpringSecurity2xPlugin"
//grails.plugin.location.'ModelCatalogueElasticSearchPlugin' = "../ModelCatalogueElasticSearchPlugin"
//grails.plugin.location.'ModelCatalogueDataArchitectPlugin' = "../ModelCatalogueDataArchitectPlugin"

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false
    // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        mavenLocal()
        grailsCentral()
        mavenCentral()
        // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"

        mavenRepo "http://dl.bintray.com/metadata/model-catalogue"
    }

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes e.g.
        // runtime 'mysql:mysql-connector-java:5.1.27'
        // runtime 'org.postgresql:postgresql:9.3-1100-jdbc41'

        runtime "org.modelcatalogue:spring-security-ajax-aware:0.1.1"
        runtime 'mysql:mysql-connector-java:5.1.24'

        // Selenium WebDriver, for use in Geb
        def webDriverVersion = "2.41.0"

        // Testing modules
        test "org.gebish:geb-spock:0.9.3"
        test "org.seleniumhq.selenium:selenium-support:${webDriverVersion}"
        test "org.seleniumhq.selenium:selenium-firefox-driver:${webDriverVersion}"
        test "org.seleniumhq.selenium:selenium-chrome-driver:${webDriverVersion}"
        test "org.seleniumhq.selenium:selenium-remote-driver:${webDriverVersion}"

        // Required because of bug in 2.37.0 of WebDriver:
        test "org.apache.httpcomponents:httpclient:4.3.1"
        test("org.seleniumhq.selenium:selenium-htmlunit-driver:${webDriverVersion}") {
            exclude 'xml-apis'
        }

        test "org.grails:grails-datastore-test-support:1.0-grails-2.4"

    }

    plugins {


        build ":tomcat:7.0.55"

        // plugins for the compile step
        compile ":scaffolding:2.1.2"
        compile ':cache:1.1.7'
        compile ":asset-pipeline:1.9.6"

        // plugins needed at runtime but not for compilation
        runtime ":hibernate:3.6.10.17" //":hibernate4:4.3.5.5" // or
        runtime ":database-migration:1.4.0"
        runtime ":jquery:1.11.1"

        test ":geb:0.9.3"

        // Uncomment these (or add new ones) to enable additional resources capabilities
        //runtime ":zipped-resources:1.0.1"
        //runtime ":cached-resources:1.1"
        //runtime ":yui-minify-resources:0.1.5"

        // runtime ":ModelCatalogueCorePlugin:0.1"
    }
}

